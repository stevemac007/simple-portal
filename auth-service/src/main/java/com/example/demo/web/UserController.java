package com.example.demo.web;

import com.example.demo.domain.User;
import com.example.demo.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.http.ResponseEntity.created;
import static org.springframework.http.ResponseEntity.noContent;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*")
public class UserController {

    private UserRepository users;

    public UserController(UserRepository users) {
        this.users = users;
    }

    @GetMapping("")
    public ResponseEntity all() {
        return ok(this.users.findAll());
    }

    @PostMapping("")
    public ResponseEntity save(@RequestBody UserForm form, HttpServletRequest request) {
        User saved = this.users.save(User.builder().username(form.getUsername()).build());
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
