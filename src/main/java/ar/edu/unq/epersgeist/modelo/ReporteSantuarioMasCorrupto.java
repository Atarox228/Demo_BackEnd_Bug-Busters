package ar.edu.unq.epersgeist.modelo;

import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;


@Getter
@Setter
@ToString
@NoArgsConstructor
@EqualsAndHashCode

public class ReporteSantuarioMasCorrupto implements Serializable {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
}


