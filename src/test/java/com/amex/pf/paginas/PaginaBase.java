package com.amex.pf.paginas;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
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

    protected void hacerClic(By selector) {
        espera().until(ExpectedConditions.elementToBeClickable(selector)).click();
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

    protected void cerrarOverlayConEscape() {
        navegador().findElement(By.tagName("body")).sendKeys(Keys.ESCAPE);
    }

    protected String urlBase() {
        return Configuracion.urlBase();
    }
}
