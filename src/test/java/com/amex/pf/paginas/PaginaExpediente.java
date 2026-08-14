package com.amex.pf.paginas;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.SkipException;

import com.amex.pf.base.Configuracion;
import com.amex.pf.utilidades.Descargas;

/**
 * Pantalla de Expediente (tabla de solicitudes), solo consultas: columnas de la
 * tabla, filtro y detalle de una solicitud.
 *
 * El dato con el que se filtra se toma de la propia tabla, por eso no hace falta
 * cargar datos semilla en el ambiente.
 */
public class PaginaExpediente extends PaginaBase {

    public static final int COLUMNA_REFERENCIA = 0;
    public static final int COLUMNA_NOMBRE = 1;
    public static final int COLUMNA_DNI = 3;

    /** Abre la pantalla y espera a que la aplicacion termine de navegar. */
    public PaginaExpediente abrir() {
        esperarQueLaUrlContenga("expedient/request");
        verVisible(Selectores.TABLA);
        return this;
    }

    public int cuantasSolicitudesMuestraLaTabla() {
        return filasConDatos().size();
    }

    public String valorDeLaPrimeraFila(int columna) {
        return leerAunqueLaTablaSeRefresque(() -> {
            List<WebElement> celdas = filasConDatos().get(0).findElements(By.tagName("td"));
            Assert.assertTrue(celdas.size() > columna,
                    "La fila solo tiene " + celdas.size() + " columnas.");
            return textoDe(celdas.get(columna));
        });
    }

    public PaginaExpediente abrirElFiltro() {
        hacerClic(Selectores.BOTON_FILTRAR);
        verVisible(Selectores.SOLICITUDES_FILTRO_REFERENCIA);
        return this;
    }

    public PaginaExpediente elFiltroDebeTenerSusCampos() {
        for (By campo : List.of(Selectores.SOLICITUDES_FILTRO_REFERENCIA,
                Selectores.SOLICITUDES_FILTRO_DNI,
                Selectores.SOLICITUDES_FILTRO_NOMBRE,
                Selectores.SOLICITUDES_FILTRO_APELLIDOS,
                Selectores.SOLICITUDES_FILTRO_FECHA_INICIO,
                Selectores.SOLICITUDES_FILTRO_FECHA_FIN,
                Selectores.SOLICITUDES_FILTRO_ESTATUS)) {
            Assert.assertTrue(estaVisible(campo, 10),
                    "El filtro de Expediente no muestra el campo " + campo + ".");
        }
        return this;
    }

    public PaginaExpediente filtrarPorDni(String dni) {
        escribir(Selectores.SOLICITUDES_FILTRO_DNI, dni);
        hacerClic(Selectores.BOTON_BUSCAR);
        return this;
    }

    public PaginaExpediente limpiarElFiltro() {
        hacerClic(Selectores.BOTON_LIMPIAR);
        return this;
    }

    public String valorDelFiltroDeDni() {
        String valor = valorDe(Selectores.SOLICITUDES_FILTRO_DNI);
        return valor == null ? "" : valor;
    }

    public List<String> documentosDeLaTabla() {
        return leerAunqueLaTablaSeRefresque(() -> filasConDatos().stream()
                .map(fila -> textoDe(fila.findElements(By.tagName("td")).get(COLUMNA_DNI)))
                .toList());
    }

    /** Espera a que la tabla responda al filtro y devuelve los DNI que muestra. */
    public List<String> documentosDeLaTablaCuandoTodosSean(String dni) {
        espera().until(navegador -> documentosDeLaTabla().stream().allMatch(dni::equals));
        return documentosDeLaTabla();
    }

    public PaginaExpediente abrirElDetalleDeLaPrimeraSolicitud() {
        if (filasConDatos().isEmpty()) {
            throw new SkipException("El ambiente no tiene solicitudes para abrir su detalle.");
        }
        // El clic va dentro del reintento: Angular vuelve a pintar la tabla y el boton
        // que se acababa de localizar queda obsoleto.
        leerAunqueLaTablaSeRefresque(() -> {
            List<WebElement> ojos = filasConDatos().get(0)
                    .findElements(Selectores.VER_DETALLE_DE_LA_SOLICITUD);
            Assert.assertFalse(ojos.isEmpty(),
                    "La fila de la solicitud no muestra el boton Ver detalle.");
            if (!ojos.get(0).isDisplayed()) {
                return null;
            }
            hacerClic(ojos.get(0));
            return true;
        });
        esperarQueLaUrlContenga("requisitions/view");
        return this;
    }

    public PaginaExpediente elDetalleDebeMostrar(String... etiquetas) {
        for (String etiqueta : etiquetas) {
            Assert.assertTrue(estaVisible(Selectores.textoVisible(etiqueta), 15),
                    "El detalle de la solicitud no muestra \"" + etiqueta + "\".");
        }
        return this;
    }

    // ------------------------------------------------------- Descargas (ola 4)

    public PaginaExpediente exportarAExcel() {
        esperarQueTermineDeCargar();
        hacerClic(Selectores.SOLICITUDES_BOTON_EXPORTAR);
        return this;
    }

    public boolean hayBotonImportar() {
        return estaVisible(Selectores.SOLICITUDES_BOTON_IMPORTAR, 5);
    }

    /**
     * Descarga uno de los dos ZIP de la primera solicitud firmada: 0 es el
     * expediente completo y 1 el ZIP Griffin. Las solicitudes sin documentos no
     * muestran estos botones, por eso se busca la primera fila que si los tenga.
     */
    public PaginaExpediente descargarElZipDeUnaSolicitudFirmada(int cual) {
        esperarQueTermineDeCargar();
        if (buscarTodos(Selectores.ZIP_DE_LA_FILA).size() <= cual) {
            throw new SkipException("Ninguna solicitud de la tabla ofrece los dos botones de "
                    + "descarga (expediente y ZIP Griffin): hace falta una solicitud firmada "
                    + "con sus documentos cargados en este ambiente.");
        }
        // El ZIP se arma en el servidor y a veces el primer clic se pierde porque
        // Angular vuelve a pintar la tabla: se reintenta hasta que la descarga arranca.
        for (int intento = 1; intento <= 3; intento++) {
            clicEnElZip(cual);
            if (Descargas.empezoLaDescarga(Configuracion.esperaMaximaSegundos())) {
                return this;
            }
        }
        return this;
    }

    private void clicEnElZip(int cual) {
        // Angular vuelve a pintar la tabla mientras se busca la fila: si el boton queda
        // obsoleto se vuelve a localizar.
        leerAunqueLaTablaSeRefresque(() -> {
            List<WebElement> zips = filasConDatos().stream()
                    .map(fila -> fila.findElements(Selectores.ZIP_DE_LA_FILA))
                    .filter(botones -> botones.size() > cual)
                    .findFirst()
                    .orElse(List.of());
            if (zips.isEmpty()) {
                return null;
            }
            hacerClic(zips.get(cual));
            return true;
        });
    }

    public PaginaExpediente elDetalleDebeMostrarLaImagenDeLaTarjeta() {
        Assert.assertFalse(buscarTodos(By.cssSelector("img")).isEmpty(),
                "El detalle de la solicitud no muestra imagenes.");
        return this;
    }
}
