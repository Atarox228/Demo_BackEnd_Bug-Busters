package ar.edu.unq.epersgeist.modelo;

import java.io.Serializable;

public class Espiritu implements Serializable {

    private Long id;
    private String tipo;
    private Integer nivelDeConexion;
    private String nombre;

    public Espiritu(String tipo, Integer nivelDeConexion, String nombre) {
        this.tipo = tipo;
        // esto es para setear el valor default en caso de que no pongan valor
        // o pongan un valor no acorde al rango establecido
        if(nivelDeConexion>=0 && nivelDeConexion<=100){
            this.nivelDeConexion = nivelDeConexion;
        } else {
            this.nivelDeConexion = 0;
        }
        this.nombre = nombre;
    }

    public Espiritu(Long id, String tipo, Integer nivelDeConexion, String nombre) {
        this.id = id;
        this.tipo = tipo;
        this.nivelDeConexion = nivelDeConexion;
        this.nombre = nombre;
    }

//    public void aumentarConexion(Medium medium) {
//        if (!medium.tieneConNombre_(this.nombre)) {
//            int maxNivelConexion = 100;
//            int nuevoNivelConexion = this.nivelDeConexion+10;
//            int minimo = Math.min(nuevoNivelConexion, maxNivelConexion);
//            nivelDeConexion = minimo;
//        }
//    }



    public Long getId() {
        return id;
    }

    public String getTipo() {
        return tipo;
    }

    public Integer getNivelDeConexion() {
        return nivelDeConexion;
    }

    public String getNombre() {
        return nombre;
    }

}
