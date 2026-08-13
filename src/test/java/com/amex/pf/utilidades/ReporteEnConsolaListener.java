package com.amex.pf.utilidades;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

/**
 * Imprime en consola el resultado de cada caso con el ID de la matriz:
 *
 * <pre>
 * [PF_CP_004] APROBADO  Usuario y contrasena correctos (12.3 s)
 * [PF_CP_046] FALLIDO   La lista muestra todos los catalogos esperados (3.1 s)
 *             Motivo: Faltan catalogos en la lista: [Versiones].
 * </pre>
 *
 * El ID se toma de la primera palabra de la descripcion del @Test (por ejemplo
 * description = "PF_CP_046 La lista muestra..."), asi que al agregar un caso
 * nuevo solo hay que empezar la descripcion con el ID de la matriz.
 *
 * Se registra en las suites XML dentro de &lt;listeners&gt;.
 */
public class ReporteEnConsolaListener implements ITestListener {

    private static final String APROBADO = "APROBADO";
    private static final String FALLIDO = "FALLIDO ";
    private static final String OMITIDO = "OMITIDO ";

    private final List<String> resumen = new ArrayList<>();

    @Override
    public void onTestSuccess(ITestResult resultado) {
        imprimir(APROBADO, resultado, null);
    }

    @Override
    public void onTestFailure(ITestResult resultado) {
        imprimir(FALLIDO, resultado, resultado.getThrowable());
    }

    @Override
    public void onTestSkipped(ITestResult resultado) {
        imprimir(OMITIDO, resultado, resultado.getThrowable());
    }

    @Override
    public void onFinish(ITestContext contexto) {
        System.out.println();
        System.out.println("RESUMEN DE CASOS EJECUTADOS");
        resumen.forEach(System.out::println);
        System.out.println("Aprobados: " + contexto.getPassedTests().size()
                + " | Fallidos: " + contexto.getFailedTests().size()
                + " | Omitidos: " + contexto.getSkippedTests().size());
        System.out.println();
    }

    private void imprimir(String estado, ITestResult resultado, Throwable motivo) {
        String linea = String.format("[%s] %s %s%s (%.1f s)",
                idDelCaso(resultado), estado, nombreDelCaso(resultado),
                datosDelCaso(resultado), duracionEnSegundos(resultado));

        System.out.println(linea);
        if (motivo != null) {
            System.out.println("            Motivo: " + primeraLinea(motivo.getMessage()));
        }
        resumen.add(linea);
    }

    /**
     * ID de la matriz. Se busca primero en los datos del caso (los @DataProvider que
     * cubren varios IDs mandan el suyo como primer dato) y si no, en la primera
     * palabra de la descripcion del @Test.
     */
    private String idDelCaso(ITestResult resultado) {
        String enLosDatos = idEnLosDatos(resultado);
        if (enLosDatos != null) {
            return enLosDatos;
        }
        String descripcion = resultado.getMethod().getDescription();
        if (descripcion == null || descripcion.isBlank()) {
            return resultado.getMethod().getMethodName();
        }
        return descripcion.trim().split("\\s+")[0];
    }

    /** Descripcion del caso sin el ID. */
    private String nombreDelCaso(ITestResult resultado) {
        String descripcion = resultado.getMethod().getDescription();
        if (descripcion == null || descripcion.isBlank()) {
            return resultado.getMethod().getMethodName();
        }
        String[] partes = descripcion.trim().split("\\s+", 2);
        return partes.length > 1 ? partes[1] : partes[0];
    }

    /** Datos del caso cuando viene de un @DataProvider (por ejemplo el catalogo). */
    private String datosDelCaso(ITestResult resultado) {
        Object[] datos = resultado.getParameters();
        if (datos == null || datos.length == 0) {
            return "";
        }
        List<String> valores = new ArrayList<>();
        for (Object dato : datos) {
            String valor = String.valueOf(dato);
            // Los selectores no aportan nada al reporte del tester.
            if (dato instanceof By) {
                continue;
            }
            if (esId(valor)) {
                valor = valor.replaceFirst("^\\S+\\s*", "");
            }
            if (!valor.isBlank()) {
                valores.add(valor);
            }
        }
        return valores.isEmpty() ? "" : " -> " + String.join(", ", valores);
    }

    private String idEnLosDatos(ITestResult resultado) {
        Object[] datos = resultado.getParameters();
        if (datos == null) {
            return null;
        }
        for (Object dato : datos) {
            if (dato instanceof String texto && esId(texto)) {
                return texto.trim().split("\\s+")[0];
            }
        }
        return null;
    }

    /** Reconoce los IDs de la matriz y los internos: PF_CP_046, VAL_001, DEF_01, SEG_001. */
    private boolean esId(String texto) {
        return texto != null
                && texto.trim().split("\\s+")[0].matches("(PF_CP|VAL|DEF|SEG)_[0-9]+([-_][0-9]+)?");
    }

    private double duracionEnSegundos(ITestResult resultado) {
        return (resultado.getEndMillis() - resultado.getStartMillis()) / 1000.0;
    }

    private String primeraLinea(String mensaje) {
        if (mensaje == null) {
            return "(sin mensaje)";
        }
        return mensaje.split("\\R")[0];
    }
}
