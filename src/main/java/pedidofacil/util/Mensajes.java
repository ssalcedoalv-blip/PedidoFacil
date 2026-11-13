package pedidofacil.util;

import javafx.scene.control.Label;

public final class Mensajes {

    private Mensajes() {
    }

    public static void ok(Label etiqueta, String texto) {
        mostrar(etiqueta, texto, "mensaje-ok");
    }

    public static void error(Label etiqueta, String texto) {
        mostrar(etiqueta, texto, "mensaje-error");
    }

    public static void limpiar(Label etiqueta) {
        etiqueta.setText("");
        etiqueta.getStyleClass().removeAll("mensaje-ok", "mensaje-error");
    }

    private static void mostrar(Label etiqueta, String texto, String clase) {
        limpiar(etiqueta);
        etiqueta.setText(texto);
        etiqueta.getStyleClass().add(clase);
    }
}
