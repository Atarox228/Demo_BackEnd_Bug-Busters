package ar.edu.unq.epersgeist.controller.dto;

import java.util.List;

public record DegreeRequest(List<Long> ids, String degrreType) {
}
