package ar.edu.unq.epersgeist.controller.dto;

import ar.edu.unq.epersgeist.modelo.*;
import ar.edu.unq.epersgeist.modelo.enums.TipoUbicacion;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon;
import java.util.List;

public record UbicacionDTO(
        Long id,
        List<String> nombresDestino,
        GeoJsonPolygon area,
        @NotBlank String nombre,
        @NotNull TipoUbicacion tipoDeUbicacion,
        @Min(0) @Max(100) Integer flujoDeEnergia)

{

    public GeoJsonPolygon getArea(){
        return this.area;
    }

    public static UbicacionDTO desdeModelo(Ubicacion ubicacion) {
        return new UbicacionDTO(
                ubicacion.getId(),
                List.of(),
                null,
                ubicacion.getNombre(),
                ubicacion.getTipo(),
                ubicacion.getFlujoEnergia()
        );
    }

    public static UbicacionDTO desdeNeo(UbicacionNeo4J ubicacion) {
        List<String> conexiones = ubicacion.getUbicaciones()
                .stream()
                .map(UbicacionNeo4J::getNombre)
                .toList();

        return new UbicacionDTO(
                ubicacion.getId(),
                conexiones,
                null,
                ubicacion.getNombre(),
                ubicacion.getTipo(),
                ubicacion.getFlujoEnergia()
        );
    }

    public Ubicacion aModelo(){
        return switch (this.tipoDeUbicacion) {
            case SANTUARIO -> new Santuario(nombre, flujoDeEnergia);
            case CEMENTERIO -> new Cementerio(nombre, flujoDeEnergia);
        };
    }

    public UbicacionMongo aMongo(){
        return new UbicacionMongo(nombre, tipoDeUbicacion ,flujoDeEnergia, area);
    }
}
