package com.amex.pf.paginas;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;

/** Pantalla de Solicitudes / Expediente: alta de datos del solicitante. */
public class PaginaSolicitudes extends PaginaFormulario {

    /** Abre el formulario de alta (nunca se presiona CREAR SOLICITUD). */
    public PaginaSolicitudes abrirElAlta() {
        hacerClic(Selectores.SOLICITUDES_BOTON_CREAR);
        esperarQueLaUrlContenga("requisitions/add");
        verVisible(Selectores.SOLICITUDES_CAMPO_NOMBRE);
        return this;
    }

    public String valorDelCampo(By campo) {
        return valorDe(campo);
    }

    /** PF_CP_118: las dos opciones de la seccion Adicionales PEP. */
    public List<String> opcionesPep() {
        return buscarTodos(Selectores.SOLICITUDES_OPCIONES_PEP).stream()
                .map(this::textoDe).toList();
    }

    public boolean hayAlgunaOpcionPepMarcada() {
        return !buscarTodos(Selectores.SOLICITUDES_OPCION_PEP_MARCADA).isEmpty();
    }

    /** PF_CP_119: elige "con adicionales" para que aparezca la seccion de PEP. */
    public PaginaSolicitudes elegirLaOpcionPep(int numero) {
        List<WebElement> opciones = buscarTodos(Selectores.SOLICITUDES_OPCIONES_PEP);
        Assert.assertTrue(opciones.size() >= numero,
                "La seccion Adicionales PEP muestra " + opciones.size() + " opciones.");
        opciones.get(numero - 1).click();
        return this;
    }

    public boolean hayCalendarioDeFechaDeNacimiento() {
        return estaVisible(Selectores.SOLICITUDES_CALENDARIO, 5);
    }

    /** PF_CP_120: abre el modal para registrar un adicional PEP. */
    public PaginaSolicitudes abrirElModalDePep() {
        hacerClic(Selectores.SOLICITUDES_BOTON_AGREGAR_PEP);
        verVisible(Selectores.MODAL);
        return this;
    }

    public PaginaSolicitudes cerrarElModal() {
        hacerClic(Selectores.botonDelModal("CANCELAR"));
        esperarQueDesaparezca(Selectores.MODAL);
        return this;
    }
}
