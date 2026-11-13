package pedidofacil.controllers;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

import pedidofacil.services.PedidoService;
import pedidofacil.services.ProductoService;

public class MainController implements UsaServicios {

    @FXML private Label lblTitulo;
    @FXML private StackPane contenedor;
    @FXML private Button btnInicio;
    @FXML private Button btnPedidos;
    @FXML private Button btnNuevoPedido;
    @FXML private Button btnProductos;
    @FXML private Button btnHistorial;

    private ProductoService productoService;
    private PedidoService pedidoService;
    private List<Button> botonesMenu;

    @Override
    public void inicializar(ProductoService productoService, PedidoService pedidoService) {
        this.productoService = productoService;
        this.pedidoService = pedidoService;
        this.botonesMenu = List.of(btnInicio, btnPedidos, btnNuevoPedido, btnProductos, btnHistorial);
        mostrarInicio();
    }

    @FXML
    private void mostrarInicio() {
        mostrar("Inicio", "/views/InicioView.fxml", btnInicio);
    }

    @FXML
    private void mostrarPedidos() {
        mostrar("Pedidos activos", "/views/PedidosView.fxml", btnPedidos);
    }

    @FXML
    private void mostrarNuevoPedido() {
        mostrar("Nuevo pedido", "/views/NuevoPedidoView.fxml", btnNuevoPedido);
    }

    @FXML
    private void mostrarProductos() {
        mostrar("Productos", "/views/ProductosView.fxml", btnProductos);
    }

    @FXML
    private void mostrarHistorial() {
        mostrar("Historial", "/views/HistorialView.fxml", btnHistorial);
    }

    private void mostrar(String titulo, String rutaFxml, Button botonActivo) {
        try {
            FXMLLoader cargador = new FXMLLoader(getClass().getResource(rutaFxml));
            Parent vista = cargador.load();
            Object controlador = cargador.getController();
            if (controlador instanceof UsaServicios usaServicios) {
                usaServicios.inicializar(productoService, pedidoService);
            }
            contenedor.getChildren().setAll(vista);
            lblTitulo.setText(titulo);
            marcarActivo(botonActivo);
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo cargar la vista " + rutaFxml, e);
        }
    }

    private void marcarActivo(Button activo) {
        for (Button boton : botonesMenu) {
            boton.getStyleClass().remove("boton-menu-activo");
        }
        activo.getStyleClass().add("boton-menu-activo");
    }
}
