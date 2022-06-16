package com.sportyshoes.services;

import com.sportyshoes.exceptions.ProductNotFoundException;
import com.sportyshoes.models.Product;
import com.sportyshoes.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional
    public Product getProductById(long id) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        return optionalProduct.orElseThrow(() -> new ProductNotFoundException());
    }

    @Transactional
    public void updateProduct(Product productToUpdate, Product editedProduct) {
        productToUpdate.setName(editedProduct.getName());
        productToUpdate.setPrice(editedProduct.getPrice());
        productToUpdate.setCategoryId(editedProduct.getCategoryId());
         productRepository.save(productToUpdate);
    }

    @Transactional
    public void deleteProduct(long id) {
        productRepository.deleteById(id);
    }

    @Transactional
    public List<Product> getAllProducts() {
        return (List<Product>) productRepository.findAll();
    }

    @Transactional
    public List<Product> getProductsByCategoryId(Long categoryId) {
        return (List<Product>) productRepository.findByCategoryId(categoryId);
    }

    @Transactional
    public Product addProduct(Product product) {
        return productRepository.save(product);
    }

}
