package com.sportyshoes.repositories;

import com.sportyshoes.models.Purchase;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PurchaseRepository extends CrudRepository<Purchase, Long> {

    @Modifying(clearAutomatically = true)
    @Query(value = "SELECT * from Purchase p WHERE user_id = ?1", nativeQuery = true)
    List<Purchase> getAllItemsByUserId(long userId);

    @Modifying(clearAutomatically = true)
    @Query("update Purchase p set p.userId = ?1, p.date = ?2, p.total = ?3" +
            "where p.id = ?4")
    long updatePurchase(Purchase p);
}

