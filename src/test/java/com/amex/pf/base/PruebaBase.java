package com.amex.pf.base;

import java.time.Duration;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Listeners;

import com.amex.pf.paginas.PaginaLogin;
import com.amex.pf.utilidades.CierreDeSesionPorApi;
import com.amex.pf.utilidades.EvidenciaListener;
import com.amex.pf.utilidades.OrdenDeLaMatriz;
import com.amex.pf.utilidades.ReporteEnConsolaListener;

/**
 * Clase de la que heredan todas las pruebas: abre el navegador antes de cada
 * caso y lo cierra despues.
 *
 * IMPORTANTE: la aplicacion permite UNA SOLA sesion activa por usuario, por eso
 * cada caso que inicia sesion debe cerrarla (ver cerrarSesionSiHayAlguna).
 *
 * Los listeners se registran aqui y NO en las suites XML: asi el reporte en consola
 * y las evidencias salen igual con "mvn test -Dsuite=..." que al correr un solo caso
 * con "mvn test -Dtest=Clase#metodo", y sin imprimir cada linea dos veces.
 */
@Listeners({EvidenciaListener.class, ReporteEnConsolaListener.class, OrdenDeLaMatriz.class})
public abstract class PruebaBase {

    private static final ThreadLocal<WebDriver> NAVEGADOR = new ThreadLocal<>();

    public static WebDriver navegador() {
        return NAVEGADOR.get();
    }

    public static WebDriverWait espera() {
        return new WebDriverWait(navegador(),
                Duration.ofSeconds(Configuracion.esperaMaximaSegundos()));
    }

    /**
     * Si la ejecucion anterior murio sin cerrar sesion, se cierra ahora: si no, el
     * usuario queda bloqueado (la aplicacion permite una sola sesion por usuario).
     */
    @BeforeSuite(alwaysRun = true)
    public void liberarLaSesionAnterior() {
        CierreDeSesionPorApi.liberarSesionPendiente();
    }

    @BeforeMethod(alwaysRun = true)
    public void abrirAplicacion() {
        NAVEGADOR.set(FabricaDeNavegador.crear());
        navegador().get(Configuracion.urlBase() + "#/login");
    }

    @AfterMethod(alwaysRun = true)
    public void cerrarAplicacion() {
        if (navegador() == null) {
            return;
        }
        String token = tokenGuardado();
        CierreDeSesionPorApi.recordarToken(token);
        boolean cerroDesdeLaPantalla = new PaginaLogin().cerrarSesionSiHayAlguna();
        try {
            // Red de seguridad: si no se pudo cerrar desde la pantalla, se cierra
            // por API para no dejar al usuario bloqueado (una sola sesion por usuario).
            if (!cerroDesdeLaPantalla && token != null) {
                CierreDeSesionPorApi.cerrarSesion(token);
            }
            CierreDeSesionPorApi.olvidarToken();
        } finally {
            navegador().quit();
            NAVEGADOR.remove();
        }
    }

    /** Token que la aplicacion guarda en localStorage al iniciar sesion. */
    private String tokenGuardado() {
        try {
            Object valor = ((JavascriptExecutor) navegador())
                    .executeScript("return window.localStorage.getItem('token');");
            return valor == null ? null : valor.toString();
        } catch (RuntimeException sinAcceso) {
            return null;
        }
    }
}
