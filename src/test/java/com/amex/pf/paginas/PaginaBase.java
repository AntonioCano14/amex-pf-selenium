package com.amex.pf.paginas;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
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
        for (String etiqueta : new String[]{"CANCELAR", "Cancelar", "ACEPTAR", "Aceptar"}) {
            List<WebElement> botones = buscarTodos(Selectores.botonDelModal(etiqueta));
            if (!botones.isEmpty()) {
                botones.get(0).click();
                esperarQueDesaparezca(Selectores.MODAL);
                return;
            }
        }
        cerrarOverlayConEscape();
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
        elegida.click();
        esperarQueSeCierrenLasListas();
    }

    /** Texto leido del DOM, para no depender de animaciones ni del scroll. */
    protected String textoDe(WebElement elemento) {
        Object valor = ((JavascriptExecutor) navegador())
                .executeScript("return arguments[0].textContent;", elemento);
        return valor == null ? "" : valor.toString().replace('\u00a0', ' ').trim();
    }

    protected String urlBase() {
        return Configuracion.urlBase();
    }
}
