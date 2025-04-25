package ar.edu.unq.epersgeist.modelo;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor

@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipoReporte", discriminatorType = DiscriminatorType.STRING)

@Entity
public abstract class Reporte implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private Ubicacion ubicacion;
    @ManyToOne
    private Medium medium;
    private Integer cantEspiritusTotal;

    public Reporte(@NonNull Ubicacion ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String nombreUbicacion() {
        return this.ubicacion.getNombre();
    }

}
