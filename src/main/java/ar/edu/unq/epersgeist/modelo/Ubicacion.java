package ar.edu.unq.epersgeist.modelo;

import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@ToString @EqualsAndHashCode

@Entity
public class Ubicacion implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true,nullable = false, columnDefinition = "VARCHAR(255) CHECK (char_length(nombre) > 0)")
    private String nombre;
    public Ubicacion(@NonNull String nombre) {
        this.nombre = nombre;
    }

}
