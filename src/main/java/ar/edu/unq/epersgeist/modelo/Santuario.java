package ar.edu.unq.epersgeist.modelo;

import ar.edu.unq.epersgeist.modelo.exception.EspirituNoLibreException;
import ar.edu.unq.epersgeist.modelo.exception.InvocacionFallidaPorUbicacionException;
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
        return espiritu.getTipo() == TipoEspiritu.ANGELICAL;
    }

    @Override
    public Integer valorDeRecuperacionMedium(){
        return (int) Math.floor(this.getFlujoEnergia() * 1.5) ;
    }

//    @Override
//    public void moverMedium(Medium medium) {
//        medium.moverASantuario(this);
//    }

    @Override
    public void moverAEspiritu(Espiritu espiritu){
        espiritu.setUbicacion(this);
        if(espiritu.getTipo() == TipoEspiritu.DEMONIACO){
            espiritu.reducirConexionYdesvincularSiEsNecesario(10);
        }
    }

//    @Override
//    public void invocarEspirituDemoniaco(Demonio demonio) {
//        throw new InvocacionFallidaPorUbicacionException(demonio, this);
//    }

//    @Override
//    public void invocarEspirituAngelical(Angel angel) {
//        angel.moverseASantuario(this);
//    }
}
