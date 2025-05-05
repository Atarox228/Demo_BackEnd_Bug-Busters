package ar.edu.unq.epersgeist.controller.dto;

import jakarta.validation.constraints.NotBlank;

public record ActualizarEspirituRequestDTO(
        @NotBlank String nombre
) {
}
