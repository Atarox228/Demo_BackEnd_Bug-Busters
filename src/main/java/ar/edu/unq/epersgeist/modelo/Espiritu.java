package ar.edu.unq.epersgeist.modelo;

import java.io.Serializable;

import lombok.*;

import jakarta.persistence.*;


import java.util.HashSet;
import java.util.Set;

@Getter @Setter @NoArgsConstructor @ToString

@Entity
public class Espiritu implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String tipo;
    private Integer nivelConexion;
    private String nombre;

    public Espiritu(@NonNull String tipo, @NonNull Integer nivelDeConexion, @NonNull String nombre) {
        this.tipo = tipo;
        // esto es para setear el valor default en caso de que no pongan valor
        // o pongan un valor no acorde al rango establecido
        if(nivelDeConexion>=0 && nivelDeConexion<=100){
            this.nivelConexion = nivelDeConexion;
        } else {
            this.nivelConexion = 0;
        }
        this.nombre = nombre;
    }

    public Espiritu(@NonNull Long id, @NonNull String tipo, @NonNull Integer nivelDeConexion, @NonNull String nombre) {
        this.id = id;
        this.tipo = tipo;
        this.nivelConexion = nivelDeConexion;
        this.nombre = nombre;
    }

    public void aumentarConexion(Integer conexion) {
        int maxNivelConexion = 100;
        int nuevoNivelConexion = this.nivelConexion + conexion;
        int minimo = Math.min(nuevoNivelConexion, maxNivelConexion);
        nivelConexion = minimo;
    }

    public Long getId() {
        return id;
    }

    public String getTipo() {
        return tipo;
    }

    public Integer getNivelDeConexion() {
        return nivelConexion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre){
        this.nombre = nombre;
    }

}