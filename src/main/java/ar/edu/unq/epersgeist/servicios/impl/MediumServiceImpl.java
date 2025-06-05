package ar.edu.unq.epersgeist.servicios.impl;

import ar.edu.unq.epersgeist.controller.excepciones.*;
import ar.edu.unq.epersgeist.modelo.*;
import ar.edu.unq.epersgeist.modelo.exception.UbicacionLejanaException;
import ar.edu.unq.epersgeist.persistencia.dao.*;
import ar.edu.unq.epersgeist.persistencia.repositories.interfaces.MediumRepository;
import ar.edu.unq.epersgeist.persistencia.repositories.interfaces.UbicacionRepository;
import ar.edu.unq.epersgeist.servicios.MediumService;
import ar.edu.unq.epersgeist.servicios.exception.*;
import jakarta.transaction.Transactional;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.stereotype.Service;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MediumServiceImpl implements MediumService {
    public final MediumDAO mediumDAO;
    public final MediumDAOMongo mediumDAOMongo;
    public final EspirituDAO espirituDAO;
    private final UbicacionRepository ubicacionRepository;
    private final MediumRepository mediumRepository;

    public MediumServiceImpl(MediumRepository mediumRepository, MediumDAO mediumDAO, MediumDAOMongo mediumDAOMongo, EspirituDAO espirituDAO, UbicacionRepository ubicacionRepository) {
        this.mediumDAO = mediumDAO;
        this.mediumDAOMongo = mediumDAOMongo;
        this.espirituDAO = espirituDAO;
        this.ubicacionRepository = ubicacionRepository;
        this.mediumRepository = mediumRepository;
    }

    @Override
    public void crear(Medium medium) {
        mediumRepository.crear(medium);
    }

    @Override
    public Medium recuperar(Long id) {
        validacionesGenerales.revisarId(id);
        Medium medium = mediumRepository.recuperar(id);
        validacionesGenerales.revisarEntidadEliminado(medium.getDeleted(),medium);

        return medium;
    }

    @Override
    public Collection<Medium> recuperarTodos() {
        return mediumDAO.recuperarTodosNoEliminados();
    }

    @Override
    public void eliminar(Medium medium) {
        if (!mediumDAO.existsById(medium.getId())) {
            throw new RecursoNoEncontradoException("Medium con ID " + medium.getId() + " no encontrado");
        }
        validacionesGenerales.revisarEntidadEliminado(medium.getDeleted(),medium);
        validacionesGenerales.revisarUbicacionConEntidades(medium,!espiritus(medium.getId()).isEmpty());
        medium.setDeleted(true);
        mediumDAO.save(medium);
    }

    @Override
    public void eliminarTodo() {
        mediumDAO.deleteAll();
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
        
        List<Espiritu> angeles = espirituDAO.recuperarEspiritusDeTipo(medium.getId(), Angel.class);
        List<Espiritu> demonios = espirituDAO.recuperarEspiritusDeTipo(medium2.getId(), Demonio.class);
        medium.exorcizar(medium2, angeles, demonios);
        mediumRepository.actualizar(medium);
        mediumRepository.actualizar(medium2);
    }

    @Override
    public Optional<Espiritu> invocar(Long mediumId, Long espirituId) {
        validacionesGenerales.revisarId(mediumId);
        validacionesGenerales.revisarId(espirituId);
        
        Medium medium = mediumRepository.recuperar(mediumId);
        Espiritu espiritu = espirituDAO.findById(espirituId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Espiritu con ID " + espirituId + " no encontrada"));

        validacionesGenerales.revisarEntidadEliminado(medium.getDeleted(),medium);
        validacionesGenerales.revisarEntidadEliminado(espiritu.getDeleted(),espiritu);
        validacionesGenerales.revisarUbicacionNoNula(medium.getUbicacion(),medium,mediumId);
        
        medium.invocar(espiritu);
        mediumRepository.actualizar(medium);
        return espirituDAO.findById(espirituId);
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
        MediumMongo mediumMongo = mediumRepository.recuperarPorIdSQL(mediumId);

        GeoJsonPoint coordenadaDestino = new GeoJsonPoint(latitud, longitud);

        UbicacionMongo ubicacionMongo = ubicacionRepository.recuperarPorCoordenada(coordenadaDestino);
        Ubicacion ubicacion = ubicacionRepository.recupoerarPorNombre(ubicacionMongo.getNombre());

        if (medium.getUbicacion() != null && medium.getUbicacion().getId().equals(ubicacion.getId())) throw new MovimientoInvalidoException();
        if (!ubicacionRepository.estanConectadasDirecta(medium.getUbicacion().getNombre(), ubicacion.getNombre())) {
            throw new UbicacionLejanaException();
        }

        medium.moverseA(ubicacion);
        mediumMongo.moverseA(coordenadaDestino);

        mediumRepository.actualizar(medium);
    }
}