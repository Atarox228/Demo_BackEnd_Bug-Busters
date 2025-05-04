package ar.edu.unq.epersgeist.controller.dto;

import ar.edu.unq.epersgeist.modelo.*

import java.util.Objects;

public record EspirituDTO(Long id, String nombre, String tipoDeEspiritu, Integer nivelDeConexion, UbicacionDTO ubicacion, MediumDTO medium,) {

    public static EspirituDTO desdeModelo(Espiritu espiritu){
        return new EspirituDTO(
                espiritu.getId(),
                espiritu.getNombre(),
                espiritu.getTipo().toString(),
                espiritu.getNivelConexion(),
                espiritu.getUbicacion() != null ? UbicacionDTO.desdeModelo(espiritu.getUbicacion()) : null,
                espiritu.getMedium() != null ? MediumDTO.desdeModeloNoRecursivo(espiritu.getMedium()) : null

        );
    }

    public Espiritu aModelo(Ubicacion ubicacion){
       Espiritu espiritu = this.aModelo();
       espiritu.setUbicacion(ubicacion);
       return espiritu;
    }
    public Espiritu aModelo(Medium medium){
        Espiritu espiritu = this.aModelo();
        espiritu.setMedium(medium);
        return espiritu;
    }
    public Espiritu aModelo(Ubicacion ubicacion, Medium medium){
        Espiritu espiritu = this.aModelo();
        espiritu.setUbicacion(ubicacion);
        espiritu.setMedium(medium);
        return espiritu;
    }
    public Espiritu aModelo(){
        Espiritu espiritu;
        if (Objects.equals(this.tipoDeEspiritu, "ANGELICAL")){
            espiritu = new Angel(this.nombre);
        } else {
            espiritu = new Demonio(this.nombre);
        }
        espiritu.setNivelConexion(this.nivelDeConexion);
        espiritu.setId(this.id);
        return espiritu;
    }

}
