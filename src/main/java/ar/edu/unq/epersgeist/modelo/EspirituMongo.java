package ar.edu.unq.epersgeist.modelo;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

@ToString
@Setter
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)

public class EspirituMongo {

    @Id
    private String id;
    private Long espirituIdSQL;
    private GeoJsonPoint coordenada;

    public EspirituMongo(String id, GeoJsonPoint coordenada) {
        this.id = id;
        this.coordenada = coordenada;
    }
}
