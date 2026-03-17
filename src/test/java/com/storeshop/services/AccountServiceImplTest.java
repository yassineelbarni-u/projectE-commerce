package com.storeshop.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.storeshop.entities.Role;
import com.storeshop.entities.User;
import com.storeshop.repositories.UserRepository;
import com.storeshop.services.impl.AccountServiceImpl;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests du Service AccountServiceImpl")
class AccountServiceImplTest {

    @Mock
    private UserRepository appUserRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AccountServiceImpl accountService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .userId("uuid-123")
                .username("testuser")
                .password("encodedPassword")
                .email("test@gmail.com")
                .role(Role.USER)
                .build();
    }

    @Test
    void testAddUser_Success() {
        when(appUserRepository.findByUsername("newuser")).thenReturn(null);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(appUserRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        User result = accountService.AddUser("newuser", "password123", "new@gmail.com", "password123");

        assertNotNull(result);
        assertEquals("newuser", result.getUsername());
        assertEquals("new@gmail.com", result.getEmail());
        assertEquals("encodedPassword", result.getPassword());
        verify(appUserRepository).save(any(User.class));
    }

    @Test
    void testAddUser_UserAlreadyExists() {
        when(appUserRepository.findByUsername("testuser")).thenReturn(testUser);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> accountService.AddUser("testuser", "pass", "email@test.com", "pass"));

        assertEquals("User already exists", exception.getMessage());
        verify(appUserRepository, never()).save(any());
    }

    @Test
    void testAddUser_PasswordsDoNotMatch() {
        when(appUserRepository.findByUsername("newuser")).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> accountService.AddUser("newuser", "pass1", "email@test.com", "pass2"));

        assertEquals("Passwords  not match", exception.getMessage());
        verify(appUserRepository, never()).save(any());
    }

    @Test
    void testEnsureUserExists_CreatesUser() {
        when(appUserRepository.findByUsername("newuser")).thenReturn(null);
        when(passwordEncoder.encode("pass")).thenReturn("encodedPass");
        when(appUserRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        User result = accountService.ensureUserExists("newuser", "pass", "new@gmail.com");

        assertNotNull(result);
        assertEquals("newuser", result.getUsername());
        verify(appUserRepository).save(any(User.class));
    }

    @Test
    void testEnsureUserExists_ReturnsExisting() {
        when(appUserRepository.findByUsername("testuser")).thenReturn(testUser);

        User result = accountService.ensureUserExists("testuser", "pass", "test@gmail.com");

        assertEquals(testUser, result);
        verify(appUserRepository, never()).save(any());
    }

    @Test
    void testLoadUserByUsername_Found() {
        when(appUserRepository.findByUsername("testuser")).thenReturn(testUser);

        User result = accountService.loadUserByUsername("testuser");

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
    }

    @Test
    void testLoadUserByUsername_NotFound() {
        when(appUserRepository.findByUsername("unknown")).thenReturn(null);

        User result = accountService.loadUserByUsername("unknown");

        assertNull(result);
    }
}
