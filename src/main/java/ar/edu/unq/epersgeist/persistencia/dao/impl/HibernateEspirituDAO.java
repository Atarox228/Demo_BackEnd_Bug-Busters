package ar.edu.unq.epersgeist.persistencia.dao.impl;

import ar.edu.unq.epersgeist.modelo.Demonio;
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
    public List<Espiritu> obtenerDemonios(Direccion direccion, Integer pagina, Integer cantidad) {
        Session session = HibernateTransactionRunner.getCurrentSession();
        String hql = "from Espiritu e where type(e) = :tipoDeEspiritu order by e.nivelConexion " + direccion.getOrden();
        Query<Espiritu> query = session.createQuery(hql, Espiritu.class);
        query.setParameter("tipoDeEspiritu", Demonio.class);
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
    public List<Espiritu> recuperarEspiritusDeTipo(Long id, Class<? extends Espiritu> tipo) {
        Session session = HibernateTransactionRunner.getCurrentSession();
        String hql = "from Espiritu e where e.medium.id = :id AND type(e) = :tipoDeEspiritu";
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
