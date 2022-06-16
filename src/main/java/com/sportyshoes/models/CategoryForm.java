package com.sportyshoes.models;

import lombok.Data;

@Data
public class CategoryForm {

    private String name;

    public Category toCategory() {

        return new Category(name);
    }
}
