package pedidofacil.controllers;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.scene.control.TableColumn;

import pedidofacil.models.Pedido;
import pedidofacil.util.Formato;

final class TablaPedidos {

    private TablaPedidos() {
    }

    static void configurar(TableColumn<Pedido, String> colId, TableColumn<Pedido, String> colFecha,
                           TableColumn<Pedido, String> colModalidad, TableColumn<Pedido, String> colEntrega,
                           TableColumn<Pedido, String> colArticulos, TableColumn<Pedido, String> colTotal,
                           TableColumn<Pedido, String> colEstado) {
        colId.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getId()));
        colFecha.setCellValueFactory(c -> new ReadOnlyStringWrapper(Formato.fechaHora(c.getValue().getFechaCreacion())));
        colModalidad.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getModalidad().getEtiqueta()));
        colEntrega.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getDescripcionEntrega()));
        colArticulos.setCellValueFactory(c -> new ReadOnlyStringWrapper(String.valueOf(c.getValue().getCantidadArticulos())));
        colTotal.setCellValueFactory(c -> new ReadOnlyStringWrapper(Formato.moneda(c.getValue().getTotal())));
        colEstado.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getEstado().getEtiqueta()));
    }
}
