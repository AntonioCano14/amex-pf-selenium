package com.amex.pf.pruebas;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.amex.pf.base.PruebaBase;
import com.amex.pf.datos.UsuarioDePrueba;
import com.amex.pf.paginas.PaginaLogin;
import com.amex.pf.paginas.PaginaPrincipal;
import com.amex.pf.paginas.PaginaUsuarios;

/**
 * OLA 5 - Alta, edicion, desactivacion y activacion de usuarios
 * (PF_CP_020 y PF_CP_039 a PF_CP_045).
 *
 * OJO: estas pruebas SI ESCRIBEN en el ambiente. Reglas de datos:
 * - el usuario que se crea lleva el prefijo de amex.datos.prefijo (ZZAUTOQA), un
 *   correo distinto en cada ejecucion y nunca es un usuario real;
 * - la aplicacion no permite borrar usuarios, por eso la prueba lo deja INACTIVO;
 * - no se toca ningun usuario que no haya creado la propia prueba (en particular,
 *   GENERAR CONTRASEÑA se presiona solo sobre el usuario de automatizacion, porque
 *   cambia la contrasena de quien lo reciba).
 *
 * Cada prueba crea su propio usuario y trabaja solo sobre el.
 *
 * PF_CP_039 a PF_CP_045 SI entran a la suite de regresion (no llevan el grupo
 * escribe_datos): cada regresion deja dos usuarios ZZAUTOQA inactivos, que es el
 * precio de validar el detalle, la contrasena y la baja sin tocar usuarios reales.
 * PF_CP_020 queda fuera porque el alta ya se ejecuta dentro de esos dos casos.
 *
 * NOTA sobre PF_CP_031 a PF_CP_038: son las mismas validaciones de campos pero en
 * el detalle del usuario y viven en UsuariosDetalleValidacionesPruebas (solo
 * lectura); no se duplican aqui.
 */
public class UsuariosAltasPruebas extends PruebaBase {

    private PaginaPrincipal inicio;
    private PaginaUsuarios usuarios;

    @BeforeMethod(alwaysRun = true)
    public void abrirUsuarios() {
        inicio = new PaginaLogin().iniciarSesionConCredencialesValidas();
        inicio.irAlMenu("Usuarios");
        usuarios = new PaginaUsuarios().abrir();
    }

    @Test(groups = {"ola5", "usuarios", "escribe_datos"},
            description = "PF_CP_020 Guardar registro da de alta al usuario y la tabla lo muestra "
                    + "activo")
    public void pfCp020GuardarRegistroDeUsuario() {
        UsuarioDePrueba usuario = UsuarioDePrueba.nuevo(sufijoDeLaEjecucion());
        try {
            usuarios.abrirElAltaDeUsuario()
                    .llenarElAltaDeUsuario(usuario);
            Assert.assertFalse(usuarios.elBotonGuardarEstaDeshabilitado(),
                    "Con todos los campos llenos, GUARDAR REGISTRO sigue deshabilitado.");

            usuarios.guardarElRegistro();
            usuarios.abrir()
                    .buscarPorCorreo(usuario.correo())
                    .laTablaDebeMostrarAlUsuario(usuario.numeroDeEmpleado(), "Activo");
        } finally {
            usuarios.desactivarSiQuedoActivo(usuario.correo(), usuario.numeroDeEmpleado());
        }
    }

