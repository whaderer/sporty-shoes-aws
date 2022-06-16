package com.sportyshoes.models;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC, force = true)
public class CartItem {

    private long productId;
    private String name;
    private BigDecimal rate;
    private BigDecimal price;
    private int quantity;

    public void increaseQuantity() {
        this.quantity++;
    }

}
