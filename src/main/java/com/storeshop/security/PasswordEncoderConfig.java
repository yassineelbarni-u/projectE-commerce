package com.storeshop.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
/**
 * Provides the password hashing implementation used by authentication and account creation.
 */
public class PasswordEncoderConfig {

  /**
   * Creates a BCrypt encoder.
   *
   * @return password encoder bean registered in Spring context
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
