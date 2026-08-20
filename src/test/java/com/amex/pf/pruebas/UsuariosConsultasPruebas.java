package com.amex.pf.pruebas;

import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;

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

    /**
     * Como debe quedar la columna filtrada en PF_CP_028. Nombre y correo son
     * campos de texto (basta con que la celda lo contenga), mientras Rol y Estatus
     * se eligen de una lista, asi que la celda debe ser exactamente lo elegido:
     * "Activo" no puede traer "Inactivo", que lo contiene como texto.
     */
    private static final BiPredicate<String, String> CONTIENE =
            (celda, buscado) -> celda.toUpperCase().contains(buscado.toUpperCase());
    private static final BiPredicate<String, String> ES_IGUAL =
            (celda, buscado) -> celda.equalsIgnoreCase(buscado);

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
        laTablaFiltradaSoloDebeMostrar("Nombre", PaginaUsuarios.COLUMNA_NOMBRE,
                nombre -> usuarios.abrirElFiltro().filtrarPorNombre(nombre), CONTIENE);
    }

    @Test(groups = {"consultas", "usuarios"},
            description = "PF_CP_028 El filtro de usuarios busca por correo electronico")
    public void pfCp028FiltrarPorCorreo() {
        laTablaFiltradaSoloDebeMostrar("Correo electronico", PaginaUsuarios.COLUMNA_CORREO,
                correo -> usuarios.abrirElFiltro().filtrarPorCorreo(correo), CONTIENE);
    }

    @Test(groups = {"consultas", "usuarios"},
            description = "PF_CP_028 El filtro de usuarios busca por rol")
    public void pfCp028FiltrarPorRol() {
        laTablaFiltradaSoloDebeMostrar("Rol", PaginaUsuarios.COLUMNA_ROL,
                rol -> usuarios.abrirElFiltro().filtrarPorRol(rol), ES_IGUAL);
    }

    @Test(groups = {"consultas", "usuarios"},
            description = "PF_CP_028 El filtro de usuarios busca por estatus")
    public void pfCp028FiltrarPorEstatus() {
        laTablaFiltradaSoloDebeMostrar("Estatus", PaginaUsuarios.COLUMNA_ESTATUS,
                estatus -> usuarios.abrirElFiltro().filtrarPorEstatus(estatus), ES_IGUAL);
    }

    /**
     * Patron comun de PF_CP_028: el valor buscado se toma de la primera fila de la
     * tabla (asi la prueba no depende de que el ambiente tenga un usuario, un rol
     * o un estatus en particular), se aplica el filtro y se exige que TODAS las
     * filas que quedaron correspondan a lo buscado en su columna.
     */
    private void laTablaFiltradaSoloDebeMostrar(String filtro, int columna,
            Consumer<String> aplicarElFiltro, BiPredicate<String, String> corresponde) {
        String buscado = usuarios.valorDeLaPrimeraFila(columna);
        Assert.assertFalse(buscado.isBlank(),
                "La primera fila de la tabla no tiene " + filtro + ": no hay con que filtrar.");

        aplicarElFiltro.accept(buscado);
        Predicate<String> condicion = celda -> corresponde.test(celda, buscado);
        List<String> visibles = usuarios.valoresDeLaColumnaCuandoTodos(columna, condicion);

        Assert.assertFalse(visibles.isEmpty(),
                "El filtro " + filtro + " = \"" + buscado + "\" no devolvio ningun usuario.");
        Assert.assertTrue(visibles.stream().allMatch(condicion),
                "La tabla filtrada por " + filtro + " = \"" + buscado
                        + "\" muestra usuarios que no corresponden: " + visibles + ".");
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
