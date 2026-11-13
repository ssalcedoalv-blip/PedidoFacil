package pedidofacil.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import pedidofacil.models.Pedido;
import pedidofacil.services.PedidoService;
import pedidofacil.services.ProductoService;
import pedidofacil.util.Formato;

public class HistorialController implements UsaServicios {

    @FXML private TableView<Pedido> tblHistorial;
    @FXML private TableColumn<Pedido, String> colId;
    @FXML private TableColumn<Pedido, String> colFecha;
    @FXML private TableColumn<Pedido, String> colModalidad;
    @FXML private TableColumn<Pedido, String> colEntrega;
    @FXML private TableColumn<Pedido, String> colArticulos;
    @FXML private TableColumn<Pedido, String> colTotal;
    @FXML private TableColumn<Pedido, String> colEstado;
    @FXML private ListView<String> lstDetalle;
    @FXML private Label lblTotalVendido;

    @FXML
    public void initialize() {
        TablaPedidos.configurar(colId, colFecha, colModalidad, colEntrega, colArticulos, colTotal, colEstado);
        tblHistorial.getSelectionModel().selectedItemProperty().addListener((obs, anterior, actual) -> {
            if (actual == null) {
                lstDetalle.getItems().clear();
            } else {
                lstDetalle.getItems().setAll(Formato.lineasDetalle(actual));
            }
        });
    }

    @Override
    public void inicializar(ProductoService productoService, PedidoService pedidoService) {
        tblHistorial.getItems().setAll(pedidoService.listarHistorial());
        lblTotalVendido.setText("Total vendido: " + Formato.moneda(pedidoService.totalVendido()));
    }
}
