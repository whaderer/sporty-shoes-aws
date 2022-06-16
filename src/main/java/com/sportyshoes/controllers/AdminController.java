package com.sportyshoes.controllers;

import com.sportyshoes.models.*;
import com.sportyshoes.security.SecurityUser;
import com.sportyshoes.security.UserRegistrationForm;
import com.sportyshoes.security.UserRepositoryUserDetailsService;
import com.sportyshoes.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;

@Controller
public class AdminController {

    private final UserRepositoryUserDetailsService userRepositoryUserDetailsService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final ProductService productService;
    private final CategoryService categoryService;
    private final PurchaseService purchaseService;
    private final PurchaseItemService purchaseItemService;

    @Autowired
    public AdminController(UserRepositoryUserDetailsService userRepositoryUserDetailsService,
                           UserService userService,
                           PasswordEncoder passwordEncoder,
                           ProductService productService,
                           CategoryService categoryService,
                           PurchaseService purchaseService,
                           PurchaseItemService purchaseItemService) {
        this.userRepositoryUserDetailsService = userRepositoryUserDetailsService;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.productService = productService;
        this.categoryService = categoryService;
        this.purchaseService = purchaseService;
        this.purchaseItemService = purchaseItemService;
    }

    @GetMapping("/admin_dashboard")
    public String getAdminDashboard() {
        return "admin-dashboard";
    }

    @GetMapping("/admin_edit_profile")
    public String editUserForm(Authentication authentication, Model model, HttpServletRequest request) {
        if (userRepositoryUserDetailsService.isUserAuthenticated()) {
            SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
            Long adminUserId = securityUser.getUser().getId();
            User adminUser = userService.getUserById(adminUserId);
            HttpSession session = request.getSession();
            session.setAttribute("adminUserToUpdate", adminUser);
            model.addAttribute("admin", adminUser);
            return "admin-edit-profile";
        }
        return "user-login";
    }

    @PostMapping("/admin_process_update")
    public String processUpdate(UserRegistrationForm form, HttpServletRequest request) {
        HttpSession session = request.getSession();
        User adminUserToUpdate = (User) session.getAttribute("adminUserToUpdate");
        userService.updateUser(adminUserToUpdate, form.toUser(passwordEncoder));
        return "redirect:/user_edit_profile_confirm";
    }

    @GetMapping("/admin_edit_profile_confirm")
    public String editConfirm() {
        return "admin-edit-profile-confirm";
    }

    @ModelAttribute(name = "productList")
    public List<Product> populateProducts() {
        return this.productService.getAllProducts();
    }

    @ModelAttribute(name = "categoryList")
    public List<Category> populateCategories() {
        return this.categoryService.getAllCategories();
    }

    @ModelAttribute(name = "admin")
    public String mapCat(Principal principal) {
        if (principal != null) {
            return principal.getName();
        }
        return null;
    }

    @ModelAttribute(name = "mapCategories")
    public HashMap<Long, String> mapCategories() {
        List<Product> list = productService.getAllProducts();
        // use MAP to map the category names to product rows
        HashMap<Long, String> mapCats = new HashMap<Long, String>();
        for (Product product : list) {
            Category category = categoryService.getCategoryById(product.getCategoryId());
            if (category != null)
                mapCats.put(product.getId(), category.getName());
        }
        return mapCats;
    }

    @GetMapping(path = "/admin_list_products")
    public String getAllProducts() {
        return "admin-product-list";
    }

    @GetMapping(path = "/list_products_by_category")
    public String getProductsByCategory(Model model, HttpServletRequest request) {
        Long categoryId = Long.valueOf(request.getParameter("categoryId"));
        System.out.println("ID: " + categoryId);
        model.addAttribute("productList", productService.getProductsByCategoryId(categoryId));
        return "admin-product-list";
    }

    @GetMapping(path = "/admin_edit_product")
    public String editProduct(ModelMap map,
                              @RequestParam(value = "id", required = true) String productId,
                              HttpServletRequest request,
                              Authentication authentication) {
        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
        Long userId = securityUser.getUser().getId();
        User user = userService.getUserById(userId);
        if ((userRepositoryUserDetailsService.isUserAuthenticated()) && (user.getRole().equals("ROLE_ADMIN"))) {
            long idValue = Long.parseLong(productId);
            Product productToUpdate = new Product();
            productToUpdate.setCategoryId(1L);
            if (idValue > -1) {
                productToUpdate = productService.getProductById(idValue);
            }
            HttpSession session = request.getSession();
            session.setAttribute("productToUpdate", productToUpdate);
            map.addAttribute("productToUpdate", productToUpdate);
            return "admin-edit-product";
        }
        return "user-login";
    }

    @PostMapping("/admin_process_product_update")
    public String processProductUpdate(ProductForm form, HttpServletRequest request) {
        HttpSession session = request.getSession();
        Product productToUpdate = (Product) session.getAttribute("productToUpdate");
        productService.updateProduct(productToUpdate, form.toProduct());
        return "redirect:/admin_list_products";
    }

