package ar.edu.unq.epersgeist.controller;

import ar.edu.unq.epersgeist.controller.dto.ActualizarMediumRequestDTO;
import ar.edu.unq.epersgeist.controller.dto.EspirituDTO;
import ar.edu.unq.epersgeist.controller.dto.MediumDTO;
import ar.edu.unq.epersgeist.modelo.Medium;
import ar.edu.unq.epersgeist.servicios.EspirituService;
import ar.edu.unq.epersgeist.servicios.MediumService;
import ar.edu.unq.epersgeist.servicios.UbicacionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/medium")
public class MediumControllerREST {
    private final MediumService mediumService;

    public MediumControllerREST(MediumService mediumService) {
        this.mediumService = mediumService;
    }

    @PostMapping
    public ResponseEntity<Void> crearMedium(@RequestBody @Valid MediumDTO medium) {
        mediumService.crear(medium.aModelo());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    @GetMapping("/{id}")
    public ResponseEntity<MediumDTO> recuperarMedium(@PathVariable Long id) {

        return ResponseEntity.ok(MediumDTO.desdeModelo(mediumService.recuperar(id).get()));
    }

    @GetMapping
    public Collection<MediumDTO> recuperararTodosLosMediums() {
        return mediumService.recuperarTodos().stream()
                .map(MediumDTO::desdeModelo)
                .collect(Collectors.toList());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> borrarMedium(@PathVariable Long id) {
        mediumService.eliminar(mediumService.recuperar(id).get());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("/{id}/actualizar")
    public ResponseEntity<Void> actualizar(@PathVariable Long id, @Valid @RequestBody ActualizarMediumRequestDTO dto) {
        Medium mediumUpdate = mediumService.recuperar(id).orElseThrow();
        mediumUpdate.setNombre(dto.nombre());
        mediumUpdate.setMana(dto.mana());
        mediumUpdate.setManaMax(dto.manaMaximo());
        mediumService.actualizar(mediumUpdate);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("/{id}/descansar")
    public ResponseEntity<Void> descansar(@PathVariable Long id){
        mediumService.descansar(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("/{id}/exorcizar/{mediumId}")
    public ResponseEntity<Void> exorcizar(@PathVariable Long id, @PathVariable Long mediumId) {
        mediumService.exorcizar(id,mediumId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


    @PutMapping("/{id}/invocar/{espirituId}")
    public ResponseEntity<Void> invocar(@PathVariable Long id, @PathVariable Long espirituId) {
        mediumService.invocar(id,espirituId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/{id}/espiritu")
    public Set<EspirituDTO> espiritu(@PathVariable Long id){

        return mediumService.espiritus(id).stream()
                .map(EspirituDTO::desdeModelo)
                .collect(Collectors.toSet());
    }

    @PutMapping("/{id}/mover/{ubicacionId}")
    public ResponseEntity<Void> mover(@PathVariable Long id, @PathVariable Long ubicacionId) {
        mediumService.mover(id,ubicacionId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
