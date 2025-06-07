package ar.edu.unq.epersgeist.persistencia.repositories.interfaces;

import ar.edu.unq.epersgeist.modelo.*;
import ar.edu.unq.epersgeist.modelo.enums.DegreeType;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon;
import java.util.Collection;
import java.util.List;

public interface UbicacionRepository {

    void crear(Ubicacion ubicacion, GeoJsonPolygon area);
    Ubicacion recuperar(Long ubicacionId);
    Ubicacion recupoerarPorNombre (String nombre);
    UbicacionNeo4J findByNombre(String nombre);
    void actualizar(Ubicacion ubicacion);
    void actualizarNeo4J(Ubicacion ubicacion,String nombreViejo);
    void eliminar(Ubicacion ubicacion);
    Collection<Ubicacion> recuperarTodos();
    boolean existsById(Long id);
    void eliminarTodos();
    Ubicacion existeUbicacionConNombre(String nombre);
    void conectarUbicaciones(String origen, String destino);
    List<UbicacionNeo4J> ubicacionesSobrecargadas(Integer umbralDeEnergia);
    Boolean estanConectadasDirecta(String origen, String destino);
    List<UbicacionNeo4J> encontrarCaminoMasCorto(String origen, String destino);
    ClosenessResult definirCentralidad(String nombre);
    double relationships();
    DegreeQuery DegreeOf(List<String> names, DegreeType type);
    List<String> namesOf(List<Long> ids);
    List<AreaMongo> recuperarPorInterseccion(GeoJsonPolygon poligono);
    AreaMongo recuperarPorCoordenada(Point coordenada);
}
