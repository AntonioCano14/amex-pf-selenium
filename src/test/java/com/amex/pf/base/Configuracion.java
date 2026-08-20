package com.amex.pf.base;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * Unico lugar donde se lee la configuracion del ambiente.
 *
 * Orden de prioridad:
 * 1. Variable de entorno   (AMEX_USUARIO=... mvn test)
 * 2. Propiedad de sistema  (mvn test -Damex.usuario=...)
 * 3. configuracion.properties
 *
 * La contrasena NO se guarda en el repositorio: se pasa por variable de entorno
 * AMEX_CONTRASENA o por -Damex.contrasena=...
 */
public final class Configuracion {

    private static final Properties ARCHIVO = new Properties();

    static {
        try (InputStream entrada = Configuracion.class.getClassLoader()
                .getResourceAsStream("configuracion.properties")) {
            if (entrada != null) {
                // UTF-8 explicito: si no, los acentos se leen mal ("Campana").
                ARCHIVO.load(new InputStreamReader(entrada, StandardCharsets.UTF_8));
            }
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo leer configuracion.properties", e);
        }
    }

    private Configuracion() {
    }

    public static String obtener(String clave) {
        String variableEntorno = System.getenv(clave.toUpperCase().replace('.', '_'));
        if (variableEntorno != null && !variableEntorno.isBlank()) {
            return variableEntorno;
        }
        String propiedad = System.getProperty(clave);
        if (propiedad != null && !propiedad.isBlank()) {
            return propiedad;
        }
        return ARCHIVO.getProperty(clave, "");
    }

    public static String urlBase() {
        return obtener("amex.url");
    }

    public static String usuario() {
        return obtener("amex.usuario");
    }

    public static String contrasena() {
        String valor = obtener("amex.contrasena");
        if (valor.isBlank()) {
            throw new IllegalStateException(
                    "Falta la contrasena. Ejecute con AMEX_CONTRASENA=... mvn test "
                            + "o con -Damex.contrasena=... (ver README).");
        }
        return valor;
    }

    public static String navegador() {
        return obtener("amex.navegador");
    }

    public static boolean sinInterfaz() {
        return Boolean.parseBoolean(obtener("amex.headless"));
    }

    public static int esperaMaximaSegundos() {
        return Integer.parseInt(obtener("amex.espera"));
    }

    /** Lee una propiedad con varios valores separados por coma. */
    public static String[] lista(String clave) {
        return obtener(clave).split("\\s*,\\s*");
    }

    /**
     * Lista que depende del usuario con el que se ejecuta: primero busca
     * "clave.usuario" (por ejemplo amex.usuario.areas.admin-centurion@na-at.com)
     * y si no existe usa la clave general. Cada perfil ve opciones distintas.
     */
    public static String[] listaDelUsuario(String clave) {
        String delUsuario = obtener(clave + "." + usuario());
        return delUsuario.isBlank() ? lista(clave) : delUsuario.split("\\s*,\\s*");
    }
}
