package com.amex.pf.paginas;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.amex.pf.base.Configuracion;
import com.amex.pf.base.PruebaBase;

/**
 * Acciones reutilizables por todas las pantallas. Aqui van las esperas y los
 * "trucos" de la aplicacion, para que las clases de pagina queden simples.
 */
public abstract class PaginaBase {

    protected WebDriver navegador() {
        return PruebaBase.navegador();
    }

    protected WebDriverWait espera() {
        return PruebaBase.espera();
    }

    protected WebDriverWait espera(int segundos) {
        return new WebDriverWait(navegador(), Duration.ofSeconds(segundos));
    }

    protected WebElement verVisible(By selector) {
        return espera().until(ExpectedConditions.visibilityOfElementLocated(selector));
    }

    /**
     * Clic con reintento: en esta aplicacion las imagenes del menu y los fondos de
     * los overlays alcanzan a tapar el elemento justo cuando se hace el clic.
     */
    protected void hacerClic(By selector) {
        WebElement elemento = espera().until(ExpectedConditions.elementToBeClickable(selector));
        try {
            elemento.click();
        } catch (ElementClickInterceptedException tapado) {
            // Segundo intento por JavaScript: no le afecta lo que este encima.
            ((JavascriptExecutor) navegador()).executeScript("arguments[0].click();", elemento);
        }
    }

    /** Clic sobre un elemento ya localizado, con el mismo reintento por JavaScript. */
    protected void hacerClic(WebElement elemento) {
        try {
            elemento.click();
        } catch (ElementClickInterceptedException tapado) {
            ((JavascriptExecutor) navegador()).executeScript("arguments[0].click();", elemento);
        }
    }

    protected void escribir(By selector, String texto) {
        WebElement campo = verVisible(selector);
        campo.clear();
        campo.sendKeys(texto);
    }

    /** En un input el texto se lee de la propiedad value, no del texto visible. */
    protected String valorDe(By selector) {
        return verVisible(selector).getDomProperty("value");
    }

    protected boolean estaVisible(By selector, int segundos) {
        try {
            espera(segundos).until(ExpectedConditions.visibilityOfElementLocated(selector));
            return true;
        } catch (RuntimeException sinElemento) {
            return false;
        }
    }

    protected List<WebElement> buscarTodos(By selector) {
        return navegador().findElements(selector);
    }

    protected void esperarQueLaUrlContenga(String fragmento) {
        espera().until(ExpectedConditions.urlContains(fragmento));
    }

    protected void esperarQueLaUrlYaNoContenga(String fragmento) {
        espera().until(ExpectedConditions.not(ExpectedConditions.urlContains(fragmento)));
    }

    protected void cerrarOverlayConEscape() {
        navegador().findElement(By.tagName("body")).sendKeys(Keys.ESCAPE);
    }

    /** El "loader" de la aplicacion cubre la pantalla y tapa los controles. */
    protected void esperarQueTermineDeCargar() {
        espera().until(navegador -> navegador.findElements(Selectores.CARGANDO).isEmpty());
    }

    protected void esperarQueDesaparezca(By selector) {
        espera().until(ExpectedConditions.invisibilityOfElementLocated(selector));
    }

    /**
     * Los modales de esta aplicacion no se cierran con Escape y su fondo tapa el
     * resto de la pantalla (por ejemplo el menu para salir): se cierran con su
     * propio boton.
     */
    protected void cerrarModalSiEstaAbierto() {
        if (!estaVisible(Selectores.MODAL, 2)) {
            return;
        }
        for (By boton : List.of(Selectores.botonDelModal("CANCELAR"),
                Selectores.botonDelModal("Cancelar"),
                Selectores.MODAL_BOTON_CERRAR,
                Selectores.botonDelModal("ACEPTAR"),
                Selectores.botonDelModal("Aceptar"))) {
            List<WebElement> botones = buscarTodos(boton);
            if (!botones.isEmpty()) {
                botones.get(0).click();
                esperarQueDesaparezca(Selectores.MODAL);
                return;
            }
        }
        cerrarOverlayConEscape();
    }

