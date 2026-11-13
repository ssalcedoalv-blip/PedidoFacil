package pedidofacil.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Pedido {

    private final String id;
    private final List<ItemPedido> items;
    private final LocalDateTime fechaCreacion;
    private EstadoPedido estado;

    protected Pedido(String id, List<ItemPedido> items, LocalDateTime fechaCreacion, EstadoPedido estado) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("El pedido necesita un id");
        }
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("El pedido debe tener al menos un producto");
        }
        for (ItemPedido item : items) {
            if (item == null) {   
                throw new IllegalArgumentException("El pedido tiene un item vacío");
            }
        }
        if (fechaCreacion == null || estado == null) {
            throw new IllegalArgumentException("Faltan la fecha o el estado del pedido");
        }
        this.id = id;
        this.items = new ArrayList<>(items);
        this.fechaCreacion = fechaCreacion;
        this.estado = estado;
    }


    public abstract Modalidad getModalidad();

    public abstract int getCostoAdicional();

    public abstract String getDescripcionEntrega();

    public abstract DatosCliente getDatos();

    public String getId() {
        return id;
    }

    public List<ItemPedido> getItems() {
        return Collections.unmodifiableList(items);
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public EstadoPedido getEstado() {
        return estado;
    }

    public int getSubtotal() {
        int suma = 0;
        for (ItemPedido item : items) {
            suma += item.getSubtotal();
        }
        return suma;
    }

    public int getTotal() {
        return getSubtotal() + getCostoAdicional();
    }

    public int getCantidadArticulos() {
        int suma = 0;
        for (ItemPedido item : items) {
            suma += item.getCantidad();
        }
        return suma;
    }

    public void avanzarEstado() {
        estado = estado.siguiente();
    }
}
