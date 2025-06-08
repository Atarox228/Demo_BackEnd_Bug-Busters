package ar.edu.unq.epersgeist.modelo;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon;
import org.springframework.data.mongodb.core.mapping.Document;

@ToString
@Setter
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)

@Document("Area")
public class AreaMongo {
    @Id
    private String id;
    private GeoJsonPolygon poligono;
    private Long idUbicacion;

    public AreaMongo(GeoJsonPolygon poligono, Long idUbicacion) {
        this.poligono = poligono;
        this.idUbicacion = idUbicacion;
    }
}
