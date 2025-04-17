package ar.edu.unq.epersgeist.modelo;

import ar.edu.unq.epersgeist.modelo.exception.EspirituNoLibreException;
import ar.edu.unq.epersgeist.modelo.exception.NoHayAngelesException;
import ar.edu.unq.epersgeist.modelo.exception.NoSePuedenConectarException;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Check;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.List;
import java.util.Set;

@Getter @Setter @ToString @EqualsAndHashCode @NoArgsConstructor

@Entity
@Check(constraints = "mana <= manaMax")
public class Medium implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "VARCHAR(255) CHECK (char_length(nombre) > 0)")
    private String nombre;
    @Column(nullable = false)
    private Integer manaMax;
    @Column(nullable = false)
    private Integer mana;

    @OneToMany(mappedBy = "medium", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Espiritu> espiritus = new HashSet<>();

    @ManyToOne
    private Ubicacion ubicacion;

    public Medium(@NonNull String nombre, @NonNull Integer manaMax, @NonNull Integer mana) {
        if (manaMax >= mana) {
            this.mana = mana;
        } else {
            this.mana = manaMax;
        }
        this.nombre = nombre;
        this.manaMax = manaMax;

    }

    public void conectarseAEspiritu(Espiritu espiritu) {
        if(!puedeConectarse(espiritu)){
            throw new NoSePuedenConectarException(this,espiritu);
        }
        espiritu.aumentarConexion(this.getMana() * 20 / 100);
        espiritus.add(espiritu);
        espiritu.setMedium(this);
    }


    public boolean puedeConectarse( Espiritu espiritu){
        return Objects.equals(this.getUbicacion().getNombre(), espiritu.getUbicacion().getNombre()) && espiritu.estaLibre();
    }


    public void descansar() {
        this.aumentarMana(15);
        espiritus.stream().forEach(espiritu -> espiritu.aumentarConexion(5));
    }

    public void aumentarMana(Integer mana) {
        this.setMana(Math.min(this.getMana() + 15, manaMax));
    }

    public void reducirMana(Integer mana) {
        this.setMana(Math.max(this.getMana() - mana, 0));
    }

    public void invocar(Espiritu espiritu) {
        if (this.mana > 10) {
            this.verificarSiEstaLibre(espiritu);
            this.reducirMana(10);
            espiritu.invocarme(this,this.ubicacion);
        }
    }

    private void verificarSiEstaLibre(Espiritu espiritu) {
        if (!espiritu.estaLibre()) {
            throw new EspirituNoLibreException(espiritu);
        }
    }

    public void exorcizar(Medium medium2, List<Espiritu> angeles, List<Espiritu> demonios) throws NoHayAngelesException {
        if(angeles.isEmpty()){
            throw new NoHayAngelesException();
        }
        List<Espiritu> angelicalesRestantes = angeles;
        List<Espiritu> demoniacosRestantes = demonios;
        while (angelicalesRestantes.size() >= 1 & demoniacosRestantes.size() >= 1) {
            Espiritu atacante = angelicalesRestantes.getFirst();
            Espiritu defensor = demoniacosRestantes.getFirst();
             if (atacante.getProbAtaque() > defensor.getProbDefensa()) {
                 defensor.reducirConexionYdesvincularSiEsNecesario(atacante.getNivelConexion() / 2);
                 if (defensor.getNivelConexion() == 0 & demoniacosRestantes.size() >= 2) {
                     demoniacosRestantes.remove(defensor);
                     defensor = demoniacosRestantes.getFirst();
                 }
             } else {
                 atacante.reducirConexionYdesvincularSiEsNecesario(5);
             }
            angelicalesRestantes.remove(atacante);

        }
    }

    public void setMana(int mana) {
        if (mana > this.manaMax) {
            this.mana = this.manaMax;
        }else{
            this.mana = mana;
        }
    }
}