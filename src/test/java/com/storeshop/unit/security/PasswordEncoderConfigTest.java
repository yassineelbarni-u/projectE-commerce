package com.storeshop.unit.security;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.storeshop.security.PasswordEncoderConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

@DisplayName("Tests de la configuration PasswordEncoder")
/**
 * Type : Test Unitaire
 */
@org.junit.jupiter.api.Tag("Unitaire")
class PasswordEncoderConfigTest {

  @Test
  @DisplayName("passwordEncoder fournit un BCrypt capable d'encoder et vérifier un mot de passe")
  void passwordEncoderUsesBcrypt() {
    PasswordEncoder encoder = new PasswordEncoderConfig().passwordEncoder();

    String rawPassword = "secret123";
    String encodedPassword = encoder.encode(rawPassword);

    assertNotEquals(rawPassword, encodedPassword);
    assertTrue(encoder.matches(rawPassword, encodedPassword));
  }
}
