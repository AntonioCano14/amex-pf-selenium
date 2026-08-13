package com.amex.pf.paginas;

import java.util.Arrays;
import java.util.List;

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
        return Configuracion.lista("amex.catalogos");
    }

    /** Abre la lista y devuelve los nombres de los catalogos que muestra hoy. */
    public List<String> catalogosDeLaLista() {
        return opcionesDeLaLista(Selectores.CATALOGO_LISTA);
    }

    /** PF_CP_046: la lista debe contener los catalogos indicados. */
    public PaginaCatalogos laListaDebeContener(String... esperados) {
        List<String> disponibles = catalogosDeLaLista();
        List<String> faltantes = Arrays.stream(esperados)
                .filter(esperado -> disponibles.stream()
                        .noneMatch(actual -> actual.equalsIgnoreCase(esperado.trim())))
                .toList();
        Assert.assertTrue(faltantes.isEmpty(),
                "Faltan catalogos en la lista: " + faltantes
                        + ". La aplicacion muestra hoy: " + disponibles + ".");
        return this;
    }

    public PaginaCatalogos abrirCatalogo(String nombre) {
        try {
            elegirDeLaLista(Selectores.CATALOGO_LISTA, nombre);
        } catch (IllegalArgumentException noExiste) {
            // Mensaje util para el tester: dice que catalogos SI existen hoy.
            Assert.fail("El catalogo \"" + nombre + "\" no aparece en la lista. "
                    + "La aplicacion muestra hoy: " + catalogosDeLaLista() + ". "
                    + "Si el catalogo cambio de nombre o ya no existe, actualice "
                    + "amex.catalogos en configuracion.properties.");
        }
        Assert.assertEquals(textoDe(verVisible(Selectores.CATALOGO_LISTA)), nombre,
                "La lista no quedo en el catalogo \"" + nombre + "\".");
        return this;
    }

    public PaginaCatalogos elBotonAgregarElementoDebeEstarVisible() {
        Assert.assertTrue(estaVisible(Selectores.CATALOGO_BOTON_AGREGAR, 10),
                "No se mostro el boton AGREGAR ELEMENTO.");
        return this;
    }
}
