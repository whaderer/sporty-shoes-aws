package com.sportyshoes.models;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;

import java.util.Date;

// Implementations of UserDetails will provide some essential user information to
// the framework, such as what authorities are granted to the user and whether the user’s
// account is enabled.
// An object that implements a UserDetailsService contract with Spring Security
// manages the details about users.

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC, force = true)
public class User {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String username;
    private String firstname;
    private String lastname;
    private String address;
    private int age;
    private String password;
    private String role;
    private boolean enabled;

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date dateAdded;

    public User(String username, String firstname, String lastname, String address, int age, String encode, String role, boolean enabled) {
        this.username = username;
        this.firstname = firstname;
        this.lastname = lastname;
        this.address = address;
        this.age = age;
        this.password = encode;
        this.role = role;
        this.enabled = enabled;
    }

    @PrePersist
    private void onCreate() {
        dateAdded = new Date();
    }

}
