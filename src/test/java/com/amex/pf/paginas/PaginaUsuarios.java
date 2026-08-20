package com.amex.pf.paginas;

import java.util.List;
import java.util.function.Predicate;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.testng.Assert;

import com.amex.pf.base.Configuracion;
import com.amex.pf.datos.UsuarioDePrueba;

/**
 * Pantalla de Usuarios: consultas de la tabla, filtro y detalle, y el formulario
 * "Agregar usuario" (PF_CP_011 a PF_CP_021).
 *
 * Los metodos de consulta y de validacion (olas 1 a 4) no guardan nada: escriben
 * en los campos y leen las listas, pero nunca presionan GUARDAR REGISTRO.
 *
 * Los metodos de la seccion "Altas, ediciones y bajas (ola 5)" SI escriben en el
 * ambiente: solo deben usarse desde pruebas del grupo escribe_datos y siempre con
 * los datos de UsuarioDePrueba, que llevan el prefijo de automatizacion.
 */
public class PaginaUsuarios extends PaginaFormulario {

    /** Columna de la tabla (0 = Numero de empleado, 1 = Nombre, 2 = Apellidos...). */
    public static final int COLUMNA_NOMBRE = 1;
    public static final int COLUMNA_CORREO = 3;
    public static final int COLUMNA_ROL = 4;
    public static final int COLUMNA_ESTATUS = 5;

    /**
     * Los cuatro filtros de la pantalla de Usuarios (PF_CP_028 y PF_CP_029), con
     * la columna de la tabla que debe responder a cada uno.
     */
    public enum Filtro {
        NOMBRE("Nombre", Selectores.USUARIOS_FILTRO_NOMBRE, COLUMNA_NOMBRE, false),
        CORREO("Correo electronico", Selectores.USUARIOS_FILTRO_CORREO, COLUMNA_CORREO, false),
        ROL("Rol", Selectores.USUARIOS_FILTRO_ROL, COLUMNA_ROL, true),
        ESTATUS("Estatus", Selectores.USUARIOS_FILTRO_ESTATUS, COLUMNA_ESTATUS, true);

        public final String etiqueta;
        public final By selector;
        public final int columna;
        /** Las listas (Rol, Estatus) se eligen; los demas se escriben. */
        public final boolean esLista;

        Filtro(String etiqueta, By selector, int columna, boolean esLista) {
            this.etiqueta = etiqueta;
            this.selector = selector;
            this.columna = columna;
            this.esLista = esLista;
        }
    }

    /** Ultimo mensaje que mostro la aplicacion en un popup (alta, edicion, baja). */
    private String ultimoMensaje = "";

    /** Numero de empleado de la fila cuyo detalle se abrio (PF_CP_031 a 038). */
    private String numeroDeEmpleadoDeLaTabla = "";

    /** Abre la pantalla y espera a que la aplicacion termine de navegar. */
    public PaginaUsuarios abrir() {
        esperarQueLaUrlContenga("expedient/users");
        verVisible(Selectores.TABLA);
        return this;
    }

    public int cuantosUsuariosMuestraLaTabla() {
        return filasConDatos().size();
    }

    public String valorDeLaPrimeraFila(int columna) {
        return leerAunqueLaTablaSeRefresque(() -> {
            List<WebElement> celdas = filasConDatos().get(0).findElements(By.tagName("td"));
            Assert.assertTrue(celdas.size() > columna,
                    "La fila solo tiene " + celdas.size() + " columnas.");
            return textoDe(celdas.get(columna));
        });
    }

    public PaginaUsuarios actualizarLaTabla() {
        hacerClic(Selectores.USUARIOS_BOTON_ACTUALIZAR_TABLA);
        return this;
    }

    public PaginaUsuarios abrirElFiltro() {
        hacerClic(Selectores.BOTON_FILTRAR);
        verVisible(Selectores.USUARIOS_FILTRO_NOMBRE);
        return this;
    }

    /**
     * Captura un filtro sin buscar todavia: PF_CP_029 llena los filtros y presiona
     * Limpiar, sin pasar por el boton Buscar.
     */
    public PaginaUsuarios capturarElFiltro(Filtro filtro, String valor) {
        if (filtro.esLista) {
            elegirDeLaLista(filtro.selector, valor);
        } else {
            escribir(filtro.selector, valor);
        }
        return this;
    }

