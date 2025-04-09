package ar.edu.unq.epersgeist.modelo;

import java.io.Serializable;
import lombok.*;
import jakarta.persistence.*;

@Getter @Setter @NoArgsConstructor @ToString

@Entity
public class Espiritu implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoEspiritu tipo;
    private Integer nivelConexion;
    private String nombre;

    @ManyToOne
    private Medium medium;

    @ManyToOne
    private Ubicacion ubicacion;

    public Espiritu(@NonNull TipoEspiritu tipo, @NonNull Integer nivelDeConexion, @NonNull String nombre) {
        this.tipo = tipo;
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
                ", tipo='" + tipo + '\'' +
                ", nivelConexion=" + nivelConexion +
                ", nombre='" + nombre + '\'' +
                ", medium= " + medium.getNombre() +
                ", ubicacion= " + ubicacion.getNombre() +
                '}';
    }

    public void invocarme(Medium medium, Ubicacion ubicacion)  {
        this.medium = medium;
        this.ubicacion = ubicacion;
    }
}