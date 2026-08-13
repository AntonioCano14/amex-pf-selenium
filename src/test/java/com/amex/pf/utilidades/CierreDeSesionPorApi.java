package com.amex.pf.utilidades;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.time.Duration;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.amex.pf.base.Configuracion;

/**
 * RED DE SEGURIDAD. La aplicacion permite UNA SOLA sesion por usuario: si una
 * ejecucion termina sin cerrar sesion (el navegador se cae, se corta la corrida),
 * el usuario queda bloqueado en el servidor y las siguientes ejecuciones fallan
 * con "Usted ya cuenta con una sesion activa en otro dispositivo".
 *
 * Por eso, si no se pudo cerrar sesion desde la pantalla, se cierra llamando al
 * API con el token que la aplicacion guarda en localStorage.
 *
 * El API rechaza las peticiones sin la cabecera "Digest" (responde
 * TAMPERED_REQUEST), asi que hay que calcularla igual que la aplicacion web.
 */
public final class CierreDeSesionPorApi {

    private static final String LLAVE_DIGEST = "Kbac396Bo0icRmmp";
    private static final String IV_DIGEST = "YlBK34u1uG76THrr";

    /**
     * Donde se recuerda el token de la ultima sesion abierta, para poder cerrarla
     * aunque la ejecucion se haya cortado. No se versiona (carpeta resultados/).
     */
    private static final Path ARCHIVO_DE_TOKEN =
            Paths.get(System.getProperty("user.dir"), "resultados", "ultima-sesion.token");

    private CierreDeSesionPorApi() {
    }

    /** Guarda el token de la sesion en curso (sin mostrarlo en consola). */
    public static void recordarToken(String token) {
        if (token == null || token.isBlank()) {
            return;
        }
        try {
            Files.createDirectories(ARCHIVO_DE_TOKEN.getParent());
            Files.writeString(ARCHIVO_DE_TOKEN, token);
        } catch (IOException noSePudoGuardar) {
            System.err.println("AVISO: no se pudo recordar el token de la sesion: "
                    + noSePudoGuardar.getMessage());
        }
    }

    public static void olvidarToken() {
        try {
            Files.deleteIfExists(ARCHIVO_DE_TOKEN);
        } catch (IOException noSePudoBorrar) {
            // sin consecuencias: el token deja de servir al cerrar la sesion
        }
    }

    /**
     * Se llama al empezar la ejecucion: si la corrida anterior murio sin cerrar
     * sesion, la cierra con el token que quedo guardado. Asi el usuario no queda
     * bloqueado ("ya cuenta con una sesion activa en otro dispositivo").
     */
    public static void liberarSesionPendiente() {
        try {
            if (!Files.exists(ARCHIVO_DE_TOKEN)) {
                return;
            }
            boolean cerrada = cerrarSesion(Files.readString(ARCHIVO_DE_TOKEN).trim());
            olvidarToken();
            if (cerrada) {
                System.out.println("Se cerro una sesion que quedo abierta en la ejecucion anterior.");
            }
        } catch (IOException noSePudoLeer) {
            System.err.println("AVISO: no se pudo revisar la sesion anterior: "
                    + noSePudoLeer.getMessage());
        }
    }

    /** Cierra la sesion del token indicado. Devuelve true si el API respondio 200. */
    public static boolean cerrarSesion(String token) {
        if (token == null || token.isBlank()) {
            return false;
        }
        try {
            HttpResponse<String> respuesta = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(30)).build()
                    .send(HttpRequest.newBuilder()
                            .uri(URI.create(urlDelApi() + "users/logout"))
                            .header("Authorization", "Bearer " + token)
                            .header("Content-Type", "application/json")
                            .header("Digest", digest("null"))
                            .POST(HttpRequest.BodyPublishers.ofString("null"))
                            .timeout(Duration.ofSeconds(30))
                            .build(),
                            HttpResponse.BodyHandlers.ofString());
            return respuesta.statusCode() == 200;
        } catch (Exception noSePudo) {
            System.err.println("AVISO: no se pudo cerrar la sesion por API: " + noSePudo.getMessage());
            return false;
        }
    }

    /** De .../expediente/ a .../api/ */
    private static String urlDelApi() {
        String base = Configuracion.urlBase();
        if (!base.endsWith("/")) {
            base = base + "/";
        }
        return base.replace("/expediente/", "/api/");
    }

    /** sha-256=<AES-CBC(hex(sha256(cuerpo)))>, igual que la aplicacion web. */
    private static String digest(String cuerpo) throws Exception {
        StringBuilder hexadecimal = new StringBuilder();
        for (byte octeto : MessageDigest.getInstance("SHA-256")
                .digest(cuerpo.getBytes(StandardCharsets.UTF_8))) {
            hexadecimal.append(String.format("%02x", octeto));
        }
        Cipher cifrador = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cifrador.init(Cipher.ENCRYPT_MODE,
                new SecretKeySpec(LLAVE_DIGEST.getBytes(StandardCharsets.UTF_8), "AES"),
                new IvParameterSpec(IV_DIGEST.getBytes(StandardCharsets.UTF_8)));
        StringBuilder resultado = new StringBuilder("sha-256=");
        for (byte octeto : cifrador.doFinal(
                hexadecimal.toString().getBytes(StandardCharsets.UTF_8))) {
            resultado.append(String.format("%02x", octeto));
        }
        return resultado.toString();
    }
}
