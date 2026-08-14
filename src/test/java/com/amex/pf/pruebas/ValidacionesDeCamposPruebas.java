package com.amex.pf.pruebas;

import java.util.List;

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
 * OLA 2 - Validaciones de campo del formulario "Crear solicitud" de la pantalla
 * Expediente (PF_CP_111 a PF_CP_120).
 *
 * EJEMPLO DE CASOS EN TABLA (data-driven con @DataProvider): para agregar un caso
 * de longitud maxima o de tipo de caracter se agrega UN RENGLON a la tabla
 * correspondiente, sin escribir codigo nuevo.
 *
 * Solo lectura: se escribe en los campos pero nunca se presiona CREAR SOLICITUD.
 */
public class ValidacionesDeCamposPruebas extends PruebaBase {

    private PaginaSolicitudes solicitudes;

    @BeforeMethod(alwaysRun = true)
    public void abrirElAltaDeSolicitud() {
        PaginaPrincipal inicio = new PaginaLogin().iniciarSesionConCredencialesValidas();
        inicio.irAlMenu("Expediente");
        inicio.elBotonDebeEstarVisible("CREAR SOLICITUD");
        solicitudes = new PaginaSolicitudes().abrirElAlta();
    }

    /** ID de la matriz | campo | maximo esperado | tipo de dato. */
    @DataProvider(name = "camposConMaximo")
    public Object[][] camposConMaximo() {
        return new Object[][]{
                {"PF_CP_111 Nombre", Selectores.SOLICITUDES_CAMPO_NOMBRE, 32, "letras"},
                {"PF_CP_112 DNI", Selectores.SOLICITUDES_CAMPO_DNI, 8, "numeros"},
                {"PF_CP_113 Apellidos", Selectores.SOLICITUDES_CAMPO_APELLIDOS, 32, "letras"},
                {"PF_CP_117 Direccion", Selectores.SOLICITUDES_CAMPO_DIRECCION, 100, "letras"},
        };
    }

    @Test(groups = "validaciones", dataProvider = "camposConMaximo",
            description = "PF_CP_111-117 Maximo de caracteres por campo")
    public void elCampoDebePermitirElMaximo(String caso, By campo, int maximo, String tipo) {
        int aceptados = solicitudes.cuantosCaracteresAcepta(campo, maximo, tipo);
        Assert.assertEquals(aceptados, maximo,
                caso + ": el campo acepto " + aceptados + " caracteres y el maximo esperado es "
                        + maximo + ".");
    }

    /** ID de la matriz | campo | texto que se escribe | lo que debe quedar. */
    @DataProvider(name = "camposQueFiltranCaracteres")
    public Object[][] camposQueFiltranCaracteres() {
        return new Object[][]{
                {"PF_CP_111 Nombre solo alfabetico",
                        Selectores.SOLICITUDES_CAMPO_NOMBRE, "Juan123!@#", "Juan"},
                {"PF_CP_113 Apellidos solo alfabetico",
                        Selectores.SOLICITUDES_CAMPO_APELLIDOS, "Perez99$", "Perez"},
                {"PF_CP_112 DNI solo numerico",
                        Selectores.SOLICITUDES_CAMPO_DNI, "ab1234cd56", "123456"},
                {"PF_CP_115 Fecha de nacimiento solo numerica",
                        Selectores.SOLICITUDES_CAMPO_FECHA_NACIMIENTO, "aa/bb/cccc", ""},
                {"PF_CP_117 Direccion alfanumerica",
                        Selectores.SOLICITUDES_CAMPO_DIRECCION, "Av Corrientes 1234",
                        "Av Corrientes 1234"},
        };
    }

    @Test(groups = "validaciones", dataProvider = "camposQueFiltranCaracteres",
            description = "PF_CP_111-117 Tipo de caracteres permitidos por campo")
    public void elCampoSoloDebePermitirSuTipoDeCaracter(
            String caso, By campo, String seEscribe, String debeQuedar) {
        String quedo = solicitudes.loQueAcepta(campo, seEscribe);
        Assert.assertEquals(quedo, debeQuedar,
                caso + ": se escribio \"" + seEscribe + "\" y el campo dejo \"" + quedo + "\".");
    }

