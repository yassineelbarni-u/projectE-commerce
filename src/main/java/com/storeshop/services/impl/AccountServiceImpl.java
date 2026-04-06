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

/**
 * Default {@link com.storeshop.services.AccountService}: password encoding and role assignment on
 * create; transactional reads/writes for consistency.
 */
@Service
@Transactional
@AllArgsConstructor
public class AccountServiceImpl implements AccountService {

  private final UserRepository UserRepository;
  private final PasswordEncoder passwordEncoder;

  /**
   * Creates a new client account after validation.
   *
   * @param username login name
   * @param password plain password
   * @param email email address
   * @param ConfirmPassword confirmation password
   * @return saved user with generated UUID and CLIENT role
   * @throws RuntimeException when username already exists or passwords mismatch
   */
  @Override
  public User AddUser(String username, String password, String email, String ConfirmPassword) {
    User existingUser = UserRepository.findByUsername(username);
    if (existingUser != null) throw new RuntimeException("User already exists");

    if (!password.equals(ConfirmPassword)) throw new RuntimeException("Passwords  not match");

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

  /**
   * Ensures one admin account exists for bootstrap use.
   *
   * @param username admin username
   * @param password plain admin password
   * @param email admin email
   * @return existing admin or newly created admin user
   */
  @Override
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

  /**
   * Loads one user by username.
   *
   * @param username login name
   * @return user or null
   */
  @Override
  public User loadUserByUsername(String username) {
    return UserRepository.findByUsername(username);
  }
}
