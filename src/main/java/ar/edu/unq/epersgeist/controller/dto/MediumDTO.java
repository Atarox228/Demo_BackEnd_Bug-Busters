package ar.edu.unq.epersgeist.controller.dto;

import ar.edu.unq.epersgeist.modelo.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public record MediumDTO(
        Long id,
        @NotBlank String nombre,
        @Min(0) Integer mana,
        @Min(1) Integer manaMaximo,
        UbicacionDTO ubicacion,
        Set<EspirituDTO> espiritus) {


    public static MediumDTO desdeModelo(Medium medium) {
        return new MediumDTO(
                medium.getId(),
                medium.getNombre(),
                medium.getMana(),
                medium.getManaMax(),
                medium.getUbicacion() != null ? UbicacionDTO.desdeModelo(medium.getUbicacion()) : null,
                medium.getEspiritus().stream()
                        .map(EspirituDTO::desdeModeloNoRecursivo)
                        .collect(Collectors.toCollection(HashSet::new))
        );
    }

    public static MediumDTO desdeModeloNoRecursivo(Medium medium) {
        return new MediumDTO(
                medium.getId(),
                medium.getNombre(),
                medium.getMana(),
                medium.getManaMax(),
                medium.getUbicacion() != null ? UbicacionDTO.desdeModelo(medium.getUbicacion()) : null,
                null
        );
    }

    public Medium aModelo(Ubicacion ubicacion){
            Medium medium = this.aModelo();
            medium.setUbicacion(ubicacion);
            return medium;
    }

    public Medium aModelo(){
        Medium medium = new Medium(this.nombre, this.manaMaximo, this.mana);
        medium.setId(this.id);
        medium.setEspiritus(this.espiritus != null ?
                this.espiritus.stream()
                        .map(EspirituDTO -> EspirituDTO.aModelo(medium))
                        .collect(Collectors.toCollection(HashSet::new)) :
                new HashSet<>()
        );
        return medium;
    }
}
