package com.storeshop.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests du Controller UserController")
class UserControllerTest {

  @Mock private AccountService accountService;

  @Mock private UserRepository appUserRepository;

  @Mock private PasswordEncoder passwordEncoder;

  @Mock private Model model;

  @InjectMocks private UserController userController;

  private MockMvc mockMvc;
  private User user1;
  private User user2;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(userController).build();

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

  @Test
  void testDashboard() {
    assertEquals("admin/dashboard", userController.dashboard());
  }

  @Test
  void testListUsers() {
    List<User> users = Arrays.asList(user1, user2);
    when(appUserRepository.findAll()).thenReturn(users);

    String view = userController.listUsers(model);

    assertEquals("admin/listeUsers", view);
    verify(model).addAttribute("users", users);
  }

  @Test
  void testShowAddUserForm() {
    String view = userController.showAddUserForm(model);
    assertEquals("admin/ajouterUser", view);
    verify(model).addAttribute("roles", Role.values());
  }

  @Test
  void testAddUser_Success() {
    when(accountService.AddUser("newuser", "pass", "new@gmail.com", "pass")).thenReturn(user1);

    String redirect = userController.addUser("newuser", "pass", "pass", "new@gmail.com", "ADMIN");

    assertEquals("redirect:/admin/users?success=add", redirect);
    verify(accountService).AddUser("newuser", "pass", "new@gmail.com", "pass");
  }

  @Test
  void testAddUser_Error() {
    when(accountService.AddUser("admin", "pass", "admin@gmail.com", "pass"))
        .thenThrow(new RuntimeException("User already exists"));

    String redirect = userController.addUser("admin", "pass", "pass", "admin@gmail.com", "ADMIN");

    assertTrue(redirect.startsWith("redirect:/admin/users/add?error="));
  }

  @Test
  void testShowEditUserForm() {
    when(appUserRepository.findById("uuid-1")).thenReturn(Optional.of(user1));

    String view = userController.showEditUserForm("uuid-1", model);

    assertEquals("admin/editUser", view);
    verify(model).addAttribute("user", user1);
    verify(model).addAttribute("allRoles", Role.values());
  }

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

  @Test
  void testEditUser_SuccessWithPassword() {
    when(appUserRepository.findById("uuid-1")).thenReturn(Optional.of(user1));
    when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");
    when(appUserRepository.save(any(User.class))).thenReturn(user1);

    String redirect =
        userController.editUser("uuid-1", "adminUpdated", "newmail@gmail.com", "newPassword", "ADMIN");

    assertEquals("redirect:/admin/users?success=edit", redirect);
    assertEquals("encodedNewPassword", user1.getPassword());
    verify(passwordEncoder).encode("newPassword");
  }

  @Test
  void testDeleteUser_Success() {
    doNothing().when(appUserRepository).deleteById("uuid-1");
    String redirect = userController.deleteUser("uuid-1");
    assertEquals("redirect:/admin/users?success=delete", redirect);
    verify(appUserRepository).deleteById("uuid-1");
  }
}
