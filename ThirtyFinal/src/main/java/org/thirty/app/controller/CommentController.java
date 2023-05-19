package org.thirty.app.controller;

import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.thirty.app.model.BlogUser;
import org.thirty.app.model.Comment;
import org.thirty.app.model.Post;
import org.thirty.app.service.BlogUserService;
import org.thirty.app.service.CommentService;
import org.thirty.app.service.PostService;

import javax.validation.Valid;
import java.security.Principal;
import java.util.Optional;

@Controller
@SessionAttributes("comment")
public class CommentController {

    private final PostService postService;
    private final BlogUserService blogUserService;
    private final CommentService commentService;

    public CommentController(PostService postService, BlogUserService blogUserService, CommentService commentService) {
        this.postService = postService;
        this.blogUserService = blogUserService;
        this.commentService = commentService;
    }

    // El método GET que muestra el formulario de agregar comentarios
    @Secured("ROLE_USER") // Requiere que el usuario tenga el rol "ROLE_USER"
    @GetMapping("/comment/{id}")
    public String showComment(@PathVariable Long id, Model model, Principal principal) {

        String authUsername = "anonymousUser";
        if (principal != null) {
            authUsername = principal.getName();
        }

        // Encontrar al usuario actual
        Optional<BlogUser> optionalBlogUser = this.blogUserService.findByUsername(authUsername);
        // Encontrar el post por id
        Optional<Post> postOptional = this.postService.getById(id);
        
        // Si ambas opciones están presentes, crear un nuevo comentario y colocarlo en el modelo
        if (postOptional.isPresent() && optionalBlogUser.isPresent()) {
            Comment comment = new Comment();
            comment.setPost(postOptional.get());
            comment.setUser(optionalBlogUser.get());

            // Devolver el formulario de comentarios
            model.addAttribute("comment", comment);
            System.err.println("GET comentario/{id}: " + comment + "/" + id);
            return "commentForm"; 
        } else {
            // Devolver la página de error
            System.err.println("No se ha podido encontrar post por el id : " + id + " o por usuario loggueado : " + authUsername);
            return "error"; 
        }
    }

    // El método POST que valida y guarda el comentario
    // Requiere que el usuario tenga el rol "ROLE_USER"
    @Secured("ROLE_USER") 
    @PostMapping("/comment")
    public String validateComment(@Valid @ModelAttribute Comment comment, BindingResult bindingResult,
            SessionStatus sessionStatus) {
        System.err.println("POST comentario: " + comment);
        // Verificar si el comentario es válido
        if (bindingResult.hasErrors()) { 
            System.err.println("Comentario no validado");
            // Devolver el formulario de comentariosS
            return "commentForm"; 
        } else {
            // Guardar el comentario en la base de datos
            this.commentService.save(comment); 
            System.err.println("SAVE comentario: " + comment);
            sessionStatus.setComplete();
            // Redirigir al usuario a la página del post donde se agregó el comentario
            return "redirect:/post/" + comment.getPost().getId(); 
        }
    }

}
