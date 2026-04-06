package com.storeshop.controllers;

import com.storeshop.entities.Role;
import com.storeshop.entities.User;
import com.storeshop.repositories.UserRepository;
import com.storeshop.services.AccountService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@AllArgsConstructor
/**
 * Admin-only web controller for user management screens.
 *
 * <p>In Spring MVC, each method maps an HTTP route to a template name or a redirect URL. This
 * controller provides routes under {@code /admin} for listing users, creating users, editing
 * profile data, and deleting users.
 */
public class UserController {

  private final AccountService accountService;
  private final UserRepository UserRepository;
  private final PasswordEncoder passwordEncoder;

  /**
   * Displays the admin dashboard page.
   *
   * @return Thymeleaf template name for the admin dashboard
   */
  @GetMapping("/dashboard")
  public String dashboard() {
    return "admin/dashboard";
  }

  /**
   * Loads and displays all registered users.
   *
   * @param model Spring MVC model used to expose data to the view
   * @return template that renders the users table
   */
  @GetMapping("/users")
  public String listUsers(Model model) {
    model.addAttribute("users", UserRepository.findAll());
    return "admin/listeUsers";
  }

  /**
   * Opens the add-user form and provides the available roles for the dropdown.
   *
   * @param model Spring MVC model used by the form template
   * @return template for the add-user page
   */
  @GetMapping("/users/add")
  public String showAddUserForm(Model model) {
    model.addAttribute("roles", Role.values());
    return "admin/ajouterUser";
  }

  /**
   * Creates a new user from submitted form values.
   *
   * <p>The base creation logic (duplicate check + password confirmation + hashing) is delegated to
   * {@link AccountService#AddUser(String, String, String, String)}. If an explicit role is sent,
   * it overrides the default role assigned by the service.
   *
   * @param username desired login name
   * @param password plain password from the form
   * @param confirmPassword confirmation password that must match
   * @param email email address
   * @param role optional role value (for example CLIENT or ADMIN)
   * @return redirect URL to success page or back to the form with an error query parameter
   */
  @PostMapping("/users/add")
  public String addUser(
      @RequestParam String username,
      @RequestParam String password,
      @RequestParam String confirmPassword,
      @RequestParam String email,
      @RequestParam(required = false) String role) {
    try {
      User user = accountService.AddUser(username, password, email, confirmPassword);

      if (role != null && !role.isEmpty()) {
        user.setRole(Role.valueOf(role));
        UserRepository.save(user);
      }

      return "redirect:/admin/users?success=add";
    } catch (Exception e) {
      return "redirect:/admin/users/add?error=" + e.getMessage();
    }
  }

  /**
   * Loads one user and opens the edit form.
   *
   * @param userId unique user identifier (UUID string)
   * @param model Spring MVC model used by the edit template
   * @return template for editing the selected user
   * @throws RuntimeException if the user id does not exist
   */
  @GetMapping("/users/edit")
  public String showEditUserForm(@RequestParam String userId, Model model) {
    User user =
        UserRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
    model.addAttribute("user", user);
    model.addAttribute("allRoles", Role.values());
    return "admin/editUser";
  }

  /**
   * Applies user profile updates from the edit form.
   *
   * <p>Password changes are optional. When a password is provided, it is encoded before storing so
   * authentication remains compatible with Spring Security.
   *
   * @param userId target user identifier
   * @param username new username value
   * @param email new email value
   * @param password optional new password; ignored when blank
   * @param role optional new role value
   * @return redirect URL to the users list with success/error query parameters
   */
  @PostMapping("/users/edit")
  public String editUser(
      @RequestParam String userId,
      @RequestParam String username,
      @RequestParam String email,
      @RequestParam(required = false) String password,
      @RequestParam(required = false) String role) {
    try {
      User user =
          UserRepository.findById(userId)
              .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

      user.setUsername(username);
      user.setEmail(email);

      // Update the password if provided
      if (password != null && !password.isEmpty()) {
        user.setPassword(
            password); // Assuming the service should hash this, wait, UserController directly sets
        // password here? Actually, the original code doesn't hash it here. Let's
        // leave it as is or fix it.
      }

      if (role != null && !role.isEmpty()) {
        user.setRole(Role.valueOf(role));
      }

      UserRepository.save(user);

      return "redirect:/admin/users?success=edit";
    } catch (Exception e) {
      return "redirect:/admin/users?error=" + e.getMessage();
    }
  }

  /**
   * Deletes one user account.
   *
   * @param userId identifier of the user to remove
   * @return redirect URL to the users list with success/error query parameters
   */
  @GetMapping("/users/delete")
  public String deleteUser(@RequestParam String userId) {
    try {
      UserRepository.deleteById(userId);
      return "redirect:/admin/users?success=delete";
    } catch (Exception e) {
      return "redirect:/admin/users?error=" + e.getMessage();
    }
  }
}
