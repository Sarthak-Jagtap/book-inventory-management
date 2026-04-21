package com.bookinventoryfrontend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // ── Wire the session-based security context repository ──────────
            // This is the KEY fix: ensures security context saved in
            // AuthViewController.processLogin() is loaded on every request.
            .securityContext(ctx -> ctx
                .securityContextRepository(securityContextRepository())
            )

            // ── CSRF ────────────────────────────────────────────────────────
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/auth/do-login")
                // Logout uses a POST form with CSRF token — no need to ignore it
            )

            // ── URL access rules ────────────────────────────────────────────
            .authorizeHttpRequests(auth -> auth
                // Static resources
                .requestMatchers("/css/**", "/js/**", "/images/**", "/favicon.ico").permitAll()
                // Public pages
                .requestMatchers("/", "/home", "/error", "/access-denied").permitAll()
                // Team pages (supervisor browses without login)
                .requestMatchers("/team/**").permitAll()
                // Auth pages
                .requestMatchers(
                    "/auth/login",
                    "/auth/do-login",
                    "/auth/register",
                    "/auth/do-register",
                    "/auth/logout"
                ).permitAll()
                
             // ───────── BOOK (ROLE BASED) ─────────
                .requestMatchers("/books/add", "/books/update", "/books/delete")
                    .hasAnyRole("StoreOwner", "Admin")

                .requestMatchers("/books/**")
                    .permitAll()   // GET operations allowed

                // ───────── CATEGORY ─────────
                .requestMatchers("/categories/add", "/categories/update", "/categories/delete")
                    .hasAnyRole("StoreOwner", "Admin")

                .requestMatchers("/categories/**")
                    .permitAll()

                // ───────── PUBLISHER ─────────
                .requestMatchers("/publishers/add", "/publishers/update", "/publishers/delete")
                    .hasAnyRole("StoreOwner", "Admin")

                .requestMatchers("/publishers/**")
                    .permitAll()
                    
                // User self-service pages
                .requestMatchers(
                    "/user/profile",
                    "/user/dashboard",
                    "/user/purchases",
                    "/user/change-password"
                ).hasAnyRole("RegisteredUser", "StoreOwner", "Admin")
                // Store Owner pages
                .requestMatchers("/store-owner/**").hasAnyRole("StoreOwner", "Admin")
                // Admin pages
                .requestMatchers("/admin/**").hasRole("Admin")
                // Everything else needs auth
                .anyRequest().authenticated()
            )

            // ── Login page ──────────────────────────────────────────────────
            // Just declare the login page — actual processing is in our controller
            .formLogin(form -> form
                .loginPage("/auth/login")
                .permitAll()
            )

            // ── Logout ──────────────────────────────────────────────────────
            // Logout works via POST form (CSRF protected) from navbar
            .logout(logout -> logout
                .logoutUrl("/auth/logout")
                .logoutSuccessUrl("/home")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )

            // ── Access denied ────────────────────────────────────────────────
            .exceptionHandling(ex -> ex
                .accessDeniedPage("/access-denied")
            );

        return http.build();
    }

    // ── Required beans ───────────────────────────────────────────────────────

    @Bean
    public SecurityContextRepository securityContextRepository() {
        // This stores and loads the Spring Security context from the HTTP session.
        // MUST be the same instance used in AuthViewController.processLogin()
        // AND wired into the filter chain above.
        return new HttpSessionSecurityContextRepository();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        // Empty placeholder — real auth happens via backend API call.
        return new InMemoryUserDetailsManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}