    public PaginaUsuarios buscar() {
        hacerClic(Selectores.BOTON_BUSCAR);
        return this;
    }

    public PaginaUsuarios filtrarPor(Filtro filtro, String valor) {
        return capturarElFiltro(filtro, valor).buscar();
    }

    public PaginaUsuarios filtrarPorNombre(String nombre) {
        return filtrarPor(Filtro.NOMBRE, nombre);
    }

    public PaginaUsuarios filtrarPorCorreo(String correo) {
        return filtrarPor(Filtro.CORREO, correo);
    }

    /** Filtra por una opcion de la lista Rol (PF_CP_028). */
    public PaginaUsuarios filtrarPorRol(String rol) {
        return filtrarPor(Filtro.ROL, rol);
    }

    /** Filtra por una opcion de la lista Estatus (PF_CP_028). */
    public PaginaUsuarios filtrarPorEstatus(String estatus) {
        return filtrarPor(Filtro.ESTATUS, estatus);
    }

    /** Valor que muestra hoy un filtro (vacio si no tiene nada capturado). */
    public String valorDelFiltro(Filtro filtro) {
        if (!filtro.esLista) {
            String valor = valorDe(filtro.selector);
            return valor == null ? "" : valor;
        }
        // En un mat-select el valor elegido es el texto del disparador; cuando no
        // hay nada elegido queda vacio (la lista de este filtro no tiene placeholder).
        return textoDe(filtro.selector);
    }

    /** Opciones que ofrece hoy la lista Rol del filtro. */
    public List<String> rolesDelFiltro() {
        return opcionesDeLaLista(Selectores.USUARIOS_FILTRO_ROL);
    }

    /** Opciones que ofrece hoy la lista Estatus del filtro. */
    public List<String> estatusDelFiltro() {
        return opcionesDeLaLista(Selectores.USUARIOS_FILTRO_ESTATUS);
    }

    public PaginaUsuarios limpiarElFiltro() {
        hacerClic(Selectores.BOTON_LIMPIAR);
        return this;
    }

    public String valorDelFiltroDeNombre() {
        return valorDelFiltro(Filtro.NOMBRE);
    }

    public List<String> nombresDeLaTabla() {
        return valoresDeLaColumna(COLUMNA_NOMBRE);
    }

    /** Valores que muestra hoy una columna de la tabla, fila por fila. */
    public List<String> valoresDeLaColumna(int columna) {
        return leerAunqueLaTablaSeRefresque(() -> filasConDatos().stream()
                .map(fila -> textoDe(fila.findElements(By.tagName("td")).get(columna)))
                .toList());
    }

    /**
     * Espera a que la tabla responda al filtro (que todas las filas cumplan lo
     * que se pidio) y devuelve lo que quedo visible en la columna. Si no llega a
     * cumplirse no falla aqui: devuelve la tabla tal cual para que el mensaje de
     * la prueba diga que usuarios quedaron.
     */
    public List<String> valoresDeLaColumnaCuandoTodos(int columna, Predicate<String> condicion) {
        try {
            espera().until(navegador -> valoresDeLaColumna(columna).stream().allMatch(condicion));
        } catch (TimeoutException noFiltro) {
            // El assert de la prueba dice exactamente que quedo en la tabla.
        }
        return valoresDeLaColumna(columna);
    }

    /** Espera a que la tabla responda al filtro y devuelve los nombres visibles. */
    public List<String> nombresDeLaTablaCuandoTodosContengan(String texto) {
        return valoresDeLaColumnaCuandoTodos(COLUMNA_NOMBRE,
                nombre -> nombre.toUpperCase().contains(texto.toUpperCase()));
    }

    public PaginaUsuarios esperarQueLaTablaTenga(int cantidad) {
        espera().until(navegador -> filasConDatos().size() == cantidad);
        return this;
    }

