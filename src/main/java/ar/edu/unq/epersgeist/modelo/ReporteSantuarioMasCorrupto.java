package ar.edu.unq.epersgeist.modelo;

import ar.edu.unq.epersgeist.modelo.exception.NoEsSantuarioException;
import ar.edu.unq.epersgeist.modelo.exception.NoSePuedenConectarException;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Stream;

@Getter
@Setter
@NoArgsConstructor

@Entity
public class ReporteSantuarioMasCorrupto implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String santuario;
    @ManyToOne
    private Medium medium;
    private Integer cantDemoniosTotal;
    private Integer cantDemoniosLibres;

    public ReporteSantuarioMasCorrupto(String santuario, Medium medium,
                                        Integer cantDemoniosTotal, Integer cantDemoniosLibres) {
        this.santuario = santuario;
        this.medium = medium;
        this.cantDemoniosTotal = cantDemoniosTotal;
        this.cantDemoniosLibres = cantDemoniosLibres;
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


