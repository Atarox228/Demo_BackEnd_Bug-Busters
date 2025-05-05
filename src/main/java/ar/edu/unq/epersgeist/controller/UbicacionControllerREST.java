package ar.edu.unq.epersgeist.controller;

import ar.edu.unq.epersgeist.controller.dto.ActualizarUbicacionRequestDTO;
import ar.edu.unq.epersgeist.controller.dto.UbicacionDTO;
import ar.edu.unq.epersgeist.modelo.Espiritu;
import ar.edu.unq.epersgeist.modelo.Medium;
import ar.edu.unq.epersgeist.modelo.Ubicacion;
import ar.edu.unq.epersgeist.servicios.UbicacionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/ubicacion")
public class UbicacionControllerREST {

    private final UbicacionService ubicacionService;

    public UbicacionControllerREST(UbicacionService ubicacionService) {
        this.ubicacionService = ubicacionService;
    }

    @PostMapping
    public ResponseEntity<Void> crearUbicacion(@Valid @RequestBody UbicacionDTO ubicacion) {
        ubicacionService.crear(ubicacion.aModelo());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UbicacionDTO> recuperarUbicacion(@PathVariable Long id) {
        Ubicacion ubicacion = ubicacionService.recuperar(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return ResponseEntity.ok(UbicacionDTO.desdeModelo(ubicacion));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        Ubicacion ubicacion = ubicacionService.recuperar(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ubicacion no encontrado"));

        ubicacionService.eliminar(ubicacion);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/actualizar")
    public ResponseEntity<Void> actualizar(@PathVariable Long id, @RequestBody ActualizarUbicacionRequestDTO dto) {
        Ubicacion ubicacion = ubicacionService.recuperar(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        ubicacion.setNombre(dto.nombre());
        ubicacion.setFlujoEnergia(dto.flujoDeEnergia());

        ubicacionService.actualizar(ubicacion);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Collection<Ubicacion>> recuperarTodos() {
        Collection<Ubicacion> ubicaciones = ubicacionService.recuperarTodos();
        return ResponseEntity.ok(ubicaciones);
    }

    @GetMapping("/{id}/espiritus")
    public ResponseEntity<List<Espiritu>> espiritusEn(@PathVariable Long id) {
        List <Espiritu> espiritusEnUbicacion = ubicacionService.espiritusEn(id);
        return ResponseEntity.ok(espiritusEnUbicacion);
    }

    @GetMapping("/{id}/mediumsSinEspiritus")
    public ResponseEntity<List<Medium>> mediumsSinEspiritus(@PathVariable Long id) {
        List<Medium> mediums = ubicacionService.mediumsSinEspiritusEn(id);
        return ResponseEntity.ok(mediums);
    }
}
