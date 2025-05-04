package ar.edu.unq.epersgeist.controller.dto;

import ar.edu.unq.epersgeist.modelo.*;
import jakarta.validation.constraints.NotBlank;

import java.util.Objects;

public record EspirituDTO(
        Long id,
        @NotBlank String nombre,
        TipoEspiritu tipoDeEspiritu,
        Integer nivelDeConexion,
        UbicacionDTO ubicacion,
        MediumDTO medium) {

    public static EspirituDTO desdeModelo(Espiritu espiritu){
        return new EspirituDTO(
                espiritu.getId(),
                espiritu.getNombre(),
                espiritu.getTipo(),
                espiritu.getNivelConexion(),
                espiritu.getUbicacion() != null ? UbicacionDTO.desdeModelo(espiritu.getUbicacion()) : null,
                espiritu.getMedium() != null ? MediumDTO.desdeModeloNoRecursivo(espiritu.getMedium()) : null

        );
    }

    public Espiritu aModelo(Ubicacion ubicacion){
       Espiritu espiritu = this.aModelo();
       espiritu.setUbicacion(ubicacion);
       return espiritu;
    }
    public Espiritu aModelo(Medium medium){
        Espiritu espiritu = this.aModelo();
        espiritu.setMedium(medium);
        return espiritu;
    }
    public Espiritu aModelo(Ubicacion ubicacion, Medium medium){
        Espiritu espiritu = this.aModelo();
        espiritu.setUbicacion(ubicacion);
        espiritu.setMedium(medium);
        return espiritu;
    }
    public Espiritu aModelo(){
        return switch (this.tipoDeEspiritu) {
            case ANGELICAL -> new Angel(nombre);
            case DEMONIACO -> new Demonio(nombre);
        };
    }

    public static EspirituDTO desdeModeloNoRecursivo(Espiritu espiritu) {
        return new EspirituDTO(
                espiritu.getId(),
                espiritu.getNombre(),
                espiritu.getTipo(),
                espiritu.getNivelConexion(),
                null,
                null
        );
    }
}
