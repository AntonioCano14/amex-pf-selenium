# Cómo funciona el código de la automatización

Guía para entender **quién llama a quién**: qué hace cada carpeta, cómo se relacionan
los archivos y qué pasa, paso a paso, cuando corres un caso.

Documentos hermanos: `TRAZABILIDAD.md` (qué caso de la matriz cubre cada prueba) y
`GUIA_CASO_POR_CASO.md` (qué hace el código de cada caso y su comando).

---

## 1. La idea en una frase

Cada capa responde a **una sola pregunta**, y solo habla con la de abajo:

| Capa | Carpeta | Pregunta que responde | Ejemplo |
|---|---|---|---|
| Pruebas | `pruebas/` | **qué** se valida | "el popup debe decir Credenciales inválidas" |
| Páginas | `paginas/Pagina*.java` | **cómo** se hace en la pantalla | "escribir usuario, escribir contraseña, clic en INICIAR SESIÓN" |
| Selectores | `paginas/Selectores.java` | **dónde** está cada elemento | `input[formcontrolname='user']` |
| Datos | `datos/` y `configuracion.properties` | **con qué** valores se prueba | usuario, URL, máximos, catálogos esperados |
| Base y utilidades | `base/`, `utilidades/` | **el alrededor**: navegador, sesión, reporte, evidencias | abrir Chrome, imprimir `[PF_CP_001] APROBADO` |

Por eso, cuando algo cambia, se toca **un solo archivo**:

| Si cambia... | Se ajusta |
|---|---|
| el lugar o el nombre técnico de un botón o campo | `Selectores.java` |
| el flujo de una pantalla (un paso más, un modal nuevo) | la `PaginaX.java` de esa pantalla |
| un texto, un máximo, una lista esperada, la URL | `configuracion.properties` |
| lo que el caso debe verificar (la matriz cambió) | la clase `...Pruebas.java` del caso |
| qué casos entran en una corrida | el XML de `suites/` (por etiquetas) |

---

## 2. El recorrido completo del caso 1

`PF_CP_001 — usuario correcto y contraseña incorrecta`:

```
mvn test -Dtest='LoginPruebas#pfCp001UsuarioCorrectoContrasenaIncorrecta'
        │
        ▼
PruebaBase                 abre Chrome y entra a la URL de login   (@BeforeMethod)
        │
        ▼
LoginPruebas.pfCp001...    QUÉ se valida
        │  login.iniciarSesionCon(Configuracion.usuario(), CONTRASENA_INCORRECTA)
        ▼
PaginaLogin                CÓMO se hace: escribir usuario → escribir contraseña → clic
        │  escribir(Selectores.CAMPO_USUARIO, usuario)
        ▼
PaginaBase                 espera a que el campo exista y sea visible, reintenta el clic
        │  Selectores.CAMPO_USUARIO
        ▼
Selectores                 DÓNDE está: input[formcontrolname='user']
        │
        ▼
Selenium / Chrome          la acción real en el navegador
        │
        ▼
LoginPruebas               Assert.assertTrue(textoDelModal().contains("Credenciales inválidas"))
        │                  Assert.assertTrue(sigueEnLaPantallaDeLogin())
        ▼
ReporteEnConsolaListener   imprime [PF_CP_001] APROBADO ... (o FALLIDO con el motivo)
EvidenciaListener          si falló, guarda la captura en resultados/evidencias/
PruebaBase                 cierra sesión y cierra Chrome                (@AfterMethod)
```

El código de la prueba (`pruebas/LoginPruebas.java`):

```java
@Test(groups = {"login", "humo"},
        description = "PF_CP_001 Usuario correcto y contrasena incorrecta")
public void pfCp001UsuarioCorrectoContrasenaIncorrecta() {
    PaginaLogin login = new PaginaLogin();
    login.iniciarSesionCon(Configuracion.usuario(), CONTRASENA_INCORRECTA);
    Assert.assertTrue(login.textoDelModal().contains(PaginaLogin.TEXTO_CREDENCIALES_INVALIDAS),
            "Se esperaba el mensaje \"" + PaginaLogin.TEXTO_CREDENCIALES_INVALIDAS + "\".");
    login.aceptarModal();
    Assert.assertTrue(login.sigueEnLaPantallaDeLogin(), "No debio ingresar a la aplicacion.");
}
```

