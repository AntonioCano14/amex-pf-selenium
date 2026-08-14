package com.amex.pf.pruebas;

import java.util.List;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.amex.pf.base.PruebaBase;
import com.amex.pf.paginas.PaginaCatalogos;
import com.amex.pf.paginas.PaginaLogin;

/**
 * Ola 3 - Consultas de los siete catalogos (PF_CP_047 a PF_CP_097).
 *
 * Cubre las pantallas, los limites de caracteres de los campos y el detalle de un
 * registro. Solo lectura: los modales se abren para revisar sus campos y se cierran
 * con CANCELAR o con la "X"; nunca se presiona ACEPTAR ni EDITAR DATOS.
 *
 * COMO AGREGAR O AJUSTAR UN CASO: cada fila de los @DataProvider de abajo es un
 * caso de la matriz. Para cubrir un catalogo nuevo se agrega una fila con su ID,
 * su nombre y lo que debe mostrar.
 */
public class CatalogosConsultasPruebas extends PruebaBase {

    private PaginaCatalogos catalogos;

    @BeforeMethod(alwaysRun = true)
    public void abrirCatalogos() {
        new PaginaLogin().iniciarSesionConCredencialesValidas().irAlMenu("Catalogos");
        catalogos = new PaginaCatalogos().abrir();
    }

    /** ID de la matriz | catalogo | columnas que debe mostrar su tabla. */
    @DataProvider(name = "pantallas")
    public Object[][] pantallas() {
        return new Object[][] {
            {"PF_CP_047", "Nacionalidades", new String[] {"Descripción", "Estatus"}},
            {"PF_CP_054", "Profesiones", new String[] {"Descripción", "Estatus"}},
            {"PF_CP_061", "Campaña", new String[] {"Código", "Promotor", "Estatus"}},
            {"PF_CP_069", "Codigo de pais", new String[] {"Imagen", "Código", "Estatus"}},
            {"PF_CP_076", "Productos",
                new String[] {"Imagen", "Nombre", "Código", "Link", "Estatus"}},
            {"PF_CP_085", "Dias festivos",
                new String[] {"Dia festivo(dd/mm/yyyy)", "Estatus"}},
            {"PF_CP_093", "Versiones", new String[] {"Descripción", "Valor", "Estatus"}},
        };
    }

    /** ID de la matriz | catalogo | campos que debe mostrar el detalle. */
    @DataProvider(name = "detalles")
    public Object[][] detalles() {
        return new Object[][] {
            {"PF_CP_050", "Nacionalidades", new String[] {"Descripción"}},
            {"PF_CP_057", "Profesiones", new String[] {"Descripción"}},
            {"PF_CP_065", "Campaña", new String[] {"Código", "Promotor"}},
            {"PF_CP_072", "Codigo de pais", new String[] {"Código"}},
            {"PF_CP_081", "Productos", new String[] {"Nombre", "Código", "Link"}},
            {"PF_CP_089", "Dias festivos", new String[] {"Dia festivo(dd/mm/yyyy)"}},
            {"PF_CP_097", "Versiones", new String[] {"Descripción", "Valor"}},
        };
    }

    /** ID de la matriz | catalogo | campo del alta | maximo de caracteres. */
    @DataProvider(name = "limitesDeCaracteres")
    public Object[][] limitesDeCaracteres() {
        return new Object[][] {
            {"PF_CP_049", "Nacionalidades", "Descripción", 100},
            {"PF_CP_056", "Profesiones", "Descripción", 250},
            {"PF_CP_064", "Campaña", "Promotor", 250},
            {"PF_CP_078", "Productos", "Nombre", 100},
            {"PF_CP_080", "Productos", "Link", 200},
        };
    }

    @Test(groups = "consultas", dataProvider = "pantallas",
            description = "Pantalla del catalogo con su boton Agregar elemento y su tabla")
    public void laPantallaDelCatalogoMuestraSuTabla(String caso, String catalogo,
            String[] columnas) {
        catalogos.abrirCatalogo(catalogo).elBotonAgregarElementoDebeEstarVisible();

        List<String> actuales = catalogos.encabezadosDeLaTabla();
        for (String columna : columnas) {
            Assert.assertTrue(actuales.stream()
                            .anyMatch(actual -> actual.equalsIgnoreCase(columna)),
                    caso + ": la tabla de " + catalogo + " no muestra la columna \"" + columna
                            + "\". Muestra hoy: " + actuales + ".");
        }
    }

    @Test(groups = "consultas", dataProvider = "detalles",
            description = "Ver detalle del catalogo con sus campos, EDITAR DATOS y la X")
    public void elDetalleDelCatalogoMuestraSusDatos(String caso, String catalogo,
            String[] campos) {
        catalogos.abrirCatalogo(catalogo)
                .abrirElDetalleDelPrimerRegistro()
                .elModalDebeTenerLosCampos(campos)
                .losCamposDelDetalleDebenSerDeSoloLectura()
                .elModalDebeTenerElBoton("EDITAR DATOS")
                .elBotonCerrarDelModalDebeEstarVisible()
                .cerrarElModal();
    }

