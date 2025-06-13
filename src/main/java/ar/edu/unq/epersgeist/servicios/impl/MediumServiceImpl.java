package ar.edu.unq.epersgeist.servicios.impl;

import ar.edu.unq.epersgeist.controller.excepciones.*;
import ar.edu.unq.epersgeist.modelo.*;
import ar.edu.unq.epersgeist.modelo.exception.UbicacionLejanaException;
import ar.edu.unq.epersgeist.persistencia.repositories.interfaces.CoordenadaRepository;
import ar.edu.unq.epersgeist.persistencia.repositories.interfaces.EspirituRepository;
import ar.edu.unq.epersgeist.persistencia.repositories.interfaces.MediumRepository;
import ar.edu.unq.epersgeist.persistencia.repositories.interfaces.UbicacionRepository;
import ar.edu.unq.epersgeist.servicios.MediumService;
import ar.edu.unq.epersgeist.servicios.exception.*;
import ar.edu.unq.epersgeist.servicios.helpers.validacionesGenerales;
import jakarta.transaction.Transactional;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.stereotype.Service;
import java.util.Collection;
import java.util.List;

@Service
@Transactional
public class MediumServiceImpl implements MediumService {
    public final EspirituRepository espirituRepository;
    private final UbicacionRepository ubicacionRepository;
    private final MediumRepository mediumRepository;
    private final CoordenadaRepository coordenadaRepository;

    public MediumServiceImpl(MediumRepository mediumRepository, EspirituRepository espirituRepository, UbicacionRepository ubicacionRepository, CoordenadaRepository coordenadaRepository) {
        this.espirituRepository = espirituRepository;
        this.ubicacionRepository = ubicacionRepository;
        this.mediumRepository = mediumRepository;
        this.coordenadaRepository = coordenadaRepository;
    }

    @Override
    public void crear(Medium medium) {
        mediumRepository.crear(medium);
    }

    @Override
    public Medium recuperar(Long id) {
        validacionesGenerales.revisarId(id);
        Medium medium = mediumRepository.recuperar(id);
        validacionesGenerales.revisarEntidadEliminado(medium.getDeleted(), medium);

        return medium;
    }


    @Override
    public Collection<Medium> recuperarTodos() {
        return mediumRepository.recuperarTodosNoEliminados();
    }

    @Override
    public void eliminar(Medium medium) {
        if (!mediumRepository.existsById(medium.getId())) {
            throw new RecursoNoEncontradoException("Medium con ID " + medium.getId() + " no encontrado");
        }
        validacionesGenerales.revisarEntidadEliminado(medium.getDeleted(),medium);
        validacionesGenerales.revisarUbicacionConEntidades(medium,!espiritus(medium.getId()).isEmpty());
        medium.setDeleted(true);
        mediumRepository.actualizar(medium);
    }

    @Override
    public void eliminarTodo() {
        mediumRepository.eliminarTodos();
    }

    @Override
    public void actualizar(Medium medium) {
        validacionesGenerales.revisarId(medium.getId());
        if (!mediumRepository.existsById(medium.getId())) {
            throw new RecursoNoEncontradoException("Medium con ID " + medium.getId() + " no encontrado");
        }
        validacionesGenerales.revisarEntidadEliminado(medium.getDeleted(),medium);
        mediumRepository.actualizar(medium);
    }

    @Override
    public void exorcizar(long idMedium, long idMedium2) {
        validacionesGenerales.revisarId(idMedium);
        validacionesGenerales.revisarId(idMedium2);
        Medium medium = mediumRepository.recuperar(idMedium);
        Medium medium2 = mediumRepository.recuperar(idMedium2);

        validacionesGenerales.revisarUbicacionNoNula(medium.getUbicacion(),medium,idMedium);
        validacionesGenerales.revisarUbicacionNoNula(medium2.getUbicacion(),medium2,idMedium2);
        
        List<Espiritu> angeles = espirituRepository.recuperarEspiritusDeTipo(medium.getId(), Angel.class);
        List<Espiritu> demonios = espirituRepository.recuperarEspiritusDeTipo(medium2.getId(), Demonio.class);
        medium.exorcizar(medium2, angeles, demonios);
        mediumRepository.actualizar(medium);
        mediumRepository.actualizar(medium2);
    }

