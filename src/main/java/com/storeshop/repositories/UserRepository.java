package com.storeshop.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.storeshop.entities.User;


@Repository
public interface UserRepository extends JpaRepository<User, String> {
    User findByUsername(String username);
}
