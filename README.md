# Automatización AMEX PF / eApply — Selenium + Java + TestNG

Suite de pruebas funcionales de la aplicación **AMEX PF / eApply Expediente**,
con trazabilidad a la matriz *Amex PF V2*: cada prueba lleva el ID del caso
(`PF_CP_004`, `VAL_001`, …) en su nombre y en su `description`.

Documentos de apoyo:

| Documento | Para qué |
|---|---|
| [`COMO_FUNCIONA_EL_CODIGO.md`](COMO_FUNCIONA_EL_CODIGO.md) | cómo se relacionan pruebas, páginas, selectores, datos y utilidades |
| [`GUIA_CASO_POR_CASO.md`](GUIA_CASO_POR_CASO.md) | por cada caso: qué pide la matriz, qué hace su código y el comando para correrlo solo |
| [`TRAZABILIDAD.md`](TRAZABILIDAD.md) / `TRAZABILIDAD.csv` | los 160 IDs de la matriz vs. lo automatizado, con lo pendiente y su motivo |
| [`PLAN_AUTOMATIZACION.md`](PLAN_AUTOMATIZACION.md) | plan por olas, convenciones y estrategia de regresión |

Versión verificada contra el ambiente DEV: **14 casos correctos, 0 fallas**
(el resto omitidos por permisos del perfil o por hallazgos abiertos; ver
[Resultado de la última ejecución](#8-resultado-de-la-última-ejecución)).

---

## 1. Qué se necesita instalar

| Herramienta | Versión | Para qué |
|---|---|---|
| **JDK 17** (Temurin/OpenJDK) | 17 o superior | compilar y ejecutar |
| **Maven** | 3.8+ | descargar dependencias y ejecutar las suites |
| **IntelliJ IDEA Community** | 2024.1+ | IDE recomendado (gratuito) |
| **Google Chrome** | actualizado | navegador de las pruebas |

No hace falta descargar `chromedriver`: desde Selenium 4.6 el **Selenium Manager**
lo resuelve solo.

### IDE recomendado: IntelliJ IDEA Community

Es el más cómodo para Maven + TestNG: reconoce el `pom.xml`, ejecuta una prueba
con clic derecho y trae el plugin de TestNG incluido. Alternativas válidas:
**Eclipse IDE for Java Developers** (con TestNG desde Marketplace) o
**VS Code** (extensiones *Extension Pack for Java* + *Test Runner for Java*).

### Instalación paso a paso (Windows)

1. **JDK 17**: descargar Temurin 17 (https://adoptium.net) → instalar con la
   opción *Set JAVA_HOME variable*. Verificar en una consola nueva:
   ```
   java -version
   ```
2. **Maven**: descargar el binario (https://maven.apache.org/download.cgi),
   descomprimir en `C:\maven`, agregar `C:\maven\bin` al `PATH`. Verificar:
   ```
   mvn -version
   ```
   *(Alternativa: usar el Maven que trae IntelliJ y ejecutar todo desde el IDE.)*
3. **IntelliJ IDEA Community**: https://www.jetbrains.com/idea/download (sección
   *Community Edition*).
4. **Chrome**: instalar o actualizar.

### Abrir el proyecto en IntelliJ

1. `File → Open…` y elegir la **carpeta del proyecto** (la que contiene
   `pom.xml`). IntelliJ lo detecta como proyecto Maven y descarga las
   dependencias (barra inferior: *Resolving dependencies*).
2. `File → Project Structure → Project` → *SDK*: **17**, *Language level*: **17**.
3. Si las dependencias no aparecen: pestaña **Maven** (icono a la derecha) →
   botón *Reload All Maven Projects*.
4. Configurar la contraseña (ver el punto siguiente) y ya se puede ejecutar.

## 2. Configuración del ambiente

Todo está en un solo archivo: `src/test/resources/configuracion.properties`.

```properties
amex.url=https://qaeapplyargpf.firmaautografa.com/es-ar/business/consumer/eapply/expediente/
amex.usuario=admin-centurion@na-at.com
amex.navegador=chrome        # chrome | firefox | edge
amex.headless=true           # false para ver la ejecución
amex.espera=20               # segundos de espera máxima
amex.catalogos=Nacionalidades,Profesiones,...   # catálogos esperados en la lista
```

**La contraseña no se guarda en el repositorio.** Se pasa de una de estas formas:

- Variable de entorno:
  - Windows (PowerShell): `$env:AMEX_CONTRASENA = "..."`
  - Linux/macOS: `export AMEX_CONTRASENA=...`
- O como parámetro de Maven: `mvn test -Damex.contrasena=...`
- En IntelliJ: `Run → Edit Configurations… → Environment variables` →
  `AMEX_CONTRASENA=...`

Cualquier valor del archivo se puede sobreescribir sin editarlo, por ejemplo:
`mvn test -Damex.usuario=otro@na-at.com -Damex.headless=false`.

## 3. Cómo ejecutar

### Desde la consola (Maven)

```bash
# Humo: lo mínimo indispensable (recomendado en cada despliegue a DEV)
mvn test -Dsuite=humo

# Regresión funcional: todos los casos de solo lectura
mvn test -Dsuite=regresion

# Consultas (ola 3): Usuarios, Expediente, Catálogos, Tasas, Costos, Reportes y Dashboard
mvn test -Dsuite=consultas

# Un solo módulo
mvn test -Dsuite=login

# Ola 2: validaciones de campos (Solicitudes y alta de usuario)
mvn test -Dsuite=validaciones

# Ola 4: descargas (Excel de Usuarios/Solicitudes/Reportes, layout y ZIP)
mvn test -Dsuite=descargas

# Ola 6: estados del expediente, dictaminacion, PEP, costos y cuotas (solo lectura)
mvn test -Dsuite=ola6

# Ola 5: altas, ediciones y bajas. OJO: es la unica suite que ESCRIBE datos
mvn test -Dsuite=altas

# Ver el navegador mientras corre
mvn test -Dsuite=login -Damex.headless=false

# Solo un grupo, sin cambiar de suite
mvn test -Dsuite=regresion -Dgrupos=navegacion

# Una sola clase o un solo método
mvn test -Dtest=LoginPruebas
mvn test -Dtest=LoginPruebas#pfCp004UsuarioYContrasenaCorrectos
```

### Caso por caso (uno a la vez)

Para probar el caso 1, luego el 2, y así:

```bash
mvn test -Dtest='LoginPruebas#pfCp001UsuarioCorrectoContrasenaIncorrecta'
mvn test -Dtest='LoginPruebas#pfCp002UsuarioIncorrectoContrasenaCorrecta'
mvn test -Dtest='LoginPruebas#pfCp001UsuarioCorrectoContrasenaIncorrecta' -Damex.headless=false
```

- **El comando exacto de cada uno de los 160 casos está en `TRAZABILIDAD.csv`**
  (columna `comando`) y la explicación de qué hace su código, en
  `GUIA_CASO_POR_CASO.md`.
- Cada caso abre su propio navegador y cierra la sesión al terminar, así que se
  puede correr solo y en cualquier orden.
- La consola imprime `[PF_CP_001] APROBADO` o `FALLIDO` con el motivo, también
  cuando corres un caso suelto con `-Dtest`.
- Un método con tabla (`@DataProvider`) cubre varios IDs: el comando los corre
  todos y la consola imprime el ID de cada renglón.

### Trazabilidad con la matriz

```bash
python3 herramientas/generar_trazabilidad.py
```

Regenera, leyendo la matriz (`datos/matriz_funcional.csv`) y el código:

| Archivo | Para qué sirve |
|---|---|
| `TRAZABILIDAD.md` | tabla ID de la matriz → prueba que lo cubre → etiquetas → motivo si falta |
| `TRAZABILIDAD.csv` | lo mismo en Excel, con el comando para correr cada caso |
| `GUIA_CASO_POR_CASO.md` | por caso: qué pide la matriz, qué hace el código, sus pasos y su comando |

Hay que volver a correrlo cada vez que se agregan o cambian casos, y completar en
`datos/matriz_funcional.csv` los casos que el documento marca como incompletos.

### No afectación después de un cambio técnico

```bash
./herramientas/no_afectacion.sh              # suite regresion
./herramientas/no_afectacion.sh humo
```

La primera corrida guarda la referencia; las siguientes comparan caso por caso
contra ella (sin los tiempos) y muestran solo lo que cambió.

### Desde IntelliJ

- Clic derecho sobre `src/test/resources/suites/humo.xml` → **Run**.
- O abrir una clase de pruebas y usar la flecha verde al lado del método
  (ejecuta un solo caso).
- Para repetir solo lo que falló: en la ventana *Run*, botón *Rerun Failed Tests*.

### Reportes y evidencias

| Qué | Dónde |
|---|---|
| Resumen de la corrida | `target/surefire-reports/` (`emailable-report.html`, `index.html`) |
| Capturas de los casos que fallan | `resultados/evidencias/` |
| Archivos descargados por las pruebas | `resultados/descargas/` |

Reporte más presentable (opcional, requiere la CLI de Allure):

```bash
mvn test -Dsuite=regresion
allure serve target/allure-results
```

## 4. Estructura del proyecto

```text
amex-pf-selenium/
├── pom.xml                       dependencias y configuración de ejecución
└── src/test/
    ├── java/com/amex/pf/
    │   ├── base/
    │   │   ├── Configuracion.java        ← URL, usuario, navegador, esperas
    │   │   ├── FabricaDeNavegador.java   ← creación del navegador
    │   │   └── PruebaBase.java           ← abre y cierra el navegador por caso
    │   ├── datos/                    ← DATOS Y REGLAS DE PRUEBA
    │   │   ├── ElementoDeCatalogo.java    ← qué se escribe en cada catálogo
    │   │   ├── EstadoDelExpediente.java   ← pestañas esperadas por estatus (ola 6)
    │   │   └── UsuarioDePrueba.java       ← datos del usuario que se da de alta
    │   ├── paginas/                  ← PAGE OBJECTS (una clase por pantalla)
    │   │   ├── Selectores.java           ← TODOS los selectores, en un solo lugar
    │   │   ├── PaginaBase.java           ← esperas y acciones reutilizables
    │   │   ├── PaginaFormulario.java      ← longitud, tipo de carácter, obligatorios
    │   │   ├── PaginaLogin.java
    │   │   ├── PaginaPrincipal.java
    │   │   ├── PaginaSolicitudes.java
    │   │   ├── PaginaCatalogos.java
    │   │   ├── PaginaUsuarios.java
    │   │   ├── PaginaExpediente.java
    │   │   ├── PaginaTasas.java
    │   │   ├── PaginaCostos.java
    │   │   ├── PaginaCuotasGenerales.java
    │   │   ├── PaginaReportes.java
    │   │   └── PaginaDashboard.java
    │   ├── pruebas/                  ← LOS CASOS (esto es lo que se edita)
    │   │   ├── LoginPruebas.java
    │   │   ├── NavegacionPruebas.java
    │   │   ├── ValidacionesDeCamposPruebas.java
    │   │   ├── CatalogosPruebas.java
    │   │   ├── UsuariosValidacionesPruebas.java
    │   │   ├── UsuariosConsultasPruebas.java
    │   │   ├── ExpedienteConsultasPruebas.java
    │   │   ├── CatalogosConsultasPruebas.java
    │   │   ├── TasasCostosYReportesPruebas.java
    │   │   ├── DescargasPruebas.java
    │   │   ├── UsuariosAltasPruebas.java     ← ola 5 (escribe datos)
    │   │   ├── CatalogosAltasPruebas.java    ← ola 5 (escribe datos)
    │   │   ├── TasasCftPruebas.java
    │   │   ├── ExpedienteEstadosPruebas.java  ← ola 6
    │   │   ├── ExpedienteDictamenPruebas.java ← ola 6
    │   │   ├── SolicitudesPepPruebas.java     ← ola 6
    │   │   └── CostosYCuotasPruebas.java      ← ola 6
    │   └── utilidades/
    │       ├── EvidenciaListener.java    ← captura de pantalla al fallar
    │       ├── ReporteEnConsolaListener.java ← imprime ID y APROBADO/FALLIDO
    │       ├── OrdenDeLaMatriz.java      ← ejecuta e imprime en el orden de la matriz
    │       ├── IdDeLaMatriz.java         ← lee el ID del caso y su orden
    │       └── Descargas.java            ← espera el archivo y lee Excel y ZIP
    └── resources/
        ├── configuracion.properties
        ├── datos/                    ← imagen.png que piden algunos catálogos
        └── suites/                   ← humo.xml, regresion.xml, login.xml,
                                         validaciones.xml, consultas.xml,
                                         descargas.xml, altas.xml, ola6.xml

├── datos/matriz_funcional.csv    ← la matriz Amex PF V2 en CSV (fuente de la trazabilidad)
├── herramientas/
│   ├── generar_trazabilidad.py   ← genera TRAZABILIDAD.* y GUIA_CASO_POR_CASO.md
│   └── no_afectacion.sh          ← corre una suite y la compara con la corrida anterior
└── resultados/                   ← capturas, descargas y logs (no se sube a Git)
```

Cómo se llaman entre sí estas capas está explicado con el recorrido completo de un
caso en [`COMO_FUNCIONA_EL_CODIGO.md`](COMO_FUNCIONA_EL_CODIGO.md).

**Regla de oro:** ningún `By` dentro de `pruebas/`. Si la aplicación cambia un
botón, se corrige **una línea** en `Selectores.java`.

## 5. Cómo agregar o ajustar un caso

### Caso nuevo de un tipo que ya existe (longitud de campo, catálogo…)

Se agrega **un renglón** en el `@DataProvider`, sin escribir lógica:

```java
{"PF_CP_115 Telefono", Selectores.SOLICITUDES_CAMPO_TELEFONO, 10, "numeros"},
```

Hay tres tablas de este tipo en la ola 2:

| Tabla | Para qué sirve | Dónde está |
|---|---|---|
| `camposConMaximo` | máximo de caracteres de un campo | `ValidacionesDeCamposPruebas` |
| `camposQueFiltranCaracteres` | qué caracteres deja escribir un campo | `ValidacionesDeCamposPruebas` |
| `camposDeTexto` | máximo, tipo de carácter y obligatoriedad del alta de usuario | `UsuariosValidacionesPruebas` |

### Caso nuevo distinto

```java
@Test(groups = "navegacion", description = "PF_CP_XXX Descripción del caso")
public void pfCpXxxNombreCorto() {
    PaginaPrincipal inicio = new PaginaLogin().iniciarSesionConCredencialesValidas();
    inicio.irAlMenu("Reportes")
          .laDireccionDebeContener("expedient/reports")
          .laPantallaDebeTenerUnaTablaConInformacion();
}
```

### Cambió un botón o un texto en la aplicación

Solo `Selectores.java`:

```java
public static final By BOTON_INICIAR_SESION = By.xpath("//button[contains(., 'INGRESAR')]");
```

### Grupos (etiquetas) que usamos

| Grupo | Significado |
|---|---|
| `humo` | mínimo indispensable, corre en cada despliegue |
| `login`, `navegacion`, `validaciones`, `catalogos`, `usuarios`, `consultas` | por módulo de la matriz |
| `descargas` | archivos que entrega la aplicación (Excel, layout y ZIP) |
| `escribe_datos` | crea o modifica información (excluido de la regresión, salvo `PF_CP_039`–`045`) |
| `defecto_conocido` | falla por un defecto abierto de la aplicación |
| `regla_por_confirmar` | la matriz y la aplicación no coinciden y falta definición |

Un defecto **no se oculta cambiando la expectativa**: se etiqueta y se documenta.

## 5.1 Reporte en consola por caso

Al terminar cada caso se imprime su **ID de la matriz** y su estado, y al final un
resumen:

```
[PF_CP_001] APROBADO Usuario correcto y contrasena incorrecta (1.4 s)
[PF_CP_004] APROBADO Usuario y contrasena correctos (1.3 s)
[PF_CP_046] FALLIDO  La lista muestra todos los catalogos esperados (0.7 s)
            Motivo: Faltan catalogos en la lista: [Versiones].
[PF_CP_047-093] APROBADO Cada catalogo muestra su tabla ... -> Versiones (1.3 s)

RESUMEN DE CASOS EJECUTADOS (en el orden de la matriz)
...
Aprobados: 30 | Fallidos: 0 | Omitidos: 0
```

**Los casos salen en el orden de la matriz** (PF_CP_001, PF_CP_002, PF_CP_003… y
al final los internos VAL, SEG y DEF), tanto mientras corren como en el resumen,
sin importar en qué clase está cada uno. De eso se encarga `OrdenDeLaMatriz`,
registrado en cada suite; para que TestNG respete ese orden entre clases distintas
las suites declaran `parallel="methods" thread-count="1"`: **los casos siguen
corriendo de uno en uno** (la aplicación solo permite una sesión activa por
usuario), solo se deja de agrupar por clase.

El ID sale de la **primera palabra de la descripción** del `@Test` (o del primer
dato del `@DataProvider` cuando un método cubre varios IDs), así que un caso nuevo
solo debe empezar su descripción con el ID de la matriz para aparecer identificado:

```java
@Test(groups = "navegacion", description = "PF_CP_120 Descripción del caso")
```

Cuando una prueba cubre **varios casos del mismo flujo**, los IDs se declaran
separados por `/` (o como rango con `-`) y el reporte los imprime completos y los
ordena por el primero, igual que la trazabilidad:

```java
@Test(description = "PF_CP_042/043/044/045 Desactivar un usuario y volver a activarlo")
// → [PF_CP_042/043/044/045] APROBADO Desactivar un usuario y volver a activarlo (16.3 s)
```

Esto lo hacen `ReporteEnConsolaListener` y `OrdenDeLaMatriz`, registrados en las
suites XML junto con `EvidenciaListener`; no hay que tocarlos al agregar casos.

## 5.2 Mensajes de la consola que son normales

Al ejecutar aparecen líneas que **no son errores** y no afectan el resultado:

| Mensaje | Qué significa |
|---|---|
| `Unable to find CDP implementation matching 152` | Selenium no trae el módulo del protocolo de depuración para esa versión exacta de Chrome; solo se usa para funciones avanzadas (interceptar red) que esta suite no usa. |
| `SLF4J: Failed to load class StaticLoggerBinder` | falta un motor de registro opcional. |
| `Evidencia guardada: .../resultados/evidencias/...png` | **sí importa**: un caso falló y quedó la captura. |

Los dos avisos ya están silenciados en el proyecto (`logging.properties` y la
dependencia `slf4j-nop`); si aparecen en una copia anterior, se pueden ignorar.

**Lo único que dice si la corrida estuvo bien es el resumen final:**
`Tests run: N, Failures: 0, Errors: 0` → `BUILD SUCCESS`. Con `Failures` mayor a
cero hay casos rojos: el detalle está en `target/surefire-reports/` y la captura
en `resultados/evidencias/`.

## 6. Restricciones de la aplicación que hay que conocer

1. **Una sola sesión activa por usuario.** Si una ejecución deja la sesión
   abierta, el siguiente intento muestra *"Usted ya cuenta con una sesión activa
   en otro dispositivo"* y el usuario queda bloqueado del lado del servidor. Por
   eso `PruebaBase` cierra sesión al terminar cada caso y, además:
   - guarda el token de la sesión en `resultados/ultima-sesion.token`;
   - si el cierre desde la pantalla falla, cierra la sesión **llamando al API**;
   - al empezar la siguiente ejecución cierra la sesión que hubiera quedado
     abierta con ese token (`liberarSesionPendiente`).

   **Consecuencia: hace falta un usuario por tester y uno exclusivo para CI**; no
   se puede ejecutar en paralelo con el mismo usuario. Si aun así queda bloqueado
   (se cortó la ejecución y se borró la carpeta `resultados/`), solo lo libera
   desarrollo/DBA invalidando la sesión.
2. **Cada perfil ve menús distintos.** Con `user-agency` solo hay *Inicio* y
   *Expediente*: los casos de Usuarios, Catálogos, Tasas, Reportes, Dashboard,
   Costos y Cuotas se reportan **OMITIDOS con el motivo** (`SkipException`), no
   como falla. Para ejecutarlos hace falta un perfil administrador.
3. **La aplicación no tiene `data-testid`.** Los selectores se apoyan en
   `formcontrolname` y en textos visibles; los `id` son autogenerados
   (`mat-input-0`) y no se usan. Pedir `data-testid` a desarrollo es la mejora
   que más reduce el mantenimiento.
4. **Ejecución en paralelo:** el proyecto está preparado (`ThreadLocal` para el
   navegador), pero **solo tiene sentido con un usuario distinto por hilo**.

## 7. Casos incluidos hoy

| Clase | Casos de la matriz |
|---|---|
| `LoginPruebas` | PF_CP_001–004, VAL_001–004, DEF_01 |
| `NavegacionPruebas` | PF_CP_008, 009, 010, 046, 101, 108, 147, 151, 153, 159, SEG_001 |
| `ValidacionesDeCamposPruebas` | PF_CP_111–120 (Solicitudes: longitudes, tipo de carácter, fecha, dirección, PEP) |
| `UsuariosValidacionesPruebas` | PF_CP_011–019 y 021 (alta de usuario: listas, longitudes, obligatorios, formato de correo, teléfonos) |
| `CatalogosPruebas` | PF_CP_047–093 (plantilla que recorre los catálogos de `amex.catalogos`) |
| `UsuariosConsultasPruebas` | PF_CP_026, 028, 029, 030 |
| `ExpedienteConsultasPruebas` | PF_CP_109, 128, 129 |
| `CatalogosConsultasPruebas` | PF_CP_047, 049, 050, 054, 056, 057, 061, 063, 064, 065, 069, 071, 072, 076, 078, 079, 080, 081, 085, 087, 088, 089, 093, 095, 096, 097 |
| `TasasCostosYReportesPruebas` | PF_CP_102, 104, 105, 148, 156, 160 |
| `DescargasPruebas` | PF_CP_022, 027, 125, 127, 142, 143, 154, 155, 157, 158 (archivos que descarga la aplicación) |
| `UsuariosAltasPruebas` | PF_CP_020, 039–045 (alta, detalle, contraseña, desactivar y activar) — **escribe datos** |
| `CatalogosAltasPruebas` | PF_CP_048–100 en los 7 catálogos: alta, edición, inactivar y activar — **escribe datos** |
| `TasasCftPruebas` | PF_CP_107 (Costo Financiero Total: solo números, máximo 9 caracteres) |
| `ExpedienteEstadosPruebas` | PF_CP_130–141 (detalle de la solicitud por estatus: pestañas habilitadas) |
| `ExpedienteDictamenPruebas` | PF_CP_144, 145 (popups de Aprobar/Denegar, sin confirmar), 146 (ZIP Doc. Griffin), 124, 126 |
| `SolicitudesPepPruebas` | PF_CP_121 (editar y eliminar el adicional PEP), 122 |
| `CostosYCuotasPruebas` | PF_CP_149, 150 (popup de costos, sin guardar), 152 (Cuotas Generales) |

**Catálogos:** la lista esperada está en `amex.catalogos` y hoy es la de
PF_CP_046 (Nacionalidades, Profesiones, Campaña, Código de país, Productos, Días
festivos y Versiones). Si un catálogo no aparece, el caso falla indicando **qué
catálogos sí muestra hoy** la aplicación, para distinguir un cambio de nombre de
un defecto. `PF_CP_046` valida la lista completa de una sola vez.

## 7.1 Ola 3 — consultas de solo lectura

La suite `consultas` cubre solo lectura: **nunca** se presiona GUARDAR, ACEPTAR,
EDITAR DATOS, ACTUALIZAR, AGREGAR, DICTAMINAR ni DEVOLVER. Los modales de alta se
abren únicamente para revisar campos, máximos y calendario, y se cierran con
CANCELAR o con la X.

Los datos con los que se filtra (nombre de usuario, DNI de la solicitud) se toman
de la propia tabla, así la suite funciona en cualquier ambiente sin datos semilla
fijos.

**PF_CP_028 — los cuatro filtros de Usuarios.** La matriz pide filtrar «por
nombre, correo electrónico, rol o estatus», así que el caso son cuatro pruebas
independientes (todas se reportan como `PF_CP_028`, cada una con el filtro que
usó):

```
mvn test -Dtest='UsuariosConsultasPruebas#pfCp028FiltrarPorNombre'
mvn test -Dtest='UsuariosConsultasPruebas#pfCp028FiltrarPorCorreo'
mvn test -Dtest='UsuariosConsultasPruebas#pfCp028FiltrarPorRol'
mvn test -Dtest='UsuariosConsultasPruebas#pfCp028FiltrarPorEstatus'
```

Las cuatro siguen el mismo patrón: toman el valor de la **primera fila** de la
columna que corresponde (por eso no dependen de que exista un usuario, un rol o
un estatus concreto en el ambiente), aplican el filtro y exigen que **todas** las
filas que quedaron correspondan a lo buscado; si no, el mensaje lista los
usuarios que la tabla dejó visibles.

Nombre y correo son campos de texto: basta con que la celda **contenga** lo
buscado. *Rol* y *Estatus* se eligen de una lista, así que se compara el valor
**exacto** — de lo contrario un filtro por *Activo* pasaría mostrando filas
*Inactivo*, que contiene esa palabra.

**PF_CP_029 — Limpiar.** Captura los **cuatro** filtros (y comprueba que cada uno
tomó el valor, para que Limpiar no pase por descarte), busca, presiona Limpiar y
exige que los cuatro queden vacíos y que la tabla vuelva a mostrar a todos los
usuarios:

```
mvn test -Dtest='UsuariosConsultasPruebas#pfCp029LimpiarLosFiltros'
```

Los cuatro filtros viven en un solo lugar, el enum `PaginaUsuarios.Filtro`
(etiqueta, selector, columna de la tabla y si es lista o campo de texto): si la
pantalla agrega un filtro, se añade ahí y los casos 028/029 lo cubren solos.

Pendientes de negocio que esta ola dejó documentados (etiquetados y **fuera** de
la suite y de la regresión; para verlos: `mvn test -Dgroups=defecto_conocido` o
`-Dgroups=regla_por_confirmar`):

| Caso | Qué dice la matriz | Qué hace la aplicación |
|---|---|---|
| `PF_CP_063` | Código de Campaña admite 250 caracteres | admite más de 250 (la propia matriz ya lo anota) |
| `PF_CP_071` | Código de país admite 100 caracteres | admite 10 (un código real no necesita 100: ¿se corrige la matriz?) |
| `PF_CP_095`, `PF_CP_096` | Versiones tiene un campo Fecha con calendario | Versiones tiene Descripción y Valor; el calendario es de Días festivos |
| `PF_CP_148` | muestra el costo del producto por año y mes | QA responde *"No se han registrado los costos"*: falta el dato semilla, el caso se reporta OMITIDO |

## 7.2 Diferencias entre la matriz y el ambiente QA (ola 2)

| Caso | La matriz dice | La aplicación en QA hace | Cómo quedó |
|---|---|---|---|
| PF_CP_012 y PF_CP_013 | área *Ventas* con 5 tipos de usuario | depende del perfil: con `admin@na-at.com` es *VENTAS* con los 5 tipos de la matriz, y con `admin-centurion@na-at.com` es *CENTURION* con 2 tipos | resuelto: la lista esperada se declara **por usuario** (ver abajo); ya no es una diferencia con la matriz |
| PF_CP_018 | teléfono móvil solo numérico | acepta letras (`abc12de345`) | **DEF_02**, grupo `defecto_conocido` (excluido de la regresión) |
| PF_CP_114 | CUIL de 11 caracteres | se muestra con máscara `20-12345678-9`: 13 caracteres = 11 dígitos | resuelto: el caso cuenta dígitos y ya no es `regla_por_confirmar` |
| PF_CP_122–123 | check *Condicionada a ingresos* en el alta de solicitud | ese check no está hoy en la pantalla | `PF_CP_122` lo reporta como diferencia (ola 6, sección 7.5); `PF_CP_123` depende de él |

Cuando negocio confirme otro área o otros tipos de usuario se ajusta
`configuracion.properties`; cuando se corrija DEF_02 se quita el grupo
`defecto_conocido` del caso PF_CP_018.

### Listas que dependen del usuario (PF_CP_012 y PF_CP_013)

Las opciones de *Area* y *Tipo de usuario* **cambian según el perfil con el que
se ejecuta**: `admin-centurion@na-at.com` ve *CENTURION* con 2 tipos y
`admin@na-at.com` ve *VENTAS* con los 5 tipos que describe la matriz. Para que
el caso no falle al cambiar de usuario, la lista esperada se declara por
usuario, y gana sobre la lista general:

```properties
amex.usuario.areas=CENTURION                                  # respaldo general
amex.usuario.areas.admin-centurion@na-at.com=CENTURION
amex.usuario.tipos.admin-centurion@na-at.com=Administrador Centurion,Usuario Centurion
amex.usuario.areas.admin@na-at.com=VENTAS
amex.usuario.tipos.admin@na-at.com=Administrador Apex,Supervisor AXP,Usuario AXP,Supervisor Agencia,Usuario Agencia
```

Al agregar un usuario nuevo se copia el mismo par de líneas con su correo.

La clave se arma con `amex.usuario.areas.` + el correo de `amex.usuario`. Si no
existe una clave para ese correo se usa `amex.usuario.areas` tal cual, así que
nada cambia para quien ya lo tenía configurado. También se puede indicar en la
corrida, sin editar el archivo:

```bash
mvn test -Dtest='UsuariosValidacionesPruebas#pfCp012ListaArea' \
    -Damex.usuario=otro-usuario@na-at.com -Damex.usuario.areas=Ventas
```

Si la lista no coincide, el mensaje del caso dice **qué falta y qué muestra hoy
la aplicación**, que es el dato con el que se completa la configuración de un
usuario nuevo.

### Correo electrónico (PF_CP_017)

El caso valida **formato**, no un valor fijo: recorre las listas
`amex.usuario.correos.validos` y `amex.usuario.correos.invalidos`. Con cada
inválido exige que *GUARDAR REGISTRO* quede deshabilitado, y con cada válido que
el campo deje el correo tal cual y no lo marque en rojo (`aria-invalid`). Para
probar otro ejemplo se agrega a la lista, sin tocar Java.

Observación del ambiente: la pantalla **filtra el signo `+`** al escribirlo
(`tester.qa+1@dominio.com.ar` queda `tester.qa1@dominio.com.ar`), así que ese
carácter no se usa como ejemplo válido.

## 7.3 Ola 4 — descargas de archivos

La suite `descargas` revisa los archivos que entrega la aplicación: el Excel de
Usuarios, el layout de carga masiva, el Excel de Solicitudes, los dos ZIP de una
solicitud firmada y los reportes en Excel. No modifica información: solo descarga
y abre lo descargado (Apache POI para los Excel, `ZipFile` para los ZIP).

Cómo funciona el mantenimiento: **las columnas esperadas de cada archivo están en
`configuracion.properties`** (`amex.excel.*`, `amex.zip.*`). El caso verifica que
estén todas las columnas de la matriz; si el archivo trae columnas extra no falla,
las informa en el mensaje. Cuando negocio cambie un layout se ajusta la propiedad,
sin tocar código.

Otros detalles del ambiente:

- `resultados/descargas` se vacía antes de cada caso, así nunca se aprueba un caso
  con el archivo de una corrida anterior.
- Los reportes de totales (URL y WhatsApp) exigen fecha inicio y fin: se eligen con
  el calendario según `amex.reportes.anio/mes.inicio|fin`. El ambiente **limita el
  rango** (no acepta un año completo abierto): si se pide un mes fuera del rango el
  caso falla con un mensaje que lo explica.
- `PF_CP_142`/`PF_CP_143`: la fila de la tabla muestra dos botones ZIP solo cuando
  la solicitud está firmada — el primero baja el expediente completo
  (`expedient.zip`) y el segundo el ZIP Griffin (`expedient-<referencia>.zip`, solo
  `identity_validation.pdf` y `signed_pdf.pdf`). Si ninguna solicitud del ambiente
  los ofrece, el caso se reporta OMITIDO en lugar de fallar.
- `PF_CP_023`–`PF_CP_025` (subir el layout de usuarios) **no** entran en esta ola:
  escriben datos y hacen falta los layouts oficiales válido e inválido.

Pendientes que esta ola dejó documentados (etiquetados y **fuera** de la suite y de
la regresión; para verlos: `mvn test -Dtest=DescargasPruebas`):

| Caso | Qué dice la matriz | Qué hace la aplicación en QA |
|---|---|---|
| `PF_CP_022` | el layout trae *Teléfono móvil* y *Teléfono Fijo* | trae dos columnas llamadas *Teléfono móvil* → **DEF_03** |
| `PF_CP_125` | Expediente tiene un botón *Importar* para carga masiva de solicitudes | la pantalla solo tiene *Exportar*: falta confirmar si se quitó o depende de otro permiso |
| `PF_CP_127` | el Excel de solicitudes empieza con *Id_Solicitud* | no exporta esa columna (sí las otras 10) |
| `PF_CP_154` | el reporte general empieza con *Id* | no exporta *Id*, y entrega 32 columnas (13 más que la matriz: CUIL, Fecha firma, RENAPER, Navegador…) |

## 7.4 Ola 5 — altas, ediciones y bajas (escribe datos)

La suite `altas` es la **única que escribe en el ambiente**. Todo lo que crea lleva
el prefijo `amex.datos.prefijo` (hoy `ZZAUTOQA`) más el número de la ejecución, así
se reconoce de un vistazo qué registros son de automatización.

```bash
mvn test -Dsuite=altas                         # los 11 casos de la ola 5
mvn test -Dsuite=altas -Damex.headless=false   # viendo el navegador
```

Reglas de uso, importantes:

- **No correrla en paralelo** con otra suite usando el mismo usuario (la aplicación
  solo permite una sesión activa por usuario).
- La aplicación **no permite borrar** usuarios ni elementos de catálogo: cada caso
  termina dejando **inactivo** lo que creó (bloque `finally`), incluso si falla a la
  mitad. Por eso el ambiente acumula registros `ZZAUTOQA …` inactivos.
- Está **fuera de la regresión** (grupo `escribe_datos`), **excepto**
  `PF_CP_039`–`045` (detalle del usuario, GENERAR CONTRASEÑA, desactivar y
  activar): esos dos casos sí corren en la regresión, así que cada corrida deja
  dos usuarios `ZZAUTOQA` inactivos más. `PF_CP_020` (el alta) sigue fuera porque
  el alta ya se ejecuta dentro de esos dos casos.

Qué datos usa cada catálogo está en una sola clase, `datos/ElementoDeCatalogo`
(placeholder del campo → valor); los del usuario, en `datos/UsuarioDePrueba`. Para
cambiar un dato de prueba se edita esa clase o estas propiedades:

```properties
amex.datos.prefijo=ZZAUTOQA
amex.datos.correo=na-at.com
amex.datos.usuario.tipo=Usuario Centurion
amex.datos.usuario.codigo.pais=+549
amex.datos.anio=2031
amex.datos.mes=DIC
```

Comportamientos del ambiente que la ola 5 dejó documentados:

| Qué se encontró | Cómo lo maneja la automatización |
|---|---|
| El alta de usuario **exige Número de empleado** y teléfonos de **10 dígitos** (la matriz no lo dice) | `UsuarioDePrueba` genera un número de empleado único por ejecución y teléfonos de 10 dígitos; la fila se ubica por ese número |
| El servicio rechaza un **Código de Campaña de más de 10 caracteres** (`size must be between 0 and 10`) aunque la pantalla deje escribir más | el código generado respeta el máximo real; queda para negocio confirmar el límite |
| El **detalle del usuario se abre vacío** y se llena después con la respuesta del servicio (mientras carga muestra "Inactivo") | se espera a que el detalle traiga los datos antes de leer su estatus o sus campos |
| **GENERAR CONTRASEÑA muestra la contraseña en pantalla** con un botón *Copiar contraseña* (no la envía por correo) | el caso solo comprueba que apareció: **nunca** lee ni imprime el valor de la contraseña |
| Al guardar el detalle, el aviso *Usuario actualizado* solo se cierra con la **X** | el cierre de avisos acepta ACEPTAR, Aceptar, OK o la X |
| Los **días festivos** se identifican por su fecha y no se pueden borrar | la fecha depende de la ejecución y, si ya está ocupada, se prueba la siguiente; si no queda ninguna libre el caso se reporta OMITIDO pidiendo cambiar `amex.datos.anio`/`amex.datos.mes` |

Casos de la ola 5 que **no** se automatizaron todavía, y por qué:

| Caso | Motivo |
|---|---|
| `PF_CP_031`–`PF_CP_038` | repiten las validaciones de `PF_CP_012`–`PF_CP_019`, ya cubiertas en la ola 2 (no se duplican) |
| `PF_CP_103`, `PF_CP_106` | modifican las **tasas vigentes** del ambiente: hace falta acordar un periodo de prueba seguro |
| `PF_CP_110`–`PF_CP_146` (expediente) | necesitan solicitudes fixture, una por estatus, y los layouts oficiales |
| `PF_CP_149`, `PF_CP_150`, `PF_CP_152` | Costos y Cuotas Generales afectan el cálculo de todo el ambiente: falta definir si se pueden modificar en QA |

## 7.5 Ola 6 — estados del expediente, dictaminación, costos y cuotas

La suite `ola6` es de **solo lectura**, aunque toque pantallas que escriben:

- Los popups de **Aprobar / Denegar solicitudes** se abren, se lee la leyenda y se
  cierran con **Cancelar**: ninguna solicitud cambia de estatus.
- El **adicional PEP** (PF_CP_121) se registra dentro del formulario de alta y se
  edita y elimina ahí mismo; **nunca** se presiona CREAR SOLICITUD, así que la
  solicitud no llega a existir.
- El popup de **costos** se llena y se cierra con **Cancelar**; en **Cuotas
  Generales** solo se leen los importes: nunca se presiona Guardar.
- Del detalle de la solicitud solo se leen el estatus y las pestañas: **nunca** se
  presiona Devolver ni Dictaminar.

```bash
mvn test -Dsuite=ola6                        # los 13 casos de la ola 6
mvn test -Dsuite=ola6 -Damex.headless=false  # viendo el navegador
mvn test -Dtest=ExpedienteEstadosPruebas     # solo el detalle por estatus
```

### Las reglas de PF_CP_130–141 se ajustan sin tocar código

Cada caso declara en `configuracion.properties` el estatus por el que filtra y las
pestañas que deben quedar habilitadas. Si negocio confirma otra regla, se edita
esta configuración y no el Java:

```properties
amex.expediente.pestanas=DNI,Firma,Carátula,RENAPER,Devolver,Dictaminar
amex.expediente.PF_CP_136.estatus=Pendiente de firma
amex.expediente.PF_CP_136.habilitadas=DNI,Firma,Carátula,RENAPER,Devolver
```

Si el ambiente no tiene ninguna solicitud en ese estatus el caso se reporta
**OMITIDO** diciendo qué fixture falta (no falla). Hoy falta la solicitud en
estatus *Firmada*; siguen pendientes las **12 solicitudes fixture, una por
estatus**, para que la ola quede completa y estable.

### Diferencias entre la matriz y la aplicación que dejó esta ola

Etiquetadas `regla_por_confirmar`, fuera de la suite y de la regresión (para
verlas: `mvn test -Dgroups=regla_por_confirmar`):

| Caso | La matriz dice | La aplicación en QA hace |
|---|---|---|
| `PF_CP_133`, `PF_CP_134`, `PF_CP_135` | en esos estatus solo se puede consultar el DNI | además habilita **Devolver** |
| `PF_CP_136` (Pendiente de firma) | habilita Firma y RENAPER | las deja **deshabilitadas** (la firma aún no existe) |
| `PF_CP_139`, `PF_CP_140` (Denegada / Aprobada) | habilita Devolver | la deja **deshabilitada** (la solicitud ya está dictaminada) |
| `PF_CP_122`–`PF_CP_123` | el alta de solicitud tiene el check *Condicionada a ingresos* | ese check no está en la pantalla |
| `PF_CP_124` | Expediente tiene botón para **eliminar solicitudes** con layout | no existe: solo CREAR SOLICITUD, Aprobar, Denegar, Exportar y Filtrar |
| `PF_CP_126` | Expediente permite **cargar el layout** de solicitudes | no existe el botón Importar (misma diferencia que `PF_CP_125`) |

Las expectativas de las pestañas se tomaron de la matriz. Si negocio confirma que
el comportamiento actual es el correcto, se ajusta
`amex.expediente.PF_CP_XXX.habilitadas` y el caso pasa a la regresión.

### Lo que la ola 6 **no** automatiza, y por qué

| Caso | Motivo |
|---|---|
| `PF_CP_005`, `PF_CP_006`, `PF_CP_007` | la matriz los marca **Cancelado** (recuperación y cambio de contraseña): fuera de alcance |
| `PF_CP_110`, `PF_CP_123` | crean solicitudes reales que después **no se pueden borrar**: hace falta un ambiente con limpieza acordada |
| `PF_CP_144`, `PF_CP_145` (confirmar el dictamen) | aprobar o denegar cambia el estatus de solicitudes reales: hacen falta solicitudes desechables |
| `PF_CP_149`, `PF_CP_150`, `PF_CP_152` (guardar) | los importes de Costos y Cuotas Generales afectan el cálculo de todo el ambiente; hoy además QA **no tiene importes cargados** |
| `PF_CP_023`–`PF_CP_025` | la carga masiva crea usuarios reales: faltan los layouts oficiales (válido e inválido) |
| `PF_CP_103`, `PF_CP_106` | modifican las tasas vigentes: falta acordar un periodo de prueba |
| Firma en el dispositivo, correo real y revisión visual | son pruebas manuales: no son deterministas desde el navegador |

## 7.6 Validaciones del detalle del usuario (PF_CP_031–PF_CP_038)

La matriz pide las mismas reglas de los campos del alta (PF_CP_012–019) **pero en
el detalle**: *Usuarios → seleccionar un usuario → ver detalle → EDITAR DATOS →
click en el campo*. Por eso viven en su propia clase,
`UsuariosDetalleValidacionesPruebas`, y no en la del alta.

Es solo lectura: se escribe en los campos y se abren las listas, pero **nunca** se
presiona GUARDAR; cada prueba sale con CANCELAR (incluso si el caso falló, con un
`@AfterMethod`), así el usuario de QA queda con sus datos originales.

**El usuario que se valida debe estar Activo**: en un usuario inactivo la
aplicación no muestra el botón EDITAR DATOS, así que no hay detalle editable que
validar. La prueba toma el primer usuario de la tabla con número de empleado, lee
su estatus y, si está *Inactivo*, lo **activa** para poder validar y lo deja
*Inactivo* otra vez al terminar (`desactivarSiQuedoActivo`). Es el único cambio que
estos casos hacen en el ambiente; si el primer usuario ya está activo, no se toca
nada.

```
mvn test -Dtest='UsuariosDetalleValidacionesPruebas'                                  # los 8 casos
mvn test -Dtest='UsuariosDetalleValidacionesPruebas#pfCp031ListaAreaDelDetalle'
mvn test -Dtest='UsuariosDetalleValidacionesPruebas#pfCp032ListaTipoDeUsuarioDelDetalle'
mvn test -Dtest='UsuariosDetalleValidacionesPruebas#elCampoDelDetalleDebePermitir35Caracteres'
mvn test -Dtest='UsuariosDetalleValidacionesPruebas#elCampoDelDetalleNoDebePermitirNumerosNiEspeciales'
mvn test -Dtest='UsuariosDetalleValidacionesPruebas#pfCp036CampoCorreoElectronicoDelDetalle'
mvn test -Dtest='UsuariosDetalleValidacionesPruebas#pfCp037TelefonoMovilDelDetalle'
mvn test -Dtest='UsuariosDetalleValidacionesPruebas#pfCp038TelefonoFijoDelDetalle'
```

| Caso | Qué valida la prueba |
|---|---|
| `PF_CP_031` | la lista *Area* del detalle muestra exactamente las áreas del perfil con el que se ejecuta (`amex.usuario.areas.CORREO`, igual que PF_CP_012) |
| `PF_CP_032` | la lista *Tipo de usuario* del detalle muestra los tipos de ese perfil (`amex.usuario.tipos.CORREO`) |
| `SIN_ID` (Número de empleado) | el número de empleado que muestra la tabla es el que trae el campo del detalle (`elDetalleMuestraElNumeroDeEmpleadoDeLaTabla`) |
| `PF_CP_033`–`035` | Nombre(s), Apellidos y Cargo: máximo 35 caracteres, mínimo 1, y que no acepten números ni caracteres especiales |
| `PF_CP_036` | acepta los correos de `amex.usuario.correos.validos` sin marcarlos en rojo y rechaza los de `amex.usuario.correos.invalidos` |
| `PF_CP_037`, `PF_CP_038` | Teléfono móvil y fijo: solo 10 caracteres y solo numéricos |

Diferencias encontradas en el detalle (etiquetadas `defecto_conocido`, **fuera** de
la regresión; para verlas: `mvn test -Dgroups=defecto_conocido`):

| Caso | La matriz dice | El detalle hace en QA |
|---|---|---|
| `PF_CP_037` | teléfono móvil solo numérico | **DEF_02**, el mismo defecto del alta (PF_CP_018): acepta letras (`abc12de345`) |

Dos comportamientos del detalle que conviene conocer al mantener estos casos:

- Para vaciar un campo **no se usa `clear()`**: así el formulario de Angular no se
  entera del cambio y sus validaciones no se disparan (parecería que GUARDAR
  queda habilitado con el campo vacío, cuando en la pantalla sí se deshabilita).
  `PaginaFormulario.limpiar(...)` borra carácter por carácter con BACKSPACE.
- El campo de correo **filtra** algunos caracteres inválidos en lugar de marcar
  error (por ejemplo el espacio de `pruebita qa@qa.com`); cuando el campo cambia
  lo capturado, la prueba no lo cuenta como formato inválido porque ya no es el
  texto que se quiso probar. Confirmado con negocio: así se queda.

## 8. Resultado de la última ejecución

Con el usuario `admin-centurion` en QA:

```
mvn test -Dsuite=ola6        →  13 casos: 10 aprobados, 0 fallidos, 3 omitidos
mvn test -Dsuite=altas       →  11 casos: 11 aprobados, 0 fallidos, 0 omitidos
mvn test -Dsuite=regresion   → 111 casos: 107 aprobados, 0 fallidos, 4 omitidos
```

Las omisiones son datos semilla que faltan en QA: `PF_CP_148` y `PF_CP_150`
(costos del producto), `PF_CP_152` (importes de Cuotas Generales) y `PF_CP_137`
(solicitud en estatus *Firmada*). Si el ambiente responde lento, subir
`amex.espera` en `configuracion.properties`.

Cubre login y sus negativos, las 9 pantallas del menú, la sesión al recargar, las
validaciones de campo de la ola 2 (Solicitudes y alta de usuario), los 7 catálogos,
todas las consultas de la ola 3 y las descargas de la ola 4; la ola 5 (altas y bajas)
va aparte, en la suite `altas`, porque escribe datos. Con un perfil sin todos
los permisos (por ejemplo `user-agency`) los casos de los menús que no ve se
reportan **OMITIDOS con el motivo**, no como falla.

Excluidos de la regresión: `DEF_01` (el login rechaza correos válidos con `+`),
`DEF_02` (PF_CP_018: el teléfono móvil acepta letras), `DEF_03` (PF_CP_022: el
layout de usuarios no trae *Teléfono Fijo*), la ola 5 (grupo `escribe_datos`,
menos `PF_CP_039`–`045`, que sí entran) y los pendientes de las secciones 7.1,
7.2 y 7.3.

## 9. Integración continua

`.github/workflows/pruebas.yml` ejecuta el humo en cada cambio y la regresión
todas las noches, con tres secretos: `AMEX_URL`, `AMEX_USUARIO_CI` y
`AMEX_CONTRASENA_CI` (usuario exclusivo de CI por la restricción de sesión
única), y publica el reporte como artefacto.
