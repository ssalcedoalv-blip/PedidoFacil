package pedidofacil.app;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import pedidofacil.controllers.MainController;
import pedidofacil.services.Almacenamiento;
import pedidofacil.services.AlmacenamientoJson;
import pedidofacil.services.PedidoService;
import pedidofacil.services.ProductoService;

public class Main extends Application {

    @Override
    public void start(Stage ventana) throws Exception {
        ProductoService productos;
        PedidoService pedidos;
        try {
            Almacenamiento almacenamiento = new AlmacenamientoJson();
            productos = new ProductoService(almacenamiento);
            pedidos = new PedidoService(almacenamiento);
        } catch (RuntimeException e) {
            Alert alerta = new Alert(Alert.AlertType.ERROR, "No se pudieron cargar los datos:\n" + e.getMessage());
            alerta.setHeaderText("Error al iniciar PedidoFácil");
            alerta.showAndWait();
            Platform.exit();
            return;
        }

        FXMLLoader cargador = new FXMLLoader(getClass().getResource("/views/MainView.fxml"));
        Parent raiz = cargador.load();
        MainController controlador = cargador.getController();
        controlador.inicializar(productos, pedidos);

        Scene escena = new Scene(raiz, 1100, 700);
        escena.getStylesheets().add(getClass().getResource("/styles/estilos.css").toExternalForm());
        ventana.setTitle("PedidoFácil");
        ventana.setMinWidth(950);
        ventana.setMinHeight(600);
        ventana.setScene(escena);
        ventana.show();
    }

    public static void main(String[] args) {
        launch(args);  
    }
}
