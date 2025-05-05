package ar.edu.unq.epersgeist.controller;

import ar.edu.unq.epersgeist.controller.dto.ActualizarEspirituRequestDTO;
import ar.edu.unq.epersgeist.controller.dto.EspirituDTO;
import ar.edu.unq.epersgeist.modelo.*;
import ar.edu.unq.epersgeist.servicios.EspirituService;
import ar.edu.unq.epersgeist.servicios.MediumService;
import ar.edu.unq.epersgeist.servicios.enums.Direccion;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/espiritu")
public class EspirituControllerREST {

    private final EspirituService espirituService;
    private final MediumService mediumService;

    public EspirituControllerREST(EspirituService espirituService, MediumService mediumService) {
        this.espirituService = espirituService;
        this.mediumService = mediumService;
    }

    @PostMapping
    public void crearEspiritu(@RequestBody EspirituDTO espiritu) {
        espirituService.crear(espiritu.aModelo());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EspirituDTO> recuperarEspiritu(@PathVariable Long id) {
        return espirituService.recuperar(id)
                .map(EspirituDTO::desdeModelo)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public Set<EspirituDTO> recuperarTodos() {
        return espirituService.recuperarTodos().stream()
                .map(EspirituDTO::desdeModelo)
                .collect(Collectors.toSet());
    }

    @DeleteMapping("{id}")
    public void eliminarEspiritu(@PathVariable Long id) {
        Espiritu espiritu = espirituService.recuperar(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        espirituService.eliminar(espiritu);
    }

    @PutMapping("/{id}/actualizar")
    public void actualizarEspiritu(@PathVariable Long id, @RequestBody ActualizarEspirituRequestDTO dto) {
        Espiritu espiritu = espirituService.recuperar(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        espiritu.setNombre(dto.nombre());
        espirituService.actualizar(espiritu);
    }

    @PutMapping("/{id}/conectarse/{mediumid}")
    public void conectarse(@PathVariable Long id, @PathVariable Long mediumid) {
        Espiritu espiritu = espirituService.recuperar(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        Medium medium = mediumService.recuperar(mediumid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        espirituService.conectar(espiritu.getId(),medium.getId());
    }

    @GetMapping("/demoniacos/{direccion}/{pagina}/{cantidadPorPagina}")
    public Set<EspirituDTO> espiritusDemoniacos(@PathVariable Direccion direccion, @PathVariable Integer pagina, @PathVariable Integer cantidadPorPagina){
        return espirituService.espiritusDemoniacos( direccion, pagina, cantidadPorPagina).stream()
                .map(EspirituDTO::desdeModelo)
                .collect(Collectors.toSet());
    }
}