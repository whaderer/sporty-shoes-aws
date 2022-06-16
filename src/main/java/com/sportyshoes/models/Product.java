package com.sportyshoes.models;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC, force = true)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    private BigDecimal price;

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date dateAdded;

    private Long categoryId;

    public Product(String name, BigDecimal price, Date dateAdded, Long categoryId) {
        this.name = name;
        this.price = price;
        this.dateAdded = dateAdded;
        this.categoryId = categoryId;
    }

    @PrePersist
    private void onCreate() {
        dateAdded = new Date();
    }

}
