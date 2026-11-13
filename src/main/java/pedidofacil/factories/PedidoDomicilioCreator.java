package pedidofacil.factories;

import java.time.LocalDateTime;
import java.util.List;

import pedidofacil.models.DatosCliente;
import pedidofacil.models.EstadoPedido;
import pedidofacil.models.ItemPedido;
import pedidofacil.models.Modalidad;
import pedidofacil.models.Pedido;
import pedidofacil.models.PedidoDomicilio;

public class PedidoDomicilioCreator extends PedidoCreator {

    @Override
    public Modalidad getModalidad() {
        return Modalidad.DOMICILIO;
    }

    @Override
    protected Pedido crearPedido(String id, DatosCliente datos, List<ItemPedido> items,
                                 LocalDateTime fecha, EstadoPedido estado) {
        return new PedidoDomicilio(id, datos, items, fecha, estado);
    }
}
