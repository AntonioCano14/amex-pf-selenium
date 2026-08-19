package com.amex.pf.utilidades;

import java.util.ArrayList;
import java.util.Comparator;
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
 * El resumen final sale ordenado como la matriz. El orden de ejecucion lo pone
 * {@link OrdenDeLaMatriz}, que se registra en la misma suite.
 *
 * Se registra en las suites XML dentro de &lt;listeners&gt;.
 */
public class ReporteEnConsolaListener implements ITestListener {

    private static final String APROBADO = "APROBADO";
    private static final String FALLIDO = "FALLIDO ";
    private static final String OMITIDO = "OMITIDO ";

    /** Cada caso ejecutado: clave de orden de la matriz + linea impresa. */
    private final List<CasoDelResumen> resumen = new ArrayList<>();

    private record CasoDelResumen(String orden, String linea) {
    }

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
        System.out.println("RESUMEN DE CASOS EJECUTADOS (en el orden de la matriz)");
        resumen.stream()
                .sorted(Comparator.comparing(CasoDelResumen::orden))
                .map(CasoDelResumen::linea)
                .forEach(System.out::println);
        System.out.println("Aprobados: " + contexto.getPassedTests().size()
                + " | Fallidos: " + contexto.getFailedTests().size()
                + " | Omitidos: " + contexto.getSkippedTests().size());
        System.out.println();
    }

    private void imprimir(String estado, ITestResult resultado, Throwable motivo) {
        String id = idDelCaso(resultado);
        String linea = String.format("[%s] %s %s%s (%.1f s)",
                id, estado, nombreDelCaso(resultado),
                datosDelCaso(resultado), duracionEnSegundos(resultado));

        System.out.println(linea);
        if (motivo != null) {
            System.out.println("            Motivo: " + primeraLinea(motivo.getMessage()));
        }
        resumen.add(new CasoDelResumen(IdDeLaMatriz.comoOrden(id) + "|" + linea, linea));
    }

    /**
     * ID de la matriz. Se busca primero en los datos del caso (los @DataProvider que
     * cubren varios IDs mandan el suyo como primer dato) y si no, en la primera
     * palabra de la descripcion del @Test.
     */
    private String idDelCaso(ITestResult resultado) {
        String enLosDatos = IdDeLaMatriz.deLosDatos(resultado.getParameters());
        if (enLosDatos != null) {
            return enLosDatos;
        }
        return IdDeLaMatriz.deLaDescripcion(resultado.getMethod().getDescription(),
                resultado.getMethod().getMethodName());
    }

    /** Descripcion del caso sin el ID que la encabeza. */
    private String nombreDelCaso(ITestResult resultado) {
        String descripcion = resultado.getMethod().getDescription();
        if (descripcion == null || descripcion.isBlank()) {
            return resultado.getMethod().getMethodName();
        }
        String[] partes = descripcion.trim().split("\\s+", 2);
        if (partes.length > 1 && IdDeLaMatriz.es(partes[0])) {
            return partes[1];
        }
        return descripcion.trim();
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
            // Los selectores y las listas de valores esperados no aportan nada al
            // reporte del tester.
            if (dato instanceof By || dato instanceof Object[]) {
                continue;
            }
            if (IdDeLaMatriz.es(valor)) {
                valor = valor.replaceFirst("^\\S+\\s*", "");
            }
            if (!valor.isBlank()) {
                valores.add(valor);
            }
        }
        return valores.isEmpty() ? "" : " -> " + String.join(", ", valores);
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
