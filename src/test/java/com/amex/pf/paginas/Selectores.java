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

    /** Algunos campos de los modales solo se distinguen por su placeholder. */
    public static By campoDelModalPorPlaceholder(String placeholder) {
        return By.cssSelector("mat-dialog-container input[placeholder='" + placeholder + "']");
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
    /** Capa de carga de la aplicacion: mientras esta visible tapa los controles. */
    public static final By CARGANDO = By.cssSelector("div.loader");
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

    // ------------------------------------------------------------------- Inicio
    /** Leyenda de la grafica de Inicio: "Aprobada - 4" (PF_CP_008). */
    public static final By INICIO_LEYENDA_DE_LA_GRAFICA =
            By.cssSelector("span.progress-legend-one");
    /** Bloque de detalle de un estatus: porcentaje, cantidad y nombre del estatus. */
    public static final By INICIO_DETALLE_POR_ESTATUS = By.cssSelector("div.detail-progress");
    public static final By INICIO_PORCENTAJE_DEL_DETALLE = By.cssSelector("span.progress-detail");
    public static final By INICIO_ESTATUS_DEL_DETALLE = By.cssSelector("span.status-detail");

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
    public static final By USUARIO_CAMPO_NUMERO_DE_EMPLEADO =
            By.cssSelector("input[formcontrolname='employeeNumber']");

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
    /** Ojo de la columna Ver de la tabla del catalogo. */
    public static final By CATALOGO_VER_DETALLE = By.cssSelector("table tr td img.eye");
    public static final By CATALOGO_BOTON_EDITAR = botonDelModal("EDITAR DATOS");

    // ------------------------------------------------- Altas y bajas (ola 5)
    /** Paginas de la tabla: la tabla de un catalogo no tiene filtro de busqueda. */
    public static final By PAGINA_DE_LA_TABLA = By.cssSelector("button.item");
    /** Icono de la columna Inactivar de la fila de un catalogo. */
    public static final By CATALOGO_INACTIVAR_DE_LA_FILA =
            By.cssSelector("img[src*='delete']");
    public static final By CATALOGO_BOTON_ACTIVAR = botonDelModal("ACTIVAR REGISTRO");
    public static final By MODAL_BOTON_ACEPTAR_MAYUSCULAS = botonDelModal("ACEPTAR");
    public static final By MODAL_CAMPO_ARCHIVO =
            By.cssSelector("mat-dialog-container input[type='file']");

    /** Detalle de un usuario (modal): botones de edicion, contrasena y activacion. */
    public static final By USUARIO_DETALLE_BOTON_EDITAR = botonDelModal("EDITAR DATOS");
    public static final By USUARIO_DETALLE_BOTON_GUARDAR = botonDelModal("GUARDAR");
    public static final By USUARIO_DETALLE_BOTON_CONTRASENA = botonDelModal("GENERAR CONTRASEÑA");
    public static final By USUARIO_DETALLE_BOTON_CANCELAR = botonDelModal("CANCELAR");
    public static final By USUARIO_DETALLE_BOTON_ACTIVAR = botonDelModal("ACTIVAR USUARIO");
    public static final By USUARIO_DETALLE_BOTON_COPIAR_CONTRASENA =
            botonDelModal("Copiar contraseña");
    public static final By USUARIO_DETALLE_CAMPO_NOMBRES = campoDelModal("name");

    /** Boton Cancelar de los popup de confirmacion (los del detalle son CANCELAR). */
    public static final By MODAL_BOTON_CANCELAR = botonDelModal("Cancelar");

    public static final By USUARIO_DETALLE_CAMPO_CARGO = campoDelModal("position");
    /** Icono de la columna Desactivar de la fila de un usuario. */
    public static final By DESACTIVAR_DE_LA_FILA =
            By.cssSelector("td.action-column button img[src*='disable-user']");

    // ----------------------------------------------------- Descargas (ola 4)
    public static final By USUARIOS_BOTON_EXPORTAR = boton("Exportar a excel");
    public static final By USUARIOS_BOTON_CARGA_MASIVA = boton("CARGA MASIVA");
    public static final By CARGA_MASIVA_BOTON_LAYOUT = boton("Descargar layout");
    public static final By CARGA_MASIVA_CAMPO_ARCHIVO = By.cssSelector("input[type='file']");
    public static final By SOLICITUDES_BOTON_EXPORTAR = boton("Exportar");
    public static final By SOLICITUDES_BOTON_IMPORTAR = boton("Importar");
    /** Los dos botones de descarga de una fila: expediente completo y ZIP Griffin. */
    public static final By ZIP_DE_LA_FILA =
            By.cssSelector("td.action-column button img[src*='zip-download']");
    public static final By REPORTES_BOTON_GENERAR = boton("Generar reporte");
    public static final By REPORTES_CAMPO_FECHA_INICIO =
            By.cssSelector("input[formcontrolname='dateInitial']");
    public static final By REPORTES_CAMPO_FECHA_FIN =
            By.cssSelector("input[formcontrolname='dateEnd']");
    public static final By CALENDARIO_BOTONES = By.cssSelector("mat-datepicker-toggle button");
    public static final By CALENDARIO_PERIODO = By.cssSelector(".mat-calendar-period-button");
    public static final By CALENDARIO_SIGUIENTE = By.cssSelector(".mat-calendar-next-button");

    public static final By OPCIONES_DE_LISTA = By.tagName("mat-option");

    // -------------------------------------------- Detalle del expediente (ola 6)
    /** Las acciones del detalle (DNI, Firma, Caratula, RENAPER, Devolver,
     * Dictaminar) no son botones: son pestanas de Angular Material. */
    public static final By DETALLE_PESTANAS = By.cssSelector(".mat-tab-label");
    public static final By DETALLE_ESTATUS = By.xpath("//*[contains(text(), 'Estatus:')]");
    /** Menu de tres puntos del detalle (Expirar, Reenviar URL, Copiar URL, ZIP). */
    public static final By DETALLE_MENU = By.xpath("//button[contains(., 'more_vert')]");
    public static final By MENU_ABIERTO = By.cssSelector("div.mat-menu-panel");
    public static final By OPCIONES_DEL_MENU = By.cssSelector("div.mat-menu-panel button");

    public static By opcionDelMenu(String etiqueta) {
        return By.xpath("//div[contains(@class, 'mat-menu-panel')]//button[contains(., '"
                + etiqueta + "')]");
    }

    /** Casilla de seleccion de una fila de la tabla de solicitudes. */
    public static final By CASILLAS_DE_LA_TABLA = By.cssSelector("table mat-checkbox input");
    public static final By SOLICITUDES_BOTON_APROBAR = boton("Aprobar solicitudes");
    public static final By SOLICITUDES_BOTON_DENEGAR = boton("Denegar solicitudes");

    // ------------------------------------------- Adicionales PEP (ola 6)
    /** Tabla de adicionales PEP del formulario de alta de solicitud. */
    public static final By PEP_TABLA = By.cssSelector("table");
    public static final By PEP_FILAS = FILAS_CON_DATOS;
    public static final By PEP_CAMPO_NOMBRE = campoDelModal("name");
    public static final By PEP_CAMPO_APELLIDOS = campoDelModal("lastName");
    public static final By PEP_CAMPO_DNI = campoDelModal("dni");
    public static final By PEP_CAMPO_CARGO = campoDelModal("position");
    public static final By PEP_CAMPO_RELACION = campoDelModal("relationship");
    public static final By PEP_BOTON_ACEPTAR = botonDelModal("ACEPTAR");
    /** Los iconos de la fila del PEP son botones con el nombre del icono. */
    public static final By PEP_BOTON_EDITAR = boton("edit");
    public static final By PEP_BOTON_ELIMINAR = boton("delete");
    /** Check "Condicionada a ingresos" del alta de solicitud (PF_CP_122). */
    public static final By SOLICITUDES_CHECKS = By.tagName("mat-checkbox");

    // ---------------------------------------------------- Costos y cuotas (ola 6)
    public static final By COSTOS_BOTON_AGREGAR = boton("Agregar costo");
    public static final By COSTO_CAMPO_INGRESO_MINIMO = campoDelModal("minimumIncome");
    public static final By COSTO_CAMPO_COSTO = campoDelModal("cost");
    public static final By COSTO_CAMPO_COSTO_SIN_IMPUESTOS = campoDelModal("costWithoutTax");
    public static final By COSTO_CAMPO_CUOTA_METAL = campoDelModal("metalCost");
    public static final By COSTO_CAMPO_CUOTA_METAL_SIN_IMPUESTOS =
            campoDelModal("metalCostWithoutTax");
    public static final By COSTO_CAMPO_CUOTA_ADICIONAL = campoDelModal("additionalCost");
    public static final By COSTO_CAMPO_CUOTA_ADICIONAL_SIN_IMPUESTOS =
            campoDelModal("additionalCostWithoutTax");
    public static final By COSTO_BOTON_GUARDAR = botonDelModal("Guardar");
    public static final By COSTO_BOTON_CANCELAR = botonDelModal("Cancelar");
    /** Icono de edicion de una fila de costos. */
    public static final By COSTO_EDITAR_DE_LA_FILA = By.cssSelector("table img[src*='edit']");
    /** Los campos de Cuotas Generales no tienen formcontrolname: se ubican por fila. */
    public static final By CUOTAS_CAMPOS = By.cssSelector("table input");
    public static final By CUOTAS_BOTON_GUARDAR = boton("Guardar");

    public static By opcionDeLista(String nombre) {
        return By.xpath("//mat-option[contains(., '" + nombre + "')]");
    }
}
