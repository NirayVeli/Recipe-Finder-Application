package com.recipefinder.controller;

import com.recipefinder.model.User;
import com.recipefinder.repository.UserRepository;
import com.recipefinder.dto.UserDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.Cookie;

import java.security.Principal;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
// change the default localhost later
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class UserController {

@Autowired
private UserRepository userRepository;

@Autowired
private PasswordEncoder passwordEncoder;

@Autowired
private AuthenticationManager authenticationManager;

// check for registered users and users

@PostMapping("/register")
public ResponseEntity<?> registerUser(@RequestBody User user) {
    if (userRepository.findByUsername(user.getUsername()).isPresent()) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already exists");
    }

    user.setPassword(passwordEncoder.encode(user.getPassword()));
    if (user.getRole() == null || user.getRole().isEmpty()) {
        user.setRole("ROLE_USER");
    }

    userRepository.save(user);

    return ResponseEntity.ok("User registered successfully");
}

// Ze Login

@PostMapping("/login")
public ResponseEntity<?> login(@RequestBody Map<String, String> loginData, HttpServletRequest request, HttpServletResponse response) {
    String username = loginData.get("username");
    String password = loginData.get("password");

    System.out.println("Login attempt for user: " + username);

    try {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        HttpSession session = request.getSession(true);
        System.out.println("Session created/vzet. Session ID (sled auth): " + session.getId());

        Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();
        if (currentAuth != null && currentAuth.isAuthenticated()) {
            System.out.println("SecurityContextHolder after login: user '" + currentAuth.getName() + "' is authenticated.");
        } else {
            System.out.println("SecurityContextHolder after login: user is not authenticated or empty.");
        }
// manual adding  - YouTube check
        String jsessionid = session.getId();
        String cookieHeader = String.format("JSESSIONID=%s; Path=/; HttpOnly; Secure; SameSite=None; Max-Age=%d",
                jsessionid, (7 * 24 * 60 * 60)); // 7 days
        response.addHeader("Set-Cookie", cookieHeader);
        System.out.println("manually added Set-Cookie header: " + cookieHeader);

        User user = userRepository.findByUsername(username).orElse(null);

        if (user != null) {
            UserDto userDto = new UserDto(user.getId(), user.getUsername(), user.getRole());
            System.out.println("login successful for user: " + user.getUsername() + ", Role: " + user.getRole());
            return ResponseEntity.ok(userDto);
        } else {
            System.out.println("user not found in DB after authentication (ne trqq stava).");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("User data not found after authentication.");
        }

    } catch (Exception e) {
        System.err.println("authentication failed for user " + username + ": " + e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("invalid username or password");
    }
}

@GetMapping("/me")
public ResponseEntity<?> getCurrentUser(Principal principal) {
    System.out.println("Entering /me endpoint.");

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null) {
        System.out.println("/me endpoint: SecurityContextHolder.getContext().getAuthentication() is NULL.");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated: SecurityContext is null.");
    }
    if (!authentication.isAuthenticated()) {
        System.out.println("/me endpoint: User is NOT authenticated in SecurityContext. Authentication object: " + authentication);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated: Not isAuthenticated.");
    }
    if ("anonymousUser".equals(authentication.getName())) {
        System.out.println("/me endpoint: User is 'anonymousUser'. Authentication object: " + authentication);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated: Anonymous user.");
    }

    if (principal == null) {
        System.out.println("/me endpoint: Principal is null, user not authenticated (Security context e settnat).");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated.");
    }

    String username = principal.getName();
    System.out.println("/me endpoint: Authenticated user: " + username);

    Optional<User> userOptional = userRepository.findByUsername(username);
    if (userOptional.isPresent()) {
        User user = userOptional.get();
        UserDto userDto = new UserDto(user.getId(), user.getUsername(), user.getRole());
        System.out.println("/me endpoint: Returning user DTO for: " + user.getUsername());
        return ResponseEntity.ok(userDto);
    } else {
        System.out.println("/me endpoint: User not found in DB for username: " + username);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
    }
}

@PostMapping("/logout")
public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
    System.out.println("entering /api/users/logout endpoint.");

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getName())) {
        System.out.println("user " + authentication.getName() + " is attempting to log out.");
        // clear
        SecurityContextHolder.clearContext();
        System.out.println("SecurityContext cleared.");
        // invalidating
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
            System.out.println("HTTP Session invalidated - ID: " + session.getId());
        } else {
            System.out.println("No active HTTP session to invalidate.");
        }

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                // cookies are JSESSIONID?
                if (cookie.getName().equals("JSESSIONID")) {
                    cookie.setValue("");
                    cookie.setPath("/");
                    cookie.setMaxAge(0);
                    cookie.setHttpOnly(true);
                    cookie.setSecure(true);
                    cookie.setDomain("localhost");
                    response.addCookie(cookie);
                    System.out.println("JSESSIONID cookie se maha.");
                    break;
                }
            }
        } else {
            System.out.println("No cookies found to delete.");
        }

        return ResponseEntity.ok("Successfully logged out");
    } else {
        System.out.println("Logout request: No authenticated user or anonymous user.");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No authenticated user to log out.");
    }
}
}

// when returning for 2.0 version of the app make the code more readable
// 100% will forget what have i even written here