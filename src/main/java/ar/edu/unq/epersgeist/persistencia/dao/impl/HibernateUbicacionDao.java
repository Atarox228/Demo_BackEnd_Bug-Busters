package ar.edu.unq.epersgeist.persistencia.dao.impl;

import ar.edu.unq.epersgeist.modelo.Ubicacion;
import ar.edu.unq.epersgeist.persistencia.dao.UbicacionDAO;
import ar.edu.unq.epersgeist.servicios.runner.HibernateTransactionRunner;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.Collection;

public class HibernateUbicacionDao extends HibernateDAO<Ubicacion> implements UbicacionDAO {
    public HibernateUbicacionDao() {
        super(Ubicacion.class);
    }

    @Override
    public Collection<Ubicacion> recuperarTodos() {
        Session session = HibernateTransactionRunner.getCurrentSession();
        String hql = "select u from Ubicacion u order by u.nombre asc";
        Query<Ubicacion> query = session.createQuery(hql, Ubicacion.class);
        return query.getResultList();
    }
}
