package com.storeshop.security;

import com.storeshop.services.impl.UserDetailsServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@AllArgsConstructor
/**
 * Main Spring Security configuration.
 *
 * <p>This class defines:
 *
 * <p>1) which URLs are public or protected,
 *
 * <p>2) how login success redirects are decided,
 *
 * <p>3) which security headers are added to responses.
 */
public class ConfigSecurity {

  private final UserDetailsServiceImpl userDetailsServiceImpl;

  /**
   * Builds a post-login redirect strategy.
   *
   * @return handler that sends admins to {@code /admin/dashboard} and other users to {@code /}
   */
  @Bean
  public AuthenticationSuccessHandler authenticationSuccessHandler() {
    return (request, response, authentication) -> {
      boolean isAdmin =
          authentication.getAuthorities().stream()
              .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

      response.sendRedirect(isAdmin ? "/admin/dashboard" : "/");
    };
  }

  @Bean
  public SecurityFilterChain securityFilterChain(
      HttpSecurity httpSecurity, AuthenticationSuccessHandler authenticationSuccessHandler)
      throws Exception {

    // The chain below configures both browser protections and route-level authorization.

    return httpSecurity
      .headers(
        headers ->
          // Browser-level hardening headers (CSP, referrer policy, frame denial, HSTS).
          headers
            .contentSecurityPolicy(
              csp ->
                csp.policyDirectives(
                  "default-src 'self'; " +
                  "img-src 'self' data: images.unsplash.com picsum.photos fastly.picsum.photos; " +
                  "style-src 'self' 'unsafe-inline' fonts.googleapis.com; " +
                  "font-src 'self' fonts.gstatic.com; " +
                  "script-src 'self' 'unsafe-inline'; " +
                  "object-src 'none'; " +
                  "base-uri 'self'; " +
                  "frame-ancestors 'none'"))
            .referrerPolicy(
              referrer ->
                referrer.policy(
                  ReferrerPolicyHeaderWriter.ReferrerPolicy.NO_REFERRER))
            .frameOptions(frame -> frame.deny())
            .httpStrictTransportSecurity(
              hsts -> hsts.includeSubDomains(true).maxAgeInSeconds(31536000)))
        .formLogin(
            form ->
              // Use our custom login page and role-aware success redirect.
                form.loginPage("/login").successHandler(authenticationSuccessHandler).permitAll())
        .userDetailsService(userDetailsServiceImpl)
        .logout(
            logout ->
                logout
                    .logoutSuccessUrl("/login?logout")
                    .invalidateHttpSession(true)
                    .deleteCookies("JSESSIONID")
                    .permitAll())
        .authorizeHttpRequests(
            auth ->
             // Public storefront resources are open; admin area requires ADMIN role.
               auth.requestMatchers(
                        "/",
                        "/home",
                        "/produits",
                        "/produits/**",
                        "/register",
                        "/login",
                        "/css/**",
                        "/js/**",
                        "/images/**",
                        "/uploads/**",
                        "/webjars/**")
                    .permitAll()
                    .requestMatchers("/admin/**")
                    .hasRole("ADMIN")
                    .anyRequest()
                    .authenticated())
        .build();
  }
}
