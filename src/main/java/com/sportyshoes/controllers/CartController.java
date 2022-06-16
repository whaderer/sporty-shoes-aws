package com.sportyshoes.controllers;

import com.sportyshoes.models.CartItem;
import com.sportyshoes.models.Product;
import com.sportyshoes.models.Purchase;
import com.sportyshoes.models.PurchaseItem;
import com.sportyshoes.security.SecurityUser;
import com.sportyshoes.security.UserRepositoryUserDetailsService;
import com.sportyshoes.services.ProductService;
import com.sportyshoes.services.PurchaseItemService;
import com.sportyshoes.services.PurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Controller
public class CartController {

    private final ProductService productService;
    private final PurchaseService purchaseService;
    private final PurchaseItemService purchaseItemService;
    private final UserRepositoryUserDetailsService userRepositoryUserDetailsService;

    @Autowired
    public CartController(
            ProductService productService,
            PurchaseService purchaseService,
            PurchaseItemService purchaseItemService,
            UserRepositoryUserDetailsService userRepositoryUserDetailsService
    ) {
        this.productService = productService;
        this.purchaseService = purchaseService;
        this.purchaseItemService = purchaseItemService;
        this.userRepositoryUserDetailsService = userRepositoryUserDetailsService;
    }

    @GetMapping(path = "/cart")
    public String populateCart(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession();
        // check if user is logged in
        if (userRepositoryUserDetailsService.isUserAuthenticated()) {
            // if cart is already in session then retrieve it else create a new cart purchaseList and save it to session
            List<CartItem> cartItems = populateCartItemList(session);
            BigDecimal totalValue = getCartValue(cartItems);
            model.addAttribute("cartValue", totalValue);
            model.addAttribute("cartItems", cartItems);
        } else {
            model.addAttribute("error", "Error, You need to login before adding items to cart");
        }
        return "cart";
    }

    @GetMapping(path = "/cartadditem")
    public String addItemToCart(
            Model model,
            HttpServletRequest request,
            @RequestParam(value = "id", required = true) String productId
    ) {
        // check if user is logged in
        HttpSession session = request.getSession();
        if (userRepositoryUserDetailsService.isUserAuthenticated()) {
            long idValue = Long.parseLong(productId);
            // if cart is already in session then retrieve it else create a new cart purchaseList and save it to session
            List<CartItem> cartItems = populateCartItemList(session);
            if (isItemInCartIncreaseQuantity(cartItems, idValue)) {
                model.addAttribute("error", "This item is already in your cart");
            } else {
                Product product = productService.getProductById(idValue);
                CartItem item = new CartItem();
                item.setProductId(idValue);
                item.setQuantity(1);
                item.setRate(product.getPrice());
                BigDecimal dprice = item.getRate().multiply(new BigDecimal(item.getQuantity()));
                item.setPrice(dprice);
                item.setName(product.getName());
                cartItems.add(item);
                session.setAttribute("cart_items", cartItems);
            }
        } else {
            model.addAttribute("error", "Error, You need to login before adding items to cart");
        }
        return "redirect:cart";
    }

    @GetMapping(path = "/cartdeleteitem")
    public String cartDeleteItem(
            Model model,
            HttpServletRequest request,
            @RequestParam(value = "id", required = true) String id
    ) {
        HttpSession session = request.getSession();
        if (userRepositoryUserDetailsService.isUserAuthenticated()) {
            long idValue = Long.parseLong(id);
            List<CartItem> cartItems = populateCartItemList(session);
            for (CartItem item : cartItems) {
                if (item.getProductId() == idValue) {
                    cartItems.remove(item);
                    session.setAttribute("cart_items", cartItems);
                    break;
                }
            }
        } else {
            model.addAttribute("error", "Error, You need to login before deleting items from cart");
        }
        return "redirect:cart";
    }

