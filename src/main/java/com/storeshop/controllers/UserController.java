package com.storeshop.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.storeshop.entities.Role;
import com.storeshop.entities.User;
import com.storeshop.repositories.UserRepository;
import com.storeshop.services.AccountService;

import lombok.AllArgsConstructor;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@AllArgsConstructor
public class UserController {

  private final AccountService accountService;
  private final UserRepository UserRepository;
  private final PasswordEncoder passwordEncoder;

  @GetMapping("/dashboard")
  public String dashboard() {
    return "admin/dashboard";
  }

  @GetMapping("/users")
  public String listUsers(Model model) {
    model.addAttribute("users", UserRepository.findAll());
    return "admin/listeUsers";
  }

  @GetMapping("/users/add")
  public String showAddUserForm(Model model) {
    model.addAttribute("roles", Role.values());
    return "admin/ajouterUser";
  }

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

  @GetMapping("/users/edit")
  public String showEditUserForm(@RequestParam String userId, Model model) {
    User user =
        UserRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
    model.addAttribute("user", user);
    model.addAttribute("allRoles", Role.values());
    return "admin/editUser";
  }

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

      // Update the password if provided, encoding it before storage
      if (password != null && !password.isEmpty()) {
        user.setPassword(passwordEncoder.encode(password));
      }

      // Update the role
      if (role != null && !role.isEmpty()) {
        user.setRole(Role.valueOf(role));
      }

      UserRepository.save(user);

      return "redirect:/admin/users?success=edit";
    } catch (Exception e) {
      return "redirect:/admin/users?error=" + e.getMessage();
    }
  }

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
