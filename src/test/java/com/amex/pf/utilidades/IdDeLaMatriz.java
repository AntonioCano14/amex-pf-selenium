package com.amex.pf.utilidades;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ID del caso en la matriz funcional (PF_CP_046, CAM_001, VAL_001, DEF_01, SEG_001).
 *
 * El ID se toma de la primera palabra de la descripcion del @Test, o del primer
 * dato cuando el caso viene de un @DataProvider que cubre varios IDs. Al agregar
 * un caso nuevo solo hay que empezar su descripcion con el ID de la matriz.
 *
 * Una prueba que cubre varios casos del mismo flujo los declara separados por
 * "/" o como rango (PF_CP_042/043/044/045, PF_CP_115-116): el reporte los imprime
 * completos y los ordena por el primer numero, igual que la trazabilidad.
 */
public final class IdDeLaMatriz {

    /** Los casos de la matriz van primero; los internos, despues. */
    private static final List<String> ORDEN_DE_LOS_PREFIJOS =
            List.of("PF_CP", "CAM", "VAL", "SEG", "DEF");
    private static final Pattern PARTES_DEL_ID =
            Pattern.compile("(PF_CP|VAL|DEF|SEG|CAM)_([0-9]+)");
    private static final String ID = "(PF_CP|VAL|DEF|SEG|CAM)_[0-9]+([-_/][0-9]+)*";

    private IdDeLaMatriz() {
    }

    public static boolean es(String texto) {
        return texto != null && primeraPalabra(texto).matches(ID);
    }

    /** ID que trae un @DataProvider, o null si sus datos no traen ninguno. */
    public static String deLosDatos(Object[] datos) {
        if (datos == null) {
            return null;
        }
        for (Object dato : datos) {
            if (dato instanceof String texto && es(texto)) {
                return primeraPalabra(texto);
            }
        }
        return null;
    }

    /** ID que encabeza la descripcion del @Test; si no lo trae, el nombre del metodo. */
    public static String deLaDescripcion(String descripcion, String nombreDelMetodo) {
        if (descripcion == null || descripcion.isBlank() || !es(descripcion)) {
            return nombreDelMetodo;
        }
        return primeraPalabra(descripcion);
    }

    /**
     * Clave para ordenar los casos como estan en la matriz: primero por prefijo
     * (PF_CP antes que VAL, SEG y DEF) y despues por numero. Lo que no es un ID
     * queda al final, en orden alfabetico.
     */
    public static String comoOrden(String id) {
        Matcher partes = PARTES_DEL_ID.matcher(id == null ? "" : primeraPalabra(id));
        if (!partes.lookingAt()) {
            return "9|" + id;
        }
        return ORDEN_DE_LOS_PREFIJOS.indexOf(partes.group(1))
                + "|" + String.format("%06d", Integer.parseInt(partes.group(2)))
                + "|" + id;
    }

    private static String primeraPalabra(String texto) {
        return texto.trim().split("\\s+")[0];
    }
}
