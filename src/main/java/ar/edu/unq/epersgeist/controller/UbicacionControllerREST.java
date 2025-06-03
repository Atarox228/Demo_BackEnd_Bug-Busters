package ar.edu.unq.epersgeist.controller;

import ar.edu.unq.epersgeist.controller.dto.*;
import ar.edu.unq.epersgeist.modelo.*;
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
        ubicacionService.crear(ubicacion.aMongo());
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
    public ResponseEntity<Void> actualizar(@PathVariable Long id, @Valid @RequestBody ActualizarUbicacionRequestDTO dto) {
        Ubicacion ubicacion = ubicacionService.recuperar(id).get();

        String nombreViejo = ubicacion.getNombre();
        ubicacion.setNombre(dto.nombre());
        ubicacion.setFlujoEnergia(dto.flujoDeEnergia());

        ubicacionService.actualizar(ubicacion,nombreViejo);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping
    public Collection<UbicacionDTO> recuperarTodos() {
        return ubicacionService.recuperarTodos().stream()
                .map(UbicacionDTO::desdeModelo)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}/{espiritus}")
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

    @PutMapping("/{id}/conectar/{idDestino}")
    public ResponseEntity<Void> conectar(@PathVariable Long id, @PathVariable Long idDestino) {
        ubicacionService.conectar(id, idDestino);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("/{id}/estanConectados/{idDestino}")
    public ResponseEntity<String> estanConectados(@PathVariable Long id, @PathVariable Long idDestino) {

        Boolean resultado = ubicacionService.estanConectadas(id, idDestino);
        String mensaje = resultado ? "Conectado" : "Desconectado";

        return ResponseEntity.status(HttpStatus.OK).body(mensaje);
    }

    @GetMapping("/DegreeCentrality")
    public DegreeResult degreeCentrality(@Valid @RequestBody DegreeRequestDTO dto) {
        return ubicacionService.degreeOf(dto.ids(), dto.degreeType());
    }

    @GetMapping("/ubicacionesSobrecargadas/{umbralDeEnergia}")
    public List<UbicacionDTO> ubicacionesSobrecargadas(@PathVariable Integer umbralDeEnergia){
        return ubicacionService.ubicacionesSobrecargadas(umbralDeEnergia).stream()
                .map(UbicacionDTO::desdeNeo)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}/caminoMasCorto/{idDestino}")
    public List<UbicacionDTO> caminoMasCorto(@PathVariable Long id, @PathVariable Long idDestino){
        return ubicacionService.caminoMasCorto(id, idDestino).stream()
                .map(UbicacionDTO::desdeNeo)
                .collect(Collectors.toList());
    }

    @GetMapping("/closeness")
    public List<ClosenessResultDTO> closenessOf(@RequestBody @Valid ClosenessRequestDTO closeness) {
        return ubicacionService.closenessOf(closeness.ids()).stream()
                .map(ClosenessResultDTO::desdeModelo)
                .collect(Collectors.toList());
    }

}
