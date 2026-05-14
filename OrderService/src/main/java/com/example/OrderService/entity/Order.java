package com.example.OrderService.entity;

import com.example.OrderService.entity.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "E_COM_ORDER")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order {

    @Id
    @SequenceGenerator(sequenceName = "E_COM_ORDER_SEQ", name = "orderSeq")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "orderSeq")
    private Integer orderId;
    @ElementCollection
    private List<Integer> productIds;
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;
    private double totalPrice;

}
