package ar.edu.unq.epersgeist.modelo;

import lombok.*;

import jakarta.persistence.*;

@Getter @Setter @NoArgsConstructor @ToString

@Entity
@DiscriminatorValue("ANGELICAL")
public final class Angel extends Espiritu {

    public Angel(@NonNull Integer nivelDeConexion, @NonNull String nombre) {
        this.setNivelConexion(nivelDeConexion);
        this.setNombre(nombre);
    }

    @Override
    public TipoEspiritu getTipo() {
        return TipoEspiritu.ANGELICAL;
    }
}
