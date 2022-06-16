package com.sportyshoes.controllers;

import com.sportyshoes.models.Purchase;
import com.sportyshoes.models.PurchaseItem;
import com.sportyshoes.security.SecurityUser;
import com.sportyshoes.security.UserRepositoryUserDetailsService;
import com.sportyshoes.services.PurchaseItemService;
import com.sportyshoes.services.PurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Controller
public class PurchaseController {

    private final PurchaseService purchaseService;
    private final PurchaseItemService purchaseItemService;
    private final UserRepositoryUserDetailsService userRepositoryUserDetailsService;

    @Autowired
    public PurchaseController(PurchaseService purchaseService, PurchaseItemService purchaseItemService, UserRepositoryUserDetailsService userRepositoryUserDetailsService) {
        this.purchaseService = purchaseService;
        this.purchaseItemService = purchaseItemService;
        this.userRepositoryUserDetailsService = userRepositoryUserDetailsService;
    }

    @GetMapping(path = "/memberpurchases")
    public String memberpurchases(
            Model model,
            Authentication authentication
    ) {
        if (userRepositoryUserDetailsService.isUserAuthenticated()) {
            BigDecimal total = new BigDecimal("0.0");
            SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
            Long userId = securityUser.getUser().getId();
            List<Purchase> purchaseList = purchaseService.getAllPurchasesByUserId(userId);
            List<PurchaseItem> purchaseItemList = new ArrayList<PurchaseItem>();
            for (Purchase purchase : purchaseList) {
                total = total.add(purchase.getTotal());
                purchaseItemList.addAll(purchaseItemService.getAllPurchaseItemsByPurchaseId(purchase.getId()));
                model.addAttribute("purchaseItemList", purchaseItemList);
            }
            model.addAttribute("totalAmount", total);
            model.addAttribute("purchaseList", purchaseList);
            model.addAttribute("pageTitle", "SPORTY SHOES - YOUR ORDERS");
            return "purchases";
        }
        return "user-login";
    }
}

