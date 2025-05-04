package ar.edu.unq.epersgeist.controller.dto;

import ar.edu.unq.epersgeist.modelo.*;

public record UbicacionDTO(Long id, String nombre, String tipoDeUbicacion, Integer flujoDeEnergia) {


    public static UbicacionDTO desdeModelo(Ubicacion ubicacion) {
        return new UbicacionDTO(
                ubicacion.getId(),
                ubicacion.getNombre(),
                ubicacion.getTipo(),
                ubicacion.getFlujoEnergia()
        );
    }

    public Ubicacion aModelo(){
        Ubicacion ubicacion;
        if (this.tipoDeUbicacion.equals("SANTUARIO")){
            ubicacion = new Santuario(this.nombre, this.flujoDeEnergia);
        } else {
            ubicacion = new Cementerio(this.nombre, this.flujoDeEnergia);
        }
        ubicacion.setId(this.id);
        return ubicacion;
    }
}
