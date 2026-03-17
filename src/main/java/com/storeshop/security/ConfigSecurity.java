package com.storeshop.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.storeshop.services.impl.UserDetailsServiceImpl;

import lombok.AllArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@AllArgsConstructor
public class ConfigSecurity {

    private final UserDetailsServiceImpl userDetailsServiceImpl;

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return (request, response, authentication) -> {
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

            response.sendRedirect(isAdmin ? "/admin/dashboard" : "/");
        };
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity,
                                                   AuthenticationSuccessHandler authenticationSuccessHandler) throws Exception {
        
        return httpSecurity
                .formLogin(form -> form
                    .loginPage("/login")
                    .successHandler(authenticationSuccessHandler)
                    .permitAll()
                )
                .userDetailsService(userDetailsServiceImpl)
                .logout(logout -> logout
                    .logoutSuccessUrl("/login?logout")
                    .invalidateHttpSession(true)
                    .deleteCookies("JSESSIONID")
                    .permitAll()
                )
                .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/", "/home", "/produits", "/produits/**", "/register", "/login", "/css/**", "/js/**", "/images/**", "/uploads/**", "/webjars/**").permitAll()
                    .requestMatchers("/admin/**").hasRole("ADMIN")
                    .anyRequest().authenticated()
                )
                .build();
    }

}
