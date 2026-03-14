package fstm.ilisi.Gestion_bibliotheque.controller;

import fstm.ilisi.Gestion_bibliotheque.entity.AppRole;
import fstm.ilisi.Gestion_bibliotheque.entity.AppUser;
import fstm.ilisi.Gestion_bibliotheque.repository.AppRoleRepository;
import fstm.ilisi.Gestion_bibliotheque.repository.AppUserRepository;
import fstm.ilisi.Gestion_bibliotheque.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests du Controller UserController")
class UserControllerTest {

    @Mock
    private AccountService accountService;

    @Mock
    private AppUserRepository appUserRepository;

    @Mock
    private AppRoleRepository appRoleRepository;

    @Mock
    private Model model;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;
    private AppUser user1;
    private AppUser user2;
    private AppRole adminRole;
    private AppRole userRole;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();

        adminRole = new AppRole("ADMIN");
        userRole = new AppRole("USER");

        user1 = AppUser.builder()
                .userId("uuid-1")
                .username("admin")
                .password("encodedPass")
                .email("admin@gmail.com")
                .roles(new ArrayList<>(Arrays.asList(adminRole)))
                .build();

        user2 = AppUser.builder()
                .userId("uuid-2")
                .username("user")
                .password("encodedPass")
                .email("user@gmail.com")
                .roles(new ArrayList<>(Arrays.asList(userRole)))
                .build();
    }


    @Test
    @DisplayName("Test dashboard - Retourne la vue dashboard")
    void testDashboard() {
        String view = userController.dashboard();

        assertEquals("admin/dashboard", view);
    }

    @Test
    @DisplayName("Test dashboard avec MockMvc")
    void testDashboardWithMockMvc() throws Exception {
        mockMvc.perform(get("/admin/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/dashboard"));
    }


    @Test
    @DisplayName("Test listUsers - Affiche la liste des utilisateurs")
    void testListUsers() {
        List<AppUser> users = Arrays.asList(user1, user2);
        when(appUserRepository.findAll()).thenReturn(users);

        String view = userController.listUsers(model);

        assertEquals("admin/listeUsers", view);
        verify(model).addAttribute("users", users);
    }

    @Test
    @DisplayName("Test listUsers avec MockMvc")
    void testListUsersWithMockMvc() throws Exception {
        when(appUserRepository.findAll()).thenReturn(Arrays.asList(user1, user2));

        mockMvc.perform(get("/admin/users"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/listeUsers"));
    }


    @Test
    @DisplayName("Test showAddUserForm - Affiche le formulaire d'ajout")
    void testShowAddUserForm() {
        List<AppRole> roles = Arrays.asList(adminRole, userRole);
        when(appRoleRepository.findAll()).thenReturn(roles);

        String view = userController.showAddUserForm(model);

        assertEquals("admin/ajouterUser", view);
        verify(model).addAttribute("roles", roles);
    }


    @Test
    @DisplayName("Test addUser - Ajout réussi sans rôles")
    void testAddUser_SuccessWithoutRoles() {
        when(accountService.AddUser("newuser", "pass", "new@gmail.com", "pass"))
                .thenReturn(user1);

        String redirect = userController.addUser("newuser", "pass", "pass", "new@gmail.com", null);

        assertEquals("redirect:/admin/users?success=add", redirect);
        verify(accountService).AddUser("newuser", "pass", "new@gmail.com", "pass");
        verify(accountService, never()).AddRoleToUser(anyString(), anyString());
    }

    @Test
    @DisplayName("Test addUser - Ajout réussi avec rôles")
    void testAddUser_SuccessWithRoles() {
        when(accountService.AddUser("newuser", "pass", "new@gmail.com", "pass"))
                .thenReturn(user1);

        String redirect = userController.addUser("newuser", "pass", "pass", "new@gmail.com",
                new String[]{"ADMIN", "USER"});

        assertEquals("redirect:/admin/users?success=add", redirect);
        verify(accountService).AddRoleToUser("newuser", "ADMIN");
        verify(accountService).AddRoleToUser("newuser", "USER");
    }

    @Test
    @DisplayName("Test addUser - Échec avec erreur")
    void testAddUser_Error() {
        when(accountService.AddUser("admin", "pass", "admin@gmail.com", "pass"))
                .thenThrow(new RuntimeException("User already exists"));

        String redirect = userController.addUser("admin", "pass", "pass", "admin@gmail.com", null);

        assertTrue(redirect.startsWith("redirect:/admin/users/add?error="));
    }


    @Test
    @DisplayName("Test showEditUserForm - Affiche le formulaire d'édition")
    void testShowEditUserForm() {
        when(appUserRepository.findById("uuid-1")).thenReturn(Optional.of(user1));
        when(appRoleRepository.findAll()).thenReturn(Arrays.asList(adminRole, userRole));

        String view = userController.showEditUserForm("uuid-1", model);

        assertEquals("admin/editUser", view);
        verify(model).addAttribute("user", user1);
        verify(model).addAttribute("allRoles", Arrays.asList(adminRole, userRole));
    }

    @Test
    @DisplayName("Test showEditUserForm - Utilisateur non trouvé")
    void testShowEditUserForm_UserNotFound() {
        when(appUserRepository.findById("unknown")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> userController.showEditUserForm("unknown", model));
    }


    @Test
    @DisplayName("Test editUser - Modification réussie sans mot de passe")
    void testEditUser_SuccessWithoutPassword() {
        when(appUserRepository.findById("uuid-1")).thenReturn(Optional.of(user1));
        when(appUserRepository.save(any(AppUser.class))).thenReturn(user1);
        when(appRoleRepository.findById("ADMIN")).thenReturn(Optional.of(adminRole));

        String redirect = userController.editUser("uuid-1", "adminUpdated", "newmail@gmail.com",
                null, new String[]{"ADMIN"});

        assertEquals("redirect:/admin/users?success=edit", redirect);
        assertEquals("adminUpdated", user1.getUsername());
        assertEquals("newmail@gmail.com", user1.getEmail());
    }

    @Test
    @DisplayName("Test editUser - Modification réussie avec mot de passe")
    void testEditUser_SuccessWithPassword() {
        when(appUserRepository.findById("uuid-1")).thenReturn(Optional.of(user1));
        when(appUserRepository.save(any(AppUser.class))).thenReturn(user1);

        String redirect = userController.editUser("uuid-1", "admin", "admin@gmail.com",
                "newPassword", null);

        assertEquals("redirect:/admin/users?success=edit", redirect);
        assertEquals("newPassword", user1.getPassword());
    }

    @Test
    @DisplayName("Test editUser - Échec utilisateur non trouvé")
    void testEditUser_UserNotFound() {
        when(appUserRepository.findById("unknown")).thenReturn(Optional.empty());

        String redirect = userController.editUser("unknown", "test", "test@gmail.com",
                null, null);

        assertTrue(redirect.startsWith("redirect:/admin/users?error="));
    }


    @Test
    @DisplayName("Test deleteUser - Suppression réussie")
    void testDeleteUser_Success() {
        doNothing().when(appUserRepository).deleteById("uuid-1");

        String redirect = userController.deleteUser("uuid-1");

        assertEquals("redirect:/admin/users?success=delete", redirect);
        verify(appUserRepository).deleteById("uuid-1");
    }

    @Test
    @DisplayName("Test deleteUser - Échec avec erreur")
    void testDeleteUser_Error() {
        doThrow(new RuntimeException("Erreur")).when(appUserRepository).deleteById("unknown");

        String redirect = userController.deleteUser("unknown");

        assertTrue(redirect.startsWith("redirect:/admin/users?error="));
    }

    @Test
    @DisplayName("Test deleteUser avec MockMvc")
    void testDeleteUserWithMockMvc() throws Exception {
        doNothing().when(appUserRepository).deleteById("uuid-1");

        mockMvc.perform(get("/admin/users/delete").param("userId", "uuid-1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/users?success=delete"));
    }
}
