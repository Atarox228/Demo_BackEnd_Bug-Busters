package ar.edu.unq.epersgeist.servicios.impl;

import ar.edu.unq.epersgeist.modelo.Espiritu;
import ar.edu.unq.epersgeist.modelo.Medium;
import ar.edu.unq.epersgeist.persistencia.dao.jdbc.JDBCEspirituDAO;
import ar.edu.unq.epersgeist.servicios.EspirituService;

import java.util.List;

public class EspirituServiceImpl implements EspirituService {

    private final JDBCEspirituDAO jDBCEspirituDao;

    public EspirituServiceImpl(){
        this.jDBCEspirituDao = new JDBCEspirituDAO();
    }

    @Override
    public Espiritu crear(Espiritu espiritu) {
        return jDBCEspirituDao.crear(espiritu);
    }

    @Override
    public Espiritu recuperar(Long espirituId) {
        return jDBCEspirituDao.recuperar(espirituId);
    }

    @Override
    public List<Espiritu> recuperarTodos() {

        return jDBCEspirituDao.recuperarTodos();
    }

    @Override
    public void actualizar(Espiritu espiritu) {
        jDBCEspirituDao.actualizar(espiritu);
    }

    @Override
    public void eliminar(Long espirituId) {
        jDBCEspirituDao.eliminar(espirituId);
    }

    @Override
    public Medium conectar(Long espirituId, Medium medium) {
        Espiritu espi = this.recuperar(espirituId);
        medium.conectarseAEspiritu(espi);
        this.actualizar(espi);
        return medium;
    }

}
