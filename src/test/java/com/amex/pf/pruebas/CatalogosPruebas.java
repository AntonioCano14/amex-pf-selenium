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
 * Un solo metodo recorre todos los catalogos: para cubrir un catalogo nuevo se
 * agrega su nombre en la propiedad amex.catalogos de configuracion.properties,
 * sin tocar el codigo.
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
        String[] esperados = PaginaCatalogos.catalogosEsperados();
        Object[][] datos = new Object[esperados.length][1];
        for (int i = 0; i < esperados.length; i++) {
            datos[i][0] = esperados[i];
        }
        return datos;
    }

    @Test(groups = {"catalogos", "humo"},
            description = "PF_CP_046 La lista muestra todos los catalogos esperados")
    public void pfCp046LaListaMuestraLosCatalogosEsperados() {
        new PaginaCatalogos().laListaDebeContener(PaginaCatalogos.catalogosEsperados());
    }

    @Test(groups = "catalogos", dataProvider = "catalogos",
            description = "PF_CP_047/054/061/069/076/085/093 Cada catalogo muestra su "
                    + "tabla y su boton Agregar elemento")
    public void cadaCatalogoMuestraSuTabla(String catalogo) {
        new PaginaCatalogos()
                .abrirCatalogo(catalogo)
                .elBotonAgregarElementoDebeEstarVisible();
        inicio.laPantallaDebeTenerUnaTablaConInformacion();
    }
}
