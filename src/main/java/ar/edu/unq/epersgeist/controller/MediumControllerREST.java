package ar.edu.unq.epersgeist.controller;

import ar.edu.unq.epersgeist.modelo.Medium;
import ar.edu.unq.epersgeist.servicios.MediumService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;

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
    public ResponseEntity<Medium> recuperarMedium(@PathVariable Long id) {
        return mediumService.recuperar(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<Collection<Medium>> recuperararTodosLosMediums() {
        Collection<Medium> mediums = mediumService.recuperarTodos();
        return ResponseEntity.ok(mediums);
    }

    @DeleteMapping("/{id}")
    public void borrarMedium(@PathVariable Long id) {
        Medium medium = mediumService.recuperar(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        mediumService.eliminar(medium);
    }

    @PutMapping("/{id}")
    public void actualizar(@PathVariable Long id, @RequestBody Medium medium) {
        Medium mediumUpdate = mediumService.recuperar(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        mediumUpdate.setNombre(medium.getNombre());
        mediumUpdate.setMana(medium.getMana());
        mediumUpdate.setManaMax(medium.getManaMax());
        mediumUpdate.setUbicacion(medium.getUbicacion());
        mediumService.actualizar(mediumUpdate);
    }
}
