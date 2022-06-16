package com.sportyshoes.controllers;

import com.sportyshoes.models.User;
import com.sportyshoes.security.SecurityUser;
import com.sportyshoes.security.UserRegistrationForm;
import com.sportyshoes.security.UserRepositoryUserDetailsService;
import com.sportyshoes.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.WebAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
public class UserController {

    private final UserRepositoryUserDetailsService userRepositoryUserDetailsService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserController(UserRepositoryUserDetailsService userRepositoryUserDetailsService, UserService userService, PasswordEncoder passwordEncoder) {
        this.userRepositoryUserDetailsService = userRepositoryUserDetailsService;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @RequestMapping(value = "/login_user", method = RequestMethod.GET)
    public String login() {
        return "user-login";
    }

    @RequestMapping(value = "/login_error", method = RequestMethod.GET)
    public String login(HttpServletRequest request, Model model) {
        // will return current session if current session exists. If not, it will not create a new session.
        HttpSession session = request.getSession(false);
        String errorMessage = null;
        if (session != null) {
            AuthenticationException ex = (AuthenticationException) session
                    .getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
            if (ex != null) {
                errorMessage = ex.getMessage();
            }
        }
        model.addAttribute("errorMessage", errorMessage);
        return "user-login";
    }

    @GetMapping("/user_edit_profile")
    public String editUserForm(Authentication authentication, Model model, HttpServletRequest request) {
        if (userRepositoryUserDetailsService.isUserAuthenticated()) {
            SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
            Long userId = securityUser.getUser().getId();
            User user = userService.getUserById(userId);
            HttpSession session = request.getSession();
            session.setAttribute("userToUpdate", user);
            model.addAttribute("user", user);
            return "user-edit-profile";
        }
        return "user-login";
    }

    @PostMapping("/user_process_update")
    public String processUpdate(UserRegistrationForm form, HttpServletRequest request) {
        HttpSession session = request.getSession();
        User userToUpdate = (User) session.getAttribute("userToUpdate");
        userService.updateUser(userToUpdate, form.toUser(passwordEncoder));
        return "redirect:/user_edit_profile_confirm";
    }

    @GetMapping("/user_edit_profile_confirm")
    public String editConfirm() {
        return "user-edit-profile-confirm";
    }

}

