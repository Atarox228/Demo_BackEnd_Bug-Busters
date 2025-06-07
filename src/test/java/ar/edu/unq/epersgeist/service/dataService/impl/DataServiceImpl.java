package ar.edu.unq.epersgeist.service.dataService.impl;

import ar.edu.unq.epersgeist.persistencia.dao.CoordenadaDAOMongo;
import ar.edu.unq.epersgeist.persistencia.dao.EspirituDAO;
import ar.edu.unq.epersgeist.persistencia.dao.MediumDAO;
import ar.edu.unq.epersgeist.persistencia.dao.MediumDAOMongo;
import ar.edu.unq.epersgeist.persistencia.repositories.interfaces.EspirituRepository;
import ar.edu.unq.epersgeist.persistencia.repositories.interfaces.MediumRepository;
import ar.edu.unq.epersgeist.persistencia.repositories.interfaces.UbicacionRepository;
import ar.edu.unq.epersgeist.service.dataService.DataService;
import ar.edu.unq.epersgeist.servicios.exception.IdNoValidoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DataServiceImpl implements DataService {

    private final MediumDAO mediumDAO;
    private final EspirituRepository espirituRepository;
    private final UbicacionRepository ubicacionRepository;
    private final CoordenadaDAOMongo coordenadaDAOMongo;

    public DataServiceImpl (EspirituRepository espirituRepository, MediumDAO mediumDAO, UbicacionRepository ubicacionRepository, CoordenadaDAOMongo coordenadaDAOMongo) {
        this.espirituRepository = espirituRepository;
        this.mediumDAO = mediumDAO;
        this.ubicacionRepository = ubicacionRepository;
        this.coordenadaDAOMongo = coordenadaDAOMongo;
    }

    public void eliminarTodo(){
            espirituRepository.eliminarTodos();
            mediumDAO.deleteAll();
            ubicacionRepository.eliminarTodos();
            coordenadaDAOMongo.deleteAll();

    }

    public void revisarId(Long id) {
        if (id == null || id <= 0) {
            throw new IdNoValidoException();
        }
    }
}
