package pedidofacil.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PedidoGuardado {

    private String id;
    private Modalidad modalidad;
    private EstadoPedido estado;
    private String fechaCreacion;
    private String nombreCliente;
    private String telefono;
    private String direccion;
    private int numeroMesa;
    private List<ItemPedido> items;

    public static PedidoGuardado desde(Pedido pedido) {
        PedidoGuardado g = new PedidoGuardado();
        g.id = pedido.getId();
        g.modalidad = pedido.getModalidad();
        g.estado = pedido.getEstado();
        g.fechaCreacion = pedido.getFechaCreacion().toString();
        g.nombreCliente = pedido.getDatos().getNombre();
        g.telefono = pedido.getDatos().getTelefono();
        g.direccion = pedido.getDatos().getDireccion();
        g.numeroMesa = pedido.getDatos().getNumeroMesa();
        g.items = new ArrayList<>(pedido.getItems());
        return g;
    }

    public String getId() {
        return id;
    }

    public Modalidad getModalidad() {
        return modalidad;
    }

    public EstadoPedido getEstado() {
        return estado;
    }

    public LocalDateTime getFechaCreacion() {
        return LocalDateTime.parse(fechaCreacion);
    }

    public DatosCliente getDatos() {
        return new DatosCliente(nombreCliente, telefono, direccion, numeroMesa);
    }

    public List<ItemPedido> getItems() {
        return items == null ? new ArrayList<>() : new ArrayList<>(items);
    }
}
