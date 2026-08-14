package com.amex.pf.pruebas;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.amex.pf.base.Configuracion;
import com.amex.pf.base.PruebaBase;
import com.amex.pf.paginas.PaginaExpediente;
import com.amex.pf.paginas.PaginaLogin;
import com.amex.pf.paginas.PaginaPrincipal;
import com.amex.pf.paginas.PaginaReportes;
import com.amex.pf.paginas.PaginaUsuarios;
import com.amex.pf.utilidades.Descargas;

/**
 * Ola 4: archivos que entrega la aplicacion (Excel de Usuarios, Expediente y
 * Reportes, layout de carga masiva y los ZIP de una solicitud firmada).
 *
 * Ningun caso modifica informacion: solo se descargan archivos y se revisa su
 * contenido. Las columnas esperadas viven en configuracion.properties para que se
 * ajusten sin tocar codigo cuando negocio cambie un layout.
 */
public class DescargasPruebas extends PruebaBase {

    private PaginaPrincipal entrar() {
        return new PaginaLogin().iniciarSesionConCredencialesValidas();
    }

    /** Cada caso arranca con la carpeta vacia para revisar solo su propio archivo. */
    @BeforeMethod(alwaysRun = true)
    public void limpiarLasDescargas() {
        Descargas.limpiar();
    }

    private void debeTenerLasColumnas(Path archivo, String hoja, String propiedad) {
        List<String> encabezados = Descargas.encabezadosDeLaHoja(archivo, hoja);
        List<String> faltantes = new ArrayList<>();
        for (String esperada : Configuracion.lista(propiedad)) {
            if (!encabezados.contains(esperada)) {
                faltantes.add(esperada);
            }
        }
        Assert.assertTrue(faltantes.isEmpty(), "Al archivo " + archivo.getFileName()
                + " le faltan las columnas " + faltantes + " que pide la matriz. "
                + "La aplicacion entrega hoy: " + encabezados + ". Si el layout cambio, "
                + "ajuste " + propiedad + " en configuracion.properties.");
    }

    private void debeTraerLosDocumentos(Path zip, String propiedad) {
        List<String> contenido = Descargas.contenidoDelZip(zip);
        List<String> faltantes = new ArrayList<>();
        for (String documento : Configuracion.lista(propiedad)) {
            if (contenido.stream().noneMatch(nombre -> nombre.endsWith(documento))) {
                faltantes.add(documento);
            }
        }
        Assert.assertTrue(faltantes.isEmpty(), "Al ZIP " + zip.getFileName() + " le faltan los "
                + "documentos " + faltantes + ". Trae: " + contenido + ".");
    }

    @Test(groups = "descargas",
            description = "PF_CP_027 Exportar a excel descarga la tabla de usuarios")
    public void exportarUsuariosAExcel() {
        entrar().irAlMenu("Usuarios");
        new PaginaUsuarios().abrir().exportarAExcel();

        Path excel = Descargas.esperarArchivo(".xlsx");
        debeTenerLasColumnas(excel, null, "amex.excel.usuarios");
    }

    @Test(groups = {"descargas", "defecto_conocido"},
            description = "PF_CP_022 El layout de carga masiva trae las columnas de la matriz")
    public void descargarElLayoutDeCargaMasiva() {
        entrar().irAlMenu("Usuarios");
        PaginaUsuarios usuarios = new PaginaUsuarios().abrir()
                .abrirLaCargaMasiva()
                .laCargaMasivaDebePermitirElegirArchivo()
                .descargarElLayout();

        Path layout = Descargas.esperarArchivo(".xlsx");
        Assert.assertEquals(Descargas.hojasDelExcel(layout),
                List.of(Configuracion.lista("amex.excel.layout.usuarios.hojas")),
                "El layout no trae las hojas esperadas.");
        debeTenerLasColumnas(layout, "Layout", "amex.excel.layout.usuarios");
        usuarios.salirDeLaCargaMasiva();
    }

    @Test(groups = {"descargas", "regla_por_confirmar"},
            description = "PF_CP_125 Expediente ofrece importar solicitudes masivas")
    public void expedienteOfreceImportar() {
        entrar().irAlMenu("Expediente");
        Assert.assertTrue(new PaginaExpediente().abrir().hayBotonImportar(),
                "La pantalla de Expediente no muestra el boton Importar que describe la matriz: "
                        + "hay que confirmar con negocio si la carga masiva de solicitudes se "
                        + "quito o depende de otro permiso.");
    }

