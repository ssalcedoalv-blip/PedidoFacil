package pedidofacil.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import pedidofacil.models.ItemPedido;
import pedidofacil.models.Pedido;

public final class Formato {

    private static final DateTimeFormatter FECHA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private Formato() {
    }

    public static String moneda(int valor) {
        DecimalFormatSymbols simbolos = new DecimalFormatSymbols(Locale.ROOT);
        simbolos.setGroupingSeparator('.');
        return "$" + new DecimalFormat("#,##0", simbolos).format(valor);
    }

    public static String fechaHora(LocalDateTime fecha) {
        return FECHA_HORA.format(fecha);
    }

    public static String linea(ItemPedido item) {
        return item.getCantidad() + " x " + item.getNombre() + "  " + moneda(item.getSubtotal());
    }

    public static List<String> lineasDetalle(Pedido pedido) {
        List<String> lineas = new ArrayList<>();
        lineas.add("Pedido " + pedido.getId() + " - " + pedido.getEstado().getEtiqueta());
        lineas.add("Fecha: " + fechaHora(pedido.getFechaCreacion()));
        lineas.add("Modalidad: " + pedido.getModalidad().getEtiqueta());
        lineas.add("Entrega: " + pedido.getDescripcionEntrega());
        if (!pedido.getDatos().getTelefono().isEmpty()) {
            lineas.add("Teléfono: " + pedido.getDatos().getTelefono());
        }
        lineas.add("");
        for (ItemPedido item : pedido.getItems()) {
            lineas.add(linea(item));
        }
        lineas.add("");
        lineas.add("Subtotal: " + moneda(pedido.getSubtotal()));
        if (pedido.getCostoAdicional() > 0) {
            lineas.add("Costo adicional: " + moneda(pedido.getCostoAdicional()));
        }
        lineas.add("Total: " + moneda(pedido.getTotal()));
        return lineas;
    }
}
