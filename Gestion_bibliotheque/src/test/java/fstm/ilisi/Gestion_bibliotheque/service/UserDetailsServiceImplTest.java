package fstm.ilisi.Gestion_bibliotheque.service;

import fstm.ilisi.Gestion_bibliotheque.entity.AppRole;
import fstm.ilisi.Gestion_bibliotheque.entity.AppUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests du Service UserDetailsServiceImpl")
class UserDetailsServiceImplTest {

    @Mock
    private AccountService accountService;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    private AppUser testUser;

    @BeforeEach
    void setUp() {
        AppRole adminRole = new AppRole("ADMIN");
        AppRole userRole = new AppRole("USER");

        testUser = AppUser.builder()
                .userId("uuid-123")
                .username("testuser")
                .password("encodedPassword")
                .email("test@gmail.com")
                .roles(new ArrayList<>(Arrays.asList(adminRole, userRole)))
                .build();
    }

    @Test
    @DisplayName("loadUserByUsername - Retourne les details de l'utilisateur")
    void testLoadUserByUsername_Success() {
        when(accountService.loadUserByUsername("testuser")).thenReturn(testUser);

        UserDetails result = userDetailsService.loadUserByUsername("testuser");

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("encodedPassword", result.getPassword());
        // Spring Security ajoute le préfixe ROLE
        assertTrue(result.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
        assertTrue(result.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
    }

    @Test
    @DisplayName("loadUserByUsername - Lève une exception si l'utilisateur n'existe pas")
    void testLoadUserByUsername_NotFound() {
        when(accountService.loadUserByUsername("unknown")).thenReturn(null);

        assertThrows(UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("unknown"));
    }

    @Test
    @DisplayName("loadUserByUsername - Utilisateur avec un seul rôle")
    void testLoadUserByUsername_SingleRole() {
        AppUser singleRoleUser = AppUser.builder()
                .userId("uuid-456")
                .username("simpleuser")
                .password("pass")
                .email("simple@gmail.com")
                .roles(new ArrayList<>(Arrays.asList(new AppRole("USER"))))
                .build();

        when(accountService.loadUserByUsername("simpleuser")).thenReturn(singleRoleUser);

        UserDetails result = userDetailsService.loadUserByUsername("simpleuser");

        assertEquals(1, result.getAuthorities().size());
        assertTrue(result.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
    }
}
