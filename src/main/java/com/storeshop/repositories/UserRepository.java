package com.storeshop.repositories;

import com.storeshop.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
/**
 * Database access for {@link User} entities.
 */
public interface UserRepository extends JpaRepository<User, String> {

  /**
   * Finds one user by login name.
   *
   * @param username login name
   * @return matching user or null when not found
   */
  User findByUsername(String username);
}
