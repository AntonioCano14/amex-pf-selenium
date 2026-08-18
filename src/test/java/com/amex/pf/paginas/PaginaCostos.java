package com.amex.pf.paginas;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.SkipException;

/**
 * Pantalla de Costos, solo consultas: se elige un producto y se revisa la pantalla
 * de costos por anio y mes. Nunca se presiona Agregar costo.
 */
public class PaginaCostos extends PaginaBase {

    public PaginaCostos abrir() {
        esperarQueLaUrlContenga("expedient/costs");
        return this;
    }

    public PaginaCostos seleccionarElPrimerProducto() {
        hacerClic(Selectores.COSTOS_BOTON_SELECCIONAR);
        verVisible(Selectores.textoVisible("Seleccionar otro producto"));
        return this;
    }

    /** La pantalla del producto pide anio y mes para mostrar sus costos. */
    public PaginaCostos debePedirAnioYMes() {
        espera().until(navegador ->
                navegador.findElements(Selectores.COSTOS_LISTAS).size() >= 2);
        Assert.assertTrue(estaVisible(Selectores.textoVisible("año"), 10),
                "La pantalla del producto no pide el anio.");
        return this;
    }

    /** Elige el primer anio y el primer mes que ofrecen las listas del producto. */
    public PaginaCostos elegirElPrimerPeriodo() {
        List<WebElement> listas = buscarTodos(Selectores.COSTOS_LISTAS);
        Assert.assertTrue(listas.size() >= 2,
                "La pantalla del producto no muestra las listas de anio y mes.");
        elegirLaPrimeraOpcion(listas.get(0));
        elegirLaPrimeraOpcion(buscarTodos(Selectores.COSTOS_LISTAS).get(1));
        return this;
    }

    /**
     * Los costos del periodo elegido. Si el ambiente no los tiene cargados el caso se
     * reporta OMITIDO: es un dato semilla faltante, no un defecto de la aplicacion.
     */
    public PaginaCostos debeMostrarLosCostosDelPeriodo() {
        espera().until(navegador -> !navegador.findElements(Selectores.TABLA).isEmpty()
                || !navegador.findElements(
                        Selectores.textoVisible("No se han registrado los costos")).isEmpty());

        if (estaVisible(Selectores.textoVisible("No se han registrado los costos"), 3)) {
            throw new SkipException("El producto no tiene costos cargados en este ambiente "
                    + "(\"No se han registrado los costos.\"): hace falta el dato semilla para "
                    + "validar PF_CP_148 completo.");
        }
        verVisible(Selectores.TABLA);
        return this;
    }

    public PaginaCostos debeVerseElTexto(String texto) {
        Assert.assertTrue(estaVisible(Selectores.textoVisible(texto), 15),
                "La pantalla de costos no muestra \"" + texto + "\".");
        return this;
    }

    // ------------------------------------------- Agregar y editar (ola 6)

    /** Campos que debe pedir el popup de costos, en el orden de la matriz. */
    public static final List<By> CAMPOS_DEL_COSTO = List.of(
            Selectores.COSTO_CAMPO_INGRESO_MINIMO,
            Selectores.COSTO_CAMPO_COSTO,
            Selectores.COSTO_CAMPO_COSTO_SIN_IMPUESTOS,
            Selectores.COSTO_CAMPO_CUOTA_METAL,
            Selectores.COSTO_CAMPO_CUOTA_METAL_SIN_IMPUESTOS,
            Selectores.COSTO_CAMPO_CUOTA_ADICIONAL,
            Selectores.COSTO_CAMPO_CUOTA_ADICIONAL_SIN_IMPUESTOS);

    public PaginaCostos elegirElPeriodo(String anio, String mes) {
        List<WebElement> listas = buscarTodos(Selectores.COSTOS_LISTAS);
        Assert.assertTrue(listas.size() >= 2,
                "La pantalla del producto no muestra las listas de anio y mes.");
        elegirDeLaLista(Selectores.COSTOS_LISTAS, anio);
        WebElement listaDeMeses = buscarTodos(Selectores.COSTOS_LISTAS).get(1);
        hacerClic(listaDeMeses);
        WebElement mesElegido = espera().until(navegador -> navegador
                .findElements(Selectores.OPCIONES_DE_LISTA).stream()
                .filter(opcion -> textoDe(opcion).equalsIgnoreCase(mes))
                .findFirst()
                .orElse(null));
        hacerClic(mesElegido);
        esperarQueSeCierrenLasListas();
        return this;
    }

    public PaginaCostos abrirElPopupDeAgregarCosto() {
        hacerClic(Selectores.COSTOS_BOTON_AGREGAR);
        verVisible(Selectores.MODAL);
        return this;
    }

    /**
     * Abre el popup de edicion del primer costo del periodo. Si el periodo no tiene
     * costos cargados el caso queda OMITIDO: falta el dato semilla.
     */
    public PaginaCostos abrirElPopupDeEditarCosto() {
        if (buscarTodos(Selectores.COSTO_EDITAR_DE_LA_FILA).isEmpty()) {
            throw new SkipException("El periodo elegido no tiene costos cargados: hace falta el "
                    + "dato semilla (un costo por producto y periodo) para validar la edicion.");
        }
        hacerClic(Selectores.COSTO_EDITAR_DE_LA_FILA);
        verVisible(Selectores.MODAL);
        return this;
    }

    public PaginaCostos elPopupDebePedirTodosLosDatosDelCosto() {
        for (By campo : CAMPOS_DEL_COSTO) {
            Assert.assertTrue(estaVisible(campo, 10),
                    "El popup de costos no pide el campo " + campo + ".");
        }
        Assert.assertTrue(estaVisible(Selectores.COSTO_BOTON_GUARDAR, 10),
                "El popup de costos no muestra el boton Guardar.");
        return this;
    }

    /** Escribe el mismo importe en todos los campos del popup, sin guardar. */
    public PaginaCostos escribirLosDatosDelCosto(String importe) {
        for (By campo : CAMPOS_DEL_COSTO) {
            escribir(campo, importe);
        }
        return this;
    }

    public boolean elPopupPermiteGuardar() {
        return verVisible(Selectores.COSTO_BOTON_GUARDAR).isEnabled();
    }

    /** Cierra el popup con Cancelar: la automatizacion nunca guarda un costo. */
    public PaginaCostos cerrarElPopupSinGuardar() {
        hacerClic(Selectores.COSTO_BOTON_CANCELAR);
        esperarQueDesaparezca(Selectores.MODAL);
        return this;
    }

    private void elegirLaPrimeraOpcion(WebElement lista) {
        esperarQueSeCierrenLasListas();
        esperarQueTermineDeCargar();
        hacerClic(lista);
        List<WebElement> opciones = espera().until(navegador -> {
            List<WebElement> encontradas = navegador.findElements(Selectores.OPCIONES_DE_LISTA);
            return encontradas.isEmpty() ? null : encontradas;
        });
        hacerClic(opciones.get(0));
        esperarQueSeCierrenLasListas();
    }
}
