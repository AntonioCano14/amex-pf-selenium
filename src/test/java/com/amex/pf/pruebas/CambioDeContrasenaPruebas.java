package com.amex.pf.pruebas;

import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.amex.pf.base.Configuracion;
import com.amex.pf.base.PruebaBase;
import com.amex.pf.paginas.PaginaCambioDeContrasena;
import com.amex.pf.paginas.PaginaLogin;
import com.amex.pf.paginas.Selectores;

/**
 * Pantalla "Cambio de contraseña" (menu Hola, [usuario] -> Cambiar contraseña).
 *
 * Cubre PF_CP_005, PF_CP_006 y PF_CP_007 mas los casos que la matriz V2 agrego
 * sin ID; a esos se les asigno CAM_001 a CAM_007 para poder identificarlos en la
 * consola y en la matriz.
 *
 * NINGUN caso presiona GUARDAR: la contrasena de la cuenta con la que se ejecuta
 * no cambia (si cambiara, el resto de la suite ya no podria iniciar sesion). El
 * ultimo caso (CAM_007) llega hasta verificar que GUARDAR se habilita y sale con
 * CANCELAR, que es el alcance acordado con QA.
 */
public class CambioDeContrasenaPruebas extends PruebaBase {

    /** Contrasena de ejemplo para el campo "Contraseña actual": no se guarda nada. */
    private static final String ACTUAL_DE_EJEMPLO = "ContrasenaDeEjemplo1#";

    private PaginaCambioDeContrasena pantalla;

    @BeforeMethod(alwaysRun = true)
    public void iniciarSesion() {
        new PaginaLogin().iniciarSesionConCredencialesValidas();
        pantalla = new PaginaCambioDeContrasena();
    }

    /** Sale de la pantalla sin guardar, incluso si el caso fallo. */
    @AfterMethod(alwaysRun = true)
    public void salirSinGuardar() {
        if (pantalla != null) {
            pantalla.cancelarSiSigueEnLaPantalla();
        }
    }

    @Test(groups = {"cambio_contrasena", "login"},
            description = "PF_CP_005 El menu del usuario muestra Cambiar contraseña y Salir con "
                    + "sus iconos")
    public void pfCp005OpcionesDelMenuDelUsuario() {
        pantalla.abrirElMenuDelUsuario()
                .elMenuDebeMostrarLaOpcion("Cambiar contraseña")
                .elMenuDebeMostrarLaOpcion("Salir")
                .laOpcionDebeTraerIcono("Cambiar contraseña")
                .laOpcionDebeTraerIcono("Salir");
    }

    @Test(groups = {"cambio_contrasena", "login"},
            description = "PF_CP_006 La pantalla de cambio de contraseña muestra titulo, "
                    + "requisitos, los tres campos y los botones")
    public void pfCp006PantallaDeCambioDeContrasena() {
        pantalla.abrir()
                .elTituloDebeSer(PaginaCambioDeContrasena.TITULO)
                .debenMostrarseLosRequisitos(
                        Configuracion.listaSeparadaPorBarra("amex.contrasena.requisitos"))
                .losCamposDebenEstarVisibles();
        Assert.assertTrue(pantalla.elBotonCancelarEstaVisible(), "Falta el boton CANCELAR.");
        Assert.assertFalse(pantalla.elBotonGuardarEstaHabilitado(),
                "Con los campos vacios GUARDAR debe estar deshabilitado.");
    }

    /**
     * PF_CP_007 y CAM_005: los requisitos son los mismos para "Nueva contraseña" y
     * para "Confirmar contraseña". El caso escribe contrasenas que NO cumplen y
     * exige que el campo se marque en rojo y que GUARDAR siga deshabilitado.
     */
    @Test(groups = {"cambio_contrasena", "login"}, dataProvider = "camposDeContrasenaNueva",
            description = "PF_CP_007/CAM_005 Requisitos de la contrasena en los campos Nueva "
                    + "contraseña y Confirmar contraseña")
    public void requisitosDeLaContrasena(String id, By campo, String etiqueta) {
        pantalla.abrir()
                .debenMostrarseLosRequisitos(
                        Configuracion.listaSeparadaPorBarra("amex.contrasena.requisitos"));
        pantalla.escribirEnElCampo(Selectores.CONTRASENA_ACTUAL, ACTUAL_DE_EJEMPLO);
        for (String invalida : Configuracion.lista("amex.contrasena.invalidas")) {
            pantalla.escribirEnElCampo(campo, invalida);
            Assert.assertTrue(pantalla.elCampoSeMarcoEnRojo(campo),
                    "El campo " + etiqueta + " no se marco en rojo con \"" + invalida
                            + "\", que no cumple los requisitos.");
            Assert.assertFalse(pantalla.elBotonGuardarEstaHabilitado(),
                    "GUARDAR no debe habilitarse con \"" + invalida + "\" en " + etiqueta + ".");
        }
    }

    @DataProvider(name = "camposDeContrasenaNueva")
    public Object[][] camposDeContrasenaNueva() {
        return new Object[][] {
            {"PF_CP_007", Selectores.CONTRASENA_NUEVA, "Nueva contraseña"},
            {"CAM_005", Selectores.CONTRASENA_CONFIRMAR, "Confirmar contraseña"},
        };
    }

