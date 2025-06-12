package ar.edu.unq.epersgeist.modelo;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.Map;

@Getter @Setter
@Document
public class SnapShot {

    @Id
    private String id;

    @NotNull
    private LocalDate date;

    @NotNull
    private Map<String, Object> sql;

//    private Map<String, Object> mongo;
//    private Map<String, Object> neo4j;

    public SnapShot(LocalDate date) {
        this.date = date;
    }

    public SnapShot(LocalDate date, Map<String, Object> sql) {
        this.date = date;
        this.sql = sql;
    }

//    public SnapShot(LocalDate date, Map<String, Object> sql, Map<String, Object> mongo, Map<String, Object> neo4j) {
//        this.date = date;
//        this.sql = sql;
//        this.mongo = mongo;
//        this.neo4j = neo4j;
//    }
}
