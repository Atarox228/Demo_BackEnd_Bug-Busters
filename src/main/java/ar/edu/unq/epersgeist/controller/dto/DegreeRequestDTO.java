package ar.edu.unq.epersgeist.controller.dto;

import ar.edu.unq.epersgeist.modelo.enums.DegreeType;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record DegreeRequestDTO(
        @NotNull List<Long> ids,
        @NotNull DegreeType degreeType
) {
}