    /**
     * Abre el detalle del primer usuario que tenga numero de empleado: la aplicacion
     * no muestra ese dato en los usuarios que no lo tienen y PF_CP_030 lo exige.
     */
    public PaginaUsuarios abrirElDetalleDeUnUsuarioConNumeroDeEmpleado() {
        WebElement ojo = leerAunqueLaTablaSeRefresque(() -> {
            List<WebElement> filas = filasConDatos().stream()
                    .filter(fila -> !textoDe(fila.findElements(By.tagName("td")).get(0)).isBlank())
                    .toList();
            Assert.assertFalse(filas.isEmpty(),
                    "Ningun usuario de la tabla tiene numero de empleado: no se puede validar el "
                            + "detalle completo que pide PF_CP_030.");

            List<WebElement> celdas = filas.get(0).findElements(By.tagName("td"));
            numeroDeEmpleadoDeLaTabla = textoDe(celdas.get(0));
            List<WebElement> ojos = filas.get(0).findElements(Selectores.VER_DETALLE_DE_LA_FILA);
            Assert.assertFalse(ojos.isEmpty(),
                    "La fila del usuario no muestra el boton Ver detalle.");
            return ojos.get(0).isDisplayed() ? ojos.get(0) : null;
        });
        ojo.click();
        verVisible(Selectores.MODAL);
        return this;
    }

    /** Numero de empleado que mostraba la tabla del usuario cuyo detalle se abrio. */
    public String numeroDeEmpleadoDeLaTabla() {
        return numeroDeEmpleadoDeLaTabla;
    }

    // ------------------------------- Detalle en modo edicion (PF_CP_031 a 038)

    /** Presiona EDITAR DATOS y espera a que el detalle quede editable. */
    public PaginaUsuarios editarLosDatosDelDetalle() {
        hacerClic(Selectores.USUARIO_DETALLE_BOTON_EDITAR);
        verVisible(Selectores.USUARIO_DETALLE_BOTON_GUARDAR);
        return this;
    }

    public List<String> areasDelDetalle() {
        return opcionesDeLaLista(Selectores.USUARIO_DETALLE_LISTA_AREA);
    }

    public List<String> tiposDeUsuarioDelDetalle() {
        return opcionesDeLaLista(Selectores.USUARIO_DETALLE_LISTA_TIPO);
    }

    /** Valor que muestra un campo del detalle (vacio si no tiene nada). */
    public String valorDelDetalle(By campo) {
        String valor = valorDe(campo);
        return valor == null ? "" : valor;
    }

    /** El GUARDAR del detalle, que no es el GUARDAR REGISTRO del alta. */
    public boolean elBotonGuardarDelDetalleEstaDeshabilitado() {
        return elBotonEstaDeshabilitado(Selectores.USUARIO_DETALLE_BOTON_GUARDAR);
    }

    /** Sale del detalle con CANCELAR: la edicion nunca se guarda. */
    public PaginaUsuarios cancelarLaEdicionDelDetalle() {
        hacerClic(Selectores.USUARIO_DETALLE_BOTON_CANCELAR);
        esperarQueDesaparezca(Selectores.MODAL);
        return this;
    }

    public String textoDelDetalle() {
        return textoDe(Selectores.MODAL);
    }

    public PaginaUsuarios elDetalleDebeMostrar(String... etiquetas) {
        // El modal pinta sus campos poco a poco: se espera a que esten todos antes de comparar.
        try {
            espera().until(navegador -> {
                String texto = textoDelDetalle();
                return List.of(etiquetas).stream().allMatch(texto::contains);
            });
        } catch (TimeoutException faltoAlguna) {
            // El mensaje del assert de abajo dice exactamente cual falto.
        }

        String detalle = textoDelDetalle();
        for (String etiqueta : etiquetas) {
            Assert.assertTrue(detalle.contains(etiqueta),
                    "El detalle del usuario no muestra \"" + etiqueta + "\". Muestra: "
                            + detalle.replace("\n", " | ") + ".");
        }
        return this;
    }

    public PaginaUsuarios cerrarElDetalle() {
        cerrarModalSiEstaAbierto();
        esperarQueDesaparezca(Selectores.MODAL);
        return this;
    }

    /**
     * Areas que debe mostrar la lista. Dependen del perfil con el que se ejecuta
     * (CENTURION con un administrador, Ventas con otro perfil), por eso se
     * configuran en amex.usuario.areas o en amex.usuario.areas.CORREO_DEL_USUARIO.
     */
    public static String[] areasEsperadas() {
        return Configuracion.listaDelUsuario("amex.usuario.areas");
    }

    /** Tipos de usuario esperados, configurables igual que las areas. */
    public static String[] tiposDeUsuarioEsperados() {
        return Configuracion.listaDelUsuario("amex.usuario.tipos");
    }

    public PaginaUsuarios abrirElAltaDeUsuario() {
        hacerClic(Selectores.USUARIOS_BOTON_AGREGAR);
        esperarQueLaUrlContenga("users/add");
        verVisible(Selectores.USUARIO_CAMPO_NOMBRES);
        return this;
    }

