package com.storeshop.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.storeshop.entities.Role;
import com.storeshop.entities.User;
import com.storeshop.services.impl.UserDetailsServiceImpl;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests du Service UserDetailsServiceImpl")
class UserDetailsServiceImplTest {

    @Mock
    private AccountService accountService;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .userId("uuid-123")
                .username("testuser")
                .password("encodedPassword")
                .email("test@gmail.com")
                .role(Role.ADMIN)
                .build();
    }

    @Test
    void testLoadUserByUsername_Success() {
        when(accountService.loadUserByUsername("testuser")).thenReturn(testUser);

        UserDetails result = userDetailsService.loadUserByUsername("testuser");

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("encodedPassword", result.getPassword());
        assertTrue(result.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    void testLoadUserByUsername_NotFound() {
        when(accountService.loadUserByUsername("unknown")).thenReturn(null);

        assertThrows(UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("unknown"));
    }

    @Test
    void testLoadUserByUsername_SingleRole() {
        User singleRoleUser = User.builder()
                .userId("uuid-456")
                .username("simpleuser")
                .password("pass")
                .email("simple@gmail.com")
                .role(Role.USER)
                .build();

        when(accountService.loadUserByUsername("simpleuser")).thenReturn(singleRoleUser);

        UserDetails result = userDetailsService.loadUserByUsername("simpleuser");

        assertEquals(1, result.getAuthorities().size());
        assertTrue(result.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
    }
}
