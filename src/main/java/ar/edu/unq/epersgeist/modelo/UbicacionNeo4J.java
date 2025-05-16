package ar.edu.unq.epersgeist.modelo;

import ar.edu.unq.epersgeist.modelo.enums.TipoUbicacion;
import lombok.*;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;
import java.util.HashSet;
import java.util.Set;

@ToString
@Setter
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)

@Node
public class UbicacionNeo4J {

    @Id
    @GeneratedValue
    private Long id;

    private String nombre;
    private TipoUbicacion tipo;
    private Integer flujoEnergia;

    @Relationship(type = "CONECTADA", direction = Relationship.Direction.OUTGOING)
    private Set<UbicacionNeo4J> ubicaciones = new HashSet<>();

    public UbicacionNeo4J(String nombre, TipoUbicacion tipo ,Integer flujoEnergia) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.flujoEnergia = flujoEnergia;
    }

    public void conectarse(UbicacionNeo4J destino) {
        this.ubicaciones.add(destino);
    }
}
