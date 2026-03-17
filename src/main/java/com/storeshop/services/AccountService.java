package com.storeshop.services;

import com.storeshop.entities.User;

public interface AccountService {
    User AddUser(String username, String password, String email, String ConfirmPassword);
    User loadUserByUsername(String username);    
    User ensureUserExists(String username, String password, String email);
}
