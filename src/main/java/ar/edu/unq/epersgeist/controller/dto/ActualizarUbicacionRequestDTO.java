package ar.edu.unq.epersgeist.controller.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record ActualizarUbicacionRequestDTO(
        @NotBlank String nombre,
        @Min(1) Integer flujoDeEnergia
) {
}
