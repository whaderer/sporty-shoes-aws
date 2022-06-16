package com.sportyshoes.controllers;

import com.sportyshoes.models.Category;
import com.sportyshoes.models.Product;
import com.sportyshoes.repositories.UserRepository;
import com.sportyshoes.services.CategoryService;
import com.sportyshoes.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;

@RequestMapping("/home")
@Controller
public class HomeController {

    private final ProductService productService;
    private final CategoryService categoryService;

    @Autowired
    public HomeController(ProductService productService, CategoryService categoryService, UserRepository userRepository) {
        this.productService = productService;
        this.categoryService = categoryService;
    }

    // curl -X GET -H 'content-type:application/json' http://localhost:8080/home/allProducts
    // curl -X POST -H 'content-type:application/json' -d '{"name": "fish and chips", "price" : "100", "dateAdded" : "2022-02-03", "categoryId" : "1"}' http://localhost:8080/home/addProduct

    @PostMapping(path = "/addProduct") // Map ONLY POST Requests
    public @ResponseBody
    String addNewProduct(
            @RequestBody Product product
    ) {
        // @ResponseBody means the returned String is the response, not a view name
        productService.addProduct(product);
        return "Saved";
    }

    // curl -X POST -H 'content-type:application/json' -d '{"name": "fish"}' http://localhost:8080/home/addCategory

    @PostMapping(path = "/addCategory") // Map ONLY POST Requests
    public @ResponseBody String addNewCategory(
            @RequestBody Category category) {
        // @RequestParam means it is a parameter from the GET or POST request
        categoryService.addCategory(category);
        return "Saved";
    }

    @ModelAttribute("allProducts")
    public List<Product> populateProducts() {
        return this.productService.getAllProducts();
    }

    // accept a java.security.Principal as a parameter.
    @ModelAttribute(name = "user")
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

    @GetMapping(path = "/allProducts")
    public String getAllProducts() {
        return "index";
    }
}
