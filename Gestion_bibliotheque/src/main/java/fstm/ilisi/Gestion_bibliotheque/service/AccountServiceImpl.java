package fstm.ilisi.Gestion_bibliotheque.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import fstm.ilisi.Gestion_bibliotheque.entity.AppRole;
import fstm.ilisi.Gestion_bibliotheque.entity.AppUser;
import fstm.ilisi.Gestion_bibliotheque.repository.AppRoleRepository;
import fstm.ilisi.Gestion_bibliotheque.repository.AppUserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@Transactional
@AllArgsConstructor
public class AccountServiceImpl implements AccountService {

    private  AppUserRepository appUserRepository;
    private AppRoleRepository appRoleRepository;
    private PasswordEncoder passwordEncoder;

    @Override
    public AppUser AddUser(String username, String password, String email, String ConfirmPassword) {
      AppUser appUser = appUserRepository.findByUsername(username);
      if (appUser != null) throw new RuntimeException("User already exists");
      if (!password.equals(ConfirmPassword)) throw new RuntimeException("Passwords  not match");
           appUser= AppUser.builder()
                 .userId(UUID.randomUUID().toString())
                 .username(username)
                 .password(passwordEncoder.encode(password))
                 .email(email)
                 .build();
        AppUser savedUser = appUserRepository.save(appUser);
        return savedUser;
        
    }

    @Override
    public AppRole AddRole(String roleName) {
        AppRole appRole = appRoleRepository.findByRoleName(roleName);
        if (appRole != null) throw new RuntimeException("Role already exist");
        appRole = AppRole.builder()
                .roleName(roleName)
                .build();
        AppRole savedRole = appRoleRepository.save(appRole);
        return savedRole;
    }

    @Override
    public void AddRoleToUser(String username, String roleName) {

        AppUser appUser = appUserRepository.findByUsername(username);
        if (appUser == null) throw new RuntimeException("User not found");
        AppRole appRole = appRoleRepository.findById(roleName).get();
        if (appRole == null) throw new RuntimeException("Role not found");
        appUser.getRoles().add(appRole);
        // methode transactionnelle pas besion de faire save(appUser)
        // appUserRepository.save(appUser);
    }

    @Override
    public void removeRoleFromUser(String username, String roleName) {
        AppUser appUser = appUserRepository.findByUsername(username);
        if (appUser == null) throw new RuntimeException("User not found");
        AppRole appRole = appRoleRepository.findById(roleName).get();
        if (appRole == null) throw new RuntimeException("Role not found");
        appUser.getRoles().remove(appRole);
        // appUserRepository.save(appUser);
    }

    @Override
    public AppUser loadUserByUsername(String username) {
        return appUserRepository.findByUsername(username);
    }
    
    
}
