package com.amex.pf.pruebas;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.amex.pf.base.PruebaBase;
import com.amex.pf.paginas.PaginaCatalogos;
import com.amex.pf.paginas.PaginaLogin;
import com.amex.pf.paginas.PaginaPrincipal;

/**
 * PLANTILLA para los casos de catalogos (PF_CP_046 a PF_CP_093).
 *
 * Un solo metodo recorre los 8 catalogos: para cubrir un catalogo nuevo se
 * agrega su nombre en PaginaCatalogos.CATALOGOS.
 *
 * Solo lectura: se abre cada catalogo y se verifica su tabla y su boton, pero no
 * se agrega ni se modifica ningun elemento.
 */
public class CatalogosPruebas extends PruebaBase {

    private PaginaPrincipal inicio;

    @BeforeMethod(alwaysRun = true)
    public void abrirCatalogos() {
        inicio = new PaginaLogin().iniciarSesionConCredencialesValidas();
        inicio.irAlMenu("Catalogos");
    }

    @DataProvider(name = "catalogos")
    public Object[][] catalogos() {
        Object[][] datos = new Object[PaginaCatalogos.CATALOGOS.length][1];
        for (int i = 0; i < PaginaCatalogos.CATALOGOS.length; i++) {
            datos[i][0] = PaginaCatalogos.CATALOGOS[i];
        }
        return datos;
    }

    @Test(groups = "catalogos", dataProvider = "catalogos",
            description = "PF_CP_047-093 Cada catalogo muestra su tabla y su boton Agregar elemento")
    public void cadaCatalogoMuestraSuTabla(String catalogo) {
        new PaginaCatalogos()
                .abrirCatalogo(catalogo)
                .elBotonAgregarElementoDebeEstarVisible();
        inicio.laPantallaDebeTenerUnaTablaConInformacion();
    }
}
