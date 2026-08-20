package com.amex.pf.paginas;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

/**
 * Acciones comunes a los formularios de la aplicacion (alta de solicitud y alta
 * de usuario): sirven para los casos de longitud maxima, tipo de caracter y
 * campos obligatorios de la matriz.
 *
 * Ninguna de estas acciones guarda informacion: escriben en los campos y leen lo
 * que el formulario acepto.
 */
public abstract class PaginaFormulario extends PaginaBase {

    protected static final String LETRAS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    protected static final String NUMEROS = "0123456789";

    /**
     * Escribe mas caracteres de los permitidos y devuelve cuantos acepto el campo.
     * Sirve para los casos de longitud maxima de la matriz.
     */
    public int cuantosCaracteresAcepta(By campo, int maximoEsperado, String tipo) {
        String base = tipo.equals("numeros") ? NUMEROS : LETRAS;
        int cantidad = maximoEsperado + 5;
        StringBuilder texto = new StringBuilder();
        while (texto.length() < cantidad) {
            texto.append(base.charAt(texto.length() % base.length()));
        }
        return loQueAcepta(campo, texto.substring(0, cantidad)).length();
    }

    /** Escribe un texto y devuelve lo que quedo en el campo (el resto lo filtro la app). */
    public String loQueAcepta(By campo, String texto) {
        escribir(campo, texto);
        String valor = valorDe(campo);
        return valor == null ? "" : valor;
    }

    /** Solo los digitos de lo que muestra el campo: el CUIL se ve con mascara 20-12345678-9. */
    public int cuantosDigitosAcepta(By campo, int maximoEsperado) {
        String valor = loQueAcepta(campo, NUMEROS.repeat(1 + (maximoEsperado + 5) / NUMEROS.length())
                .substring(0, maximoEsperado + 5));
        return valor.replaceAll("\\D", "").length();
    }

    /**
     * Vacia el campo borrando caracter por caracter. No usa clear(): al limpiar asi
     * el formulario no se entera del cambio y sus validaciones (campo obligatorio,
     * boton GUARDAR deshabilitado) no se disparan.
     */
    public void limpiar(By campo) {
        WebElement elemento = verVisible(campo);
        String valor = elemento.getDomProperty("value");
        int cuantos = valor == null ? 0 : valor.length();
        for (int i = 0; i < cuantos; i++) {
            elemento.sendKeys(Keys.BACK_SPACE);
        }
    }

    /** Saca el foco del campo para que se muestren las validaciones del formulario. */
    public void salirDelCampo(By campo) {
        verVisible(campo).sendKeys(Keys.TAB);
    }

    public boolean elBotonEstaDeshabilitado(By boton) {
        return !verVisible(boton).isEnabled();
    }

    /**
     * Si el formulario marco el campo como invalido. Angular Material pone
     * aria-invalid="true" en el input cuando su validacion no pasa (es lo que
     * pinta el campo en rojo), asi que sirve para cualquier campo del alta.
     */
    public boolean elCampoTieneErrorDeFormato(By campo) {
        return "true".equals(verVisible(campo).getDomAttribute("aria-invalid"));
    }
}
