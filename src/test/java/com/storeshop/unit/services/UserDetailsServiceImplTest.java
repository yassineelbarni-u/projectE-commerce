package com.storeshop.unit.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.storeshop.services.AccountService;
import com.storeshop.entities.Role;
import com.storeshop.entities.User;
import com.storeshop.services.impl.UserDetailsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * Tests unitaires pour {@link UserDetailsServiceImpl}.
 * Vérifie l'intégration avec Spring Security pour le chargement des utilisateurs par leur nom.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Tests du Service UserDetailsServiceImpl")
/**
 * Type : Test Unitaire
 */
@org.junit.jupiter.api.Tag("Unitaire")
class UserDetailsServiceImplTest {

  @Mock private AccountService accountService;

  @InjectMocks private UserDetailsServiceImpl userDetailsService;

  private User testUser;

  /**
   * Initialisation d'un utilisateur de test avec le rôle ADMIN.
   */
  @BeforeEach
  void setUp() {
    testUser =
        User.builder()
            .userId("uuid-123")
            .username("testuser")
            .password("encodedPassword")
            .email("test@gmail.com")
            .role(Role.ADMIN)
            .build();
  }

  /**
   * Vérifie le chargement réussi d'un utilisateur existant.
   */
  @Test
  @DisplayName("loadUserByUsername - Chargement réussi")
  void testLoadUserByUsername_Success() {
    when(accountService.loadUserByUsername("testuser")).thenReturn(testUser);

    UserDetails result = userDetailsService.loadUserByUsername("testuser");

    assertNotNull(result);
    assertEquals("testuser", result.getUsername());
    assertEquals("encodedPassword", result.getPassword());
    assertTrue(
        result.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
  }

  /**
   * Vérifie qu'une exception est levée si l'utilisateur n'est pas trouvé.
   */
  @Test
  @DisplayName("loadUserByUsername - Utilisateur non trouvé")
  void testLoadUserByUsername_NotFound() {
    when(accountService.loadUserByUsername("unknown")).thenReturn(null);

    assertThrows(
        UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername("unknown"));
  }

  /**
   * Vérifie le bon mapping du rôle USER vers l'autorité Spring Security.
   */
  @Test
  @DisplayName("loadUserByUsername - Vérification mapping rôle USER")
  void testLoadUserByUsername_SingleRole() {
    User singleRoleUser =
        User.builder()
            .userId("uuid-456")
            .username("simpleuser")
            .password("pass")
            .email("simple@gmail.com")
            .role(Role.USER)
            .build();

    when(accountService.loadUserByUsername("simpleuser")).thenReturn(singleRoleUser);

    UserDetails result = userDetailsService.loadUserByUsername("simpleuser");

    assertEquals(1, result.getAuthorities().size());
    assertTrue(
        result.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
  }
}
