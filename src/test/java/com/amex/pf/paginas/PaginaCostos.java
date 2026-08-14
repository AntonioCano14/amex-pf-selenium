package com.amex.pf.paginas;

import java.util.List;

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

    private void elegirLaPrimeraOpcion(WebElement lista) {
        esperarQueSeCierrenLasListas();
        lista.click();
        List<WebElement> opciones = espera().until(navegador -> {
            List<WebElement> encontradas = navegador.findElements(Selectores.OPCIONES_DE_LISTA);
            return encontradas.isEmpty() ? null : encontradas;
        });
        opciones.get(0).click();
        esperarQueSeCierrenLasListas();
    }
}
