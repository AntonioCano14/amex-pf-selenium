package com.amex.pf.pruebas;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.amex.pf.base.PruebaBase;
import com.amex.pf.datos.EstadoDelExpediente;
import com.amex.pf.paginas.PaginaExpediente;
import com.amex.pf.paginas.PaginaLogin;

/**
 * Ola 6 - Detalle de la solicitud segun su estatus (PF_CP_130 a PF_CP_141).
 *
 * Cada caso filtra la tabla por un estatus, abre el detalle de la primera
 * solicitud y verifica que las pestanas habilitadas sean las que pide la matriz.
 * El estatus y las pestanas de cada caso se declaran en configuracion.properties
 * (amex.expediente.PF_CP_XXX.*): para ajustar una regla NO hay que tocar codigo.
 *
 * Solo lectura: nunca se presiona Devolver ni Dictaminar. Si el ambiente no tiene
 * ninguna solicitud en ese estatus el caso queda OMITIDO indicando el fixture que
 * hace falta.
 */
public class ExpedienteEstadosPruebas extends PruebaBase {

    private PaginaExpediente expediente;

    @BeforeMethod(alwaysRun = true)
    public void abrirExpediente() {
        new PaginaLogin().iniciarSesionConCredencialesValidas().irAlMenu("Expediente");
        expediente = new PaginaExpediente().abrir();
    }

    @Test(groups = {"ola6", "expediente"},
            description = "PF_CP_130 Detalle de una solicitud en estatus Creado")
    public void pfCp130DetalleCreado() {
        verificarElDetalleDelEstatus("PF_CP_130");
    }

    @Test(groups = {"ola6", "expediente"},
            description = "PF_CP_131 Detalle de una solicitud en estatus Ingreso")
    public void pfCp131DetalleIngreso() {
        verificarElDetalleDelEstatus("PF_CP_131");
    }

    @Test(groups = {"ola6", "expediente"},
            description = "PF_CP_132 Detalle de una solicitud en estatus Aviso de privacidad")
    public void pfCp132DetalleAvisoDePrivacidad() {
        verificarElDetalleDelEstatus("PF_CP_132");
    }

    @Test(groups = {"ola6", "expediente", "regla_por_confirmar"},
            description = "PF_CP_133 Detalle de una solicitud en estatus Identificación oficial")
    public void pfCp133DetalleIdentificacionOficial() {
        verificarElDetalleDelEstatus("PF_CP_133");
    }

    @Test(groups = {"ola6", "expediente", "regla_por_confirmar"},
            description = "PF_CP_134 Detalle de una solicitud en estatus Validación de identidad")
    public void pfCp134DetalleValidacionDeIdentidad() {
        verificarElDetalleDelEstatus("PF_CP_134");
    }

    @Test(groups = {"ola6", "expediente", "regla_por_confirmar"},
            description = "PF_CP_135 Detalle de una solicitud en estatus Terminos y condiciones")
    public void pfCp135DetalleTerminosYCondiciones() {
        verificarElDetalleDelEstatus("PF_CP_135");
    }

    @Test(groups = {"ola6", "expediente", "regla_por_confirmar"},
            description = "PF_CP_136 Detalle de una solicitud en estatus Pendiente de firma")
    public void pfCp136DetallePendienteDeFirma() {
        verificarElDetalleDelEstatus("PF_CP_136");
    }

    @Test(groups = {"ola6", "expediente"},
            description = "PF_CP_137 Detalle de una solicitud en estatus Firmada")
    public void pfCp137DetalleFirmada() {
        verificarElDetalleDelEstatus("PF_CP_137");
    }

    @Test(groups = {"ola6", "expediente"},
            description = "PF_CP_138 Detalle de una solicitud en estatus Por dictaminar")
    public void pfCp138DetallePorDictaminar() {
        verificarElDetalleDelEstatus("PF_CP_138");
    }

    @Test(groups = {"ola6", "expediente", "regla_por_confirmar"},
            description = "PF_CP_139 Detalle de una solicitud en estatus Denegada")
    public void pfCp139DetalleDenegada() {
        verificarElDetalleDelEstatus("PF_CP_139");
    }

    @Test(groups = {"ola6", "expediente", "regla_por_confirmar"},
            description = "PF_CP_140 Detalle de una solicitud en estatus Aprobada")
    public void pfCp140DetalleAprobada() {
        verificarElDetalleDelEstatus("PF_CP_140");
    }

    @Test(groups = {"ola6", "expediente"},
            description = "PF_CP_141 Detalle de una solicitud en estatus Cancelado por datos "
                    + "erróneos")
    public void pfCp141DetalleCanceladoPorDatosErroneos() {
        verificarElDetalleDelEstatus("PF_CP_141");
    }

    /**
     * Pasos comunes de PF_CP_130 a PF_CP_141: filtrar por el estatus del caso, abrir
     * el detalle y comparar las pestanas habilitadas contra lo que pide la matriz.
     */
    private void verificarElDetalleDelEstatus(String caso) {
        EstadoDelExpediente esperado = EstadoDelExpediente.delCaso(caso);

        expediente.filtrarPorEstatus(esperado.estatus());
        if (expediente.cuantasSolicitudesEncontro() == 0) {
            throw new SkipException("El ambiente no tiene ninguna solicitud en estatus \""
                    + esperado.estatus() + "\": hace falta la solicitud fixture de ese estatus "
                    + "para ejecutar " + caso + ".");
        }
        expediente.abrirElDetalleDeLaPrimeraSolicitud();

        Assert.assertEquals(expediente.estatusDelDetalle(), esperado.estatus(),
                "El detalle no muestra el estatus por el que se filtro.");
        expediente.elDetalleDebeMostrarLaImagenDeLaTarjeta();

        Map<String, Boolean> pestanas = expediente.pestanasDelDetalle();
        Assert.assertEquals(List.copyOf(pestanas.keySet()),
                EstadoDelExpediente.todasLasPestanas(),
                "El detalle no muestra todas las pestanas esperadas. Muestra: "
                        + pestanas.keySet() + ".");

        List<String> diferencias = new ArrayList<>();
        for (Map.Entry<String, Boolean> pestana : pestanas.entrySet()) {
            boolean debeEstarHabilitada = esperado.debeEstarHabilitada(pestana.getKey());
            if (debeEstarHabilitada != pestana.getValue()) {
                diferencias.add(pestana.getKey() + ": se esperaba "
                        + (debeEstarHabilitada ? "HABILITADA" : "DESHABILITADA") + " y esta "
                        + (pestana.getValue() ? "HABILITADA" : "DESHABILITADA"));
            }
        }
        Assert.assertTrue(diferencias.isEmpty(), "En estatus \"" + esperado.estatus()
                + "\" las pestanas del detalle no coinciden con la matriz -> " + diferencias
                + ". Habilitadas segun la matriz: " + esperado.pestanasHabilitadas()
                + ". Si la regla cambio, se ajusta amex.expediente." + caso
                + ".habilitadas en configuracion.properties.");
    }
}
