package com.amex.pf.paginas;

import org.openqa.selenium.By;

/** Pantalla de Solicitudes / Expediente: alta de datos del solicitante. */
public class PaginaSolicitudes extends PaginaBase {

    private static final String LETRAS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final String NUMEROS = "0123456789";

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
        escribir(campo, texto.substring(0, cantidad));
        return valorDe(campo).length();
    }

    public String valorDelCampo(By campo) {
        return valorDe(campo);
    }
}