    /**
     * Cierra el ultimo popup abierto y devuelve su texto. Se trabaja sobre el ultimo
     * porque los avisos se abren encima de otro modal (por ejemplo, el aviso de
     * "Usuario actualizado" sobre el detalle del usuario). Segun el aviso, el boton
     * es ACEPTAR, Aceptar, OK o solo la "X" (close).
     */
    protected String aceptarElPopupYDevolverSuTexto() {
        WebElement popup = espera().until(navegador -> {
            List<WebElement> modales = navegador.findElements(Selectores.MODAL);
            if (modales.isEmpty()) {
                return null;
            }
            WebElement ultimo = modales.get(modales.size() - 1);
            return botonParaCerrar(ultimo) == null ? null : ultimo;
        });
        String texto = textoDe(popup).replace("\n", " ").trim();
        hacerClic(botonParaCerrar(popup));
        espera().until(ExpectedConditions.invisibilityOf(popup));
        return texto;
    }

    /** Boton con el que se cierra un aviso, o null si el modal no es un aviso. */
    private WebElement botonParaCerrar(WebElement modal) {
        for (String etiqueta : List.of("ACEPTAR", "Aceptar", "OK", "close")) {
            List<WebElement> botones =
                    modal.findElements(By.xpath(".//button[contains(., '" + etiqueta + "')]"));
            if (!botones.isEmpty()) {
                return botones.get(0);
            }
        }
        return null;
    }

    /**
     * Abre una lista desplegable (mat-select) y devuelve sus opciones. Mientras el
     * panel se abre las opciones ya existen pero todavia sin texto, por eso se
     * espera a que todas tengan contenido.
     */
    protected List<WebElement> abrirLista(By lista) {
        esperarQueSeCierrenLasListas();
        hacerClic(lista);
        return espera().until(navegador -> {
            List<WebElement> encontradas = navegador.findElements(Selectores.OPCIONES_DE_LISTA);
            if (encontradas.isEmpty()) {
                return null;
            }
            boolean todasConTexto = encontradas.stream()
                    .noneMatch(opcion -> textoDe(opcion).isEmpty());
            return todasConTexto ? encontradas : null;
        });
    }

    /** Opciones que muestra hoy una lista desplegable (la deja cerrada). */
    protected List<String> opcionesDeLaLista(By lista) {
        List<String> nombres = abrirLista(lista).stream().map(this::textoDe).toList();
        cerrarOverlayConEscape();
        esperarQueSeCierrenLasListas();
        return nombres;
    }

    /**
     * Las opciones de una lista que se acaba de cerrar siguen unos instantes en el
     * DOM: si no se espera, se leen mezcladas con las de la siguiente lista.
     */
    protected void esperarQueSeCierrenLasListas() {
        espera().until(navegador ->
                navegador.findElements(Selectores.OPCIONES_DE_LISTA).isEmpty());
    }

    /** Elige una opcion de una lista desplegable comparando el texto exacto. */
    protected void elegirDeLaLista(By lista, String opcion) {
        WebElement elegida = abrirLista(lista).stream()
                .filter(disponible -> textoDe(disponible).equalsIgnoreCase(opcion.trim()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "La lista no tiene la opcion \"" + opcion + "\"."));
        ((JavascriptExecutor) navegador())
                .executeScript("arguments[0].scrollIntoView({block: 'center'});", elegida);
        hacerClic(elegida);
        esperarQueSeCierrenLasListas();
    }

    /** Texto leido del DOM, para no depender de animaciones ni del scroll. */
    protected String textoDe(WebElement elemento) {
        Object valor = ((JavascriptExecutor) navegador())
                .executeScript("return arguments[0].textContent;", elemento);
        return valor == null ? "" : valor.toString().replace('\u00a0', ' ').trim();
    }

    protected String textoDe(By selector) {
        return textoDe(verVisible(selector));
    }

    /**
     * Estatus que muestra una fila: "ACTIVO" o "INACTIVO" (vacio si no lo dice).
     * Se busca primero "INACTIVO" porque la palabra contiene a "ACTIVO".
     */
    protected static String estatusDeLaFila(String textoDeLaFila) {
        String mayusculas = textoDeLaFila.toUpperCase();
        if (mayusculas.contains("INACTIVO")) {
            return "INACTIVO";
        }
        return mayusculas.contains("ACTIVO") ? "ACTIVO" : "";
    }

    /** Filas con datos de la tabla (la primera fila de estas tablas es el encabezado). */
    protected List<WebElement> filasConDatos() {
        verVisible(Selectores.TABLA);
        return espera().until(navegador -> {
            List<WebElement> filas = navegador.findElements(Selectores.FILAS_CON_DATOS);
            return filas.isEmpty() ? null : filas;
        });
    }

