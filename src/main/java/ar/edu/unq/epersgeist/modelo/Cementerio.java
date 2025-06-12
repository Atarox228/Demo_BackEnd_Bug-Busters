package ar.edu.unq.epersgeist.modelo;

import ar.edu.unq.epersgeist.modelo.enums.*;
import jakarta.persistence.*;
import lombok.*;

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

    @Override
    public TipoUbicacion getTipo() {
        return TipoUbicacion.valueOf("CEMENTERIO");
    }


}