    @GetMapping(path = "/checkout")
    public String checkout(
            Model model,
            HttpServletRequest request
    ) {
        HttpSession session = request.getSession();
        if (userRepositoryUserDetailsService.isUserAuthenticated()) {
            List<CartItem> cartItems = populateCartItemList(session);
            BigDecimal totalValue = getCartValue(cartItems);
            model.addAttribute("cartValue", totalValue);
            model.addAttribute("cartItems", cartItems);
        } else {
            model.addAttribute("error", "Error, You need to login before checking out");
        }
        return "checkout";
    }

    @GetMapping(path = "/gateway")
    public String gateway(
            Model model,
            HttpServletRequest request
    ) {
        HttpSession session = request.getSession();
        if (userRepositoryUserDetailsService.isUserAuthenticated()) {
            List<CartItem> cartItems = populateCartItemList(session);
            BigDecimal totalValue = getCartValue(cartItems);
            model.addAttribute("cartValue", totalValue);
            model.addAttribute("cartItems", cartItems);
        } else {
            model.addAttribute("error", "Error, You need to login before making payment");
        }
        return "gateway";
    }

    @GetMapping(path = "/completepurchase")
    public String completePurchase(
            Model model,
            HttpServletRequest request,
            Authentication authentication
    ) {
        HttpSession session = request.getSession();
        if (userRepositoryUserDetailsService.isUserAuthenticated()) {
            // take items from cart and update the database
            List<CartItem> cartItems = populateCartItemList(session);
            BigDecimal totalValue = getCartValue(cartItems);
            SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
            Long userId = securityUser.getUser().getId();
            Purchase purchase = new Purchase();
            purchase.setUserId(userId);
            purchase.setDate(Calendar.getInstance().getTime());
            purchase.setTotal(totalValue);
            long purchaseId = purchaseService.addPurchase(purchase).getId();

            for (CartItem item : cartItems) {
                PurchaseItem pItem = new PurchaseItem();
                pItem.setPurchaseId(purchaseId);
                pItem.setProduct(productService.getProductById(item.getProductId()));
                //  pItem.setProduct(productService.getAllProducts());
                pItem.setUserId(userId);
                pItem.setRate(item.getRate());
                pItem.setQuantity(item.getQuantity());
                pItem.setPrice(item.getPrice());
                purchaseItemService.addPurchaseItem(pItem);
            }
            model.addAttribute("cartValue", totalValue);
            model.addAttribute("cartItems", cartItems);
        } else {
            model.addAttribute("error", "Error, You need to login before completing purchase");
        }
        return "redirect:confirm";
    }

    @GetMapping(path = "/confirm")
    public String completePurchase(
            Model model,
            HttpServletRequest request
    ) {
        HttpSession session = request.getSession();
        if (userRepositoryUserDetailsService.isUserAuthenticated()) {
            List<CartItem> cartItems = populateCartItemList(session);
            BigDecimal totalValue = getCartValue(cartItems);
            model.addAttribute("cartValue", totalValue);
            cartItems.clear();
            session.setAttribute("cart_items", null);
        } else {
            model.addAttribute("error", "Error, You need to login before completing the purchase");
        }
        return "order-confirm";
    }

    private List<CartItem> populateCartItemList(HttpSession session) {
        List<CartItem> cartItems = new ArrayList<CartItem>();
        if (session.getAttribute("cart_items") != null)
            cartItems = (List<CartItem>) session.getAttribute("cart_items");
        return cartItems;
    }

    private boolean isItemInCartIncreaseQuantity(List<CartItem> list, long item) {
        boolean retVal = false;

        for (CartItem thisItem : list) {
            if (item == thisItem.getProductId()) {
                thisItem.increaseQuantity();
                BigDecimal dprice = thisItem.getRate().multiply(new BigDecimal(thisItem.getQuantity()));
                thisItem.setPrice(dprice);
                retVal = true;
                break;
            }
        }
        return retVal;
    }

    private BigDecimal getCartValue(List<CartItem> list) {
        BigDecimal total = new BigDecimal(0.0);

        for (CartItem item : list) {
            BigDecimal dprice = item.getRate().multiply(new BigDecimal(item.getQuantity()));
            total = total.add(dprice);
        }
        return total;
    }
}

