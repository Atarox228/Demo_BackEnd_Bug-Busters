package ar.edu.unq.epersgeist.modelo;

import ar.edu.unq.epersgeist.modelo.enums.TipoEspiritu;
import lombok.*;

import jakarta.persistence.*;

import java.util.List;

@Getter @Setter @NoArgsConstructor @ToString

@Entity
@DiscriminatorValue("DEMONIACO")
public final class Demonio extends Espiritu {

    public Demonio(@NonNull String nombre) {
        this.setNivelConexion(0);
        this.setNombre(nombre);
    }

    @Override
    public TipoEspiritu getTipo() {
        return TipoEspiritu.DEMONIACO;
    }

    @Override
    public List<Espiritu> ataque(List<Espiritu> demoniacosRestantes) {
        return demoniacosRestantes;
    }

    @Override
    public void moverSantuario() {
        this.reducirConexionYdesvincularSiEsNecesario(10);
    }

    @Override
    public boolean puedeRecuperarseEnCementerio() {
        return true;
    }

    @Override
    public boolean puedeRecuperarseEnSantuario() {
        return false;
    }

    @Override
    public void aumentarConexionDeSantuario(Integer flujoEnergia) {}

    @Override
    public void aumentarConexionDeCementerio(Integer flujoEnergia) {
        this.setNivelConexion(Math.min(this.getNivelConexion()+flujoEnergia,100));
    }
}