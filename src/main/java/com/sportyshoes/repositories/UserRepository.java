package com.sportyshoes.repositories;

import com.sportyshoes.models.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends CrudRepository<User, Long> {

    // In addition to the CRUD operations provided by extending CrudRepository, User-
    // Repository defines a findByUsername() method that you’ll use in the user details service
    // to look up a User by their username.
    User findByUsername(String username);

    @Query(value = "SELECT u FROM User u WHERE u.username LIKE %:username% AND u.role = 'ROLE_USER' ORDER BY u.username")
    List<User> searchUserByUsername(@Param("username") String username);
}



