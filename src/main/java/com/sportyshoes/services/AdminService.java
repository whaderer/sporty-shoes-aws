package com.sportyshoes.services;

import com.sportyshoes.exceptions.ProductNotFoundException;
import com.sportyshoes.models.Admin;
import com.sportyshoes.repositories.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
public class AdminService {

    private final AdminRepository adminRepository;

    @Autowired
    public AdminService(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    @Transactional
    public Admin getUserById(Long id) {
        Optional<Admin> optionalAdmin = adminRepository.findById(id);
        return optionalAdmin.orElseThrow(() -> new ProductNotFoundException());
    }
}
