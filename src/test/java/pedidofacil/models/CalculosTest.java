package pedidofacil.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import pedidofacil.factories.PedidoDomicilioCreator;
import pedidofacil.factories.PedidoMesaCreator;
import pedidofacil.factories.PedidoParaLlevarCreator;

class CalculosTest {

    private final List<ItemPedido> items = List.of(
            new ItemPedido(1, "Hamburguesa", 15000, 2),   // 30.000
            new ItemPedido(7, "Gaseosa", 4000, 3));       // 12.000

    @Test
    void subtotalDeUnItemEsPrecioPorCantidad() {
        assertEquals(30000, new ItemPedido(1, "Hamburguesa", 15000, 2).getSubtotal());
    }

    @Test
    void itemTomaNombreYPrecioDelProducto() {
        ItemPedido item = new ItemPedido(new Producto(3, "Salchipapa", 13000), 2);
        assertEquals(3, item.getProductoId());
        assertEquals("Salchipapa", item.getNombre());
        assertEquals(26000, item.getSubtotal());
    }

    @Test
    void itemConCantidadCeroLanzaError() {
        assertThrows(IllegalArgumentException.class, () -> new ItemPedido(1, "X", 1000, 0));
    }

    @Test
    void itemConPrecioNegativoLanzaError() {
        assertThrows(IllegalArgumentException.class, () -> new ItemPedido(1, "X", -1, 1));
    }

    @Test
    void subtotalDelPedidoSumaTodosLosItems() {
        Pedido p = new PedidoMesaCreator().crear("PED-0001", DatosCliente.paraMesa(1), items);
        assertEquals(42000, p.getSubtotal());
    }

    @Test
    void pedidoMesaNoCobraAdicional() {
        Pedido p = new PedidoMesaCreator().crear("PED-0001", DatosCliente.paraMesa(1), items);
        assertEquals(0, p.getCostoAdicional());
        assertEquals(42000, p.getTotal());
    }

    @Test
    void pedidoParaLlevarNoCobraAdicional() {
        Pedido p = new PedidoParaLlevarCreator().crear("PED-0001", DatosCliente.paraLlevar("Luis"), items);
        assertEquals(42000, p.getTotal());
    }

    @Test
    void pedidoDomicilioCobraCincoMil() {
        Pedido p = new PedidoDomicilioCreator().crear("PED-0001",
                DatosCliente.paraDomicilio("Ana", "300", "Calle 1"), items);
        assertEquals(5000, PedidoDomicilio.COSTO_DOMICILIO);
        assertEquals(5000, p.getCostoAdicional());
        assertEquals(47000, p.getTotal());
    }

    @Test
    void cantidadDeArticulosSumaLasCantidades() {
        Pedido p = new PedidoMesaCreator().crear("PED-0001", DatosCliente.paraMesa(1), items);
        assertEquals(5, p.getCantidadArticulos());
    }

    @Test
    void cambiarPrecioDelProductoNoAlteraPedidoYaCreado() {
        Producto producto = new Producto(1, "Hamburguesa", 15000);
        Pedido p = new PedidoMesaCreator().crear("PED-0001", DatosCliente.paraMesa(1),
                List.of(new ItemPedido(producto, 1)));
        producto.setPrecio(20000);
        assertEquals(15000, p.getTotal());
    }

    @Test
    void listaDeItemsDelPedidoNoSePuedeModificarDesdeFuera() {
        Pedido p = new PedidoMesaCreator().crear("PED-0001", DatosCliente.paraMesa(1), items);
        List<ItemPedido> vista = p.getItems();
        assertThrows(UnsupportedOperationException.class, () -> vista.add(items.get(0)));
    }

    @Test
    void descripcionDeEntregaDependeDeLaModalidad() {
        Pedido mesa = new PedidoMesaCreator().crear("PED-0001", DatosCliente.paraMesa(5), items);
        Pedido llevar = new PedidoParaLlevarCreator().crear("PED-0002", DatosCliente.paraLlevar("Luis"), items);
        Pedido casa = new PedidoDomicilioCreator().crear("PED-0003",
                DatosCliente.paraDomicilio("Ana", "300", "Calle 1"), items);
        assertEquals("Mesa 5", mesa.getDescripcionEntrega());
        assertEquals("Recoge Luis", llevar.getDescripcionEntrega());
        assertEquals("Calle 1 (Ana)", casa.getDescripcionEntrega());
    }

    @Test
    void productoConPrecioCeroLanzaError() {
        assertThrows(IllegalArgumentException.class, () -> new Producto(1, "Algo", 0));
    }

    @Test
    void productoSinNombreLanzaError() {
        assertThrows(IllegalArgumentException.class, () -> new Producto(1, "  ", 1000));
    }

    @Test
    void pedidoDirectoConDatosNulosLanzaError() {
        assertThrows(IllegalArgumentException.class, () -> new PedidoMesa("PED-0001", null, items,
                LocalDateTime.now(), EstadoPedido.PENDIENTE));
    }
}
