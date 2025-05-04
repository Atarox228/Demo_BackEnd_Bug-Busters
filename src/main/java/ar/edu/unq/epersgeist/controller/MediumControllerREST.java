package ar.edu.unq.epersgeist.controller;

import ar.edu.unq.epersgeist.controller.dto.ActualizarMediumRequestDTO;
import ar.edu.unq.epersgeist.controller.dto.MediumDTO;
import ar.edu.unq.epersgeist.controller.dto.UbicacionDTO;
import ar.edu.unq.epersgeist.modelo.Medium;
import ar.edu.unq.epersgeist.modelo.Ubicacion;
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
    public ResponseEntity<Void> crearMedium(@RequestBody MediumDTO medium) {

        mediumService.crear(medium.aModelo());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<MediumDTO> recuperarMedium(@PathVariable Long id) {
        Medium medium = mediumService.recuperar(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return ResponseEntity.ok(MediumDTO.desdeModelo(medium));
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
    public void actualizar(@PathVariable Long id, @RequestBody ActualizarMediumRequestDTO dto) {
        Medium mediumUpdate = mediumService.recuperar(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        mediumUpdate.setNombre(dto.nombre());
        mediumUpdate.setMana(dto.mana());
        mediumUpdate.setManaMax(dto.manaMaximo());
        mediumService.actualizar(mediumUpdate);
    }
}
