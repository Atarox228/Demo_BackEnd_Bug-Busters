package ar.edu.unq.epersgeist.modelo;

import ar.edu.unq.epersgeist.modelo.exception.EspirituNoLibreException;
import lombok.*;

import jakarta.persistence.*;

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
}