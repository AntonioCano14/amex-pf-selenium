package com.amex.pf.paginas;

import org.testng.Assert;

/** Dashboard de firmas, solo consulta: grafica e indicadores. */
public class PaginaDashboard extends PaginaBase {

    /** Abre la pantalla y espera a que la aplicacion termine de navegar. */
    public PaginaDashboard abrir() {
        esperarQueLaUrlContenga("fad-dashboard");
        return this;
    }

    public PaginaDashboard debeVerseLaGrafica() {
        Assert.assertTrue(estaVisible(Selectores.GRAFICA, 20),
                "El dashboard no muestra la grafica de firmas.");
        espera().until(navegador ->
                !navegador.findElements(Selectores.DASHBOARD_PARTES_DE_LA_GRAFICA).isEmpty());
        return this;
    }

    public PaginaDashboard debeVerseElTexto(String texto) {
        Assert.assertTrue(estaVisible(Selectores.textoVisible(texto), 20),
                "El dashboard no muestra \"" + texto + "\".");
        return this;
    }
}
