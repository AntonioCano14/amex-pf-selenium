package com.amex.pf.paginas;

import java.util.List;

import org.openqa.selenium.WebElement;
import org.testng.Assert;

import com.amex.pf.base.Configuracion;

/**
 * Pantalla de Reportes: tipos de reporte, filtros y generacion de los reportes en
 * Excel (ola 4). Generar un reporte no modifica informacion: solo descarga.
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

    // ------------------------------------------------------- Descargas (ola 4)

    public PaginaReportes elegirElTipoDeReporte(String tipo) {
        elegirDeLaLista(Selectores.REPORTES_LISTA_TIPO, tipo);
        return this;
    }

    public PaginaReportes filtrarPorDni(String dni) {
        escribir(Selectores.REPORTES_CAMPO_DNI, dni);
        return this;
    }

    /**
     * Los reportes de totales exigen fecha inicio y fin: el boton Generar reporte
     * queda deshabilitado hasta que se eligen las dos en el calendario.
     */
    public PaginaReportes elegirElRangoDeFechas() {
        elegirElPrimerDiaDelMes(0, Configuracion.obtener("amex.reportes.anio.inicio"),
                Configuracion.obtener("amex.reportes.mes.inicio"));
        elegirElPrimerDiaDelMes(1, Configuracion.obtener("amex.reportes.anio.fin"),
                Configuracion.obtener("amex.reportes.mes.fin"));
        return this;
    }

    public PaginaReportes generarElReporte() {
        espera().until(navegador ->
                navegador.findElement(Selectores.REPORTES_BOTON_GENERAR).isEnabled());
        hacerClic(Selectores.REPORTES_BOTON_GENERAR);
        return this;
    }

    /** Texto del popup de la aplicacion (por ejemplo "No se encontraron resultados."). */
    public String mensajeDelPopup() {
        return textoDe(Selectores.MODAL);
    }

    public PaginaReportes aceptarElPopup() {
        cerrarModalSiEstaAbierto();
        return this;
    }

    private String valorDeCampo(WebElement campo) {
        String valor = campo.getDomProperty("value");
        return valor == null ? "" : valor;
    }
}
