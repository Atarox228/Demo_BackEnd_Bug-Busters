package ar.edu.unq.epersgeist.modelo;

import ar.edu.unq.epersgeist.modelo.exception.EspirituNoLibreException;
import ar.edu.unq.epersgeist.modelo.exception.InvocacionFallidaPorUbicacionException;
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

    @OneToMany(mappedBy = "medium", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<Espiritu> espiritus = new HashSet<>();

    @Setter
    @ManyToOne
    private Ubicacion ubicacion;

    public Medium(@NonNull String nombre, @NonNull Integer manaMax, @NonNull Integer mana) {
        this.mana = (manaMax >= mana) ? mana : manaMax;
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

    public boolean puedeConectarse(Espiritu espiritu){
        return Objects.equals(this.getUbicacion().getNombre(), espiritu.getUbicacion().getNombre()) && espiritu.estaLibre();
    }

    public void descansar() {
        this.aumentarMana(this.ubicacion.valorDeRecuperacionMedium());
        espiritus.stream()
                .filter(espiritu -> this.ubicacion.puedeRecuperarse(espiritu))
                .forEach(espiritu -> espiritu.aumentarConexion(this.ubicacion.valorDeRecuperacionEspiritu()));

    }

    public void aumentarMana(Integer mana) {
        this.setMana(Math.min(this.getMana() + mana, manaMax));
    }

    public void reducirMana(Integer mana) {
        this.setMana(Math.max(this.getMana() - mana, 0));
    }

//    public void invocar(Espiritu espiritu) {
//        if (this.mana > 10) {
//            espiritu.invocarseA(this.ubicacion);
//            this.reducirMana(10);
//        }
//    }

    //@Transactional
    public void invocar(Espiritu espiritu) {
        if (this.mana > 10) {
            this.verificarSiPuedeInvocar(espiritu);
            this.reducirMana(10);
            espiritu.invocarseA(this.ubicacion);
        }
    }

    private void verificarSiPuedeInvocar(Espiritu espiritu) {
        //Indica si el espiritu esta libre y si la ubicacion del medium permite invocarlo
        if (! espiritu.estaLibre()) {
            throw new EspirituNoLibreException(espiritu);
        }

        if (! ubicacion.permiteInvocarTipo(espiritu.getTipo())) {
            throw new InvocacionFallidaPorUbicacionException(espiritu, ubicacion);
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
        this.mana = (mana > this.manaMax) ? this.manaMax : mana;
    }

    public void moverseA(Ubicacion ubicacion){
        setUbicacion(ubicacion);
        espiritus.forEach(espiritu -> ubicacion.moverAEspiritu(espiritu));
    }

//    public void moverASantuario(Santuario santuario) {
//        espiritus.forEach(espiritu -> espiritu.moverseASantuario(santuario));
//    }
//
//    public void moverACementerio(Cementerio cementerio) {
//        espiritus.forEach(espiritu -> espiritu.moverseACementerio(cementerio));
//    }

    public void desconectarse(Espiritu espiritu) {
        getEspiritus().remove(espiritu);
    }
}