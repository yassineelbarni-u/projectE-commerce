package fstm.ilisi.Gestion_bibliotheque.service;

import fstm.ilisi.Gestion_bibliotheque.entity.AppRole;
import fstm.ilisi.Gestion_bibliotheque.entity.AppUser;
import fstm.ilisi.Gestion_bibliotheque.repository.AppRoleRepository;
import fstm.ilisi.Gestion_bibliotheque.repository.AppUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests du Service AccountServiceImpl")
class AccountServiceImplTest {

    @Mock
    private AppUserRepository appUserRepository;

    @Mock
    private AppRoleRepository appRoleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AccountServiceImpl accountService;

    private AppUser testUser;
    private AppRole adminRole;
    private AppRole userRole;

    @BeforeEach
    void setUp() {
        adminRole = new AppRole("ADMIN");
        userRole = new AppRole("USER");

        testUser = AppUser.builder()
                .userId("uuid-123")
                .username("testuser")
                .password("encodedPassword")
                .email("test@gmail.com")
                .roles(new ArrayList<>())
                .build();
    }


    @Test
    @DisplayName("AddUser - Création réussie d'un utilisateur")
    void testAddUser_Success() {
        when(appUserRepository.findByUsername("newuser")).thenReturn(null);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(appUserRepository.save(any(AppUser.class))).thenAnswer(i -> i.getArgument(0));

        AppUser result = accountService.AddUser("newuser", "password123", "new@gmail.com", "password123");

        assertNotNull(result);
        assertEquals("newuser", result.getUsername());
        assertEquals("new@gmail.com", result.getEmail());
        assertEquals("encodedPassword", result.getPassword());
        verify(appUserRepository).save(any(AppUser.class));
    }

    @Test
    @DisplayName("AddUser - Échec si l'utilisateur existe déjà")
    void testAddUser_UserAlreadyExists() {
        when(appUserRepository.findByUsername("testuser")).thenReturn(testUser);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> accountService.AddUser("testuser", "pass", "email@test.com", "pass"));

