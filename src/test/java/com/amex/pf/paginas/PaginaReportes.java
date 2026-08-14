package com.amex.pf.paginas;

import java.util.List;

import org.openqa.selenium.WebElement;
import org.testng.Assert;

/**
 * Pantalla de Reportes, solo consultas: tipos de reporte y filtros. No se presiona
 * GENERAR REPORTE (la descarga de archivos es de una ola posterior).
 */
public class PaginaReportes extends PaginaBase {

    /** Abre la pantalla y espera a que la aplicacion termine de navegar. */
    public PaginaReportes abrir() {
        esperarQueLaUrlContenga("expedient/reports");
        verVisible(Selectores.REPORTES_LISTA_TIPO);
        return this;
    }

    public List<String> tiposDeReporte() {
        return opcionesDeLaLista(Selectores.REPORTES_LISTA_TIPO);
    }

    public PaginaReportes llenarLosFiltros(String referencia, String dni, String nombre,
            String apellidos) {
        escribir(Selectores.REPORTES_CAMPO_REFERENCIA, referencia);
        escribir(Selectores.REPORTES_CAMPO_DNI, dni);
        escribir(Selectores.REPORTES_CAMPO_NOMBRE, nombre);
        escribir(Selectores.REPORTES_CAMPO_APELLIDOS, apellidos);
        return this;
    }

    public PaginaReportes limpiarLosFiltros() {
        hacerClic(Selectores.REPORTES_BOTON_LIMPIAR);
        return this;
    }

    public List<String> valoresDeLosFiltros() {
        return buscarTodos(Selectores.REPORTES_CAMPOS_DEL_FILTRO).stream()
                .map(campo -> valorDeCampo(campo))
                .toList();
    }

    public PaginaReportes losFiltrosDebenEstarVacios() {
        espera().until(navegador ->
                valoresDeLosFiltros().stream().allMatch(String::isBlank));
        Assert.assertTrue(valoresDeLosFiltros().stream().allMatch(String::isBlank),
                "Limpiar filtros dejo valores: " + valoresDeLosFiltros() + ".");
        return this;
    }

    private String valorDeCampo(WebElement campo) {
        String valor = campo.getDomProperty("value");
        return valor == null ? "" : valor;
    }
}
