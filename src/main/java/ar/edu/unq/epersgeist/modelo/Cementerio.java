package ar.edu.unq.epersgeist.modelo;

import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode

@Entity
public class Cementerio extends Ubicacion{

    public Cementerio(@NonNull String nombre, @NonNull Integer flujoEnergia) {
        super(nombre,flujoEnergia);
    }

    @Override
    public boolean permiteInvocarTipo(TipoEspiritu tipo){
        return tipo == TipoEspiritu.DEMONIACO;
    }

    @Override
    public boolean puedeRecuperarse(Espiritu espiritu){
        return espiritu.getTipo() == TipoEspiritu.DEMONIACO;
    }

    @Override
    public Integer valorDeRecuperacionMedium(){
         return this.getFlujoEnergia() / 2;
    }

}
