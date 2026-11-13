package pedidofacil.models;

import java.time.LocalDateTime;
import java.util.List;

public class PedidoMesa extends Pedido {

    private final DatosCliente datos;

    public PedidoMesa(String id, DatosCliente datos, List<ItemPedido> items,
                      LocalDateTime fechaCreacion, EstadoPedido estado) {
        super(id, items, fechaCreacion, estado);
        if (datos == null || datos.getNumeroMesa() <= 0) {
            throw new IllegalArgumentException("El número de mesa debe ser mayor que cero");
        }
        this.datos = datos;
    }

    @Override
    public Modalidad getModalidad() {
        return Modalidad.MESA;
    }

    @Override
    public int getCostoAdicional() {
        return 0;
    }

    @Override
    public String getDescripcionEntrega() {
        return "Mesa " + datos.getNumeroMesa();
    }

    @Override
    public DatosCliente getDatos() {
        return datos;
    }
}
