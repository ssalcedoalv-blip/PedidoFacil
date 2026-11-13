package pedidofacil.models;

import java.time.LocalDateTime;
import java.util.List;

public class PedidoDomicilio extends Pedido {

    public static final int COSTO_DOMICILIO = 5000;

    private final DatosCliente datos;

    public PedidoDomicilio(String id, DatosCliente datos, List<ItemPedido> items,
                           LocalDateTime fechaCreacion, EstadoPedido estado) {
        super(id, items, fechaCreacion, estado);
        if (datos == null || datos.getNombre().isEmpty() || datos.getTelefono().isEmpty()
                || datos.getDireccion().isEmpty()) {
            throw new IllegalArgumentException("El domicilio necesita nombre, teléfono y dirección");
        }
        this.datos = datos;
    }

    @Override
    public Modalidad getModalidad() {
        return Modalidad.DOMICILIO;
    }

    @Override
    public int getCostoAdicional() {
        return COSTO_DOMICILIO;
    }

    @Override
    public String getDescripcionEntrega() {
        return datos.getDireccion() + " (" + datos.getNombre() + ")";
    }

    @Override
    public DatosCliente getDatos() {
        return datos;
    }
}