Lo que hace su página (`paginas/PaginaLogin.java`):

```java
public PaginaLogin iniciarSesionCon(String usuario, String contrasena) {
    escribirUsuario(usuario);        // escribir(Selectores.CAMPO_USUARIO, usuario)
    escribirContrasena(contrasena);  // escribir(Selectores.CAMPO_CONTRASENA, contrasena)
    return clicIniciarSesion();      // hacerClic(Selectores.BOTON_INICIAR_SESION)
}
```

Y dónde busca los elementos (`paginas/Selectores.java`):

```java
public static final By CAMPO_USUARIO = By.cssSelector("input[formcontrolname='user']");
public static final By CAMPO_CONTRASENA = By.cssSelector("input[formcontrolname='password']");
public static final By BOTON_INICIAR_SESION = By.xpath("//button[contains(., 'INICIAR SESIÓN')]");
```

**Regla de oro:** en `pruebas/` no debe aparecer ni un CSS ni un XPath, y en
`paginas/` no debe aparecer ningún `Assert` de negocio. Si respetas eso, la suite se
mantiene sola.

---

## 3. Archivo por archivo

### `src/test/java/com/amex/pf/pruebas/` — los casos (lo que edita el equipo)

Una clase por módulo de la matriz; un `@Test` por caso, y su `description` **empieza
con el ID de la matriz**: de ahí salen el reporte en consola, el orden y la
trazabilidad.

| Clase | Cubre |
|---|---|
| `LoginPruebas` | login y validaciones del formulario (PF_CP_001–004, VAL, DEF_01) |
| `NavegacionPruebas` | menús y pantallas iniciales (PF_CP_008–010, SEG_001) |
| `UsuariosConsultasPruebas`, `UsuariosValidacionesPruebas`, `UsuariosAltasPruebas` | pantalla Usuarios: consultas, reglas de campo y altas/bajas |
| `CatalogosPruebas`, `CatalogosConsultasPruebas`, `CatalogosAltasPruebas` | los 7 catálogos: lista, detalle, máximos y ciclo alta/edición/baja |
| `ValidacionesDeCamposPruebas`, `SolicitudesPepPruebas` | reglas de campo de Solicitudes |
| `ExpedienteConsultasPruebas`, `ExpedienteEstadosPruebas`, `ExpedienteDictamenPruebas` | expediente: tabla, detalle por estatus y popups de dictamen |
| `TasasCftPruebas`, `TasasCostosYReportesPruebas`, `CostosYCuotasPruebas` | Tasas, Costos, Cuotas Generales, Reportes y Dashboard |
| `DescargasPruebas` | Excel, layout y ZIP: descarga el archivo y revisa su contenido |

### `paginas/` — cómo se opera cada pantalla

- `PaginaBase`: lo que comparten todas — esperas, clic con reintento, leer texto,
  escribir en un campo, abrir listas de Angular Material. Aquí viven los "trucos"
  del ambiente (overlays que tapan, opciones que aún no tienen texto), para que las
  demás clases queden simples.
- `PaginaLogin`, `PaginaPrincipal`, `PaginaUsuarios`, `PaginaCatalogos`,
  `PaginaSolicitudes`, `PaginaExpediente`, `PaginaTasas`, `PaginaCostos`,
  `PaginaCuotasGenerales`, `PaginaReportes`, `PaginaDashboard`, `PaginaFormulario`:
  una por pantalla, con métodos que se leen como pasos manuales
  (`abrirCatalogo`, `verDetalleDelUsuario`, `elBotonAgregarElementoDebeEstarVisible`).
- Muchos métodos devuelven `this` o la siguiente página: eso permite encadenar
  (`catalogos.abrirCatalogo(x).elBotonAgregarElementoDebeEstarVisible()`).
- `Selectores`: **todos** los localizadores, agrupados por pantalla. No se usan los
  `id` autogenerados de Angular (`mat-input-0`) porque cambian de orden.

### `datos/` — datos que se construyen en la corrida

- `UsuarioDePrueba`: arma el usuario que dan de alta las pruebas de escritura, con
  prefijo `ZZAUTOQA` y números distintos en cada corrida (la app no permite repetir
  correo ni número de empleado).
- `ElementoDeCatalogo`: lo mismo para los elementos de catálogo.
- `EstadoDelExpediente`: los estatus del expediente y las pestañas que cada uno debe
  habilitar (los valores esperados salen de `configuracion.properties`).

