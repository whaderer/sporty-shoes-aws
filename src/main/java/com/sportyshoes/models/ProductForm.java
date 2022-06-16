package com.sportyshoes.models;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class ProductForm {

    private String name;
    private BigDecimal price;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date dateAdded;
    private Long categoryId;

    public Product toProduct() {

        return new Product(name, price, dateAdded, categoryId);
    }
}
