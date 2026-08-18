package com.amex.pf.pruebas;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.amex.pf.base.Configuracion;
import com.amex.pf.base.PruebaBase;
import com.amex.pf.paginas.PaginaLogin;
import com.amex.pf.paginas.PaginaSolicitudes;

/**
 * Ola 6 - Adicionales PEP y solicitud condicionada a ingresos (PF_CP_121, 122
 * y 123).
 *
 * El adicional PEP se registra dentro del formulario de alta, que nunca se
 * confirma: no se presiona CREAR SOLICITUD, asi que el ambiente no cambia.
 */
public class SolicitudesPepPruebas extends PruebaBase {

    private PaginaSolicitudes solicitudes;

    @BeforeMethod(alwaysRun = true)
    public void abrirElAltaDeSolicitud() {
        new PaginaLogin().iniciarSesionConCredencialesValidas().irAlMenu("Expediente");
        solicitudes = new PaginaSolicitudes().abrirElAlta();
    }

    @Test(groups = {"ola6", "solicitudes"},
            description = "PF_CP_121 El adicional PEP se puede editar y eliminar")
    public void pfCp121EditarYEliminarAdicionalPep() {
        String prefijo = Configuracion.obtener("amex.datos.prefijo");

        solicitudes.elegirLaOpcionPep(2)
                .registrarUnAdicionalPep(prefijo, prefijo + " APELLIDO", "12345678",
                        "Cargo de prueba", "Relacion de prueba");

        Assert.assertEquals(solicitudes.cuantosAdicionalesPepHay(), 1,
                "La tabla de adicionales PEP no muestra el adicional registrado. Muestra: \""
                        + solicitudes.tablaDeAdicionalesPep() + "\".");
        Assert.assertTrue(solicitudes.elAdicionalPepTieneSusIconos(),
                "La fila del adicional PEP no muestra los dos iconos (editar y eliminar).");

        Assert.assertEquals(solicitudes.abrirLaEdicionDelAdicionalPep(), prefijo,
                "El icono de editar no abre el adicional PEP con sus datos.");
        solicitudes.cerrarElModal().eliminarElAdicionalPep();

        Assert.assertEquals(solicitudes.cuantosAdicionalesPepHay(), 0,
                "El icono de eliminar no quito el adicional PEP. La tabla muestra: \""
                        + solicitudes.tablaDeAdicionalesPep() + "\".");
    }

    @Test(groups = {"ola6", "solicitudes", "regla_por_confirmar"},
            description = "PF_CP_122 El alta de solicitud ofrece el check Condicionada a ingresos")
    public void pfCp122CheckCondicionadaAIngresos() {
        Assert.assertTrue(solicitudes.hayCheckCondicionadaAIngresos(),
                "El formulario de alta de solicitud no tiene el check \"Condicionada a ingresos\" "
                        + "que pide la matriz: la pantalla no muestra ningun check (encontrados: "
                        + solicitudes.cuantosChecksTieneElFormulario() + "). Sin ese control "
                        + "tampoco se puede automatizar PF_CP_123.");
    }
}
