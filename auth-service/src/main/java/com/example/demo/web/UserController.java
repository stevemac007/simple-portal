package com.example.demo.web;

import com.example.demo.domain.User;
import com.example.demo.repository.UserRepository;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import java.util.*;

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

    @Secured("ROLE_ADMIN")
    @PostMapping("/resetpassword/{id}")
    public ResponseEntity resetPassword(@PathVariable("id") Long id) {
        User existed = this.users.findById(id).orElseThrow(() -> new UserNotFoundException(id));

        String temporaryPassword = RandomString.make(8);
        existed.setTemporaryPassword(passwordEncoder.encode(temporaryPassword));

        this.users.save(existed);

        Map<Object, Object> map = new HashMap<>();
        map.put("temporary_password", temporaryPassword);

        return ok(map);
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/changepassword/{id}")
    public ResponseEntity changePassword(@PathVariable("id") Long id, @Valid @RequestBody UserPasswordResetForm form) {
        User existed = this.users.findById(id).orElseThrow(() -> new UserNotFoundException(id));

        if (existed.getTemporaryPassword() != null && passwordEncoder.matches(form.getCurrentPassword(), existed.getTemporaryPassword())) {
            existed.setPassword(passwordEncoder.encode(form.getNewPassword()));
            existed.setTemporaryPassword(null);
            this.users.save(existed);
        }
        else if (passwordEncoder.matches(form.getCurrentPassword(), existed.getPassword())) {
            existed.setPassword(passwordEncoder.encode(form.getNewPassword()));
            existed.setTemporaryPassword(null);
            this.users.save(existed);
        }
        else {
            throw new BadCredentialsException("Invalid username/password supplied");
        }

        Map<Object, Object> map = new HashMap<>();
        map.put("result", "Password changed successfully");

        return ok(map);
    }

    @PostMapping("/changepassword")
    public ResponseEntity changeSelfPassword(@AuthenticationPrincipal UserDetails userDetails, @Valid @RequestBody UserPasswordResetForm form) {
        User existed = this.users.findByUsername(userDetails.getUsername()).orElseThrow(() -> new UserNotFoundException());

        if (existed.getTemporaryPassword() != null && passwordEncoder.matches(form.getCurrentPassword(), existed.getTemporaryPassword())) {
            existed.setPassword(passwordEncoder.encode(form.getNewPassword()));
            existed.setTemporaryPassword(null);
            this.users.save(existed);
        }
        else if (passwordEncoder.matches(form.getCurrentPassword(), existed.getPassword())) {
            existed.setPassword(passwordEncoder.encode(form.getNewPassword()));
            existed.setTemporaryPassword(null);
            this.users.save(existed);
        }
        else {
            throw new BadCredentialsException("Invalid password supplied");
        }

        Map<Object, Object> map = new HashMap<>();
        map.put("result", "Password changed successfully");

        return ok(map);
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


    @Secured("ROLE_ADMIN")
    @PutMapping("/{id}")
    public ResponseEntity update(@PathVariable("id") Long id, @RequestBody UserForm form) {
        User existed = this.users.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        existed.setUsername(form.getUsername());
        existed.setRoles(form.getRoles());

        this.users.save(existed);
        return noContent().build();
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable("id") Long id) {
        User existed = this.users.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        this.users.delete(existed);
        return noContent().build();
    }
}
