package com.amex.pf.pruebas;

import java.util.Arrays;
import java.util.List;

import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.amex.pf.base.Configuracion;
import com.amex.pf.base.PruebaBase;
import com.amex.pf.paginas.PaginaLogin;
import com.amex.pf.paginas.PaginaPrincipal;
import com.amex.pf.paginas.PaginaUsuarios;
import com.amex.pf.paginas.Selectores;

/**
 * Validaciones del DETALLE del usuario en modo edicion (PF_CP_031 a PF_CP_038).
 *
 * Son las mismas reglas que pide la matriz para el alta (PF_CP_012 a PF_CP_019),
 * pero sobre "Ver detalle -> EDITAR DATOS": la matriz exige que el detalle las
 * respete igual.
 *
 * Solo lectura: se escribe en los campos y se abren las listas, pero NUNCA se
 * presiona GUARDAR; cada prueba sale del detalle con CANCELAR, asi que el usuario
 * de QA queda con sus datos originales.
 *
 * El usuario que se abre debe estar ACTIVO: la aplicacion no muestra EDITAR DATOS
 * en un usuario inactivo. Se toma el primer usuario de la tabla con numero de
 * empleado y, si esta Inactivo, la prueba lo activa para poder validar el detalle y
 * lo vuelve a dejar Inactivo al terminar (es el unico cambio que hace en el
 * ambiente; el detalle en si nunca se guarda).
 */
public class UsuariosDetalleValidacionesPruebas extends PruebaBase {

    private PaginaUsuarios usuarios;

    /** Numero de empleado del usuario sobre el que se valida el detalle. */
    private String numeroDeEmpleado = "";

    /** true cuando la prueba activo al usuario y debe dejarlo inactivo al terminar. */
    private boolean seActivoParaLaPrueba;

    @BeforeMethod(alwaysRun = true)
    public void abrirLaEdicionDelDetalle() {
        PaginaPrincipal inicio = new PaginaLogin().iniciarSesionConCredencialesValidas();
        inicio.irAlMenu("Usuarios");
        usuarios = new PaginaUsuarios().abrir();

        PaginaUsuarios.UsuarioDeLaTabla usuario = usuarios.primerUsuarioConNumeroDeEmpleado();
        numeroDeEmpleado = usuario.numeroDeEmpleado();
        // Se marca antes de activar: si la activacion falla a medias, el @AfterMethod
        // igual intenta devolverle su estatus original.
        seActivoParaLaPrueba = !usuario.estaActivo();
        if (seActivoParaLaPrueba) {
            // Sin esto el detalle no muestra EDITAR DATOS y no hay nada que validar.
            usuarios.activarAlUsuario(numeroDeEmpleado);
        }

        usuarios.abrirElDetalleDelUsuario(numeroDeEmpleado).editarLosDatosDelDetalle();
    }

    /**
     * Sale del detalle con CANCELAR aunque la prueba haya fallado (asi los datos que
     * se escribieron para validar nunca se guardan) y devuelve al usuario su estatus
     * original si la prueba lo tuvo que activar.
     */
    @AfterMethod(alwaysRun = true)
    public void cancelarLaEdicion() {
        if (usuarios == null) {
            return;
        }
        try {
            usuarios.cerrarElDetalle();
        } catch (RuntimeException yaEstabaCerrado) {
            // La prueba pudo fallar antes de abrir el detalle: no hay nada que cancelar.
        }
        if (seActivoParaLaPrueba) {
            usuarios.desactivarSiQuedoActivo(numeroDeEmpleado);
        }
    }

    @Test(groups = {"validaciones", "usuarios"},
            description = "PF_CP_031 La lista Area del detalle muestra las opciones esperadas")
    public void pfCp031ListaAreaDelDetalle() {
        laListaDebeMostrar("Area", usuarios.areasDelDetalle(), PaginaUsuarios.areasEsperadas());
    }

