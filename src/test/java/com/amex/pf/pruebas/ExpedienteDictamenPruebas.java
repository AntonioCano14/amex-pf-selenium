package com.amex.pf.pruebas;

import java.nio.file.Path;
import java.util.List;

import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.amex.pf.base.Configuracion;
import com.amex.pf.base.PruebaBase;
import com.amex.pf.paginas.PaginaExpediente;
import com.amex.pf.paginas.PaginaLogin;
import com.amex.pf.paginas.Selectores;
import com.amex.pf.utilidades.Descargas;

/**
 * Ola 6 - Dictaminacion y descargas del expediente (PF_CP_144, 145, 146) y los
 * dos casos que dependen de la carga masiva de solicitudes (PF_CP_124 y 126).
 *
 * PF_CP_144 y PF_CP_145 validan el popup de Aprobar/Denegar solicitudes y lo
 * cierran con Cancelar: la automatizacion NO cambia el estatus de solicitudes
 * reales. Confirmar el dictamen exige solicitudes desechables (ver README 7.5).
 */
public class ExpedienteDictamenPruebas extends PruebaBase {

    private PaginaExpediente expediente;

    @BeforeMethod(alwaysRun = true)
    public void abrirExpediente() {
        new PaginaLogin().iniciarSesionConCredencialesValidas().irAlMenu("Expediente");
        expediente = new PaginaExpediente().abrir();
    }

    @Test(groups = {"ola6", "expediente"},
            description = "PF_CP_144 El boton Aprobar solicitudes confirma cuantas se aprobaran")
    public void pfCp144PopupDeAprobarSolicitudes() {
        seleccionarUnaSolicitudPorDictaminar();

        String leyenda = expediente.leerElPopupDeDictamenYCancelar(
                Selectores.SOLICITUDES_BOTON_APROBAR);

        Assert.assertTrue(leyenda.toUpperCase().contains("APROBAR"),
                "El popup de Aprobar solicitudes no menciona la accion. Dice: \"" + leyenda + "\".");
        Assert.assertTrue(leyenda.contains("1"),
                "El popup no indica cuantas solicitudes se aprobaran. Dice: \"" + leyenda + "\".");
    }

    @Test(groups = {"ola6", "expediente"},
            description = "PF_CP_145 El boton Denegar solicitudes confirma cuantas se denegaran")
    public void pfCp145PopupDeDenegarSolicitudes() {
        seleccionarUnaSolicitudPorDictaminar();

        String leyenda = expediente.leerElPopupDeDictamenYCancelar(
                Selectores.SOLICITUDES_BOTON_DENEGAR);

        Assert.assertTrue(leyenda.toUpperCase().contains("DENEGAR"),
                "El popup de Denegar solicitudes no menciona la accion. Dice: \"" + leyenda + "\".");
        Assert.assertTrue(leyenda.contains("1"),
                "El popup no indica cuantas solicitudes se denegaran. Dice: \"" + leyenda + "\".");
    }

    @Test(groups = {"ola6", "expediente", "descargas"},
            description = "PF_CP_146 Doc. Griffin descarga el expediente comprimido")
    public void pfCp146DocGriffinDelDetalle() {
        Descargas.limpiar();
        filtrarPorSolicitudesDictaminables();
        expediente.abrirElDetalleDeLaPrimeraSolicitud();

        List<String> opciones = expediente.abrirElMenuDelDetalle();
        Assert.assertTrue(opciones.stream().anyMatch(opcion -> opcion.contains("Griffin")),
                "El menu del detalle no ofrece Doc. Griffin. Ofrece: " + opciones + ".");

        expediente.descargarElDocGriffinDelDetalle();
        Path zip = Descargas.esperarArchivo("zip");

        List<String> documentos = Descargas.contenidoDelZip(zip);
        for (String documento : Configuracion.lista("amex.zip.griffin.detalle")) {
            Assert.assertTrue(documentos.stream().anyMatch(nombre -> nombre.endsWith(documento)),
                    "El ZIP de Doc. Griffin no trae \"" + documento + "\". Trae: " + documentos
                            + ".");
        }
    }

    @Test(groups = {"ola6", "expediente", "regla_por_confirmar"},
            description = "PF_CP_124 La pantalla ofrece eliminar solicitudes con un layout")
    public void pfCp124EliminarSolicitudesConLayout() {
        Assert.assertTrue(expediente.hayBotonParaEliminarSolicitudes(),
                "La pantalla de Expediente no ofrece eliminar solicitudes: no existe el boton que "
                        + "pide la matriz (misma diferencia que PF_CP_125, sin boton Importar). "
                        + "Botones que ofrece hoy: CREAR SOLICITUD, Aprobar solicitudes, Denegar "
                        + "solicitudes, Exportar y Filtrar.");
    }

    @Test(groups = {"ola6", "expediente", "regla_por_confirmar"},
            description = "PF_CP_126 La pantalla permite cargar el layout de solicitudes")
    public void pfCp126CargarLayoutDeSolicitudes() {
        Assert.assertTrue(expediente.hayBotonImportar(),
                "La pantalla de Expediente no permite cargar el layout de solicitudes: no existe "
                        + "el boton Importar (misma diferencia que PF_CP_125). Sin esa pantalla no "
                        + "hay resumen de cargas correctas/con error ni descarga del detalle de "
                        + "errores.");
    }

    /**
     * Deja la tabla filtrada por el estatus dictaminable y marca la primera
     * solicitud. Si el ambiente no tiene ninguna, el caso queda OMITIDO.
     */
    private void seleccionarUnaSolicitudPorDictaminar() {
        filtrarPorSolicitudesDictaminables();
        expediente.seleccionarLaPrimeraSolicitud();
    }

    private void filtrarPorSolicitudesDictaminables() {
        String estatus = Configuracion.obtener("amex.expediente.estatus.dictaminar");
        expediente.filtrarPorEstatus(estatus);
        if (expediente.cuantasSolicitudesEncontro() == 0) {
            throw new SkipException("El ambiente no tiene solicitudes en estatus \"" + estatus
                    + "\": hacen falta solicitudes fixture para probar la dictaminacion.");
        }
    }
}
