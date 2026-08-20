package com.amex.pf.datos;

import com.amex.pf.base.Configuracion;

/**
 * Datos del usuario que da de alta la ola 5 (PF_CP_020 y siguientes).
 *
 * El nombre lleva el prefijo de automatizacion (amex.datos.prefijo), y el numero de
 * empleado y el correo llevan un numero distinto en cada ejecucion, porque la
 * aplicacion no permite repetirlos. Asi los usuarios creados por las pruebas se
 * reconocen a simple vista en la tabla y la propia prueba los deja desactivados al
 * terminar.
 *
 * Reglas del formulario comprobadas en el ambiente: Nombre(s) y Apellidos aceptan
 * solo letras (los numeros se descartan al escribirlos), los telefonos exigen 10
 * digitos y el Numero de empleado es obligatorio; por eso el usuario se localiza en
 * la tabla por su numero de empleado y no por su nombre.
 */
public final class UsuarioDePrueba {

    /** Digitos que tiene el numero de empleado y los telefonos que se generan. */
    private static final int DIGITOS_DEL_NUMERO = 8;
    private static final int DIGITOS_DEL_TELEFONO = 10;

    private final String numeroDeEmpleado;
    private final String nombres;
    private final String apellidos;
    private final String cargo;
    private final String correo;
    private final String telefonoMovil;
    private final String telefonoFijo;

    private UsuarioDePrueba(String numeroDeEmpleado, String nombres, String apellidos,
            String cargo, String correo, String telefonoMovil, String telefonoFijo) {
        this.numeroDeEmpleado = numeroDeEmpleado;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.cargo = cargo;
        this.correo = correo;
        this.telefonoMovil = telefonoMovil;
        this.telefonoFijo = telefonoFijo;
    }

    /** Usuario nuevo para esta ejecucion. El sufijo es el numero que lo hace unico. */
    public static UsuarioDePrueba nuevo(String sufijo) {
        String numero = digitos(sufijo, DIGITOS_DEL_NUMERO);
        String telefono = digitos(sufijo, DIGITOS_DEL_TELEFONO);
        return new UsuarioDePrueba(
                numero,
                // Nombre y apellidos, solo letras: la pantalla descarta los numeros.
                ElementoDeCatalogo.prefijo(),
                "AUTOMATIZACION",
                "TESTER AUTOMATIZACION",
                // Sin "+" en el correo: el formulario lo rechaza (DEF_01 de la ola 1).
                "qa.automatizacion." + numero + "@" + Configuracion.obtener("amex.datos.correo"),
                telefono,
                telefono);
    }

    /** Numero de la longitud pedida, tomado del sufijo de la ejecucion. */
    private static String digitos(String sufijo, int cuantos) {
        String soloNumeros = sufijo.replaceAll("\\D", "");
        String relleno = ("1" + soloNumeros).repeat(1 + cuantos / Math.max(1, soloNumeros.length()));
        return relleno.substring(relleno.length() - cuantos);
    }

    /** Area con la que se da de alta (amex.usuario.areas, primera de la lista). */
    public static String area() {
        return Configuracion.listaDelUsuario("amex.usuario.areas")[0];
    }

    /** Tipo de usuario con el que se da de alta (amex.datos.usuario.tipo). */
    public static String tipoDeUsuario() {
        return Configuracion.obtener("amex.datos.usuario.tipo");
    }

    /** Codigo de pais que se elige en el alta (amex.datos.usuario.codigo.pais). */
    public static String codigoDePais() {
        return Configuracion.obtener("amex.datos.usuario.codigo.pais");
    }

    /** Numero de empleado: identifica a este usuario en la tabla. */
    public String numeroDeEmpleado() {
        return numeroDeEmpleado;
    }

    public String nombres() {
        return nombres;
    }

    public String apellidos() {
        return apellidos;
    }

    public String cargo() {
        return cargo;
    }

    /** Cargo con el que se comprueba la edicion (PF_CP_039). */
    public String cargoEditado() {
        return "TESTER EDITADO";
    }

    public String correo() {
        return correo;
    }

    public String telefonoMovil() {
        return telefonoMovil;
    }

    public String telefonoFijo() {
        return telefonoFijo;
    }

    /** Texto con el que se busca al usuario en la tabla. */
    public String nombreCompleto() {
        return nombres + " " + apellidos;
    }

    /** Como se describe al usuario en los mensajes de las pruebas. */
    @Override
    public String toString() {
        return nombreCompleto() + " (numero de empleado " + numeroDeEmpleado + ")";
    }
}
