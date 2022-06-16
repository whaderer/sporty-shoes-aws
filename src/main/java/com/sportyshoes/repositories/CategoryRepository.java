package com.sportyshoes.repositories;

import com.sportyshoes.models.Category;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface CategoryRepository extends CrudRepository<Category, Long> {

    @Modifying(clearAutomatically = true)
    @Query("update Category c set c.name = ?1" +
            "where c.id = ?1")
    int updateCategory(Category c);
}
