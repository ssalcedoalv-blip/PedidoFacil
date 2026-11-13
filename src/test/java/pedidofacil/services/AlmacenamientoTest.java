package pedidofacil.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import pedidofacil.models.DatosCliente;
import pedidofacil.models.EstadoPedido;
import pedidofacil.models.ItemPedido;
import pedidofacil.models.Modalidad;
import pedidofacil.models.Pedido;
import pedidofacil.models.PedidoGuardado;
import pedidofacil.models.Producto;

/** Usa archivos reales (en una carpeta temporal que JUnit borra al terminar). */
class AlmacenamientoTest {

    @TempDir
    Path carpeta;

    @Test
    void sinArchivosNoHayProductosNiPedidos() {
        AlmacenamientoJson almacen = new AlmacenamientoJson(carpeta);
        assertFalse(almacen.hayProductos());
        assertTrue(almacen.cargarProductos().isEmpty());
        assertTrue(almacen.cargarPedidos().isEmpty());
    }

    @Test
    void guardaYCargaProductosConTildes() {
        AlmacenamientoJson almacen = new AlmacenamientoJson(carpeta);
        almacen.guardarProductos(List.of(new Producto(1, "Jugo de maracuyá", 5000),
                new Producto(2, "Ñapa especial", 2500)));

        assertTrue(almacen.hayProductos());
        List<Producto> cargados = new AlmacenamientoJson(carpeta).cargarProductos();
        assertEquals(2, cargados.size());
        assertEquals("Jugo de maracuyá", cargados.get(0).getNombre());
        assertEquals(2500, cargados.get(1).getPrecio());
    }

    @Test
    void creaLaCarpetaSiNoExiste() {
        Path nueva = carpeta.resolve("data").resolve("interna");
        new AlmacenamientoJson(nueva).guardarProductos(List.of(new Producto(1, "Café", 2000)));
        assertTrue(Files.exists(nueva.resolve("productos.json")));
    }

    @Test
    void noDejaArchivoTemporalDespuesDeGuardar() {
        new AlmacenamientoJson(carpeta).guardarProductos(List.of(new Producto(1, "Café", 2000)));
        assertFalse(Files.exists(carpeta.resolve("productos.json.tmp")));
    }

    @Test
    void sobrescribeElArchivoAnterior() {
        AlmacenamientoJson almacen = new AlmacenamientoJson(carpeta);
        almacen.guardarProductos(List.of(new Producto(1, "A", 1000), new Producto(2, "B", 2000)));
        almacen.guardarProductos(List.of(new Producto(3, "C", 3000)));
        assertEquals(1, almacen.cargarProductos().size());
    }

    @Test
    void guardaYCargaPedidosConTodosSusDatos() {
        AlmacenamientoJson almacen = new AlmacenamientoJson(carpeta);
        PedidoService servicio = new PedidoService(almacen);
        servicio.crearPedido(Modalidad.DOMICILIO,
                DatosCliente.paraDomicilio("María Pérez", "3001234567", "Calle 5 #10-20"),
                List.of(new ItemPedido(1, "Hamburguesa", 15000, 2), new ItemPedido(7, "Gaseosa", 4000, 1)));

        List<PedidoGuardado> cargados = new AlmacenamientoJson(carpeta).cargarPedidos();

        assertEquals(1, cargados.size());
        PedidoGuardado g = cargados.get(0);
        assertEquals("PED-0001", g.getId());
        assertEquals(Modalidad.DOMICILIO, g.getModalidad());
        assertEquals(EstadoPedido.PENDIENTE, g.getEstado());
        assertEquals("María Pérez", g.getDatos().getNombre());
        assertEquals("Calle 5 #10-20", g.getDatos().getDireccion());
        assertEquals(2, g.getItems().size());
        assertEquals(15000, g.getItems().get(0).getPrecioUnitario());
    }

    @Test
    void servicioReconstruyeLosPedidosDesdeElJsonConLasFabricas() {
        PedidoService primero = new PedidoService(new AlmacenamientoJson(carpeta));
        List<ItemPedido> items = List.of(new ItemPedido(1, "Hamburguesa", 15000, 2));
        primero.crearPedido(Modalidad.MESA, DatosCliente.paraMesa(3), items);
        Pedido llevar = primero.crearPedido(Modalidad.PARA_LLEVAR, DatosCliente.paraLlevar("Luis"), items);
        primero.avanzarEstado(llevar.getId());

        PedidoService segundo = new PedidoService(new AlmacenamientoJson(carpeta));

        assertEquals(2, segundo.listar().size());
        Pedido mesa = segundo.buscarPorId("PED-0001").get();
        assertEquals(Modalidad.MESA, mesa.getModalidad());
        assertEquals("Mesa 3", mesa.getDescripcionEntrega());
        assertEquals(EstadoPedido.EN_PREPARACION, segundo.buscarPorId("PED-0002").get().getEstado());
        assertEquals(30000, mesa.getTotal());
        assertEquals("PED-0003", segundo.crearPedido(Modalidad.MESA, DatosCliente.paraMesa(1), items).getId());
    }

    @Test
    void fechaDelPedidoSobreviveAlGuardado() {
        PedidoService primero = new PedidoService(new AlmacenamientoJson(carpeta));
        Pedido original = primero.crearPedido(Modalidad.MESA, DatosCliente.paraMesa(1),
                List.of(new ItemPedido(1, "Gaseosa", 4000, 1)));
        PedidoService segundo = new PedidoService(new AlmacenamientoJson(carpeta));
        assertEquals(original.getFechaCreacion(), segundo.listar().get(0).getFechaCreacion());
    }

    @Test
    void productoServiceCargaEjemploSoloLaPrimeraVezConArchivosReales() {
        ProductoService primero = new ProductoService(new AlmacenamientoJson(carpeta));
        int cantidad = primero.listar().size();
        primero.eliminar(1);

        ProductoService segundo = new ProductoService(new AlmacenamientoJson(carpeta));
        assertEquals(cantidad - 1, segundo.listar().size());
    }

    @Test
    void archivoVacioSeTrataComoSinDatos() throws IOException {
        Files.writeString(carpeta.resolve("pedidos.json"), "   ", StandardCharsets.UTF_8);
        assertTrue(new AlmacenamientoJson(carpeta).cargarPedidos().isEmpty());
    }

    @Test
    void archivoDanadoLanzaErrorClaro() throws IOException {
        Files.writeString(carpeta.resolve("productos.json"), "{esto no es json", StandardCharsets.UTF_8);
        AlmacenamientoJson almacen = new AlmacenamientoJson(carpeta);
        assertThrows(IllegalStateException.class, almacen::cargarProductos);
    }
}
