package com.amex.pf.paginas;

import java.util.List;

import org.openqa.selenium.WebElement;
import org.testng.Assert;

import com.amex.pf.base.Configuracion;

/** Pantalla de inicio de sesion (modulo "Expediente Login" de la matriz). */
public class PaginaLogin extends PaginaBase {

    public static final String TEXTO_CREDENCIALES_INVALIDAS = "Credenciales inválidas";
    public static final String TEXTO_CORREO_INVALIDO = "Introduce un correo válido";
    public static final String TEXTO_CAMPO_OBLIGATORIO = "El campo es requerido";
    public static final String TEXTO_SESION_ACTIVA = "sesión activa";

    public PaginaLogin escribirUsuario(String usuario) {
        escribir(Selectores.CAMPO_USUARIO, usuario);
        return this;
    }

    public PaginaLogin escribirContrasena(String contrasena) {
        escribir(Selectores.CAMPO_CONTRASENA, contrasena);
        return this;
    }

    public PaginaLogin clicIniciarSesion() {
        hacerClic(Selectores.BOTON_INICIAR_SESION);
        return this;
    }

    public PaginaLogin iniciarSesionCon(String usuario, String contrasena) {
        escribirUsuario(usuario);
        escribirContrasena(contrasena);
        return clicIniciarSesion();
    }

    /** Inicia sesion con el usuario configurado y espera llegar al inicio. */
    public PaginaPrincipal iniciarSesionConCredencialesValidas() {
        iniciarSesionCon(Configuracion.usuario(), Configuracion.contrasena());
        if (!estaVisible(Selectores.SALUDO_USUARIO, Configuracion.esperaMaximaSegundos())) {
            explicarPorQueNoSePudoIngresar();
        }
        esperarQueLaUrlContenga("expedient/home");
        return new PaginaPrincipal();
    }

    /**
     * Entra y sale de los dos campos sin escribir: asi Angular los marca como
     * "tocados" y muestra el mensaje de campo obligatorio.
     */
    public PaginaLogin tocarLosDosCamposYSalir() {
        verVisible(Selectores.CAMPO_USUARIO).click();
        verVisible(Selectores.CAMPO_CONTRASENA).click();
        navegador().findElement(org.openqa.selenium.By.tagName("body")).click();
        return this;
    }

    /** Mensaje del modal (por ejemplo "Credenciales inválidas"). */
    public String textoDelModal() {
        return verVisible(Selectores.MODAL).getText();
    }

    public void aceptarModal() {
        hacerClic(Selectores.MODAL_BOTON_ACEPTAR);
    }

    public boolean botonIniciarSesionHabilitado() {
        return verVisible(Selectores.BOTON_INICIAR_SESION).isEnabled();
    }

    public boolean seMuestraElMensaje(String mensaje) {
        List<WebElement> errores = buscarTodos(Selectores.ERRORES_DE_CAMPO);
        return errores.stream().anyMatch(error -> error.getText().contains(mensaje));
    }

    public boolean sigueEnLaPantallaDeLogin() {
        return navegador().getCurrentUrl().contains("#/login");
    }

    /** Cierra sesion por el menu del usuario. */
    public void cerrarSesion() {
        try {
            cerrarModalSiEstaAbierto();
            cerrarOverlayConEscape();
        } catch (RuntimeException sinCuerpo) {
            // la pantalla ya no responde: se intenta el clic de todos modos
        }
        hacerClic(Selectores.MENU_USUARIO);
        hacerClic(Selectores.OPCION_SALIR);
        espera().until(navegador -> navegador.getCurrentUrl().contains("#/login"));
    }

    /**
     * Se llama al terminar cada caso: si quedo una sesion abierta la cierra, para
     * no dejar al usuario bloqueado (la aplicacion permite una sola sesion).
     */
    public boolean cerrarSesionSiHayAlguna() {
        try {
            if (!estaVisible(Selectores.SALUDO_USUARIO, 2)) {
                return true; // no habia sesion abierta
            }
            cerrarSesion();
            return true;
        } catch (RuntimeException noSePudoCerrar) {
            System.err.println("AVISO: no se pudo cerrar la sesion desde la pantalla, "
                    + "se intentara por API: " + noSePudoCerrar.getMessage());
            return false;
        }
    }

    private void explicarPorQueNoSePudoIngresar() {
        if (estaVisible(Selectores.MODAL, 3)) {
            String texto = textoDelModal();
            if (texto.contains(TEXTO_SESION_ACTIVA)) {
                Assert.fail("El usuario " + Configuracion.usuario() + " ya tiene una sesion abierta "
                        + "(la aplicacion permite una sola sesion por usuario). Cierrela o use "
                        + "otro usuario para esta ejecucion.");
            }
            Assert.fail("No se pudo ingresar. La aplicacion mostro: " + texto);
        }
        Assert.fail("No se pudo ingresar y la aplicacion no mostro ningun mensaje.");
    }
}
