package ar.edu.unq.epersgeist.modelo;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class Medium implements Serializable {

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
        espiritu.aumentarConexion(this);
        espiritus.add(espiritu);
    }

    public boolean tieneConNombre_(String nombre) {
        return espiritus.stream().anyMatch(esp -> esp.getNombre().equals(nombre));
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