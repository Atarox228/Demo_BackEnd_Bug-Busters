package ar.edu.unq.epersgeist.modelo;

import lombok.NonNull;

public class Santuario extends Ubicacion{

    public Santuario (@NonNull String nombre, @NonNull Integer flujoEnergia) {
        super(nombre,flujoEnergia);
    }

    @Override
    public boolean permiteInvocarTipo(TipoEspiritu tipo){
        return tipo == TipoEspiritu.ANGELICAL;
    }

    @Override
    public boolean puedeRecuperarse(Espiritu espiritu){
        return espiritu.getTipo() == TipoEspiritu.ANGELICAL;
    }

    @Override
    public Integer valorDeRecuperacion(){
        return (int) Math.floor(this.getFlujoEnergia() * 1.5) ;
    }
}
