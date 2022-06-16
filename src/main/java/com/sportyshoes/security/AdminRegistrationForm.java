package com.sportyshoes.security;

import com.sportyshoes.models.Admin;
import lombok.Data;
import org.springframework.security.crypto.password.PasswordEncoder;

@Data
public class AdminRegistrationForm {

    private String adminId;
    private String adminPwd;

    public Admin toAdmin(PasswordEncoder passwordEncoder) {
        return new Admin(adminId, passwordEncoder.encode(adminPwd));
    }

}
