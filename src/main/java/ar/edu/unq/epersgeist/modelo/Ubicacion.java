package ar.edu.unq.epersgeist.modelo;

import jakarta.persistence.*;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@ToString @EqualsAndHashCode

@Entity
public class Ubicacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String nombre;
    public Ubicacion(@NonNull String nombre) {
        this.nombre = nombre;
    }

    public void agregarEspiritu(Espiritu espiritu) {}

    public void agregarMedium(Espiritu espiritu) {}
}
