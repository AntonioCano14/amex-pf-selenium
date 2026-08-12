package com.amex.pf.utilidades;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.amex.pf.base.PruebaBase;

/**
 * Guarda una captura de pantalla cada vez que un caso falla.
 * Se registra en las suites XML: <listeners>.
 */
public class EvidenciaListener implements ITestListener {

    private static final Path CARPETA =
            Paths.get(System.getProperty("user.dir"), "resultados", "evidencias");

    @Override
    public void onTestFailure(ITestResult resultado) {
        if (PruebaBase.navegador() == null) {
            return;
        }
        try {
            Files.createDirectories(CARPETA);
            String marca = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
            String nombre = resultado.getMethod().getMethodName() + "-" + marca + ".png";
            File captura = ((TakesScreenshot) PruebaBase.navegador())
                    .getScreenshotAs(OutputType.FILE);
            Files.copy(captura.toPath(), CARPETA.resolve(nombre),
                    StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Evidencia guardada: " + CARPETA.resolve(nombre));
        } catch (Exception noSePudoGuardar) {
            System.err.println("No se pudo guardar la evidencia: " + noSePudoGuardar.getMessage());
        }
    }
}
