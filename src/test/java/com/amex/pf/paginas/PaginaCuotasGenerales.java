package com.amex.pf.paginas;

import java.util.List;

import org.openqa.selenium.WebElement;
import org.testng.Assert;

/**
 * Pantalla de Cuotas Generales.
 *
 * Solo lectura: se leen los conceptos y sus importes. NUNCA se presiona Guardar,
 * porque los importes de esta pantalla afectan el calculo de todo el ambiente
 * (ver README seccion 7.5).
 */
public class PaginaCuotasGenerales extends PaginaBase {

    public PaginaCuotasGenerales abrir() {
        esperarQueLaUrlContenga("general-fees");
        verVisible(Selectores.TABLA);
        return this;
    }

    /** Nombre interno de cada importe (ADDITIONAL_MEMBER_FEE, ...). */
    public List<String> conceptos() {
        return campos().stream()
                .map(campo -> campo.getDomAttribute("ng-reflect-name"))
                .filter(nombre -> nombre != null && !nombre.isBlank())
                .toList();
    }

    public List<String> importes() {
        return campos().stream().map(campo -> campo.getDomProperty("value")).toList();
    }

    public PaginaCuotasGenerales losImportesDebenSerEditables() {
        List<WebElement> encontrados = campos();
        Assert.assertFalse(encontrados.isEmpty(),
                "La pantalla de Cuotas Generales no muestra campos de importe.");
        for (WebElement campo : encontrados) {
            Assert.assertTrue(campo.isEnabled(),
                    "El importe \"" + campo.getDomAttribute("ng-reflect-name")
                            + "\" no se puede editar.");
        }
        return this;
    }

    public boolean hayBotonGuardar() {
        return estaVisible(Selectores.CUOTAS_BOTON_GUARDAR, 10);
    }

    public boolean elBotonGuardarEstaHabilitado() {
        return verVisible(Selectores.CUOTAS_BOTON_GUARDAR).isEnabled();
    }

    private List<WebElement> campos() {
        verVisible(Selectores.TABLA);
        return buscarTodos(Selectores.CUOTAS_CAMPOS);
    }
}
