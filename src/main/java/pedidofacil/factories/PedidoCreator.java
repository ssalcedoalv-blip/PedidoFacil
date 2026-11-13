package pedidofacil.factories;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import pedidofacil.models.DatosCliente;
import pedidofacil.models.EstadoPedido;
import pedidofacil.models.ItemPedido;
import pedidofacil.models.Modalidad;
import pedidofacil.models.Pedido;
import pedidofacil.models.PedidoGuardado;

public abstract class PedidoCreator {

    public abstract Modalidad getModalidad();

    protected abstract Pedido crearPedido(String id, DatosCliente datos, List<ItemPedido> items,
                                          LocalDateTime fecha, EstadoPedido estado);

    public final Pedido crear(String id, DatosCliente datos, List<ItemPedido> items) {
        LocalDateTime ahora = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        return crearPedido(id, datos, items, ahora, EstadoPedido.PENDIENTE);
    }

    public final Pedido restaurar(PedidoGuardado guardado) {
        if (guardado.getModalidad() != getModalidad()) {
            throw new IllegalArgumentException("Este creador es de " + getModalidad()
                    + " pero el pedido guardado es de " + guardado.getModalidad());
        }
        return crearPedido(guardado.getId(), guardado.getDatos(), guardado.getItems(),
                guardado.getFechaCreacion(), guardado.getEstado());
    }
}
