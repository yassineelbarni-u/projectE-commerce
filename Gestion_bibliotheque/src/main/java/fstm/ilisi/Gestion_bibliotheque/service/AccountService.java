package fstm.ilisi.Gestion_bibliotheque.service;

import fstm.ilisi.Gestion_bibliotheque.entity.AppRole;
import fstm.ilisi.Gestion_bibliotheque.entity.AppUser;

public interface AccountService {
    AppUser AddUser(String username, String password, String email, String ConfirmPassword);
    AppRole AddRole (String roleName);
    void AddRoleToUser(String username, String roleName);
    void removeRoleFromUser(String username, String roleName);
    AppUser loadUserByUsername(String username);

    
}
