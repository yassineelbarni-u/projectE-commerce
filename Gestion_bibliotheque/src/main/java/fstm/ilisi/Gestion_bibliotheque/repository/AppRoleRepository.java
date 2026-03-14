package fstm.ilisi.Gestion_bibliotheque.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import fstm.ilisi.Gestion_bibliotheque.entity.AppRole;

public interface AppRoleRepository extends JpaRepository<AppRole, String> {
    AppRole  findByRoleName(String roleName);

}
