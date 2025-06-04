package ar.edu.unq.epersgeist.persistencia.repositorys.interfaces;

import ar.edu.unq.epersgeist.modelo.*;
import ar.edu.unq.epersgeist.modelo.enums.DegreeType;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UbicacionRepository {

    void crear(Ubicacion ubicacion, List<Coordenada> area);
    Ubicacion recuperar(Long ubicacionId);
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
}
