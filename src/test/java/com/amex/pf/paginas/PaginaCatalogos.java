package com.amex.pf.paginas;

import java.util.List;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.testng.Assert;

import com.amex.pf.base.Configuracion;

/** Pantalla de Catalogos: una lista desplegable con los catalogos del sistema. */
public class PaginaCatalogos extends PaginaBase {

    /**
     * Catalogos que se esperan en la lista. Se pueden cambiar sin tocar el codigo,
     * con la propiedad amex.catalogos de configuracion.properties (separados por
     * coma) o con -Damex.catalogos="Nacionalidades,Profesiones,...".
     */
    public static String[] catalogosEsperados() {
        return Configuracion.obtener("amex.catalogos").split("\\s*,\\s*");
    }

    /** Abre la lista y devuelve los nombres de los catalogos que muestra hoy. */
    public List<String> catalogosDeLaLista() {
        List<String> nombres = abrirLaLista().stream()
                .map(opcion -> opcion.getText().trim()).toList();
        cerrarOverlayConEscape();
        return nombres;
    }

    /** PF_CP_046: la lista debe contener los catalogos indicados. */
    public PaginaCatalogos laListaDebeContener(String... esperados) {
        List<String> disponibles = catalogosDeLaLista();
        List<String> faltantes = java.util.Arrays.stream(esperados)
                .filter(esperado -> disponibles.stream()
                        .noneMatch(actual -> actual.equalsIgnoreCase(esperado.trim())))
                .toList();
        Assert.assertTrue(faltantes.isEmpty(),
                "Faltan catalogos en la lista: " + faltantes
                        + ". La aplicacion muestra hoy: " + disponibles + ".");
        return this;
    }

    private List<WebElement> abrirLaLista() {
        hacerClic(Selectores.CATALOGO_LISTA);
        return espera().until(
                navegador -> {
                    List<WebElement> encontradas = navegador.findElements(Selectores.OPCIONES_DE_LISTA);
                    return encontradas.isEmpty() ? null : encontradas;
                });
    }

    public PaginaCatalogos abrirCatalogo(String nombre) {
        List<WebElement> opciones = abrirLaLista();

        WebElement opcion = opciones.stream()
                .filter(elemento -> elemento.getText().trim().equalsIgnoreCase(nombre.trim()))
                .findFirst()
                .orElse(null);

        if (opcion == null) {
            // Mensaje util para el tester: dice que catalogos SI existen hoy.
            String disponibles = opciones.stream().map(WebElement::getText)
                    .reduce((a, b) -> a + " | " + b).orElse("(ninguno)");
            Assert.fail("El catalogo \"" + nombre + "\" no aparece en la lista. "
                    + "La aplicacion muestra hoy: " + disponibles + ". "
                    + "Si el catalogo cambio de nombre o ya no existe, actualice "
                    + "amex.catalogos en configuracion.properties.");
        }

        // La lista puede tener mas opciones de las que caben en pantalla.
        ((JavascriptExecutor) navegador())
                .executeScript("arguments[0].scrollIntoView({block: 'center'});", opcion);
        opcion.click();

        Assert.assertEquals(verVisible(Selectores.CATALOGO_LISTA).getText().trim(), nombre,
                "La lista no quedo en el catalogo \"" + nombre + "\".");
        return this;
    }

    public PaginaCatalogos elBotonAgregarElementoDebeEstarVisible() {
        Assert.assertTrue(estaVisible(Selectores.CATALOGO_BOTON_AGREGAR, 10),
                "No se mostro el boton AGREGAR ELEMENTO.");
        return this;
    }
}
