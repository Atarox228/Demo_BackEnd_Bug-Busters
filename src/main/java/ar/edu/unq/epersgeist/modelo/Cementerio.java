package ar.edu.unq.epersgeist.modelo;

import ar.edu.unq.epersgeist.modelo.exception.InvocacionFallidaPorUbicacionException;
import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@ToString

@Entity
@DiscriminatorValue("CEMENTERIO")
public class Cementerio extends Ubicacion{

    public Cementerio(@NonNull String nombre, @NonNull Integer flujoEnergia) {
        super(nombre,flujoEnergia);
    }

    @Override
    public boolean puedeRecuperarse(Espiritu espiritu){
        return espiritu.puedeRecuperarseEnCementerio();
    }

    @Override
    public Integer valorDeRecuperacionMedium(){
         return this.getFlujoEnergia() / 2;
    }

    @Override
    public void moverAEspiritu(Espiritu espiritu){
        espiritu.setUbicacion(this);
        espiritu.moverCementerio();
    }

    public boolean permiteInvocarTipo(TipoEspiritu tipo){
        return tipo == TipoEspiritu.DEMONIACO;
    }

    @Override
    public void aumentarConexionDe(Espiritu espiritu) {
        espiritu.aumentarConexionDeCementerio(this.getFlujoEnergia());
    }


}
