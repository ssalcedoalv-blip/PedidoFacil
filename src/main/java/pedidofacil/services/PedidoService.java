package pedidofacil.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import pedidofacil.factories.PedidoCreator;
import pedidofacil.factories.PedidoDomicilioCreator;
import pedidofacil.factories.PedidoMesaCreator;
import pedidofacil.factories.PedidoParaLlevarCreator;
import pedidofacil.models.DatosCliente;
import pedidofacil.models.EstadoPedido;
import pedidofacil.models.ItemPedido;
import pedidofacil.models.Modalidad;
import pedidofacil.models.Pedido;
import pedidofacil.models.PedidoGuardado;

public class PedidoService {

    private static final String PREFIJO_ID = "PED-";

    private final Almacenamiento almacenamiento;
    private final Map<Modalidad, PedidoCreator> creadores = new EnumMap<>(Modalidad.class);
    private final List<Pedido> pedidos = new ArrayList<>();
    private int siguienteNumero = 1;

    public PedidoService(Almacenamiento almacenamiento) {
        this(almacenamiento, List.of(new PedidoDomicilioCreator(), new PedidoMesaCreator(),
                new PedidoParaLlevarCreator()));
    }

    public PedidoService(Almacenamiento almacenamiento, List<PedidoCreator> fabricas) {
        this.almacenamiento = almacenamiento;
        for (PedidoCreator fabrica : fabricas) {
            if (creadores.put(fabrica.getModalidad(), fabrica) != null) {
                throw new IllegalArgumentException("Hay dos fábricas para " + fabrica.getModalidad());
            }
        }
        for (PedidoGuardado guardado : almacenamiento.cargarPedidos()) {
            pedidos.add(fabricaDe(guardado.getModalidad()).restaurar(guardado));
            siguienteNumero = Math.max(siguienteNumero, numeroDe(guardado.getId()) + 1);
        }
    }

    public Pedido crearPedido(Modalidad modalidad, DatosCliente datos, List<ItemPedido> items) {
        PedidoCreator fabrica = fabricaDe(modalidad);
        Pedido pedido = fabrica.crear(formatearId(siguienteNumero), datos, items);
        pedidos.add(pedido);
        try {
            guardar();
        } catch (RuntimeException e) {
            pedidos.remove(pedido);   
            throw e;
        }
        siguienteNumero++;
        return pedido;
    }

    public Pedido avanzarEstado(String id) {
        Pedido pedido = buscarPorId(id)
                .orElseThrow(() -> new NoSuchElementException("No existe el pedido " + id));
        pedido.avanzarEstado();
        guardar();
        return pedido;
    }

    public Optional<Pedido> buscarPorId(String id) {
        return pedidos.stream().filter(p -> p.getId().equals(id)).findFirst();
    }

    public List<Pedido> listar() {
        List<Pedido> copia = new ArrayList<>(pedidos);
        copia.sort(Comparator.comparing(Pedido::getFechaCreacion).thenComparing(Pedido::getId).reversed());
        return Collections.unmodifiableList(copia);
    }

    public List<Pedido> listarActivos() {
        return listar().stream().filter(p -> !p.getEstado().esFinal()).toList();
    }

    public List<Pedido> listarHistorial() {
        return listar().stream().filter(p -> p.getEstado().esFinal()).toList();
    }

    public List<Pedido> listarPorEstado(EstadoPedido estado) {
        return listar().stream().filter(p -> p.getEstado() == estado).toList();
    }

    public int contarPorEstado(EstadoPedido estado) {
        return listarPorEstado(estado).size();
    }

    public int totalVendido() {
        return pedidos.stream().filter(p -> p.getEstado().esFinal()).mapToInt(Pedido::getTotal).sum();
    }

    private PedidoCreator fabricaDe(Modalidad modalidad) {
        PedidoCreator fabrica = creadores.get(modalidad);
        if (fabrica == null) {
            throw new IllegalArgumentException("No hay fábrica registrada para " + modalidad);
        }
        return fabrica;
    }

    private void guardar() {
        List<PedidoGuardado> copias = new ArrayList<>();
        for (Pedido p : pedidos) {
            copias.add(PedidoGuardado.desde(p));
        }
        almacenamiento.guardarPedidos(copias);
    }

    private static String formatearId(int numero) {
        return String.format("%s%04d", PREFIJO_ID, numero);
    }

    private static int numeroDe(String id) {
        try {
            return Integer.parseInt(id.substring(PREFIJO_ID.length()));
        } catch (RuntimeException e) {
            throw new IllegalStateException("Id de pedido con formato inválido: " + id, e);
        }
    }
}
