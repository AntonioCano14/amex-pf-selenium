package com.amex.pf.utilidades;

import java.util.Comparator;
import java.util.List;

import org.testng.IMethodInstance;
import org.testng.IMethodInterceptor;
import org.testng.ITestContext;

/**
 * Ejecuta los casos en el orden de la matriz funcional: PF_CP_001, PF_CP_002,
 * PF_CP_003... y al final los internos (VAL, SEG, DEF).
 *
 * Asi la consola se lee igual que la matriz, sin importar en que clase esta cada
 * caso. El ID sale de la descripcion del @Test (ver {@link IdDeLaMatriz}); un caso
 * sin ID queda al final, ordenado por el nombre de su metodo.
 *
 * Se registra en las suites XML dentro de &lt;listeners&gt;. Para que TestNG
 * respete este orden entre clases distintas, las suites declaran
 * parallel="methods" thread-count="1": los casos siguen ejecutandose de uno en
 * uno (la aplicacion solo permite una sesion activa por usuario), pero ya no se
 * agrupan por clase.
 */
public class OrdenDeLaMatriz implements IMethodInterceptor {

    @Override
    public List<IMethodInstance> intercept(List<IMethodInstance> casos, ITestContext contexto) {
        return casos.stream()
                .sorted(Comparator.comparing(this::claveDeOrden))
                .toList();
    }

    private String claveDeOrden(IMethodInstance caso) {
        String id = IdDeLaMatriz.deLaDescripcion(caso.getMethod().getDescription(),
                caso.getMethod().getMethodName());
        return IdDeLaMatriz.comoOrden(id) + "|" + caso.getMethod().getMethodName();
    }
}
