package pedidofacil.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import pedidofacil.factories.PedidoMesaCreator;

class EstadoPedidoTest {

    @Test
    void elOrdenDeLosEstadosEsElEsperado() {
        assertEquals(EstadoPedido.EN_PREPARACION, EstadoPedido.PENDIENTE.siguiente());
        assertEquals(EstadoPedido.LISTO, EstadoPedido.EN_PREPARACION.siguiente());
        assertEquals(EstadoPedido.ENTREGADO, EstadoPedido.LISTO.siguiente());
    }

    @Test
    void entregadoEsFinalYNoAvanza() {
        assertTrue(EstadoPedido.ENTREGADO.esFinal());
        assertThrows(IllegalStateException.class, EstadoPedido.ENTREGADO::siguiente);
    }

    @Test
    void losDemasEstadosNoSonFinales() {
        assertFalse(EstadoPedido.PENDIENTE.esFinal());
        assertFalse(EstadoPedido.EN_PREPARACION.esFinal());
        assertFalse(EstadoPedido.LISTO.esFinal());
    }

    @Test
    void textoDeAccionExisteSoloSiPuedeAvanzar() {
        assertEquals("Iniciar preparación", EstadoPedido.PENDIENTE.getTextoAccion());
        assertNull(EstadoPedido.ENTREGADO.getTextoAccion());
    }

    @Test
    void etiquetasSeVenBien() {
        assertEquals("En preparación", EstadoPedido.EN_PREPARACION.getEtiqueta());
    }

    @Test
    void pedidoRecorreTodosLosEstadosUnoAUno() {
        Pedido p = new PedidoMesaCreator().crear("PED-0001", DatosCliente.paraMesa(1),
                List.of(new ItemPedido(1, "Gaseosa", 4000, 1)));
        assertEquals(EstadoPedido.PENDIENTE, p.getEstado());
        p.avanzarEstado();
        assertEquals(EstadoPedido.EN_PREPARACION, p.getEstado());
        p.avanzarEstado();
        assertEquals(EstadoPedido.LISTO, p.getEstado());
        p.avanzarEstado();
        assertEquals(EstadoPedido.ENTREGADO, p.getEstado());
    }

    @Test
    void pedidoEntregadoNoPuedeAvanzarMas() {
        Pedido p = new PedidoMesaCreator().crear("PED-0001", DatosCliente.paraMesa(1),
                List.of(new ItemPedido(1, "Gaseosa", 4000, 1)));
        p.avanzarEstado();
        p.avanzarEstado();
        p.avanzarEstado();
        assertThrows(IllegalStateException.class, p::avanzarEstado);
        assertEquals(EstadoPedido.ENTREGADO, p.getEstado());
    }
}
