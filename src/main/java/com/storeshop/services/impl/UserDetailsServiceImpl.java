package com.storeshop.services.impl;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.storeshop.entities.User;
import com.storeshop.services.AccountService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final AccountService accountService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User appUser = accountService.loadUserByUsername(username);
        if(appUser == null) throw new UsernameNotFoundException("User not found");
        
        String role = appUser.getRole() != null ? appUser.getRole().name() : "USER";
        
        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(appUser.getUsername())
                .password(appUser.getPassword())
                .roles(role).build();

        return userDetails;

    }
}
