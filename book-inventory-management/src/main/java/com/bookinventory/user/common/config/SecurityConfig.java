package com.bookinventory.user.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth

            	    // 🔥 UI + Static + Dashboard (merged from both)
            	    .requestMatchers(
            	        "/",
            	        "/home",
            	        "/ui/**",
            	        "/reviewui/**",
            	        "/api-author-dashboard",
            	        "/api-author-result",
            	        "/css/**",
            	        "/js/**",
            	        "/images/**"
            	    ).permitAll()
                // ══════════════════════════════════════════════════
                // GUEST — fully public, no token needed
                // ══════════════════════════════════════════════════

                // Auth
            	.requestMatchers(HttpMethod.POST,
            	    "/api/v1/auth/register",
            	    "/api/v1/auth/login",
            	    "/api/v1/auth/validate-token")   // ← add this line
            	    .permitAll()

                // Roles (public read)
                .requestMatchers(HttpMethod.GET,
                    "/api/v1/roles",
                    "/api/v1/roles/**")
                    .permitAll()

                // Books — public read (teammate owns these)
                .requestMatchers(HttpMethod.GET,
                    "/api/v1/books",
                    "/api/v1/books/**")
                    .permitAll()

                // Categories — public read
                .requestMatchers(HttpMethod.GET,
                    "/api/v1/categories",
                    "/api/v1/categories/**")
                    .permitAll()

                // Publishers — public read
                .requestMatchers(HttpMethod.GET,
                    "/api/v1/publishers",
                    "/api/v1/publishers/**")
                    .permitAll()

                // Authors — public read
                .requestMatchers(HttpMethod.GET,
                    "/api/v1/authors",
                    "/api/v1/authors/**")
                    .permitAll()

                // States — public read
                .requestMatchers(HttpMethod.GET,
                    "/api/v1/states",
                    "/api/v1/states/**")
                    .permitAll()

                // Reviews — public read only
                // GET /api/v1/reviews/book/{isbn}
                // GET /api/v1/reviews/book/{isbn}/rating-summary
                // GET /api/v1/reviews/book/{isbn}/count
                // GET /api/v1/reviews/reviewer/{reviewerId}
                // GET /api/v1/reviews
                .requestMatchers(HttpMethod.GET,
                    "/api/v1/reviews",
                    "/api/v1/reviews/**")
                    .permitAll()

                // Reviewer — public read only
                .requestMatchers(HttpMethod.GET,
                    "/api/v1/reviewer",
                    "/api/v1/reviewer/**")
                    .permitAll()
                
                 // ══════════════════════════════════════════════════
                 // UI PAGES — public, no token needed
                 // ══════════════════════════════════════════════════
                .requestMatchers(HttpMethod.GET,
                        "/",
                        "/home",
                        "/ui/**",
                        "/css/**",
                        "/images/**")
                        .permitAll()

                // ══════════════════════════════════════════════════
                // REGISTERED USER — needs valid token
                // hasAnyRole means StoreOwner and Admin can also access
                // ══════════════════════════════════════════════════

                // Own profile
                .requestMatchers(HttpMethod.GET,
                    "/api/v1/user/profile")
                    .hasAnyRole("RegisteredUser", "StoreOwner", "Admin")

                .requestMatchers(HttpMethod.PATCH,
                    "/api/v1/user/profile",
                    "/api/v1/user/change-password")
                    .hasAnyRole("RegisteredUser", "StoreOwner", "Admin")
                
                 // PUT for user profile (add this — currently only PATCH is covered)
                 .requestMatchers(HttpMethod.PUT,
                    "/api/v1/user/profile")
                    .hasAnyRole("RegisteredUser", "StoreOwner", "Admin")

                // Own purchases
                .requestMatchers(HttpMethod.GET,
                    "/api/v1/user/purchases",
                    "/api/v1/user/purchases/**")
                    .hasAnyRole("RegisteredUser", "StoreOwner", "Admin")

                .requestMatchers(HttpMethod.POST,
                    "/api/v1/user/purchases")
                    .hasAnyRole("RegisteredUser", "StoreOwner", "Admin")

                // Shopping cart — teammate owns controller, you own security
                // GET  /api/v1/user/cart/{userId}
                // GET  /api/v1/user/cart/options/{isbn}
                // GET  /api/v1/user/cart/view/{userId}
                // POST /api/v1/user/cart/add
                // POST /api/v1/user/cart/checkout
                // DELETE /api/v1/user/cart/remove
                .requestMatchers(HttpMethod.GET,
                    "/api/v1/user/cart/**")
                    .hasAnyRole("RegisteredUser", "StoreOwner", "Admin")

                .requestMatchers(HttpMethod.POST,
                    "/api/v1/user/cart/add",
                    "/api/v1/user/cart/checkout")
                    .hasAnyRole("RegisteredUser", "StoreOwner", "Admin")

                .requestMatchers(HttpMethod.DELETE,
                    "/api/v1/user/cart/remove")
                    .hasAnyRole("RegisteredUser", "StoreOwner", "Admin")

                // Reviews written by user
                // POST /api/v1/user/reviews  OR  POST /api/v1/reviews
                // PUT  /api/v1/user/reviews/{isbn}/{reviewerId}
                // DELETE /api/v1/user/reviews/{isbn}/{reviewerId}
                .requestMatchers(HttpMethod.POST,
                    "/api/v1/user/reviews",
                    "/api/v1/reviews")
                    .hasAnyRole("RegisteredUser", "StoreOwner", "Admin")

                .requestMatchers(HttpMethod.PUT,
                    "/api/v1/user/reviews/**",
                    "/api/v1/reviews")
                    .hasAnyRole("RegisteredUser", "StoreOwner", "Admin")

                .requestMatchers(HttpMethod.DELETE,
                    "/api/v1/user/reviews/**",
                    "/api/v1/reviews/**")
                    .hasAnyRole("RegisteredUser", "StoreOwner", "Admin")

                // ══════════════════════════════════════════════════
                // STORE OWNER — GET + POST + PUT + DELETE
                // ══════════════════════════════════════════════════

                // Purchases view
                .requestMatchers(HttpMethod.GET,
                    "/api/v1/store-owner/**")
                    .hasAnyRole("StoreOwner", "Admin")

                // Inventory management
                // POST /api/v1/store-owner/inventory
                // POST /api/v1/store-owner/books
                // POST /api/v1/store-owner/categories
                // POST /api/v1/store-owner/publishers
                // POST /api/v1/store-owner/authors
                // POST /api/v1/store-owner/book-authors
                // POST /api/v1/store-owner/reviewers
                .requestMatchers(HttpMethod.POST,
                    "/api/v1/store-owner/**")
                    .hasAnyRole("StoreOwner", "Admin")

                // PUT /api/v1/store-owner/inventory/{id}
                // PUT /api/v1/store-owner/inventory/purchase/{id}
                // PUT /api/v1/store-owner/books/{isbn}
                // PUT /api/v1/store-owner/categories/{id}
                // PUT /api/v1/store-owner/publishers/{id}
                // PUT /api/v1/store-owner/authors/{id}
                // PUT /api/v1/store-owner/book-authors/{isbn}/{authorId}
                // PUT /api/v1/store-owner/reviewers/{id}
                .requestMatchers(HttpMethod.PUT,
                    "/api/v1/store-owner/**")
                    .hasAnyRole("StoreOwner", "Admin")

                // DELETE /api/v1/store-owner/inventory/{id}
                // DELETE /api/v1/store-owner/books/{isbn}
                // DELETE /api/v1/store-owner/categories/{id}
                // DELETE /api/v1/store-owner/publishers/{id}
                // DELETE /api/v1/store-owner/authors/{id}
                // DELETE /api/v1/store-owner/book-authors/{isbn}/{authorId}
                // DELETE /api/v1/store-owner/reviewers/{id}
                .requestMatchers(HttpMethod.DELETE,
                    "/api/v1/store-owner/**")
                    .hasAnyRole("StoreOwner", "Admin")

                // ══════════════════════════════════════════════════
                // ADMIN ONLY
                // ══════════════════════════════════════════════════

                .requestMatchers(HttpMethod.GET,
                    "/api/v1/admin/**")
                    .hasRole("Admin")

                .requestMatchers(HttpMethod.PATCH,
                    "/api/v1/admin/**")
                    .hasRole("Admin")

                .requestMatchers(HttpMethod.PUT,
                    "/api/v1/admin/**")
                    .hasRole("Admin")

                .requestMatchers(HttpMethod.DELETE,
                    "/api/v1/admin/**")
                    .hasRole("Admin")
                // ══════════════════════════════════════════════════
                // CATCH-ALL — anything else needs a valid token
                // ══════════════════════════════════════════════════
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthFilter,
                UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}