    public List<String> areasDeLaLista() {
        return opcionesDeLaLista(Selectores.USUARIO_LISTA_AREA);
    }

    /**
     * Los tipos de usuario dependen del area: la lista viene vacia hasta que se
     * elige una.
     */
    public List<String> tiposDeUsuarioDeLaLista(String area) {
        elegirDeLaLista(Selectores.USUARIO_LISTA_AREA, area);
        return opcionesDeLaLista(Selectores.USUARIO_LISTA_TIPO);
    }

    public boolean elBotonGuardarEstaDeshabilitado() {
        return elBotonEstaDeshabilitado(Selectores.USUARIO_BOTON_GUARDAR);
    }

    public PaginaUsuarios cancelar() {
        hacerClic(Selectores.BOTON_CANCELAR);
        esperarQueLaUrlYaNoContenga("users/add");
        return this;
    }

    // ------------------------------------------- Altas, ediciones y bajas (ola 5)

    /** Llena el formulario de alta con los datos de prueba, sin guardar todavia. */
    public PaginaUsuarios llenarElAltaDeUsuario(UsuarioDePrueba usuario) {
        elegirDeLaLista(Selectores.USUARIO_LISTA_AREA, UsuarioDePrueba.area());
        elegirDeLaLista(Selectores.USUARIO_LISTA_TIPO, UsuarioDePrueba.tipoDeUsuario());
        escribir(Selectores.USUARIO_CAMPO_NUMERO_DE_EMPLEADO, usuario.numeroDeEmpleado());
        escribir(Selectores.USUARIO_CAMPO_NOMBRES, usuario.nombres());
        escribir(Selectores.USUARIO_CAMPO_APELLIDOS, usuario.apellidos());
        escribir(Selectores.USUARIO_CAMPO_CARGO, usuario.cargo());
        escribir(Selectores.USUARIO_CAMPO_CORREO, usuario.correo());
        elegirDeLaLista(Selectores.USUARIO_LISTA_CODIGO_PAIS, UsuarioDePrueba.codigoDePais());
        escribir(Selectores.USUARIO_CAMPO_TELEFONO_MOVIL, usuario.telefonoMovil());
        escribir(Selectores.USUARIO_CAMPO_TELEFONO_FIJO, usuario.telefonoFijo());
        return this;
    }

    /**
     * PF_CP_020: presiona GUARDAR REGISTRO y devuelve el mensaje con el que la
     * aplicacion confirma el alta.
     */
    public String guardarElRegistro() {
        hacerClic(Selectores.USUARIO_BOTON_GUARDAR);
        String mensaje = estaVisible(Selectores.MODAL, 15) ? aceptarElPopupYDevolverSuTexto() : "";
        esperarQueLaUrlYaNoContenga("users/add");
        esperarQueTermineDeCargar();
        return mensaje;
    }

    /** Filtra la tabla por nombre y espera a que responda. */
    public PaginaUsuarios buscarPorNombre(String nombre) {
        esperarQueTermineDeCargar();
        if (!estaVisible(Selectores.USUARIOS_FILTRO_NOMBRE, 3)) {
            abrirElFiltro();
        }
        escribir(Selectores.USUARIOS_FILTRO_NOMBRE, nombre);
        hacerClic(Selectores.BOTON_BUSCAR);
        esperarQueTermineDeCargar();
        return this;
    }

    /** La tabla debe mostrar al usuario con el estatus indicado (Activo/Inactivo). */
    public PaginaUsuarios laTablaDebeMostrarAlUsuario(String texto, String estatus) {
        String fila = espera().until(navegador -> {
            WebElement encontrada = filaDelUsuarioSiExiste(texto);
            if (encontrada == null) {
                return null;
            }
            String contenido = textoDe(encontrada);
            return estatusDeLaFila(contenido).equals(estatus.toUpperCase()) ? contenido : null;
        });
        Assert.assertEquals(estatusDeLaFila(fila), estatus.toUpperCase(),
                "El usuario \"" + texto + "\" deberia estar " + estatus
                        + " y la tabla muestra: " + fila.replace("\n", " ") + ".");
        return this;
    }

