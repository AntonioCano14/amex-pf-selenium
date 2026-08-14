package com.amex.pf.paginas;

import java.time.Duration;
import java.util.List;
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
     * Hace clic y, si una imagen del menu o un overlay tapa el elemento, repite el
     * clic por JavaScript en lugar de fallar.
     */
    protected void hacerClic(By selector) {
        WebElement elemento = espera().until(ExpectedConditions.elementToBeClickable(selector));
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

    protected void esperarQueDesaparezca(By selector) {
        espera().until(ExpectedConditions.invisibilityOfElementLocated(selector));
    }

    /**
     * Texto leido del DOM. Angular Material agrega las opciones a la pantalla antes
     * de terminar de pintarlas, y getText() devolveria vacio en ese instante.
     */
    protected String textoDe(WebElement elemento) {
        Object valor = ((JavascriptExecutor) navegador())
                .executeScript("return arguments[0].textContent;", elemento);
        return valor == null ? "" : valor.toString().replace('\u00a0', ' ').trim();
    }

    protected String textoDe(By selector) {
        return textoDe(verVisible(selector));
    }

    /** Abre una lista desplegable y espera a que todas sus opciones tengan texto. */
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

    protected List<String> opcionesDeLaLista(By lista) {
        List<String> nombres = abrirLista(lista).stream().map(this::textoDe).toList();
        cerrarOverlayConEscape();
        esperarQueSeCierrenLasListas();
        return nombres;
    }

    protected void elegirDeLaLista(By lista, String valor) {
        List<WebElement> opciones = abrirLista(lista);
        WebElement elegida = opciones.stream()
                .filter(opcion -> textoDe(opcion).equalsIgnoreCase(valor.trim()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "La lista no tiene la opcion \"" + valor + "\". Muestra hoy: "
                                + opciones.stream().map(this::textoDe).toList() + "."));
        ((JavascriptExecutor) navegador())
                .executeScript("arguments[0].scrollIntoView({block: 'center'});", elegida);
        elegida.click();
        esperarQueSeCierrenLasListas();
    }

    /** Las opciones de una lista siguen en la pantalla un instante despues de cerrarla. */
    protected void esperarQueSeCierrenLasListas() {
        espera().until(navegador ->
                navegador.findElements(Selectores.OPCIONES_DE_LISTA).isEmpty());
    }

    /** Un modal abierto tapa el menu de salir: se cierra antes de terminar el caso. */
    protected void cerrarModalSiEstaAbierto() {
        if (!estaVisible(Selectores.MODAL, 2)) {
            return;
        }
        for (By boton : List.of(Selectores.botonDelModal("CANCELAR"),
                Selectores.MODAL_BOTON_CERRAR,
                Selectores.botonDelModal("ACEPTAR"))) {
            List<WebElement> botones = buscarTodos(boton);
            if (!botones.isEmpty()) {
                botones.get(0).click();
                esperarQueDesaparezca(Selectores.MODAL);
                return;
            }
        }
        cerrarOverlayConEscape();
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

    protected void cerrarOverlayConEscape() {
        navegador().findElement(By.tagName("body")).sendKeys(Keys.ESCAPE);
    }

    protected String urlBase() {
        return Configuracion.urlBase();
    }
}
