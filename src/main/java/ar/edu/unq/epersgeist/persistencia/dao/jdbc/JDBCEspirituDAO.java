package ar.edu.unq.epersgeist.persistencia.dao.jdbc;

import ar.edu.unq.epersgeist.modelo.Espiritu;
import ar.edu.unq.epersgeist.persistencia.dao.EspirituDAO;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public record JDBCEspirituDAO() implements EspirituDAO {

    public Espiritu crear(Espiritu espiritu) {
        // TODO completar
        return null;
    }

    public Espiritu recuperar(Long idDelEspiritu) {
        // TODO completar
        return null;
    }

    public List<Espiritu> recuperarTodos() {
        // TODO completar
        return null;
    }

    public void actualizar(Espiritu espiritu) {
        // TODO completar
    }

    public void eliminar(Long idDelEspiritu) {
        // TODO completar
    }

    public JDBCEspirituDAO() {
        try {
            var uri = getClass().getClassLoader().getResource("createAll.sql").toURI();
            var initializeScript = Files.readString(Paths.get(uri));
            JDBCConnector.getInstance().execute(conn -> {
                try {
                    var ps = conn.prepareStatement(initializeScript);
                    return ps.execute();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}