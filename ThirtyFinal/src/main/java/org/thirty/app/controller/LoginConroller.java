package org.thirty.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class LoginConroller {

    @GetMapping("/login")
    public String login(Principal principal) {
        if (principal != null) {
             // Si está loggeado redirect /home
            return "redirect:/home";
        } else {
                return "login";
            //}
        }
    }
}

