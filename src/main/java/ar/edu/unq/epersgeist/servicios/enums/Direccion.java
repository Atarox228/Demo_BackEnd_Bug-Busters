package ar.edu.unq.epersgeist.servicios.enums;

public enum Direccion {

    ASCENDENTE("asc"),
    DESCENDENTE("desc");

    private final String orden;

    Direccion(String orden) {
        this.orden = orden;
    }

    public String getOrden(){
        return orden;
    }
}
