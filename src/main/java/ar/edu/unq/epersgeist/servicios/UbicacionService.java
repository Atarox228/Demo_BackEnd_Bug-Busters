package ar.edu.unq.epersgeist.servicios;

import ar.edu.unq.epersgeist.modelo.*;
import ar.edu.unq.epersgeist.modelo.enums.DegreeType;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UbicacionService {
    void crear(Ubicacion ubicacion);
    Optional<Ubicacion> recuperar(Long ubicacionId);
    UbicacionNeo4J recuperarPorNombre(String nombre);
    void eliminar(Ubicacion ubicacion);
    void actualizar(Ubicacion ubicacion,String nombreViejo);
    Collection<Ubicacion> recuperarTodos();
    List<Espiritu> espiritusEn(Long ubicacionId);
    List<Medium> mediumsSinEspiritusEn(Long ubicacionId);
    void clearAll();
    Optional<Ubicacion> recuperarAunConSoftDelete(Long ubicacionId);
    void conectar (Long idOrigen, Long idDestino);
    List<UbicacionNeo4J> ubicacionesSobrecargadas(Integer umbralDeEnergia);
    Boolean estanConectadas(Long idOrigen, Long idDestino);
    List<UbicacionNeo4J> caminoMasCorto(Long idOrigen, Long idDestino);

    List<ClosenessResult> closenessOf(List<Long> ids);
    DegreeResult degreeOf(List<Long> ids, DegreeType type);
}
