package ar.edu.unq.epersgeist.modelo;

import lombok.*;

@Setter
@Getter
@ToString
public class Coordenada {
    private double latitud;
    private double longitud;

    public Coordenada(double latitud, double longitud) {
        this.latitud = latitud;
        this.longitud = longitud;
    }
}

