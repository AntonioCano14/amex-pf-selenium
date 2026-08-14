package com.amex.pf.pruebas;

import java.util.List;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.amex.pf.base.PruebaBase;
import com.amex.pf.paginas.PaginaCostos;
import com.amex.pf.paginas.PaginaDashboard;
import com.amex.pf.paginas.PaginaLogin;
import com.amex.pf.paginas.PaginaPrincipal;
import com.amex.pf.paginas.PaginaReportes;
import com.amex.pf.paginas.PaginaTasas;
import com.amex.pf.paginas.Selectores;

/**
 * Ola 3 - Consultas de Tasas de interes, Costos, Reportes y Dashboard
 * (PF_CP_102, 104, 105, 148, 156 y 160).
 *
 * Solo lectura: se consulta lo que muestra cada pantalla. No se presiona ACTUALIZAR
 * en tasas, ni AGREGAR en costos, ni GENERAR REPORTE (las descargas son de la ola 4).
 */
public class TasasCostosYReportesPruebas extends PruebaBase {

    private PaginaPrincipal inicio;

    @BeforeMethod(alwaysRun = true)
    public void iniciarSesion() {
        inicio = new PaginaLogin().iniciarSesionConCredencialesValidas();
    }

    @Test(groups = "consultas",
            description = "PF_CP_102 Tasas de interes muestra las tablas del periodo elegido")
    public void pfCp102ConsultaDeTasasDeInteres() {
        inicio.irAlMenu("Tasas de interes");

        new PaginaTasas().abrir()
                .elegirTipoDeTasa("Tasas de interés")
                .elegirElPrimerPeriodo()
                .debeVerseElTexto("Tasas de intereses financieros")
                .debeVerseElTexto("Tasas de intereses punitorios")
                .elCampoDebeEstarVisible(Selectores.TASAS_CAMPO_TEM_PESOS);
    }

    @Test(groups = "consultas",
            description = "PF_CP_104 El porcentaje de la tasa solo acepta numeros")
    public void pfCp104CaracteresDelPorcentajeDeLaTasa() {
        inicio.irAlMenu("Tasas de interes");

        PaginaTasas tasas = new PaginaTasas().abrir()
                .elegirTipoDeTasa("Tasas de interés")
                .elegirElPrimerPeriodo();

        String conLetras = tasas.loQueAcepta(Selectores.TASAS_CAMPO_TEM_PESOS, "abc");
        Assert.assertTrue(conLetras.replaceAll("[\\d.,]", "").isEmpty(),
                "El campo de porcentaje acepto letras: \"" + conLetras + "\".");
    }

    @Test(groups = "consultas",
            description = "PF_CP_105 Costo Financiero Total muestra su tabla")
    public void pfCp105ConsultaDeCostoFinancieroTotal() {
        inicio.irAlMenu("Tasas de interes");

        new PaginaTasas().abrir()
                .elegirTipoDeTasa("Costo Financiero Total")
                .elegirElPrimerPeriodo()
                .elCampoDebeEstarVisible(Selectores.TASAS_CAMPO_CFT_PESOS);
    }

    @Test(groups = "consultas",
            description = "PF_CP_148 Seleccionar un producto abre su pantalla de costos")
    public void pfCp148CostosDelProducto() {
        inicio.irAlMenu("Costos");

        new PaginaCostos().abrir()
                .seleccionarElPrimerProducto()
                .debePedirAnioYMes()
                .elegirElPrimerPeriodo()
                .debeMostrarLosCostosDelPeriodo();
    }

    @Test(groups = "consultas",
            description = "PF_CP_156 Limpiar filtros borra los datos del filtro de Reportes")
    public void pfCp156LimpiarFiltrosDeReportes() {
        inicio.irAlMenu("Reportes");

        PaginaReportes reportes = new PaginaReportes().abrir();
        List<String> tipos = reportes.tiposDeReporte();
        Assert.assertTrue(tipos.contains("Reporte general"),
                "La lista de tipos de reporte no ofrece \"Reporte general\". Ofrece: "
                        + tipos + ".");

        reportes.llenarLosFiltros("REF12345", "12345678", "JUAN", "PEREZ")
                .limpiarLosFiltros()
                .losFiltrosDebenEstarVacios();
    }

    @Test(groups = "consultas",
            description = "PF_CP_160 El dashboard muestra la grafica con sus indicadores")
    public void pfCp160GraficaDelDashboard() {
        inicio.irAlMenu("Dashboard");

        new PaginaDashboard().abrir()
                .debeVerseLaGrafica()
                .debeVerseElTexto("Total de firmas")
                .debeVerseElTexto("Firmas concluidas")
                .debeVerseElTexto("Firmas en proceso");
    }
}