    @GetMapping(path = "/admin_delete_product")
    public String deleteProduct(@RequestParam(value = "id", required = true) String productId,
                                HttpServletRequest request,
                                Authentication authentication) {
        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
        Long userId = securityUser.getUser().getId();
        User user = userService.getUserById(userId);
        if ((userRepositoryUserDetailsService.isUserAuthenticated()) && (user.getRole().equals("ROLE_ADMIN"))) {
            long idValue = Long.parseLong(productId);
            System.out.println("VALUE " + idValue);
            if (idValue > -1) {
                productService.deleteProduct(idValue);
            }
            return "redirect:/admin_list_products";
        }
        return "user-login";
    }

    @GetMapping(path = "/admin_list_categories")
    public String getAllCategories() {
        return "admin-category-list";
    }


    @GetMapping(path = "/admin_edit_category")
    public String editCategory(ModelMap map,
                               @RequestParam(value = "id", required = true) String categoryId,
                               HttpServletRequest request,
                               Authentication authentication) {
        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
        Long userId = securityUser.getUser().getId();
        User user = userService.getUserById(userId);
        if ((userRepositoryUserDetailsService.isUserAuthenticated()) && (user.getRole().equals("ROLE_ADMIN"))) {
            long idValue = Long.parseLong(categoryId);
            Category categoryToUpdate = new Category();
            if (idValue > -1) {
                categoryToUpdate = categoryService.getCategoryById(idValue);
            }
            HttpSession session = request.getSession();
            session.setAttribute("categoryToUpdate", categoryToUpdate);
            map.addAttribute("categoryToUpdate", categoryToUpdate);
            return "admin-edit-category";
        }
        return "user-login";
    }

    @PostMapping("/admin_process_category_update")
    public String processCategoryUpdate(CategoryForm form, HttpServletRequest request) {
        HttpSession session = request.getSession();
        Category categoryToUpdate = (Category) session.getAttribute("categoryToUpdate");
        categoryService.updateCategory(categoryToUpdate, form.toCategory());
        return "redirect:/admin_list_categories";
    }

    @GetMapping(path = "/admin_delete_category")
    public String deleteCategory(@RequestParam(value = "id", required = true) String categoryId,
                                 HttpServletRequest request,
                                 Authentication authentication) {
        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
        Long userId = securityUser.getUser().getId();
        User user = userService.getUserById(userId);
        if ((userRepositoryUserDetailsService.isUserAuthenticated()) && (user.getRole().equals("ROLE_ADMIN"))) {
            long idValue = Long.parseLong(categoryId);
            if (idValue > -1) {
                categoryService.deleteCategory(idValue);
            }
            return "redirect:/admin_list_categories";
        }
        return "user-login";
    }

    @ModelAttribute(name = "memberList")
    public List<User> populateMember() {
        return this.userService.getAllUsers();
    }

    @GetMapping(path = "/admin_list_members")
    public String getAllMembers() {
        return "admin-member-list";
    }

    @GetMapping(path = "/admin_purchase_report")
    public String purchases(ModelMap map, HttpServletRequest request) {
        List<Purchase> listAllPurchases = purchaseService.getAllPurchases();
        BigDecimal total = new BigDecimal(0.0);
        for (Purchase purchase : listAllPurchases) {
            total = total.add(purchase.getTotal());
        }
        HashMap<Long, String> mapItems = new HashMap<Long, String>();
        HashMap<Long, String> mapUsers = new HashMap<Long, String>();

        for (Purchase purchase : listAllPurchases) {
            total = total.add(purchase.getTotal());
            User user = userService.getUserById(purchase.getUserId());
            if (user != null)
                mapUsers.put(purchase.getId(), user.getFirstname() + " " + user.getLastname());

            List<PurchaseItem> purchaseItemList = purchaseItemService.getAllPurchaseItemsByPurchaseId(purchase.getId());
            StringBuilder sb = new StringBuilder("");
            for (PurchaseItem item : purchaseItemList) {
                Product product = productService.getProductById(item.getProduct().getId());
                if (product != null)
                    sb.append(product.getName() + ", " + item.getQuantity() + " units @" + item.getRate() + " = "
                            + item.getPrice() + "\n");
            }
            mapItems.put(purchase.getId(), sb.toString());
        }
        map.addAttribute("totalAmount", total);
        map.addAttribute("listAllPurchases", listAllPurchases);
        map.addAttribute("mapItems", mapItems);
        map.addAttribute("mapUsers", mapUsers);
        return "admin-purchases-report";
    }

    @GetMapping(path = "/search_user")
    public String searchUser(@RequestParam(value = "username", required = true) String username, ModelMap map) {
        System.out.println("USERNAME: " + username);
        if (username != null) {
            List<User> searchResult = userService.searchUserByUsername(username);
            for (User item : searchResult) {
                System.out.println("USER: " + item);
            }
            map.addAttribute("searchResult", searchResult);
        }
        return "admin-member-list";
    }
}
