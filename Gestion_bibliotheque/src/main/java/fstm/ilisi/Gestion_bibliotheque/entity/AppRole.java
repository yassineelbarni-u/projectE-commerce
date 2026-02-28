package fstm.ilisi.Gestion_bibliotheque.entity;

import lombok.Builder;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity

@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class AppRole {
    
    @Id
    private String roleName;

    
}