    @Override
    public Espiritu invocar(Long mediumId, Long espirituId) {
        validacionesGenerales.revisarId(mediumId);
        validacionesGenerales.revisarId(espirituId);

        Medium medium = mediumRepository.recuperar(mediumId);
        Espiritu espiritu = espirituRepository.recuperar(espirituId);
        validacionesGenerales.revisarEntidadEliminado(medium.getDeleted(),medium);
        validacionesGenerales.revisarEntidadEliminado(espiritu.getDeleted(),espiritu);
        validacionesGenerales.revisarUbicacionNoNula(medium.getUbicacion(),medium,mediumId);

        CoordenadaMongo coordenadaMedium = coordenadaRepository.findByEntityIdAndEntityType(medium.getClass().toString(), mediumId);
        CoordenadaMongo coordenadaEspiritu = coordenadaRepository.findByEntityIdAndEntityType(espiritu.getTipo().toString(), espirituId);

        if (! coordenadaRepository.estaEnRangoDeInvocar(coordenadaEspiritu, medium)) {
            throw new FueraDeRangoDistanciaException();
        }

        medium.invocar(espiritu);
        coordenadaEspiritu.setPunto(coordenadaMedium.getPunto());

        mediumRepository.actualizar(medium);
        coordenadaRepository.actualizarCoordenada(coordenadaEspiritu);
        return espirituRepository.recuperar(espirituId);
    }

    @Override
    public List<Espiritu> espiritus(Long idMedium) {
        validacionesGenerales.revisarId(idMedium);
        Medium medium = mediumRepository.recuperar(idMedium);
        validacionesGenerales.revisarEntidadEliminado(medium.getDeleted(),medium);
        return mediumRepository.obtenerEspiritus(idMedium);
    }

    @Override
    public void descansar(Long mediumId){
        validacionesGenerales.revisarId(mediumId);
        Medium medium = mediumRepository.recuperar(mediumId);
        validacionesGenerales.revisarUbicacionNoNula(medium.getUbicacion(),medium,mediumId);
        medium.descansar();
        mediumRepository.actualizar(medium);
    }

    @Override
    public void mover(Long mediumId, Double latitud, Double longitud) {

        validacionesGenerales.revisarId(mediumId);
        Medium medium = mediumRepository.recuperar(mediumId);
        validacionesGenerales.revisarEntidadEliminado(medium.getDeleted(),medium);

        GeoJsonPoint puntoDestino = new GeoJsonPoint(longitud, latitud);
        AreaMongo areaDestino = ubicacionRepository.recuperarPorCoordenada(puntoDestino);

        Ubicacion ubicacionDestino = ubicacionRepository.recuperar(areaDestino.getIdUbicacion());

        if (medium.getUbicacion() != null && medium.getUbicacion().getId().equals(ubicacionDestino.getId())) throw new MovimientoInvalidoException();
        if ((medium.getUbicacion() != null &&
                !coordenadaRepository.estaEnRangoDeMover(medium.getId(), longitud, latitud)) || !ubicacionRepository.estanConectadasDirecta(medium.getUbicacion(), ubicacionDestino)){
            throw new UbicacionLejanaException();
        }

        List<Long> espiritusIds = mediumRepository.obtenerIdsDeEspiritus(mediumId);

        medium.moverseA(ubicacionDestino);
        mediumRepository.actualizar(medium);

        coordenadaRepository.actualizarOCrearCoordenada("MEDIUM", mediumId, puntoDestino);
        coordenadaRepository.actualizarCoordenadas("ESPIRITU", espiritusIds, puntoDestino);
    }
}