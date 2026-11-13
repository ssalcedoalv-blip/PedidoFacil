package pedidofacil.services;

import java.util.List;

import pedidofacil.models.PedidoGuardado;
import pedidofacil.models.Producto;

public interface Almacenamiento {

    boolean hayProductos();

    List<Producto> cargarProductos();

    void guardarProductos(List<Producto> productos);

    List<PedidoGuardado> cargarPedidos();

    void guardarPedidos(List<PedidoGuardado> pedidos);
}
