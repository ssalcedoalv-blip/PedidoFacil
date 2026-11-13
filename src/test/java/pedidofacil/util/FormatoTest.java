package pedidofacil.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import pedidofacil.factories.PedidoDomicilioCreator;
import pedidofacil.factories.PedidoMesaCreator;
import pedidofacil.models.DatosCliente;
import pedidofacil.models.ItemPedido;
import pedidofacil.models.Pedido;

class FormatoTest {

    @Test
    void monedaUsaPuntoComoSeparadorDeMiles() {
        assertEquals("$0", Formato.moneda(0));
        assertEquals("$500", Formato.moneda(500));
        assertEquals("$15.000", Formato.moneda(15000));
        assertEquals("$1.250.000", Formato.moneda(1250000));
    }

    @Test
    void fechaHoraTieneFormatoDiaMesAnioHoraMinuto() {
        assertEquals("09/11/2026 14:05", Formato.fechaHora(LocalDateTime.of(2026, 11, 9, 14, 5)));
    }

    @Test
    void lineaDeItemMuestraCantidadNombreYSubtotal() {
        assertEquals("2 x Hamburguesa  $30.000", Formato.linea(new ItemPedido(1, "Hamburguesa", 15000, 2)));
    }

    @Test
    void detalleDeDomicilioIncluyeTelefonoYCostoAdicional() {
        Pedido p = new PedidoDomicilioCreator().crear("PED-0001",
                DatosCliente.paraDomicilio("Ana", "3001234567", "Calle 1"),
                List.of(new ItemPedido(1, "Hamburguesa", 15000, 2)));
        List<String> lineas = Formato.lineasDetalle(p);
        assertTrue(lineas.contains("Pedido PED-0001 - Pendiente"));
        assertTrue(lineas.contains("Teléfono: 3001234567"));
        assertTrue(lineas.contains("2 x Hamburguesa  $30.000"));
        assertTrue(lineas.contains("Costo adicional: $5.000"));
        assertEquals("Total: $35.000", lineas.get(lineas.size() - 1));
    }

    @Test
    void detalleDeMesaNoMuestraTelefonoNiCostoAdicional() {
        Pedido p = new PedidoMesaCreator().crear("PED-0002", DatosCliente.paraMesa(4),
                List.of(new ItemPedido(7, "Gaseosa", 4000, 1)));
        List<String> lineas = Formato.lineasDetalle(p);
        assertTrue(lineas.contains("Entrega: Mesa 4"));
        assertFalse(lineas.stream().anyMatch(l -> l.startsWith("Teléfono")));
        assertFalse(lineas.stream().anyMatch(l -> l.startsWith("Costo adicional")));
        assertEquals("Total: $4.000", lineas.get(lineas.size() - 1));
    }
}
