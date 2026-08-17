package com.amex.pf.paginas;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.testng.Assert;

import com.amex.pf.base.Configuracion;
import com.amex.pf.datos.ElementoDeCatalogo;

/** Pantalla de Catalogos: una lista desplegable con los catalogos del sistema. */
public class PaginaCatalogos extends PaginaBase {

    /** Tope de paginas que se recorren buscando una fila (los catalogos mas grandes tienen ~20). */
    private static final int MAXIMO_DE_PAGINAS = 60;

    /**
     * Catalogos que se esperan en la lista. Se pueden cambiar sin tocar el codigo,
     * con la propiedad amex.catalogos de configuracion.properties (separados por
     * coma) o con -Damex.catalogos="Nacionalidades,Profesiones,...".
     */
    public static String[] catalogosEsperados() {
        return Configuracion.lista("amex.catalogos");
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
            Assert.assertTrue(estaVisible(Selectores.campoDelModalPorPlaceholder(placeholder), 10),
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
        By campo = Selectores.campoDelModalPorPlaceholder(placeholder);
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
        return estaVisible(Selectores.campoDelModalPorPlaceholder(placeholder), 3);
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

    // ------------------------------------------- Altas, ediciones y bajas (ola 5)

    /**
     * Da de alta un elemento: llena los campos que pide el modal de ese catalogo y
     * presiona ACEPTAR. Los catalogos que piden imagen usan la imagen de prueba de
     * src/test/resources/datos.
     */
    public PaginaCatalogos agregarElemento(ElementoDeCatalogo elemento) {
        hacerClic(Selectores.CATALOGO_BOTON_AGREGAR);
        verVisible(Selectores.MODAL);

        if (elemento.pideImagen()) {
            cargarLaImagenDePrueba();
        }
        if (elemento.seEligeConCalendario()) {
            elegirElDiaDelMes(0, ElementoDeCatalogo.anio(), ElementoDeCatalogo.mes(),
                    elemento.diaDelAlta());
        }
        elemento.campos().forEach((campo, valor) ->
                escribir(Selectores.campoDelModalPorPlaceholder(campo), valor));

        aceptarElModal();
        return this;
    }

    /** PF_CP_051 y equivalentes: abre el detalle, presiona EDITAR DATOS y guarda. */
    public PaginaCatalogos editarElElemento(ElementoDeCatalogo elemento) {
        abrirElDetalleDe(elemento.identificador());
        hacerClic(Selectores.CATALOGO_BOTON_EDITAR);

        if (elemento.seEligeConCalendario()) {
            elegirElDiaDelMes(0, ElementoDeCatalogo.anio(), ElementoDeCatalogo.mes(),
                    elemento.diaDeLaEdicion());
        } else {
            escribir(Selectores.campoDelModalPorPlaceholder(elemento.campoQueSeEdita()),
                    elemento.valorEditado());
        }

        aceptarElModal();
        return this;
    }

    /** Abre el detalle (ojo) de la fila que contiene el texto indicado. */
    public PaginaCatalogos abrirElDetalleDe(String texto) {
        WebElement fila = buscarLaFila(texto);
        List<WebElement> ojos = fila.findElements(Selectores.CATALOGO_VER_DETALLE);
        Assert.assertFalse(ojos.isEmpty(),
                "La fila \"" + texto + "\" no muestra el boton Ver.");
        hacerClic(ojos.get(0));
        verVisible(Selectores.MODAL);
        return this;
    }

    /**
     * PF_CP_052 y equivalentes: presiona Inactivar en la fila y acepta el popup de
     * confirmacion.
     */
    public PaginaCatalogos inactivarElElemento(String texto) {
        WebElement fila = buscarLaFila(texto);
        List<WebElement> iconos = fila.findElements(Selectores.CATALOGO_INACTIVAR_DE_LA_FILA);
        Assert.assertFalse(iconos.isEmpty(),
                "La fila \"" + texto + "\" no muestra el boton Inactivar.");
        hacerClic(iconos.get(0));
        verVisible(Selectores.MODAL);
        return this;
    }

    /** Texto del popup de confirmacion que se muestra al inactivar. */
    public String textoDelPopup() {
        return textoDe(Selectores.MODAL).replace("\n", " ");
    }

    /** PF_CP_053 y equivalentes: abre el detalle del registro inactivo y lo activa. */
    public PaginaCatalogos activarElElemento(String texto) {
        abrirElDetalleDe(texto);
        Assert.assertTrue(estaVisible(Selectores.CATALOGO_BOTON_ACTIVAR, 10),
                "El detalle del registro inactivo no muestra el boton ACTIVAR REGISTRO.");
        hacerClic(Selectores.CATALOGO_BOTON_ACTIVAR);
        esperarQueDesaparezca(Selectores.MODAL);
        esperarQueTermineDeCargar();
        return this;
    }

    public PaginaCatalogos aceptarElModal() {
        hacerClic(Selectores.MODAL_BOTON_ACEPTAR_MAYUSCULAS);
        // Si el servicio rechaza los datos, la aplicacion cambia el modal por un aviso
        // de error: se informa tal cual, para no esperar en vano a que se cierre.
        String error = espera().until(navegador -> {
            List<WebElement> modales = navegador.findElements(Selectores.MODAL);
            if (modales.isEmpty()) {
                return "";
            }
            String texto = textoDe(modales.get(0));
            return texto.contains("error") ? texto.replace("\n", " ") : null;
        });
        Assert.assertTrue(error.isEmpty(),
                "La aplicacion no guardo el elemento: " + error + ".");
        esperarQueTermineDeCargar();
        return this;
    }

    /**
     * La fila debe estar en la tabla con el estatus indicado ("ACTIVO"/"INACTIVO").
     * La tabla del catalogo no tiene filtro, por eso se recorren sus paginas.
     */
    public PaginaCatalogos laTablaDebeMostrar(String texto, String estatus) {
        String esperado = estatus.toUpperCase();
        String fila;
        try {
            // La tabla tarda en reflejar el alta o el cambio de estatus.
            fila = espera().until(navegador -> {
                WebElement encontrada = buscarLaFilaSiExiste(texto);
                if (encontrada == null) {
                    return null;
                }
                String contenido = textoDe(encontrada);
                return estatusDeLaFila(contenido).equals(esperado) ? contenido : null;
            });
        } catch (TimeoutException noQuedoAsi) {
            fila = textoDe(buscarLaFila(texto));
        }
        Assert.assertEquals(estatusDeLaFila(fila), esperado,
                "La fila \"" + texto + "\" deberia estar " + estatus
                        + " y la tabla muestra: " + fila.replace("\n", " ") + ".");
        return this;
    }

    /** Busca la fila que contiene el texto recorriendo todas las paginas de la tabla. */
    public WebElement buscarLaFila(String texto) {
        WebElement fila;
        try {
            fila = espera().until(navegador -> buscarLaFilaSiExiste(texto));
        } catch (TimeoutException noAparecio) {
            fila = null;
        }
        Assert.assertNotNull(fila, "La tabla del catalogo \"" + textoDe(Selectores.CATALOGO_LISTA)
                + "\" no muestra ninguna fila con \"" + texto + "\" en ninguna de sus paginas.");
        return fila;
    }

    /** Igual que buscarLaFila, pero devuelve null si no existe (para la limpieza). */
    public WebElement buscarLaFilaSiExiste(String texto) {
        return leerAunqueLaTablaSeRefresqueOpcional(() -> {
            for (int pagina = 1; pagina <= MAXIMO_DE_PAGINAS; pagina++) {
                esperarQueTermineDeCargar();
                WebElement encontrada = filaDeLaPaginaActual(texto);
                if (encontrada != null) {
                    return encontrada;
                }
                if (!irALaPagina(pagina + 1)) {
                    return null;
                }
            }
            return null;
        }).orElse(null);
    }

    /**
     * Deja el ambiente como estaba: inactiva el elemento creado por la prueba. No
     * falla si ya no existe o si ya estaba inactivo.
     */
    public PaginaCatalogos inactivarSiQuedoActivo(String texto) {
        try {
            WebElement fila = buscarLaFilaSiExiste(texto);
            if (fila == null || !textoDe(fila).toUpperCase().contains("ACTIVO")
                    || textoDe(fila).toUpperCase().contains("INACTIVO")) {
                return this;
            }
            inactivarElElemento(texto);
            aceptarElModal();
        } catch (RuntimeException noSePudo) {
            System.out.println("            Aviso: no se pudo inactivar \"" + texto
                    + "\" al terminar (" + noSePudo.getClass().getSimpleName() + ").");
        }
        return this;
    }

    private WebElement filaDeLaPaginaActual(String texto) {
        List<WebElement> filas;
        try {
            filas = filasConDatos();
        } catch (TimeoutException tablaSinFilas) {
            // El catalogo puede no tener ningun elemento todavia: no es una fila que falte.
            return null;
        }
        return filas.stream()
                .filter(fila -> textoDe(fila).contains(texto))
                .findFirst()
                .orElse(null);
    }

    /** Va a la pagina indicada de la tabla; devuelve false si esa pagina no existe. */
    private boolean irALaPagina(int numero) {
        WebElement boton = buscarTodos(Selectores.PAGINA_DE_LA_TABLA).stream()
                .filter(candidato -> textoDe(candidato).equals(String.valueOf(numero)))
                .findFirst()
                .orElse(null);
        if (boton == null) {
            return false;
        }
        hacerClic(boton);
        esperarQueTermineDeCargar();
        return true;
    }

    private void cargarLaImagenDePrueba() {
        Path imagen = Paths.get(System.getProperty("user.dir"),
                "src", "test", "resources", "datos", "imagen.png");
        Assert.assertTrue(Files.exists(imagen),
                "Falta la imagen de prueba " + imagen + " que piden los catalogos con imagen.");
        verVisible(Selectores.MODAL);
        // El input de archivo esta oculto: se le escribe la ruta directamente.
        buscarTodos(Selectores.MODAL_CAMPO_ARCHIVO).get(0).sendKeys(imagen.toString());
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
