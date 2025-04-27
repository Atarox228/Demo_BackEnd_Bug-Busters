package ar.edu.unq.epersgeist.modelo;

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
        setUbicacion(ubicacion);
        reducirConexionYdesvincularSiEsNecesario(5);
    }
}
