# Prueba de extensión: pedido programado

**Objetivo:** comprobar cuánto cuesta agregar una cuarta modalidad. Se hizo en una **copia** del proyecto (`PedidoFacil-extension-programado.zip` (copia aparte, no incluida en este proyecto)); el proyecto principal no incluye `PROGRAMADO`.

**Qué se agregó:** el pedido programado es un pedido que el cliente recoge en el local a una hora acordada. Pide nombre y hora (teléfono opcional), la hora no puede ser anterior a la creación del pedido y no cobra costo adicional.

**Resultado, contando lo que realmente hubo que tocar:**

| Capa | Archivos | Cambio (entre paréntesis, líneas del diff) |
|---|---|---|
| Fábrica (lo previsto) | `Modalidad` (existente) | +1 valor del enum (3 líneas) |
| | `PedidoService` (existente) | Registrar el creador nuevo en el constructor por defecto (5 líneas) |
| | `PedidoProgramado`, `PedidoProgramadoCreator` (nuevos) | La subclase y su creador |
| Dato nuevo (la hora) | `DatosCliente` (existente) | Campo `horaProgramada`, constructor de 5 parámetros, `paraProgramado(...)` (21 líneas) |
| | `PedidoGuardado` (existente) | Campo `horaProgramada` en el JSON y su lectura (6 líneas) |
| Interfaz | `NuevoPedidoController` (existente) | Leer y validar la hora (19 líneas) |
| | `NuevoPedidoView.fxml` (existente) | Un campo de texto nuevo con su etiqueta, y un texto de ayuda ajustado (4 líneas) |
| Pruebas | `PedidoProgramadoTest`, `ModalidadesRegistradasTest` (nuevas) | 10 pruebas nuevas |

- **Clases existentes modificadas: 6** (4 de lógica y 2 de interfaz). **Clases nuevas: 2** (más 2 archivos de pruebas).
- `PedidoCreator`, los otros tres creadores, `Pedido`, los otros tres pedidos, `ProductoService`, `AlmacenamientoJson` y el resto de controladores **no cambiaron**.
- **Las 80 pruebas anteriores pasaron sin modificar ninguna.** Con las nuevas, 90 en total (ejecutor propio, no `mvn test`).
- Solo la parte de Factory Method (`Modalidad`, `PedidoService`, 2 clases nuevas) cumplió lo previsto. Las otras 4 modificaciones existen porque la modalidad necesita un **dato nuevo** (la hora), que atraviesa `DatosCliente`, `PedidoGuardado` y el formulario. Por lo que se ve en el código, una modalidad que reutilice los datos que ya existen tocaría solo esas 2 clases (deducido, no probado).
- Los JSON de la Etapa 4 (sin el campo de hora) siguen cargando: hay una prueba con un `pedidos.json` en el formato antiguo.

**Hallazgos:**

1. Agregar solo el valor al `enum` compila y las 80 pruebas pasan, pero crear el pedido falla en ejecución ("No hay fábrica registrada para PROGRAMADO"): el combo del formulario sale de `Modalidad.values()`. Por eso se agregó `ModalidadesRegistradasTest` (también al proyecto principal); se comprobó que falla si se olvida registrar la fábrica.
2. `NuevoPedidoController.costoAdicional(...)` repite la regla "solo el domicilio cobra" para la vista previa del total. Programado no cobra, así que no se tocó, pero una modalidad con costo obligaría a editarlo además de su subclase (esto sale de leer el código; no se probó).
3. Cada modalidad nueva con datos propios hace crecer `DatosCliente` y `PedidoGuardado`.

**No verificado:** la parte de interfaz de la extensión (`NuevoPedidoController` y el FXML) solo se comprobó que esté sintácticamente bien (el controlador se analizó sin errores de sintaxis y el FXML es XML válido). No se compiló ni se ejecutó, porque requiere JavaFX. Falta `mvn javafx:run` en tu entorno.
