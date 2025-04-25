package ar.edu.unq.epersgeist.modelo;

import ar.edu.unq.epersgeist.modelo.exception.NoEsSantuarioException;
import ar.edu.unq.epersgeist.modelo.exception.NoSePuedenConectarException;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.*;

import java.util.List;
import java.util.stream.Stream;

@Getter
@Setter
@NoArgsConstructor
@ToString

@Entity
@DiscriminatorValue("SanturioMasCorrupto")
public final class ReporteSantuarioMasCorrupto extends Reporte {

    public ReporteSantuarioMasCorrupto(@NonNull Ubicacion ubicacion) {
        if (ubicacion.getNombre() != "Santuario") {
            throw new NoEsSantuarioException();
        }
        setUbicacion(ubicacion);
    }



//    public Integer obtenerCantDemonios(List<Espiritu> espiritus) {
//        Stream<Espiritu> espiritusDemoniacos = espiritus.stream().
//                                                filter(e -> e.getTipo() == TipoEspiritu.DEMONIACO);
//        return espiritusDemoniacos.toList().size();
//    }

//    public Integer obtenerDemoniosLibres(List<Espiritu> espiritus) {
//        Stream<Espiritu> espiritusDemoniacos = espiritus.stream().
//                filter(e -> e.getTipo() == TipoEspiritu.DEMONIACO && e.estaLibre());
//        return espiritusDemoniacos.toList().size();
//    }
}


