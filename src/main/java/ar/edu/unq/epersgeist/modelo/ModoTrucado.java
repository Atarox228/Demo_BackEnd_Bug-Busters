package ar.edu.unq.epersgeist.modelo;

public class ModoTrucado implements Modo {

    private final int trucado10;
    private final int trucado100;

    public ModoTrucado(int i, int i2) {
        this.trucado10 = i;
        this.trucado100 = i2;
    }

    public int generarNumero(int min, int max) {
        int resultado = 0;
        if (max == 10){
            resultado = trucado10;
        } else {
            resultado = trucado100;
        }
        return resultado;
    }
}
