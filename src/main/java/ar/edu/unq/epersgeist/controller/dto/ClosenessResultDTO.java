package ar.edu.unq.epersgeist.controller.dto;

import ar.edu.unq.epersgeist.modelo.ClosenessResult;
import ar.edu.unq.epersgeist.modelo.UbicacionNeo4J;

public record ClosenessResultDTO(UbicacionNeo4J ubicacion, Double closeness) {

    public static ClosenessResultDTO desdeModelo(ClosenessResult closeness){
        return new ClosenessResultDTO(
                closeness.ubicacion(),
                closeness.closeness()
        );
    }
}


