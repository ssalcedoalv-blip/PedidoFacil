package pedidofacil.models;

public enum Modalidad {
    DOMICILIO("Domicilio"),
    MESA("En mesa"),
    PARA_LLEVAR("Para llevar");

    private final String etiqueta;

    Modalidad(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
