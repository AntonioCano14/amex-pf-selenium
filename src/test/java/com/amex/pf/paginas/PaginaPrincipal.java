package com.amex.pf.paginas;

import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.SkipException;

import com.amex.pf.base.Configuracion;

/** Encabezado, menu principal y verificaciones comunes a todas las pantallas. */
public class PaginaPrincipal extends PaginaBase {

    /**
     * Entra a un menu del encabezado. Si el perfil del usuario no tiene ese menu,
     * el caso se reporta OMITIDO con el motivo (no como falla): cada perfil ve un
     * conjunto distinto de menus.
     */
    public PaginaPrincipal irAlMenu(String nombre) {
        By menu = Selectores.menuPrincipal(nombre);
        if (!estaVisible(menu, 5)) {
            throw new SkipException("El menu \"" + nombre + "\" no esta disponible para "
                    + Configuracion.usuario() + " (permisos del perfil). Ejecute este caso con un "
                    + "usuario que tenga ese permiso.");
        }
        hacerClic(menu);
        return this;
    }

    public PaginaPrincipal laDireccionDebeContener(String fragmento) {
        esperarQueLaUrlContenga(fragmento);
        return this;
    }

    public PaginaPrincipal debeVerseElTexto(String texto) {
        Assert.assertTrue(estaVisible(Selectores.textoVisible(texto),
                        Configuracion.esperaMaximaSegundos()),
                "No se mostro el texto \"" + texto + "\".");
        return this;
    }

    public PaginaPrincipal elBotonDebeEstarVisible(String etiqueta) {
        Assert.assertTrue(estaVisible(Selectores.boton(etiqueta),
                        Configuracion.esperaMaximaSegundos()),
                "No se mostro el boton \"" + etiqueta + "\".");
        return this;
    }

    /**
     * Las tablas de esta aplicacion no usan tbody: la primera fila es el
     * encabezado, por eso se exige mas de una fila.
     */
    public PaginaPrincipal laPantallaDebeTenerUnaTablaConInformacion() {
        verVisible(Selectores.TABLA);
        espera().until(navegador -> navegador.findElements(Selectores.FILAS_DE_TABLA).size() > 1);
        return this;
    }

    public PaginaPrincipal debeVerseUnaGrafica() {
        Assert.assertTrue(estaVisible(Selectores.GRAFICA, Configuracion.esperaMaximaSegundos()),
                "No se mostro la grafica de la pantalla de Inicio.");
        return this;
    }

    public PaginaPrincipal recargarLaPantalla() {
        navegador().navigate().refresh();
        return this;
    }

    public boolean laSesionSigueAbierta() {
        return estaVisible(Selectores.SALUDO_USUARIO, Configuracion.esperaMaximaSegundos());
    }
}
