package com.storeshop.unit.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.storeshop.controllers.UserController;
import com.storeshop.entities.Role;
import com.storeshop.entities.User;
import com.storeshop.repositories.UserRepository;
import com.storeshop.services.AccountService;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

/**
 * Tests unitaires pour {@link UserController}.
 * Valide la gestion administrative des utilisateurs : dashboard, listing, ajout, modification et suppression.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Tests du Controller UserController")
/**
 * Type : Test Unitaire
 */
@org.junit.jupiter.api.Tag("Unitaire")
class UserControllerTest {

  @Mock private AccountService accountService;

  @Mock private UserRepository appUserRepository;

  @Mock private Model model;

  @InjectMocks private UserController userController;

  private User user1;
  private User user2;

  /**
   * Initialisation des utilisateurs mockés.
   */
  @BeforeEach
  void setUp() {
    user1 =
        User.builder()
            .userId("uuid-1")
            .username("admin")
            .password("encodedPass")
            .email("admin@gmail.com")
            .role(Role.ADMIN)
            .build();

    user2 =
        User.builder()
            .userId("uuid-2")
            .username("user")
            .password("encodedPass")
            .email("user@gmail.com")
            .role(Role.USER)
            .build();
  }

  /**
   * Teste l'accès au tableau de bord.
   */
  @Test
  void testDashboard() {
    assertEquals("admin/dashboard", userController.dashboard());
  }

  /**
   * Teste le listing de tous les utilisateurs.
   */
  @Test
  void testListUsers() {
    List<User> users = Arrays.asList(user1, user2);
    when(appUserRepository.findAll()).thenReturn(users);

    String view = userController.listUsers(model);

    assertEquals("admin/listeUsers", view);
    verify(model).addAttribute("users", users);
  }

  /**
   * Teste l'affichage du formulaire de création.
   */
  @Test
  void testShowAddUserForm() {
    String view = userController.showAddUserForm(model);
    assertEquals("admin/ajouterUser", view);
    verify(model).addAttribute("roles", Role.values());
  }

  /**
   * Teste la création d'un utilisateur avec succès.
   */
  @Test
  void testAddUser_Success() {
    when(accountService.AddUser("newuser", "pass", "new@gmail.com", "pass")).thenReturn(user1);

    String redirect = userController.addUser("newuser", "pass", "pass", "new@gmail.com", "ADMIN");

    assertEquals("redirect:/admin/users?success=add", redirect);
    verify(accountService).AddUser("newuser", "pass", "new@gmail.com", "pass");
  }

  /**
   * Teste le cas d'erreur lors de l'ajout (ex: déjà existant).
   */
  @Test
  void testAddUser_Error() {
    when(accountService.AddUser("admin", "pass", "admin@gmail.com", "pass"))
        .thenThrow(new RuntimeException("User already exists"));

    String redirect = userController.addUser("admin", "pass", "pass", "admin@gmail.com", "ADMIN");

    assertTrue(redirect.startsWith("redirect:/admin/users/add?error="));
  }

  /**
   * Teste l'affichage du formulaire de modification d'un utilisateur existant.
   */
  @Test
  void testShowEditUserForm() {
    when(appUserRepository.findById("uuid-1")).thenReturn(Optional.of(user1));

    String view = userController.showEditUserForm("uuid-1", model);

    assertEquals("admin/editUser", view);
    verify(model).addAttribute("user", user1);
    verify(model).addAttribute("allRoles", Role.values());
  }

  /**
   * Teste la mise à jour des informations sans changer le mot de passe.
   */
  @Test
  void testEditUser_SuccessWithoutPassword() {
    when(appUserRepository.findById("uuid-1")).thenReturn(Optional.of(user1));
    when(appUserRepository.save(any(User.class))).thenReturn(user1);

    String redirect =
        userController.editUser("uuid-1", "adminUpdated", "newmail@gmail.com", null, "ADMIN");

    assertEquals("redirect:/admin/users?success=edit", redirect);
    assertEquals("adminUpdated", user1.getUsername());
    assertEquals("newmail@gmail.com", user1.getEmail());
  }

  /**
   * Teste la mise à jour avec mot de passe et rôle.
   */
  @Test
  void testEditUser_SuccessWithPasswordAndRole() {
    when(appUserRepository.findById("uuid-1")).thenReturn(Optional.of(user1));
    when(appUserRepository.save(any(User.class))).thenReturn(user1);

    String redirect =
        userController.editUser("uuid-1", "adminUpdated", "newmail@gmail.com", "newPass", "CLIENT");

    assertEquals("redirect:/admin/users?success=edit", redirect);
    assertEquals("newPass", user1.getPassword());
    assertEquals(Role.CLIENT, user1.getRole());
  }

  /**
   * Teste la redirection d'erreur quand l'utilisateur à éditer est introuvable.
   */
  @Test
  void testEditUser_NotFound() {
    when(appUserRepository.findById("missing")).thenReturn(Optional.empty());

    String redirect =
        userController.editUser("missing", "adminUpdated", "newmail@gmail.com", null, "ADMIN");

    assertTrue(redirect.startsWith("redirect:/admin/users?error="));
  }

  /**
   * Teste la redirection d'erreur quand le formulaire d'édition cible un utilisateur absent.
   */
  @Test
  void testShowEditUserForm_NotFound() {
    when(appUserRepository.findById("missing")).thenReturn(Optional.empty());

    RuntimeException exception =
        org.junit.jupiter.api.Assertions.assertThrows(
            RuntimeException.class, () -> userController.showEditUserForm("missing", model));

    assertEquals("Utilisateur non trouvé", exception.getMessage());
  }

  /**
   * Teste la suppression d'un utilisateur.
   */
  @Test
  void testDeleteUser_Success() {
    doNothing().when(appUserRepository).deleteById("uuid-1");
    String redirect = userController.deleteUser("uuid-1");
    assertEquals("redirect:/admin/users?success=delete", redirect);
    verify(appUserRepository).deleteById("uuid-1");
  }

  /**
   * Teste la redirection d'erreur quand la suppression échoue.
   */
  @Test
  void testDeleteUser_Error() {
    doThrow(new RuntimeException("constraint violation")).when(appUserRepository).deleteById("uuid-1");

    String redirect = userController.deleteUser("uuid-1");

    assertEquals("redirect:/admin/users?error=constraint violation", redirect);
  }
}
