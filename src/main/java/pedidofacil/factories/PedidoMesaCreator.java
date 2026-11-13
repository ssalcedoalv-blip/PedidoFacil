package pedidofacil.factories;

import java.time.LocalDateTime;
import java.util.List;

import pedidofacil.models.DatosCliente;
import pedidofacil.models.EstadoPedido;
import pedidofacil.models.ItemPedido;
import pedidofacil.models.Modalidad;
import pedidofacil.models.Pedido;
import pedidofacil.models.PedidoMesa;

public class PedidoMesaCreator extends PedidoCreator {

    @Override
    public Modalidad getModalidad() {
        return Modalidad.MESA;
    }

    @Override
    protected Pedido crearPedido(String id, DatosCliente datos, List<ItemPedido> items,
                                 LocalDateTime fecha, EstadoPedido estado) {
        return new PedidoMesa(id, datos, items, fecha, estado);
    }
}