    @Test(groups = {"validaciones", "usuarios"},
            description = "PF_CP_032 La lista Tipo de usuario del detalle muestra las opciones "
                    + "esperadas")
    public void pfCp032ListaTipoDeUsuarioDelDetalle() {
        laListaDebeMostrar("Tipo de usuario", usuarios.tiposDeUsuarioDelDetalle(),
                PaginaUsuarios.tiposDeUsuarioEsperados());
    }

    @Test(groups = {"validaciones", "usuarios"},
            description = "PF_CP_031-038 El detalle muestra el numero de empleado de la tabla")
    public void elDetalleMuestraElNumeroDeEmpleadoDeLaTabla() {
        String enLaTabla = usuarios.numeroDeEmpleadoDeLaTabla();
        String enElDetalle =
                usuarios.valorDelDetalle(Selectores.USUARIO_DETALLE_CAMPO_NUMERO_DE_EMPLEADO);
        Assert.assertEquals(enElDetalle, enLaTabla,
                "La tabla muestra el numero de empleado \"" + enLaTabla
                        + "\" y el detalle muestra \"" + enElDetalle + "\".");
    }

    /** ID de la matriz | campo del detalle | maximo esperado. */
    @DataProvider(name = "camposDeTextoDelDetalle")
    public Object[][] camposDeTextoDelDetalle() {
        return new Object[][]{
                {"PF_CP_033 Nombres", Selectores.USUARIO_DETALLE_CAMPO_NOMBRES, 35},
                {"PF_CP_034 Apellidos", Selectores.USUARIO_DETALLE_CAMPO_APELLIDOS, 35},
                {"PF_CP_035 Cargo", Selectores.USUARIO_DETALLE_CAMPO_CARGO, 35},
        };
    }

    @Test(groups = {"validaciones", "usuarios"}, dataProvider = "camposDeTextoDelDetalle",
            description = "PF_CP_033-035 Maximo de 35 caracteres por campo del detalle")
    public void elCampoDelDetalleDebePermitir35Caracteres(String caso, By campo, int maximo) {
        int aceptados = usuarios.cuantosCaracteresAcepta(campo, maximo, "letras");
        Assert.assertEquals(aceptados, maximo,
                caso + ": el campo acepto " + aceptados + " caracteres y el maximo esperado es "
                        + maximo + ".");
    }

    @Test(groups = {"validaciones", "usuarios"}, dataProvider = "camposDeTextoDelDetalle",
            description = "PF_CP_033-035 El detalle no permite numeros ni caracteres especiales")
    public void elCampoDelDetalleNoDebePermitirNumerosNiEspeciales(String caso, By campo,
            int maximo) {
        String quedo = usuarios.loQueAcepta(campo, "Juan123!@#");
        Assert.assertEquals(quedo, "Juan",
                caso + ": se escribio \"Juan123!@#\" y el campo dejo \"" + quedo + "\".");
    }

    @Test(groups = {"validaciones", "usuarios"},
            dataProvider = "camposDeTextoDelDetalle",
            description = "PF_CP_033-035 El campo del detalle es obligatorio (minimo un caracter)")
    public void elCampoVacioDelDetalleNoDebePermitirGuardar(String caso, By campo, int maximo) {
        usuarios.limpiar(campo);
        usuarios.salirDelCampo(campo);
        elDetalleDebeRechazarLoCapturado(caso + ": el campo quedo vacio y el detalle debe "
                + "rechazarlo (minimo un caracter)", campo);
    }

    /**
     * El detalle avisa distinto que el alta: en unos casos marca el campo en rojo
     * (aria-invalid) y en otros deshabilita GUARDAR. Se acepta cualquiera de las
     * dos formas, lo que no se acepta es que el dato invalido pase sin aviso.
     */
    private void elDetalleDebeRechazarLoCapturado(String mensaje, By campo) {
        Assert.assertTrue(usuarios.elCampoTieneErrorDeFormato(campo)
                        || usuarios.elBotonGuardarDelDetalleEstaDeshabilitado(),
                mensaje + ": ni el campo quedo marcado como invalido ni GUARDAR quedo "
                        + "deshabilitado.");
    }

