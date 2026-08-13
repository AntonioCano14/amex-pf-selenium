package com.amex.pf.paginas;

import java.util.List;

import com.amex.pf.base.Configuracion;

/**
 * Pantalla de Usuarios y su formulario "Agregar usuario" (PF_CP_011 a PF_CP_021).
 *
 * Solo lectura: se escribe en los campos y se leen las listas, pero nunca se
 * presiona GUARDAR REGISTRO, asi que no se crean usuarios.
 */
public class PaginaUsuarios extends PaginaFormulario {

    /** Areas que debe mostrar la lista, configurables en amex.usuario.areas. */
    public static String[] areasEsperadas() {
        return Configuracion.lista("amex.usuario.areas");
    }

    /** Tipos de usuario esperados, configurables en amex.usuario.tipos. */
    public static String[] tiposDeUsuarioEsperados() {
        return Configuracion.lista("amex.usuario.tipos");
    }

    public PaginaUsuarios abrirElAltaDeUsuario() {
        hacerClic(Selectores.USUARIOS_BOTON_AGREGAR);
        esperarQueLaUrlContenga("users/add");
        verVisible(Selectores.USUARIO_CAMPO_NOMBRES);
        return this;
    }

    public List<String> areasDeLaLista() {
        return opcionesDeLaLista(Selectores.USUARIO_LISTA_AREA);
    }

    /**
     * Los tipos de usuario dependen del area: la lista viene vacia hasta que se
     * elige una.
     */
    public List<String> tiposDeUsuarioDeLaLista(String area) {
        elegirDeLaLista(Selectores.USUARIO_LISTA_AREA, area);
        return opcionesDeLaLista(Selectores.USUARIO_LISTA_TIPO);
    }

    public boolean elBotonGuardarEstaDeshabilitado() {
        return elBotonEstaDeshabilitado(Selectores.USUARIO_BOTON_GUARDAR);
    }

    public PaginaUsuarios cancelar() {
        hacerClic(Selectores.BOTON_CANCELAR);
        esperarQueLaUrlYaNoContenga("users/add");
        return this;
    }
}
