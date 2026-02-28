package fstm.ilisi.Gestion_bibliotheque.repository;

import fstm.ilisi.Gestion_bibliotheque.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface AppUserRepository extends JpaRepository<AppUser, String> {
    AppUser findByUsername(String username);
}
