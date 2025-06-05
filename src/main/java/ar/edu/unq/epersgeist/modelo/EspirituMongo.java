package ar.edu.unq.epersgeist.modelo;
import org.springframework.data.geo.Point;
import lombok.*;
import org.springframework.data.annotation.Id;

@ToString
@Setter
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)

public class EspirituMongo {

    @Id
    private String id;
    private Long espirituIdSQL;
    private Point coordenada;

    public EspirituMongo(Long espirituIdSQL) {
        this.espirituIdSQL = espirituIdSQL;
    }
}
