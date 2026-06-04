package tirthankarRana.LostFound.controller;

import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tirthankarRana.LostFound.dto.LoginRequest;
import tirthankarRana.LostFound.dto.RegisterRequest;
import tirthankarRana.LostFound.model.User;
import tirthankarRana.LostFound.repository.UserRepository;
import tirthankarRana.LostFound.util.PasswordUtil;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;

    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        if (request.getName() == null || request.getEmail() == null || request.getPassword() == null
            || request.getPhone() == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "All fields are required"));
        }

        String email = request.getEmail().trim().toLowerCase();
        String phone = request.getPhone().trim();
        String name = request.getName().trim();
        String password = request.getPassword().trim();

        if (name.length() < 2 || password.length() < 6) {
            return ResponseEntity.badRequest().body(Map.of("error", "Name or password is too short"));
        }

        if (userRepository.existsByEmail(email)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", "Email already registered"));
        }

        if (userRepository.existsByPhone(phone)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", "Phone already registered"));
        }

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(PasswordUtil.hash(password));
        user.setPhone(phone);

        User saved = userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(
            Map.of(
                "message", "Registration successful",
                "userId", saved.getId(),
                "name", saved.getName(),
                "email", saved.getEmail()
            )
        );
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        if (request.getEmail() == null || request.getPassword() == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email and password are required"));
        }

        Optional<User> userOpt = userRepository.findByEmail(request.getEmail().trim().toLowerCase());
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid credentials"));
        }

        User user = userOpt.get();
        if (!PasswordUtil.matches(request.getPassword().trim(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid credentials"));
        }

        return ResponseEntity.ok(
            Map.of(
                "message", "Login successful",
                "userId", user.getId(),
                "name", user.getName(),
                "email", user.getEmail()
            )
        );
    }
}
