package com.amex.pf.pruebas;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.amex.pf.base.PruebaBase;
import com.amex.pf.paginas.PaginaLogin;
import com.amex.pf.paginas.PaginaPrincipal;
import com.amex.pf.paginas.PaginaTasas;
import com.amex.pf.paginas.Selectores;

/**
 * OLA 5 - Campos de Costo Financiero Total (PF_CP_107).
 *
 * No escribe en el ambiente: se escribe en el campo para medir que acepta, pero
 * nunca se presiona AGREGAR ni ACTUALIZAR.
 *
 * PF_CP_103 (editar el porcentaje de una tasa) y PF_CP_106 (agregar el porcentaje
 * del costo financiero total) quedan fuera de esta ola: modifican tasas reales del
 * ambiente y necesitan un periodo de prueba acordado con negocio (ver README 7.4).
 */
public class TasasCftPruebas extends PruebaBase {

    /** La matriz pide 9 caracteres numericos en los campos de costo financiero total. */
    private static final int CARACTERES_ESPERADOS = 9;

    private PaginaPrincipal inicio;

    @BeforeMethod(alwaysRun = true)
    public void iniciarSesion() {
        inicio = new PaginaLogin().iniciarSesionConCredencialesValidas();
    }

    @Test(groups = {"ola5", "tasas"},
            description = "PF_CP_107 Los campos de Costo Financiero Total solo aceptan numeros y "
                    + "hasta 9 caracteres")
    public void pfCp107CaracteresDelCostoFinancieroTotal() {
        inicio.irAlMenu("Tasas de interes");

        PaginaTasas tasas = new PaginaTasas().abrir()
                .elegirTipoDeTasa("Costo Financiero Total")
                .elegirElPrimerPeriodo();

        String conLetras = tasas.loQueAcepta(Selectores.TASAS_CAMPO_CFT_PESOS, "abc");
        Assert.assertTrue(conLetras.replaceAll("[\\d.,]", "").isEmpty(),
                "El campo de costo financiero total acepto letras: \"" + conLetras + "\".");

        int digitos = tasas.cuantosDigitosAcepta(
                Selectores.TASAS_CAMPO_CFT_PESOS, CARACTERES_ESPERADOS);
        Assert.assertTrue(digitos <= CARACTERES_ESPERADOS,
                "El campo de costo financiero total acepto " + digitos + " digitos y la matriz "
                        + "permite " + CARACTERES_ESPERADOS + ".");
    }
}
