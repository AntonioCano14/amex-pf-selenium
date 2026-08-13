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

    public static By botonDelModal(String etiqueta) {
        return By.xpath("//mat-dialog-container//button[contains(., '" + etiqueta + "')]");
    }

    public static By campoDelModal(String formcontrolname) {
        return By.cssSelector(
                "mat-dialog-container input[formcontrolname='" + formcontrolname + "']");
    }

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
    public static final By SOLICITUDES_CAMPO_FECHA_NACIMIENTO =
            By.cssSelector("input[formcontrolname='birthDate']");
    /** La direccion es un textarea, no un input. */
    public static final By SOLICITUDES_CAMPO_DIRECCION =
            By.cssSelector("textarea[formcontrolname='street']");
    public static final By SOLICITUDES_CALENDARIO = By.tagName("mat-datepicker-toggle");
    public static final By SOLICITUDES_OPCIONES_PEP = By.cssSelector(
            "mat-radio-group[formcontrolname='hasPoliticallyExposedApplicants'] mat-radio-button");
    public static final By SOLICITUDES_OPCION_PEP_MARCADA = By.cssSelector(
            "mat-radio-group[formcontrolname='hasPoliticallyExposedApplicants'] "
                    + "mat-radio-button.mat-radio-checked");
    public static final By SOLICITUDES_BOTON_AGREGAR_PEP = boton("AGREGAR ADICIONAL");

    // --------------------------------------------------------------- Usuarios
    public static final By USUARIOS_BOTON_AGREGAR = boton("AGREGAR USUARIO");
    public static final By USUARIO_LISTA_AREA = By.cssSelector("mat-select[formcontrolname='area']");
    public static final By USUARIO_LISTA_TIPO = By.cssSelector("mat-select[formcontrolname='rol']");
    public static final By USUARIO_CAMPO_NOMBRES = By.cssSelector("input[formcontrolname='name']");
    public static final By USUARIO_CAMPO_APELLIDOS =
            By.cssSelector("input[formcontrolname='lastName']");
    public static final By USUARIO_CAMPO_CARGO =
            By.cssSelector("input[formcontrolname='position']");
    public static final By USUARIO_CAMPO_CORREO = By.cssSelector("input[formcontrolname='email']");
    public static final By USUARIO_LISTA_CODIGO_PAIS =
            By.cssSelector("mat-select[formcontrolname='phoneCountryCode']");
    public static final By USUARIO_CAMPO_TELEFONO_MOVIL =
            By.cssSelector("input[formcontrolname='mobilePhone']");
    public static final By USUARIO_CAMPO_TELEFONO_FIJO =
            By.cssSelector("input[formcontrolname='phone']");
    public static final By USUARIO_BOTON_GUARDAR = boton("GUARDAR REGISTRO");
    public static final By BOTON_CANCELAR = boton("CANCELAR");

    // --------------------------------------------------------------- Catalogos
    public static final By CATALOGO_LISTA =
            By.xpath("//mat-form-field[contains(., 'Seleccionar catálogo')]//mat-select");
    public static final By CATALOGO_BOTON_AGREGAR = boton("AGREGAR ELEMENTO");

    public static final By OPCIONES_DE_LISTA = By.tagName("mat-option");

    public static By opcionDeLista(String nombre) {
        return By.xpath("//mat-option[contains(., '" + nombre + "')]");
    }
}
