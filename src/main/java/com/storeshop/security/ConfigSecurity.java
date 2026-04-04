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
public class ConfigSecurity {

  private final UserDetailsServiceImpl userDetailsServiceImpl;

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

    return httpSecurity
      .headers(
        headers ->
          headers
            .contentSecurityPolicy(
              csp ->
                csp.policyDirectives(
                  "default-src 'self'; img-src 'self' data:; style-src 'self' 'unsafe-inline'; script-src 'self' 'unsafe-inline'; object-src 'none'; base-uri 'self'; frame-ancestors 'none'"))
            .referrerPolicy(
              referrer ->
                referrer.policy(
                  ReferrerPolicyHeaderWriter.ReferrerPolicy.NO_REFERRER))
            .frameOptions(frame -> frame.deny())
            .httpStrictTransportSecurity(
              hsts -> hsts.includeSubDomains(true).maxAgeInSeconds(31536000)))
        .formLogin(
            form ->
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
