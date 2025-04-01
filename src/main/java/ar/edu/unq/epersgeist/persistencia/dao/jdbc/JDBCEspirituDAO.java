package ar.edu.unq.epersgeist.persistencia.dao.jdbc;

import ar.edu.unq.epersgeist.modelo.Espiritu;
import ar.edu.unq.epersgeist.persistencia.dao.EspirituDAO;
import ar.edu.unq.epersgeist.persistencia.dao.jdbc.exception.RecuperarException;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.*;

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
                throw new RuntimeException("Ya existe un espiritu con el mismo nombre");
            }
        });
        return recuperarEspirituPorNombre(espiritu.getNombre());
    }

    public Espiritu recuperarEspirituPorNombre(String nombre) {
        return JDBCConnector.getInstance().execute( conn -> {
            try {
                var ps = conn.prepareStatement("SELECT id, tipo, nivelConexion FROM espiritu WHERE nombre = ?");
                ps.setString(1, nombre);
                var resultSet = ps.executeQuery();
                Espiritu espiritu = null;
                while (resultSet.next()) {
                    if (espiritu != null) {
                        throw new RuntimeException(String.format("Existe mas de un espiritu con el nombre %s", nombre));
                    }
                    espiritu = new Espiritu(
                            resultSet.getLong("id"),
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

    public Espiritu recuperar(Long idDelEspiritu) {
        return JDBCConnector.getInstance().execute( conn -> {
            try {
                var ps = conn.prepareStatement("SELECT tipo, nivelConexion, nombre FROM espiritu WHERE id = ?");
                ps.setLong(1, idDelEspiritu);
                var resultSet = ps.executeQuery();
                Espiritu espiritu = null;
                while (resultSet.next()) {
                    espiritu = new Espiritu(
                            idDelEspiritu,
                            resultSet.getString("tipo"),
                            resultSet.getInt("nivelConexion"),
                            resultSet.getString("nombre")
                    );
                }
                if (espiritu == null) {
                    throw new RuntimeException("No se encontro el espiritu");
                }
                return espiritu;
            } catch (SQLException e) {
                throw new RuntimeException("No se encontro el espiritu");
            }
        });
    }



    public List<Espiritu> recuperarTodos() {
        return JDBCConnector.getInstance().execute(conn -> {
            try {
                var ps = conn.prepareStatement("SELECT * FROM espiritu ORDER BY nombre ASC");
                var resultSet = ps.executeQuery();
                Espiritu espiritu = null;
                List<Espiritu> espiritus = new ArrayList<>();
                while (resultSet.next()) {

                    espiritu = new Espiritu(
                            resultSet.getLong("id"),
                            resultSet.getString("tipo"),
                            resultSet.getInt("nivelConexion"),
                            resultSet.getString("nombre")
                    );
                    espiritus.add(espiritu);
                }
                return espiritus;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void actualizar(Espiritu espiritu) {
        JDBCConnector.getInstance().execute(conn  -> {
            try {
                if (espiritu.getId() == null) {
                    throw new RuntimeException("Espiritu sin id asignada");
                }
                var ps = conn.prepareStatement("UPDATE espiritu SET tipo = ?, nivelconexion = ?, nombre = ? WHERE id = ?");
                ps.setString(1, espiritu.getTipo());
                ps.setInt(2, espiritu.getNivelDeConexion());
                ps.setString(3, espiritu.getNombre());
                ps.setLong(4, espiritu.getId());

                return ps.execute();
            } catch (SQLException e) {
                throw new RuntimeException("Espiritu no encontrado");
            }
        });

    }

    public void eliminar(Long idDelEspiritu) {
        JDBCConnector.getInstance().execute (conn -> {
            try {
                var ps = conn.prepareStatement("DELETE FROM espiritu WHERE id = ? ");
                ps.setLong(1, idDelEspiritu);
                return ps.execute();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
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