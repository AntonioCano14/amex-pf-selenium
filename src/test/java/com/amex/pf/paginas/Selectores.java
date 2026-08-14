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
    public static final By MODAL_BOTON_CERRAR =
            By.cssSelector("mat-dialog-container button[aria-label='cerrar']");
    public static final By MODAL_CAMPOS = By.cssSelector("mat-dialog-container input");

    public static By botonDelModal(String etiqueta) {
        return By.xpath("//mat-dialog-container//button[contains(., '" + etiqueta + "')]");
    }

    /** Los campos de los modales no tienen formcontrolname: se ubican por su placeholder. */
    public static By campoDelModal(String placeholder) {
        return By.cssSelector("mat-dialog-container input[placeholder='" + placeholder + "']");
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
    /** Las tablas no usan tbody: las filas con datos son las que tienen celdas td. */
    public static final By FILAS_CON_DATOS = By.xpath("//table//tr[td]");
    public static final By ENCABEZADOS_DE_TABLA = By.cssSelector("table th");
    public static final By GRAFICA = By.cssSelector("canvas, svg");
    public static final By CALENDARIO = By.tagName("mat-datepicker-toggle");
    public static final By CALENDARIO_ABIERTO = By.tagName("mat-calendar");
    public static final By CALENDARIO_DIAS = By.cssSelector("mat-calendar td.mat-calendar-body-cell");
    public static final By MARCA_DEL_DIA_DE_HOY = By.cssSelector(".mat-calendar-body-today");
    public static final By BOTON_BUSCAR = boton("Buscar");
    public static final By BOTON_LIMPIAR = boton("Limpiar");
    public static final By BOTON_FILTRAR = boton("Filtrar");

    public static By textoVisible(String texto) {
        return By.xpath("//*[contains(text(), '" + texto + "')]");
    }

    public static By boton(String etiqueta) {
        return By.xpath("//button[contains(., '" + etiqueta + "')]");
    }

    // ----------------------------------------------------------------- Usuarios
    public static final By USUARIOS_BOTON_ACTUALIZAR_TABLA = boton("Actualizar tabla");
    public static final By USUARIOS_FILTRO_NOMBRE = By.cssSelector("input[formcontrolname='name']");
    public static final By USUARIOS_FILTRO_CORREO = By.cssSelector("input[formcontrolname='email']");
    public static final By USUARIOS_FILTRO_ROL = By.cssSelector("mat-select[formcontrolname='rol']");
    public static final By USUARIOS_FILTRO_ESTATUS =
            By.cssSelector("mat-select[formcontrolname='status']");
    /** Ojo de Ver detalle dentro de una fila de la tabla. */
    public static final By VER_DETALLE_DE_LA_FILA =
            By.cssSelector("td.action-column button img[src*='show-off']");

    // ------------------------------------------------------------- Solicitudes
    public static final By SOLICITUDES_BOTON_CREAR = boton("CREAR SOLICITUD");
    public static final By SOLICITUDES_FILTRO_REFERENCIA =
            By.cssSelector("input[formcontrolname='reference']");
    public static final By SOLICITUDES_FILTRO_DNI = By.cssSelector("input[formcontrolname='dni']");
    public static final By SOLICITUDES_FILTRO_NOMBRE =
            By.cssSelector("input[formcontrolname='name']");
    public static final By SOLICITUDES_FILTRO_APELLIDOS =
            By.cssSelector("input[formcontrolname='lastName']");
    public static final By SOLICITUDES_FILTRO_FECHA_INICIO =
            By.cssSelector("input[formcontrolname='dateInitial']");
    public static final By SOLICITUDES_FILTRO_FECHA_FIN =
            By.cssSelector("input[formcontrolname='dateEnd']");
    public static final By SOLICITUDES_FILTRO_ESTATUS =
            By.cssSelector("mat-select[formcontrolname='status']");
    /** Ojo de Ver detalle dentro de una fila de la tabla de solicitudes. */
    public static final By VER_DETALLE_DE_LA_SOLICITUD =
            By.cssSelector("td.action-column a img.detail-icon");

    public static By solicitudPestanaDelDetalle(String nombre) {
        return By.xpath("//button[contains(., '" + nombre + "')] | //*[contains(@class, 'tab')]"
                + "[contains(., '" + nombre + "')]");
    }

    // ------------------------------------------------------- Tasas de interes
    public static final By TASAS_LISTA_TIPO =
            By.xpath("//mat-form-field[contains(., 'Tipo de tasa')]//mat-select");
    public static final By TASAS_LISTA_ANIO =
            By.xpath("//mat-form-field[contains(., 'Año')]//mat-select");
    public static final By TASAS_LISTA_MES =
            By.xpath("//mat-form-field[contains(., 'Mes')]//mat-select");
    public static final By TASAS_CAMPO_TEM_PESOS =
            By.cssSelector("input[formcontrolname='temPes']");
    public static final By TASAS_CAMPO_CFT_PESOS =
            By.cssSelector("input[formcontrolname='totalFinancialCostPesos']");

    // ------------------------------------------------------------------ Costos
    public static final By COSTOS_BOTON_SELECCIONAR = boton("Seleccionar");
    public static final By COSTOS_LISTAS = By.tagName("mat-select");

    // ---------------------------------------------------------------- Reportes
    public static final By REPORTES_LISTA_TIPO =
            By.cssSelector("mat-select[formcontrolname='reportType']");
    public static final By REPORTES_CAMPO_REFERENCIA =
            By.cssSelector("input[formcontrolname='reference']");
    public static final By REPORTES_CAMPO_DNI = By.cssSelector("input[formcontrolname='dni']");
    public static final By REPORTES_CAMPO_NOMBRE = By.cssSelector("input[formcontrolname='name']");
    public static final By REPORTES_CAMPO_APELLIDOS =
            By.cssSelector("input[formcontrolname='lastName']");
    public static final By REPORTES_CAMPOS_DEL_FILTRO =
            By.cssSelector("input[formcontrolname]");
    public static final By REPORTES_BOTON_LIMPIAR = boton("Limpiar filtros");

    // --------------------------------------------------------------- Dashboard
    public static final By DASHBOARD_LISTA = By.tagName("mat-select");
    public static final By DASHBOARD_PARTES_DE_LA_GRAFICA = By.cssSelector("svg path");
    public static final By SOLICITUDES_CAMPO_NOMBRE = By.cssSelector("input[formcontrolname='name']");
    public static final By SOLICITUDES_CAMPO_APELLIDOS =
            By.cssSelector("input[formcontrolname='lastName']");
    public static final By SOLICITUDES_CAMPO_DNI = By.cssSelector("input[formcontrolname='dni']");
    public static final By SOLICITUDES_CAMPO_CUIL = By.cssSelector("input[formcontrolname='cuil']");

    // --------------------------------------------------------------- Catalogos
    public static final By CATALOGO_LISTA =
            By.xpath("//mat-form-field[contains(., 'Seleccionar catálogo')]//mat-select");
    public static final By CATALOGO_BOTON_AGREGAR = boton("AGREGAR ELEMENTO");
    /** Ojo de la columna Ver de la tabla del catalogo. */
    public static final By CATALOGO_VER_DETALLE = By.cssSelector("table tr td img.eye");
    public static final By CATALOGO_BOTON_EDITAR = botonDelModal("EDITAR DATOS");

    public static final By OPCIONES_DE_LISTA = By.tagName("mat-option");

    public static By opcionDeLista(String nombre) {
        return By.xpath("//mat-option[contains(., '" + nombre + "')]");
    }
}
