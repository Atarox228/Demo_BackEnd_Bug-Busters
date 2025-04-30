package ar.edu.unq.epersgeist.modelo;

import java.io.Serializable;

import ar.edu.unq.epersgeist.modelo.exception.EspirituNoLibreException;
import lombok.*;
import jakarta.persistence.*;


import java.util.HashSet;
import java.util.Set;

@Getter @Setter @NoArgsConstructor

@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo", discriminatorType = DiscriminatorType.STRING)

@Entity
public abstract class Espiritu implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Integer nivelConexion;
    @Column(nullable = false, columnDefinition = "VARCHAR(255) CHECK (char_length(nombre) > 0)")
    private String nombre;
    @ManyToOne
    private Medium medium;
    @ManyToOne
    private Ubicacion ubicacion;

    public Espiritu(@NonNull String nombre) {
        this.nivelConexion = 0;
        this.nombre = nombre;
        this.medium = null;
    }

    public void aumentarConexion(Integer conexion) {
            int maxNivelConexion = 100;
            int nuevoNivelConexion = this.nivelConexion + conexion;
            int minimo = Math.min(nuevoNivelConexion, maxNivelConexion);
            nivelConexion = minimo;
    }

    public boolean estaLibre(){
        return this.medium == null;
    }

    public int getProbDefensa() {
        GeneradorNumeros dado = Dado.getInstance();
        return dado.generarNumero(1,100);
    }

    public int getProbAtaque() {
        GeneradorNumeros dado = Dado.getInstance();
        return Math.min(dado.generarNumero(1,10) + nivelConexion, 100);
    }

    public void reducirConexionYdesvincularSiEsNecesario(int i) {
        this.nivelConexion = Math.max(this.nivelConexion - i, 0);
        if (nivelConexion == 0) {
            medium.desconectarse(this);
            this.medium = null;
        }
    }

    public void invocarseA(Ubicacion ubicacion) {
        if (!estaLibre()) {
            throw new EspirituNoLibreException(this);
        }
    }

    public abstract TipoEspiritu getTipo();

//    public void moverseACementerio(Ubicacion ubicacion) {
//        setUbicacion(ubicacion);
//    }
//
//    public void moverseASantuario(Ubicacion ubicacion) {
//        setUbicacion(ubicacion);
//    }
}