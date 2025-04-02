package ar.edu.unq.epersgeist.modelo;

import ar.edu.unq.epersgeist.persistencia.dao.exception.NoSePuedenConectarException;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
@Getter @Setter @ToString @EqualsAndHashCode @NoArgsConstructor

@Entity
public class Medium implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private Integer manaMax;
    private Integer mana;
    private Set<Espiritu> espiritusAngelicales = new HashSet<>();
    private Set<Espiritu> espiritusDemoniacos = new HashSet<>();



    @ManyToOne
    private Ubicacion ubicacion;

    public Medium(String nombre, Integer manaMax, Integer mana) {
        this.nombre = nombre;
        this.manaMax = manaMax;
        this.mana = mana;
    }


    public Medium(String nombre, Integer manaMax, Integer mana, Ubicacion ubicacion) {
        this.nombre = nombre;
        this.manaMax = manaMax;
        this.mana = mana;
        this.ubicacion = ubicacion;
    }

    // COMPLETAR
    public boolean tieneConNombre_(String nombre) {
        return false;
    }

    // COMPLETAR
    public void conectarseAEspiritu(Espiritu espiritu) {
        if(!puedeConectarse(espiritu)){
            throw new NoSePuedenConectarException(this,espiritu);
        }
        espiritu.aumentarConexion(this.getMana() * 20 / 100);
        if (espiritu.getTipo() == "Angelical"){
            espiritusAngelicales.add(espiritu);
        } else {
            espiritusDemoniacos.add(espiritu);
        }

        espiritu.setMedium(this);
    }


    public boolean puedeConectarse( Espiritu espiritu){
        return this.getUbicacion().getNombre() == espiritu.getUbicacion().getNombre() && espiritu.estaLibre();
    }


    public void descansar() {
        this.aumentarMana(15);
        espiritusAngelicales.stream().forEach(espiritu -> espiritu.aumentarConexion(5));
        espiritusDemoniacos.stream().forEach(espiritu -> espiritu.aumentarConexion(5));
    }

    public void aumentarMana(Integer mana) {
        this.setMana(Math.min(this.getMana() + 15, manaMax));
    }
}