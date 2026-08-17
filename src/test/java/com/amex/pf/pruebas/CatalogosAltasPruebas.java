package com.amex.pf.pruebas;

import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.amex.pf.base.PruebaBase;
import com.amex.pf.datos.ElementoDeCatalogo;
import com.amex.pf.paginas.PaginaCatalogos;
import com.amex.pf.paginas.PaginaLogin;

/**
 * OLA 5 - Altas, ediciones, bajas y activaciones de los siete catalogos.
 *
 * OJO: estas pruebas SI ESCRIBEN en el ambiente (grupo escribe_datos, excluido de
 * la suite de regresion). Para no ensuciar los datos reales:
 * - todo lo que se crea lleva el prefijo de amex.datos.prefijo (ZZAUTOQA) y un
 *   numero distinto en cada ejecucion;
 * - nunca se toca un registro que no haya creado la propia prueba;
 * - al terminar, la prueba deja su registro INACTIVO (la aplicacion no permite
 *   borrar elementos de catalogo).
 *
 * Cada prueba recorre el ciclo completo de un catalogo, porque los cuatro casos de
 * la matriz son pasos del mismo flujo: se agrega un elemento (1), se edita (2), se
 * inactiva (3) y se vuelve a activar (4). El ID de cada paso se imprime en el
 * mensaje de la consola y esta en la descripcion de la prueba.
 *
 * COMO AGREGAR UN CATALOGO NUEVO: se agrega un metodo como los de abajo con los
 * IDs de la matriz en la descripcion y se declaran sus campos en
 * com.amex.pf.datos.ElementoDeCatalogo.
 */
public class CatalogosAltasPruebas extends PruebaBase {

    private PaginaCatalogos catalogos;

    @BeforeMethod(alwaysRun = true)
    public void abrirCatalogos() {
        new PaginaLogin().iniciarSesionConCredencialesValidas().irAlMenu("Catalogos");
        catalogos = new PaginaCatalogos().abrir();
    }

    @Test(groups = {"ola5", "catalogos", "escribe_datos"},
            description = "PF_CP_048/051/052/053 Nacionalidades: agregar, editar, inactivar y "
                    + "activar un elemento")
    public void nacionalidades() {
        cicloDelCatalogo("Nacionalidades");
    }

    @Test(groups = {"ola5", "catalogos", "escribe_datos"},
            description = "PF_CP_055/058/059/060 Profesiones: agregar, editar, inactivar y "
                    + "activar un elemento")
    public void profesiones() {
        cicloDelCatalogo("Profesiones");
    }

    @Test(groups = {"ola5", "catalogos", "escribe_datos"},
            description = "PF_CP_062/066/067/068 Campaña: agregar, editar, inactivar y activar "
                    + "un elemento")
    public void campana() {
        cicloDelCatalogo("Campaña");
    }

    @Test(groups = {"ola5", "catalogos", "escribe_datos"},
            description = "PF_CP_070/073/074/075 Codigo de pais: agregar, editar, inactivar y "
                    + "activar un elemento")
    public void codigoDePais() {
        cicloDelCatalogo("Codigo de pais");
    }

    @Test(groups = {"ola5", "catalogos", "escribe_datos"},
            description = "PF_CP_077/082/083/084 Productos: agregar, editar, inactivar y activar "
                    + "un elemento")
    public void productos() {
        cicloDelCatalogo("Productos");
    }

    @Test(groups = {"ola5", "catalogos", "escribe_datos"},
            description = "PF_CP_086/090/091/092 Dias festivos: agregar, editar, inactivar y "
                    + "activar un elemento")
    public void diasFestivos() {
        cicloDelCatalogo(ElementoDeCatalogo.DIAS_FESTIVOS);
    }

    @Test(groups = {"ola5", "catalogos", "escribe_datos"},
            description = "PF_CP_094/098/099/100 Versiones: agregar, editar, inactivar y activar "
                    + "un elemento")
    public void versiones() {
        cicloDelCatalogo("Versiones");
    }

    /**
     * Datos de prueba que todavia no estan en la tabla del catalogo. Los elementos de
     * texto ya son unicos por ejecucion; los dias festivos se identifican por su
     * fecha y no se pueden borrar (solo inactivar), asi que se prueban varias fechas
     * hasta encontrar una libre para el alta y para la edicion.
     */
    private ElementoDeCatalogo datosQueNoExistenAun(String catalogo, String sufijo) {
        for (int desplazamiento = 0; desplazamiento < 14; desplazamiento++) {
            ElementoDeCatalogo candidato =
                    ElementoDeCatalogo.para(catalogo, sufijo, desplazamiento);
            if (!candidato.seEligeConCalendario()
                    || (catalogos.buscarLaFilaSiExiste(candidato.identificador()) == null
                            && catalogos.buscarLaFilaSiExiste(candidato.valorEditado()) == null)) {
                return candidato;
            }
        }
        throw new SkipException("El catalogo \"" + catalogo + "\" ya tiene ocupadas todas las "
                + "fechas de prueba de " + ElementoDeCatalogo.mes() + " "
                + ElementoDeCatalogo.anio() + ": cambie amex.datos.anio o amex.datos.mes.");
    }

    /**
     * Ciclo de vida de un elemento del catalogo: agregar, editar, inactivar y
     * activar, comprobando en la tabla el resultado de cada paso.
     */
    private void cicloDelCatalogo(String catalogo) {
        catalogos.abrirCatalogo(catalogo);
        ElementoDeCatalogo elemento = datosQueNoExistenAun(catalogo, sufijoDeLaEjecucion());

        String identificador = elemento.identificador();
        try {
            // 1) Agregar elemento: la tabla debe mostrarlo activo.
            catalogos.agregarElemento(elemento)
                    .laTablaDebeMostrar(identificador, "ACTIVO");

            // 2) Editar datos desde el detalle.
            catalogos.editarElElemento(elemento);
            identificador = elemento.identificadorEditado();
            catalogos.laTablaDebeMostrar(elemento.valorEditado(), "ACTIVO");

            // 3) Inactivar: la aplicacion pide confirmacion antes de hacerlo.
            catalogos.inactivarElElemento(identificador);
            String confirmacion = catalogos.textoDelPopup();
            Assert.assertTrue(confirmacion.contains("inactivar"),
                    "El popup de inactivar no pide confirmacion. Muestra: " + confirmacion + ".");
            catalogos.aceptarElModal()
                    .laTablaDebeMostrar(identificador, "INACTIVO");

            // 4) Activar registro desde el detalle del elemento inactivo.
            catalogos.activarElElemento(identificador)
                    .laTablaDebeMostrar(identificador, "ACTIVO");
        } finally {
            // Limpieza: el elemento creado por la prueba queda inactivo.
            catalogos.inactivarSiQuedoActivo(identificador);
        }
    }

    /** Numero que distingue los datos de esta ejecucion de los de las anteriores. */
    private String sufijoDeLaEjecucion() {
        return String.valueOf(System.currentTimeMillis() % 100000000L);
    }
}