    @Test(groups = {"validaciones", "usuarios"},
            description = "PF_CP_036 El correo del detalle exige formato de correo")
    public void pfCp036CampoCorreoElectronicoDelDetalle() {
        By campo = Selectores.USUARIO_DETALLE_CAMPO_CORREO;
        for (String invalido : Configuracion.lista("amex.usuario.correos.invalidos")) {
            String quedo = usuarios.loQueAcepta(campo, invalido);
            usuarios.salirDelCampo(campo);
            if (!quedo.equals(invalido)) {
                // El campo filtro caracteres (por ejemplo el espacio): ya no es el
                // texto invalido que se quiso probar.
                continue;
            }
            elDetalleDebeRechazarLoCapturado(
                    "El correo \"" + invalido + "\" no tiene formato de direccion de correo",
                    campo);
        }

        for (String valido : Configuracion.lista("amex.usuario.correos.validos")) {
            String quedo = usuarios.loQueAcepta(campo, valido);
            usuarios.salirDelCampo(campo);
            Assert.assertEquals(quedo, valido,
                    "El campo cambio el correo \"" + valido + "\": dejo \"" + quedo + "\".");
            Assert.assertFalse(usuarios.elCampoTieneErrorDeFormato(campo),
                    "El correo \"" + valido + "\" tiene formato valido y la aplicacion lo marco "
                            + "como invalido.");
        }
    }

    /**
     * HALLAZGO DEF_02: igual que en el alta (PF_CP_018), el Telefono movil del
     * detalle acepta letras y la matriz pide solo numeros. Queda en el grupo
     * "defecto_conocido" (fuera de la regresion) hasta que se corrija.
     */
    @Test(groups = {"validaciones", "usuarios", "defecto_conocido"},
            description = "PF_CP_037 Telefono movil del detalle solo 10 caracteres numericos")
    public void pfCp037TelefonoMovilDelDetalle() {
        elTelefonoDebeAceptarSolo10Numeros("PF_CP_037 Telefono movil",
                Selectores.USUARIO_DETALLE_CAMPO_TELEFONO_MOVIL);
    }

    @Test(groups = {"validaciones", "usuarios"},
            description = "PF_CP_038 Telefono fijo del detalle solo 10 caracteres numericos")
    public void pfCp038TelefonoFijoDelDetalle() {
        elTelefonoDebeAceptarSolo10Numeros("PF_CP_038 Telefono fijo",
                Selectores.USUARIO_DETALLE_CAMPO_TELEFONO_FIJO);
    }

    private void elTelefonoDebeAceptarSolo10Numeros(String caso, By campo) {
        String conLetras = usuarios.loQueAcepta(campo, "abc12de345fg");
        Assert.assertEquals(conLetras.replaceAll("\\D", ""), conLetras,
                caso + ": se escribio \"abc12de345fg\" y el campo dejo \"" + conLetras
                        + "\" (debe aceptar solo numeros).");

        int aceptados = usuarios.cuantosCaracteresAcepta(campo, 10, "numeros");
        Assert.assertEquals(aceptados, 10,
                caso + ": el campo acepto " + aceptados + " numeros y el maximo esperado es 10.");
    }

    private void laListaDebeMostrar(String lista, List<String> disponibles, String[] esperadas) {
        List<String> faltantes = Arrays.stream(esperadas)
                .filter(esperada -> disponibles.stream()
                        .noneMatch(actual -> actual.equalsIgnoreCase(esperada.trim())))
                .toList();
        Assert.assertTrue(faltantes.isEmpty(),
                "La lista " + lista + " del detalle no muestra: " + faltantes
                        + ". La aplicacion muestra hoy: " + disponibles
                        + ". Si el ambiente cambio, ajuste configuracion.properties.");
        Assert.assertEquals(disponibles.size(), esperadas.length,
                "La lista " + lista + " del detalle muestra " + disponibles + " y se esperaban "
                        + Arrays.toString(esperadas) + ".");
    }
}
