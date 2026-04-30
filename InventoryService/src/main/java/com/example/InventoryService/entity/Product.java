package com.example.InventoryService.entity;

import com.example.InventoryService.enums.ProductCategory;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "E_COM_PRODUCT")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Product {

    @Id
    @SequenceGenerator(name = "prodSeq", sequenceName = "E_COM_PRODUCT_SEQ")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "prodSeq")
    private Integer productId;
    private String productName;
    @Enumerated(EnumType.STRING)
    private ProductCategory productCategory;
    private double price;
    private double rating;
    private int stock;

}
