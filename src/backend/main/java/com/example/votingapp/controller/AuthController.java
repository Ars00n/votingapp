package com.example.votingapp.controller;

import com.example.votingapp.model.User;
import com.example.votingapp.security.JwtTokenProvider;
import com.example.votingapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = Logger.getLogger(AuthController.class.getName());

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> loginRequest) {
        String email = loginRequest.get("email");
        String password = loginRequest.get("password");

        logger.info("Attempting to login user with email: " + email);

        // Використання Optional для перевірки користувача
        Optional<User> optionalUser = userService.findByEmail(email);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            logger.info("User found: " + user.toString());
            if (passwordEncoder.matches(password, user.getPassword())) {
                logger.info("Password matches for user with email: " + email);

                String token = jwtTokenProvider.generateToken(email);

                Map<String, String> response = new HashMap<>();
                response.put("token", token);
                response.put("role", user.getRole());

                return ResponseEntity.ok(response);
            } else {
                logger.info("Password does not match for user with email: " + email);
                return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
            }
        } else {
            logger.info("User not found with email: " + email);
            return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
        }
    }
}
