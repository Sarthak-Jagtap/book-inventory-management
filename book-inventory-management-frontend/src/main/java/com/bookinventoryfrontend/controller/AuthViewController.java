package com.bookinventoryfrontend.controller;

import com.bookinventoryfrontend.dto.LoginResponseDTO;
import com.bookinventoryfrontend.dto.RegisterRequestDTO;
import com.bookinventoryfrontend.service.BackendApiService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
 
import java.util.Collections;
 
@Controller
@RequestMapping("/auth")
public class AuthViewController {
 
    // Session key constants — used everywhere in the app to get the JWT
    public static final String SESSION_JWT_TOKEN  = "JWT_TOKEN";
    public static final String SESSION_USER_ID    = "USER_ID";
    public static final String SESSION_USER_NAME  = "USER_NAME";
    public static final String SESSION_ROLE_NAME  = "ROLE_NAME";
    public static final String SESSION_FIRST_NAME = "FIRST_NAME";
 
    private final BackendApiService      backendApiService;
    private final SecurityContextRepository securityContextRepository;
 
    public AuthViewController(BackendApiService backendApiService,
                              SecurityContextRepository securityContextRepository) {
        this.backendApiService         = backendApiService;
        this.securityContextRepository = securityContextRepository;
    }
 
    // ── GET /auth/login — Show login form ────────────────────────
    @GetMapping("/login")
    public String showLoginPage(
            @RequestParam(required = false) String error,
            @RequestParam(required = false) String logout,
            @RequestParam(required = false) String registered,
            Model model) {
 
        if (error    != null) model.addAttribute("error",
                "Invalid username or password. Please try again.");
        if (logout   != null) model.addAttribute("logout", true);
        if (registered != null) model.addAttribute("registered", true);
 
        return "auth/login"; // → templates/auth/login.html
    }
 
    // ── POST /auth/do-login — Process login form submission ──────
    //
    // HOW THIS WORKS:
    // 1. Get userName + password from form
    // 2. Call backend API: POST /api/v1/auth/login
    // 3. Backend validates and returns JWT + user info
    // 4. We store JWT in HTTP session (so every future request can use it)
    // 5. We tell Spring Security "this user is logged in with this role"
    // 6. Redirect to home page
    //
    @PostMapping("/do-login")
    public String processLogin(
            @RequestParam String userName,
            @RequestParam String password,
            HttpServletRequest  request,
            HttpServletResponse response,
            Model model) {
 
        try {
            // Step 1: Call backend login API
            LoginResponseDTO loginResult = backendApiService.login(userName, password);
 
            // Step 2: Store JWT and user info in HTTP Session
            // This session data is available in ALL controllers via session.getAttribute()
            HttpSession session = request.getSession(true);
            session.setAttribute(SESSION_JWT_TOKEN,  loginResult.getToken());
            session.setAttribute(SESSION_USER_ID,    loginResult.getUserId());
            session.setAttribute(SESSION_USER_NAME,  loginResult.getUserName());
            session.setAttribute(SESSION_ROLE_NAME,  loginResult.getRoleName());
            session.setAttribute(SESSION_FIRST_NAME, loginResult.getFirstName());
 
            // Step 3: Build Spring Security authentication object
            // This is what allows @sec:authorize and .hasRole() checks to work in Thymeleaf
            String roleName = loginResult.getRoleName(); // e.g. "Admin"
 
            UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(
                    loginResult.getUserName(),   // principal (username)
                    null,                         // credentials (null — we don't store password)
                    Collections.singletonList(
                        new SimpleGrantedAuthority("ROLE_" + roleName)
                    )
                );
 
            // Step 4: Save authentication to Spring Security context (stored in session)
            SecurityContext context = new SecurityContextImpl(auth);
            SecurityContextHolder.setContext(context);
            securityContextRepository.saveContext(context, request, response);
 
            // Step 5: Redirect to home page
            return "redirect:/home";
 
        } catch (Exception e) {
            // Extract clean message from JSON error response
            // Raw error looks like: 401 Unauthorized: {"success":false,...,"message":"Invalid username or password",...}
            String rawMessage = e.getMessage();
            String cleanMessage = extractErrorMessage(rawMessage);

            model.addAttribute("error", cleanMessage);
            model.addAttribute("userName", userName);
            return "auth/login";
        }
    }
 
    // ── GET /auth/register — Show registration form ──────────────
    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        try {
            // Load all roles to show in the dropdown
            model.addAttribute("roles", backendApiService.getAllRoles());
        } catch (Exception e) {
            // If backend is not available, show empty form without roles
        }
        return "auth/register"; // → templates/auth/register.html
    }
 
    // ── POST /auth/do-register — Process registration form ───────
    @PostMapping("/do-register")
    public String processRegister(
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam String userName,
            @RequestParam String password,
            @RequestParam(required = false) String phoneNumber,
            @RequestParam(required = false) Integer roleNumber,
            Model model) {
 
        try {
            // Build register DTO from form fields
            RegisterRequestDTO dto = new RegisterRequestDTO();
            dto.setFirstName(firstName);
            dto.setLastName(lastName);
            dto.setUserName(userName);
            dto.setPassword(password);
 
            // Only set phone if provided and not empty
            if (phoneNumber != null && !phoneNumber.isBlank()) {
                dto.setPhoneNumber(phoneNumber);
            }
 
            // Only set role if chosen (backend defaults to RegisteredUser)
            if (roleNumber != null) {
                dto.setRoleNumber(roleNumber);
            }
 
            // Call backend register API
            backendApiService.register(dto);
 
            // Registration success → redirect to login with success message
            return "redirect:/auth/login?registered=true";
 
        } catch (Exception e) {
            model.addAttribute("error", extractErrorMessage(e.getMessage())); // ← change this
            model.addAttribute("firstName",   firstName);
            model.addAttribute("lastName",    lastName);
            model.addAttribute("userName",    userName);
            model.addAttribute("phoneNumber", phoneNumber);
            try {
                model.addAttribute("roles", backendApiService.getAllRoles());
            } catch (Exception ignored) {}
            return "auth/register";
        }
    }
    
    /**
     * Extracts a clean, user-friendly error message from the raw
     * exception message which may contain a full JSON body.
     *
     * Example input:
     *   "401 Unauthorized: {"success":false,"statusCode":401,
     *    "message":"Invalid username or password","data":null}"
     *
     * Returns: "Invalid username or password"
     */
    private String extractErrorMessage(String rawMessage) {
        if (rawMessage == null) {
            return "An unexpected error occurred. Please try again.";
        }

        // Try to extract "message":"..." from the JSON
        try {
            int msgIndex = rawMessage.indexOf("\"message\":\"");
            if (msgIndex != -1) {
                int start = msgIndex + 11; // length of "message":"
                int end   = rawMessage.indexOf("\"", start);
                if (end > start) {
                    return rawMessage.substring(start, end);
                }
            }
        } catch (Exception ignored) {}

        // Fallback: if it's a 401, give a standard message
        if (rawMessage.contains("401")) {
            return "Invalid username or password. Please check your credentials.";
        }
        if (rawMessage.contains("404")) {
            return "User not found. Please check your username.";
        }
        if (rawMessage.contains("500")) {
            return "Backend server error. Please try again later.";
        }
        if (rawMessage.contains("Connection refused") ||
            rawMessage.contains("connect timed out")) {
            return "Cannot connect to backend server. Please ensure it is running.";
        }

        // Last fallback: return raw but trimmed
        return rawMessage.length() > 120 ? rawMessage.substring(0, 120) + "..." : rawMessage;
    }
}
