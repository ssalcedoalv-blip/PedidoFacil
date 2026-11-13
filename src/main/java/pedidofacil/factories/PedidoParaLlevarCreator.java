package pedidofacil.factories;

import java.time.LocalDateTime;
import java.util.List;

import pedidofacil.models.DatosCliente;
import pedidofacil.models.EstadoPedido;
import pedidofacil.models.ItemPedido;
import pedidofacil.models.Modalidad;
import pedidofacil.models.Pedido;
import pedidofacil.models.PedidoParaLlevar;

public class PedidoParaLlevarCreator extends PedidoCreator {

    @Override
    public Modalidad getModalidad() {
        return Modalidad.PARA_LLEVAR;
    }

    @Override
    protected Pedido crearPedido(String id, DatosCliente datos, List<ItemPedido> items,
                                 LocalDateTime fecha, EstadoPedido estado) {
        return new PedidoParaLlevar(id, datos, items, fecha, estado);
    }
}
