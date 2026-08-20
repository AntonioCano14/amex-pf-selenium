package com.amex.pf.pruebas;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.amex.pf.base.PruebaBase;
import com.amex.pf.paginas.PaginaLogin;
import com.amex.pf.paginas.PaginaPrincipal;

/**
 * Navegacion inicial y pantallas principales.
 * Los casos cuyo menu no exista para el perfil en uso se reportan OMITIDOS.
 */
public class NavegacionPruebas extends PruebaBase {

    private PaginaPrincipal inicio;

    @BeforeMethod(alwaysRun = true)
    public void iniciarSesion() {
        inicio = new PaginaLogin().iniciarSesionConCredencialesValidas();
    }

    @Test(groups = {"navegacion", "humo"},
            description = "PF_CP_008 Pantalla de Inicio con la grafica de solicitudes")
    public void pfCp008GraficaDeInicio() {
        inicio.laDireccionDebeContener("expedient/home").debeVerseUnaGrafica();
    }

    @Test(groups = "navegacion", description = "PF_CP_009 Pantalla de Inicio con la tabla de solicitudes")
    // Faltaria agregar que se haga un scroll para que se pueda visualizar la tabla y validar que si este presente
    public void pfCp009TablaDeInicio() {
        inicio.laPantallaDebeTenerUnaTablaConInformacion();
    }

    @Test(groups = "navegacion", description = "PF_CP_010 Pantalla de Usuarios con sus botones")
    public void pfCp010PantallaUsuarios() {
        inicio.irAlMenu("Usuarios")
                .laDireccionDebeContener("expedient/users")
                .laPantallaDebeTenerUnaTablaConInformacion();
    }

    @Test(groups = {"navegacion", "humo"},
            description = "PF_CP_108 Pantalla de Solicitudes con sus botones")
    public void pfCp108PantallaSolicitudes() {
        inicio.irAlMenu("Expediente").laDireccionDebeContener("expedient/request");
    }

    @Test(groups = "navegacion", description = "PF_CP_046 Menu de Catalogos")
    public void pfCp046MenuCatalogos() {
        inicio.irAlMenu("Catalogos").laDireccionDebeContener("expedient/catalogs");
    }

    @Test(groups = "navegacion", description = "PF_CP_101 Pantalla de Tasas de interes")
    public void pfCp101PantallaTasas() {
        inicio.irAlMenu("Tasas de interes").laDireccionDebeContener("expedient/rates");
    }

    @Test(groups = "navegacion", description = "PF_CP_153 Pantalla de Reportes con sus filtros")
    public void pfCp153PantallaReportes() {
        inicio.irAlMenu("Reportes").laDireccionDebeContener("expedient/reports");
    }

    @Test(groups = "navegacion", description = "PF_CP_159 Pantalla de Dashboard de firmas")
    public void pfCp159PantallaDashboard() {
        inicio.irAlMenu("Dashboard").laDireccionDebeContener("fad-dashboard");
    }

    @Test(groups = "navegacion", description = "PF_CP_147 Pantalla de Costos")
    public void pfCp147PantallaCostos() {
        inicio.irAlMenu("Costos").laDireccionDebeContener("expedient/costs");
    }

    @Test(groups = "navegacion", description = "PF_CP_151 Pantalla de Cuotas generales")
    public void pfCp151PantallaCuotasGenerales() {
        inicio.irAlMenu("Cuotas Generales").laDireccionDebeContener("general-fees");
    }

    @Test(groups = "navegacion", description = "SEG_001 La sesion se mantiene al recargar la pantalla")
    public void seg001LaSesionSeMantieneAlRecargar() {
        inicio.irAlMenu("Usuarios").recargarLaPantalla();
        Assert.assertTrue(inicio.laSesionSigueAbierta(),
                "La sesion se perdio al recargar la pantalla.");
    }
}