    @Test(groups = {"ola5", "usuarios"},
            description = "PF_CP_039/040/041 Detalle del usuario: editar datos, generar "
                    + "contraseña y cancelar")
    public void pfCp039DetalleDelUsuario() {
        UsuarioDePrueba usuario = crearElUsuarioDePrueba();
        try {
            // PF_CP_039: se edita el cargo, se guarda y el detalle lo muestra guardado.
            usuarios.abrirElDetalleDelUsuario(usuario.numeroDeEmpleado())
                    .editarElCargoDelDetalle(usuario.cargoEditado());
            Assert.assertTrue(usuarios.ultimoMensaje().toUpperCase().contains("ACTUALIZ"),
                    "Al guardar no se aviso que se actualizo el usuario. La aplicacion mostro: "
                            + usuarios.ultimoMensaje() + ".");
            usuarios.abrir()
                    .buscarPorCorreo(usuario.correo())
                    .abrirElDetalleDelUsuario(usuario.numeroDeEmpleado())
                    .elCargoDelDetalleDebeSer(usuario.cargoEditado());

            // PF_CP_040: generar contrasena avisa que se envio al correo del usuario.
            usuarios.generarLaContrasena();
            Assert.assertFalse(usuarios.ultimoMensaje().isBlank(),
                    "GENERAR CONTRASEÑA no mostro ningun mensaje sobre el detalle del usuario.");

            // PF_CP_041: Cancelar cierra el detalle y regresa a la pantalla de Usuarios.
            if (!usuarios.elDetalleEstaAbierto()) {
                usuarios.abrir()
                        .buscarPorCorreo(usuario.correo())
                        .abrirElDetalleDelUsuario(usuario.numeroDeEmpleado());
            }
            usuarios.cancelarElDetalle();
            inicio.laDireccionDebeContener("expedient/users");
            usuarios.abrir();
        } finally {
            usuarios.desactivarSiQuedoActivo(usuario.correo(), usuario.numeroDeEmpleado());
        }
    }

    @Test(groups = {"ola5", "usuarios"},
            description = "PF_CP_042/043/044/045 Desactivar un usuario (aceptar y cancelar el "
                    + "modal) y volver a activarlo")
    public void pfCp042DesactivarYActivarUsuario() {
        UsuarioDePrueba usuario = crearElUsuarioDePrueba();
        try {
            // PF_CP_042 y PF_CP_044: el modal avisa a quien se va a desactivar y Cancelar
            // no cambia el estatus.
            usuarios.desactivarAlUsuario(usuario.numeroDeEmpleado());
            String aviso = usuarios.textoDelPopup();
            Assert.assertTrue(aviso.toUpperCase().contains(usuario.nombres().toUpperCase()),
                    "El modal de desactivar no dice a que usuario se va a desactivar. Muestra: "
                            + aviso + ".");
            usuarios.cancelarElPopup()
                    .laTablaDebeMostrarAlUsuario(usuario.numeroDeEmpleado(), "Activo");

            // PF_CP_043: al aceptar, el estatus cambia a inactivo.
            usuarios.desactivarAlUsuario(usuario.numeroDeEmpleado())
                    .aceptarElPopup()
                    .laTablaDebeMostrarAlUsuario(usuario.numeroDeEmpleado(), "Inactivo");

            // PF_CP_045: Activar usuario desde el detalle lo regresa a activo.
            usuarios.activarAlUsuario(usuario.numeroDeEmpleado())
                    .laTablaDebeMostrarAlUsuario(usuario.numeroDeEmpleado(), "Activo");
        } finally {
            usuarios.desactivarSiQuedoActivo(usuario.correo(), usuario.numeroDeEmpleado());
        }
    }

    /** Crea el usuario con el que trabajan los casos del detalle y de la baja. */
    private UsuarioDePrueba crearElUsuarioDePrueba() {
        UsuarioDePrueba usuario = UsuarioDePrueba.nuevo(sufijoDeLaEjecucion());
        usuarios.abrirElAltaDeUsuario()
                .llenarElAltaDeUsuario(usuario)
                .guardarElRegistro();
        // Se filtra por correo (dato unico): filtrar por el nombre ZZAUTOQA trae a
        // todos los usuarios de automatizacion y el recien creado puede caer en otra
        // pagina de la tabla.
        usuarios.abrir().buscarPorCorreo(usuario.correo());
        return usuario;
    }

    /** Numero que distingue los datos de esta ejecucion de los de las anteriores. */
    private String sufijoDeLaEjecucion() {
        return String.valueOf(System.currentTimeMillis() % 100000000L);
    }
}
