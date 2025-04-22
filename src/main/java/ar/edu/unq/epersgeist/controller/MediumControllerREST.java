package ar.edu.unq.epersgeist.controller;

import ar.edu.unq.epersgeist.modelo.Medium;
import ar.edu.unq.epersgeist.servicios.MediumService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/medium")
public class MediumControllerREST {
    private final MediumService mediumService;

    public MediumControllerREST(MediumService mediumService) {
        this.mediumService = mediumService;
    }

    @PostMapping
    public void crearMedium(@RequestBody Medium medium) {
        mediumService.crear(medium);
    }

    @GetMapping("/{id}")
    public Medium recuperarMedium(@PathVariable Long id) {
        Medium medium = mediumService.recuperar(id);
        return ResponseEntity.ok(medium).getBody();
    }
}
