package com.sportyshoes.repositories;

import com.sportyshoes.models.Product;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ProductRepository extends CrudRepository<Product, Long> {

    @Modifying(clearAutomatically = true)
    @Query("update Product p set p.name = ?1, p.price = ?2, p.dateAdded = ?3, p.categoryId = ?4 " +
            "where p.id = ?5")
    int updateProduct(Product p);

    @Modifying(clearAutomatically = true)
    @Query(value = "SELECT * from product p WHERE category_id = ?1", nativeQuery = true)
    List<Product> findByCategoryId(Long categoryId);

}
