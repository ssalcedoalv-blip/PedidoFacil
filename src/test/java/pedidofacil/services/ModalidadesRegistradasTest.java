package pedidofacil.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;

import org.junit.jupiter.api.Test;

import pedidofacil.models.DatosCliente;
import pedidofacil.models.ItemPedido;
import pedidofacil.models.Modalidad;
import pedidofacil.models.Pedido;

/**
 * Guarda contra un olvido: si alguien agrega un valor a Modalidad y no registra su fábrica
 * en PedidoService, todo compila pero crear ese pedido falla en ejecución. Esta prueba lo detecta.
 */
class ModalidadesRegistradasTest {

    @Test
    void todaModalidadTieneFabricaEnElServicioPorDefecto() {
        PedidoService servicio = new PedidoService(new AlmacenamientoMemoria());
        // Datos completos: así cada modalidad encuentra lo que necesita
        DatosCliente completos = new DatosCliente("Ana", "300", "Calle 1", 4);
        List<ItemPedido> items = List.of(new ItemPedido(1, "Gaseosa", 4000, 1));
        for (Modalidad m : Modalidad.values()) {
            try {
                Pedido p = servicio.crearPedido(m, completos, items);
                assertEquals(m, p.getModalidad());
            } catch (RuntimeException e) {
                fail("La modalidad " + m + " no se puede crear: " + e.getMessage());
            }
        }
    }
}
