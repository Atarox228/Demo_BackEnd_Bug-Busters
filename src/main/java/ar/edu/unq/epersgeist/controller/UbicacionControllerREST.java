package ar.edu.unq.epersgeist.controller;

import ar.edu.unq.epersgeist.configuration.excepciones.RecursoNoEncontradoException;
import ar.edu.unq.epersgeist.modelo.Espiritu;
import ar.edu.unq.epersgeist.modelo.Medium;
import ar.edu.unq.epersgeist.modelo.Ubicacion;
import ar.edu.unq.epersgeist.servicios.UbicacionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;
import java.util.List;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/ubicacion")
public class UbicacionControllerREST {

    private final UbicacionService ubicacionService;

    public UbicacionControllerREST(UbicacionService ubicacionService) {
        this.ubicacionService = ubicacionService;
    }

    @PostMapping
    public void crearUbicacion(@Valid @RequestBody Ubicacion ubicacion) {
        ubicacionService.crear(ubicacion);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ubicacion> recuperarUbicacion(@PathVariable Long id) {
        return ubicacionService.recuperar(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // ejemplo de uso de excepcion hecha en configuracion
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        Ubicacion ubicacion = ubicacionService.recuperar(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Ubicación con ID " + id + " no encontrada"));

        ubicacionService.eliminar(ubicacion);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> actualizar(@PathVariable Long id, @RequestBody Ubicacion ubicacionActualizada) {
        Ubicacion ubicacionActualizar = ubicacionService.recuperar(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ubicacion no encontrado"));
        ubicacionActualizar.setNombre(ubicacionActualizada.getNombre());
        ubicacionService.actualizar(ubicacionActualizar);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Collection<Ubicacion>> recuperarTodos() {
        Collection<Ubicacion> ubicaciones = ubicacionService.recuperarTodos();
        return ResponseEntity.ok(ubicaciones);
    }

    @GetMapping("/espiritusEn/{id}")
    public ResponseEntity<List<Espiritu>> espiritusEn(@PathVariable Long id) {
        List <Espiritu> espiritusEnUbicacion = ubicacionService.espiritusEn(id);
        return ResponseEntity.ok(espiritusEnUbicacion);
    }

    @GetMapping("/mediumsSinEspiritusEn/{id}")
    public ResponseEntity<List<Medium>> mediumsSinEspiritusEn(@PathVariable Long id) {
        List<Medium> mediums = ubicacionService.mediumsSinEspiritusEn(id);
        return ResponseEntity.ok(mediums);
    }

    // ejemplo para comprobar funcionamiento del manejador de errores y su handleo de errores
    @GetMapping("/test-error")
    public void lanzarError() {
        throw new RecursoNoEncontradoException("Este es un error de prueba");
    }
}
