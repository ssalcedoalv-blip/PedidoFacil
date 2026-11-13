package pedidofacil.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import pedidofacil.factories.PedidoCreator;
import pedidofacil.factories.PedidoMesaCreator;
import pedidofacil.models.DatosCliente;
import pedidofacil.models.EstadoPedido;
import pedidofacil.models.ItemPedido;
import pedidofacil.models.Modalidad;
import pedidofacil.models.Pedido;
import pedidofacil.models.PedidoDomicilio;

class PedidoServiceTest {

    private AlmacenamientoMemoria almacen;
    private PedidoService servicio;
    private final List<ItemPedido> items = List.of(new ItemPedido(1, "Hamburguesa", 15000, 2));

    @BeforeEach
    void preparar() {
        almacen = new AlmacenamientoMemoria();
        servicio = new PedidoService(almacen);
    }

    private Pedido crearMesa(int mesa) {
        return servicio.crearPedido(Modalidad.MESA, DatosCliente.paraMesa(mesa), items);
    }

    @Test
    void losIdsSonSecuencialesConCuatroDigitos() {
        assertEquals("PED-0001", crearMesa(1).getId());
        assertEquals("PED-0002", crearMesa(2).getId());
        assertEquals("PED-0003", crearMesa(3).getId());
    }

    @Test
    void crearPedidoUsaLaFabricaDeLaModalidad() {
        Pedido p = servicio.crearPedido(Modalidad.DOMICILIO,
                DatosCliente.paraDomicilio("Ana", "300", "Calle 1"), items);
        assertTrue(p instanceof PedidoDomicilio);
        assertEquals(35000, p.getTotal());
        assertEquals(EstadoPedido.PENDIENTE, p.getEstado());
    }

    @Test
    void crearPedidoLoGuardaEnElAlmacenamiento() {
        crearMesa(1);
        assertEquals(1, almacen.cargarPedidos().size());
    }

    @Test
    void unNuevoServicioRecuperaLosPedidosYContinuaLosIds() {
        crearMesa(1);
        Pedido segundo = crearMesa(2);
        servicio.avanzarEstado(segundo.getId());

        PedidoService reabierto = new PedidoService(almacen);

        assertEquals(2, reabierto.listar().size());
        assertEquals(EstadoPedido.EN_PREPARACION, reabierto.buscarPorId("PED-0002").get().getEstado());
        assertEquals("PED-0003", reabierto.crearPedido(Modalidad.MESA, DatosCliente.paraMesa(3), items).getId());
    }

    @Test
    void siFallaElGuardadoElPedidoNoQuedaEnMemoriaNiGastaElId() {
        almacen.fallarAlGuardarPedidos = true;
        assertThrows(IllegalStateException.class, () -> crearMesa(1));
        assertTrue(servicio.listar().isEmpty());

        almacen.fallarAlGuardarPedidos = false;
        assertEquals("PED-0001", crearMesa(1).getId());
    }

    @Test
    void crearConModalidadSinFabricaLanzaError() {
        PedidoService soloMesa = new PedidoService(almacen, List.<PedidoCreator>of(new PedidoMesaCreator()));
        assertThrows(IllegalArgumentException.class, () -> soloMesa.crearPedido(Modalidad.DOMICILIO,
                DatosCliente.paraDomicilio("Ana", "300", "Calle 1"), items));
    }

    @Test
    void fabricasRepetidasParaLaMismaModalidadLanzanError() {
        List<PedidoCreator> repetidas = List.of(new PedidoMesaCreator(), new PedidoMesaCreator());
        assertThrows(IllegalArgumentException.class, () -> new PedidoService(almacen, repetidas));
    }

    @Test
    void avanzarEstadoCambiaElPedidoYGuarda() {
        Pedido p = crearMesa(1);
        servicio.avanzarEstado(p.getId());
        assertEquals(EstadoPedido.EN_PREPARACION, servicio.buscarPorId(p.getId()).get().getEstado());
        assertEquals(EstadoPedido.EN_PREPARACION, almacen.cargarPedidos().get(0).getEstado());
    }

    @Test
    void avanzarPedidoInexistenteLanzaError() {
        assertThrows(NoSuchElementException.class, () -> servicio.avanzarEstado("PED-9999"));
    }

    @Test
    void avanzarPedidoEntregadoLanzaError() {
        Pedido p = crearMesa(1);
        servicio.avanzarEstado(p.getId());
        servicio.avanzarEstado(p.getId());
        servicio.avanzarEstado(p.getId());
        assertThrows(IllegalStateException.class, () -> servicio.avanzarEstado(p.getId()));
    }

    @Test
    void activosEHistorialSeSeparanPorEstado() {
        Pedido a = crearMesa(1);
        Pedido b = crearMesa(2);
        servicio.avanzarEstado(b.getId());
        servicio.avanzarEstado(b.getId());
        servicio.avanzarEstado(b.getId());

        assertEquals(1, servicio.listarActivos().size());
        assertEquals(a.getId(), servicio.listarActivos().get(0).getId());
        assertEquals(1, servicio.listarHistorial().size());
        assertEquals(b.getId(), servicio.listarHistorial().get(0).getId());
    }

    @Test
    void contarYFiltrarPorEstado() {
        crearMesa(1);
        Pedido b = crearMesa(2);
        servicio.avanzarEstado(b.getId());
        assertEquals(1, servicio.contarPorEstado(EstadoPedido.PENDIENTE));
        assertEquals(1, servicio.contarPorEstado(EstadoPedido.EN_PREPARACION));
        assertEquals(0, servicio.contarPorEstado(EstadoPedido.LISTO));
        assertEquals(b.getId(), servicio.listarPorEstado(EstadoPedido.EN_PREPARACION).get(0).getId());
    }

    @Test
    void totalVendidoSumaSoloLosEntregados() {
        Pedido a = crearMesa(1);                       // 30.000
        crearMesa(2);                                  // sin entregar
        Pedido c = servicio.crearPedido(Modalidad.DOMICILIO,
                DatosCliente.paraDomicilio("Ana", "300", "Calle 1"), items);   // 35.000
        for (int i = 0; i < 3; i++) {
            servicio.avanzarEstado(a.getId());
            servicio.avanzarEstado(c.getId());
        }
        assertEquals(65000, servicio.totalVendido());
    }

    @Test
    void listarDevuelveLosMasRecientesPrimero() {
        crearMesa(1);
        crearMesa(2);
        crearMesa(3);
        assertEquals("PED-0003", servicio.listar().get(0).getId());
        assertEquals("PED-0001", servicio.listar().get(2).getId());
    }

    @Test
    void buscarIdInexistenteDevuelveVacio() {
        assertFalse(servicio.buscarPorId("PED-0001").isPresent());
    }

    @Test
    void sinPedidosTodoEstaVacio() {
        assertTrue(servicio.listar().isEmpty());
        assertEquals(0, servicio.totalVendido());
    }
}
