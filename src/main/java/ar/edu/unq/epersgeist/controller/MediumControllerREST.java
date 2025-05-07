package ar.edu.unq.epersgeist.controller;

import ar.edu.unq.epersgeist.controller.dto.ActualizarMediumRequestDTO;
import ar.edu.unq.epersgeist.controller.dto.EspirituDTO;
import ar.edu.unq.epersgeist.controller.dto.MediumDTO;
import ar.edu.unq.epersgeist.modelo.Espiritu;
import ar.edu.unq.epersgeist.modelo.Medium;
import ar.edu.unq.epersgeist.modelo.Ubicacion;
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
    private final EspirituService espirituService;
    private final UbicacionService ubicacionService;

    public MediumControllerREST(MediumService mediumService, EspirituService espirituService, UbicacionService ubicacionService) {
        this.mediumService = mediumService;
        this.espirituService = espirituService;
        this.ubicacionService = ubicacionService;
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
    public void borrarMedium(@PathVariable Long id) {
        mediumService.eliminar(mediumService.recuperar(id).get());
    }

    @PutMapping("/{id}/actualizar")
    public void actualizar(@PathVariable Long id, @Valid @RequestBody ActualizarMediumRequestDTO dto) {
        Medium mediumUpdate = mediumService.recuperar(id).orElseThrow();
        mediumUpdate.setNombre(dto.nombre());
        mediumUpdate.setMana(dto.mana());
        mediumUpdate.setManaMax(dto.manaMaximo());
        mediumService.actualizar(mediumUpdate);
    }

    @PutMapping("/{id}/descansar")
    public void descansar(@PathVariable Long id){
//        Medium medium = mediumService.recuperar(id).orElseThrow();
//        mediumService.descansar(medium.getId());
        mediumService.descansar(id);
    }

    @PutMapping("/{id}/exorcizar/{mediumId}")
    public void exorcizar(@PathVariable Long id, @PathVariable Long mediumId) {

        Medium medium = mediumService.recuperar(id).orElseThrow();
        Medium medium2 = mediumService.recuperar(mediumId).orElseThrow();

        mediumService.exorcizar(medium.getId(),medium2.getId());
    }


    @PutMapping("/{id}/invocar/{espirituId}")
    public void invocar(@PathVariable Long id, @PathVariable Long espirituId) {
        Medium medium = mediumService.recuperar(id).orElseThrow();
        Espiritu espiritu = espirituService.recuperar(espirituId).orElseThrow();

        mediumService.invocar(medium.getId(),espiritu.getId());
    }

    @GetMapping("/{id}/espiritu")
    public Set<EspirituDTO> espiritu(@PathVariable Long id){
        Medium medium = mediumService.recuperar(id).orElseThrow();

        return mediumService.espiritus(medium.getId()).stream()
                .map(EspirituDTO::desdeModelo)
                .collect(Collectors.toSet());
    }

    @PutMapping("/{id}/mover/{ubicacionId}")
    public void mover(@PathVariable Long id, @PathVariable Long ubicacionId) {
        Medium medium = mediumService.recuperar(id).orElseThrow();
        Ubicacion ubicacion = ubicacionService.recuperar(ubicacionId).orElseThrow();

        mediumService.mover(medium.getId(),ubicacion.getId());
    }

}
