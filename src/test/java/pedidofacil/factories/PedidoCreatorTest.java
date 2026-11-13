package pedidofacil.factories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import pedidofacil.models.DatosCliente;
import pedidofacil.models.EstadoPedido;
import pedidofacil.models.ItemPedido;
import pedidofacil.models.Modalidad;
import pedidofacil.models.Pedido;
import pedidofacil.models.PedidoDomicilio;
import pedidofacil.models.PedidoGuardado;
import pedidofacil.models.PedidoMesa;
import pedidofacil.models.PedidoParaLlevar;

class PedidoCreatorTest {

    private final List<ItemPedido> items = List.of(new ItemPedido(1, "Hamburguesa", 15000, 2));

    @Test
    void creadorDomicilioFabricaPedidoDomicilio() {
        PedidoCreator creador = new PedidoDomicilioCreator();
        Pedido p = creador.crear("PED-0001", DatosCliente.paraDomicilio("Ana", "3001234567", "Calle 1 #2-3"), items);
        assertTrue(p instanceof PedidoDomicilio);
        assertEquals(Modalidad.DOMICILIO, p.getModalidad());
    }

    @Test
    void creadorMesaFabricaPedidoMesa() {
        PedidoCreator creador = new PedidoMesaCreator();
        Pedido p = creador.crear("PED-0002", DatosCliente.paraMesa(4), items);
        assertTrue(p instanceof PedidoMesa);
        assertEquals(Modalidad.MESA, p.getModalidad());
    }

    @Test
    void creadorParaLlevarFabricaPedidoParaLlevar() {
        PedidoCreator creador = new PedidoParaLlevarCreator();
        Pedido p = creador.crear("PED-0003", DatosCliente.paraLlevar("Luis"), items);
        assertTrue(p instanceof PedidoParaLlevar);
        assertEquals(Modalidad.PARA_LLEVAR, p.getModalidad());
    }

    @Test
    void pedidoNuevoEmpiezaPendienteConIdYFecha() {
        Pedido p = new PedidoMesaCreator().crear("PED-0007", DatosCliente.paraMesa(1), items);
        assertEquals("PED-0007", p.getId());
        assertEquals(EstadoPedido.PENDIENTE, p.getEstado());
        assertTrue(p.getFechaCreacion() != null);
    }

    @Test
    void domicilioSinDireccionLanzaError() {
        PedidoCreator creador = new PedidoDomicilioCreator();
        DatosCliente sinDireccion = DatosCliente.paraDomicilio("Ana", "300", "  ");
        assertThrows(IllegalArgumentException.class, () -> creador.crear("PED-0001", sinDireccion, items));
    }

    @Test
    void mesaConNumeroInvalidoLanzaError() {
        PedidoCreator creador = new PedidoMesaCreator();
        assertThrows(IllegalArgumentException.class,
                () -> creador.crear("PED-0001", DatosCliente.paraMesa(0), items));
    }

    @Test
    void paraLlevarSinNombreLanzaError() {
        PedidoCreator creador = new PedidoParaLlevarCreator();
        assertThrows(IllegalArgumentException.class,
                () -> creador.crear("PED-0001", DatosCliente.paraLlevar(""), items));
    }

    @Test
    void pedidoSinItemsLanzaError() {
        PedidoCreator creador = new PedidoMesaCreator();
        List<ItemPedido> vacia = List.of();
        assertThrows(IllegalArgumentException.class,
                () -> creador.crear("PED-0001", DatosCliente.paraMesa(1), vacia));
    }

    @Test
    void restaurarConservaIdEstadoFechaYDatos() {
        PedidoCreator creador = new PedidoDomicilioCreator();
        Pedido original = creador.crear("PED-0009",
                DatosCliente.paraDomicilio("Ana", "3001234567", "Calle 1 #2-3"), items);
        original.avanzarEstado();

        Pedido copia = creador.restaurar(PedidoGuardado.desde(original));

        assertTrue(copia instanceof PedidoDomicilio);
        assertEquals("PED-0009", copia.getId());
        assertEquals(EstadoPedido.EN_PREPARACION, copia.getEstado());
        assertEquals(original.getFechaCreacion(), copia.getFechaCreacion());
        assertEquals("Ana", copia.getDatos().getNombre());
        assertEquals("Calle 1 #2-3", copia.getDatos().getDireccion());
        assertEquals(original.getTotal(), copia.getTotal());
    }

    @Test
    void restaurarConCreadorDeOtraModalidadLanzaError() {
        Pedido mesa = new PedidoMesaCreator().crear("PED-0001", DatosCliente.paraMesa(2), items);
        PedidoGuardado guardado = PedidoGuardado.desde(mesa);
        PedidoCreator equivocado = new PedidoDomicilioCreator();
        assertThrows(IllegalArgumentException.class, () -> equivocado.restaurar(guardado));
    }

    @Test
    void cadaCreadorReportaSuModalidad() {
        assertEquals(Modalidad.DOMICILIO, new PedidoDomicilioCreator().getModalidad());
        assertEquals(Modalidad.MESA, new PedidoMesaCreator().getModalidad());
        assertEquals(Modalidad.PARA_LLEVAR, new PedidoParaLlevarCreator().getModalidad());
    }

    @Test
    void fechaDelPedidoNuevoEsReciente() {
        LocalDateTime antes = LocalDateTime.now().minusSeconds(2);
        Pedido p = new PedidoMesaCreator().crear("PED-0001", DatosCliente.paraMesa(1), items);
        assertTrue(p.getFechaCreacion().isAfter(antes));
    }
}
