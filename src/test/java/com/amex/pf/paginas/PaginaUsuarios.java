package com.amex.pf.paginas;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.testng.Assert;

import com.amex.pf.base.Configuracion;

/**
 * Pantalla de Usuarios: consultas de la tabla, filtro y detalle, y el formulario
 * "Agregar usuario" (PF_CP_011 a PF_CP_021).
 *
 * Solo lectura: se escribe en los campos y se leen las listas, pero nunca se
 * presiona GUARDAR REGISTRO, asi que no se crean usuarios. El dato con el que se
 * filtra se toma de la propia tabla, asi la prueba sirve en cualquier ambiente.
 */
public class PaginaUsuarios extends PaginaFormulario {

    /** Columna de la tabla (0 = Numero de empleado, 1 = Nombre, 2 = Apellidos...). */
    public static final int COLUMNA_NOMBRE = 1;
    public static final int COLUMNA_CORREO = 3;

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

    public PaginaUsuarios filtrarPorNombre(String nombre) {
        escribir(Selectores.USUARIOS_FILTRO_NOMBRE, nombre);
        hacerClic(Selectores.BOTON_BUSCAR);
        return this;
    }

    public PaginaUsuarios limpiarElFiltro() {
        hacerClic(Selectores.BOTON_LIMPIAR);
        return this;
    }

    public String valorDelFiltroDeNombre() {
        String valor = valorDe(Selectores.USUARIOS_FILTRO_NOMBRE);
        return valor == null ? "" : valor;
    }

    public List<String> nombresDeLaTabla() {
        return leerAunqueLaTablaSeRefresque(() -> filasConDatos().stream()
                .map(fila -> textoDe(fila.findElements(By.tagName("td")).get(COLUMNA_NOMBRE)))
                .toList());
    }

    /** Espera a que la tabla responda al filtro y devuelve los nombres visibles. */
    public List<String> nombresDeLaTablaCuandoTodosContengan(String texto) {
        espera().until(navegador -> nombresDeLaTabla().stream()
                .allMatch(nombre -> nombre.toUpperCase().contains(texto.toUpperCase())));
        return nombresDeLaTabla();
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

            List<WebElement> ojos = filas.get(0).findElements(Selectores.VER_DETALLE_DE_LA_FILA);
            Assert.assertFalse(ojos.isEmpty(),
                    "La fila del usuario no muestra el boton Ver detalle.");
            return ojos.get(0).isDisplayed() ? ojos.get(0) : null;
        });
        ojo.click();
        verVisible(Selectores.MODAL);
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

    /** Areas que debe mostrar la lista, configurables en amex.usuario.areas. */
    public static String[] areasEsperadas() {
        return Configuracion.lista("amex.usuario.areas");
    }

    /** Tipos de usuario esperados, configurables en amex.usuario.tipos. */
    public static String[] tiposDeUsuarioEsperados() {
        return Configuracion.lista("amex.usuario.tipos");
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
}