    @Test(groups = {"cambio_contrasena", "login"},
            description = "CAM_001 Los tres campos vacios se pintan de rojo y GUARDAR sigue "
                    + "deshabilitado")
    public void cam001CamposVacios() {
        pantalla.abrir();
        for (By campo : new By[] {Selectores.CONTRASENA_ACTUAL, Selectores.CONTRASENA_NUEVA,
                Selectores.CONTRASENA_CONFIRMAR}) {
            pantalla.tocarYDejarVacio(campo);
            Assert.assertTrue(pantalla.elCampoSeMarcoEnRojo(campo),
                    "El campo quedo vacio y no se marco en rojo.");
        }
        Assert.assertFalse(pantalla.elBotonGuardarEstaHabilitado(),
                "Con los tres campos vacios GUARDAR debe estar deshabilitado.");
    }

    /** CAM_002, CAM_003 y CAM_004: el ojo muestra y vuelve a ocultar la contrasena. */
    @Test(groups = {"cambio_contrasena", "login"}, dataProvider = "camposConOjo",
            description = "CAM_002/003/004 Ver u ocultar la contrasena con el simbolo de ojo")
    public void verUOcultarLaContrasena(String id, By campo, String etiqueta) {
        pantalla.abrir().escribirEnElCampo(campo,
                Configuracion.obtener("amex.contrasena.valida"));
        Assert.assertEquals(pantalla.comoSeMuestraElCampo(campo), "password",
                "El campo " + etiqueta + " debe venir oculto.");
        pantalla.presionarElOjo(campo);
        Assert.assertEquals(pantalla.comoSeMuestraElCampo(campo), "text",
                "Al presionar el ojo la contrasena de " + etiqueta + " debe verse.");
        pantalla.presionarElOjo(campo);
        Assert.assertEquals(pantalla.comoSeMuestraElCampo(campo), "password",
                "Al presionar el ojo otra vez la contrasena de " + etiqueta + " debe ocultarse.");
    }

    @DataProvider(name = "camposConOjo")
    public Object[][] camposConOjo() {
        return new Object[][] {
            {"CAM_002", Selectores.CONTRASENA_ACTUAL, "Contraseña actual"},
            {"CAM_003", Selectores.CONTRASENA_NUEVA, "Nueva contraseña"},
            {"CAM_004", Selectores.CONTRASENA_CONFIRMAR, "Confirmar contraseña"},
        };
    }

    @Test(groups = {"cambio_contrasena", "login"},
            description = "CAM_006 Confirmar contraseña distinta a Nueva contraseña: campo en "
                    + "rojo y GUARDAR deshabilitado")
    public void cam006ConfirmacionDistinta() {
        String valida = Configuracion.obtener("amex.contrasena.valida");
        pantalla.abrir()
                .escribirEnElCampo(Selectores.CONTRASENA_ACTUAL, ACTUAL_DE_EJEMPLO)
                .escribirEnElCampo(Selectores.CONTRASENA_NUEVA, valida)
                .escribirEnElCampo(Selectores.CONTRASENA_CONFIRMAR, otraContrasenaValida(valida));
        Assert.assertTrue(pantalla.elCampoSeMarcoEnRojo(Selectores.CONTRASENA_CONFIRMAR),
                "Confirmar contraseña debe marcarse en rojo cuando no coincide.");
        Assert.assertFalse(pantalla.elBotonGuardarEstaHabilitado(),
                "GUARDAR no debe habilitarse si las contrasenas no coinciden.");
    }

    /**
     * CAM_007: con los tres campos llenos y validos GUARDAR se habilita. El caso
     * NO lo presiona (sale con CANCELAR): presionarlo cambiaria la contrasena de la
     * cuenta de ejecucion y dejaria a la suite sin poder iniciar sesion.
     */
    @Test(groups = {"cambio_contrasena", "login"},
            description = "CAM_007 Campos llenos y validos: GUARDAR se habilita (no se presiona)")
    public void cam007CamposLlenosYValidos() {
        String valida = Configuracion.obtener("amex.contrasena.valida");
        pantalla.abrir()
                .escribirEnElCampo(Selectores.CONTRASENA_ACTUAL, ACTUAL_DE_EJEMPLO)
                .escribirEnElCampo(Selectores.CONTRASENA_NUEVA, valida)
                .escribirEnElCampo(Selectores.CONTRASENA_CONFIRMAR, valida);
        Assert.assertFalse(pantalla.elCampoSeMarcoEnRojo(Selectores.CONTRASENA_NUEVA),
                "La contrasena de amex.contrasena.valida no cumple los requisitos de hoy.");
        Assert.assertTrue(pantalla.elBotonGuardarEstaHabilitado(),
                "Con los tres campos validos e iguales GUARDAR debe habilitarse.");
    }

    /** Otra contrasena valida distinta de la primera, para el caso de no coincidencia. */
    private String otraContrasenaValida(String valida) {
        return valida.replace('2', '3');
    }
}
