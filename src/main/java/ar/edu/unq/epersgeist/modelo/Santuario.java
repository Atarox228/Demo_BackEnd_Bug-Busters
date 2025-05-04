package ar.edu.unq.epersgeist.modelo;

import ar.edu.unq.epersgeist.controller.dto.TipoUbicacion;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@ToString

@Entity
@DiscriminatorValue("SANTUARIO")
public class Santuario extends Ubicacion{

    public Santuario (@NonNull String nombre, @NonNull Integer flujoEnergia) {
        super(nombre,flujoEnergia);
    }

    @Override
    public boolean puedeRecuperarse(Espiritu espiritu){
        return espiritu.puedeRecuperarseEnSantuario();
    }

    @Override
    public Integer valorDeRecuperacionMedium(){
        return (int) Math.floor(this.getFlujoEnergia() * 1.5) ;
    }

    @Override
    public void moverAEspiritu(Espiritu espiritu){
        espiritu.setUbicacion(this);
        espiritu.moverSantuario();
    }

    public boolean permiteInvocarTipo(TipoEspiritu tipo){
        return tipo == TipoEspiritu.ANGELICAL;
    }

    @Override
    public void aumentarConexionDe(Espiritu espiritu) {
        espiritu.aumentarConexionDeSantuario(this.getFlujoEnergia());
    }

    @Override
    public TipoUbicacion getTipo() {
        return TipoUbicacion.valueOf("SANTUARIO");
    }

}
