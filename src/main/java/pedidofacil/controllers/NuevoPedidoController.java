package pedidofacil.controllers;

import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;

import pedidofacil.models.DatosCliente;
import pedidofacil.models.ItemPedido;
import pedidofacil.models.Modalidad;
import pedidofacil.models.Pedido;
import pedidofacil.models.PedidoDomicilio;
import pedidofacil.models.Producto;
import pedidofacil.services.PedidoService;
import pedidofacil.services.ProductoService;
import pedidofacil.util.Formato;
import pedidofacil.util.Mensajes;

public class NuevoPedidoController implements UsaServicios {

    @FXML private ComboBox<Modalidad> cmbModalidad;
    @FXML private TextField txtNombre;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtDireccion;
    @FXML private TextField txtMesa;
    @FXML private ComboBox<Producto> cmbProducto;
    @FXML private Spinner<Integer> spnCantidad;
    @FXML private ListView<ItemPedido> lstCarrito;
    @FXML private Label lblSubtotal;
    @FXML private Label lblAdicional;
    @FXML private Label lblTotal;
    @FXML private Label lblMensaje;

    private PedidoService pedidoService;
    private final ObservableList<ItemPedido> carrito = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        cmbModalidad.getItems().setAll(Modalidad.values());
        cmbModalidad.getSelectionModel().selectFirst();
        cmbModalidad.setConverter(new StringConverter<>() {
            @Override
            public String toString(Modalidad m) {
                return m == null ? "" : m.getEtiqueta();
            }

            @Override
            public Modalidad fromString(String texto) {
                return null;
            }
        });
        cmbModalidad.valueProperty().addListener((obs, anterior, actual) -> actualizarTotales());

        cmbProducto.setConverter(new StringConverter<>() {
            @Override
            public String toString(Producto p) {
                return p == null ? "" : p.getNombre() + " - " + Formato.moneda(p.getPrecio());
            }

            @Override
            public Producto fromString(String texto) {
                return null;
            }
        });

        spnCantidad.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 99, 1));

        lstCarrito.setItems(carrito);
        lstCarrito.setCellFactory(lista -> new ListCell<>() {
            @Override
            protected void updateItem(ItemPedido item, boolean vacio) {
                super.updateItem(item, vacio);
                setText(vacio || item == null ? null : Formato.linea(item));
            }
        });
        carrito.addListener((javafx.collections.ListChangeListener<ItemPedido>) cambio -> actualizarTotales());
        actualizarTotales();
    }

    @Override
    public void inicializar(ProductoService productoService, PedidoService pedidoService) {
        this.pedidoService = pedidoService;
        cmbProducto.getItems().setAll(productoService.listar());
    }

    @FXML
    private void agregarProducto() {
        Producto producto = cmbProducto.getValue();
        if (producto == null) {
            Mensajes.error(lblMensaje, "Elige un producto");
            return;
        }
        int cantidad = spnCantidad.getValue();
        for (int i = 0; i < carrito.size(); i++) {
            ItemPedido existente = carrito.get(i);
            if (existente.getProductoId() == producto.getId()) {
                carrito.set(i, new ItemPedido(producto, existente.getCantidad() + cantidad));
                Mensajes.limpiar(lblMensaje);
                return;
            }
        }
        carrito.add(new ItemPedido(producto, cantidad));
        Mensajes.limpiar(lblMensaje);
    }

    @FXML
    private void quitarProducto() {
        ItemPedido seleccionado = lstCarrito.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            Mensajes.error(lblMensaje, "Selecciona un producto de la lista para quitarlo");
            return;
        }
        carrito.remove(seleccionado);
        Mensajes.limpiar(lblMensaje);
    }

    @FXML
    private void crearPedido() {
        Modalidad modalidad = cmbModalidad.getValue();
        int mesa = 0;
        String textoMesa = txtMesa.getText() == null ? "" : txtMesa.getText().trim();
        if (!textoMesa.isEmpty()) {
            try {
                mesa = Integer.parseInt(textoMesa);
            } catch (NumberFormatException e) {
                Mensajes.error(lblMensaje, "El número de mesa debe ser un número entero");
                return;
            }
        }
        DatosCliente datos = new DatosCliente(txtNombre.getText(), txtTelefono.getText(),
                txtDireccion.getText(), mesa);
        try {
            Pedido pedido = pedidoService.crearPedido(modalidad, datos, new ArrayList<>(carrito));
            limpiarFormulario();
            Mensajes.ok(lblMensaje, "Pedido " + pedido.getId() + " creado. Total: " + Formato.moneda(pedido.getTotal()));
        } catch (RuntimeException e) {
            Mensajes.error(lblMensaje, e.getMessage());
        }
    }

    private void limpiarFormulario() {
        txtNombre.clear();
        txtTelefono.clear();
        txtDireccion.clear();
        txtMesa.clear();
        carrito.clear();
        cmbProducto.getSelectionModel().clearSelection();
        spnCantidad.getValueFactory().setValue(1);
    }

    private void actualizarTotales() {
        int subtotal = 0;
        for (ItemPedido item : carrito) {
            subtotal += item.getSubtotal();
        }
        int adicional = costoAdicional(cmbModalidad.getValue());
        lblSubtotal.setText(Formato.moneda(subtotal));
        lblAdicional.setText(Formato.moneda(adicional));
        lblTotal.setText(Formato.moneda(subtotal + adicional));
    }

    private int costoAdicional(Modalidad modalidad) {
        return modalidad == Modalidad.DOMICILIO ? PedidoDomicilio.COSTO_DOMICILIO : 0;
    }
}
