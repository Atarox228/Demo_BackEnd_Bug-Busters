package ar.edu.unq.epersgeist.modelo;

import ar.edu.unq.epersgeist.modelo.enums.TipoEspiritu;
import ar.edu.unq.epersgeist.modelo.enums.TipoUbicacion;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@ToString @EqualsAndHashCode

@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
//@DiscriminatorColumn(name = "tipo_ubicacion", discriminatorType = DiscriminatorType.STRING)

@Entity
public abstract class Ubicacion implements Serializable, SQLObject{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, columnDefinition = "VARCHAR(255) CHECK (char_length(nombre) > 0)")
    private String nombre;
    @Min(0) @Max(100)
    private Integer flujoEnergia;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;
    @Column(nullable = false)
    private Boolean deleted = false;

    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
        updatedAt  = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt  = new Date();
    }

    public Ubicacion(@NonNull String nombre, @NonNull Integer flujoEnergia) {
        this.nombre = nombre;
        this.flujoEnergia = flujoEnergia;
    }

    public abstract Integer valorDeRecuperacionMedium();

    public abstract void moverAEspiritu(Espiritu espiritu);

    public abstract boolean permiteInvocarTipo(TipoEspiritu tipo);

    public abstract void aumentarConexionDe(Espiritu espiritu);


    public abstract TipoUbicacion getTipo();
}
