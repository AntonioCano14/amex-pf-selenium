package com.amex.pf.base;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

/**
 * Crea el navegador. Desde Selenium 4.6 el driver se descarga solo
 * (Selenium Manager): no hay que bajar chromedriver a mano.
 */
public final class FabricaDeNavegador {

    public static final Path CARPETA_DESCARGAS =
            Paths.get(System.getProperty("user.dir"), "resultados", "descargas");

    static {
        // Silencia los avisos de CDP de Selenium, que no afectan la ejecucion
        // y ensucian la consola.
        java.util.logging.Logger.getLogger("org.openqa.selenium.devtools")
                .setLevel(java.util.logging.Level.SEVERE);
        java.util.logging.Logger.getLogger("org.openqa.selenium.chromium")
                .setLevel(java.util.logging.Level.SEVERE);
    }

    private FabricaDeNavegador() {
    }

    public static WebDriver crear() {
        WebDriver navegador = switch (Configuracion.navegador().toLowerCase()) {
            case "firefox" -> crearFirefox();
            case "edge" -> crearEdge();
            default -> crearChrome();
        };
        navegador.manage().timeouts()
                .implicitlyWait(Duration.ZERO)  // se usan esperas explicitas
                .pageLoadTimeout(Duration.ofSeconds(60));
        navegador.manage().window().setSize(new org.openqa.selenium.Dimension(1600, 900));
        return navegador;
    }

    private static WebDriver crearChrome() {
        ChromeOptions opciones = new ChromeOptions();
        if (Configuracion.sinInterfaz()) {
            opciones.addArguments("--headless=new");
        }
        opciones.addArguments("--no-sandbox", "--disable-dev-shm-usage", "--lang=es-AR");
        Map<String, Object> preferencias = new HashMap<>();
        preferencias.put("download.default_directory", CARPETA_DESCARGAS.toString());
        preferencias.put("download.prompt_for_download", false);
        preferencias.put("download.directory_upgrade", true);
        // Sin esto Chrome bloquea la segunda descarga de la misma pantalla.
        preferencias.put("profile.default_content_setting_values.automatic_downloads", 1);
        preferencias.put("safebrowsing.enabled", true);
        opciones.setExperimentalOption("prefs", preferencias);
        return new ChromeDriver(opciones);
    }

    private static WebDriver crearFirefox() {
        FirefoxOptions opciones = new FirefoxOptions();
        if (Configuracion.sinInterfaz()) {
            opciones.addArguments("-headless");
        }
        opciones.addPreference("browser.download.dir", CARPETA_DESCARGAS.toString());
        opciones.addPreference("browser.download.folderList", 2);
        return new FirefoxDriver(opciones);
    }

    private static WebDriver crearEdge() {
        EdgeOptions opciones = new EdgeOptions();
        if (Configuracion.sinInterfaz()) {
            opciones.addArguments("--headless=new");
        }
        return new EdgeDriver(opciones);
    }
}