    /** Abre el detalle (ojo) del usuario indicado. */
    public PaginaUsuarios abrirElDetalleDelUsuario(String texto) {
        WebElement ojo = leerAunqueLaTablaSeRefresque(() -> {
            WebElement fila = filaDelUsuario(texto);
            List<WebElement> ojos = fila.findElements(Selectores.VER_DETALLE_DE_LA_FILA);
            Assert.assertFalse(ojos.isEmpty(),
                    "La fila de \"" + texto + "\" no muestra el boton Ver detalle.");
            return ojos.get(0);
        });
        hacerClic(ojo);
        verVisible(Selectores.MODAL);
        // El detalle se abre vacio y la aplicacion lo llena despues de consultar al
        // servicio: sin esta espera se leerian los datos y el estatus del modal en
        // blanco (aparece como "Inactivo" mientras carga).
        espera().until(navegador -> {
            String nombre = valorDe(Selectores.USUARIO_DETALLE_CAMPO_NOMBRES);
            return nombre == null || nombre.isBlank() ? null : nombre;
        });
        return this;
    }

    /** PF_CP_039: cambia el cargo desde el detalle y guarda. */
    public PaginaUsuarios editarElCargoDelDetalle(String cargo) {
        hacerClic(Selectores.USUARIO_DETALLE_BOTON_EDITAR);
        verVisible(Selectores.USUARIO_DETALLE_BOTON_GUARDAR);
        escribir(Selectores.USUARIO_DETALLE_CAMPO_CARGO, cargo);
        hacerClic(Selectores.USUARIO_DETALLE_BOTON_GUARDAR);
        // La aplicacion cierra el detalle y muestra el aviso "Usuario actualizado",
        // que solo se cierra con la "X".
        ultimoMensaje = aceptarElPopupYDevolverSuTexto();
        esperarQueTermineDeCargar();
        return this;
    }

    /** Comprueba el cargo que quedo guardado en el detalle (PF_CP_039). */
    public PaginaUsuarios elCargoDelDetalleDebeSer(String cargo) {
        String guardado = espera().until(navegador -> {
            String valor = valorDe(Selectores.USUARIO_DETALLE_CAMPO_CARGO);
            return valor == null || valor.isBlank() ? null : valor;
        });
        Assert.assertEquals(guardado, cargo,
                "El detalle no muestra el cargo que se guardo.");
        return this;
    }

    /**
     * PF_CP_040: presiona GENERAR CONTRASEÑA. La aplicacion muestra la contrasena
     * nueva dentro del mismo detalle; la prueba solo comprueba que aparecio con su
     * boton "Copiar contraseña" y NUNCA lee ni imprime el valor de la contrasena.
     */
    public PaginaUsuarios generarLaContrasena() {
        hacerClic(Selectores.USUARIO_DETALLE_BOTON_CONTRASENA);
        Assert.assertTrue(estaVisible(Selectores.USUARIO_DETALLE_BOTON_COPIAR_CONTRASENA, 20),
                "GENERAR CONTRASEÑA no mostro la contrasena nueva en el detalle del usuario.");
        ultimoMensaje = "El detalle muestra la contrasena nueva y el boton Copiar contraseña.";
        return this;
    }

    public boolean elDetalleEstaAbierto() {
        return estaVisible(Selectores.MODAL, 3);
    }

    /** PF_CP_041: sale del detalle con CANCELAR, sin guardar. */
    public PaginaUsuarios cancelarElDetalle() {
        hacerClic(Selectores.USUARIO_DETALLE_BOTON_CANCELAR);
        esperarQueDesaparezca(Selectores.MODAL);
        return this;
    }

    /** PF_CP_042: presiona Desactivar en la fila del usuario y deja el popup abierto. */
    public PaginaUsuarios desactivarAlUsuario(String texto) {
        WebElement icono = leerAunqueLaTablaSeRefresque(() -> {
            WebElement fila = filaDelUsuario(texto);
            List<WebElement> iconos = fila.findElements(Selectores.DESACTIVAR_DE_LA_FILA);
            Assert.assertFalse(iconos.isEmpty(),
                    "La fila de \"" + texto + "\" no muestra el boton Desactivar.");
            return iconos.get(0);
        });
        hacerClic(icono);
        verVisible(Selectores.MODAL);
        return this;
    }

    /** Texto del popup abierto (para comprobar el mensaje de confirmacion). */
    public String textoDelPopup() {
        return textoDe(Selectores.MODAL).replace("\n", " ");
    }

