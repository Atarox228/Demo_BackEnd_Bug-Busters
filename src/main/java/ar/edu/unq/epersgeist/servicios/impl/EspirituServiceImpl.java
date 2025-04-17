package ar.edu.unq.epersgeist.servicios.impl;

import ar.edu.unq.epersgeist.modelo.*;
import ar.edu.unq.epersgeist.persistencia.dao.EspirituDAO;
import ar.edu.unq.epersgeist.persistencia.dao.MediumDAO;
import ar.edu.unq.epersgeist.persistencia.dao.UbicacionDAO;
import ar.edu.unq.epersgeist.servicios.EspirituService;
import ar.edu.unq.epersgeist.servicios.enums.Direccion;
import ar.edu.unq.epersgeist.servicios.exception.IdNoValidoException;
import ar.edu.unq.epersgeist.servicios.exception.PaginaInvalidaException;
import ar.edu.unq.epersgeist.servicios.runner.HibernateTransactionRunner;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class EspirituServiceImpl implements EspirituService {

    private final EspirituDAO espirituDAO;
    private final MediumDAO mediumDAO;
    private final UbicacionDAO ubicacionDAO;

    public EspirituServiceImpl(EspirituDAO espirituDAO, MediumDAO mediumDao, UbicacionDAO ubicacionDAO) {
        this.espirituDAO = espirituDAO;
        this.mediumDAO = mediumDao;
        this.ubicacionDAO = ubicacionDAO;
    }

//    @Override
//    public void crear(Espiritu espiritu) {
//        HibernateTransactionRunner.runTrx(() -> {
//            espirituDAO.guardar(espiritu);
//            return null;
//        });
//    }

    @Override
    public void crear(Espiritu espiritu) {
        this.espirituDAO.save(espiritu);
    }

    @Override
    public Espiritu recuperar(Long espirituId) {
        return espirituDAO.findById(espirituId).orElseThrow(() -> new NoSuchElementException("Personaje not found with id: " + espirituId));
    }

    @Override
    public List<Espiritu> recuperarTodos() {
        List<Espiritu> espiritus = espirituDAO.findAll();
        return  espiritus;
    }

    @Override
    public void actualizar(Espiritu espiritu) {
        espirituDAO.save(espiritu);
    }

    @Override
    public void eliminar(Espiritu espiritu) {
        espirituDAO.deleteById(espiritu.getId());
    }

    public Medium conectar(Long espirituId, Long mediumId) {
        return HibernateTransactionRunner.runTrx(() -> {
            Espiritu espiritu = this.espirituDAO.recuperar(espirituId);
            Medium medium = this.mediumDAO.recuperar(mediumId);
            medium.conectarseAEspiritu(espiritu);
            this.espirituDAO.actualizar(espiritu);
            this.mediumDAO.actualizar(medium);
            return this.mediumDAO.recuperar(medium.getId());
        });
    }

    @Override
    public List<Espiritu> espiritusDemoniacos(Direccion direccion, Integer pagina, Integer cantidadPorPagina) {
        if (pagina < 0 || cantidadPorPagina < 0){
            throw new PaginaInvalidaException();
        }
        return HibernateTransactionRunner.runTrx(() -> espirituDAO.obtenerDemonios(direccion, pagina, cantidadPorPagina));
    }

    public void ubicarseEn(Long idEspiritu, Long idUbicacion) {
        HibernateTransactionRunner.runTrx(() -> {
            Espiritu espiritu = espirituDAO.recuperar(idEspiritu);
            Ubicacion ubicacion = ubicacionDAO.recuperar(idUbicacion);
            espiritu.setUbicacion(ubicacion);
            return null;
        });

    }
}
