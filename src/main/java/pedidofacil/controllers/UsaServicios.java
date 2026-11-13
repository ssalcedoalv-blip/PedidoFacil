package pedidofacil.controllers;

import pedidofacil.services.PedidoService;
import pedidofacil.services.ProductoService;

public interface UsaServicios {

    void inicializar(ProductoService productoService, PedidoService pedidoService);
}
