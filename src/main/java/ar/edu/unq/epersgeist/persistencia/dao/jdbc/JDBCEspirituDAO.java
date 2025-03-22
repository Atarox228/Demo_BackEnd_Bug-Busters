package ar.edu.unq.epersgeist.persistencia.dao.jdbc;

import ar.edu.unq.epersgeist.modelo.Espiritu;
import ar.edu.unq.epersgeist.persistencia.dao.EspirituDAO;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;

public record JDBCEspirituDAO() implements EspirituDAO {


    public Espiritu crear(Espiritu espiritu) {
        JDBCConnector.getInstance().execute(conn  -> {
            try {
                var ps = conn.prepareStatement("INSERT INTO espiritu (tipo, nivelConexion, nombre) VALUES (?,?,?)");
                ps.setString(1, espiritu.getTipo());
                ps.setInt(2, espiritu.getNivelDeConexion());
                ps.setString(3, espiritu.getNombre());
                return ps.execute();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        return recuperarEspirituPorNombre(espiritu.getNombre());
    }

    public Espiritu recuperar(Long idDelEspiritu) {
        // TODO completar
        return null;
    }

    public Espiritu recuperarEspirituPorNombre(String nombre) {
        return JDBCConnector.getInstance().execute( conn -> {
            try {
                var ps = conn.prepareStatement("SELECT tipo, nivelConexion FROM espiritu WHERE nombre = ?");
                ps.setString(1, nombre);
                var resultSet = ps.executeQuery();
                Espiritu espiritu = null;
                while (resultSet.next()) {
                    if (espiritu != null) {
                        throw new RuntimeException(String.format("Existe mas de un personaje con el nombre %s", nombre));
                    }
                    espiritu = new Espiritu(
                            resultSet.getString("tipo"),
                            resultSet.getInt("nivelConexion"),
                            nombre
                    );
                }
                return espiritu;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
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