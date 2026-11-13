package pedidofacil.models;

public class DatosCliente {

    private final String nombre;
    private final String telefono;
    private final String direccion;
    private final int numeroMesa;

    public DatosCliente(String nombre, String telefono, String direccion, int numeroMesa) {
        this.nombre = limpiar(nombre);
        this.telefono = limpiar(telefono);
        this.direccion = limpiar(direccion);
        this.numeroMesa = numeroMesa;
    }

    public static DatosCliente paraDomicilio(String nombre, String telefono, String direccion) {
        return new DatosCliente(nombre, telefono, direccion, 0);
    }

    public static DatosCliente paraMesa(int numeroMesa) {
        return new DatosCliente("", "", "", numeroMesa);
    }

    public static DatosCliente paraLlevar(String nombre) {
        return new DatosCliente(nombre, "", "", 0);
    }

    private static String limpiar(String texto) {
        return texto == null ? "" : texto.trim();
    }

    public String getNombre() {
        return nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public int getNumeroMesa() {
        return numeroMesa;
    }
}
