package com.sportyshoes.repositories;

import com.sportyshoes.models.PurchaseItem;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PurchaseItemRepository extends CrudRepository<PurchaseItem, Long> {

    @Modifying(clearAutomatically = true)
    @Query("update Purchase p set p.userId = ?1, p.date = ?2, p.total = ?3" +
            "where p.id = ?4")
    void updatePurchaseItem(PurchaseItem item);

    @Modifying(clearAutomatically = true)
    @Query(value = "Delete from purchase_item p WHERE p.purchase_id = ?1", nativeQuery = true)
    void deleteAllItemsByPurchaseId(long purchaseId);

    @Modifying(clearAutomatically = true)
    @Query(value = "SELECT * from purchase_item p WHERE p.purchase_id = ?1", nativeQuery = true)
    List<PurchaseItem> findAllByPurchaseId(long purchaseId);
}
