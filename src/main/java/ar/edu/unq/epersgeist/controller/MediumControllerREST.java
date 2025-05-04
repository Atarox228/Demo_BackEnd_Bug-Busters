package ar.edu.unq.epersgeist.controller;

import ar.edu.unq.epersgeist.controller.dto.ActualizarMediumRequestDTO;
import ar.edu.unq.epersgeist.controller.dto.EspirituDTO;
import ar.edu.unq.epersgeist.controller.dto.MediumDTO;
import ar.edu.unq.epersgeist.controller.dto.UbicacionDTO;
import ar.edu.unq.epersgeist.modelo.Espiritu;
import ar.edu.unq.epersgeist.modelo.Medium;
import ar.edu.unq.epersgeist.modelo.Ubicacion;
import ar.edu.unq.epersgeist.servicios.EspirituService;
import ar.edu.unq.epersgeist.servicios.MediumService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/medium")
public class MediumControllerREST {
    private final MediumService mediumService;
    private final EspirituService espirituService;

    public MediumControllerREST(MediumService mediumService, EspirituService espirituService) {
        this.mediumService = mediumService;
        this.espirituService = espirituService;
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

    @PutMapping("/{id}/descansar")
    public void descansar(@PathVariable Long id){
        Medium medium = mediumService.recuperar(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        mediumService.descansar(medium.getId());
    }

    @PutMapping("/{id}/exorcizar/{mediumId}")
    public void exorcizarA(@PathVariable Long id, @PathVariable Long mediumId) {
        Medium medium = mediumService.recuperar(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        Medium medium2 = mediumService.recuperar(mediumId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        mediumService.exorcizar(medium.getId(),medium2.getId());
    }

    @PutMapping("/{id}/invocar/{espirituId}")
    public void invocarA(@PathVariable Long id, @PathVariable Long espirituId) {
        Medium medium = mediumService.recuperar(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        Espiritu espiritu = espirituService.recuperar(espirituId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        mediumService.invocar(medium.getId(),espiritu.getId());
    }

    @GetMapping("/{id}/espiritu")
    public Set<EspirituDTO> espiritu(@PathVariable Long id){
        Medium medium = mediumService.recuperar(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return mediumService.espiritus(medium.getId()).stream()
                .map(EspirituDTO::desdeModelo)
                .collect(Collectors.toSet());
    }

    @PutMapping("/{id}/mover/{ubicacionId}")
    public void moverA(@PathVariable Long id, @PathVariable Long ubicacionId) {
        Medium medium = mediumService.recuperar(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        mediumService.mover(ubicacionId,medium.getId());
    }
}
