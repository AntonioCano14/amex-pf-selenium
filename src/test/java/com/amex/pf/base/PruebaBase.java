package com.amex.pf.base;

import java.time.Duration;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import com.amex.pf.paginas.PaginaLogin;

/**
 * Clase de la que heredan todas las pruebas: abre el navegador antes de cada
 * caso y lo cierra despues.
 *
 * IMPORTANTE: la aplicacion permite UNA SOLA sesion activa por usuario, por eso
 * cada caso que inicia sesion debe cerrarla (ver cerrarSesionSiHayAlguna).
 */
public abstract class PruebaBase {

    private static final ThreadLocal<WebDriver> NAVEGADOR = new ThreadLocal<>();

    public static WebDriver navegador() {
        return NAVEGADOR.get();
    }

    public static WebDriverWait espera() {
        return new WebDriverWait(navegador(),
                Duration.ofSeconds(Configuracion.esperaMaximaSegundos()));
    }

    @BeforeMethod(alwaysRun = true)
    public void abrirAplicacion() {
        NAVEGADOR.set(FabricaDeNavegador.crear());
        navegador().get(Configuracion.urlBase() + "#/login");
    }

    @AfterMethod(alwaysRun = true)
    public void cerrarAplicacion() {
        if (navegador() != null) {
            try {
                new PaginaLogin().cerrarSesionSiHayAlguna();
            } finally {
                navegador().quit();
                NAVEGADOR.remove();
            }
        }
    }
}
