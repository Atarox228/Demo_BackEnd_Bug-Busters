package ar.edu.unq.epersgeist.modelo;

import ar.edu.unq.epersgeist.modelo.enums.TipoEspiritu;
import lombok.*;

import jakarta.persistence.*;

import java.util.List;

@Getter @Setter @NoArgsConstructor @ToString

@Entity
@DiscriminatorValue("ANGELICAL")
public final class Angel extends Espiritu {

    public Angel(@NonNull String nombre) {
        this.setNivelConexion(0);
        this.setNombre(nombre);
    }

    @Override
    public TipoEspiritu getTipo() {
        return TipoEspiritu.ANGELICAL;
    }

    @Override
    public List<Espiritu> ataque(List<Espiritu> demoniacosRestantes) {
        List<Espiritu> demoniacosDefensores = demoniacosRestantes;
        Espiritu defensor = demoniacosDefensores.getFirst();
        if (this.getProbAtaque() > defensor.getProbDefensa()){
            defensor.reducirConexionYdesvincularSiEsNecesario(this.getNivelConexion() / 2);
            if (defensor.getNivelConexion() == 0) {
                demoniacosDefensores.remove(defensor);
               }
        } else {
            this.reducirConexionYdesvincularSiEsNecesario(5);
        }

        return demoniacosDefensores;
    }

    @Override
    public void moverCementerio() {
        this.reducirConexionYdesvincularSiEsNecesario(5);
    }

    @Override
    public void aumentarConexionDeSantuario(Integer flujoEnergia) {
        this.setNivelConexion(Math.min(this.getNivelConexion()+flujoEnergia,100));
    }

    @Override
    public void aumentarConexionDeCementerio(Integer flujoEnergia) {
    }
}
