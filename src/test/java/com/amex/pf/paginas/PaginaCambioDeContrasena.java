package com.amex.pf.paginas;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;

import com.amex.pf.base.Configuracion;

/**
 * Pantalla "Cambio de contraseña" (menu Hola, [usuario] -> Cambiar contraseña).
 *
 * Ninguna accion de esta clase guarda el cambio: los casos de la matriz solo
 * verifican los requisitos, el ojo de ver/ocultar y el estado del boton GUARDAR.
 * Para salir se usa siempre CANCELAR.
 */
public class PaginaCambioDeContrasena extends PaginaFormulario {

    public static final String TITULO = "Cambio de contraseña";
    private static final String FRAGMENTO_DE_LA_URL = "expedient/password-change";

    /** Abre el menu del usuario del encabezado. */
    public PaginaCambioDeContrasena abrirElMenuDelUsuario() {
        hacerClic(Selectores.MENU_USUARIO);
        verVisible(Selectores.OPCION_SALIR);
        return this;
    }

    /** Entra a la pantalla desde el menu del usuario. */
    public PaginaCambioDeContrasena abrir() {
        abrirElMenuDelUsuario();
        hacerClic(Selectores.OPCION_CAMBIAR_CONTRASENA);
        esperarQueLaUrlContenga(FRAGMENTO_DE_LA_URL);
        verVisible(Selectores.CONTRASENA_ACTUAL);
        return this;
    }

    public PaginaCambioDeContrasena elMenuDebeMostrarLaOpcion(String etiqueta) {
        Assert.assertTrue(estaVisible(Selectores.boton(etiqueta),
                        Configuracion.esperaMaximaSegundos()),
                "El menu del usuario no muestra la opcion \"" + etiqueta + "\".");
        return this;
    }

    /**
     * Verifica que la opcion del menu traiga su icono. La aplicacion los dibuja
     * con mat-icon, cuyo texto es el nombre del icono (por ejemplo lock_reset).
     */
    public PaginaCambioDeContrasena laOpcionDebeTraerIcono(String etiqueta) {
        WebElement opcion = verVisible(Selectores.boton(etiqueta));
        Assert.assertFalse(opcion.findElements(By.tagName("mat-icon")).isEmpty(),
                "La opcion \"" + etiqueta + "\" del menu no muestra su icono.");
        return this;
    }

    public PaginaCambioDeContrasena elTituloDebeSer(String titulo) {
        Assert.assertTrue(estaVisible(Selectores.textoVisible(titulo),
                        Configuracion.esperaMaximaSegundos()),
                "La pantalla no muestra el titulo \"" + titulo + "\".");
        return this;
    }

    /**
     * Verifica que la pantalla liste los requisitos de seguridad de la matriz.
     * La lista se declara en configuracion.properties (amex.contrasena.requisitos)
     * para ajustarla sin tocar el codigo.
     */
    public PaginaCambioDeContrasena debenMostrarseLosRequisitos(String... requisitos) {
        String pantalla = textoDe(By.tagName("body"));
        for (String requisito : requisitos) {
            Assert.assertTrue(pantalla.contains(requisito),
                    "La pantalla no muestra el requisito \"" + requisito + "\".");
        }
        return this;
    }

    public PaginaCambioDeContrasena losCamposDebenEstarVisibles() {
        verVisible(Selectores.CONTRASENA_ACTUAL);
        verVisible(Selectores.CONTRASENA_NUEVA);
        verVisible(Selectores.CONTRASENA_CONFIRMAR);
        return this;
    }

    public PaginaCambioDeContrasena escribirEnElCampo(By campo, String texto) {
        WebElement elemento = verVisible(campo);
        elemento.clear();
        elemento.sendKeys(texto);
        salirDelCampo(campo);
        return this;
    }

    /**
     * Entra al campo, escribe y borra: asi el formulario lo marca como tocado y
     * pinta el campo en rojo (con clear() Angular no se entera del cambio).
     */
    public PaginaCambioDeContrasena tocarYDejarVacio(By campo) {
        WebElement elemento = verVisible(campo);
        elemento.click();
        elemento.sendKeys("x");
        limpiar(campo);
        salirDelCampo(campo);
        return this;
    }

    /**
     * Si el formulario pinto el campo en rojo. En esta pantalla el input no usa
     * aria-invalid, la marca la pone Angular Material en el contenedor
     * (mat-form-field-invalid), que es la clase que dibuja el borde rojo.
     */
    public boolean elCampoSeMarcoEnRojo(By campo) {
        WebElement contenedor = verVisible(campo).findElement(By.xpath("ancestor::mat-form-field"));
        String clases = contenedor.getDomAttribute("class");
        return clases != null && clases.contains("mat-form-field-invalid");
    }

    /** Tipo del input: password cuando la contrasena esta oculta, text cuando se ve. */
    public String comoSeMuestraElCampo(By campo) {
        return verVisible(campo).getDomAttribute("type");
    }

    /** Presiona el ojo (boton de sufijo) del campo indicado. */
    public PaginaCambioDeContrasena presionarElOjo(By campo) {
        WebElement contenedor = verVisible(campo).findElement(By.xpath("ancestor::mat-form-field"));
        hacerClic(contenedor.findElement(By.cssSelector("button[matsuffix]")));
        return this;
    }

    public boolean elBotonCancelarEstaVisible() {
        return estaVisible(Selectores.BOTON_CANCELAR_CONTRASENA, Configuracion.esperaMaximaSegundos());
    }

    public boolean elBotonGuardarEstaHabilitado() {
        return verVisible(Selectores.BOTON_GUARDAR_CONTRASENA).isEnabled();
    }

    /** Sale de la pantalla sin guardar. Se llama siempre al terminar el caso. */
    public void cancelar() {
        hacerClic(Selectores.BOTON_CANCELAR_CONTRASENA);
        esperarQueLaUrlYaNoContenga(FRAGMENTO_DE_LA_URL);
    }

    /** Cancela si el caso quedo dentro de la pantalla (limpieza de @AfterMethod). */
    public void cancelarSiSigueEnLaPantalla() {
        try {
            if (navegador().getCurrentUrl().contains(FRAGMENTO_DE_LA_URL)) {
                cancelar();
            }
        } catch (RuntimeException noSePudoCancelar) {
            System.err.println("AVISO: no se pudo salir de la pantalla de cambio de contrasena: "
                    + noSePudoCancelar.getMessage());
        }
    }
}
