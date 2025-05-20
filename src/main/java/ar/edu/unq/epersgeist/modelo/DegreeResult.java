package ar.edu.unq.epersgeist.modelo;

import ar.edu.unq.epersgeist.modelo.enums.DegreeType;

public record DegreeResult(UbicacionNeo4J node, Double centrality, DegreeType typeResult ) {
}
