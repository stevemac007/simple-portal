package com.example.demo.web;

import com.example.demo.domain.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import static org.springframework.http.ResponseEntity.*;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*")
public class UserController {

    private UserRepository users;

    @Autowired
    PasswordEncoder passwordEncoder;

    public UserController(UserRepository users) {
        this.users = users;
    }

    @GetMapping("")
    public ResponseEntity all(HttpServletRequest request, @AuthenticationPrincipal UserDetails userDetails) {
        if (request.isUserInRole("ROLE_ADMIN")) {
            return ok(this.users.findAll());
        }
        else {
            return ok(this.users.findByUsername(userDetails.getUsername()));
        }
    }

    @PostMapping("")
    public ResponseEntity save(@Valid @RequestBody UserForm form, HttpServletRequest request) {

        if (this.users.findByUsername(form.getUsername()).isPresent()) {
            throw new UsernameDuplicateException(form.getUsername());
        }

        // ENSURE THE DEFAULT USER ROLE IS PRESENT
        if (form.getRoles() == null) {
            form.setRoles(new ArrayList<>());
            form.getRoles().add("USER");
        }

        User saved = this.users.save(User.builder()
                .username(form.getUsername())
                .password(this.passwordEncoder.encode("password"))
                .roles(form.getRoles())
                .build());

        return created(
            ServletUriComponentsBuilder
                .fromContextPath(request)
                .path("/v1/users/{id}")
                .buildAndExpand(saved.getId())
                .toUri())
            .build();
    }

    @GetMapping("/{id}")
    public ResponseEntity get(@PathVariable("id") Long id) {
        return ok(this.users.findById(id).orElseThrow(() -> new UserNotFoundException(id)));
    }


    @PutMapping("/{id}")
    public ResponseEntity update(@PathVariable("id") Long id, @RequestBody UserForm form) {
        User existed = this.users.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        existed.setUsername(form.getUsername());
        existed.setRoles(form.getRoles());

        this.users.save(existed);
        return noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable("id") Long id) {
        User existed = this.users.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        this.users.delete(existed);
        return noContent().build();
    }
}
