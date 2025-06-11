package net.sample.wordpress.controller;

import lombok.AllArgsConstructor;
import net.sample.wordpress.entity.User;
import net.sample.wordpress.service.UserService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@AllArgsConstructor
@RequestMapping("/register")
public class RegisterController {

    private final UserService userService;

    @GetMapping()
    public String showRegisterationForm(Model model) {

        model.addAttribute("user", new User());
        return "register";

    }

    @PostMapping()
    public String registerUser(User user, Model model) {
        User isRegistered= userService.saveUser(user);
        if(isRegistered == null) {
            throw new UsernameNotFoundException("User not found");

        }

        model.addAttribute("user", user);
        return "redirect:/login";
    }




}
