package com.amex.pf.datos;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.amex.pf.base.Configuracion;

/**
 * Datos con los que la ola 5 da de alta un elemento en cada catalogo.
 *
 * Cada catalogo pide campos distintos, por eso aqui se describe QUE se escribe en
 * cada uno: el nombre del campo es el placeholder que muestra el modal, asi el
 * tester puede leer esta clase junto a la pantalla y entenderla.
 *
 * Todos los valores llevan el prefijo de automatizacion (amex.datos.prefijo) y un
 * numero distinto en cada ejecucion, para no chocar con los datos reales del
 * ambiente y para poder reconocerlos y limpiarlos despues.
 */
public final class ElementoDeCatalogo {

    /** Catalogo cuyo elemento se da de alta con el calendario, no escribiendo texto. */
    public static final String DIAS_FESTIVOS = "Dias festivos";

    private final String catalogo;
    private final Map<String, String> campos;
    private final String campoQueIdentifica;
    private final String campoQueSeEdita;
    private final boolean pideImagen;
    private final int diaDelAlta;

    private ElementoDeCatalogo(String catalogo, Map<String, String> campos,
            String campoQueIdentifica, String campoQueSeEdita, boolean pideImagen) {
        this(catalogo, campos, campoQueIdentifica, campoQueSeEdita, pideImagen, 1);
    }

    private ElementoDeCatalogo(String catalogo, Map<String, String> campos,
            String campoQueIdentifica, String campoQueSeEdita, boolean pideImagen,
            int diaDelAlta) {
        this.catalogo = catalogo;
        this.campos = campos;
        this.campoQueIdentifica = campoQueIdentifica;
        this.campoQueSeEdita = campoQueSeEdita;
        this.pideImagen = pideImagen;
        this.diaDelAlta = diaDelAlta;
    }

    /** Prefijo con el que se reconocen los datos creados por la automatizacion. */
    public static String prefijo() {
        return Configuracion.obtener("amex.datos.prefijo");
    }

    /**
     * Datos para el catalogo indicado. El sufijo distingue una ejecucion de otra
     * (se usa la hora del sistema).
     */
    public static ElementoDeCatalogo para(String catalogo, String sufijo) {
        return para(catalogo, sufijo, 0);
    }

    /**
     * Igual que {@link #para(String, String)}, con un desplazamiento para pedir otros
     * datos cuando los primeros ya existen en el ambiente (solo aplica a los dias
     * festivos, que se identifican por su fecha y no por un texto unico).
     */
    public static ElementoDeCatalogo para(String catalogo, String sufijo, int desplazamiento) {
        String texto = prefijo() + " " + sufijo;
        String codigo = sufijo;
        Map<String, String> campos = new LinkedHashMap<>();

        switch (catalogo) {
            case "Nacionalidades", "Profesiones" -> {
                campos.put("Descripción", texto);
                return new ElementoDeCatalogo(catalogo, campos, "Descripción", "Descripción", false);
            }
            case "Campaña" -> {
                // El servicio rechaza codigos de mas de 10 caracteres ("size must be between
                // 0 and 10") aunque la pantalla deje escribir mas: ver README seccion 7.4.
                campos.put("Código", "ZZ" + codigo);
                campos.put("Promotor", texto + " PROMOTOR");
                return new ElementoDeCatalogo(catalogo, campos, "Código", "Promotor", false);
            }
            case "Codigo de pais" -> {
                // El campo Codigo solo acepta 10 caracteres: se usa el numero de la ejecucion.
                campos.put("Código", "+" + codigo);
                return new ElementoDeCatalogo(catalogo, campos, "Código", "Código", true);
            }
            case "Productos" -> {
                campos.put("Nombre", texto);
                campos.put("Código", codigo);
                campos.put("Link", "https://example.com/" + codigo);
                return new ElementoDeCatalogo(catalogo, campos, "Nombre", "Nombre", true);
            }
            case "Versiones" -> {
                campos.put("Descripción", texto);
                campos.put("Valor", codigo);
                return new ElementoDeCatalogo(catalogo, campos, "Descripción", "Valor", false);
            }
            case DIAS_FESTIVOS -> {
                // La fecha se elige en el calendario (ver PaginaCatalogos.agregarElemento).
                // El dia depende de la ejecucion porque los dias festivos que crea la
                // automatizacion no se pueden borrar, solo inactivar: si se usara
                // siempre el mismo dia la segunda ejecucion chocaria con el anterior.
                int numero = Integer.parseInt(sufijo.replaceAll("\\D", ""));
                int dia = 1 + Math.floorMod(numero / 2 + desplazamiento, 14) * 2;
                return new ElementoDeCatalogo(catalogo, campos, "", "", false, dia);
            }
            default -> throw new IllegalArgumentException(
                    "No hay datos definidos para el catalogo \"" + catalogo + "\". "
                            + "Agreguelos en ElementoDeCatalogo.");
        }
    }

