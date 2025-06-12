package ar.edu.unq.epersgeist.modelo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Getter @Setter
@Document
public class SnapShot {

    @Id
    private String id;

    @NotNull
    private LocalDate date;

    public SnapShot(LocalDate date) {
        this.date = date;
    }
}
