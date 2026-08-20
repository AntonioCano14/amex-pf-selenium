package com.amex.pf.pruebas;

import java.util.Arrays;
import java.util.List;

import org.openqa.selenium.By;
import org.testng.Assert;
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
 * OLA 2 - Validaciones del formulario "Agregar usuario" de la pantalla Usuarios
 * (PF_CP_011 a PF_CP_021).
 *
 * Solo lectura: se escribe en los campos y se abren las listas, pero NUNCA se
 * presiona GUARDAR REGISTRO, asi que no se crea ningun usuario.
 *
 * Para agregar un caso de longitud maxima o de tipo de caracter se agrega un
 * renglon a la tabla (@DataProvider) correspondiente.
 */
public class UsuariosValidacionesPruebas extends PruebaBase {

    private PaginaPrincipal inicio;
    private PaginaUsuarios usuarios;

    @BeforeMethod(alwaysRun = true)
    public void abrirElAltaDeUsuario() {
        inicio = new PaginaLogin().iniciarSesionConCredencialesValidas();
        inicio.irAlMenu("Usuarios");
        usuarios = new PaginaUsuarios().abrirElAltaDeUsuario();
    }

    @Test(groups = {"validaciones", "usuarios"},
            description = "PF_CP_011 El alta de usuario muestra todos sus campos y botones")
    public void pfCp011FormularioDeAltaDeUsuario() {
        inicio.debeVerseElTexto("Area")
                .debeVerseElTexto("Tipo de usuario")
                .debeVerseElTexto("Nombre(s)")
                .debeVerseElTexto("Apellidos")
                .debeVerseElTexto("Cargo")
                .debeVerseElTexto("Correo electrónico")
                .debeVerseElTexto("Teléfono móvil")
                .debeVerseElTexto("Teléfono fijo")
                .elBotonDebeEstarVisible("GUARDAR REGISTRO")
                .elBotonDebeEstarVisible("CANCELAR");
    }

    @Test(groups = {"validaciones", "usuarios"},
            description = "PF_CP_012 La lista Area muestra las opciones esperadas")
    public void pfCp012ListaArea() {
        laListaDebeMostrar("Area", usuarios.areasDeLaLista(), PaginaUsuarios.areasEsperadas());
    }

    @Test(groups = {"validaciones", "usuarios"},
            description = "PF_CP_013 La lista Tipo de usuario muestra las opciones esperadas")
    public void pfCp013ListaTipoDeUsuario() {
        String area = PaginaUsuarios.areasEsperadas()[0];
        laListaDebeMostrar("Tipo de usuario", usuarios.tiposDeUsuarioDeLaLista(area),
                PaginaUsuarios.tiposDeUsuarioEsperados());
    }

    /** ID de la matriz | campo | maximo esperado. */
    @DataProvider(name = "camposDeTexto")
    public Object[][] camposDeTexto() {
        return new Object[][]{
                {"PF_CP_014 Nombres", Selectores.USUARIO_CAMPO_NOMBRES, 35},
                {"PF_CP_015 Apellidos", Selectores.USUARIO_CAMPO_APELLIDOS, 35},
                {"PF_CP_016 Cargo", Selectores.USUARIO_CAMPO_CARGO, 35},
        };
    }

    @Test(groups = {"validaciones", "usuarios"}, dataProvider = "camposDeTexto",
            description = "PF_CP_014-016 Maximo de 35 caracteres por campo")
    public void elCampoDebePermitir35Caracteres(String caso, By campo, int maximo) {
        int aceptados = usuarios.cuantosCaracteresAcepta(campo, maximo, "letras");
        Assert.assertEquals(aceptados, maximo,
                caso + ": el campo acepto " + aceptados + " caracteres y el maximo esperado es "
                        + maximo + ".");
    }

    @Test(groups = {"validaciones", "usuarios"}, dataProvider = "camposDeTexto",
            description = "PF_CP_014-016 No se permiten numeros ni caracteres especiales")
    public void elCampoNoDebePermitirNumerosNiEspeciales(String caso, By campo, int maximo) {
        String quedo = usuarios.loQueAcepta(campo, "Juan123!@#");
        Assert.assertEquals(quedo, "Juan",
                caso + ": se escribio \"Juan123!@#\" y el campo dejo \"" + quedo + "\".");
    }

