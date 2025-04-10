package ar.edu.unq.epersgeist.modelo;

import java.io.Serializable;

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
    private String nombre;
    @ManyToOne
    private Medium medium;
    @ManyToOne
    private Ubicacion ubicacion;

    public Espiritu( @NonNull Integer nivelDeConexion, @NonNull String nombre) {
        // esto es para setear el valor default en caso de que no pongan valor
        // o pongan un valor no acorde al rango establecido
        if(nivelDeConexion>=0 && nivelDeConexion<=100){
            this.nivelConexion = nivelDeConexion;
        } else {
            this.nivelConexion = 0;
        }
        this.nombre = nombre;
        this.medium = null;
    }

    public Espiritu(@NonNull Integer nivelDeConexion, @NonNull String nombre, @NonNull Ubicacion ubicacion) {
        // esto es para setear el valor default en caso de que no pongan valor
        // o pongan un valor no acorde al rango establecido
        if(nivelDeConexion>=0 && nivelDeConexion<=100){
            this.nivelConexion = nivelDeConexion;
        } else {
            this.nivelConexion = 0;
        }
        this.nombre = nombre;
        //this.medium = null;
        this.ubicacion = ubicacion;
    }

    public Espiritu(@NonNull Long id, @NonNull Integer nivelDeConexion, @NonNull String nombre) {
        this.id = id;
        this.nivelConexion = nivelDeConexion;
        this.nombre = nombre;
        //this.medium = null;
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

    @Override
    public String toString() {
        return "Espiritu{" +
                "id=" + id +
                ", nivelConexion=" + nivelConexion +
                ", nombre='" + nombre + '\'' +
                ", medium= " + medium.getNombre() +
                ", ubicacion= " + ubicacion.getNombre() +
                '}';
    }

    public Long getId() {
        return id;
    }

    public Integer getNivelDeConexion() {
        return nivelConexion;
    }

    public String getNombre() {
        return nombre;
    }

    public Medium getMedium() {
        return medium;
    }

    public Ubicacion getUbicacion() {
        return ubicacion;
    }

    public void setNombre(String nombre){
        this.nombre = nombre;
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
            this.medium = null;
        }
    }
    public void invocarme(Medium medium, Ubicacion ubicacion)  {
        this.medium = medium;
        this.ubicacion = ubicacion;
    }

    public abstract TipoEspiritu getTipo();
}