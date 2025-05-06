package ar.edu.unq.epersgeist.controller;

import ar.edu.unq.epersgeist.configuration.excepciones.RecursoNoEncontradoException;
import ar.edu.unq.epersgeist.controller.dto.ActualizarMediumRequestDTO;
import ar.edu.unq.epersgeist.controller.dto.EspirituDTO;
import ar.edu.unq.epersgeist.controller.dto.MediumDTO;
import ar.edu.unq.epersgeist.modelo.Espiritu;
import ar.edu.unq.epersgeist.modelo.Medium;
import ar.edu.unq.epersgeist.modelo.Ubicacion;
import ar.edu.unq.epersgeist.modelo.exception.*;
import ar.edu.unq.epersgeist.servicios.EspirituService;
import ar.edu.unq.epersgeist.servicios.MediumService;
import ar.edu.unq.epersgeist.servicios.UbicacionService;
import ar.edu.unq.epersgeist.servicios.exception.IdNoValidoException;
import ar.edu.unq.epersgeist.servicios.exception.MovimientoInvalidoException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Objects;
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
        public ResponseEntity<Void> crearMedium(@Valid @RequestBody MediumDTO medium) {
            mediumService.crear(medium.aModelo());
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }


    @GetMapping("/{id}")
    public ResponseEntity<MediumDTO> recuperarMedium(@PathVariable Long id) {
        ValidacionID(id);
        Medium medium = mediumService.recuperar(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Ubicación con ID " + id + " no encontrada"));
        return ResponseEntity.ok(MediumDTO.desdeModelo(medium));
    }

    @GetMapping
    public ResponseEntity<Collection<Medium>> recuperararTodosLosMediums() {
        Collection<Medium> mediums = mediumService.recuperarTodos();
        return ResponseEntity.ok(mediums);
    }

    @DeleteMapping("/{id}")
    public void borrarMedium(@PathVariable Long id) {
        ValidacionID(id);
        Medium medium = mediumService.recuperar(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Ubicación con ID " + id + " no encontrada"));
        mediumService.eliminar(medium);
    }

    @PutMapping("/{id}/actualizar")
    public void actualizar(@PathVariable Long id, @Valid @RequestBody ActualizarMediumRequestDTO dto) {
        ValidacionID(id);
        Medium mediumUpdate = mediumService.recuperar(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Ubicación con ID " + id + " no encontrada"));
        mediumUpdate.setNombre(dto.nombre());
        mediumUpdate.setMana(dto.mana());
        mediumUpdate.setManaMax(dto.manaMaximo());
        mediumService.actualizar(mediumUpdate);
    }

    @PutMapping("/{id}/descansar")
    public void descansar(@PathVariable Long id){
        ValidacionID(id);
        Medium medium = mediumService.recuperar(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Ubicación con ID " + id + " no encontrada"));
        mediumService.descansar(medium.getId());
    }

    @PutMapping("/{id}/exorcizar/{mediumId}")
    public void exorcizar(@PathVariable Long id, @PathVariable Long mediumId) {
        ValidacionID(id);
        Medium medium = mediumService.recuperar(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Ubicación con ID " + id + " no encontrada"));
        ValidacionID(mediumId);
        Medium medium2 = mediumService.recuperar(mediumId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Ubicación con ID " + id + " no encontrada"));

        ValidarExorcismo(medium,medium2);

        mediumService.exorcizar(medium.getId(),medium2.getId());
    }

    private void ValidarExorcismo(Medium medium, Medium medium2) {

        if(Objects.equals(medium.getId(), medium2.getId())){
            throw new MismoMediumException();
        }
        if(!Objects.equals(medium.getUbicacion().getId(), medium2.getUbicacion().getId())){
            throw new ExorcismoEnDiferenteUbicacionException();
        }
        if(! medium.tieneAngeles()){
            throw new NoHayAngelesException();
        }
    }

    @PutMapping("/{id}/invocar/{espirituId}")
    public void invocar(@PathVariable Long id, @PathVariable Long espirituId) {
        ValidacionID(id);
        Medium medium = mediumService.recuperar(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Ubicación con ID " + id + " no encontrada"));
        ValidacionID(espirituId);
        Espiritu espiritu = espirituService.recuperar(espirituId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Ubicación con ID " + id + " no encontrada"));

        ValidarInvocacion(medium,espiritu);

        mediumService.invocar(medium.getId(),espiritu.getId());
    }

    private void ValidarInvocacion(Medium medium, Espiritu espiritu) {

        if(! espiritu.estaLibre()){
            throw new EspirituNoLibreException(espiritu);
        }
        if (! medium.getUbicacion().permiteInvocarTipo(espiritu.getTipo())){
            throw new InvocacionFallidaPorUbicacionException(espiritu,medium.getUbicacion());
        }

    }

    @GetMapping("/{id}/espiritu")
    public Set<EspirituDTO> espiritu(@PathVariable Long id){
        ValidacionID(id);
        Medium medium = mediumService.recuperar(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Ubicación con ID " + id + " no encontrada"));

        return mediumService.espiritus(medium.getId()).stream()
                .map(EspirituDTO::desdeModelo)
                .collect(Collectors.toSet());
    }

    @PutMapping("/{id}/mover/{ubicacionId}")
    public ResponseEntity<Void> mover(@PathVariable Long id, @PathVariable Long ubicacionId) {
        ValidacionID(id);
        Medium medium = mediumService.recuperar(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Ubicación con ID " + id + " no encontrada"));
        ValidacionID(ubicacionId);
        Ubicacion ubicacion = ubicacionService.recuperar(ubicacionId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Ubicación con ID " + id + " no encontrada"));

        if(medium.getUbicacion() != null && medium.getUbicacion().getId().equals(ubicacion.getId())){
            throw new MovimientoInvalidoException();
        }

        mediumService.mover(medium.getId(),ubicacion.getId());
        return ResponseEntity.noContent().build();
    }

    private void ValidacionID(@PathVariable Long id) {
        if (id == null || id <= 0) {
            throw new IdNoValidoException();
        }
    }
}
