package ar.edu.unq.epersgeist.controller.dto;

import ar.edu.unq.epersgeist.modelo.SnapShot;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.Map;

public record SnapshotDTO(
        @NotNull LocalDate date,
        @NotNull Map<String, Object>sql,
        @NotNull Map<String, Object> mongo,
        @NotNull Map<String, Object> neo4j) {


    public static SnapshotDTO desdeModelo(SnapShot snapshot){
        return new SnapshotDTO(
                snapshot.getDate(),
                snapshot.getSql(),
                snapshot.getMongo(),
                snapshot.getNeo4j()
        );
    }
}
