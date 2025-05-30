package ar.edu.unq.epersgeist.modelo;


public class ModoRandom implements Modo {

    @Override
    public int generarNumero(int min, int max) {
        return  (int)(Math.random() * (max - min + 1) + min);
    }
}