    @Test(groups = "consultas", dataProvider = "limitesDeCaracteres",
            description = "Maximo de caracteres de un campo del alta del catalogo")
    public void elCampoDelCatalogoRespetaSuMaximo(String caso, String catalogo, String campo,
            int maximo) {
        catalogos.abrirCatalogo(catalogo).abrirElAltaDeElemento();
        int aceptados = catalogos.cuantosCaracteresAceptaElCampo(campo, maximo);
        catalogos.cerrarElModal();

        Assert.assertEquals(aceptados, maximo,
                caso + ": el campo \"" + campo + "\" de " + catalogo + " acepto " + aceptados
                        + " caracteres y la matriz pide " + maximo + ".");
    }

    @Test(groups = {"consultas", "defecto_conocido"},
            description = "PF_CP_063 El campo Codigo de Campaña acepta como maximo 250 caracteres")
    public void pfCp063MaximoDelCodigoDeCampana() {
        catalogos.abrirCatalogo("Campaña").abrirElAltaDeElemento();
        int aceptados = catalogos.cuantosCaracteresAceptaElCampo("Código", 250);
        catalogos.cerrarElModal();

        Assert.assertEquals(aceptados, 250,
                "El campo Codigo de Campaña acepto " + aceptados + " caracteres: la matriz pide "
                        + "250 y ya senala en su resultado esperado que permite mas (DEF_03).");
    }

    @Test(groups = {"consultas", "regla_por_confirmar"},
            description = "PF_CP_071 El campo Codigo de Codigo de pais acepta 100 caracteres")
    public void pfCp071MaximoDelCodigoDePais() {
        catalogos.abrirCatalogo("Codigo de pais").abrirElAltaDeElemento();
        int aceptados = catalogos.cuantosCaracteresAceptaElCampo("Código", 100);
        catalogos.cerrarElModal();

        Assert.assertEquals(aceptados, 100,
                "El campo Codigo de Codigo de pais acepto " + aceptados + " caracteres y la "
                        + "matriz pide 100. Por confirmar con negocio: un codigo de pais real "
                        + "no necesita 100 caracteres.");
    }

    @Test(groups = "consultas",
            description = "PF_CP_079 El campo Codigo de Productos acepta 10 caracteres")
    public void pfCp079MaximoDelCodigoDeProductos() {
        catalogos.abrirCatalogo("Productos").abrirElAltaDeElemento();
        int aceptados = catalogos.cuantosCaracteresAceptaElCampo("Código", 10);
        catalogos.cerrarElModal();

        Assert.assertEquals(aceptados, 10,
                "El campo Codigo de Productos acepto " + aceptados + " caracteres.");
    }

    @Test(groups = "consultas",
            description = "PF_CP_087 El dia festivo acepta 8 digitos con formato dd/mm/aaaa")
    public void pfCp087DigitosDelDiaFestivo() {
        catalogos.abrirCatalogo("Dias festivos").abrirElAltaDeElemento();
        int digitos = catalogos.cuantosDigitosAceptaElCampo("Dia festivo(dd/mm/yyyy)", 8);
        catalogos.cerrarElModal();

        Assert.assertEquals(digitos, 8,
                "El campo Dia festivo acepto " + digitos + " digitos y la matriz pide 8.");
    }

    @Test(groups = "consultas",
            description = "PF_CP_088 El calendario de Dias festivos solo habilita de hoy en adelante")
    public void pfCp088CalendarioDeDiasFestivos() {
        catalogos.abrirCatalogo("Dias festivos")
                .abrirElAltaDeElemento()
                .elModalDebeTenerCalendario()
                .abrirElCalendario()
                .soloDebeHabilitarDesdeHoy()
                .cerrarElCalendario();
        catalogos.cerrarElModal();
    }

    @Test(groups = {"consultas", "regla_por_confirmar"},
            description = "PF_CP_095 Versiones tiene un campo Fecha de 8 digitos")
    public void pfCp095CampoFechaDeVersiones() {
        catalogos.abrirCatalogo("Versiones").abrirElAltaDeElemento();
        boolean tieneFecha = catalogos.tieneElCampo("Dia festivo(dd/mm/yyyy)")
                || catalogos.tieneElCampo("Fecha");
        catalogos.cerrarElModal();

        Assert.assertTrue(tieneFecha,
                "El alta de Versiones no tiene campo Fecha: muestra Descripción y Valor. "
                        + "PF_CP_095 y PF_CP_096 parecen copiados de Dias festivos; por "
                        + "confirmar con negocio si la matriz debe corregirse.");
    }

    @Test(groups = {"consultas", "regla_por_confirmar"},
            description = "PF_CP_096 Versiones tiene boton de calendario")
    public void pfCp096CalendarioDeVersiones() {
        catalogos.abrirCatalogo("Versiones").abrirElAltaDeElemento();
        boolean tieneCalendario = catalogos.tieneCalendario();
        catalogos.cerrarElModal();

        Assert.assertTrue(tieneCalendario,
                "El alta de Versiones no tiene calendario: muestra Descripción y Valor. "
                        + "Mismo pendiente que PF_CP_095.");
    }
}
