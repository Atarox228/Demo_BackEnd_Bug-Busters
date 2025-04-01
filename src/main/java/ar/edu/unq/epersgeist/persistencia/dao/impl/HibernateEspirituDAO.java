package ar.edu.unq.epersgeist.persistencia.dao.impl;

import ar.edu.unq.epersgeist.modelo.Espiritu;
import ar.edu.unq.epersgeist.persistencia.dao.EspirituDAO;
import ar.edu.unq.epersgeist.servicios.runner.HibernateTransactionRunner;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class HibernateEspirituDAO extends HibernateDAO<Espiritu> implements EspirituDAO {

    public HibernateEspirituDAO(){
        super(Espiritu.class);
    }

    public void actualizar(Espiritu espiritu){
        Session session = HibernateTransactionRunner.getCurrentSession();
        String hql = "UPDATE Espiritu e SET e.tipo = :espTipo , e.nombre = :espNombre, e.nivelConexion = :espNivelConexion where e.id = :espId";
        // UPDATE Entidad e SET e.campo1 = :param1, e.campo2 = :param2 WHERE e.id = :paramId
        Query<Espiritu> query = session.createQuery(hql);
        query.setParameter("espTipo", espiritu.getTipo());
        query.setParameter("espNombre", espiritu.getNombre());
        query.setParameter("espNivelConexion", espiritu.getNivelDeConexion());
        query.setParameter("espId", espiritu.getId());
        query.executeUpdate();
    }

    public List<Espiritu> recuperarTodos(){
        Session session = HibernateTransactionRunner.getCurrentSession();
        String hql = "from Espiritu e order by e.nombre asc";
        Query<Espiritu> query = session.createQuery(hql, Espiritu.class);
        return query.getResultList();
    }

}
