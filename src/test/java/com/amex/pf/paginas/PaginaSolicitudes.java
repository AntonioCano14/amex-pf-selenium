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

    // -------------------------------------------- Adicionales PEP (ola 6)

    /**
     * Registra un adicional PEP en el formulario. Solo agrega la fila al formulario
     * que se esta llenando: la solicitud NO se crea, porque nunca se presiona CREAR
     * SOLICITUD.
     */
    public PaginaSolicitudes registrarUnAdicionalPep(String nombre, String apellidos, String dni,
            String cargo, String relacion) {
        abrirElModalDePep();
        escribir(Selectores.PEP_CAMPO_NOMBRE, nombre);
        escribir(Selectores.PEP_CAMPO_APELLIDOS, apellidos);
        escribir(Selectores.PEP_CAMPO_DNI, dni);
        escribir(Selectores.PEP_CAMPO_CARGO, cargo);
        escribir(Selectores.PEP_CAMPO_RELACION, relacion);
        hacerClic(Selectores.PEP_BOTON_ACEPTAR);
        esperarQueDesaparezca(Selectores.MODAL);
        return this;
    }

    public int cuantosAdicionalesPepHay() {
        return buscarTodos(Selectores.PEP_FILAS).size();
    }

    /** Texto de la tabla de adicionales PEP (encabezados y filas). */
    public String tablaDeAdicionalesPep() {
        return textoDe(Selectores.PEP_TABLA);
    }

    public boolean elAdicionalPepTieneSusIconos() {
        return estaVisible(Selectores.PEP_BOTON_EDITAR, 10)
                && estaVisible(Selectores.PEP_BOTON_ELIMINAR, 10);
    }

    /** Abre el adicional PEP registrado y devuelve el nombre que trae el modal. */
    public String abrirLaEdicionDelAdicionalPep() {
        hacerClic(Selectores.PEP_BOTON_EDITAR);
        verVisible(Selectores.MODAL);
        return valorDe(Selectores.PEP_CAMPO_NOMBRE);
    }

    public PaginaSolicitudes eliminarElAdicionalPep() {
        int antes = cuantosAdicionalesPepHay();
        hacerClic(Selectores.PEP_BOTON_ELIMINAR);
        espera().until(navegador -> cuantosAdicionalesPepHay() < antes);
        return this;
    }

    /** PF_CP_122: check "Condicionada a ingresos" del alta de solicitud. */
    public boolean hayCheckCondicionadaAIngresos() {
        return buscarTodos(Selectores.SOLICITUDES_CHECKS).stream()
                .anyMatch(check -> textoDe(check).toLowerCase().contains("condicionada"));
    }

    public int cuantosChecksTieneElFormulario() {
        return buscarTodos(Selectores.SOLICITUDES_CHECKS).size();
    }
}
