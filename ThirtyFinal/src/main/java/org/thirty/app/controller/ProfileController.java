package org.thirty.app.controller;

import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.thirty.app.model.BlogUser;
import org.thirty.app.repository.BlogUserRepository;

@Transactional
@Controller
public class ProfileController {

    @Autowired
    private BlogUserRepository blogUserRepository;

    @GetMapping("/profile")
    public String showProfile(Authentication authentication, Model model) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        BlogUser blogUser = blogUserRepository.findByUsername(userDetails.getUsername()).orElse(null);

        if (blogUser != null) {
            model.addAttribute("user", blogUser);
            return "profile";
        } else {
            return "redirect:/error";
        }
    }

    @GetMapping("/profile/{id}")
    public String showProfileById(@PathVariable Long id, Model model) {
        Optional<BlogUser> optionalBlogUser = blogUserRepository.findById(id);
        
        if (optionalBlogUser.isPresent()) {
            BlogUser blogUser = optionalBlogUser.get();
            model.addAttribute("profileUser", blogUser);
            return "profile";
        } else {
            model.addAttribute("error", "El perfil solicitado no existe.");
            return "error";
        }
    }

    @GetMapping("/editProfile")
    public String showEditProfileForm(Authentication authentication, Model model) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        BlogUser blogUser = blogUserRepository.findByUsername(userDetails.getUsername()).orElse(null);

        if (blogUser != null) {
            model.addAttribute("blogUser", blogUser);
            model.addAttribute("bindingResult", new BeanPropertyBindingResult(blogUser, "blogUser"));
            return "editProfile";
        } else {
            return "redirect:/error";
        }
    }



    @PostMapping("/editProfile")
    public String processEditProfileForm(@Valid @ModelAttribute BlogUser blogUser, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "editProfile";
        }

        try {
            BlogUser existingBlogUser = blogUserRepository.findByUsername(blogUser.getUsername()).orElse(null);

            if (existingBlogUser != null && !existingBlogUser.getId().equals(blogUser.getId())) {
                model.addAttribute("error", "Nombre de usuario ya existe.");
                return "editProfile";
            }

            BlogUser savedBlogUser = blogUserRepository.save(blogUser);
            model.addAttribute("message", "Perfil actualizado exitosamente.");
            model.addAttribute("profileUser", savedBlogUser); // Agregar el objeto 'profileUser' al modelo

            return "redirect:/profile";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Error al guardar los cambios en el perfil.");
            return "editProfile";
        }
    }

}
