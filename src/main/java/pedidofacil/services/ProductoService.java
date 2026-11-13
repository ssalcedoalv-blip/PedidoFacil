package pedidofacil.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import pedidofacil.models.Producto;

public class ProductoService {

    private final Almacenamiento almacenamiento;
    private final List<Producto> productos;

    public ProductoService(Almacenamiento almacenamiento) {
        this.almacenamiento = almacenamiento;
        if (almacenamiento.hayProductos()) {
            this.productos = new ArrayList<>(almacenamiento.cargarProductos());
        } else {
            this.productos = new ArrayList<>(productosDeEjemplo());
            almacenamiento.guardarProductos(productos);
        }
    }

    private static List<Producto> productosDeEjemplo() {
        return List.of(
                new Producto(1, "Hamburguesa", 15000),
                new Producto(2, "Perro caliente", 10000),
                new Producto(3, "Salchipapa", 13000),
                new Producto(4, "Pizza personal", 12000),
                new Producto(5, "Empanada", 3000),
                new Producto(6, "Papas fritas", 6000),
                new Producto(7, "Gaseosa", 4000),
                new Producto(8, "Jugo natural", 5000));
    }

    public List<Producto> listar() {
        return Collections.unmodifiableList(productos);
    }

    public Optional<Producto> buscarPorId(int id) {
        return productos.stream().filter(p -> p.getId() == id).findFirst();
    }

    public Producto agregar(String nombre, int precio) {
        Producto.validar(nombre, precio);
        validarNombreLibre(nombre, -1);
        int nuevoId = productos.stream().mapToInt(Producto::getId).max().orElse(0) + 1;
        Producto nuevo = new Producto(nuevoId, nombre, precio);
        productos.add(nuevo);
        guardar();
        return nuevo;
    }

    public Producto editar(int id, String nombre, int precio) {
        Producto producto = buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("No existe el producto " + id));
        Producto.validar(nombre, precio);
        validarNombreLibre(nombre, id);
        producto.setNombre(nombre);
        producto.setPrecio(precio);
        guardar();
        return producto;
    }

    public void eliminar(int id) {
        Producto producto = buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("No existe el producto " + id));
        productos.remove(producto);
        guardar();
    }

    private void validarNombreLibre(String nombre, int idIgnorado) {
        boolean repetido = productos.stream()
                .anyMatch(p -> p.getId() != idIgnorado && p.getNombre().equalsIgnoreCase(nombre.trim()));
        if (repetido) {
            throw new IllegalArgumentException("Ya existe un producto llamado " + nombre.trim());
        }
    }

    private void guardar() {
        almacenamiento.guardarProductos(productos);
    }
}
