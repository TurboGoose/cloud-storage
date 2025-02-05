package ru.turbogoose.cloud.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import ru.turbogoose.cloud.exceptions.UsernameAlreadyExistsException;
import ru.turbogoose.cloud.models.User;
import ru.turbogoose.cloud.services.UserService;

@Slf4j
@Controller
@RequiredArgsConstructor
public class AuthenticationController {
    private final UserService userService;

    @GetMapping("/login")
    public String getLoginForm() {
        return "auth/login";
    }

    @GetMapping("/signup")
    public String getSignupForm(Model model) {
        model.addAttribute("user", new User());
        return "auth/signup";
    }

    @PostMapping("/signup")
    public String signUpUser(@ModelAttribute("user") @Valid User user,
                             BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "auth/signup";
        }
        try {
            userService.createUser(user);
            log.debug("Sign up successful for user \"{}\" (id={})", user.getUsername(), user.getId());
            return "redirect:/login";
        } catch (UsernameAlreadyExistsException exc) {
            log.debug("Sign up failed for user \"{}\"", user.getUsername(), exc);
            bindingResult.rejectValue("username", "user.alreadyExists", "User with this username already exists");
            return "auth/signup";
        }
    }
}
