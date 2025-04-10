package ar.edu.unq.epersgeist.modelo;

import lombok.*;

import jakarta.persistence.*;

@Getter @Setter @NoArgsConstructor @ToString

@Entity
@DiscriminatorValue("DEMONIACO")
public final class Demonio extends Espiritu {

    public Demonio(@NonNull Integer nivelDeConexion, @NonNull String nombre) {
        this.setNivelConexion(nivelDeConexion);
        this.setNombre(nombre);
    }

    @Override
    public TipoEspiritu getTipo() {
        return TipoEspiritu.DEMONIACO;
    }
}