package pedidofacil.models;

public class ItemPedido {

    private final int productoId;
    private final String nombre;
    private final int precioUnitario;
    private final int cantidad;

    public ItemPedido(Producto producto, int cantidad) {
        this(producto.getId(), producto.getNombre(), producto.getPrecio(), cantidad);
    }

    public ItemPedido(int productoId, String nombre, int precioUnitario, int cantidad) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El item necesita un nombre");
        }
        if (precioUnitario < 0) {
            throw new IllegalArgumentException("El precio no puede ser negativo");
        }
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero");
        }
        this.productoId = productoId;
        this.nombre = nombre;
        this.precioUnitario = precioUnitario;
        this.cantidad = cantidad;
    }

    public int getProductoId() {
        return productoId;
    }

    public String getNombre() {
        return nombre;
    }

    public int getPrecioUnitario() {
        return precioUnitario;
    }

    public int getCantidad() {
        return cantidad;
    }

    public int getSubtotal() {
        return precioUnitario * cantidad;
    }
}
