package ar.edu.unq.epersgeist.controller.dto;

import ar.edu.unq.epersgeist.modelo.*;
import ar.edu.unq.epersgeist.modelo.enums.TipoUbicacion;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UbicacionDTO(
        Long id,
        @NotBlank String nombre,
        @NotNull TipoUbicacion tipoDeUbicacion,
        @Min(0) @Max(100) Integer flujoDeEnergia)
{


    public static UbicacionDTO desdeModelo(Ubicacion ubicacion) {
        return new UbicacionDTO(
                ubicacion.getId(),
                ubicacion.getNombre(),
                ubicacion.getTipo(),
                ubicacion.getFlujoEnergia()
        );
    }

    public Ubicacion aModelo(){
        return switch (this.tipoDeUbicacion) {
            case SANTUARIO -> new Santuario(nombre, flujoDeEnergia);
            case CEMENTERIO -> new Cementerio(nombre, flujoDeEnergia);
        };
    }
}