    @Test(groups = {"validaciones", "usuarios"}, dataProvider = "camposDeTexto",
            description = "PF_CP_014-016 El campo es obligatorio (minimo un caracter)")
    public void elCampoVacioNoDebePermitirGuardar(String caso, By campo, int maximo) {
        usuarios.loQueAcepta(campo, "Ana");
        usuarios.limpiar(campo);
        usuarios.salirDelCampo(campo);
        Assert.assertTrue(usuarios.elBotonGuardarEstaDeshabilitado(),
                caso + ": con el campo vacio el boton GUARDAR REGISTRO debe quedar deshabilitado.");
    }

    @Test(groups = {"validaciones", "usuarios"},
            description = "PF_CP_017 El correo electronico exige formato de correo")
    public void pfCp017CampoCorreoElectronico() {
        for (String invalido : Configuracion.lista("amex.usuario.correos.invalidos")) {
            usuarios.loQueAcepta(Selectores.USUARIO_CAMPO_CORREO, invalido);
            usuarios.salirDelCampo(Selectores.USUARIO_CAMPO_CORREO);
            Assert.assertTrue(usuarios.elBotonGuardarEstaDeshabilitado(),
                    "El correo \"" + invalido + "\" no tiene formato de direccion de correo, "
                            + "asi que GUARDAR REGISTRO debia quedar deshabilitado.");
        }

        for (String valido : Configuracion.lista("amex.usuario.correos.validos")) {
            String quedo = usuarios.loQueAcepta(Selectores.USUARIO_CAMPO_CORREO, valido);
            usuarios.salirDelCampo(Selectores.USUARIO_CAMPO_CORREO);
            Assert.assertEquals(quedo, valido,
                    "El campo cambio el correo \"" + valido + "\": dejo \"" + quedo + "\".");
            Assert.assertFalse(usuarios.elCampoTieneErrorDeFormato(Selectores.USUARIO_CAMPO_CORREO),
                    "El correo \"" + valido + "\" tiene formato valido y la aplicacion lo marco "
                            + "como invalido.");
        }
    }

    /**
     * HALLAZGO DEF_02: hoy el campo Telefono movil acepta letras (se escribio
     * "abc12de345" y las dejo), y la matriz pide solo caracteres numericos. Queda
     * en el grupo "defecto_conocido" (excluido de la regresion) hasta que se
     * corrija en la aplicacion; cuando se corrija, se quita ese grupo.
     */
    @Test(groups = {"validaciones", "usuarios", "defecto_conocido"},
            description = "PF_CP_018 Telefono movil solo 10 caracteres numericos")
    public void pfCp018TelefonoMovil() {
        elTelefonoDebeAceptarSolo10Numeros(
                "PF_CP_018 Telefono movil", Selectores.USUARIO_CAMPO_TELEFONO_MOVIL);
    }

    @Test(groups = {"validaciones", "usuarios"},
            description = "PF_CP_019 Telefono fijo solo 10 caracteres numericos")
    public void pfCp019TelefonoFijo() {
        elTelefonoDebeAceptarSolo10Numeros(
                "PF_CP_019 Telefono fijo", Selectores.USUARIO_CAMPO_TELEFONO_FIJO);
    }

    @Test(groups = {"validaciones", "usuarios"},
            description = "PF_CP_021 Cancelar regresa a la pantalla de Usuarios sin guardar")
    public void pfCp021BotonCancelar() {
        usuarios.loQueAcepta(Selectores.USUARIO_CAMPO_NOMBRES, "Prueba");
        usuarios.cancelar();
        inicio.elBotonDebeEstarVisible("AGREGAR USUARIO")
                .laPantallaDebeTenerUnaTablaConInformacion();
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
                "La lista " + lista + " no muestra: " + faltantes
                        + ". La aplicacion muestra hoy: " + disponibles
                        + ". Si el ambiente cambio, ajuste configuracion.properties.");
        Assert.assertEquals(disponibles.size(), esperadas.length,
                "La lista " + lista + " muestra " + disponibles + " y se esperaban "
                        + Arrays.toString(esperadas) + ".");
    }
}
