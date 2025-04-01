package ar.edu.unq.epersgeist.modelo;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
@Getter @Setter @ToString @EqualsAndHashCode @NoArgsConstructor

@Entity
public class Medium implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private Integer manaMax;
    private Integer mana;
    //private Set<Espiritu> espiritus = new HashSet<>();

    //@ManyToOne
    //private Ubicacion ubicacion;

    public Medium(String nombre, Integer manaMax, Integer mana) {
        this.nombre = nombre;
        this.manaMax = manaMax;
        this.mana = mana;
    }

    //public void conectarseAEspiritu(Espiritu espiritu) {
    //    espiritu.aumentarConexion(this);
    //    espiritus.add(espiritu);
    //}

    //public boolean tieneConNombre_(String nombre) {
    //    return espiritus.stream().anyMatch(esp -> esp.getNombre().equals(nombre));
    //}


}