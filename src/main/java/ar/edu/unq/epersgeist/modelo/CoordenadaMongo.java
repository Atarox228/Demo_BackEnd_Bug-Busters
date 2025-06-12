package ar.edu.unq.epersgeist.modelo;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.index.*;
import org.springframework.data.mongodb.core.mapping.Document;

@ToString
@Setter
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)


@Document("Coordenada")
public class CoordenadaMongo {

    @Id
    private String id;

    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    private GeoJsonPoint punto;

    private String entityType;
    private Long entityId;

    public CoordenadaMongo(GeoJsonPoint punto, String entityType, Long entityId) {
        this.punto = punto;
        this.entityType = entityType;
        this.entityId = entityId;
    }

    public double getLongitud() {
        return this.punto.getX();
    }

    public double getLatitud() {
        return this.punto.getY();
    }


}