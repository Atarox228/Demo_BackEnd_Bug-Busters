package ar.edu.unq.epersgeist.controller;

import ar.edu.unq.epersgeist.controller.excepciones.RecursoNoEncontradoException;
import ar.edu.unq.epersgeist.controller.dto.ActualizarEspirituRequestDTO;
import ar.edu.unq.epersgeist.controller.dto.EspirituDTO;
import ar.edu.unq.epersgeist.modelo.*;
import ar.edu.unq.epersgeist.modelo.exception.NoSePuedenConectarException;
import ar.edu.unq.epersgeist.servicios.EspirituService;
import ar.edu.unq.epersgeist.servicios.MediumService;
import ar.edu.unq.epersgeist.servicios.enums.Direccion;
import ar.edu.unq.epersgeist.servicios.exception.IdNoValidoException;
import ar.edu.unq.epersgeist.servicios.exception.PaginaInvalidaException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<Void> crearEspiritu(@RequestBody @Valid EspirituDTO espiritu) {
        espirituService.crear(espiritu.aModelo());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<EspirituDTO> recuperarEspiritu(@PathVariable Long id) {

        return ResponseEntity.ok(EspirituDTO.desdeModelo(espirituService.recuperar(id).get()));
    }

    @GetMapping
    public Set<EspirituDTO> recuperarTodos() {
        return espirituService.recuperarTodos().stream()
                .map(EspirituDTO::desdeModelo)
                .collect(Collectors.toSet());
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> eliminarEspiritu(@PathVariable Long id) {
        Espiritu espiritu = espirituService.recuperar(id).get();
        espirituService.eliminar(espiritu);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("/{id}/actualizar")
    public ResponseEntity<Void> actualizarEspiritu(@PathVariable Long id, @RequestBody ActualizarEspirituRequestDTO dto) {
        Espiritu espiritu = espirituService.recuperar(id).get();
        espiritu.setNombre(dto.nombre());
        espirituService.actualizar(espiritu);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("/{id}/conectarse/{mediumid}")
    public ResponseEntity<Void> conectarse(@PathVariable Long id, @PathVariable Long mediumid) {
        espirituService.conectar(id,mediumid);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/demoniacos/{direccion}/{pagina}/{cantidadPorPagina}")
    public Set<EspirituDTO> espiritusDemoniacos(@PathVariable Direccion direccion, @PathVariable Integer pagina, @PathVariable Integer cantidadPorPagina){
        if (pagina < 1 ){
            throw new PaginaInvalidaException("La pagina no es valida");
        }
        if (cantidadPorPagina < 1 ){
            throw new PaginaInvalidaException("La cantidad de paginas no es valida");
        }

        return espirituService.espiritusDemoniacos( direccion, pagina, cantidadPorPagina).stream()
                .map(EspirituDTO::desdeModelo)
                .collect(Collectors.toSet());
    }

}