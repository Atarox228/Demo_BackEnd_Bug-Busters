package ar.edu.unq.epersgeist.controller;


import ar.edu.unq.epersgeist.controller.dto.EspirituDTO;
import ar.edu.unq.epersgeist.controller.dto.MediumDTO;
import ar.edu.unq.epersgeist.controller.dto.ActualizarUbicacionRequestDTO;
import ar.edu.unq.epersgeist.controller.dto.UbicacionDTO;
import ar.edu.unq.epersgeist.modelo.Ubicacion;
import ar.edu.unq.epersgeist.servicios.UbicacionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/ubicacion")
public class UbicacionControllerREST {

    private final UbicacionService ubicacionService;

    public UbicacionControllerREST(UbicacionService ubicacionService) {
        this.ubicacionService = ubicacionService;
    }

    @PostMapping
    public ResponseEntity<Void> crearUbicacion(@RequestBody @Valid UbicacionDTO ubicacion) {
        ubicacionService.crear(ubicacion.aModelo());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UbicacionDTO> recuperarUbicacion(@PathVariable Long id) {
        return ResponseEntity.ok(UbicacionDTO.desdeModelo(ubicacionService.recuperar(id).get()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        Ubicacion ubicacion = ubicacionService.recuperar(id).get();
        ubicacionService.eliminar(ubicacion);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("/{id}/actualizar")
    public ResponseEntity<Void> actualizar(@PathVariable Long id, @RequestBody ActualizarUbicacionRequestDTO dto) {
        Ubicacion ubicacion = ubicacionService.recuperar(id).get();

        ubicacion.setNombre(dto.nombre());
        ubicacion.setFlujoEnergia(dto.flujoDeEnergia());

        ubicacionService.actualizar(ubicacion);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping
    public Collection<UbicacionDTO> recuperarTodos() {
        return ubicacionService.recuperarTodos().stream()
                .map(UbicacionDTO::desdeModelo)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}/espiritus")
    public List<EspirituDTO> espiritusEn(@PathVariable Long id) {
        return ubicacionService.espiritusEn(id).stream()
                .map(EspirituDTO::desdeModelo)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}/mediumsSinEspiritus")
    public List<MediumDTO> mediumsSinEspiritus(@PathVariable Long id) {
        return ubicacionService.mediumsSinEspiritusEn(id).stream()
                .map(MediumDTO::desdeModelo)
                .collect(Collectors.toList());
    }

}
