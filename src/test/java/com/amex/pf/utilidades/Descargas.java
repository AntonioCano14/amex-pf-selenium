package com.amex.pf.utilidades;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.amex.pf.base.Configuracion;
import com.amex.pf.base.FabricaDeNavegador;

/**
 * Archivos que descarga la aplicacion (ola 4): esperar a que el navegador termine
 * de bajarlos y leer su contenido.
 *
 * Todo se descarga en resultados/descargas, que se limpia antes de cada caso para
 * que el archivo que se revisa sea siempre el del caso en curso.
 */
public final class Descargas {

    /** Chrome deja este sufijo mientras la descarga no termina. */
    private static final String EN_PROCESO = ".crdownload";

    private Descargas() {
    }

    public static Path carpeta() {
        try {
            return Files.createDirectories(FabricaDeNavegador.CARPETA_DESCARGAS);
        } catch (IOException noSePudoCrear) {
            throw new UncheckedIOException(noSePudoCrear);
        }
    }

    /** Borra lo descargado antes, para no confundir archivos de casos anteriores. */
    public static void limpiar() {
        try (var archivos = Files.list(carpeta())) {
            for (Path archivo : archivos.toList()) {
                Files.deleteIfExists(archivo);
            }
        } catch (IOException noSePudoLimpiar) {
            throw new UncheckedIOException(noSePudoLimpiar);
        }
    }

    /**
     * Espera el archivo que acaba de pedirse. Falla con un mensaje claro si la
     * aplicacion no lo entrega dentro del tiempo configurado en amex.espera.
     */
    public static Path esperarArchivo(String extension) {
        return esperarArchivo(extension, Configuracion.esperaMaximaSegundos() * 6L);
    }

    /** True si la aplicacion ya empezo a bajar algo: sirve para reintentar un clic. */
    public static boolean empezoLaDescarga(long segundos) {
        Instant hasta = Instant.now().plusSeconds(segundos);
        while (Instant.now().isBefore(hasta)) {
            if (!archivos().isEmpty()) {
                return true;
            }
            dormir(500);
        }
        return false;
    }

    public static Path esperarArchivo(String extension, long segundos) {
        Duration limite = Duration.ofSeconds(segundos);
        Instant hasta = Instant.now().plus(limite);
        while (Instant.now().isBefore(hasta)) {
            Optional<Path> descargado = archivos().stream()
                    .filter(archivo -> archivo.getFileName().toString().endsWith(extension))
                    .findFirst();
            boolean sigueBajando = archivos().stream()
                    .anyMatch(archivo -> archivo.getFileName().toString().endsWith(EN_PROCESO));
            if (descargado.isPresent() && !sigueBajando) {
                return descargado.get();
            }
            dormir(500);
        }
        throw new AssertionError("La aplicacion no descargo ningun archivo \"" + extension
                + "\" en " + limite.toSeconds() + " s. La carpeta tiene: " + archivos() + ".");
    }

    public static List<Path> archivos() {
        try (var contenido = Files.list(carpeta())) {
            return contenido.sorted(Comparator.comparing(Path::getFileName)).toList();
        } catch (IOException noSePudoLeer) {
            throw new UncheckedIOException(noSePudoLeer);
        }
    }

    /** Nombres de las hojas del Excel, en orden. */
    public static List<String> hojasDelExcel(Path excel) {
        try (Workbook libro = WorkbookFactory.create(excel.toFile())) {
            List<String> hojas = new ArrayList<>();
            for (int numero = 0; numero < libro.getNumberOfSheets(); numero++) {
                hojas.add(libro.getSheetName(numero));
            }
            return hojas;
        } catch (IOException noSePudoAbrir) {
            throw new UncheckedIOException(noSePudoAbrir);
        }
    }

    /** Encabezados (primera fila con datos) de la primera hoja del Excel. */
    public static List<String> encabezadosDelExcel(Path excel) {
        return encabezadosDeLaHoja(excel, null);
    }

    /**
     * Encabezados de la hoja indicada. Con {@code hoja} nulo se usa la primera.
     * La primera fila con celdas se toma como encabezado: algunos reportes de la
     * aplicacion dejan filas vacias arriba.
     */
    public static List<String> encabezadosDeLaHoja(Path excel, String hoja) {
        try (Workbook libro = WorkbookFactory.create(excel.toFile())) {
            Sheet pagina = hoja == null ? libro.getSheetAt(0) : libro.getSheet(hoja);
            if (pagina == null) {
                throw new AssertionError("El archivo " + excel.getFileName()
                        + " no tiene la hoja \"" + hoja + "\". Tiene: " + hojasDelExcel(excel)
                        + ".");
            }
            DataFormatter lector = new DataFormatter();
            for (Row fila : pagina) {
                List<String> celdas = new ArrayList<>();
                for (Cell celda : fila) {
                    celdas.add(lector.formatCellValue(celda).trim());
                }
                celdas.removeIf(String::isEmpty);
                if (!celdas.isEmpty()) {
                    return celdas;
                }
            }
            return List.of();
        } catch (IOException noSePudoAbrir) {
            throw new UncheckedIOException(noSePudoAbrir);
        }
    }

    /** Cuantas filas con datos trae la primera hoja, sin contar el encabezado. */
    public static int filasConDatosDelExcel(Path excel) {
        try (Workbook libro = WorkbookFactory.create(excel.toFile())) {
            Sheet pagina = libro.getSheetAt(0);
            return Math.max(0, pagina.getLastRowNum());
        } catch (IOException noSePudoAbrir) {
            throw new UncheckedIOException(noSePudoAbrir);
        }
    }

    /** Nombres de los archivos que trae el ZIP. */
    public static List<String> contenidoDelZip(Path zip) {
        try (ZipFile comprimido = new ZipFile(zip.toFile())) {
            return comprimido.stream().map(ZipEntry::getName).sorted().toList();
        } catch (IOException noSePudoAbrir) {
            throw new AssertionError("El archivo " + zip.getFileName()
                    + " no se pudo abrir como ZIP: " + noSePudoAbrir.getMessage(), noSePudoAbrir);
        }
    }

    private static void dormir(long milisegundos) {
        try {
            Thread.sleep(milisegundos);
        } catch (InterruptedException interrumpido) {
            Thread.currentThread().interrupt();
        }
    }
}
