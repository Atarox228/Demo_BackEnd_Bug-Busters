package ar.edu.unq.epersgeist.persistencia.repositories.impl;
import ar.edu.unq.epersgeist.controller.excepciones.RecursoNoEncontradoException;
import ar.edu.unq.epersgeist.modelo.*;
import ar.edu.unq.epersgeist.persistencia.dao.*;
import ar.edu.unq.epersgeist.persistencia.repositories.interfaces.MediumRepository;
import org.springframework.stereotype.Repository;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public class MediumRepositoryImpl implements MediumRepository {

    private final MediumDAO mediumDAO;
    private final CoordenadaDAOMongo coordenadaDAOMongo;
    private final EspirituRepositoryImpl espirituRepositoryImpl;

    public MediumRepositoryImpl(MediumDAO mediumDAO, CoordenadaDAOMongo coordenadaDAOMongo, EspirituRepositoryImpl espirituRepositoryImpl) {
        this.mediumDAO = mediumDAO;
        this.coordenadaDAOMongo = coordenadaDAOMongo;
        this.espirituRepositoryImpl = espirituRepositoryImpl;
    }

    @Override
    public void crear(Medium medium){
        mediumDAO.save(medium);
    }

    @Override
    public void actualizar(Medium medium) {
        mediumDAO.save(medium);
    }

//    @Override
//    public void eliminar(Medium medium) {
//        MediumMongo mediumMongo = mediumDAOMongo.findByMediumIdSQL(medium.getId())
//                .orElseThrow(() -> new RecursoNoEncontradoException("Medium con id SQL " + medium.getId() + " no encontrada"));
//        mediumDAOMongo.delete(mediumMongo);
//    }

    @Override
    public void eliminarTodos() {
        mediumDAO.deleteAll();
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
    public List<Espiritu> obtenerEspiritus(Long mediumId) {
        return mediumDAO.obtenerEspiritus(mediumId);
    }

    @Override
    public Collection<Medium> recuperarTodosNoEliminados() {
        return mediumDAO.recuperarTodosNoEliminados();
    }


    @Override
    public boolean estaEnRango30KM(Long id, Double longitud, Double latitud) {
        Optional<CoordenadaMongo> coordenadasDeMedium = coordenadaDAOMongo.findCercana("MEDIUM", id, longitud,  latitud, 30000D);
        return !coordenadasDeMedium.isEmpty();
    }
}
