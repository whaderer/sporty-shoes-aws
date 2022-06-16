package com.sportyshoes.security;

import com.sportyshoes.models.User;
import lombok.Data;
import org.springframework.security.crypto.password.PasswordEncoder;

@Data
public class UserRegistrationForm {

    private String username;
    private String firstname;
    private String lastname;
    private String address;
    private int age;
    private String password;
    private String role;
    private boolean enabled = true;

    public User toUser(PasswordEncoder passwordEncoder) {
        return new User(username, firstname, lastname, address, age, passwordEncoder.encode(password), role, enabled);
    }
}
