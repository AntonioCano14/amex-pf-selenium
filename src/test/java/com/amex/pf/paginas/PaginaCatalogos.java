package com.amex.pf.paginas;

import java.util.Arrays;
import java.util.List;

import org.openqa.selenium.By;
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

    /** Abre la pantalla y espera a que la aplicacion termine de navegar. */
    public PaginaCatalogos abrir() {
        esperarQueLaUrlContenga("expedient/catalogs");
        verVisible(Selectores.CATALOGO_LISTA);
        return this;
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
        List<WebElement> opciones = abrirLista(Selectores.CATALOGO_LISTA);

        WebElement opcion = opciones.stream()
                .filter(elemento -> textoDe(elemento).equalsIgnoreCase(nombre.trim()))
                .findFirst()
                .orElse(null);

        if (opcion == null) {
            // Mensaje util para el tester: dice que catalogos SI existen hoy.
            String disponibles = opciones.stream().map(this::textoDe)
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
        esperarQueSeCierrenLasListas();

        Assert.assertEquals(textoDe(Selectores.CATALOGO_LISTA), nombre,
                "La lista no quedo en el catalogo \"" + nombre + "\".");
        return this;
    }

    public PaginaCatalogos elBotonAgregarElementoDebeEstarVisible() {
        Assert.assertTrue(estaVisible(Selectores.CATALOGO_BOTON_AGREGAR, 10),
                "No se mostro el boton AGREGAR ELEMENTO.");
        return this;
    }

    /**
     * Abre el modal de AGREGAR ELEMENTO. Solo consulta: el caso que lo usa cierra el
     * modal con CANCELAR y nunca presiona ACEPTAR.
     */
    public PaginaCatalogos abrirElAltaDeElemento() {
        hacerClic(Selectores.CATALOGO_BOTON_AGREGAR);
        verVisible(Selectores.MODAL);
        return this;
    }

    /** Abre el detalle (ojo de la columna Ver) del primer registro de la tabla. */
    public PaginaCatalogos abrirElDetalleDelPrimerRegistro() {
        List<WebElement> ojos = buscarTodos(Selectores.CATALOGO_VER_DETALLE);
        Assert.assertFalse(ojos.isEmpty(),
                "La tabla del catalogo no muestra el boton Ver de ningun registro.");
        ojos.get(0).click();
        verVisible(Selectores.MODAL);
        return this;
    }

    public PaginaCatalogos elModalDebeTenerLosCampos(String... placeholders) {
        for (String placeholder : placeholders) {
            Assert.assertTrue(estaVisible(Selectores.campoDelModal(placeholder), 10),
                    "El modal no muestra el campo \"" + placeholder + "\". Muestra: "
                            + textoDe(Selectores.MODAL).replace("\n", " | ") + ".");
        }
        return this;
    }

    public PaginaCatalogos elModalDebeTenerElBoton(String etiqueta) {
        Assert.assertTrue(estaVisible(Selectores.botonDelModal(etiqueta), 10),
                "El modal no muestra el boton \"" + etiqueta + "\".");
        return this;
    }

    public PaginaCatalogos elBotonCerrarDelModalDebeEstarVisible() {
        Assert.assertTrue(estaVisible(Selectores.MODAL_BOTON_CERRAR, 10),
                "El modal no muestra el boton de cerrar (X).");
        return this;
    }

    public PaginaCatalogos losCamposDelDetalleDebenSerDeSoloLectura() {
        List<WebElement> campos = buscarTodos(Selectores.MODAL_CAMPOS).stream()
                .filter(campo -> !"hidden".equals(campo.getDomProperty("type")))
                .toList();
        Assert.assertFalse(campos.isEmpty(), "El detalle no muestra ningun campo.");
        for (WebElement campo : campos) {
            Assert.assertFalse(campo.isEnabled(),
                    "El campo \"" + campo.getDomProperty("placeholder")
                            + "\" del detalle deberia ser de solo lectura.");
            Assert.assertFalse(campo.getDomProperty("value").isBlank(),
                    "El campo \"" + campo.getDomProperty("placeholder")
                            + "\" del detalle no muestra ningun valor.");
        }
        return this;
    }

    /** Cuantos caracteres acepta un campo del modal (se escribe de mas a proposito). */
    public int cuantosCaracteresAceptaElCampo(String placeholder, int maximoEsperado) {
        return valorQueQuedaEnElCampo(placeholder, textoDePrueba(maximoEsperado + 5)).length();
    }

    /** Cuantos digitos acepta un campo con mascara, como la fecha dd/mm/aaaa. */
    public int cuantosDigitosAceptaElCampo(String placeholder, int digitosEsperados) {
        String numeros = "1234567890".repeat(1 + (digitosEsperados + 5) / 10);
        return valorQueQuedaEnElCampo(placeholder, numeros.substring(0, digitosEsperados + 5))
                .replaceAll("\\D", "").length();
    }

    public String valorQueQuedaEnElCampo(String placeholder, String texto) {
        By campo = Selectores.campoDelModal(placeholder);
        escribir(campo, texto);
        String valor = valorDe(campo);
        return valor == null ? "" : valor;
    }

    public PaginaCatalogos elModalDebeTenerCalendario() {
        Assert.assertTrue(estaVisible(Selectores.CALENDARIO, 10),
                "El modal no muestra el boton del calendario.");
        return this;
    }

    public boolean tieneElCampo(String placeholder) {
        return estaVisible(Selectores.campoDelModal(placeholder), 3);
    }

    public boolean tieneCalendario() {
        return estaVisible(Selectores.CALENDARIO, 3);
    }

    public PaginaCatalogos abrirElCalendario() {
        hacerClic(Selectores.CALENDARIO);
        verVisible(Selectores.CALENDARIO_ABIERTO);
        return this;
    }

    /** PF_CP_088: solo deben poder elegirse las fechas de hoy en adelante. */
    public PaginaCatalogos soloDebeHabilitarDesdeHoy() {
        List<WebElement> dias = buscarTodos(Selectores.CALENDARIO_DIAS);
        Assert.assertFalse(dias.isEmpty(), "El calendario no muestra dias.");

        int posicionDeHoy = -1;
        for (int i = 0; i < dias.size(); i++) {
            // La clase de "hoy" esta en el contenido de la celda, no en la celda.
            if (!dias.get(i).findElements(Selectores.MARCA_DEL_DIA_DE_HOY).isEmpty()) {
                posicionDeHoy = i;
                break;
            }
        }
        Assert.assertTrue(posicionDeHoy >= 0,
                "El calendario no abrio en el mes de hoy, no se puede validar la regla.");

        for (int i = 0; i < dias.size(); i++) {
            boolean deshabilitado = "true".equals(dias.get(i).getDomAttribute("aria-disabled"));
            String dia = textoDe(dias.get(i));
            if (i < posicionDeHoy) {
                Assert.assertTrue(deshabilitado,
                        "El dia " + dia + " es anterior a hoy y deberia estar deshabilitado.");
            } else {
                Assert.assertFalse(deshabilitado,
                        "El dia " + dia + " es hoy o posterior y deberia poder elegirse.");
            }
        }
        return this;
    }

    public PaginaCatalogos cerrarElCalendario() {
        cerrarOverlayConEscape();
        esperarQueDesaparezca(Selectores.CALENDARIO_ABIERTO);
        return this;
    }

    public PaginaCatalogos cerrarElModal() {
        cerrarModalSiEstaAbierto();
        esperarQueDesaparezca(Selectores.MODAL);
        return this;
    }

    private String textoDePrueba(int cantidad) {
        String base = "ABCDEFGHIJ0123456789";
        StringBuilder texto = new StringBuilder();
        while (texto.length() < cantidad) {
            texto.append(base.charAt(texto.length() % base.length()));
        }
        return texto.substring(0, cantidad);
    }
}
