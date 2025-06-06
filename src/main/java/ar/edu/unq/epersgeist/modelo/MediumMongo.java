package ar.edu.unq.epersgeist.modelo;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.geo.Point;
import java.util.HashSet;
import java.util.Set;

@ToString
@Setter
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)

@Document("Medium")
public class MediumMongo {
    @Id
    private String id;
    private Long mediumIdSQL;
    private org.springframework.data.geo.Point coordenada;
    private Set<EspirituMongo> espiritus = new HashSet<>();

    public MediumMongo(Long id) {
        this.mediumIdSQL = id;
    }

    public void moverseA(Point coordenada) {
        setCoordenada(coordenada);
        for (EspirituMongo espiritu : espiritus) {
            espiritu.setCoordenada(coordenada);
        }
    }
}