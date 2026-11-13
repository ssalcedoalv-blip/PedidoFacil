package pedidofacil.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import pedidofacil.models.Pedido;
import pedidofacil.services.PedidoService;
import pedidofacil.services.ProductoService;
import pedidofacil.util.Formato;
import pedidofacil.util.Mensajes;

public class PedidosController implements UsaServicios {

    private static final String TEXTO_BOTON = "Avanzar estado";

    @FXML private TableView<Pedido> tblPedidos;
    @FXML private TableColumn<Pedido, String> colId;
    @FXML private TableColumn<Pedido, String> colFecha;
    @FXML private TableColumn<Pedido, String> colModalidad;
    @FXML private TableColumn<Pedido, String> colEntrega;
    @FXML private TableColumn<Pedido, String> colArticulos;
    @FXML private TableColumn<Pedido, String> colTotal;
    @FXML private TableColumn<Pedido, String> colEstado;
    @FXML private ListView<String> lstDetalle;
    @FXML private Button btnAvanzar;
    @FXML private Label lblMensaje;

    private PedidoService pedidoService;

    @FXML
    public void initialize() {
        TablaPedidos.configurar(colId, colFecha, colModalidad, colEntrega, colArticulos, colTotal, colEstado);
        tblPedidos.getSelectionModel().selectedItemProperty()
                .addListener((obs, anterior, actual) -> mostrarDetalle(actual));
        mostrarDetalle(null);
    }

    @Override
    public void inicializar(ProductoService productoService, PedidoService pedidoService) {
        this.pedidoService = pedidoService;
        refrescar(null);
    }

    @FXML
    private void avanzarEstado() {
        Pedido seleccionado = tblPedidos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            Mensajes.error(lblMensaje, "Selecciona un pedido de la tabla");
            return;
        }
        try {
            Pedido actualizado = pedidoService.avanzarEstado(seleccionado.getId());
            refrescar(actualizado.getId());
            Mensajes.ok(lblMensaje, "Pedido " + actualizado.getId() + ": " + actualizado.getEstado().getEtiqueta());
        } catch (RuntimeException e) {
            Mensajes.error(lblMensaje, e.getMessage());
        }
    }

    private void refrescar(String idASeleccionar) {
        tblPedidos.getItems().setAll(pedidoService.listarActivos());
        if (idASeleccionar != null) {
            for (Pedido p : tblPedidos.getItems()) {
                if (p.getId().equals(idASeleccionar)) {
                    tblPedidos.getSelectionModel().select(p);
                    break;
                }
            }
        }
        mostrarDetalle(tblPedidos.getSelectionModel().getSelectedItem());
    }

    private void mostrarDetalle(Pedido pedido) {
        if (pedido == null) {
            lstDetalle.getItems().clear();
            btnAvanzar.setDisable(true);
            btnAvanzar.setText(TEXTO_BOTON);
            return;
        }
        lstDetalle.getItems().setAll(Formato.lineasDetalle(pedido));
        btnAvanzar.setDisable(pedido.getEstado().esFinal());
        String accion = pedido.getEstado().getTextoAccion();
        btnAvanzar.setText(accion == null ? TEXTO_BOTON : accion);
    }
}
