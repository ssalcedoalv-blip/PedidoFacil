package pedidofacil.services;

import java.util.ArrayList;
import java.util.List;

import pedidofacil.models.PedidoGuardado;
import pedidofacil.models.Producto;

/** Almacenamiento falso para las pruebas: guarda todo en memoria, sin archivos. */
public class AlmacenamientoMemoria implements Almacenamiento {

    private List<Producto> productos = null;           // null = nunca se guardó
    private List<PedidoGuardado> pedidos = new ArrayList<>();
    public int vecesGuardadoPedidos = 0;
    public boolean fallarAlGuardarPedidos = false;

    @Override
    public boolean hayProductos() {
        return productos != null;
    }

    @Override
    public List<Producto> cargarProductos() {
        return productos == null ? new ArrayList<>() : new ArrayList<>(productos);
    }

    @Override
    public void guardarProductos(List<Producto> lista) {
        productos = new ArrayList<>(lista);
    }

    @Override
    public List<PedidoGuardado> cargarPedidos() {
        return new ArrayList<>(pedidos);
    }

    @Override
    public void guardarPedidos(List<PedidoGuardado> lista) {
        if (fallarAlGuardarPedidos) {
            throw new IllegalStateException("Fallo simulado al guardar");
        }
        pedidos = new ArrayList<>(lista);
        vecesGuardadoPedidos++;
    }
}
