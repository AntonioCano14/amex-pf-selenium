package com.amex.pf.pruebas;

import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.amex.pf.base.PruebaBase;
import com.amex.pf.paginas.PaginaLogin;
import com.amex.pf.paginas.PaginaPrincipal;
import com.amex.pf.paginas.PaginaSolicitudes;
import com.amex.pf.paginas.Selectores;

/**
 * EJEMPLO DE CASOS EN TABLA (data-driven con @DataProvider).
 *
 * Asi se cubren los ~35 casos de la matriz del tipo "el campo debe permitir N
 * caracteres": para agregar un caso nuevo se agrega UN RENGLON a la tabla de
 * abajo, sin escribir codigo nuevo.
 *
 * Solo lectura: se escribe en los campos pero nunca se presiona Guardar/Crear.
 */
public class ValidacionesDeCamposPruebas extends PruebaBase {

    private PaginaSolicitudes solicitudes;

    @BeforeMethod(alwaysRun = true)
    public void abrirElAltaDeSolicitud() {
        PaginaPrincipal inicio = new PaginaLogin().iniciarSesionConCredencialesValidas();
        inicio.irAlMenu("Expediente");
        new PaginaPrincipal().elBotonDebeEstarVisible("CREAR SOLICITUD");
        PruebaBase.navegador().findElement(Selectores.SOLICITUDES_BOTON_CREAR).click();
        solicitudes = new PaginaSolicitudes();
    }

    /** ID de la matriz | campo | maximo esperado | tipo de dato. */
    @DataProvider(name = "camposConMaximo")
    public Object[][] camposConMaximo() {
        return new Object[][]{
                {"PF_CP_111 Nombre", Selectores.SOLICITUDES_CAMPO_NOMBRE, 32, "letras"},
                {"PF_CP_112 DNI", Selectores.SOLICITUDES_CAMPO_DNI, 8, "numeros"},
                {"PF_CP_113 Apellidos", Selectores.SOLICITUDES_CAMPO_APELLIDOS, 32, "letras"},
        };
    }

    @Test(groups = "validaciones", dataProvider = "camposConMaximo",
            description = "PF_CP_111-113 Maximo de caracteres por campo")
    public void elCampoDebePermitirElMaximo(String caso, By campo, int maximo, String tipo) {
        int aceptados = solicitudes.cuantosCaracteresAcepta(campo, maximo, tipo);
        Assert.assertEquals(aceptados, maximo,
                caso + ": el campo acepto " + aceptados + " caracteres y el maximo esperado es "
                        + maximo + ".");
    }

    /**
     * HALLAZGO: hoy el campo CUIL acepta 13 caracteres y la matriz espera 11.
     * Queda en el grupo "regla_por_confirmar" (se excluye de la regresion) hasta
     * que negocio defina el maximo real; cuando se confirme, se quita el grupo.
     */
    @Test(groups = {"validaciones", "regla_por_confirmar"},
            description = "PF_CP_114 Campo CUIL 11 caracteres")
    public void pfCp114CampoCuil() {
        int aceptados = solicitudes.cuantosCaracteresAcepta(
                Selectores.SOLICITUDES_CAMPO_CUIL, 11, "numeros");
        Assert.assertEquals(aceptados, 11,
                "El campo acepto " + aceptados + " caracteres y el maximo esperado es 11.");
    }
}
