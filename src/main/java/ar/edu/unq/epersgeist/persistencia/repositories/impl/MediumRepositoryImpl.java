package ar.edu.unq.epersgeist.persistencia.repositories.impl;
import ar.edu.unq.epersgeist.controller.excepciones.RecursoNoEncontradoException;
import ar.edu.unq.epersgeist.modelo.*;
import ar.edu.unq.epersgeist.persistencia.dao.*;
import ar.edu.unq.epersgeist.persistencia.repositories.interfaces.MediumRepository;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.stereotype.Repository;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public class MediumRepositoryImpl implements MediumRepository {

    private final MediumDAO mediumDAO;
    private final MediumDAOMongo mediumDAOMongo;
    private final CoordenadaDAOMongo coordenadaDAOMongo;
    private final EspirituRepositoryImpl espirituRepositoryImpl;

    public MediumRepositoryImpl(MediumDAO mediumDAO, MediumDAOMongo mediumDAOMongo, CoordenadaDAOMongo coordenadaDAOMongo, EspirituRepositoryImpl espirituRepositoryImpl) {
        this.mediumDAO = mediumDAO;
        this.mediumDAOMongo = mediumDAOMongo;
        this.coordenadaDAOMongo = coordenadaDAOMongo;
        this.espirituRepositoryImpl = espirituRepositoryImpl;
    }

    @Override
    public void crear(Medium medium){
        Medium mediumGuardado = mediumDAO.save(medium);
        MediumMongo mediumMongo = new MediumMongo(mediumGuardado.getId());
        mediumDAOMongo.save(mediumMongo);
    }

    @Override
    public void actualizar(Medium medium) {
        mediumDAO.save(medium);
    }

    @Override
    public void actualizarCoordenadas(Medium medium, GeoJsonPoint punto) {
        Optional<CoordenadaMongo> coordenada = coordenadaDAOMongo.findByEntityIdAndEntityType(medium.getId(), medium.getClass().toString());
        if (coordenada.isPresent()) {
            CoordenadaMongo coordenadaMongo = coordenada.get();
            coordenadaMongo.setPunto(punto);
            coordenadaDAOMongo.save(coordenadaMongo);
        } else {
            CoordenadaMongo coordenadaNueva = new CoordenadaMongo(punto, medium.getClass().toString(), medium.getId());
            coordenadaDAOMongo.save(coordenadaNueva);
        }
    }

    @Override
    public void eliminar(Medium medium) {
        MediumMongo mediumMongo = mediumDAOMongo.findByMediumIdSQL(medium.getId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Medium con id SQL " + medium.getId() + " no encontrada"));
        mediumDAOMongo.delete(mediumMongo);
    }

    @Override
    public void eliminarTodos() {
        mediumDAO.deleteAll();
        mediumDAOMongo.deleteAll();
    }

    @Override
    public boolean existsById(Long id) {
        return mediumDAO.existsById(id);
    }

    @Override
    public Medium recuperar(long idMedium) {
        return mediumDAO.findById(idMedium)
                .orElseThrow(() -> new RecursoNoEncontradoException("Medium con id " + idMedium + " no encontrada"));
    }

    @Override
    public MediumMongo recuperarPorIdSQL(Long idMedium) {
        return mediumDAOMongo.findByMediumIdSQL(idMedium)
                .orElseThrow(() -> new RecursoNoEncontradoException("Medium con id SQL" + idMedium + " no encontrada"));
    }

    @Override
    public List<Espiritu> obtenerEspiritus(Long mediumId) {
        return mediumDAO.obtenerEspiritus(mediumId);
    }

    @Override
    public Collection<Medium> recuperarTodosNoEliminados() {
        return mediumDAO.recuperarTodosNoEliminados();
    }


    @Override
    public boolean estaEnRango30KM(Long id, Double longitud, Double latitud) {
        Optional<CoordenadaMongo> coordenadasDeMedium = coordenadaDAOMongo.findMediumEntreLosKms(longitud, latitud, id, 30000D);
        return !coordenadasDeMedium.isEmpty();
    }

    @Override
    public boolean yaTieneCoordenadas(Medium medium) {
        Optional<CoordenadaMongo> coordenadasDeMedium = coordenadaDAOMongo.findByEntityIdAndEntityType(medium.getId(), medium.getClass().toString());
        return !coordenadasDeMedium.isEmpty();
    }
}
