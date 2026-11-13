package pedidofacil.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import pedidofacil.models.Producto;

class ProductoServiceTest {

    private AlmacenamientoMemoria almacen;
    private ProductoService servicio;

    @BeforeEach
    void preparar() {
        almacen = new AlmacenamientoMemoria();
        servicio = new ProductoService(almacen);
    }

    @Test
    void laPrimeraVezCargaProductosDeEjemploYLosGuarda() {
        assertFalse(servicio.listar().isEmpty());
        assertTrue(almacen.hayProductos());
        assertEquals(servicio.listar().size(), almacen.cargarProductos().size());
    }

    @Test
    void siYaHayProductosNoSeVuelvenACargarLosDeEjemplo() {
        servicio.eliminar(1);
        int cantidad = servicio.listar().size();
        ProductoService otro = new ProductoService(almacen);
        assertEquals(cantidad, otro.listar().size());
        assertFalse(otro.buscarPorId(1).isPresent());
    }

    @Test
    void siElMenuQuedaVacioNoSeRecargaElEjemplo() {
        for (Producto p : servicio.listar().toArray(new Producto[0])) {
            servicio.eliminar(p.getId());
        }
        assertTrue(new ProductoService(almacen).listar().isEmpty());
    }

    @Test
    void agregarAsignaIdSiguienteYGuarda() {
        int maxId = servicio.listar().stream().mapToInt(Producto::getId).max().getAsInt();
        Producto nuevo = servicio.agregar("Choriperro", 11000);
        assertEquals(maxId + 1, nuevo.getId());
        assertEquals(11000, nuevo.getPrecio());
        assertTrue(new ProductoService(almacen).buscarPorId(nuevo.getId()).isPresent());
    }

    @Test
    void agregarConNombreRepetidoLanzaError() {
        assertThrows(IllegalArgumentException.class, () -> servicio.agregar("hamburguesa", 9000));
    }

    @Test
    void agregarConDatosInvalidosLanzaError() {
        assertThrows(IllegalArgumentException.class, () -> servicio.agregar("", 5000));
        assertThrows(IllegalArgumentException.class, () -> servicio.agregar("Tinto", 0));
    }

    @Test
    void editarCambiaNombreYPrecio() {
        servicio.editar(1, "Hamburguesa doble", 18000);
        Producto p = servicio.buscarPorId(1).get();
        assertEquals("Hamburguesa doble", p.getNombre());
        assertEquals(18000, p.getPrecio());
        assertEquals(18000, new ProductoService(almacen).buscarPorId(1).get().getPrecio());
    }

    @Test
    void editarPuedeConservarSuPropioNombre() {
        servicio.editar(1, "Hamburguesa", 16000);
        assertEquals(16000, servicio.buscarPorId(1).get().getPrecio());
    }

    @Test
    void editarConNombreDeOtroProductoLanzaError() {
        assertThrows(IllegalArgumentException.class, () -> servicio.editar(1, "Gaseosa", 5000));
    }

    @Test
    void editarProductoInexistenteLanzaError() {
        assertThrows(IllegalArgumentException.class, () -> servicio.editar(999, "X", 1000));
    }

    @Test
    void eliminarQuitaElProducto() {
        servicio.eliminar(2);
        assertFalse(servicio.buscarPorId(2).isPresent());
    }

    @Test
    void eliminarProductoInexistenteLanzaError() {
        assertThrows(IllegalArgumentException.class, () -> servicio.eliminar(999));
    }

    @Test
    void laListaDevueltaNoSePuedeModificarDesdeFuera() {
        assertThrows(UnsupportedOperationException.class, () -> servicio.listar().clear());
    }

    @Test
    void idNoSeReutilizaDeUnProductoMaximoEliminadoSiHayOtrosMayores() {
        Producto a = servicio.agregar("Producto A", 1000);
        Producto b = servicio.agregar("Producto B", 2000);
        servicio.eliminar(a.getId());
        Producto c = servicio.agregar("Producto C", 3000);
        assertEquals(b.getId() + 1, c.getId());
    }
}
