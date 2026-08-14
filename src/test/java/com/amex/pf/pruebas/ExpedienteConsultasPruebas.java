package com.amex.pf.pruebas;

import java.util.List;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.amex.pf.base.PruebaBase;
import com.amex.pf.paginas.PaginaExpediente;
import com.amex.pf.paginas.PaginaLogin;

/**
 * Ola 3 - Consultas de la pantalla de Expediente (PF_CP_109, 128 y 129).
 *
 * Solo lectura: se consulta la tabla, se filtra y se abre el detalle de una
 * solicitud, pero nunca se presiona Devolver, Dictaminar ni CREAR SOLICITUD.
 */
public class ExpedienteConsultasPruebas extends PruebaBase {

    private PaginaExpediente expediente;

    @BeforeMethod(alwaysRun = true)
    public void abrirExpediente() {
        new PaginaLogin().iniciarSesionConCredencialesValidas().irAlMenu("Expediente");
        expediente = new PaginaExpediente().abrir();
    }

    @Test(groups = {"consultas", "humo"},
            description = "PF_CP_109 La tabla de solicitudes muestra todas sus columnas")
    public void pfCp109ColumnasDeLaTablaDeSolicitudes() {
        List<String> esperadas = List.of("Referencia", "Nombre", "Apellido(s)", "DNI", "Producto",
                "Fecha creación", "Fecha modificación", "Campaña", "Creada por", "Estatus");

        List<String> actuales = expediente.encabezadosDeLaTabla();
        for (String columna : esperadas) {
            Assert.assertTrue(actuales.stream()
                            .anyMatch(actual -> actual.equalsIgnoreCase(columna)),
                    "La tabla de solicitudes no muestra la columna \"" + columna
                            + "\". Muestra hoy: " + actuales + ".");
        }
        Assert.assertTrue(expediente.cuantasSolicitudesMuestraLaTabla() > 0,
                "La tabla de solicitudes no muestra ninguna solicitud.");
    }

    @Test(groups = "consultas",
            description = "PF_CP_128 El filtro de solicitudes busca por sus campos")
    public void pfCp128FiltrarSolicitudes() {
        // El DNI del filtro se toma de la primera fila: la prueba no depende de que
        // el ambiente tenga una solicitud en particular.
        String dni = expediente.valorDeLaPrimeraFila(PaginaExpediente.COLUMNA_DNI);

        List<String> documentos = expediente.abrirElFiltro()
                .elFiltroDebeTenerSusCampos()
                .filtrarPorDni(dni)
                .documentosDeLaTablaCuandoTodosSean(dni);

        Assert.assertFalse(documentos.isEmpty(),
                "El filtro por DNI \"" + dni + "\" no devolvio ninguna solicitud.");
        Assert.assertTrue(documentos.stream().allMatch(dni::equals),
                "La tabla filtrada por el DNI \"" + dni + "\" muestra otros: " + documentos + ".");

        // Al buscar, la aplicacion cierra el panel: hay que abrirlo otra vez para
        // presionar Limpiar.
        expediente.abrirElFiltro().limpiarElFiltro();
        Assert.assertEquals(expediente.valorDelFiltroDeDni(), "",
                "El campo DNI del filtro no quedo vacio despues de Limpiar.");
    }

    @Test(groups = "consultas",
            description = "PF_CP_129 El detalle de la solicitud muestra sus pestanas")
    public void pfCp129DetalleDeLaSolicitud() {
        expediente.abrirElDetalleDeLaPrimeraSolicitud()
                .elDetalleDebeMostrarLaImagenDeLaTarjeta()
                .elDetalleDebeMostrar("DNI", "Firma", "Carátula", "RENAPER", "Devolver",
                        "Dictaminar");
    }
}
