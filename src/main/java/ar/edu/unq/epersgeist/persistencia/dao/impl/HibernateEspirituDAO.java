package ar.edu.unq.epersgeist.persistencia.dao.impl;

import ar.edu.unq.epersgeist.servicios.enums.Direccion;
import ar.edu.unq.epersgeist.modelo.Espiritu;
import ar.edu.unq.epersgeist.modelo.TipoEspiritu;
import ar.edu.unq.epersgeist.persistencia.dao.EspirituDAO;
import ar.edu.unq.epersgeist.servicios.runner.HibernateTransactionRunner;
import org.hibernate.Session;
import org.hibernate.query.Query;
import java.util.List;
import java.util.Set;

public class HibernateEspirituDAO extends HibernateDAO<Espiritu> implements EspirituDAO {

    public HibernateEspirituDAO(){
        super(Espiritu.class);
    }

    @Override
    public List<Espiritu> todosLosEspiritusDeTipo(TipoEspiritu tipoEspiritu) {
        Session session = HibernateTransactionRunner.getCurrentSession();
        String hql = "from Espiritu e where e.tipo = :tipoDeEspiritu order by e.nivelConexion desc";
        Query<Espiritu> query = session.createQuery(hql, Espiritu.class);
        query.setParameter("tipoDeEspiritu", tipoEspiritu);
        return query.getResultList();
    }

    @Override
    public List<Espiritu> obtenerEspiritus(Direccion direccion, Integer pagina, Integer cantidad, TipoEspiritu tipoEspiritu) {
        Session session = HibernateTransactionRunner.getCurrentSession();
        String hql = "from Espiritu e where e.tipo = :tipoDeEspiritu order by e.nivelConexion " + direccion.getOrden();
        Query<Espiritu> query = session.createQuery(hql, Espiritu.class);
        query.setParameter("tipoDeEspiritu", tipoEspiritu);
        query.setFirstResult((pagina - 1) * cantidad);
        query.setMaxResults(cantidad);
        return query.getResultList();
    }

    @Override
    public List<Espiritu> espiritusEn(Long ubicacionId) {
        Session session = HibernateTransactionRunner.getCurrentSession();
        String hql = "from Espiritu e where e.ubicacion.id = :ubicacionEspiritu";
        Query<Espiritu> query = session.createQuery(hql, Espiritu.class);
        query.setParameter("ubicacionEspiritu", ubicacionId);
        return query.getResultList();
    }

    @Override
    public void actualizarEspiritus(List<Espiritu> espiritus) {
        while (espiritus.iterator().hasNext()) {
            Espiritu actualizado = espiritus.getFirst();
            this.actualizar(actualizado);
            espiritus.remove(actualizado);
        }
    }

    @Override
    public List<Espiritu> recuperarEspirtusDeTipo(Long id, TipoEspiritu tipo) {
        Session session = HibernateTransactionRunner.getCurrentSession();
        String hql = "from Espiritu e where e.medium.id = :id AND e.tipo = :tipoDeEspiritu";
        Query<Espiritu> query = session.createQuery(hql, Espiritu.class);
        query.setParameter("tipoDeEspiritu", tipo);
        query.setParameter("id",id);
        return query.getResultList();
    }




    public List<Espiritu> recuperarTodos(){
        Session session = HibernateTransactionRunner.getCurrentSession();
        String hql = "from Espiritu e order by e.nombre asc";
        Query<Espiritu> query = session.createQuery(hql, Espiritu.class);
        return query.getResultList();
    }

}
