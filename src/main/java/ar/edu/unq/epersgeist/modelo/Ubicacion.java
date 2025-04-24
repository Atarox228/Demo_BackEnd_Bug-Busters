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
    private Integer flujoEnergia;
    public Ubicacion(@NonNull String nombre, @NonNull Integer flujoEnergia) {
        this.nombre = nombre;
        this.flujoEnergia = flujoEnergia;
    }

    public boolean permiteInvocarTipo(TipoEspiritu tipo){
        return true;
    }

    public boolean puedeRecuperarse(Espiritu espiritu){
        return true;
    }

    public Integer valorDeRecuperacion(){
        return 0;
    }
}
