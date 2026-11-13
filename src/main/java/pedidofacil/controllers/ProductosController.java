package pedidofacil.controllers;

import java.util.Optional;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import pedidofacil.models.Producto;
import pedidofacil.services.PedidoService;
import pedidofacil.services.ProductoService;
import pedidofacil.util.Formato;
import pedidofacil.util.Mensajes;

public class ProductosController implements UsaServicios {

    @FXML private TableView<Producto> tblProductos;
    @FXML private TableColumn<Producto, String> colId;
    @FXML private TableColumn<Producto, String> colNombre;
    @FXML private TableColumn<Producto, String> colPrecio;
    @FXML private TextField txtNombre;
    @FXML private TextField txtPrecio;
    @FXML private Button btnAgregar;
    @FXML private Button btnGuardar;
    @FXML private Button btnEliminar;
    @FXML private Label lblMensaje;

    private ProductoService productoService;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(c -> new ReadOnlyStringWrapper(String.valueOf(c.getValue().getId())));
        colNombre.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getNombre()));
        colPrecio.setCellValueFactory(c -> new ReadOnlyStringWrapper(Formato.moneda(c.getValue().getPrecio())));
        tblProductos.getSelectionModel().selectedItemProperty()
                .addListener((obs, anterior, actual) -> alSeleccionar(actual));
        alSeleccionar(null);
    }

    @Override
    public void inicializar(ProductoService productoService, PedidoService pedidoService) {
        this.productoService = productoService;
        recargar();
    }

    @FXML
    private void agregarProducto() {
        try {
            Producto nuevo = productoService.agregar(txtNombre.getText(), leerPrecio());
            recargar();
            limpiarFormulario();
            Mensajes.ok(lblMensaje, "Producto agregado: " + nuevo.getNombre());
        } catch (RuntimeException e) {
            Mensajes.error(lblMensaje, e.getMessage());
        }
    }

    @FXML
    private void guardarCambios() {
        Producto seleccionado = tblProductos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            Mensajes.error(lblMensaje, "Selecciona un producto de la tabla");
            return;
        }
        try {
            Producto editado = productoService.editar(seleccionado.getId(), txtNombre.getText(), leerPrecio());
            recargar();
            limpiarFormulario();
            Mensajes.ok(lblMensaje, "Producto actualizado: " + editado.getNombre());
        } catch (RuntimeException e) {
            Mensajes.error(lblMensaje, e.getMessage());
        }
    }

    @FXML
    private void eliminarProducto() {
        Producto seleccionado = tblProductos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            Mensajes.error(lblMensaje, "Selecciona un producto de la tabla");
            return;
        }
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION,
                "¿Eliminar \"" + seleccionado.getNombre() + "\" del menú?\nLos pedidos ya hechos no se afectan.",
                ButtonType.YES, ButtonType.NO);
        confirmacion.setHeaderText(null);
        Optional<ButtonType> respuesta = confirmacion.showAndWait();
        if (respuesta.isPresent() && respuesta.get() == ButtonType.YES) {
            try {
                productoService.eliminar(seleccionado.getId());
                recargar();
                limpiarFormulario();
                Mensajes.ok(lblMensaje, "Producto eliminado: " + seleccionado.getNombre());
            } catch (RuntimeException e) {
                Mensajes.error(lblMensaje, e.getMessage());
            }
        }
    }

    @FXML
    private void limpiarFormulario() {
        tblProductos.getSelectionModel().clearSelection();
        txtNombre.clear();
        txtPrecio.clear();
    }

    private int leerPrecio() {
        String texto = txtPrecio.getText() == null ? "" : txtPrecio.getText().trim().replace(".", "");
        try {
            return Integer.parseInt(texto);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("El precio debe ser un número entero, por ejemplo 15000");
        }
    }

    private void recargar() {
        tblProductos.getItems().setAll(productoService.listar());
    }

    private void alSeleccionar(Producto producto) {
        boolean haySeleccion = producto != null;
        btnGuardar.setDisable(!haySeleccion);
        btnEliminar.setDisable(!haySeleccion);
        if (haySeleccion) {
            txtNombre.setText(producto.getNombre());
            txtPrecio.setText(String.valueOf(producto.getPrecio()));
        }
    }
}
