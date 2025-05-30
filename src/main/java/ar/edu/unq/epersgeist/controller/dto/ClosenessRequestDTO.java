package ar.edu.unq.epersgeist.controller.dto;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ClosenessRequestDTO(@NotNull List<Long> ids) {
}