    /**
     * Encabezados de la tabla sin el nombre de los iconos de ordenamiento
     * ("Descripcionarrow_drop_up..." se lee "Descripcion").
     */
    public List<String> encabezadosDeLaTabla() {
        verVisible(Selectores.TABLA);
        return buscarTodos(Selectores.ENCABEZADOS_DE_TABLA).stream()
                .map(encabezado -> textoDe(encabezado)
                        .replaceAll("arrow_drop_(up|down)", "")
                        .split("\n")[0].trim())
                .filter(texto -> !texto.isEmpty())
                .toList();
    }

    /**
     * Vuelve a intentar una lectura de la tabla cuando Angular la vuelve a pintar
     * mientras se estaba leyendo (stale element).
     */
    protected <T> T leerAunqueLaTablaSeRefresque(Supplier<T> lectura) {
        return espera().until(navegador -> {
            try {
                return lectura.get();
            } catch (StaleElementReferenceException seRefresco) {
                return null;
            }
        });
    }

    /**
     * Igual que {@link #leerAunqueLaTablaSeRefresque(Supplier)} para lecturas cuyo
     * resultado puede ser "no encontrado": ahi un null es la respuesta buena y no
     * hay que seguir esperando.
     */
    protected <T> Optional<T> leerAunqueLaTablaSeRefresqueOpcional(Supplier<T> lectura) {
        return espera().until(navegador -> {
            try {
                return Optional.ofNullable(lectura.get());
            } catch (StaleElementReferenceException seRefresco) {
                return null;
            }
        });
    }

    /**
     * Elige el dia 1 del mes indicado en el calendario numero {@code indice} de la
     * pantalla (0 = el primero). El calendario de esta aplicacion abre en vista de
     * dias o de anios segun el campo, por eso se revisa que muestra antes de elegir.
     */
    protected void elegirElPrimerDiaDelMes(int indiceDelCalendario, String anio, String mes) {
        elegirElDiaDelMes(indiceDelCalendario, anio, mes, 1);
    }

    /** Igual que elegirElPrimerDiaDelMes, eligiendo el dia indicado. */
    protected void elegirElDiaDelMes(int indiceDelCalendario, String anio, String mes, int dia) {
        hacerClic(Selectores.CALENDARIO_BOTONES, indiceDelCalendario);
        verVisible(Selectores.CALENDARIO_ABIERTO);
        if (!hayCeldaDelCalendario(anio)) {
            hacerClic(Selectores.CALENDARIO_PERIODO);
        }
        // La vista de anios muestra 24 anios por pagina: puede hacer falta avanzar.
        for (int pagina = 0; pagina < 3 && !hayCeldaDelCalendario(anio); pagina++) {
            hacerClic(Selectores.CALENDARIO_SIGUIENTE);
        }
        clicEnLaCeldaDelCalendario(anio);
        clicEnLaCeldaDelCalendario(mes);
        clicEnLaCeldaDelCalendario(String.valueOf(dia));
        esperarQueDesaparezca(Selectores.CALENDARIO_ABIERTO);
    }

    private boolean hayCeldaDelCalendario(String texto) {
        return buscarTodos(Selectores.CALENDARIO_DIAS).stream()
                .anyMatch(celda -> textoDelCalendario(celda).equals(texto));
    }

    private void clicEnLaCeldaDelCalendario(String texto) {
        WebElement celda = espera().until(navegador -> navegador
                .findElements(Selectores.CALENDARIO_DIAS).stream()
                .filter(candidata -> textoDelCalendario(candidata).equals(texto))
                .findFirst()
                .orElse(null));
        if (celda.getDomAttribute("aria-disabled") != null
                && celda.getDomAttribute("aria-disabled").equals("true")) {
            throw new IllegalStateException("El calendario no permite elegir \"" + texto
                    + "\": la pantalla limita el rango de fechas. Ajuste amex.reportes.* o "
                    + "amex.datos.* en configuracion.properties.");
        }
        celda.click();
    }

    /** Los meses del calendario se muestran abreviados y con punto ("ENE."). */
    private String textoDelCalendario(WebElement celda) {
        String texto = textoDe(celda).toUpperCase();
        return texto.endsWith(".") ? texto.substring(0, texto.length() - 1) : texto;
    }

    /** Clic en uno de los elementos que comparten selector (por ejemplo dos calendarios). */
    protected void hacerClic(By selector, int indice) {
        List<WebElement> elementos = espera().until(navegador -> {
            List<WebElement> encontrados = navegador.findElements(selector);
            return encontrados.size() > indice ? encontrados : null;
        });
        elementos.get(indice).click();
    }

    protected String urlBase() {
        return Configuracion.urlBase();
    }
}
