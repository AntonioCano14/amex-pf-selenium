package com.amex.pf.pruebas;

import java.util.List;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.amex.pf.base.PruebaBase;
import com.amex.pf.paginas.PaginaLogin;
import com.amex.pf.paginas.PaginaUsuarios;

/**
 * Ola 3 - Consultas de la pantalla de Usuarios (PF_CP_026 a PF_CP_030).
 *
 * Solo lectura: se consulta la tabla, se filtra y se abre el detalle, pero nunca
 * se presiona GUARDAR REGISTRO, EDITAR DATOS ni Desactivar.
 */
public class UsuariosConsultasPruebas extends PruebaBase {

    private PaginaUsuarios usuarios;

    @BeforeMethod(alwaysRun = true)
    public void abrirUsuarios() {
        new PaginaLogin().iniciarSesionConCredencialesValidas().irAlMenu("Usuarios");
        usuarios = new PaginaUsuarios().abrir();
    }

    @Test(groups = {"consultas", "usuarios"},
            description = "PF_CP_026 El boton Actualizar tabla recarga la tabla de usuarios")
    public void pfCp026ActualizarLaTabla() {
        int antes = usuarios.cuantosUsuariosMuestraLaTabla();
        usuarios.actualizarLaTabla().esperarQueLaTablaTenga(antes);
    }

    @Test(groups = {"consultas", "usuarios"},
            description = "PF_CP_028 El filtro de usuarios busca por nombre")
    public void pfCp028FiltrarPorNombre() {
        // El dato del filtro se toma de la propia tabla: asi la prueba no depende
        // de que el ambiente tenga un usuario especifico.
        String nombre = usuarios.valorDeLaPrimeraFila(PaginaUsuarios.COLUMNA_NOMBRE);

        List<String> nombres = usuarios.abrirElFiltro()
                .filtrarPorNombre(nombre)
                .nombresDeLaTablaCuandoTodosContengan(nombre);

        Assert.assertFalse(nombres.isEmpty(),
                "El filtro por nombre \"" + nombre + "\" no devolvio ningun usuario.");
        Assert.assertTrue(nombres.stream()
                        .allMatch(actual -> actual.toUpperCase().contains(nombre.toUpperCase())),
                "La tabla filtrada por \"" + nombre + "\" muestra otros usuarios: " + nombres + ".");
    }

    @Test(groups = {"consultas", "usuarios"},
            description = "PF_CP_029 El boton Limpiar borra los filtros de usuarios")
    public void pfCp029LimpiarLosFiltros() {
        int todos = usuarios.cuantosUsuariosMuestraLaTabla();
        String nombre = usuarios.valorDeLaPrimeraFila(PaginaUsuarios.COLUMNA_NOMBRE);

        usuarios.abrirElFiltro().filtrarPorNombre(nombre).limpiarElFiltro();

        Assert.assertEquals(usuarios.valorDelFiltroDeNombre(), "",
                "El campo Nombre del filtro no quedo vacio.");
        usuarios.esperarQueLaTablaTenga(todos);
    }

    @Test(groups = {"consultas", "usuarios"},
            description = "PF_CP_030 Ver detalle muestra los datos del usuario")
    public void pfCp030VerDetalleDelUsuario() {
        usuarios.abrirElDetalleDeUnUsuarioConNumeroDeEmpleado()
                .elDetalleDebeMostrar("Activo:", "Ultimo Acceso:", "Fecha de Creación:", "Area",
                        "Tipo de usuario", "Número de empleado", "Nombre(s)", "Apellidos",
                        "Cargo", "Correo electrónico", "Código de país",
                        "Teléfono móvil (10 dígitos)", "Teléfono fijo")
                .cerrarElDetalle();
    }
}
