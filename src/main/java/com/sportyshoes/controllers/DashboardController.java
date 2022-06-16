package com.sportyshoes.controllers;

import com.sportyshoes.models.User;
import com.sportyshoes.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.security.Principal;

@Controller
public class DashboardController {

    private UserRepository userRepository;

    @Autowired
    public DashboardController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @RequestMapping(value = "/user_dashboard", method = RequestMethod.GET)
    public String getUserDashboard() {
        return "user-dashboard";
    }

    // accept a java.security.Principal as a parameter. Use the principal name to look up the user from UserRepository
    @ModelAttribute(name = "user")
    public User user(Principal principal) {
        String username = principal.getName();
        return userRepository.findByUsername(username);
    }
}