    public String catalogo() {
        return catalogo;
    }

    /** Campos a llenar en el modal: placeholder -> valor. */
    public Map<String, String> campos() {
        return campos;
    }

    /** Placeholder del campo cuyo valor identifica la fila en la tabla. */
    public String campoQueIdentifica() {
        return campoQueIdentifica;
    }

    /** Valor con el que se busca la fila en la tabla. */
    public String identificador() {
        return seEligeConCalendario() ? fecha(diaDelAlta) : campos.get(campoQueIdentifica);
    }

    // ------------------------------------------------------------ Dias festivos
    /** Anio del dia festivo de prueba (amex.datos.anio), lejano para no chocar. */
    public static String anio() {
        return Configuracion.obtener("amex.datos.anio");
    }

    /** Mes del dia festivo de prueba como lo muestra el calendario ("DIC"). */
    public static String mes() {
        return Configuracion.obtener("amex.datos.mes");
    }

    /** Dia que se elige en el calendario al dar de alta el dia festivo. */
    public int diaDelAlta() {
        return diaDelAlta;
    }

    /** Dia al que se cambia la fecha en la prueba de edicion. */
    public int diaDeLaEdicion() {
        return diaDelAlta + 1;
    }

    /** La tabla muestra el dia festivo como dd/mm/aaaa. */
    public static String fecha(int dia) {
        return String.format("%02d/%02d/%s", dia, numeroDelMes(), anio());
    }

    private static int numeroDelMes() {
        List<String> meses = List.of("ENE", "FEB", "MAR", "ABR", "MAY", "JUN",
                "JUL", "AGO", "SEP", "OCT", "NOV", "DIC");
        int posicion = meses.indexOf(mes().toUpperCase());
        if (posicion < 0) {
            throw new IllegalArgumentException("amex.datos.mes debe ser uno de " + meses
                    + " y vale \"" + mes() + "\".");
        }
        return posicion + 1;
    }

    /** Placeholder del campo que la prueba de edicion modifica. */
    public String campoQueSeEdita() {
        return campoQueSeEdita;
    }

    /** Valor nuevo que deja la edicion (PF_CP_051, 058, 066, 073, 082, 098). */
    public String valorEditado() {
        if (seEligeConCalendario()) {
            return fecha(diaDeLaEdicion());
        }
        String valor = campos.get(campoQueSeEdita);
        // Los campos de codigo solo aceptan digitos (el "+" del codigo de pais incluido):
        // si se les agregara texto la pantalla lo filtraria y la tabla seguiria mostrando
        // el valor original. Se agrega o se cambia un digito, respetando los 10 caracteres.
        if (valor.matches("\\+?\\d+")) {
            return valor.length() < 10
                    ? valor + "0"
                    : valor.substring(0, valor.length() - 1) + digitoDistinto(valor);
        }
        return valor.length() >= 10 ? valor.substring(0, valor.length() - 1) + "9" : valor + " E";
    }

    /** Digito con el que se reemplaza el ultimo de un codigo que ya llego al limite. */
    private static char digitoDistinto(String valor) {
        char ultimo = valor.charAt(valor.length() - 1);
        return ultimo == '9' ? '0' : (char) (ultimo + 1);
    }

    /**
     * Valor con el que se busca la fila DESPUES de editarla: cambia solo si la
     * edicion modifico el mismo campo que identifica al registro.
     */
    public String identificadorEditado() {
        return campoQueSeEdita.equals(campoQueIdentifica) || seEligeConCalendario()
                ? valorEditado()
                : identificador();
    }

    /** Este catalogo pide cargar una imagen para poder guardar. */
    public boolean pideImagen() {
        return pideImagen;
    }

    /** El elemento se da de alta eligiendo una fecha en el calendario. */
    public boolean seEligeConCalendario() {
        return DIAS_FESTIVOS.equals(catalogo);
    }
}
