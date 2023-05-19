package org.thirty.app.controller;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.thirty.app.model.BlogUser;
import org.thirty.app.service.BlogUserService;

import javax.management.relation.RoleNotFoundException;
import javax.validation.Valid;

@Controller
@SessionAttributes("blogUser")
public class SignupController {

    private final BlogUserService blogUserService;

    // Inyección de dependencia mediante constructor de la clase BlogUserService
    public SignupController(BlogUserService blogUserService) {
        this.blogUserService = blogUserService;
    }

    @GetMapping("/signup")
    public String getRegisterForm(Model model) {
        // Crear una nueva instancia de BlogUser y pasarlo a la vista registerForm
        BlogUser blogUser = new BlogUser();
        model.addAttribute("blogUser", blogUser);
        return "registerForm";
    }

    @PostMapping("/register")
    public String registerNewUser(@Valid @ModelAttribute BlogUser blogUser, Model model, BindingResult bindingResult,@RequestParam("confirmPassword") String confirmPassword, SessionStatus sessionStatus) throws RoleNotFoundException {
        System.err.println("newUser: " + blogUser);  // Para fines de prueba y depuración

        // Comprobar si el nombre de usuario está disponible
        if (blogUserService.findByUsername(blogUser.getUsername()).isPresent()) {
            bindingResult.rejectValue("username", "error.username","Username ya está registrado");
            System.err.println("Username con mensaje de error");
        }

        // Comprobar si el correo electrónico está disponible
        if (blogUserService.findByEmail(blogUser.getEmail()).isPresent()) {
            bindingResult.rejectValue("email", "error.email","Correo electrónico ya está registrado");
            System.err.println("Correo electrónico con mensaje de error");
        }

        // Validar los campos del usuario
        if (bindingResult.hasErrors()) {
            System.err.println("Nuevo usuario sin validar");
            return "registerForm";
        }

        if (!blogUser.getPassword().equals(blogUser.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "error.confirmPassword", "Las contraseñas no coinciden");
            System.err.println("Confirmar Contraseña con mensaje de error");
            model.addAttribute("blogUser", blogUser);
            return "registerForm";
        }
        
        // Persistir nuevo usuario del blog
        blogUser = this.blogUserService.saveNewBlogUser(blogUser);

        // Crear el token de autenticación y hacer inicio de sesión después de registrar al nuevo usuario del blog
        Authentication auth = new UsernamePasswordAuthenticationToken(blogUser, blogUser.getPassword(), blogUser.getAuthorities());
        System.err.println("AuthToken: " + auth);  // Para fines de prueba y depuración
        SecurityContextHolder.getContext().setAuthentication(auth);
        System.err.println("SecurityContext Principal: " + SecurityContextHolder.getContext().getAuthentication().getPrincipal());  // Para fines de prueba y depuración
        sessionStatus.setComplete();
        return "redirect:/home";
    }
}
