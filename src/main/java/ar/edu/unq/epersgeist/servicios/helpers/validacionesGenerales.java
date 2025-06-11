package ar.edu.unq.epersgeist.servicios.helpers;

import ar.edu.unq.epersgeist.controller.excepciones.EntidadSinUbicacionException;
import ar.edu.unq.epersgeist.modelo.Ubicacion;
import ar.edu.unq.epersgeist.servicios.exception.EntidadConEntidadesConectadasException;
import ar.edu.unq.epersgeist.servicios.exception.EntidadEliminadaException;
import ar.edu.unq.epersgeist.servicios.exception.IdNoValidoException;

public class validacionesGenerales {

    public static <T> void revisarEntidadEliminado(Boolean condicion, T entidad) {
        if(condicion){
            throw new EntidadEliminadaException(entidad);
        }
    }
    public static <T> void revisarUbicacionNoNula(Ubicacion ubicacion, T entidad, Long id) {
        if(ubicacion == null){
            throw new EntidadSinUbicacionException(entidad,id);
        }
    }
    public static void revisarId(Long id){
        if (id == null || id <= 0) {
            throw new IdNoValidoException();
        }
    }

    public static <T> void revisarUbicacionConEntidades(T entidad,Boolean condicion){
        if (condicion){
            throw new EntidadConEntidadesConectadasException(entidad);
        }
    }
}
