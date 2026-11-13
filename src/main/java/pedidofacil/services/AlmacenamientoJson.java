package pedidofacil.services;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;

import pedidofacil.models.PedidoGuardado;
import pedidofacil.models.Producto;

public class AlmacenamientoJson implements Almacenamiento {

    private static final String ARCHIVO_PRODUCTOS = "productos.json";
    private static final String ARCHIVO_PEDIDOS = "pedidos.json";

    private final Path carpeta;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public AlmacenamientoJson() {
        this(Path.of("data"));
    }

    public AlmacenamientoJson(Path carpeta) {
        this.carpeta = carpeta;
    }

    @Override
    public boolean hayProductos() {
        return Files.exists(carpeta.resolve(ARCHIVO_PRODUCTOS));
    }

    @Override
    public List<Producto> cargarProductos() {
        Producto[] datos = leer(ARCHIVO_PRODUCTOS, Producto[].class);
        return datos == null ? new ArrayList<>() : new ArrayList<>(Arrays.asList(datos));
    }

    @Override
    public void guardarProductos(List<Producto> productos) {
        escribir(ARCHIVO_PRODUCTOS, productos);
    }

    @Override
    public List<PedidoGuardado> cargarPedidos() {
        PedidoGuardado[] datos = leer(ARCHIVO_PEDIDOS, PedidoGuardado[].class);
        return datos == null ? new ArrayList<>() : new ArrayList<>(Arrays.asList(datos));
    }

    @Override
    public void guardarPedidos(List<PedidoGuardado> pedidos) {
        escribir(ARCHIVO_PEDIDOS, pedidos);
    }

    private <T> T leer(String nombreArchivo, Class<T> tipo) {
        Path archivo = carpeta.resolve(nombreArchivo);
        if (!Files.exists(archivo)) {
            return null;
        }
        try {
            String texto = Files.readString(archivo, StandardCharsets.UTF_8);
            if (texto.isBlank()) {
                return null;
            }
            return gson.fromJson(texto, tipo);
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo leer " + archivo, e);
        } catch (JsonParseException e) {
            throw new IllegalStateException("El archivo " + archivo + " está dañado", e);
        }
    }

    private void escribir(String nombreArchivo, Object contenido) {
        Path destino = carpeta.resolve(nombreArchivo);
        Path temporal = carpeta.resolve(nombreArchivo + ".tmp");
        try {
            Files.createDirectories(carpeta);
            Files.writeString(temporal, gson.toJson(contenido), StandardCharsets.UTF_8);
            try {
                Files.move(temporal, destino, StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(temporal, destino, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo guardar " + destino, e);
        }
    }
}