        assertEquals("User already exists", exception.getMessage());
        verify(appUserRepository, never()).save(any());
    }

    @Test
    @DisplayName("AddUser - Échec si les mots de passe ne correspondent pas")
    void testAddUser_PasswordsDoNotMatch() {
        when(appUserRepository.findByUsername("newuser")).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> accountService.AddUser("newuser", "pass1", "email@test.com", "pass2"));

        assertEquals("Passwords  not match", exception.getMessage());
        verify(appUserRepository, never()).save(any());
    }


    @Test
    @DisplayName("AddRole - Création réussie d'un rôle")
    void testAddRole_Success() {
        when(appRoleRepository.findByRoleName("MODERATOR")).thenReturn(null);
        when(appRoleRepository.save(any(AppRole.class))).thenAnswer(i -> i.getArgument(0));

        AppRole result = accountService.AddRole("MODERATOR");

        assertNotNull(result);
        assertEquals("MODERATOR", result.getRoleName());
        verify(appRoleRepository).save(any(AppRole.class));
    }

    @Test
    @DisplayName("AddRole - Échec si le rôle existe déjà")
    void testAddRole_RoleAlreadyExists() {
        when(appRoleRepository.findByRoleName("ADMIN")).thenReturn(adminRole);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> accountService.AddRole("ADMIN"));

        assertEquals("Role already exist", exception.getMessage());
        verify(appRoleRepository, never()).save(any());
    }


    @Test
    @DisplayName("ensureRoleExists - Crée le rôle s'il n'existe pas")
    void testEnsureRoleExists_CreatesRole() {
        when(appRoleRepository.findByRoleName("MODERATOR")).thenReturn(null);
        when(appRoleRepository.save(any(AppRole.class))).thenAnswer(i -> i.getArgument(0));

        AppRole result = accountService.ensureRoleExists("MODERATOR");

        assertNotNull(result);
        assertEquals("MODERATOR", result.getRoleName());
        verify(appRoleRepository).save(any(AppRole.class));
    }

    @Test
    @DisplayName("ensureRoleExists - Retourne le rôle existant")
    void testEnsureRoleExists_ReturnsExisting() {
        when(appRoleRepository.findByRoleName("ADMIN")).thenReturn(adminRole);

        AppRole result = accountService.ensureRoleExists("ADMIN");

        assertEquals(adminRole, result);
        verify(appRoleRepository, never()).save(any());
    }


    @Test
    @DisplayName("ensureUserExists - Crée l'utilisateur s'il n'existe pas")
    void testEnsureUserExists_CreatesUser() {
        when(appUserRepository.findByUsername("newuser")).thenReturn(null);
        when(passwordEncoder.encode("pass")).thenReturn("encodedPass");
        when(appUserRepository.save(any(AppUser.class))).thenAnswer(i -> i.getArgument(0));

        AppUser result = accountService.ensureUserExists("newuser", "pass", "new@gmail.com");

        assertNotNull(result);
        assertEquals("newuser", result.getUsername());
        verify(appUserRepository).save(any(AppUser.class));
    }

    @Test
    @DisplayName("ensureUserExists - Retourne l'utilisateur existant")
    void testEnsureUserExists_ReturnsExisting() {
        when(appUserRepository.findByUsername("testuser")).thenReturn(testUser);

        AppUser result = accountService.ensureUserExists("testuser", "pass", "test@gmail.com");

        assertEquals(testUser, result);
        verify(appUserRepository, never()).save(any());
    }


    @Test
    @DisplayName("AddRoleToUser - Ajout réussi d'un rôle à un utilisateur")
    void testAddRoleToUser_Success() {
        when(appUserRepository.findByUsername("testuser")).thenReturn(testUser);
        when(appRoleRepository.findById("ADMIN")).thenReturn(Optional.of(adminRole));

        accountService.AddRoleToUser("testuser", "ADMIN");

        assertTrue(testUser.getRoles().contains(adminRole));
    }

    @Test
    @DisplayName("AddRoleToUser - Ne duplique pas un rôle déjà assigné")
    void testAddRoleToUser_NoDuplicate() {
        testUser.getRoles().add(adminRole);
        when(appUserRepository.findByUsername("testuser")).thenReturn(testUser);
        when(appRoleRepository.findById("ADMIN")).thenReturn(Optional.of(adminRole));

        accountService.AddRoleToUser("testuser", "ADMIN");

        assertEquals(1, testUser.getRoles().stream().filter(r -> r.getRoleName().equals("ADMIN")).count());
    }

    @Test
    @DisplayName("AddRoleToUser - Échec si l'utilisateur n'existe pas")
    void testAddRoleToUser_UserNotFound() {
        when(appUserRepository.findByUsername("unknown")).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> accountService.AddRoleToUser("unknown", "ADMIN"));

        assertEquals("User not found", exception.getMessage());
    }


    @Test
    @DisplayName("removeRoleFromUser - Suppression réussie d'un rôle")
    void testRemoveRoleFromUser_Success() {
        testUser.getRoles().add(adminRole);
        when(appUserRepository.findByUsername("testuser")).thenReturn(testUser);
        when(appRoleRepository.findById("ADMIN")).thenReturn(Optional.of(adminRole));

        accountService.removeRoleFromUser("testuser", "ADMIN");

        assertFalse(testUser.getRoles().contains(adminRole));
    }

    @Test
    @DisplayName("removeRoleFromUser - Échec si l'utilisateur n'existe pas")
    void testRemoveRoleFromUser_UserNotFound() {
        when(appUserRepository.findByUsername("unknown")).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> accountService.removeRoleFromUser("unknown", "ADMIN"));

        assertEquals("User not found", exception.getMessage());
    }


    @Test
    @DisplayName("loadUserByUsername - Retourne l'utilisateur trouvé")
    void testLoadUserByUsername_Found() {
        when(appUserRepository.findByUsername("testuser")).thenReturn(testUser);

        AppUser result = accountService.loadUserByUsername("testuser");

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
    }

    @Test
    @DisplayName("loadUserByUsername - Retourne null si non trouvé")
    void testLoadUserByUsername_NotFound() {
        when(appUserRepository.findByUsername("unknown")).thenReturn(null);

        AppUser result = accountService.loadUserByUsername("unknown");

        assertNull(result);
    }
}
