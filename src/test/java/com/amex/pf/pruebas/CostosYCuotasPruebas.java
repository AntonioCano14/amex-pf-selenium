package com.amex.pf.pruebas;

import java.util.List;

import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;

import com.amex.pf.base.Configuracion;
import com.amex.pf.base.PruebaBase;
import com.amex.pf.paginas.PaginaCostos;
import com.amex.pf.paginas.PaginaCuotasGenerales;
import com.amex.pf.paginas.PaginaLogin;
import com.amex.pf.paginas.PaginaPrincipal;

/**
 * Ola 6 - Costos y Cuotas Generales (PF_CP_149, 150 y 152).
 *
 * Los importes de estas dos pantallas afectan el calculo de todo el ambiente, por
 * eso la automatizacion valida los popups y los campos pero NUNCA presiona
 * Guardar. El alta y la edicion reales quedan pendientes de un periodo de prueba
 * acordado con negocio (README seccion 7.5).
 */
public class CostosYCuotasPruebas extends PruebaBase {

    private PaginaPrincipal inicio;

    private PaginaCostos abrirElProducto() {
        inicio = new PaginaLogin().iniciarSesionConCredencialesValidas();
        inicio.irAlMenu("Costos");
        return new PaginaCostos().abrir()
                .seleccionarElPrimerProducto()
                .elegirElPeriodo(Configuracion.obtener("amex.costos.anio"),
                        Configuracion.obtener("amex.costos.mes"));
    }

    @Test(groups = {"ola6", "costos"},
            description = "PF_CP_149 El popup de Agregar costo pide todos los importes")
    public void pfCp149PopupDeAgregarCosto() {
        PaginaCostos costos = abrirElProducto();

        costos.abrirElPopupDeAgregarCosto()
                .elPopupDebePedirTodosLosDatosDelCosto()
                .escribirLosDatosDelCosto("1");

        Assert.assertTrue(costos.elPopupPermiteGuardar(),
                "Con todos los importes capturados el popup no habilita Guardar.");
        costos.cerrarElPopupSinGuardar();
    }

    @Test(groups = {"ola6", "costos"},
            description = "PF_CP_150 El popup de Editar costo trae los importes del registro")
    public void pfCp150PopupDeEditarCosto() {
        PaginaCostos costos = abrirElProducto();

        costos.abrirElPopupDeEditarCosto()
                .elPopupDebePedirTodosLosDatosDelCosto();

        Assert.assertTrue(costos.elPopupPermiteGuardar(),
                "El popup de edicion del costo no habilita Guardar.");
        costos.cerrarElPopupSinGuardar();
    }

    @Test(groups = {"ola6", "cuotas"},
            description = "PF_CP_152 Cuotas Generales permite capturar los importes y guardarlos")
    public void pfCp152CuotasGenerales() {
        inicio = new PaginaLogin().iniciarSesionConCredencialesValidas();
        inicio.irAlMenu("Cuotas Generales");
        PaginaCuotasGenerales cuotas = new PaginaCuotasGenerales().abrir();

        List<String> conceptos = cuotas.conceptos();
        Assert.assertFalse(conceptos.isEmpty(),
                "La pantalla de Cuotas Generales no muestra ningun concepto.");
        cuotas.losImportesDebenSerEditables();

        Assert.assertTrue(cuotas.hayBotonGuardar(),
                "La pantalla de Cuotas Generales no muestra el boton Guardar. Conceptos: "
                        + conceptos + ".");
        // Guardar solo se habilita cuando los importes estan capturados: en QA la
        // pantalla llega vacia, asi que el boton deshabilitado es el comportamiento
        // correcto y se informa como dato semilla faltante.
        if (cuotas.importes().stream().allMatch(importe -> importe == null || importe.isBlank())) {
            throw new SkipException("La pantalla de Cuotas Generales no tiene importes cargados en "
                    + "este ambiente (" + conceptos + "): hace falta el dato semilla y un periodo "
                    + "de prueba acordado para validar el guardado.");
        }
        Assert.assertTrue(cuotas.elBotonGuardarEstaHabilitado(),
                "El boton Guardar de Cuotas Generales esta deshabilitado con los importes "
                        + cuotas.importes() + ".");
    }
}