    /** PF_CP_043: acepta el popup de desactivacion. */
    public PaginaUsuarios aceptarElPopup() {
        ultimoMensaje = aceptarElPopupYDevolverSuTexto();
        esperarQueTermineDeCargar();
        return this;
    }

    /** PF_CP_044: cancela el popup de desactivacion, el usuario sigue activo. */
    public PaginaUsuarios cancelarElPopup() {
        hacerClic(Selectores.MODAL_BOTON_CANCELAR);
        esperarQueDesaparezca(Selectores.MODAL);
        esperarQueTermineDeCargar();
        return this;
    }

    /** PF_CP_045: activa al usuario desde su detalle. */
    public PaginaUsuarios activarAlUsuario(String texto) {
        abrirElDetalleDelUsuario(texto);
        Assert.assertTrue(estaVisible(Selectores.USUARIO_DETALLE_BOTON_ACTIVAR, 10),
                "El detalle del usuario inactivo no muestra el boton ACTIVAR USUARIO. "
                        + "El detalle muestra: " + textoDelDetalle().replace("\n", " ") + ".");
        hacerClic(Selectores.USUARIO_DETALLE_BOTON_ACTIVAR);
        ultimoMensaje = aceptarElPopupYDevolverSuTexto();
        esperarQueDesaparezca(Selectores.MODAL);
        esperarQueTermineDeCargar();
        return this;
    }

    /** Ultimo mensaje que mostro la aplicacion en un popup. */
    public String ultimoMensaje() {
        return ultimoMensaje;
    }

    /**
     * Deja el ambiente como estaba: desactiva al usuario creado por la prueba. Se
     * filtra por su nombre (lo unico que acepta el filtro) y se ubica su fila por el
     * numero de empleado.
     */
    public PaginaUsuarios desactivarSiQuedoActivo(String nombre, String numeroDeEmpleado) {
        String texto = numeroDeEmpleado;
        try {
            abrir();
            buscarPorNombre(nombre);
            WebElement fila = filaDelUsuarioSiExiste(texto);
            if (fila == null || !estatusDeLaFila(textoDe(fila)).equals("ACTIVO")) {
                return this;
            }
            desactivarAlUsuario(texto);
            aceptarElPopup();
        } catch (RuntimeException noSePudo) {
            System.out.println("            Aviso: no se pudo desactivar a \"" + texto
                    + "\" al terminar (" + noSePudo.getClass().getSimpleName() + ").");
        }
        return this;
    }

    private WebElement filaDelUsuario(String texto) {
        WebElement fila = espera().until(navegador -> filaDelUsuarioSiExiste(texto));
        Assert.assertNotNull(fila,
                "La tabla de usuarios no muestra ninguna fila con \"" + texto + "\".");
        return fila;
    }

    private WebElement filaDelUsuarioSiExiste(String texto) {
        try {
            return filasConDatos().stream()
                    .filter(fila -> textoDe(fila).toUpperCase().contains(texto.toUpperCase()))
                    .findFirst()
                    .orElse(null);
        } catch (RuntimeException sinTabla) {
            return null;
        }
    }

    // ------------------------------------------------------- Descargas (ola 4)

    public PaginaUsuarios exportarAExcel() {
        esperarQueTermineDeCargar();
        hacerClic(Selectores.USUARIOS_BOTON_EXPORTAR);
        return this;
    }

    public PaginaUsuarios abrirLaCargaMasiva() {
        hacerClic(Selectores.USUARIOS_BOTON_CARGA_MASIVA);
        esperarQueLaUrlContenga("users/upload");
        verVisible(Selectores.CARGA_MASIVA_BOTON_LAYOUT);
        return this;
    }

    public PaginaUsuarios laCargaMasivaDebePermitirElegirArchivo() {
        Assert.assertFalse(buscarTodos(Selectores.CARGA_MASIVA_CAMPO_ARCHIVO).isEmpty(),
                "La pantalla de carga masiva no ofrece donde cargar el documento.");
        return this;
    }

    public PaginaUsuarios descargarElLayout() {
        hacerClic(Selectores.CARGA_MASIVA_BOTON_LAYOUT);
        return this;
    }

    /** Sale de la carga masiva sin subir nada. */
    public PaginaUsuarios salirDeLaCargaMasiva() {
        hacerClic(Selectores.boton("Cancelar"));
        esperarQueLaUrlYaNoContenga("users/upload");
        return this;
    }
}
