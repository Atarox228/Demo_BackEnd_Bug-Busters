package ar.edu.unq.epersgeist.modelo;

import lombok.*;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Setter
@Getter
@ToString
@Document("Coordenada")
public class Coordenada {
    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    private GeoJsonPoint punto;

    public Coordenada(double latitud, double longitud) {
        this.punto = new GeoJsonPoint(longitud, latitud);
    }

    public double getLatitud() {
        return this.punto.getY();
    }

    public double getLongitud() {
        return this.punto.getX();
    }
}