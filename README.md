# Automatización AMEX PF / eApply — Selenium + Java + TestNG

Suite de pruebas funcionales de la aplicación **AMEX PF / eApply Expediente**,
con trazabilidad a la matriz *Amex PF V2*: cada prueba lleva el ID del caso
(`PF_CP_004`, `VAL_001`, …) en su nombre y en su `description`.

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

# Un solo módulo
mvn test -Dsuite=login

# Ola 2: validaciones de campos (Solicitudes y alta de usuario)
mvn test -Dsuite=validaciones

# Ver el navegador mientras corre
mvn test -Dsuite=login -Damex.headless=false

# Solo un grupo, sin cambiar de suite
mvn test -Dsuite=regresion -Dgrupos=navegacion

# Una sola clase o un solo método
mvn test -Dtest=LoginPruebas
mvn test -Dtest=LoginPruebas#pfCp004UsuarioYContrasenaCorrectos
```

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
    │   ├── paginas/                  ← PAGE OBJECTS (una clase por pantalla)
    │   │   ├── Selectores.java           ← TODOS los selectores, en un solo lugar
    │   │   ├── PaginaBase.java           ← esperas y acciones reutilizables
    │   │   ├── PaginaFormulario.java      ← longitud, tipo de carácter, obligatorios
    │   │   ├── PaginaLogin.java
    │   │   ├── PaginaPrincipal.java
    │   │   ├── PaginaSolicitudes.java
    │   │   ├── PaginaUsuarios.java
    │   │   └── PaginaCatalogos.java
    │   ├── pruebas/                  ← LOS CASOS (esto es lo que se edita)
    │   │   ├── LoginPruebas.java
    │   │   ├── NavegacionPruebas.java
    │   │   ├── ValidacionesDeCamposPruebas.java
    │   │   ├── UsuariosValidacionesPruebas.java
    │   │   └── CatalogosPruebas.java
    │   └── utilidades/
    │       ├── EvidenciaListener.java    ← captura de pantalla al fallar
    │       └── ReporteEnConsolaListener.java ← imprime ID y APROBADO/FALLIDO
    └── resources/
        ├── configuracion.properties
        └── suites/                   ← humo.xml, regresion.xml, login.xml,
                                         validaciones.xml
```

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
| `login`, `navegacion`, `validaciones`, `catalogos` | por módulo de la matriz |
| `escribe_datos` | crea o modifica información (excluido de la regresión) |
| `defecto_conocido` | falla por un defecto abierto de la aplicación |
| `regla_por_confirmar` | la matriz y la aplicación no coinciden y falta definición |

Un defecto **no se oculta cambiando la expectativa**: se etiqueta y se documenta.

## 5.1 Reporte en consola por caso

Al terminar cada caso se imprime su **ID de la matriz** y su estado, y al final un
resumen:

```
[PF_CP_004] APROBADO Usuario y contrasena correctos (1.3 s)
[PF_CP_047-093] APROBADO Cada catalogo muestra su tabla ... -> Versiones (1.3 s)
[PF_CP_046] FALLIDO  La lista muestra todos los catalogos esperados (0.7 s)
            Motivo: Faltan catalogos en la lista: [Versiones].

RESUMEN DE CASOS EJECUTADOS
...
Aprobados: 30 | Fallidos: 0 | Omitidos: 0
```

El ID sale de la **primera palabra de la descripción** del `@Test` (o del primer
dato del `@DataProvider` cuando un método cubre varios IDs), así que un caso nuevo
solo debe empezar su descripción con el ID de la matriz para aparecer identificado:

```java
@Test(groups = "navegacion", description = "PF_CP_120 Descripción del caso")
```

Esto lo hace `ReporteEnConsolaListener`, registrado en las suites XML junto con
`EvidenciaListener`; no hay que tocarlo al agregar casos.

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

**Catálogos:** la lista esperada está en `amex.catalogos` y hoy es la de
PF_CP_046 (Nacionalidades, Profesiones, Campaña, Código de país, Productos, Días
festivos y Versiones). Si un catálogo no aparece, el caso falla indicando **qué
catálogos sí muestra hoy** la aplicación, para distinguir un cambio de nombre de
un defecto. `PF_CP_046` valida la lista completa de una sola vez.

### 7.1 Diferencias entre la matriz y el ambiente QA (pendientes de negocio)

| Caso | La matriz dice | La aplicación en QA hace | Cómo quedó |
|---|---|---|---|
| PF_CP_012 | el área es *Ventas* | el área es *CENTURION* | la lista esperada se configura en `amex.usuario.areas` |
| PF_CP_013 | 5 tipos de usuario (Administrador Apex, Supervisor AXP, Usuario AXP, Supervisor Agencia, Usuario Agencia) | 2 tipos (*Administrador Centurion*, *Usuario Centurion*), y la lista se llena **después** de elegir el área | la lista esperada se configura en `amex.usuario.tipos` |
| PF_CP_018 | teléfono móvil solo numérico | acepta letras (`abc12de345`) | **DEF_02**, grupo `defecto_conocido` (excluido de la regresión) |
| PF_CP_114 | CUIL de 11 caracteres | se muestra con máscara `20-12345678-9`: 13 caracteres = 11 dígitos | resuelto: el caso cuenta dígitos y ya no es `regla_por_confirmar` |
| PF_CP_122–123 | check *Condicionada a ingresos* en el alta de solicitud | ese check no está hoy en la pantalla | sin automatizar hasta confirmar dónde vive |

Cuando negocio confirme otro área o otros tipos de usuario se ajusta
`configuracion.properties`; cuando se corrija DEF_02 se quita el grupo
`defecto_conocido` del caso PF_CP_018.

## 8. Resultado de la última ejecución

`mvn test -Dsuite=regresion` con el usuario `admin-centurion` en QA:

```
Tests run: 56, Failures: 0, Errors: 0, Skipped: 0
```

Cubre login y sus negativos, las 9 pantallas del menú, la sesión al recargar, los
7 catálogos y las validaciones de campo de la ola 2 (Solicitudes y alta de
usuario). Con un perfil sin todos los permisos (por ejemplo `user-agency`) los
casos de los menús que no ve se reportan **OMITIDOS con el motivo**, no como
falla.

Excluidos de la regresión: `DEF_01` (el login rechaza correos válidos con `+`) y
`DEF_02` (PF_CP_018: el teléfono móvil acepta letras). Ver la sección 7.1.

## 9. Integración continua

`.github/workflows/pruebas.yml` ejecuta el humo en cada cambio y la regresión
todas las noches, con tres secretos: `AMEX_URL`, `AMEX_USUARIO_CI` y
`AMEX_CONTRASENA_CI` (usuario exclusivo de CI por la restricción de sesión
única), y publica el reporte como artefacto.
