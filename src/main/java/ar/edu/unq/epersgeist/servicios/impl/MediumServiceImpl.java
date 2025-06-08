package ar.edu.unq.epersgeist.servicios.impl;

import ar.edu.unq.epersgeist.controller.excepciones.*;
import ar.edu.unq.epersgeist.modelo.*;
import ar.edu.unq.epersgeist.modelo.exception.UbicacionLejanaException;
import ar.edu.unq.epersgeist.persistencia.dao.CoordenadaDAOMongo;
import ar.edu.unq.epersgeist.persistencia.dao.EspirituDAO;
import ar.edu.unq.epersgeist.persistencia.dao.MediumDAO;
import ar.edu.unq.epersgeist.persistencia.repositories.interfaces.EspirituRepository;
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
    public final EspirituRepository espirituRepository;
    private final UbicacionRepository ubicacionRepository;
    private final MediumRepository mediumRepository;
    private final EspirituDAO espirituDAO;
    private final MediumDAO mediumDAO;
    private final CoordenadaDAOMongo coordenadaDAOMongo;

    public MediumServiceImpl(MediumRepository mediumRepository, EspirituRepository espirituRepository, UbicacionRepository ubicacionRepository, EspirituDAO espirituDAO, MediumDAO mediumDAO, CoordenadaDAOMongo coordenadaDAOMongo) {
        this.espirituRepository = espirituRepository;
        this.ubicacionRepository = ubicacionRepository;
        this.mediumRepository = mediumRepository;
        this.espirituDAO = espirituDAO;
        this.mediumDAO = mediumDAO;
        this.coordenadaDAOMongo = coordenadaDAOMongo;
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
    public MediumMongo recuperarMongo(Long id) {
        return mediumRepository.recuperarPorIdSQL(id);
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

        Medium medium = mediumDAO.findById(mediumId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Medium con ID " + mediumId + " no encontrada"));
        Espiritu espiritu = espirituDAO.findById(espirituId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Espiritu con ID " + espirituId + " no encontrada"));

        validacionesGenerales.revisarEntidadEliminado(medium.getDeleted(),medium);
        validacionesGenerales.revisarEntidadEliminado(espiritu.getDeleted(),espiritu);
        validacionesGenerales.revisarUbicacionNoNula(medium.getUbicacion(),medium,mediumId);

        Optional <CoordenadaMongo> coordenadaMedium = coordenadaDAOMongo.findByEntityIdAndEntityType(mediumId, medium.getClass().toString());
        Optional <CoordenadaMongo> coordenadaEspiritu = coordenadaDAOMongo.findByEntityIdAndEntityType(espirituId, espiritu.getTipo().toString());

        if (coordenadaMedium.isEmpty() || coordenadaEspiritu.isEmpty()) {
            throw new RecursoNoEncontradoException();
        }

        if (! this.estaEnRango(coordenadaEspiritu.get(), medium)) {
            throw new FueraDeRangoDistanciaException();
        }

        medium.invocar(espiritu);
        coordenadaEspiritu.get().setPunto(coordenadaMedium.get().getPunto());

        mediumDAO.save(medium);
        coordenadaDAOMongo.save(coordenadaEspiritu.get());
        return espirituDAO.findById(espirituId).get();
    }

    private boolean estaEnRango(CoordenadaMongo coordenadaEspiritu, Medium medium) {
        Double latitud = coordenadaEspiritu.getLatitud();
        Double longitud = coordenadaEspiritu.getLongitud();

        Optional<CoordenadaMongo> coordenadaEnRango = coordenadaDAOMongo.findCoordenadaEnRangoInvocar(
                longitud, latitud, medium.getId(), medium.getClass().toString());

        return coordenadaEnRango.isPresent();
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

        GeoJsonPoint destino = new GeoJsonPoint(longitud, latitud);

        AreaMongo ubicacionMongo = ubicacionRepository.recuperarPorCoordenada(destino);
        Ubicacion ubicacion = ubicacionRepository.recuperar(ubicacionMongo.getIdUbicacion());

        if (medium.getUbicacion() != null && medium.getUbicacion().getId().equals(ubicacion.getId())) throw new MovimientoInvalidoException();
        if (!ubicacionRepository.estanConectadasDirecta(medium.getUbicacion(), ubicacion)
           ||
                (!mediumRepository.estaEnRango30KM(medium.getId(), longitud, latitud)) && mediumRepository.yaTieneCoordenadas(medium)){
            throw new UbicacionLejanaException();
        }

        medium.moverseA(ubicacion);
        mediumRepository.actualizarCoordenadas(medium, destino);
        espirituRepository.actualizarCoordenadasDe(mediumRepository.obtenerEspiritus(mediumId), destino);

        mediumRepository.actualizar(medium);
    }
}