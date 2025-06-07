package ar.edu.unq.epersgeist.modelo;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Setter
@Getter
@ToString
@Document("Coordenada")
public class CoordenadaMongo {
    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    @Id
    private String id;
    private GeoJsonPoint punto;
    private String entityType;
    private Long entityId;

    public CoordenadaMongo(double latitud, double longitud) {
        this.punto = new GeoJsonPoint(longitud, latitud);
    }

    public double getLatitud() {
        return this.punto.getY();
    }

    public double getLongitud() {
        return this.punto.getX();
    }
}