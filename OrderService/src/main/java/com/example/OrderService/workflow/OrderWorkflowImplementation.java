package com.example.OrderService.workflow;

import com.example.OrderService.activities.OrderActivities;
import com.example.OrderService.client.inventoryServiceClient.dto.ProductDto;
import com.example.OrderService.dto.OrderRequestDto;
import com.example.OrderService.entity.Order;
import com.example.OrderService.entity.enums.OrderStatus;
import com.example.OrderService.exception.InsufficientStockException;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.failure.ActivityFailure;
import io.temporal.spring.boot.WorkflowImpl;
import io.temporal.workflow.Saga;
import io.temporal.workflow.Workflow;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.List;

@Slf4j
@WorkflowImpl(taskQueues = "ORDER_TASK_QUEUE")
public class OrderWorkflowImplementation implements OrderWorkflow {

    private boolean isCanceled = false;

    private final OrderActivities orderActivities = Workflow.newActivityStub(
            OrderActivities.class,
            ActivityOptions.newBuilder()
                    .setStartToCloseTimeout(Duration.ofSeconds(10))
                    .setRetryOptions(
                            RetryOptions.newBuilder()
                            .setMaximumAttempts(5)
                                    .setDoNotRetry(
                                            InsufficientStockException.class.getName()
                                    )
                            .build())
                    .build()
    );

    @Override
    public void placeOrder(List<OrderRequestDto> orderRequestDtoList, String orderId) {

        Saga saga = new Saga(new Saga
                .Options
                .Builder()
                .setParallelCompensation(false)
                .setContinueWithError(true)
                .build());

        Order order = Order.builder()
                .orderId(orderId)
                .productIds(
                        orderRequestDtoList
                                .stream()
                                .map(OrderRequestDto::getProductId)
                                .toList())
                .orderStatus(OrderStatus.INITIATED)
                .build();

        try {

            // Retrieving products from inventory service
            List<ProductDto> productList = orderActivities.retrieveProductsFromInventory(orderRequestDtoList);

            // Verifying if product's existence and stock availability
            orderActivities.verifyStockAvailability(productList, orderRequestDtoList);

            // Calculating total price
            double totalPrice = orderActivities.calculateTotalPrice(productList, orderRequestDtoList);
            order.setTotalPrice(totalPrice);

            // Updating inventory
            orderActivities.updateInventory(orderRequestDtoList);
            saga.addCompensation(orderActivities::restoreInventory, orderRequestDtoList);

            // Saving it in the database
            order.setOrderStatus(OrderStatus.PLACED);
            orderActivities.saveOrder(order);
            saga.addCompensation(() -> {
                order.setOrderStatus(OrderStatus.FAILED);
                orderActivities.saveOrder(order);
            });

            // Waiting for the order to be delivered to canceled
            boolean isOrderCanceled = Workflow.await(Duration.ofSeconds(20), () -> isCanceled);

            // Delivered or canceled?
            if (isOrderCanceled) {
                orderActivities.restoreInventory(orderRequestDtoList);
                order.setOrderStatus(OrderStatus.CANCELED);
            } else {
                order.setOrderStatus(OrderStatus.DELIVERED);
            }
            orderActivities.saveOrder(order);

        } catch (ActivityFailure ex) {
            saga.compensate();
            order.setOrderStatus(OrderStatus.FAILED);
            orderActivities.saveOrder(order);
            log.info("OrderId: {}, has fail to complete, reason: {}", orderId, ex.getMessage());
            throw ex;
        }

    }

    @Override
    public void cancelOrder() {
        this.isCanceled = true;
    }
}
