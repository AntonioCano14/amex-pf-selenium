package com.amex.pf.paginas;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
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
        desplazarHasta(Selectores.boton(etiqueta));
        return this;
    }

    /** Verifica una lista de botones de la pantalla (PF_CP_010). */
    public PaginaPrincipal losBotonesDebenEstarVisibles(String... etiquetas) {
        for (String etiqueta : etiquetas) {
            elBotonDebeEstarVisible(etiqueta);
        }
        return this;
    }

    /**
     * Las tablas de esta aplicacion no usan tbody: la primera fila es el
     * encabezado, por eso se exige mas de una fila.
     */
    public PaginaPrincipal laPantallaDebeTenerUnaTablaConInformacion() {
        verVisible(Selectores.TABLA);
        espera().until(navegador -> navegador.findElements(Selectores.FILAS_DE_TABLA).size() > 1);
        desplazarHasta(Selectores.TABLA);
        return this;
    }

    public PaginaPrincipal debeVerseUnaGrafica() {
        Assert.assertTrue(estaVisible(Selectores.GRAFICA, Configuracion.esperaMaximaSegundos()),
                "No se mostro la grafica de la pantalla de Inicio.");
        desplazarHasta(Selectores.GRAFICA);
        return this;
    }

    /**
     * Leyendas de la grafica de Inicio tal como las muestra la aplicacion:
     * "Aprobada - 4", "Denegada - 3"...
     */
    public List<String> leyendasDeLaGrafica() {
        desplazarHasta(Selectores.INICIO_LEYENDA_DE_LA_GRAFICA);
        return buscarTodos(Selectores.INICIO_LEYENDA_DE_LA_GRAFICA).stream()
                .map(this::textoDe)
                .filter(texto -> !texto.isEmpty())
                .toList();
    }

    /**
     * Porcentaje que la grafica muestra por cada estatus ("Aprobada" -> "4.49%").
     * Se reintenta la lectura porque Angular pinta
     * los bloques antes de tener los valores: en ese instante salen vacios.
     */
    public Map<String, String> detallePorEstatusDeLaGrafica() {
        desplazarHasta(Selectores.INICIO_DETALLE_POR_ESTATUS);
        return leerAunqueLaTablaSeRefresque(() -> {
            Map<String, String> detalle = new LinkedHashMap<>();
            for (WebElement bloque : buscarTodos(Selectores.INICIO_DETALLE_POR_ESTATUS)) {
                String estatus = textoDe(bloque.findElement(Selectores.INICIO_ESTATUS_DEL_DETALLE));
                String porcentaje =
                        textoDe(bloque.findElement(Selectores.INICIO_PORCENTAJE_DEL_DETALLE));
                if (estatus.isEmpty() || porcentaje.isEmpty()) {
                    return null;
                }
                detalle.put(estatus, porcentaje);
            }
            return detalle.isEmpty() ? null : detalle;
        });
    }

    public PaginaPrincipal recargarLaPantalla() {
        navegador().navigate().refresh();
        return this;
    }

    public boolean laSesionSigueAbierta() {
        return estaVisible(Selectores.SALUDO_USUARIO, Configuracion.esperaMaximaSegundos());
    }
}
