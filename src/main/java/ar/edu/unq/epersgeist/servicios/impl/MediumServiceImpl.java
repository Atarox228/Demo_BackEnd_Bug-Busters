package ar.edu.unq.epersgeist.servicios.impl;

import ar.edu.unq.epersgeist.modelo.Medium;
import ar.edu.unq.epersgeist.persistencia.dao.MediumDAO;
import ar.edu.unq.epersgeist.servicios.MediumService;

import java.util.Collection;
import java.util.List;

public class MediumServiceImpl implements MediumService {

    private MediumDAO dao;

    public MediumServiceImpl(MediumDAO dao) {
        this.dao = dao;
    }

    public void guardar(Medium medium) {
        dao.guardar(medium);
    }

    public Medium recuperar(Long id) {
        return dao.recuperar(id);
    }

    @Override
    public Collection<Medium> recuperarTodos() {
        return dao.recuperarTodos();
    }

    public void eliminar(Medium medium) {
        dao.eliminar(medium);
    }
}
