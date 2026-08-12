package com.amex.pf.paginas;

import org.testng.Assert;

/** Pantalla de Catalogos: una lista desplegable con los 8 catalogos. */
public class PaginaCatalogos extends PaginaBase {

    /** Catalogos que muestra hoy la aplicacion (la matriz dice "Versiones"). */
    public static final String[] CATALOGOS = {
            "Nacionalidades", "Profesiones", "Campaña", "Codigo de pais",
            "Productos", "Cuotas Generales Producto", "Dias festivos", "Constantes"
    };

    public PaginaCatalogos abrirCatalogo(String nombre) {
        hacerClic(Selectores.CATALOGO_LISTA);
        hacerClic(Selectores.opcionDeLista(nombre));
        Assert.assertEquals(verVisible(Selectores.CATALOGO_LISTA).getText(), nombre,
                "La lista no quedo en el catalogo \"" + nombre + "\".");
        return this;
    }

    public PaginaCatalogos elBotonAgregarElementoDebeEstarVisible() {
        Assert.assertTrue(estaVisible(Selectores.CATALOGO_BOTON_AGREGAR, 10),
                "No se mostro el boton AGREGAR ELEMENTO.");
        return this;
    }
}
