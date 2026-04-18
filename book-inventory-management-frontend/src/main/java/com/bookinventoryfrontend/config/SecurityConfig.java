package com.bookinventoryfrontend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
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
            // ── CSRF ────────────────────────────────────────────────────
            // Keep CSRF enabled for Thymeleaf forms (protects POST forms).
            // Thymeleaf auto-adds _csrf hidden field to every form.
            // We only disable it for our custom login POST which we handle manually.
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/auth/do-login") // our custom login POST handler
            )

            // ── URL Access Rules ────────────────────────────────────────
            .authorizeHttpRequests(auth -> auth

                // Static resources — always public
                .requestMatchers(
                    "/css/**", "/js/**", "/images/**",
                    "/webjars/**", "/favicon.ico"
                ).permitAll()

                // Public pages — no login needed
                .requestMatchers(
                    "/",
                    "/home",
                    "/error",
                    "/access-denied"
                ).permitAll()

                // Team member pages — supervisor views these without login
                // /team/member1 → your page
                // /team/member2 → teammate's page etc.
                .requestMatchers("/team/**").permitAll()

                // Auth pages — login, register, logout
                .requestMatchers(
                    "/auth/login",
                    "/auth/do-login",  // our custom POST handler (not Spring Security's)
                    "/auth/register",
                    "/auth/do-register",
                    "/auth/logout"
                ).permitAll()

                // ── Pages that need login ────────────────────────────────

                // Your endpoint demo pages (group 3 — logged-in user)
                // These pages SHOW the output of your user APIs
                .requestMatchers(
                    "/user/profile",
                    "/user/dashboard",
                    "/user/purchases",
                    "/user/change-password"
                ).hasAnyRole("RegisteredUser", "StoreOwner", "Admin")

                // Store Owner pages (group 4)
                .requestMatchers(
                    "/store-owner/**"
                ).hasAnyRole("StoreOwner", "Admin")

                // Admin pages (group 5)
                .requestMatchers(
                    "/admin/**"
                ).hasRole("Admin")

                // Everything else needs at least a login
                .anyRequest().authenticated()
            )

            // ── Login Page ───────────────────────────────────────────────
            // Just tell Spring Security WHERE the login page is.
            // We are NOT using Spring Security's built-in form processing.
            // Our AuthViewController handles the actual POST to /auth/do-login.
            .formLogin(form -> form
                .loginPage("/auth/login")
                .permitAll()
            )

            // ── Logout ───────────────────────────────────────────────────
            .logout(logout -> logout
                .logoutUrl("/auth/logout")
                .logoutSuccessUrl("/home")
                .invalidateHttpSession(true)   // clears JWT from session
                .deleteCookies("JSESSIONID")
                .permitAll()
            )

            // ── Access Denied ────────────────────────────────────────────
            // If a RegisteredUser tries to open an Admin page → show this
            .exceptionHandling(ex -> ex
                .accessDeniedPage("/access-denied")
            );

        return http.build();
    }

    // ── Bean 1: UserDetailsService ───────────────────────────────────────
    //
    // WHY IS THIS HERE?
    // Spring Boot requires a UserDetailsService bean to exist.
    // Without it, the app crashes on startup.
    //
    // But we do NOT use it for actual login — our AuthViewController
    // calls the backend API to authenticate the user.
    //
    // This is a PLACEHOLDER that Spring Boot needs to be happy.
    // It has no real users in it. The real authentication is in
    // AuthViewController → calls backend → gets JWT.
    //
    @Bean
    public UserDetailsService userDetailsService() {
        // Empty in-memory manager — no users stored here.
        // Real users are authenticated by calling the backend API.
        return new InMemoryUserDetailsManager();
    }

    // ── Bean 2: PasswordEncoder ───────────────────────────────────────────
    //
    // WHY IS THIS HERE?
    // Spring Security requires a PasswordEncoder bean.
    // Without it, the app crashes on startup.
    //
    // Again, we don't actually use this for login (backend handles passwords).
    // This just satisfies Spring Boot's startup requirement.
    //
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // ── Bean 3: SecurityContextRepository ────────────────────────────────
    //
    // WHY IS THIS HERE?
    // This is HOW Spring Security remembers who is logged in
    // between requests (using the HTTP session).
    //
    // After our AuthViewController authenticates via the backend,
    // it uses this bean to SAVE the authentication into the session.
    // On every subsequent request, Spring Security READS from the
    // session to know who this person is and what role they have.
    //
    // Think of it as the "memory" of who is logged in.
    //
    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }

    // ── Bean 4: AuthenticationManager ────────────────────────────────────
    //
    // Required by AuthViewController to manually trigger the
    // authentication process after we get a successful JWT from backend.
    //
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}