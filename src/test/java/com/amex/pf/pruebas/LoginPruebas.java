package com.amex.pf.pruebas;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.amex.pf.base.Configuracion;
import com.amex.pf.base.PruebaBase;
import com.amex.pf.paginas.PaginaLogin;
import com.amex.pf.paginas.PaginaPrincipal;

/**
 * Modulo "Expediente Login" de la matriz AMEX PF V2.
 * Todos los casos son de solo lectura: no modifican informacion.
 */
public class LoginPruebas extends PruebaBase {

    private static final String USUARIO_INEXISTENTE = "qa.usuario.inexistente@na-at.com";
    private static final String CONTRASENA_INCORRECTA = "ContrasenaIncorrecta123!";

    @Test(groups = {"login", "humo"},
            description = "PF_CP_001 Usuario correcto y contrasena incorrecta")
    public void pfCp001UsuarioCorrectoContrasenaIncorrecta() {
        PaginaLogin login = new PaginaLogin();
        login.iniciarSesionCon(Configuracion.usuario(), CONTRASENA_INCORRECTA);
        Assert.assertTrue(login.textoDelModal().contains(PaginaLogin.TEXTO_CREDENCIALES_INVALIDAS),
                "Se esperaba el mensaje \"" + PaginaLogin.TEXTO_CREDENCIALES_INVALIDAS + "\".");
        login.aceptarModal();
        Assert.assertTrue(login.sigueEnLaPantallaDeLogin(), "No debio ingresar a la aplicacion.");
    }

    @Test(groups = "login", description = "PF_CP_002 Usuario incorrecto y contrasena correcta")
    public void pfCp002UsuarioIncorrectoContrasenaCorrecta() {
        PaginaLogin login = new PaginaLogin();
        login.iniciarSesionCon(USUARIO_INEXISTENTE, Configuracion.contrasena());
        Assert.assertTrue(login.textoDelModal().contains(PaginaLogin.TEXTO_CREDENCIALES_INVALIDAS),
                "El mensaje no debe revelar si el usuario existe.");
        login.aceptarModal();
        Assert.assertTrue(login.sigueEnLaPantallaDeLogin(), "No debio ingresar a la aplicacion.");
    }

    @Test(groups = "login", description = "PF_CP_003 Usuario y contrasena incorrectos")
    public void pfCp003UsuarioYContrasenaIncorrectos() {
        PaginaLogin login = new PaginaLogin();
        login.iniciarSesionCon(USUARIO_INEXISTENTE, CONTRASENA_INCORRECTA);
        Assert.assertTrue(login.textoDelModal().contains(PaginaLogin.TEXTO_CREDENCIALES_INVALIDAS),
                "Se esperaba el mensaje \"" + PaginaLogin.TEXTO_CREDENCIALES_INVALIDAS + "\".");
        login.aceptarModal();
        Assert.assertTrue(login.sigueEnLaPantallaDeLogin(), "No debio ingresar a la aplicacion.");
    }

    @Test(groups = {"login", "humo"},
            description = "PF_CP_004 Usuario y contrasena correctos")
    public void pfCp004UsuarioYContrasenaCorrectos() {
        PaginaPrincipal inicio = new PaginaLogin().iniciarSesionConCredencialesValidas();
        inicio.debeVerseElTexto("Hola,");
    }

    @Test(groups = "login",
            description = "VAL_001 Ambos campos vacios: mensaje de obligatorio y boton deshabilitado")
    public void val001AmbosCamposVacios() {
        PaginaLogin login = new PaginaLogin();
        login.tocarLosDosCamposYSalir();
        Assert.assertTrue(login.seMuestraElMensaje(PaginaLogin.TEXTO_CAMPO_OBLIGATORIO),
                "Falta el mensaje \"" + PaginaLogin.TEXTO_CAMPO_OBLIGATORIO + "\".");
        Assert.assertFalse(login.botonIniciarSesionHabilitado(),
                "Con los campos vacios el boton debe estar deshabilitado.");
    }

    @Test(groups = "login", description = "VAL_002 Solo el usuario capturado")
    public void val002SoloUsuario() {
        PaginaLogin login = new PaginaLogin();
        login.escribirUsuario(Configuracion.usuario());
        Assert.assertFalse(login.botonIniciarSesionHabilitado(),
                "Sin contrasena el boton debe seguir deshabilitado.");
    }

    @Test(groups = "login", description = "VAL_003 Solo la contrasena capturada")
    public void val003SoloContrasena() {
        PaginaLogin login = new PaginaLogin();
        login.escribirContrasena("Cualquiera123");
        Assert.assertFalse(login.botonIniciarSesionHabilitado(),
                "Sin usuario el boton debe seguir deshabilitado.");
    }

    @Test(groups = "login", description = "VAL_004 Formato de correo invalido")
    public void val004FormatoDeCorreoInvalido() {
        PaginaLogin login = new PaginaLogin();
        login.escribirUsuario("correo-sin-arroba");
        login.escribirContrasena("Cualquiera123");
        Assert.assertTrue(login.seMuestraElMensaje(PaginaLogin.TEXTO_CORREO_INVALIDO),
                "Falta el mensaje \"" + PaginaLogin.TEXTO_CORREO_INVALIDO + "\".");
        Assert.assertFalse(login.botonIniciarSesionHabilitado(),
                "Con un correo invalido el boton debe estar deshabilitado.");
    }

    /**
     * DEFECTO ABIERTO (DEF-01): el validador rechaza correos validos con "+".
     * Se deja en el grupo "defecto_conocido" para poder excluirlo de la regresion
     * sin borrarlo: cuando desarrollo lo corrija, este caso empezara a pasar.
     */
    @Test(groups = {"login", "defecto_conocido"},
            description = "DEF_01 El correo con el signo + debe ser aceptado")
    public void def01CorreoConSignoMas() {
        PaginaLogin login = new PaginaLogin();
        login.escribirUsuario("qa.prueba+amex@na-at.com");
        login.escribirContrasena("Cualquiera123");
        Assert.assertFalse(login.seMuestraElMensaje(PaginaLogin.TEXTO_CORREO_INVALIDO),
                "DEF-01: se rechaza un correo valido que contiene \"+\".");
        Assert.assertTrue(login.botonIniciarSesionHabilitado(),
                "DEF-01: el boton queda deshabilitado con un correo valido.");
    }
}
