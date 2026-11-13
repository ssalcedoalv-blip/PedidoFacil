# Resultados de las pruebas

Hay 81 pruebas JUnit 5 que cubren modelos, fábricas, servicios, almacenamiento con archivos reales y formato. **No hay pruebas automáticas de la interfaz JavaFX**; esa parte se probó a mano con `mvn javafx:run`.

| Clase de prueba | Pruebas | Qué cubre |
|---|---:|---|
| `PedidoCreatorTest` | 12 | Qué pedido fabrica cada creador, validaciones, `restaurar` |
| `CalculosTest` | 15 | Subtotales, costo adicional, totales |
| `EstadoPedidoTest` | 7 | Avance de estados y errores al pasarse del final |
| `AlmacenamientoTest` | 11 | JSON real en carpeta temporal: guardar, cargar, tildes, archivo vacío o dañado |
| `PedidoServiceTest` | 16 | Ids, creación, avance de estado, listados, persistencia ante fallos |
| `ProductoServiceTest` | 14 | Agregar, editar, eliminar, nombres repetidos, productos de ejemplo |
| `FormatoTest` | 5 | Dinero, fechas y texto de detalle |
| `ModalidadesRegistradasTest` | 1 | Toda `Modalidad` tiene fábrica en el servicio por defecto |
| **Total** | **81** | |

## Qué está verificado y qué no

| Resultado | Dónde se obtuvo | Estado |
|---|---|---|
| `Tests run: 75, Failures: 0, Errors: 0` (Etapas 1 a 3, `mvn test`, JUnit y Gson reales) | Tu entorno (Windows, Maven 3.10.0, JDK 21) | Verificado |
| `Tests run: 81, Failures: 0, Errors: 0` | Un ejecutor propio de JUnit más el Gson 2.10 real (el `pom.xml` pide 2.10.1), con Java 21 | Verificado, pero **no** es `mvn test` |
| `Tests run: 81, Failures: 0, Errors: 0, Skipped: 0`, `BUILD SUCCESS` (Etapa 5, `mvn test`, 9 de noviembre de 2026) | Tu entorno | Verificado |
| Interfaz JavaFX de la Etapa 4 | `mvn javafx:run` en tu entorno | Probada a mano; sin pruebas automáticas |

El ejecutor propio solo reemplaza a JUnit y Maven; las clases de `models`, `factories`, `services` y `util/Formato` se compilan y ejecutan tal cual. Los controladores, `Main` y `util/Mensajes` dependen de JavaFX y no se compilaron en ese entorno.

Salida de `mvn test` (Etapa 5, solo se muestra el final de la salida):

```
[INFO]  T E S T S
[INFO] Running pedidofacil.factories.PedidoCreatorTest
[INFO] Tests run: 12, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running pedidofacil.models.CalculosTest
[INFO] Tests run: 15, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running pedidofacil.models.EstadoPedidoTest
[INFO] Tests run: 7, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running pedidofacil.services.AlmacenamientoTest
[INFO] Tests run: 11, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running pedidofacil.services.ModalidadesRegistradasTest
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running pedidofacil.services.PedidoServiceTest
[INFO] Tests run: 16, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running pedidofacil.services.ProductoServiceTest
[INFO] Tests run: 14, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running pedidofacil.util.FormatoTest
[INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0
[INFO] Results:
[INFO] Tests run: 81, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
[INFO] Total time:  15.243 s
```

(Se quitaron los tiempos por clase para que quepa en una línea cada resultado.)
