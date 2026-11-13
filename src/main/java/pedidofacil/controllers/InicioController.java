package pedidofacil.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

import pedidofacil.models.EstadoPedido;
import pedidofacil.services.PedidoService;
import pedidofacil.services.ProductoService;
import pedidofacil.util.Formato;

public class InicioController implements UsaServicios {

    @FXML private Label lblPendientes;
    @FXML private Label lblEnPreparacion;
    @FXML private Label lblListos;
    @FXML private Label lblEntregados;
    @FXML private Label lblTotalVendido;
    @FXML private Label lblProductos;

    @Override
    public void inicializar(ProductoService productoService, PedidoService pedidoService) {
        lblPendientes.setText(String.valueOf(pedidoService.contarPorEstado(EstadoPedido.PENDIENTE)));
        lblEnPreparacion.setText(String.valueOf(pedidoService.contarPorEstado(EstadoPedido.EN_PREPARACION)));
        lblListos.setText(String.valueOf(pedidoService.contarPorEstado(EstadoPedido.LISTO)));
        lblEntregados.setText(String.valueOf(pedidoService.contarPorEstado(EstadoPedido.ENTREGADO)));
        lblTotalVendido.setText(Formato.moneda(pedidoService.totalVendido()));
        lblProductos.setText(String.valueOf(productoService.listar().size()));
    }
}