    /**
     * El campo muestra el CUIL con mascara (20-12345678-9), por eso el maximo de la
     * matriz se valida contando solo los digitos: 13 caracteres visibles = 11 digitos.
     */
    @Test(groups = "validaciones", description = "PF_CP_114 Campo CUIL 11 digitos")
    public void pfCp114CampoCuil() {
        int digitos = solicitudes.cuantosDigitosAcepta(Selectores.SOLICITUDES_CAMPO_CUIL, 11);
        Assert.assertEquals(digitos, 11,
                "El campo acepto " + digitos + " digitos (se ve como \""
                        + solicitudes.valorDelCampo(Selectores.SOLICITUDES_CAMPO_CUIL)
                        + "\") y el maximo esperado es 11.");
    }

    @Test(groups = "validaciones",
            description = "PF_CP_115-116 Fecha de nacimiento con formato DD/MM/AAAA y calendario")
    public void pfCp115CampoFechaDeNacimiento() {
        String quedo = solicitudes.loQueAcepta(
                Selectores.SOLICITUDES_CAMPO_FECHA_NACIMIENTO, "31/12/1990");
        Assert.assertEquals(quedo, "31/12/1990",
                "El campo no acepto una fecha valida de 8 digitos: dejo \"" + quedo + "\".");
        Assert.assertTrue(solicitudes.hayCalendarioDeFechaDeNacimiento(),
                "No se mostro el calendario del campo Fecha de nacimiento.");
    }

    @Test(groups = "validaciones",
            description = "PF_CP_118 Adicionales PEP muestra dos opciones y ninguna preseleccionada")
    public void pfCp118OpcionesDeAdicionalesPep() {
        List<String> opciones = solicitudes.opcionesPep();
        Assert.assertEquals(opciones.size(), 2,
                "La seccion Adicionales PEP debe mostrar dos opciones y muestra: " + opciones);
        Assert.assertFalse(solicitudes.hayAlgunaOpcionPepMarcada(),
                "Ninguna opcion de Adicionales PEP debe venir seleccionada.");
    }

    @Test(groups = "validaciones",
            description = "PF_CP_119 Con adicionales habilita la seccion para agregar un PEP")
    public void pfCp119SeccionDeAdicionales() {
        solicitudes.elegirLaOpcionPep(2);
        new PaginaPrincipal().elBotonDebeEstarVisible("AGREGAR ADICIONAL");
    }

    @Test(groups = "validaciones",
            description = "PF_CP_120 El modal de PEP pide sus cinco datos y son obligatorios")
    public void pfCp120ModalDeAdicionalPep() {
        solicitudes.elegirLaOpcionPep(2).abrirElModalDePep();
        By aceptar = Selectores.botonDelModal("ACEPTAR");
        Assert.assertTrue(solicitudes.elBotonEstaDeshabilitado(aceptar),
                "El boton ACEPTAR debe estar deshabilitado con el modal vacio.");

        solicitudes.loQueAcepta(Selectores.campoDelModal("name"), "Ana");
        solicitudes.loQueAcepta(Selectores.campoDelModal("lastName"), "Gomez");
        solicitudes.loQueAcepta(Selectores.campoDelModal("dni"), "12345678");
        solicitudes.loQueAcepta(Selectores.campoDelModal("position"), "Directora");
        solicitudes.loQueAcepta(Selectores.campoDelModal("relationship"), "Conyuge");

        Assert.assertFalse(solicitudes.elBotonEstaDeshabilitado(aceptar),
                "Con los cinco datos completos el boton ACEPTAR debe habilitarse.");
        // Se cancela: la solicitud nunca se crea, no se guarda informacion.
        solicitudes.cerrarElModal();
    }
}
