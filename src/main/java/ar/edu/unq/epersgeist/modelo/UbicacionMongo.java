package ar.edu.unq.epersgeist.modelo;

import ar.edu.unq.epersgeist.modelo.enums.TipoUbicacion;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon;

@ToString
@Setter
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)

@Document("Ubicacion")
public class UbicacionMongo {

    @Id
    private String id;
    private String nombre;
    private TipoUbicacion tipo;
    private Integer flujoEnergia;
    private GeoJsonPolygon area;

    public UbicacionMongo(String nombre, TipoUbicacion tipo ,Integer flujoEnergia, GeoJsonPolygon area) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.flujoEnergia = flujoEnergia;
        this.area = area;
    }
}