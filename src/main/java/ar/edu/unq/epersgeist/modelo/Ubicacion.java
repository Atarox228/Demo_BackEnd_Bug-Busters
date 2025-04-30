package ar.edu.unq.epersgeist.modelo;

import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@ToString @EqualsAndHashCode

@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
//@DiscriminatorColumn(name = "tipo_ubicacion", discriminatorType = DiscriminatorType.STRING)

@Entity
public abstract class Ubicacion implements Serializable{
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

    public abstract boolean puedeRecuperarse(Espiritu espiritu);

    public abstract Integer valorDeRecuperacionMedium();

    public Integer valorDeRecuperacionEspiritu(){
        return this.flujoEnergia;
    }

    public abstract void moverAEspiritu(Espiritu espiritu);

    public abstract boolean permiteInvocarTipo(TipoEspiritu tipo);

}
