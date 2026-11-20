package com.bank.ePon.controller;

import com.bank.ePon.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.bank.ePon.service.AuthService;
import jakarta.servlet.http.HttpSession;

@Controller
public class AuthController {

    @Autowired
    private AuthService authService;


    @GetMapping({"/", "/login"})
    public String showLoginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String processLogin(@RequestParam("mobileNumber") String mobileNumber,
                               @RequestParam("pin") String pin,
                               HttpSession session,
                               Model model) {
        try {
            User loggedInUser = authService.login(mobileNumber, pin);

            session.setAttribute("userId", loggedInUser.getId());
            session.setAttribute("userName", loggedInUser.getFullName());

            return "redirect:/dashboard";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "login";
        }
    }

    @GetMapping("/register")
    public String showRegisterPage() {
        return "register";
    }

    @PostMapping("/register")
    public String processRegister(@RequestParam("fullName") String fullName,
                                  @RequestParam("mobileNumber") String mobileNumber,
                                  @RequestParam("pin") String pin,
                                  Model model) {
        try {
            authService.registerUser(fullName, mobileNumber, pin);
            model.addAttribute("success", "Registration successful! Please log in.");
            return "login";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login?logout";
    }
}
