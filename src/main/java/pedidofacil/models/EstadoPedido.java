package pedidofacil.models;

public enum EstadoPedido {
    PENDIENTE("Pendiente", "Iniciar preparación"),
    EN_PREPARACION("En preparación", "Marcar como listo"),
    LISTO("Listo", "Marcar como entregado"),
    ENTREGADO("Entregado", null);

    private final String etiqueta;
    private final String textoAccion;

    EstadoPedido(String etiqueta, String textoAccion) {
        this.etiqueta = etiqueta;
        this.textoAccion = textoAccion;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public String getTextoAccion() {
        return textoAccion;
    }

    public boolean esFinal() {
        return this == ENTREGADO;
    }

    public EstadoPedido siguiente() {
        if (esFinal()) {
            throw new IllegalStateException("El pedido ya está entregado y no puede avanzar más");
        }
        return values()[ordinal() + 1];
    }
}
