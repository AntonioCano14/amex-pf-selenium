package com.amex.pf.paginas;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.SkipException;

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
        WebElement ojo = leerAunqueLaTablaSeRefresque(() -> {
            List<WebElement> ojos = filasConDatos().get(0)
                    .findElements(Selectores.VER_DETALLE_DE_LA_SOLICITUD);
            Assert.assertFalse(ojos.isEmpty(),
                    "La fila de la solicitud no muestra el boton Ver detalle.");
            return ojos.get(0).isDisplayed() ? ojos.get(0) : null;
        });
        ojo.click();
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

    public PaginaExpediente elDetalleDebeMostrarLaImagenDeLaTarjeta() {
        Assert.assertFalse(buscarTodos(By.cssSelector("img")).isEmpty(),
                "El detalle de la solicitud no muestra imagenes.");
        return this;
    }
}
