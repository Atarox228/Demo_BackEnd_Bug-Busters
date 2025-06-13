package ar.edu.unq.epersgeist.modelo;

import ar.edu.unq.epersgeist.modelo.exception.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;
import org.hibernate.annotations.Check;
import java.io.Serializable;
import java.util.*;

@Getter @Setter @ToString @EqualsAndHashCode @NoArgsConstructor

@Entity
@Check(constraints = "mana <= manaMax")
public class Medium implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "VARCHAR(255) CHECK (char_length(nombre) > 0)")
    private String nombre;
    @Column(nullable = false)
    @Min(1)
    private Integer manaMax;
    @Column(nullable = false)
    @Min(0)
    private Integer mana;

    @OneToMany(mappedBy = "medium", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<Espiritu> espiritus = new HashSet<>();

    @ManyToOne
    private Ubicacion ubicacion;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;
    @Column(nullable = false)
    private Boolean deleted = false;

    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
        updatedAt  = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt  = new Date();
    }

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
        for (Espiritu espiritu : espiritus) {
            espiritu.aumentarConexionEn(this.ubicacion);
        }

    }

    public void aumentarMana(Integer mana) {
        this.setMana(Math.min(this.getMana() + mana, manaMax));
    }

    public void reducirMana(Integer mana) {
        this.setMana(Math.max(this.getMana() - mana, 0));
    }

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

    public void exorcizar(Medium medium2, List<Espiritu> angeles, List<Espiritu> demonios) throws ExorcismoEnDiferenteUbicacionException, NoHayAngelesException, MismoMediumException {

        if (this.getId() == medium2.getId()) throw new MismoMediumException();

        if (this.estaEnOtraUbicacion(medium2)) throw new ExorcismoEnDiferenteUbicacionException();


        if(angeles.isEmpty()) throw new NoHayAngelesException();


        List<Espiritu> demoniacosRestantes = demonios;

        for (Espiritu atacante : angeles){
            if (demoniacosRestantes.isEmpty()) break;
            demoniacosRestantes = atacante.ataque(demoniacosRestantes);
        }
    }

    public boolean estaEnOtraUbicacion(Medium medium2) {
        return !(this.ubicacion.getId() == medium2.getUbicacion().getId());
    }

    public void setMana(int mana) {
        this.mana = (mana > this.manaMax) ? this.manaMax : mana;
    }

    public void moverseA(Ubicacion ubicacion){
        setUbicacion(ubicacion);
        List<Espiritu> copia = new ArrayList<>(espiritus);
        copia.forEach(espiritu -> ubicacion.moverAEspiritu(espiritu));
    }

    public void desconectarse(Espiritu espiritu) {
        getEspiritus().remove(espiritu);
    }
}