    @Test(groups = {"descargas", "regla_por_confirmar"},
            description = "PF_CP_127 Exportar descarga la tabla de solicitudes")
    public void exportarSolicitudesAExcel() {
        entrar().irAlMenu("Expediente");
        new PaginaExpediente().abrir().exportarAExcel();

        Path excel = Descargas.esperarArchivo(".xlsx");
        debeTenerLasColumnas(excel, null, "amex.excel.solicitudes");
    }

    @Test(groups = "descargas",
            description = "PF_CP_142 El ZIP de la solicitud trae los documentos cargados")
    public void descargarElZipDelExpediente() {
        entrar().irAlMenu("Expediente");
        new PaginaExpediente().abrir().descargarElZipDeUnaSolicitudFirmada(0);

        debeTraerLosDocumentos(Descargas.esperarArchivo(".zip"), "amex.zip.expediente");
    }

    @Test(groups = "descargas",
            description = "PF_CP_143 El ZIP Griffin trae el documento de Renaper y el firmado")
    public void descargarElZipGriffin() {
        entrar().irAlMenu("Expediente");
        new PaginaExpediente().abrir().descargarElZipDeUnaSolicitudFirmada(1);

        debeTraerLosDocumentos(Descargas.esperarArchivo(".zip"), "amex.zip.griffin");
    }

    @Test(groups = {"descargas", "regla_por_confirmar"},
            description = "PF_CP_154 El reporte general se descarga con sus columnas")
    public void generarElReporteGeneral() {
        entrar().irAlMenu("Reportes");
        new PaginaReportes().abrir()
                .elegirElTipoDeReporte("Reporte general")
                .generarElReporte();

        debeTenerLasColumnas(Descargas.esperarArchivo(".xlsx"), null,
                "amex.excel.reporte.general");
    }

    @Test(groups = "descargas",
            description = "PF_CP_155 Un reporte sin informacion avisa que no hay resultados")
    public void generarUnReporteSinResultados() {
        entrar().irAlMenu("Reportes");
        PaginaReportes reportes = new PaginaReportes().abrir()
                .elegirElTipoDeReporte("Reporte general")
                .filtrarPorDni(Configuracion.obtener("amex.reportes.dni.inexistente"))
                .generarElReporte();

        Assert.assertTrue(reportes.mensajeDelPopup().contains("No se encontraron resultados"),
                "El reporte sin informacion no aviso que no hay resultados; el popup dice: \""
                        + reportes.mensajeDelPopup() + "\".");
        Assert.assertTrue(Descargas.archivos().isEmpty(),
                "Un reporte sin resultados no deberia descargar archivo, y descargo: "
                        + Descargas.archivos() + ".");
        reportes.aceptarElPopup();
    }

    @Test(groups = "descargas",
            description = "PF_CP_157 El reporte de totales URL trae sus dos hojas")
    public void generarElReporteDeTotalesUrl() {
        entrar().irAlMenu("Reportes");
        new PaginaReportes().abrir()
                .elegirElTipoDeReporte("Reporte totales URL")
                .generarElReporte();

        Path excel = Descargas.esperarArchivo(".xlsx");
        debeTenerLasColumnas(excel, "totales", "amex.excel.reporte.url.totales");
        debeTenerLasColumnas(excel, "data", "amex.excel.reporte.url");
    }

    @Test(groups = "descargas",
            description = "PF_CP_158 El reporte de totales de WhatsApp trae sus dos hojas")
    public void generarElReporteDeTotalesDeWhatsApp() {
        entrar().irAlMenu("Reportes");
        new PaginaReportes().abrir()
                .elegirElTipoDeReporte("Reporte totales mensajes WhatsApp")
                .elegirElRangoDeFechas()
                .generarElReporte();

        Path excel = Descargas.esperarArchivo(".xlsx");
        debeTenerLasColumnas(excel, "totales", "amex.excel.reporte.whatsapp.totales");
        debeTenerLasColumnas(excel, "data", "amex.excel.reporte.whatsapp");
    }
}
