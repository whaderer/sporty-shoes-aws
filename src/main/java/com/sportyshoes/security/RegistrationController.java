package com.sportyshoes.security;

import com.sportyshoes.repositories.AdminRepository;
import com.sportyshoes.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class RegistrationController {

    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    public RegistrationController(UserRepository userRepository, AdminRepository adminRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/registeruser")
    public String userRegisterForm() {
        return "user-register";
    }

    @PostMapping("/registeruser")
    public String userProcessRegistration(UserRegistrationForm form) {
        userRepository.save(form.toUser(passwordEncoder));
        return "redirect:/registerconfirm";
    }

    @GetMapping("/registeradmin")
    public String adminRegisterForm() {
        return "admin-register";
    }

    @PostMapping("/registeradmin")
    public String adminProcessRegistration(UserRegistrationForm form) {
        userRepository.save(form.toUser(passwordEncoder));
        return "redirect:/registerconfirm";
    }

    @GetMapping("/registerconfirm")
    public String registerconfirm() {
        return "register-confirm";
    }
}

