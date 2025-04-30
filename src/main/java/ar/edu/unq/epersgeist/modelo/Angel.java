package ar.edu.unq.epersgeist.modelo;

import ar.edu.unq.epersgeist.modelo.exception.EspirituNoLibreException;
import lombok.*;

import jakarta.persistence.*;

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
    public void moverseACementerio(Ubicacion ubicacion) {
        super.moverseACementerio(ubicacion);
        reducirConexionYdesvincularSiEsNecesario(5);
    }

    @Override
    public void invocarseA(Ubicacion ubicacion) {
        super.invocarseA(ubicacion);
        ubicacion.invocarEspirituAngelical(this);
    }
}
