package com.sportyshoes.services;

import com.sportyshoes.exceptions.ProductNotFoundException;
import com.sportyshoes.models.Purchase;
import com.sportyshoes.repositories.PurchaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;

    @Autowired
    public PurchaseService(PurchaseRepository purchaseRepository) {
        this.purchaseRepository = purchaseRepository;
    }

    @Transactional
    public Purchase getPurchaseById(long id) {
        Optional<Purchase> optionalPurchase = purchaseRepository.findById(id);
        return optionalPurchase.orElseThrow(() -> new ProductNotFoundException());
    }

    @Transactional
    public List<Purchase> getAllPurchases() {
        return (List<Purchase>) purchaseRepository.findAll();
    }

    @Transactional
    public List<Purchase> getAllPurchasesByUserId(long userId) {
        return purchaseRepository.getAllItemsByUserId(userId);
    }

    @Transactional
    public long  updatePurchase(Purchase purchase) {
        return purchaseRepository.updatePurchase(purchase);
    }

    @Transactional
    public void deletePurchaseById(long id) {
        purchaseRepository.deleteById(id);
    }

    @Transactional
    public Purchase addPurchase(Purchase purchase) {
        return purchaseRepository.save(purchase);
    }
}
