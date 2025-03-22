package ar.edu.unq.epersgeist.modelo;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class Medium implements Serializable {
//lala
    //lolo
    private String nombre;
    private Integer manaMax;
    private Integer mana;
    private Set<Espiritu> espiritus = new HashSet<>();

    public Medium(String nombre, Integer manaMax, Integer mana) {
        this.nombre = nombre;
        this.manaMax = manaMax;
        this.mana = mana;
    }

    public void conectarseAEspiritu(Espiritu espiritu) {
        // TODO completar
    }

    public String getNombre() {
        return nombre;
    }

    public Integer getManaMax() {
        return manaMax;
    }

    public Integer getMana() {
        return mana;
    }

    public Set<Espiritu> getEspiritus() {
        return espiritus;
    }
}