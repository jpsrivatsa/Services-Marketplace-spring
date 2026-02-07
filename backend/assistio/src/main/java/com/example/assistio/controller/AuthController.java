package com.example.assistio.controller;
import com.example.assistio.model.PasswordResetToken;
import com.example.assistio.model.User;
import com.example.assistio.repository.UserRepository;
import com.example.assistio.repository.PasswordResetRepository;
import com.example.assistio.service.CustomUserDetailsService;
import com.example.assistio.service.MailService;
import com.example.assistio.util.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
@RestController
@CrossOrigin
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final MailService mailService;
    private final PasswordResetRepository passwordResetRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    public AuthController(AuthenticationManager authenticationManager, UserRepository userRepository,
                          PasswordEncoder passwordEncoder, JwtUtil jwtUtil, CustomUserDetailsService userDetailsService, PasswordResetRepository passwordResetRepository, MailService mailService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.passwordResetRepository = passwordResetRepository;
        this.mailService = mailService;
    }
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        if (userRepository.UsernameExists(user.getUsername())) {
            return ResponseEntity.ok().body(Map.of("error", "Username already exists"));
        }
        if (userRepository.EmailExists(user.getEmail())) {
            return ResponseEntity.ok().body(Map.of("error", "Email already exists"));
        }
        if (userRepository.PhoneExists(user.getPhoneNumber())) {
            return ResponseEntity.ok().body(Map.of("error", "Phone Number already exists"));
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.saveUser(user);
        return ResponseEntity.ok(Map.of("message", "User registered successfully"));
    }
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> loginRequest) {
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (RuntimeException e){
            return ResponseEntity.status(200).body(Map.of("error", e));
        }
        try {
            User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
            String token = jwtUtil.generateToken(user);
            return ResponseEntity.ok(Map.of("token", token, "role", user.getRole().getName()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(Map.of("error", e));
        }
    }
    @PostMapping("/forgotPassword")
    public ResponseEntity<?> forgotPasswod(@RequestBody Map<String, String> requestBody) {
        try{
            String username = requestBody.get("username");
            User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
            PasswordResetToken resetToken = new PasswordResetToken();
            Random random = new Random();
            int number = 100000 + random.nextInt(900000); // Guarantees 6 digits
            String token = String.valueOf(number);
            String maskedMail = passwordResetRepository.maskEmail(user.getEmail());
            try{
                resetToken = passwordResetRepository.findByUsername(user.getUsername()).get();
                if (resetToken.getUser().getUsername().equals(username)) {
                    return ResponseEntity.ok(Map.of("message", "An active request already exists. Kindly check your mail - " + maskedMail));
                }
            } catch (RuntimeException e) {
                resetToken.setToken(token);
                resetToken.setUser(user);
                resetToken.setExpiryDate(LocalDateTime.now().plusMinutes(15));
                String subject = "Assistio Password Reset OTP";
                String body = "Password Reset OTP: " + token + " Valid for 15 minutes";
                mailService.sendMail(user.getEmail(), subject, body);
                passwordResetRepository.insertToken(user, resetToken);
            }
            return ResponseEntity.ok(Map.of("message", "Password Reset OPT sent to your Email - "+ maskedMail +" Successfully. Please Enter OTP to reset password"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(200).body(Map.of("error", e));
        }
    }
    @PostMapping("/resetPassword")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> requestBody) {
        String email = requestBody.get("email");
        String token = requestBody.get("token");
        String new_password = requestBody.get("new_password");
        try{
            User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
            if (!passwordResetRepository.validateToken(token, user.getUsername())) {
                return ResponseEntity.status(200).body(Map.of("error", "Invalid OTP or email. Kindly check"));
            }
            passwordResetRepository.deleteToken(token);
            user.setPassword(passwordEncoder.encode(new_password));
            userRepository.resetPasswprd(user);
            return ResponseEntity.ok(Map.of("message", "Password Reset Successfully"));
        } catch (RuntimeException e){
            return ResponseEntity.status(404).body(Map.of("error", e));
        }
    }
} 
