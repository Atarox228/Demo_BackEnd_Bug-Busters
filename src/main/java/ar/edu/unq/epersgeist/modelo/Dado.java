package ar.edu.unq.epersgeist.modelo;

import lombok.*;

@Getter @Setter

public final class Dado implements GeneradorNumeros{

    public static Dado dado;
    private Modo modo;

    public static Dado getInstance(){
        if (dado == null){
            dado = new Dado();
            dado.setModo(new ModoRandom());
        }
        return dado;
    }

    public int generarNumero(int min, int max){ return modo.generarNumero(min, max);}



}
