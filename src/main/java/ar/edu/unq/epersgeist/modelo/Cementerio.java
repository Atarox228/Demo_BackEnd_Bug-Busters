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
        return espiritu.getTipo() == TipoEspiritu.DEMONIACO;
    }

    @Override
    public Integer valorDeRecuperacionMedium(){
         return this.getFlujoEnergia() / 2;
    }

//    @Override
//    public void moverMedium(Medium medium) {
//        medium.moverACementerio(this);
//    }

    @Override
    public void moverAEspiritu(Espiritu espiritu){
        espiritu.setUbicacion(this);
        if(espiritu.getTipo() == TipoEspiritu.ANGELICAL){
            espiritu.reducirConexionYdesvincularSiEsNecesario(5);
        }
    }

//    @Override
//    public void invocarEspirituDemoniaco(Demonio demonio) {
//        demonio.moverseACementerio(this);
//    }

//    @Override
//    public void invocarEspirituAngelical(Angel angel) {
//        throw new InvocacionFallidaPorUbicacionException(angel, this);
//    }
}
