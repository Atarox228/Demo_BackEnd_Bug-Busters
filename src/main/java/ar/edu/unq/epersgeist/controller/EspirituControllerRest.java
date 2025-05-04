package ar.edu.unq.epersgeist.controller;

import ar.edu.unq.epersgeist.modelo.*;
import ar.edu.unq.epersgeist.servicios.EspirituService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ubicacion")
public class EspirituControllerRest {

    private final EspirituService espirituService;

    public EspirituControllerRest(EspirituService espirituService) {
        this.espirituService = espirituService;
    }

    @PostMapping
    public void crearEspiritu(@RequestBody Espiritu espiritu) {
        espirituService.crear(espiritu);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Espiritu> recuperarEspiritu(@PathVariable Long id) {
        return espirituService.recuperar(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}