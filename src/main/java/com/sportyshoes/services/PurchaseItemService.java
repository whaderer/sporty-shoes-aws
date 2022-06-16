package com.sportyshoes.services;

import com.sportyshoes.exceptions.ProductNotFoundException;
import com.sportyshoes.models.PurchaseItem;
import com.sportyshoes.repositories.PurchaseItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PurchaseItemService {

    private final PurchaseItemRepository purchaseItemRepository;

    @Autowired
    public PurchaseItemService(PurchaseItemRepository purchaseItemRepository) {
        this.purchaseItemRepository = purchaseItemRepository;
    }

    @Transactional
    public PurchaseItem getItemById(long id) {
        Optional<PurchaseItem> optionalPurchaseItem = purchaseItemRepository.findById(id);
        return optionalPurchaseItem.orElseThrow(() -> new ProductNotFoundException());
    }

    @Transactional
    public List<PurchaseItem> getAllPurchaseItemsByPurchaseId(long purchaseId) {
        return purchaseItemRepository.findAllByPurchaseId(purchaseId);
    }

    @Transactional
    public void updatePurchaseItem(PurchaseItem item) {
        purchaseItemRepository.updatePurchaseItem(item);
    }

    @Transactional
    public void deletePurchaseItem(long id) {
        purchaseItemRepository.deleteById(id);
    }

    @Transactional
    public void deleteAllPurchaseItemsForPurchaseId(long purchaseId) {
        purchaseItemRepository.deleteAllItemsByPurchaseId(purchaseId);
    }

    @Transactional
    public List<PurchaseItem> getAllPurchaseItems() {
        return (List<PurchaseItem>) purchaseItemRepository.findAll();
    }

    @Transactional
    public PurchaseItem addPurchaseItem(PurchaseItem purchaseItem) {
        return purchaseItemRepository.save(purchaseItem);
    }

}
