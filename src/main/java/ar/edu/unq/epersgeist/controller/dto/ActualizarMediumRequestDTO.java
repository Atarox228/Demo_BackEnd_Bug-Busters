package ar.edu.unq.epersgeist.controller.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record ActualizarMediumRequestDTO(
        @NotBlank String nombre,
        @Min(0) Integer mana,
        @Min(1) Integer manaMaximo

) {
}
