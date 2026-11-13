package pedidofacil.models;

import java.time.LocalDateTime;
import java.util.List;

public class PedidoParaLlevar extends Pedido {

    private final DatosCliente datos;

    public PedidoParaLlevar(String id, DatosCliente datos, List<ItemPedido> items,
                            LocalDateTime fechaCreacion, EstadoPedido estado) {
        super(id, items, fechaCreacion, estado);
        if (datos == null || datos.getNombre().isEmpty()) {
            throw new IllegalArgumentException("El pedido para llevar necesita el nombre del cliente");
        }
        this.datos = datos;
    }

    @Override
    public Modalidad getModalidad() {
        return Modalidad.PARA_LLEVAR;
    }

    @Override
    public int getCostoAdicional() {
        return 0;
    }

    @Override
    public String getDescripcionEntrega() {
        return "Recoge " + datos.getNombre();
    }

    @Override
    public DatosCliente getDatos() {
        return datos;
    }
}
