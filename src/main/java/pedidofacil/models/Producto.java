package pedidofacil.models;

public class Producto {

    private int id;
    private String nombre;
    private int precio;

    public Producto(int id, String nombre, int precio) {
        validar(nombre, precio);
        this.id = id;
        this.nombre = nombre.trim();
        this.precio = precio;
    }

    public static void validar(String nombre, int precio) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del producto es obligatorio");
        }
        if (precio <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor que cero");
        }
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public int getPrecio() {
        return precio;
    }

    public void setNombre(String nombre) {
        validar(nombre, this.precio);
        this.nombre = nombre.trim();
    }

    public void setPrecio(int precio) {
        validar(this.nombre, precio);
        this.precio = precio;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
