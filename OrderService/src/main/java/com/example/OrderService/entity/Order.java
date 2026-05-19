package com.example.OrderService.entity;

import com.example.OrderService.entity.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "E_COM_ORDER")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Order {

    @Id
    private String orderId;
    @ElementCollection
    private List<Integer> productIds;
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;
    private double totalPrice;

}
