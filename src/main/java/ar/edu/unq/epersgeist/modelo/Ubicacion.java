package ar.edu.unq.epersgeist.modelo;

import ar.edu.unq.epersgeist.modelo.exception.EntidadConUbicacionRegistradaException;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;


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

    @OneToMany(mappedBy = "ubicacion", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<Espiritu> espiritus = new HashSet<>();

    @OneToMany(mappedBy = "ubicacion", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<Medium> mediums = new HashSet<>();

    public void agregarEspiritu(Espiritu espiritu) {
        if(espiritu.getUbicacion() == null){
            espiritus.add(espiritu);
            espiritu.setUbicacion(this);
        } else {
            throw new EntidadConUbicacionRegistradaException(espiritu, this);
        }

    }

    public void agregarMedium(Medium medium) {
        if (medium.getUbicacion() == null) {
            mediums.add(medium);
            medium.setUbicacion(this);
        }
        else{
            throw new EntidadConUbicacionRegistradaException(medium, this);
        }

    }
}
