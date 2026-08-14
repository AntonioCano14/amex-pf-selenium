package com.amex.pf.paginas;

import java.util.List;

import org.openqa.selenium.By;
import org.testng.Assert;

/**
 * Pantalla de Tasas de interes, solo consultas: se elige tipo de tasa, anio y mes
 * y se revisa lo que muestra la pantalla. Nunca se presiona AGREGAR.
 */
public class PaginaTasas extends PaginaBase {

    /** Abre la pantalla y espera a que la aplicacion termine de navegar. */
    public PaginaTasas abrir() {
        esperarQueLaUrlContenga("expedient/rates");
        verVisible(Selectores.TASAS_LISTA_TIPO);
        return this;
    }

    public PaginaTasas elegirTipoDeTasa(String tipo) {
        elegirDeLaLista(Selectores.TASAS_LISTA_TIPO, tipo);
        return this;
    }

    /** Elige el primer anio y el primer mes que ofrecen las listas. */
    public PaginaTasas elegirElPrimerPeriodo() {
        elegirLaPrimeraOpcion(Selectores.TASAS_LISTA_ANIO);
        elegirLaPrimeraOpcion(Selectores.TASAS_LISTA_MES);
        return this;
    }

    public List<String> tiposDeTasa() {
        return opcionesDeLaLista(Selectores.TASAS_LISTA_TIPO);
    }

    public PaginaTasas debeVerseElTexto(String texto) {
        Assert.assertTrue(estaVisible(Selectores.textoVisible(texto), 15),
                "La pantalla de Tasas de interes no muestra \"" + texto + "\".");
        return this;
    }

    public PaginaTasas elCampoDebeEstarVisible(By campo) {
        Assert.assertTrue(estaVisible(campo, 15),
                "La pantalla no muestra el campo " + campo + ".");
        return this;
    }

    /** Cuantos digitos deja escribir un campo de porcentaje (se escribe de mas). */
    public int cuantosDigitosAcepta(By campo, int digitosEsperados) {
        String numeros = "1234567890".repeat(1 + (digitosEsperados + 5) / 10);
        escribir(campo, numeros.substring(0, digitosEsperados + 5));
        String valor = valorDe(campo);
        return valor == null ? 0 : valor.replaceAll("\\D", "").length();
    }

    public String loQueAcepta(By campo, String texto) {
        escribir(campo, texto);
        String valor = valorDe(campo);
        return valor == null ? "" : valor;
    }

    private void elegirLaPrimeraOpcion(By lista) {
        List<String> opciones = opcionesDeLaLista(lista);
        Assert.assertFalse(opciones.isEmpty(), "La lista " + lista + " no tiene opciones.");
        elegirDeLaLista(lista, opciones.get(0));
    }
}
