package com.amex.pf.paginas;

import org.openqa.selenium.By;

/**
 * UNICO lugar donde viven los selectores.
 *
 * Si la aplicacion cambia un boton, un campo o un texto, se corrige AQUI y todas
 * las pruebas que lo usan quedan arregladas. Nunca escriba un By dentro de una
 * clase de pruebas.
 *
 * La aplicacion no tiene atributos data-testid, por eso se usan:
 * - formcontrolname (estable, lo define el formulario de Angular)
 * - texto visible
 * - clases estables (flat-button)
 * No se usan los id autogenerados (mat-input-0), porque cambian de orden.
 */
public final class Selectores {

    private Selectores() {
    }

    // ------------------------------------------------------------------ Login
    public static final By CAMPO_USUARIO = By.cssSelector("input[formcontrolname='user']");
    public static final By CAMPO_CONTRASENA = By.cssSelector("input[formcontrolname='password']");
    public static final By BOTON_INICIAR_SESION =
            By.xpath("//button[contains(., 'INICIAR SESIÓN')]");
    /** El contenedor app-show-errors tiene altura 0: el texto esta en div.error. */
    public static final By ERRORES_DE_CAMPO = By.cssSelector("app-show-errors div.error");

    // ----------------------------------------------------------------- Modales
    public static final By MODAL = By.cssSelector("mat-dialog-container");
    public static final By MODAL_BOTON_ACEPTAR =
            By.xpath("//mat-dialog-container//button[contains(., 'Aceptar')]");

    // ------------------------------------------------------- Encabezado y menu
    public static final By SALUDO_USUARIO = By.xpath("//*[contains(text(), 'Hola,')]");
    public static final By MENU_USUARIO = By.cssSelector("button.mat-menu-trigger");
    public static final By OPCION_SALIR = By.xpath("//button[contains(., 'Salir')]");

    public static By menuPrincipal(String nombre) {
        return By.xpath("//button[contains(@class, 'flat-button')][contains(., '" + nombre + "')]");
    }

    // -------------------------------------------------------- Pantallas comunes
    public static final By TABLA = By.tagName("table");
    public static final By FILAS_DE_TABLA = By.cssSelector("table tr");
    public static final By GRAFICA = By.cssSelector("canvas, svg");

    public static By textoVisible(String texto) {
        return By.xpath("//*[contains(text(), '" + texto + "')]");
    }

    public static By boton(String etiqueta) {
        return By.xpath("//button[contains(., '" + etiqueta + "')]");
    }

    // ------------------------------------------------------------- Solicitudes
    public static final By SOLICITUDES_BOTON_CREAR = boton("CREAR SOLICITUD");
    public static final By SOLICITUDES_CAMPO_NOMBRE = By.cssSelector("input[formcontrolname='name']");
    public static final By SOLICITUDES_CAMPO_APELLIDOS =
            By.cssSelector("input[formcontrolname='lastName']");
    public static final By SOLICITUDES_CAMPO_DNI = By.cssSelector("input[formcontrolname='dni']");
    public static final By SOLICITUDES_CAMPO_CUIL = By.cssSelector("input[formcontrolname='cuil']");

    // --------------------------------------------------------------- Catalogos
    public static final By CATALOGO_LISTA =
            By.xpath("//mat-form-field[contains(., 'Seleccionar catálogo')]//mat-select");
    public static final By CATALOGO_BOTON_AGREGAR = boton("AGREGAR ELEMENTO");

    public static final By OPCIONES_DE_LISTA = By.tagName("mat-option");

    public static By opcionDeLista(String nombre) {
        return By.xpath("//mat-option[contains(., '" + nombre + "')]");
    }
}