### `base/` — el andamiaje de cada caso

- `PruebaBase`: de ella heredan todas las pruebas. Antes de cada caso abre un
  navegador limpio y entra al login; después cierra la sesión y el navegador. Por eso
  **cualquier caso se puede correr solo y en cualquier orden**. También registra los
  listeners con `@Listeners`, así el reporte sale igual con `-Dsuite` que con `-Dtest`.
- `FabricaDeNavegador`: crea el Chrome (headless o visible, carpeta de descargas,
  tamaño de ventana).
- `Configuracion`: lee `configuracion.properties` y la variable `AMEX_CONTRASENA`.
  Ningún dato de ambiente ni contraseña vive en el código.

### `utilidades/` — reporte, evidencias y sesión

- `ReporteEnConsolaListener`: imprime `[PF_CP_001] APROBADO/FALLIDO` por caso, el
  motivo de las fallas y el resumen final en el orden de la matriz.
- `IdDeLaMatriz` y `OrdenDeLaMatriz`: leen el ID de la descripción (o del renglón del
  `@DataProvider`) y ordenan la ejecución como la matriz.
- `EvidenciaListener`: guarda la captura de pantalla cuando un caso falla.
- `Descargas`: espera el archivo descargado y lo abre (Excel con Apache POI, ZIP con
  `ZipFile`).
- `CierreDeSesionPorApi`: red de seguridad — si el navegador muere sin cerrar sesión,
  la cierra por API para que el usuario no quede bloqueado (la app permite **una sola
  sesión activa por usuario**).

### `src/test/resources/`

- `configuracion.properties`: URL, usuario, navegador, esperas y **todo lo esperado
  configurable** (catálogos, columnas de los Excel, pestañas por estatus). Se ajusta
  sin tocar Java.
- `suites/*.xml`: qué casos entran en cada corrida, por etiquetas (`groups`). Los XML
  ya no declaran listeners: se registran una sola vez en `PruebaBase`.
- `datos/imagen.png`: archivo que usan los casos que suben imagen.

### Raíz del repositorio

- `TRAZABILIDAD.md` / `.csv`, `GUIA_CASO_POR_CASO.md`: generados por
  `herramientas/generar_trazabilidad.py` desde `datos/matriz_funcional.csv` y el código.
- `herramientas/no_afectacion.sh`: corre una suite y compara caso por caso contra la
  corrida de referencia.
- `resultados/`: capturas, descargas y logs de las corridas (no se sube a Git).

---

## 4. Las etiquetas (`groups`) mandan en qué se ejecuta

Cada `@Test` lleva etiquetas y las suites las incluyen o excluyen:

| Etiqueta | Significa |
|---|---|
| `humo` | lo mínimo indispensable después de un despliegue |
| `login`, `navegacion`, `consultas`, `validaciones`, `descargas`, `catalogos`, `usuarios`, `expediente`, `solicitudes`, `tasas`, `costos`, `cuotas`, `ola5`, `ola6` | módulo u ola a la que pertenece |
| `escribe_datos` | **crea o modifica información**: solo en la suite `altas` |
| `defecto_conocido` | falla por un defecto abierto de la aplicación; fuera de la regresión |
| `regla_por_confirmar` | la matriz y la aplicación no coinciden y negocio debe decidir; fuera de la regresión |

Así, `regresion` = todo lo de solo lectura que hoy debe quedar verde, y por eso su
total no es igual al número de casos escritos.

---

## 5. Cómo agrego un caso nuevo (resumen)

1. Encuentra la clase del módulo en `pruebas/` y copia un `@Test` parecido.
2. Empieza la `description` con el ID de la matriz: `"PF_CP_161 ..."`.
3. Si necesitas un paso que la página aún no tiene, agrégalo como método en la
   `PaginaX` correspondiente (y su localizador en `Selectores`).
4. Si el valor esperado puede cambiar (un texto, un máximo, una lista), ponlo en
   `configuracion.properties`.
5. Corre solo tu caso: `mvn test -Dtest='ClasePruebas#tuMetodo'`.
6. Regenera la trazabilidad: `python3 herramientas/generar_trazabilidad.py`.
7. Sube tu rama y pide revisión.

Detalle largo de este flujo, en la sección 5 del `README.md`.
