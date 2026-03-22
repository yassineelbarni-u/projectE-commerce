package com.storeshop.services.impl;

import com.storeshop.entities.Role;
import com.storeshop.entities.User;
import com.storeshop.repositories.UserRepository;
import com.storeshop.services.AccountService;
import jakarta.transaction.Transactional;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Transactional
@AllArgsConstructor
public class AccountServiceImpl implements AccountService {

  private final UserRepository UserRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  public User AddUser(String username, String password, String email, String ConfirmPassword) {
    // Check if the user already exists
    User existingUser = UserRepository.findByUsername(username);
    if (existingUser != null) throw new RuntimeException("User already exists");

    // Check if passwords match
    if (!password.equals(ConfirmPassword)) throw new RuntimeException("Passwords  not match");

    // Create and save the new user
    User newUser =
        User.builder()
            .userId(UUID.randomUUID().toString())
            .username(username)
            .password(passwordEncoder.encode(password))
            .email(email)
            .role(Role.CLIENT)
            .build();

    return UserRepository.save(newUser);
  }

  @Override
  // Utility method to create a user only if it does not exist
  public User ensureUserExists(String username, String password, String email) {
    User existingUser = UserRepository.findByUsername(username);
    if (existingUser == null) {
      existingUser =
          User.builder()
              .userId(UUID.randomUUID().toString())
              .username(username)
              .password(passwordEncoder.encode(password))
              .email(email)
              .role(Role.ADMIN)
              .build();
      existingUser = UserRepository.save(existingUser);
    }
    return existingUser;
  }

  @Override
  public User loadUserByUsername(String username) {
    return UserRepository.findByUsername(username);
  }
}
