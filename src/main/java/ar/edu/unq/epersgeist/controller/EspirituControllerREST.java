package ar.edu.unq.epersgeist.controller;

import ar.edu.unq.epersgeist.configuration.excepciones.RecursoNoEncontradoException;
import ar.edu.unq.epersgeist.controller.dto.ActualizarEspirituRequestDTO;
import ar.edu.unq.epersgeist.controller.dto.EspirituDTO;
import ar.edu.unq.epersgeist.controller.dto.MediumDTO;
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
    public void crearEspiritu(@Valid @RequestBody EspirituDTO espiritu) {
        espirituService.crear(espiritu.aModelo());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EspirituDTO> recuperarEspiritu(@PathVariable Long id) {
        ValidacionID(id);
        Espiritu espiritu = espirituService.recuperar(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Ubicación con ID " + id + " no encontrada"));
        return ResponseEntity.ok(EspirituDTO.desdeModelo(espiritu));
    }

    @GetMapping
    public Set<EspirituDTO> recuperarTodos() {
        return espirituService.recuperarTodos().stream()
                .map(EspirituDTO::desdeModelo)
                .collect(Collectors.toSet());
    }

    @DeleteMapping("{id}")
    public void eliminarEspiritu(@PathVariable Long id) {
        ValidacionID(id);
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
        ValidacionID(id);
        Espiritu espiritu = espirituService.recuperar(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        ValidacionID(mediumid);
        Medium medium = mediumService.recuperar(mediumid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if(! medium.puedeConectarse(espiritu)){
            throw new NoSePuedenConectarException(medium,espiritu);
        }

        espirituService.conectar(espiritu.getId(),medium.getId());
    }

    @GetMapping("/demoniacos/{direccion}/{pagina}/{cantidadPorPagina}")
    public Set<EspirituDTO> espiritusDemoniacos(@PathVariable Direccion direccion, @PathVariable Integer pagina, @PathVariable Integer cantidadPorPagina){
        if (pagina < 1 ){
            throw new PaginaInvalidaException("La pagina no es valida");
        }
        if (cantidadPorPagina < 0 ){
            throw new PaginaInvalidaException("La cantidad de paginas no es valida");
        }

        return espirituService.espiritusDemoniacos( direccion, pagina, cantidadPorPagina).stream()
                .map(EspirituDTO::desdeModelo)
                .collect(Collectors.toSet());
    }

    private void ValidacionID(@PathVariable Long id) {
        if (id == null || id <= 0) {
            throw new IdNoValidoException();
        }
    }
}