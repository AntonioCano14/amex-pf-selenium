# Guia caso por caso: que hace el codigo de cada caso

Generado por `herramientas/generar_trazabilidad.py`; no se edita a mano.

Por cada caso de la matriz encontraras: que pide la matriz, con que prueba se
automatizo, **el comando para correr ese caso solo**, los pasos que ejecuta y el
codigo tal como esta en el repositorio.

## Como correr un caso solo

```bash
mvn test -Dtest='LoginPruebas#pfCp001UsuarioCorrectoContrasenaIncorrecta'
mvn test -Dtest='LoginPruebas#pfCp001UsuarioCorrectoContrasenaIncorrecta' -Damex.headless=false
```

- `-Dtest='Clase#metodo'` corre un solo caso; `-Dtest='Clase'` corre toda la clase.
- `-Damex.headless=false` abre el navegador para verlo con tus propios ojos.
- La consola imprime `[PF_CP_001] APROBADO ...` o `FALLIDO` con el motivo, y si
  falla deja una captura de pantalla en `resultados/`.
- Antes de cada caso se abre un navegador limpio y al terminar se cierra la sesion:
  cada caso se puede correr solo, en cualquier orden.

## Como leer el codigo de una prueba

La relacion completa entre carpetas (pruebas, paginas, selectores, datos, utilidades)
esta explicada en `COMO_FUNCIONA_EL_CODIGO.md`.

Las pruebas no hablan de HTML: llaman metodos de las clases de pantalla (`PaginaLogin`,
`PaginaUsuarios`, ...) que se leen como pasos manuales. Ejemplo del caso 1:

```java
PaginaLogin login = new PaginaLogin();                                  // 1. estoy en la pantalla de login
login.iniciarSesionCon(Configuracion.usuario(), CONTRASENA_INCORRECTA); // 2. capturo usuario correcto + contrasena mala
Assert.assertTrue(login.textoDelModal().contains(TEXTO_CREDENCIALES_INVALIDAS)); // 3. el popup debe decir Credenciales invalidas
login.aceptarModal();                                                   // 4. presiono ACEPTAR
Assert.assertTrue(login.sigueEnLaPantallaDeLogin());                    // 5. no debio entrar
```

- `Assert.assertTrue(...)` = "esto tiene que ser cierto"; si no lo es, el caso sale
  FALLIDO con el mensaje que va al final.
- `Assert.assertFalse(...)` = lo contrario ("esto NO debe pasar").
- Los metodos que empiezan con `debe...` ya traen su propia verificacion adentro.
- El usuario y la URL salen de `configuracion.properties`; la contrasena de la
  variable de ambiente `AMEX_CONTRASENA`, nunca del codigo.
- Los selectores (como encuentra cada boton) estan todos en `Selectores.java`: si la
  aplicacion cambia un boton, se corrige ahi y no en las pruebas.

## PF_CP_001 — Validación de inicio de sesión con usuario correcto, contraseña incorrecta

- **Modulo:** Expediente Login
- **Lo que pide la matriz:** Muestra un popup indicando:AMEX Argentina Ha ocurrido un error: Credenciales invalidas Botón Aceptar
- **Prueba:** `LoginPruebas#pfCp001UsuarioCorrectoContrasenaIncorrecta`  (etiquetas: login, humo)
- **Lo que valida el codigo:** Usuario correcto y contrasena incorrecta
- **Correr solo este caso:** `mvn test -Dtest='LoginPruebas#pfCp001UsuarioCorrectoContrasenaIncorrecta'`
- **Pasos que ejecuta:** `iniciarSesionCon` -> `textoDelModal` -> `aceptarModal` -> `sigueEnLaPantallaDeLogin`
- **Verificaciones:** 2

```java
        PaginaLogin login = new PaginaLogin();
        login.iniciarSesionCon(Configuracion.usuario(), CONTRASENA_INCORRECTA);
        Assert.assertTrue(login.textoDelModal().contains(PaginaLogin.TEXTO_CREDENCIALES_INVALIDAS),
                "Se esperaba el mensaje \"" + PaginaLogin.TEXTO_CREDENCIALES_INVALIDAS + "\".");
        login.aceptarModal();
        Assert.assertTrue(login.sigueEnLaPantallaDeLogin(), "No debio ingresar a la aplicacion.");
```

## PF_CP_002 — Validación de inicio de sesión con usuario incorrecto, contraseña correcta

- **Modulo:** Expediente Login
- **Lo que pide la matriz:** Muestra un popup indicando:AMEX Argentina Ha ocurrido un error: Credenciales invalidas Botón Aceptar
- **Prueba:** `LoginPruebas#pfCp002UsuarioIncorrectoContrasenaCorrecta`  (etiquetas: login)
- **Lo que valida el codigo:** Usuario incorrecto y contrasena correcta
- **Correr solo este caso:** `mvn test -Dtest='LoginPruebas#pfCp002UsuarioIncorrectoContrasenaCorrecta'`
- **Pasos que ejecuta:** `iniciarSesionCon` -> `textoDelModal` -> `aceptarModal` -> `sigueEnLaPantallaDeLogin`
- **Verificaciones:** 2

```java
        PaginaLogin login = new PaginaLogin();
        login.iniciarSesionCon(USUARIO_INEXISTENTE, Configuracion.contrasena());
        Assert.assertTrue(login.textoDelModal().contains(PaginaLogin.TEXTO_CREDENCIALES_INVALIDAS),
                "El mensaje no debe revelar si el usuario existe.");
        login.aceptarModal();
        Assert.assertTrue(login.sigueEnLaPantallaDeLogin(), "No debio ingresar a la aplicacion.");
```

## PF_CP_003 — Validación de inicio de sesión con usuario incorrecto, contraseña incorrecta

- **Modulo:** Expediente Login
- **Lo que pide la matriz:** Muestra un popup indicando:AMEX Argentina Ha ocurrido un error: Credenciales invalidas Botón Aceptar
- **Prueba:** `LoginPruebas#pfCp003UsuarioYContrasenaIncorrectos`  (etiquetas: login)
- **Lo que valida el codigo:** Usuario y contrasena incorrectos
- **Correr solo este caso:** `mvn test -Dtest='LoginPruebas#pfCp003UsuarioYContrasenaIncorrectos'`
- **Pasos que ejecuta:** `iniciarSesionCon` -> `textoDelModal` -> `aceptarModal` -> `sigueEnLaPantallaDeLogin`
- **Verificaciones:** 2

```java
        PaginaLogin login = new PaginaLogin();
        login.iniciarSesionCon(USUARIO_INEXISTENTE, CONTRASENA_INCORRECTA);
        Assert.assertTrue(login.textoDelModal().contains(PaginaLogin.TEXTO_CREDENCIALES_INVALIDAS),
                "El mensaje no debe revelar si el usuario existe.");
        login.aceptarModal();
        Assert.assertTrue(login.sigueEnLaPantallaDeLogin(), "No debio ingresar a la aplicacion.");
```

## PF_CP_004 — Validación de inicio de sesión con usuario correcto, contraseña correcta

- **Modulo:** Expediente Login
- **Lo que pide la matriz:** Muestra la pantalla de inicio del expediente
- **Prueba:** `LoginPruebas#pfCp004UsuarioYContrasenaCorrectos`  (etiquetas: login, humo)
- **Lo que valida el codigo:** Usuario y contrasena correctos
- **Correr solo este caso:** `mvn test -Dtest='LoginPruebas#pfCp004UsuarioYContrasenaCorrectos'`
- **Pasos que ejecuta:** `iniciarSesionConCredencialesValidas` -> `debeVerseElTexto`
- **Verificaciones:** 1

```java
        PaginaPrincipal inicio = new PaginaLogin().iniciarSesionConCredencialesValidas();
        inicio.debeVerseElTexto("Hola,");
```

## PF_CP_005 — Validación de la recuperación de contraseña

- **Modulo:** Expediente Login
- **Lo que pide la matriz:** Redirige a una pantalla para la recuperacion de contraseña
- **Automatizado:** no. La matriz lo marca Cancelado.

## PF_CP_006 — Cambiar contraseña

- **Modulo:** Cambiar contraseña
- **Lo que pide la matriz:** El sistema muestra una pantalla para colocar la contraseña actual y la nueva contraseña, con el botón Guardar deshabilitado y el botón Cancelar
- **Automatizado:** no. La matriz lo marca Cancelado.

## PF_CP_007 — Características contraseña

- **Modulo:** Cambiar contraseña
- **Lo que pide la matriz:** El sistema muestra las siguientes caracterísitcas para la nueva contraseña: Mínimo 13 caracteres. Al menos una letra (mayúscula o minúscula). Al menos un número. Al menos un carácter especial: #?!@%^&*- .()- No más de dos letras o números iguales consecutivos Solo se permiten letras, números y los caracteres especiales indicados
- **Automatizado:** no. La matriz lo marca Cancelado.

## PF_CP_008 — Validacion de pantalla de inicio

- **Modulo:** Expediente pantalla Inicio
- **Lo que pide la matriz:** Se mostrara una pantalla con la informacion de todas las solicitudes realizadas, se mostrara una grafica con la cantidad de solicitudes en cada estado: -Aprobadas -Denegadas -Creadas -Condicionada a ingresos -Ingreso -Aviso de privacidad -Identificacion oficial -Validacion de identidad -Terminos y condiciones -Pendiente de firma -Por dictaminar -Cancelado Se muestran los porcentajes de cada una.y que usuario es el que la administró
- **Prueba:** `NavegacionPruebas#pfCp008GraficaDeInicio`  (etiquetas: navegacion, humo)
- **Lo que valida el codigo:** Pantalla de Inicio con la grafica de solicitudes
- **Correr solo este caso:** `mvn test -Dtest='NavegacionPruebas#pfCp008GraficaDeInicio'`
- **Pasos que ejecuta:** `laDireccionDebeContener` -> `debeVerseUnaGrafica` -> `leyendasDeLaGrafica` -> `detallePorEstatusDeLaGrafica` -> `toUpperCase` -> `startsWith` -> `entrySet` -> `getKey` -> `findFirst` -> `orElse` -> `endsWith`
- **Verificaciones:** 4

```java
        inicio.laDireccionDebeContener("expedient/home").debeVerseUnaGrafica();

        List<String> leyendas = inicio.leyendasDeLaGrafica();
        Map<String, String> detalle = inicio.detallePorEstatusDeLaGrafica();
        for (String estatus : Configuracion.lista("amex.inicio.estatus")) {
            Assert.assertTrue(leyendas.stream()
                            .anyMatch(leyenda -> leyenda.toUpperCase()
                                    .startsWith(estatus.toUpperCase() + " -")),
                    "La grafica no muestra la cantidad de solicitudes del estatus \"" + estatus
                            + "\". Muestra hoy: " + leyendas + ".");

            String porcentaje = detalle.entrySet().stream()
                    .filter(dato -> dato.getKey().equalsIgnoreCase(estatus))
                    .map(Map.Entry::getValue)
                    .findFirst()
                    .orElse("");
            Assert.assertTrue(porcentaje.endsWith("%"),
                    "La grafica no muestra el porcentaje del estatus \"" + estatus
                            + "\". Muestra hoy: " + detalle + ".");
        }
```

## PF_CP_009 — Validacion de pantalla de inicio

- **Modulo:** Expediente pantalla Inicio
- **Lo que pide la matriz:** Se mostrará una seccion donde se muestra toda la informacion acerca de la solicitud realizada, en esta seccion se mostrara la informacion de la solicitud: - Referencia - Fecha y hr. inicio - Fecha y hr. fin - Rol - Cliente - Usuario Modificó - Estatus
- **Prueba:** `NavegacionPruebas#pfCp009TablaDeInicio`  (etiquetas: navegacion)
- **Lo que valida el codigo:** Pantalla de Inicio con la tabla de solicitudes
- **Correr solo este caso:** `mvn test -Dtest='NavegacionPruebas#pfCp009TablaDeInicio'`
- **Pasos que ejecuta:** `laPantallaDebeTenerUnaTablaConInformacion` -> `encabezadosDeLaTabla`
- **Verificaciones:** 2

```java
        inicio.laPantallaDebeTenerUnaTablaConInformacion();

        List<String> actuales = inicio.encabezadosDeLaTabla();
        for (String columna : Configuracion.lista("amex.inicio.columnas")) {
            Assert.assertTrue(actuales.stream().anyMatch(actual -> actual.equalsIgnoreCase(columna)),
                    "La tabla de Inicio no muestra la columna \"" + columna
                            + "\". Muestra hoy: " + actuales + ".");
        }
```

## PF_CP_010 — Validación de la pantalla de Usuarios

- **Modulo:** Expediente pantalla usuario
- **Lo que pide la matriz:** Muestra la pantalla de usuarios con los botones de agregar usuario, carga masiva de usuarios, actualizar tabla, exportar a excel, filtrar y muestra toda la información de los usuarios existentes.
- **Prueba:** `NavegacionPruebas#pfCp010PantallaUsuarios`  (etiquetas: navegacion)
- **Lo que valida el codigo:** Pantalla de Usuarios con sus botones
- **Correr solo este caso:** `mvn test -Dtest='NavegacionPruebas#pfCp010PantallaUsuarios'`
- **Pasos que ejecuta:** `irAlMenu` -> `laDireccionDebeContener` -> `losBotonesDebenEstarVisibles` -> `laPantallaDebeTenerUnaTablaConInformacion`
- **Verificaciones:** 3

```java
        inicio.irAlMenu("Usuarios")
                .laDireccionDebeContener("expedient/users")
                .losBotonesDebenEstarVisibles(Configuracion.lista("amex.usuarios.botones"))
                .laPantallaDebeTenerUnaTablaConInformacion();
```

## PF_CP_011 — Validación de la funcionalidad del botón Agregar Usuario

- **Modulo:** Expediente pantalla usuario
- **Lo que pide la matriz:** Muestra una nueva pantalla donde se debera ingresar la infomracion del usuario a crear: -Area -Tipo de usuarios -Nombre(s) -Apellidos -Cargo -Correo Electronico -Prefijo telefonico -Telefono movil -Telefono fijo -Boton Cancelar -Boton guardar registro
- **Prueba:** `UsuariosValidacionesPruebas#pfCp011FormularioDeAltaDeUsuario`  (etiquetas: validaciones, usuarios)
- **Lo que valida el codigo:** El alta de usuario muestra todos sus campos y botones
- **Correr solo este caso:** `mvn test -Dtest='UsuariosValidacionesPruebas#pfCp011FormularioDeAltaDeUsuario'`
- **Pasos que ejecuta:** `debeVerseElTexto` -> `elBotonDebeEstarVisible`
- **Verificaciones:** 10

```java
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
```

## PF_CP_012 — Validar campo Area

- **Modulo:** Expediente pantalla usuario
- **Lo que pide la matriz:** Validar que se muestre solo la opción -Ventas
- **Prueba:** `UsuariosValidacionesPruebas#pfCp012ListaArea`  (etiquetas: validaciones, usuarios)
- **Lo que valida el codigo:** La lista Area muestra las opciones esperadas
- **Correr solo este caso:** `mvn test -Dtest='UsuariosValidacionesPruebas#pfCp012ListaArea'`
- **Pasos que ejecuta:** `areasDeLaLista`
- **Verificaciones:** 1

```java
        laListaDebeMostrar("Area", usuarios.areasDeLaLista(), PaginaUsuarios.areasEsperadas());
```

## PF_CP_013 — Validar campo Tipo de usuario

- **Modulo:** Expediente pantalla usuario
- **Lo que pide la matriz:** Validar que se muestre solo la opción -Administrador Apex -Supervisor AXP -Usuario AXP -Supervisor Agencia -Usuario Agencia
- **Prueba:** `UsuariosValidacionesPruebas#pfCp013ListaTipoDeUsuario`  (etiquetas: validaciones, usuarios)
- **Lo que valida el codigo:** La lista Tipo de usuario muestra las opciones esperadas
- **Correr solo este caso:** `mvn test -Dtest='UsuariosValidacionesPruebas#pfCp013ListaTipoDeUsuario'`
- **Pasos que ejecuta:** `tiposDeUsuarioDeLaLista`
- **Verificaciones:** 1

```java
        String area = PaginaUsuarios.areasEsperadas()[0];
        laListaDebeMostrar("Tipo de usuario", usuarios.tiposDeUsuarioDeLaLista(area),
                PaginaUsuarios.tiposDeUsuarioEsperados());
```

## PF_CP_014 — Validar Campo Nombres

- **Modulo:** Expediente pantalla usuario
- **Lo que pide la matriz:** Validar que permita -35 caracteres maximo -1 caracter Minimo -No permite caracteres numericos y especiales
- **Prueba:** `UsuariosValidacionesPruebas#elCampoDebePermitir35Caracteres`  (etiquetas: validaciones, usuarios)
- **Lo que valida el codigo:** Maximo de 35 caracteres por campo
- **Correr solo este caso:** `mvn test -Dtest='UsuariosValidacionesPruebas#elCampoDebePermitir35Caracteres'`
- **Renglon de la tabla para este caso:** `{"PF_CP_014 Nombres", Selectores.USUARIO_CAMPO_NOMBRES, 35}`
- **Ojo:** este metodo cubre varios casos con la tabla `@DataProvider("camposDeTexto")`; el comando los corre todos y la consola imprime el ID de cada uno.
- **Pasos que ejecuta:** `cuantosCaracteresAcepta`
- **Verificaciones:** 1

```java
        int aceptados = usuarios.cuantosCaracteresAcepta(campo, maximo, "letras");
        Assert.assertEquals(aceptados, maximo,
                caso + ": el campo acepto " + aceptados + " caracteres y el maximo esperado es "
                        + maximo + ".");
```
- **Prueba:** `UsuariosValidacionesPruebas#elCampoNoDebePermitirNumerosNiEspeciales`  (etiquetas: validaciones, usuarios)
- **Lo que valida el codigo:** No se permiten numeros ni caracteres especiales
- **Correr solo este caso:** `mvn test -Dtest='UsuariosValidacionesPruebas#elCampoNoDebePermitirNumerosNiEspeciales'`
- **Renglon de la tabla para este caso:** `{"PF_CP_014 Nombres", Selectores.USUARIO_CAMPO_NOMBRES, 35}`
- **Ojo:** este metodo cubre varios casos con la tabla `@DataProvider("camposDeTexto")`; el comando los corre todos y la consola imprime el ID de cada uno.
- **Pasos que ejecuta:** `loQueAcepta`
- **Verificaciones:** 1

```java
        String quedo = usuarios.loQueAcepta(campo, "Juan123!@#");
        Assert.assertEquals(quedo, "Juan",
                caso + ": se escribio \"Juan123!@#\" y el campo dejo \"" + quedo + "\".");
```
- **Prueba:** `UsuariosValidacionesPruebas#elCampoVacioNoDebePermitirGuardar`  (etiquetas: validaciones, usuarios)
- **Lo que valida el codigo:** El campo es obligatorio (minimo un caracter)
- **Correr solo este caso:** `mvn test -Dtest='UsuariosValidacionesPruebas#elCampoVacioNoDebePermitirGuardar'`
- **Renglon de la tabla para este caso:** `{"PF_CP_014 Nombres", Selectores.USUARIO_CAMPO_NOMBRES, 35}`
- **Ojo:** este metodo cubre varios casos con la tabla `@DataProvider("camposDeTexto")`; el comando los corre todos y la consola imprime el ID de cada uno.
- **Pasos que ejecuta:** `loQueAcepta` -> `limpiar` -> `salirDelCampo` -> `elBotonGuardarEstaDeshabilitado`
- **Verificaciones:** 1

```java
        usuarios.loQueAcepta(campo, "Ana");
        usuarios.limpiar(campo);
        usuarios.salirDelCampo(campo);
        Assert.assertTrue(usuarios.elBotonGuardarEstaDeshabilitado(),
                caso + ": con el campo vacio el boton GUARDAR REGISTRO debe quedar deshabilitado.");
```

## PF_CP_015 — Validar Campo Apellidos

- **Modulo:** Expediente pantalla usuario
- **Lo que pide la matriz:** Validar que permita -35 caracteres maximo -1 caracter Minimo -No permite caracteres numericos y especiales
- **Prueba:** `UsuariosValidacionesPruebas#elCampoDebePermitir35Caracteres`  (etiquetas: validaciones, usuarios)
- **Lo que valida el codigo:** Maximo de 35 caracteres por campo
- **Correr solo este caso:** `mvn test -Dtest='UsuariosValidacionesPruebas#elCampoDebePermitir35Caracteres'`
- **Renglon de la tabla para este caso:** `{"PF_CP_015 Apellidos", Selectores.USUARIO_CAMPO_APELLIDOS, 35}`
- **Ojo:** este metodo cubre varios casos con la tabla `@DataProvider("camposDeTexto")`; el comando los corre todos y la consola imprime el ID de cada uno.
- **Pasos que ejecuta:** `cuantosCaracteresAcepta`
- **Verificaciones:** 1

```java
        int aceptados = usuarios.cuantosCaracteresAcepta(campo, maximo, "letras");
        Assert.assertEquals(aceptados, maximo,
                caso + ": el campo acepto " + aceptados + " caracteres y el maximo esperado es "
                        + maximo + ".");
```
- **Prueba:** `UsuariosValidacionesPruebas#elCampoNoDebePermitirNumerosNiEspeciales`  (etiquetas: validaciones, usuarios)
- **Lo que valida el codigo:** No se permiten numeros ni caracteres especiales
- **Correr solo este caso:** `mvn test -Dtest='UsuariosValidacionesPruebas#elCampoNoDebePermitirNumerosNiEspeciales'`
- **Renglon de la tabla para este caso:** `{"PF_CP_015 Apellidos", Selectores.USUARIO_CAMPO_APELLIDOS, 35}`
- **Ojo:** este metodo cubre varios casos con la tabla `@DataProvider("camposDeTexto")`; el comando los corre todos y la consola imprime el ID de cada uno.
- **Pasos que ejecuta:** `loQueAcepta`
- **Verificaciones:** 1

```java
        String quedo = usuarios.loQueAcepta(campo, "Juan123!@#");
        Assert.assertEquals(quedo, "Juan",
                caso + ": se escribio \"Juan123!@#\" y el campo dejo \"" + quedo + "\".");
```
- **Prueba:** `UsuariosValidacionesPruebas#elCampoVacioNoDebePermitirGuardar`  (etiquetas: validaciones, usuarios)
- **Lo que valida el codigo:** El campo es obligatorio (minimo un caracter)
- **Correr solo este caso:** `mvn test -Dtest='UsuariosValidacionesPruebas#elCampoVacioNoDebePermitirGuardar'`
- **Renglon de la tabla para este caso:** `{"PF_CP_015 Apellidos", Selectores.USUARIO_CAMPO_APELLIDOS, 35}`
- **Ojo:** este metodo cubre varios casos con la tabla `@DataProvider("camposDeTexto")`; el comando los corre todos y la consola imprime el ID de cada uno.
- **Pasos que ejecuta:** `loQueAcepta` -> `limpiar` -> `salirDelCampo` -> `elBotonGuardarEstaDeshabilitado`
- **Verificaciones:** 1

```java
        usuarios.loQueAcepta(campo, "Ana");
        usuarios.limpiar(campo);
        usuarios.salirDelCampo(campo);
        Assert.assertTrue(usuarios.elBotonGuardarEstaDeshabilitado(),
                caso + ": con el campo vacio el boton GUARDAR REGISTRO debe quedar deshabilitado.");
```

## PF_CP_016 — Validar Campo Cargo

- **Modulo:** Expediente pantalla usuario
- **Lo que pide la matriz:** Validar que permita -35 caracteres maximo -1 caracter Minimo -No permite caracteres numericos y especiales
- **Prueba:** `UsuariosValidacionesPruebas#elCampoDebePermitir35Caracteres`  (etiquetas: validaciones, usuarios)
- **Lo que valida el codigo:** Maximo de 35 caracteres por campo
- **Correr solo este caso:** `mvn test -Dtest='UsuariosValidacionesPruebas#elCampoDebePermitir35Caracteres'`
- **Renglon de la tabla para este caso:** `{"PF_CP_016 Cargo", Selectores.USUARIO_CAMPO_CARGO, 35}`
- **Ojo:** este metodo cubre varios casos con la tabla `@DataProvider("camposDeTexto")`; el comando los corre todos y la consola imprime el ID de cada uno.
- **Pasos que ejecuta:** `cuantosCaracteresAcepta`
- **Verificaciones:** 1

```java
        int aceptados = usuarios.cuantosCaracteresAcepta(campo, maximo, "letras");
        Assert.assertEquals(aceptados, maximo,
                caso + ": el campo acepto " + aceptados + " caracteres y el maximo esperado es "
                        + maximo + ".");
```
- **Prueba:** `UsuariosValidacionesPruebas#elCampoNoDebePermitirNumerosNiEspeciales`  (etiquetas: validaciones, usuarios)
- **Lo que valida el codigo:** No se permiten numeros ni caracteres especiales
- **Correr solo este caso:** `mvn test -Dtest='UsuariosValidacionesPruebas#elCampoNoDebePermitirNumerosNiEspeciales'`
- **Renglon de la tabla para este caso:** `{"PF_CP_016 Cargo", Selectores.USUARIO_CAMPO_CARGO, 35}`
- **Ojo:** este metodo cubre varios casos con la tabla `@DataProvider("camposDeTexto")`; el comando los corre todos y la consola imprime el ID de cada uno.
- **Pasos que ejecuta:** `loQueAcepta`
- **Verificaciones:** 1

```java
        String quedo = usuarios.loQueAcepta(campo, "Juan123!@#");
        Assert.assertEquals(quedo, "Juan",
                caso + ": se escribio \"Juan123!@#\" y el campo dejo \"" + quedo + "\".");
```
- **Prueba:** `UsuariosValidacionesPruebas#elCampoVacioNoDebePermitirGuardar`  (etiquetas: validaciones, usuarios)
- **Lo que valida el codigo:** El campo es obligatorio (minimo un caracter)
- **Correr solo este caso:** `mvn test -Dtest='UsuariosValidacionesPruebas#elCampoVacioNoDebePermitirGuardar'`
- **Renglon de la tabla para este caso:** `{"PF_CP_016 Cargo", Selectores.USUARIO_CAMPO_CARGO, 35}`
- **Ojo:** este metodo cubre varios casos con la tabla `@DataProvider("camposDeTexto")`; el comando los corre todos y la consola imprime el ID de cada uno.
- **Pasos que ejecuta:** `loQueAcepta` -> `limpiar` -> `salirDelCampo` -> `elBotonGuardarEstaDeshabilitado`
- **Verificaciones:** 1

```java
        usuarios.loQueAcepta(campo, "Ana");
        usuarios.limpiar(campo);
        usuarios.salirDelCampo(campo);
        Assert.assertTrue(usuarios.elBotonGuardarEstaDeshabilitado(),
                caso + ": con el campo vacio el boton GUARDAR REGISTRO debe quedar deshabilitado.");
```

## PF_CP_017 — Validar Campo Correo Electrónico

- **Modulo:** Expediente pantalla usuario
- **Lo que pide la matriz:** Validar que permita -Solo perimta el formato de correo Ejem: QA@QA.COM
- **Prueba:** `UsuariosValidacionesPruebas#pfCp017CampoCorreoElectronico`  (etiquetas: validaciones, usuarios)
- **Lo que valida el codigo:** El correo electronico exige formato de correo
- **Correr solo este caso:** `mvn test -Dtest='UsuariosValidacionesPruebas#pfCp017CampoCorreoElectronico'`
- **Pasos que ejecuta:** `loQueAcepta` -> `salirDelCampo` -> `elBotonGuardarEstaDeshabilitado`
- **Verificaciones:** 2

```java
        usuarios.loQueAcepta(Selectores.USUARIO_CAMPO_CORREO, "correo-invalido");
        usuarios.salirDelCampo(Selectores.USUARIO_CAMPO_CORREO);
        Assert.assertTrue(usuarios.elBotonGuardarEstaDeshabilitado(),
                "Con un correo sin formato valido el boton GUARDAR REGISTRO debe quedar "
                        + "deshabilitado.");

        String quedo = usuarios.loQueAcepta(Selectores.USUARIO_CAMPO_CORREO, "qa@qa.com");
        Assert.assertEquals(quedo, "qa@qa.com",
                "El campo no acepto un correo con formato valido: dejo \"" + quedo + "\".");
```

## PF_CP_018 — Validar Campo Teléfono Móvil

- **Modulo:** Expediente pantalla usuario
- **Lo que pide la matriz:** Validar que permita -Solo 10 numeros -Caracteres numericos -No permita caracteres alfabeticos y especiales
- **Prueba:** `UsuariosValidacionesPruebas#pfCp018TelefonoMovil`  (etiquetas: validaciones, usuarios, defecto_conocido)
- **Lo que valida el codigo:** Telefono movil solo 10 caracteres numericos
- **Correr solo este caso:** `mvn test -Dtest='UsuariosValidacionesPruebas#pfCp018TelefonoMovil'`
- **Pasos que ejecuta:** 
- **Verificaciones:** 1

```java
        elTelefonoDebeAceptarSolo10Numeros(
                "PF_CP_018 Telefono movil", Selectores.USUARIO_CAMPO_TELEFONO_MOVIL);
```

## PF_CP_019 — Validar Campo Teléfono Fijo

- **Modulo:** Expediente pantalla usuario
- **Lo que pide la matriz:** Validar que permita -Solo 10 numeros -Caracteres numericos -No permita caracteres alfabeticos y especiales
- **Prueba:** `UsuariosValidacionesPruebas#pfCp019TelefonoFijo`  (etiquetas: validaciones, usuarios)
- **Lo que valida el codigo:** Telefono fijo solo 10 caracteres numericos
- **Correr solo este caso:** `mvn test -Dtest='UsuariosValidacionesPruebas#pfCp019TelefonoFijo'`
- **Pasos que ejecuta:** 
- **Verificaciones:** 1

```java
        elTelefonoDebeAceptarSolo10Numeros(
                "PF_CP_019 Telefono fijo", Selectores.USUARIO_CAMPO_TELEFONO_FIJO);
```

## PF_CP_020 — Validación de la funcionalidad del botón Guardar registro

- **Modulo:** Expediente pantalla usuario
- **Lo que pide la matriz:** Muestra un popup indicando:Usuario creado exitosamente Usuario: *** Se ha enviado un correo a ** para que genere su nueva contraseña y pueda ingresar al sistema. botón aceptar. En la pantalla de usuarios se debe ver reflejado en la tabla, el usuario creado
- **Prueba:** `UsuariosAltasPruebas#pfCp020GuardarRegistroDeUsuario`  (etiquetas: ola5, usuarios, escribe_datos)
- **Lo que valida el codigo:** Guardar registro da de alta al usuario y la tabla lo muestra activo
- **Correr solo este caso:** `mvn test -Dtest='UsuariosAltasPruebas#pfCp020GuardarRegistroDeUsuario'`
- **Pasos que ejecuta:** `abrirElAltaDeUsuario` -> `llenarElAltaDeUsuario` -> `elBotonGuardarEstaDeshabilitado` -> `guardarElRegistro` -> `abrir` -> `buscarPorNombre` -> `nombres` -> `laTablaDebeMostrarAlUsuario` -> `numeroDeEmpleado` -> `desactivarSiQuedoActivo`
- **Verificaciones:** 2

```java
        UsuarioDePrueba usuario = UsuarioDePrueba.nuevo(sufijoDeLaEjecucion());
        try {
            usuarios.abrirElAltaDeUsuario()
                    .llenarElAltaDeUsuario(usuario);
            Assert.assertFalse(usuarios.elBotonGuardarEstaDeshabilitado(),
                    "Con todos los campos llenos, GUARDAR REGISTRO sigue deshabilitado.");

            usuarios.guardarElRegistro();
            usuarios.abrir()
                    .buscarPorNombre(usuario.nombres())
                    .laTablaDebeMostrarAlUsuario(usuario.numeroDeEmpleado(), "Activo");
        } finally {
            usuarios.desactivarSiQuedoActivo(usuario.nombres(), usuario.numeroDeEmpleado());
        }
```

## PF_CP_021 — Validación de la funcionalidad del botón Cancelar

- **Modulo:** Expediente pantalla usuario
- **Lo que pide la matriz:** Muestra la pantalla de Usuario sin el usuario que se intentó agregar en la tabla de información
- **Prueba:** `UsuariosValidacionesPruebas#pfCp021BotonCancelar`  (etiquetas: validaciones, usuarios)
- **Lo que valida el codigo:** Cancelar regresa a la pantalla de Usuarios sin guardar
- **Correr solo este caso:** `mvn test -Dtest='UsuariosValidacionesPruebas#pfCp021BotonCancelar'`
- **Pasos que ejecuta:** `loQueAcepta` -> `cancelar` -> `elBotonDebeEstarVisible` -> `laPantallaDebeTenerUnaTablaConInformacion`
- **Verificaciones:** 2

```java
        usuarios.loQueAcepta(Selectores.USUARIO_CAMPO_NOMBRES, "Prueba");
        usuarios.cancelar();
        inicio.elBotonDebeEstarVisible("AGREGAR USUARIO")
                .laPantallaDebeTenerUnaTablaConInformacion();
```

## PF_CP_022 — Validar descargar Layout

- **Modulo:** Expediente pantalla usuario
- **Lo que pide la matriz:** Validar que el formato este correcto -Nombre -Apellido -Email -Número de empleado -Cargo -Código de país -Teléfono móvil -Teléfono Fijo -Rol -Campaña(s)
- **Prueba:** `DescargasPruebas#descargarElLayoutDeCargaMasiva`  (etiquetas: descargas, defecto_conocido)
- **Lo que valida el codigo:** El layout de carga masiva trae las columnas de la matriz
- **Correr solo este caso:** `mvn test -Dtest='DescargasPruebas#descargarElLayoutDeCargaMasiva'`
- **Pasos que ejecuta:** `irAlMenu` -> `abrir` -> `abrirLaCargaMasiva` -> `laCargaMasivaDebePermitirElegirArchivo` -> `descargarElLayout` -> `salirDeLaCargaMasiva`
- **Verificaciones:** 3

```java
        entrar().irAlMenu("Usuarios");
        PaginaUsuarios usuarios = new PaginaUsuarios().abrir()
                .abrirLaCargaMasiva()
                .laCargaMasivaDebePermitirElegirArchivo()
                .descargarElLayout();

        Path layout = Descargas.esperarArchivo(".xlsx");
        Assert.assertEquals(Descargas.hojasDelExcel(layout),
                List.of(Configuracion.lista("amex.excel.layout.usuarios.hojas")),
                "El layout no trae las hojas esperadas.");
        debeTenerLasColumnas(layout, "Layout", "amex.excel.layout.usuarios");
        usuarios.salirDeLaCargaMasiva();
```

## PF_CP_023 — Validación de la funcionalidad del botón Carga masiva de usuarios

- **Modulo:** Expediente pantalla usuario
- **Lo que pide la matriz:** Muestra la pantalla de datos cargados con éxito, muestra la información de registro totales,registros aceptados, registros con error y el botón aceptar
- **Automatizado:** no. Carga masiva: falta el layout oficial valido.

## PF_CP_024

- **Modulo:** Expediente pantalla usuario
- **Lo que pide la matriz:** Muestra la pantalla de layout incorrecto
- **Automatizado:** no. Carga masiva: falta el layout oficial invalido.

## PF_CP_025

- **Modulo:** Expediente pantalla usuario
- **Lo que pide la matriz:** Muestra la pantalla de Usuario
- **Automatizado:** no. Carga masiva: falta el layout oficial invalido.

## PF_CP_026

- **Modulo:** Expediente pantalla usuario
- **Lo que pide la matriz:** Realiza la actualización de información de la tabla de usuarios
- **Prueba:** `UsuariosConsultasPruebas#pfCp026ActualizarLaTabla`  (etiquetas: consultas, usuarios)
- **Lo que valida el codigo:** El boton Actualizar tabla recarga la tabla de usuarios
- **Correr solo este caso:** `mvn test -Dtest='UsuariosConsultasPruebas#pfCp026ActualizarLaTabla'`
- **Pasos que ejecuta:** `cuantosUsuariosMuestraLaTabla` -> `actualizarLaTabla` -> `esperarQueLaTablaTenga`
- **Verificaciones:** 0

```java
        int antes = usuarios.cuantosUsuariosMuestraLaTabla();
        usuarios.actualizarLaTabla().esperarQueLaTablaTenga(antes);
```

## PF_CP_027

- **Modulo:** Expediente pantalla usuario
- **Lo que pide la matriz:** Muestra el archivo de excel con toda la información de la tabla de usuarios con: -Número de empleado -Nombre -Apellidos -Correo electrónico -Rol -Estatus -Campañas
- **Prueba:** `DescargasPruebas#exportarUsuariosAExcel`  (etiquetas: descargas)
- **Lo que valida el codigo:** Exportar a excel descarga la tabla de usuarios
- **Correr solo este caso:** `mvn test -Dtest='DescargasPruebas#exportarUsuariosAExcel'`
- **Pasos que ejecuta:** `irAlMenu` -> `abrir` -> `exportarAExcel`
- **Verificaciones:** 1

```java
        entrar().irAlMenu("Usuarios");
        new PaginaUsuarios().abrir().exportarAExcel();

        Path excel = Descargas.esperarArchivo(".xlsx");
        debeTenerLasColumnas(excel, null, "amex.excel.usuarios");
```

## PF_CP_028

- **Modulo:** Expediente pantalla usuario
- **Lo que pide la matriz:** Realiza el filtro que se aplicó en los campos
- **Prueba:** `UsuariosConsultasPruebas#pfCp028FiltrarPorNombre`  (etiquetas: consultas, usuarios)
- **Lo que valida el codigo:** El filtro de usuarios busca por nombre
- **Correr solo este caso:** `mvn test -Dtest='UsuariosConsultasPruebas#pfCp028FiltrarPorNombre'`
- **Pasos que ejecuta:** `valorDeLaPrimeraFila` -> `abrirElFiltro` -> `filtrarPorNombre` -> `nombresDeLaTablaCuandoTodosContengan` -> `allMatch` -> `toUpperCase`
- **Verificaciones:** 2

```java
        // El dato del filtro se toma de la propia tabla: asi la prueba no depende
        // de que el ambiente tenga un usuario especifico.
        String nombre = usuarios.valorDeLaPrimeraFila(PaginaUsuarios.COLUMNA_NOMBRE);

        List<String> nombres = usuarios.abrirElFiltro()
                .filtrarPorNombre(nombre)
                .nombresDeLaTablaCuandoTodosContengan(nombre);

        Assert.assertFalse(nombres.isEmpty(),
                "El filtro por nombre \"" + nombre + "\" no devolvio ningun usuario.");
        Assert.assertTrue(nombres.stream()
                        .allMatch(actual -> actual.toUpperCase().contains(nombre.toUpperCase())),
                "La tabla filtrada por \"" + nombre + "\" muestra otros usuarios: " + nombres + ".");
```

## PF_CP_029

- **Modulo:** Expediente pantalla usuario
- **Lo que pide la matriz:** Realiza el limpiado de los filtros que se aplicarón en los campos
- **Prueba:** `UsuariosConsultasPruebas#pfCp029LimpiarLosFiltros`  (etiquetas: consultas, usuarios)
- **Lo que valida el codigo:** El boton Limpiar borra los filtros de usuarios
- **Correr solo este caso:** `mvn test -Dtest='UsuariosConsultasPruebas#pfCp029LimpiarLosFiltros'`
- **Pasos que ejecuta:** `cuantosUsuariosMuestraLaTabla` -> `valorDeLaPrimeraFila` -> `abrirElFiltro` -> `filtrarPorNombre` -> `limpiarElFiltro` -> `valorDelFiltroDeNombre` -> `esperarQueLaTablaTenga`
- **Verificaciones:** 1

```java
        int todos = usuarios.cuantosUsuariosMuestraLaTabla();
        String nombre = usuarios.valorDeLaPrimeraFila(PaginaUsuarios.COLUMNA_NOMBRE);

        usuarios.abrirElFiltro().filtrarPorNombre(nombre).limpiarElFiltro();

        Assert.assertEquals(usuarios.valorDelFiltroDeNombre(), "",
                "El campo Nombre del filtro no quedo vacio.");
        usuarios.esperarQueLaTablaTenga(todos);
```

## PF_CP_030

- **Modulo:** Expediente pantalla usuario
- **Lo que pide la matriz:** Muestra el detalle del usuario: Activo: Ultimo Acceso: Fecha de Creación: Area Tipo de usuario Número de empleado Nombre(s) Apellidos Cargo Correo electrónico Código de país Teléfono móvil (10 dígitos) Teléfono fijo Muestra los botones de editar datos, generar contraseña, cancelar. En caso de no contar con alguno de estos datos se omitira el dato y no sera visible
- **Prueba:** `UsuariosConsultasPruebas#pfCp030VerDetalleDelUsuario`  (etiquetas: consultas, usuarios)
- **Lo que valida el codigo:** Ver detalle muestra los datos del usuario
- **Correr solo este caso:** `mvn test -Dtest='UsuariosConsultasPruebas#pfCp030VerDetalleDelUsuario'`
- **Pasos que ejecuta:** `abrirElDetalleDeUnUsuarioConNumeroDeEmpleado` -> `elDetalleDebeMostrar` -> `cerrarElDetalle`
- **Verificaciones:** 1

```java
        usuarios.abrirElDetalleDeUnUsuarioConNumeroDeEmpleado()
                .elDetalleDebeMostrar("Activo:", "Ultimo Acceso:", "Fecha de Creación:", "Area",
                        "Tipo de usuario", "Número de empleado", "Nombre(s)", "Apellidos",
                        "Cargo", "Correo electrónico", "Código de país",
                        "Teléfono móvil (10 dígitos)", "Teléfono fijo")
                .cerrarElDetalle();
```

## PF_CP_031

- **Modulo:** Expediente pantalla usuario
- **Lo que pide la matriz:** Validar que se muestre solo la opción -Ventas
- **Automatizado:** no. Repite PF_CP_012 (duplicado en la matriz).

## PF_CP_032

- **Modulo:** Expediente pantalla usuario
- **Lo que pide la matriz:** Validar que se muestre solo la opción -Administrador Apex -Supervisor AXP -Usuario AXP -Supervisor Agencia -Usuario Agencia
- **Automatizado:** no. Repite PF_CP_013 (duplicado en la matriz).

## PF_CP_033

- **Modulo:** Expediente pantalla usuario
- **Lo que pide la matriz:** Validar que permita -35 caracteres maximo -1 caracter Minimo -No permite caracteres numericos y especiales
- **Automatizado:** no. Repite PF_CP_014 (duplicado en la matriz).

## PF_CP_034

- **Modulo:** Expediente pantalla usuario
- **Lo que pide la matriz:** Validar que permita -35 caracteres maximo -1 caracter Minimo -No permite caracteres numericos y especiales
- **Automatizado:** no. Repite PF_CP_015 (duplicado en la matriz).

## PF_CP_035

- **Modulo:** Expediente pantalla usuario
- **Lo que pide la matriz:** Validar que permita -35 caracteres maximo -1 caracter Minimo -No permite caracteres numericos y especiales
- **Automatizado:** no. Repite PF_CP_016 (duplicado en la matriz).

## PF_CP_036

- **Modulo:** Expediente pantalla usuario
- **Lo que pide la matriz:** Validar que permita -Solo perimta el formato de correo Ejem: QA@QA.COM
- **Automatizado:** no. Repite PF_CP_017 (duplicado en la matriz).

## PF_CP_037

- **Modulo:** Expediente pantalla usuario
- **Lo que pide la matriz:** Validar que permita -Solo 10 numeros -Caracteres numericos -No permita caracteres alfabeticos y especiales
- **Automatizado:** no. Repite PF_CP_018 (duplicado en la matriz).

## PF_CP_038

- **Modulo:** Expediente pantalla usuario
- **Lo que pide la matriz:** Validar que permita -Solo 10 numeros -Caracteres numericos -No permita caracteres alfabeticos y especiales
- **Automatizado:** no. Repite PF_CP_019 (duplicado en la matriz).

## PF_CP_039 — Validar la modificación de datos en detalles de usuario

- **Modulo:** Expediente pantalla usuario
- **Lo que pide la matriz:** Muestra un popup indicando: Usuario actualizado Se han actualizado los datos del usuario seleccionado con el botón "X" Se valida la información de la tabla actualizada
- **Prueba:** `UsuariosAltasPruebas#pfCp039DetalleDelUsuario`  (etiquetas: ola5, usuarios, escribe_datos)
- **Lo que valida el codigo:** Detalle del usuario: editar datos, generar contraseña y cancelar
- **Correr solo este caso:** `mvn test -Dtest='UsuariosAltasPruebas#pfCp039DetalleDelUsuario'`
- **Pasos que ejecuta:** `abrirElDetalleDelUsuario` -> `numeroDeEmpleado` -> `editarElCargoDelDetalle` -> `cargoEditado` -> `ultimoMensaje` -> `toUpperCase` -> `abrir` -> `buscarPorNombre` -> `nombres` -> `elCargoDelDetalleDebeSer` -> `generarLaContrasena` -> `elDetalleEstaAbierto` -> `cancelarElDetalle` -> `laDireccionDebeContener` -> `desactivarSiQuedoActivo`
- **Verificaciones:** 4

```java
        UsuarioDePrueba usuario = crearElUsuarioDePrueba();
        try {
            // PF_CP_039: se edita el cargo, se guarda y el detalle lo muestra guardado.
            usuarios.abrirElDetalleDelUsuario(usuario.numeroDeEmpleado())
                    .editarElCargoDelDetalle(usuario.cargoEditado());
            Assert.assertTrue(usuarios.ultimoMensaje().toUpperCase().contains("ACTUALIZ"),
                    "Al guardar no se aviso que se actualizo el usuario. La aplicacion mostro: "
                            + usuarios.ultimoMensaje() + ".");
            usuarios.abrir()
                    .buscarPorNombre(usuario.nombres())
                    .abrirElDetalleDelUsuario(usuario.numeroDeEmpleado())
                    .elCargoDelDetalleDebeSer(usuario.cargoEditado());

            // PF_CP_040: generar contrasena avisa que se envio al correo del usuario.
            usuarios.generarLaContrasena();
            Assert.assertFalse(usuarios.ultimoMensaje().isBlank(),
                    "GENERAR CONTRASEÑA no mostro ningun mensaje sobre el detalle del usuario.");

            // PF_CP_041: Cancelar cierra el detalle y regresa a la pantalla de Usuarios.
            if (!usuarios.elDetalleEstaAbierto()) {
                usuarios.abrir()
                        .buscarPorNombre(usuario.nombres())
                        .abrirElDetalleDelUsuario(usuario.numeroDeEmpleado());
            }
            usuarios.cancelarElDetalle();
            inicio.laDireccionDebeContener("expedient/users");
            usuarios.abrir();
        } finally {
            usuarios.desactivarSiQuedoActivo(usuario.nombres(), usuario.numeroDeEmpleado());
        }
```

## PF_CP_040 — Validación del botón generar contraseña sobre el detalle de información de usuario

- **Modulo:** Expediente pantalla usuario
- **Lo que pide la matriz:** Muestra sobre la misma pantalla de detalle lo siguiente: Tu nueva contraseña es: Copiar contraseña Recuerda guardar tu contraseña en un lugar seguro.
- **Prueba:** `UsuariosAltasPruebas#pfCp039DetalleDelUsuario`  (etiquetas: ola5, usuarios, escribe_datos)
- **Lo que valida el codigo:** Detalle del usuario: editar datos, generar contraseña y cancelar
- **Correr solo este caso:** `mvn test -Dtest='UsuariosAltasPruebas#pfCp039DetalleDelUsuario'`
- **Pasos que ejecuta:** `abrirElDetalleDelUsuario` -> `numeroDeEmpleado` -> `editarElCargoDelDetalle` -> `cargoEditado` -> `ultimoMensaje` -> `toUpperCase` -> `abrir` -> `buscarPorNombre` -> `nombres` -> `elCargoDelDetalleDebeSer` -> `generarLaContrasena` -> `elDetalleEstaAbierto` -> `cancelarElDetalle` -> `laDireccionDebeContener` -> `desactivarSiQuedoActivo`
- **Verificaciones:** 4

```java
        UsuarioDePrueba usuario = crearElUsuarioDePrueba();
        try {
            // PF_CP_039: se edita el cargo, se guarda y el detalle lo muestra guardado.
            usuarios.abrirElDetalleDelUsuario(usuario.numeroDeEmpleado())
                    .editarElCargoDelDetalle(usuario.cargoEditado());
            Assert.assertTrue(usuarios.ultimoMensaje().toUpperCase().contains("ACTUALIZ"),
                    "Al guardar no se aviso que se actualizo el usuario. La aplicacion mostro: "
                            + usuarios.ultimoMensaje() + ".");
            usuarios.abrir()
                    .buscarPorNombre(usuario.nombres())
                    .abrirElDetalleDelUsuario(usuario.numeroDeEmpleado())
                    .elCargoDelDetalleDebeSer(usuario.cargoEditado());

            // PF_CP_040: generar contrasena avisa que se envio al correo del usuario.
            usuarios.generarLaContrasena();
            Assert.assertFalse(usuarios.ultimoMensaje().isBlank(),
                    "GENERAR CONTRASEÑA no mostro ningun mensaje sobre el detalle del usuario.");

            // PF_CP_041: Cancelar cierra el detalle y regresa a la pantalla de Usuarios.
            if (!usuarios.elDetalleEstaAbierto()) {
                usuarios.abrir()
                        .buscarPorNombre(usuario.nombres())
                        .abrirElDetalleDelUsuario(usuario.numeroDeEmpleado());
            }
            usuarios.cancelarElDetalle();
            inicio.laDireccionDebeContener("expedient/users");
            usuarios.abrir();
        } finally {
            usuarios.desactivarSiQuedoActivo(usuario.nombres(), usuario.numeroDeEmpleado());
        }
```

## PF_CP_041 — Validación del boton de cancelar

- **Modulo:** Expediente pantalla usuario
- **Lo que pide la matriz:** Muestra la pantalla de Usuario
- **Prueba:** `UsuariosAltasPruebas#pfCp039DetalleDelUsuario`  (etiquetas: ola5, usuarios, escribe_datos)
- **Lo que valida el codigo:** Detalle del usuario: editar datos, generar contraseña y cancelar
- **Correr solo este caso:** `mvn test -Dtest='UsuariosAltasPruebas#pfCp039DetalleDelUsuario'`
- **Pasos que ejecuta:** `abrirElDetalleDelUsuario` -> `numeroDeEmpleado` -> `editarElCargoDelDetalle` -> `cargoEditado` -> `ultimoMensaje` -> `toUpperCase` -> `abrir` -> `buscarPorNombre` -> `nombres` -> `elCargoDelDetalleDebeSer` -> `generarLaContrasena` -> `elDetalleEstaAbierto` -> `cancelarElDetalle` -> `laDireccionDebeContener` -> `desactivarSiQuedoActivo`
- **Verificaciones:** 4

```java
        UsuarioDePrueba usuario = crearElUsuarioDePrueba();
        try {
            // PF_CP_039: se edita el cargo, se guarda y el detalle lo muestra guardado.
            usuarios.abrirElDetalleDelUsuario(usuario.numeroDeEmpleado())
                    .editarElCargoDelDetalle(usuario.cargoEditado());
            Assert.assertTrue(usuarios.ultimoMensaje().toUpperCase().contains("ACTUALIZ"),
                    "Al guardar no se aviso que se actualizo el usuario. La aplicacion mostro: "
                            + usuarios.ultimoMensaje() + ".");
            usuarios.abrir()
                    .buscarPorNombre(usuario.nombres())
                    .abrirElDetalleDelUsuario(usuario.numeroDeEmpleado())
                    .elCargoDelDetalleDebeSer(usuario.cargoEditado());

            // PF_CP_040: generar contrasena avisa que se envio al correo del usuario.
            usuarios.generarLaContrasena();
            Assert.assertFalse(usuarios.ultimoMensaje().isBlank(),
                    "GENERAR CONTRASEÑA no mostro ningun mensaje sobre el detalle del usuario.");

            // PF_CP_041: Cancelar cierra el detalle y regresa a la pantalla de Usuarios.
            if (!usuarios.elDetalleEstaAbierto()) {
                usuarios.abrir()
                        .buscarPorNombre(usuario.nombres())
                        .abrirElDetalleDelUsuario(usuario.numeroDeEmpleado());
            }
            usuarios.cancelarElDetalle();
            inicio.laDireccionDebeContener("expedient/users");
            usuarios.abrir();
        } finally {
            usuarios.desactivarSiQuedoActivo(usuario.nombres(), usuario.numeroDeEmpleado());
        }
```

## PF_CP_042 — Validar botón Desactivar

- **Modulo:** Expediente pantalla usuario
- **Lo que pide la matriz:** Se mustra modal con lo siguirente: Se va a desactivar el usuario: Nombre usuario ¿Seguro que desea continuar? Botón Aceptar Botón Cancelar
- **Prueba:** `UsuariosAltasPruebas#pfCp042DesactivarYActivarUsuario`  (etiquetas: ola5, usuarios, escribe_datos)
- **Lo que valida el codigo:** Desactivar un usuario (aceptar y cancelar el modal) y volver a activarlo
- **Correr solo este caso:** `mvn test -Dtest='UsuariosAltasPruebas#pfCp042DesactivarYActivarUsuario'`
- **Pasos que ejecuta:** `desactivarAlUsuario` -> `numeroDeEmpleado` -> `textoDelPopup` -> `toUpperCase` -> `nombres` -> `cancelarElPopup` -> `laTablaDebeMostrarAlUsuario` -> `aceptarElPopup` -> `activarAlUsuario` -> `desactivarSiQuedoActivo`
- **Verificaciones:** 4

```java
        UsuarioDePrueba usuario = crearElUsuarioDePrueba();
        try {
            // PF_CP_042 y PF_CP_044: el modal avisa a quien se va a desactivar y Cancelar
            // no cambia el estatus.
            usuarios.desactivarAlUsuario(usuario.numeroDeEmpleado());
            String aviso = usuarios.textoDelPopup();
            Assert.assertTrue(aviso.toUpperCase().contains(usuario.nombres().toUpperCase()),
                    "El modal de desactivar no dice a que usuario se va a desactivar. Muestra: "
                            + aviso + ".");
            usuarios.cancelarElPopup()
                    .laTablaDebeMostrarAlUsuario(usuario.numeroDeEmpleado(), "Activo");

            // PF_CP_043: al aceptar, el estatus cambia a inactivo.
            usuarios.desactivarAlUsuario(usuario.numeroDeEmpleado())
                    .aceptarElPopup()
                    .laTablaDebeMostrarAlUsuario(usuario.numeroDeEmpleado(), "Inactivo");

            // PF_CP_045: Activar usuario desde el detalle lo regresa a activo.
            usuarios.activarAlUsuario(usuario.numeroDeEmpleado())
                    .laTablaDebeMostrarAlUsuario(usuario.numeroDeEmpleado(), "Activo");
        } finally {
            usuarios.desactivarSiQuedoActivo(usuario.nombres(), usuario.numeroDeEmpleado());
        }
```

## PF_CP_043 — Tap botón Aceptar

- **Modulo:** Expediente pantalla usuario
- **Lo que pide la matriz:** Validar que el estatus cambie a inactivo
- **Prueba:** `UsuariosAltasPruebas#pfCp042DesactivarYActivarUsuario`  (etiquetas: ola5, usuarios, escribe_datos)
- **Lo que valida el codigo:** Desactivar un usuario (aceptar y cancelar el modal) y volver a activarlo
- **Correr solo este caso:** `mvn test -Dtest='UsuariosAltasPruebas#pfCp042DesactivarYActivarUsuario'`
- **Pasos que ejecuta:** `desactivarAlUsuario` -> `numeroDeEmpleado` -> `textoDelPopup` -> `toUpperCase` -> `nombres` -> `cancelarElPopup` -> `laTablaDebeMostrarAlUsuario` -> `aceptarElPopup` -> `activarAlUsuario` -> `desactivarSiQuedoActivo`
- **Verificaciones:** 4

```java
        UsuarioDePrueba usuario = crearElUsuarioDePrueba();
        try {
            // PF_CP_042 y PF_CP_044: el modal avisa a quien se va a desactivar y Cancelar
            // no cambia el estatus.
            usuarios.desactivarAlUsuario(usuario.numeroDeEmpleado());
            String aviso = usuarios.textoDelPopup();
            Assert.assertTrue(aviso.toUpperCase().contains(usuario.nombres().toUpperCase()),
                    "El modal de desactivar no dice a que usuario se va a desactivar. Muestra: "
                            + aviso + ".");
            usuarios.cancelarElPopup()
                    .laTablaDebeMostrarAlUsuario(usuario.numeroDeEmpleado(), "Activo");

            // PF_CP_043: al aceptar, el estatus cambia a inactivo.
            usuarios.desactivarAlUsuario(usuario.numeroDeEmpleado())
                    .aceptarElPopup()
                    .laTablaDebeMostrarAlUsuario(usuario.numeroDeEmpleado(), "Inactivo");

            // PF_CP_045: Activar usuario desde el detalle lo regresa a activo.
            usuarios.activarAlUsuario(usuario.numeroDeEmpleado())
                    .laTablaDebeMostrarAlUsuario(usuario.numeroDeEmpleado(), "Activo");
        } finally {
            usuarios.desactivarSiQuedoActivo(usuario.nombres(), usuario.numeroDeEmpleado());
        }
```

## PF_CP_044 — Tap botón Cancelar

- **Modulo:** Expediente pantalla usuario
- **Lo que pide la matriz:** Validar que cierre el Modal y te mantenga en la pantalla usuarios
- **Prueba:** `UsuariosAltasPruebas#pfCp042DesactivarYActivarUsuario`  (etiquetas: ola5, usuarios, escribe_datos)
- **Lo que valida el codigo:** Desactivar un usuario (aceptar y cancelar el modal) y volver a activarlo
- **Correr solo este caso:** `mvn test -Dtest='UsuariosAltasPruebas#pfCp042DesactivarYActivarUsuario'`
- **Pasos que ejecuta:** `desactivarAlUsuario` -> `numeroDeEmpleado` -> `textoDelPopup` -> `toUpperCase` -> `nombres` -> `cancelarElPopup` -> `laTablaDebeMostrarAlUsuario` -> `aceptarElPopup` -> `activarAlUsuario` -> `desactivarSiQuedoActivo`
- **Verificaciones:** 4

```java
        UsuarioDePrueba usuario = crearElUsuarioDePrueba();
        try {
            // PF_CP_042 y PF_CP_044: el modal avisa a quien se va a desactivar y Cancelar
            // no cambia el estatus.
            usuarios.desactivarAlUsuario(usuario.numeroDeEmpleado());
            String aviso = usuarios.textoDelPopup();
            Assert.assertTrue(aviso.toUpperCase().contains(usuario.nombres().toUpperCase()),
                    "El modal de desactivar no dice a que usuario se va a desactivar. Muestra: "
                            + aviso + ".");
            usuarios.cancelarElPopup()
                    .laTablaDebeMostrarAlUsuario(usuario.numeroDeEmpleado(), "Activo");

            // PF_CP_043: al aceptar, el estatus cambia a inactivo.
            usuarios.desactivarAlUsuario(usuario.numeroDeEmpleado())
                    .aceptarElPopup()
                    .laTablaDebeMostrarAlUsuario(usuario.numeroDeEmpleado(), "Inactivo");

            // PF_CP_045: Activar usuario desde el detalle lo regresa a activo.
            usuarios.activarAlUsuario(usuario.numeroDeEmpleado())
                    .laTablaDebeMostrarAlUsuario(usuario.numeroDeEmpleado(), "Activo");
        } finally {
            usuarios.desactivarSiQuedoActivo(usuario.nombres(), usuario.numeroDeEmpleado());
        }
```

## PF_CP_045 — Validar botón Activar usuario

- **Modulo:** Expediente pantalla usuario
- **Lo que pide la matriz:** Validar que el estatus cambie a activo
- **Prueba:** `UsuariosAltasPruebas#pfCp042DesactivarYActivarUsuario`  (etiquetas: ola5, usuarios, escribe_datos)
- **Lo que valida el codigo:** Desactivar un usuario (aceptar y cancelar el modal) y volver a activarlo
- **Correr solo este caso:** `mvn test -Dtest='UsuariosAltasPruebas#pfCp042DesactivarYActivarUsuario'`
- **Pasos que ejecuta:** `desactivarAlUsuario` -> `numeroDeEmpleado` -> `textoDelPopup` -> `toUpperCase` -> `nombres` -> `cancelarElPopup` -> `laTablaDebeMostrarAlUsuario` -> `aceptarElPopup` -> `activarAlUsuario` -> `desactivarSiQuedoActivo`
- **Verificaciones:** 4

```java
        UsuarioDePrueba usuario = crearElUsuarioDePrueba();
        try {
            // PF_CP_042 y PF_CP_044: el modal avisa a quien se va a desactivar y Cancelar
            // no cambia el estatus.
            usuarios.desactivarAlUsuario(usuario.numeroDeEmpleado());
            String aviso = usuarios.textoDelPopup();
            Assert.assertTrue(aviso.toUpperCase().contains(usuario.nombres().toUpperCase()),
                    "El modal de desactivar no dice a que usuario se va a desactivar. Muestra: "
                            + aviso + ".");
            usuarios.cancelarElPopup()
                    .laTablaDebeMostrarAlUsuario(usuario.numeroDeEmpleado(), "Activo");

            // PF_CP_043: al aceptar, el estatus cambia a inactivo.
            usuarios.desactivarAlUsuario(usuario.numeroDeEmpleado())
                    .aceptarElPopup()
                    .laTablaDebeMostrarAlUsuario(usuario.numeroDeEmpleado(), "Inactivo");

            // PF_CP_045: Activar usuario desde el detalle lo regresa a activo.
            usuarios.activarAlUsuario(usuario.numeroDeEmpleado())
                    .laTablaDebeMostrarAlUsuario(usuario.numeroDeEmpleado(), "Activo");
        } finally {
            usuarios.desactivarSiQuedoActivo(usuario.nombres(), usuario.numeroDeEmpleado());
        }
```

## PF_CP_046 — Validación de la pantalla del menú Catálogos

- **Modulo:** Expediente pantalla catalogos
- **Lo que pide la matriz:** Muestra el título de catálogos con una lista desplegable en el cual contiene: -Nacionalidades -profesiones -campaña -código de país -productos -días festivos -versiones
- **Prueba:** `CatalogosPruebas#pfCp046LaListaMuestraLosCatalogosEsperados`  (etiquetas: catalogos, humo)
- **Lo que valida el codigo:** La lista muestra todos los catalogos esperados
- **Correr solo este caso:** `mvn test -Dtest='CatalogosPruebas#pfCp046LaListaMuestraLosCatalogosEsperados'`
- **Pasos que ejecuta:** `laListaDebeContener`
- **Verificaciones:** 1

```java
        new PaginaCatalogos().laListaDebeContener(PaginaCatalogos.catalogosEsperados());
```
- **Prueba:** `NavegacionPruebas#pfCp046MenuCatalogos`  (etiquetas: navegacion)
- **Lo que valida el codigo:** Menu de Catalogos
- **Correr solo este caso:** `mvn test -Dtest='NavegacionPruebas#pfCp046MenuCatalogos'`
- **Pasos que ejecuta:** `irAlMenu` -> `laDireccionDebeContener`
- **Verificaciones:** 1

```java
        inicio.irAlMenu("Catalogos").laDireccionDebeContener("expedient/catalogs");
```

## PF_CP_047 — Validación de la pantalla del menú Catálogos - Nacionalidades

- **Modulo:** Expediente pantalla Catálogos - Nacionalidades
- **Lo que pide la matriz:** Muestra el botón de Agregar elemento, y la tabla de nacionalidades con su: -Descripción, -estatus, -detalle -inactivar
- **Prueba:** `CatalogosConsultasPruebas#laPantallaDelCatalogoMuestraSuTabla`  (etiquetas: consultas)
- **Lo que valida el codigo:** Pantalla del catalogo con su boton Agregar elemento y su tabla
- **Correr solo este caso:** `mvn test -Dtest='CatalogosConsultasPruebas#laPantallaDelCatalogoMuestraSuTabla'`
- **Renglon de la tabla para este caso:** `{"PF_CP_047", "Nacionalidades", new String[] {"Descripción", "Estatus"}}`
- **Ojo:** este metodo cubre varios casos con la tabla `@DataProvider("pantallas")`; el comando los corre todos y la consola imprime el ID de cada uno.
- **Pasos que ejecuta:** `abrirCatalogo` -> `elBotonAgregarElementoDebeEstarVisible` -> `encabezadosDeLaTabla`
- **Verificaciones:** 2

```java
        catalogos.abrirCatalogo(catalogo).elBotonAgregarElementoDebeEstarVisible();

        List<String> actuales = catalogos.encabezadosDeLaTabla();
        for (String columna : columnas) {
            Assert.assertTrue(actuales.stream()
                            .anyMatch(actual -> actual.equalsIgnoreCase(columna)),
                    caso + ": la tabla de " + catalogo + " no muestra la columna \"" + columna
                            + "\". Muestra hoy: " + actuales + ".");
        }
```
- **Prueba:** `CatalogosPruebas#cadaCatalogoMuestraSuTabla`  (etiquetas: catalogos)
- **Lo que valida el codigo:** Cada catalogo muestra su tabla y su boton Agregar elemento
- **Correr solo este caso:** `mvn test -Dtest='CatalogosPruebas#cadaCatalogoMuestraSuTabla'`
- **Ojo:** este metodo cubre varios casos con la tabla `@DataProvider("catalogos")`; el comando los corre todos y la consola imprime el ID de cada uno.
- **Pasos que ejecuta:** `abrirCatalogo` -> `elBotonAgregarElementoDebeEstarVisible` -> `laPantallaDebeTenerUnaTablaConInformacion`
- **Verificaciones:** 2

```java
        new PaginaCatalogos()
                .abrirCatalogo(catalogo)
                .elBotonAgregarElementoDebeEstarVisible();
        inicio.laPantallaDebeTenerUnaTablaConInformacion();
```

## PF_CP_048 — Validación del botón agregar elemento del menú de catálogos - nacionalidades

- **Modulo:** Expediente pantalla Catálogos - Nacionalidades
- **Lo que pide la matriz:** Muestra la nacionalidad agregada en la tabla de nacionalidades
- **Prueba:** `CatalogosAltasPruebas#nacionalidades`  (etiquetas: ola5, catalogos, escribe_datos)
- **Lo que valida el codigo:** Nacionalidades: agregar, editar, inactivar y activar un elemento
- **Correr solo este caso:** `mvn test -Dtest='CatalogosAltasPruebas#nacionalidades'`
- **Pasos que ejecuta:** 
- **Verificaciones:** 0

```java
        cicloDelCatalogo("Nacionalidades");
```

## PF_CP_049 — Validar Numero de caracteres

- **Modulo:** Expediente pantalla Catálogos - Nacionalidades
- **Lo que pide la matriz:** Permite solo 100 caracteres Tipo alfanumericos y caracteres especiales
- **Prueba:** `CatalogosConsultasPruebas#elCampoDelCatalogoRespetaSuMaximo`  (etiquetas: consultas)
- **Lo que valida el codigo:** Maximo de caracteres del alta del catalogo
- **Correr solo este caso:** `mvn test -Dtest='CatalogosConsultasPruebas#elCampoDelCatalogoRespetaSuMaximo'`
- **Renglon de la tabla para este caso:** `{"PF_CP_049", "Nacionalidades", "Descripción", 100}`
- **Ojo:** este metodo cubre varios casos con la tabla `@DataProvider("limitesDeCaracteres")`; el comando los corre todos y la consola imprime el ID de cada uno.
- **Pasos que ejecuta:** `abrirCatalogo` -> `abrirElAltaDeElemento` -> `cuantosCaracteresAceptaElCampo` -> `cerrarElModal`
- **Verificaciones:** 1

```java
        catalogos.abrirCatalogo(catalogo).abrirElAltaDeElemento();
        int aceptados = catalogos.cuantosCaracteresAceptaElCampo(campo, maximo);
        catalogos.cerrarElModal();

        Assert.assertEquals(aceptados, maximo,
                caso + ": el campo \"" + campo + "\" de " + catalogo + " acepto " + aceptados
                        + " caracteres y la matriz pide " + maximo + ".");
```

## PF_CP_050 — Validación del botón ver detalle del menú de catálogos - nacionalidades

- **Modulo:** Expediente pantalla Catálogos - Nacionalidades
- **Lo que pide la matriz:** Muestra la pantalla con la descripción, el botón editar datos y el botón "X"
- **Prueba:** `CatalogosConsultasPruebas#elDetalleDelCatalogoMuestraSusDatos`  (etiquetas: consultas)
- **Lo que valida el codigo:** Ver detalle del catalogo con sus campos, EDITAR DATOS y la X
- **Correr solo este caso:** `mvn test -Dtest='CatalogosConsultasPruebas#elDetalleDelCatalogoMuestraSusDatos'`
- **Renglon de la tabla para este caso:** `{"PF_CP_050", "Nacionalidades", new String[] {"Descripción"}}`
- **Ojo:** este metodo cubre varios casos con la tabla `@DataProvider("detalles")`; el comando los corre todos y la consola imprime el ID de cada uno.
- **Pasos que ejecuta:** `abrirCatalogo` -> `abrirElDetalleDelPrimerRegistro` -> `elModalDebeTenerLosCampos` -> `losCamposDelDetalleDebenSerDeSoloLectura` -> `elModalDebeTenerElBoton` -> `elBotonCerrarDelModalDebeEstarVisible` -> `cerrarElModal`
- **Verificaciones:** 4

```java
        catalogos.abrirCatalogo(catalogo)
                .abrirElDetalleDelPrimerRegistro()
                .elModalDebeTenerLosCampos(campos)
                .losCamposDelDetalleDebenSerDeSoloLectura()
                .elModalDebeTenerElBoton("EDITAR DATOS")
                .elBotonCerrarDelModalDebeEstarVisible()
                .cerrarElModal();
```

## PF_CP_051 — Validación de la edición de alguna nacionalidad sobre el detalle del menú de catálogos - nacionalidades

- **Modulo:** Expediente pantalla Catálogos - Nacionalidades
- **Lo que pide la matriz:** Muestra la tabla de nacionalidades con la información editada
- **Prueba:** `CatalogosAltasPruebas#nacionalidades`  (etiquetas: ola5, catalogos, escribe_datos)
- **Lo que valida el codigo:** Nacionalidades: agregar, editar, inactivar y activar un elemento
- **Correr solo este caso:** `mvn test -Dtest='CatalogosAltasPruebas#nacionalidades'`
- **Pasos que ejecuta:** 
- **Verificaciones:** 0

```java
        cicloDelCatalogo("Nacionalidades");
```

## PF_CP_052 — Validación de la funcinalidad del botón inactivar

- **Modulo:** Expediente pantalla Catálogos - Nacionalidades
- **Lo que pide la matriz:** Muestra la tabla de nacionalidades con la nacaionalidad inactiva
- **Prueba:** `CatalogosAltasPruebas#nacionalidades`  (etiquetas: ola5, catalogos, escribe_datos)
- **Lo que valida el codigo:** Nacionalidades: agregar, editar, inactivar y activar un elemento
- **Correr solo este caso:** `mvn test -Dtest='CatalogosAltasPruebas#nacionalidades'`
- **Pasos que ejecuta:** 
- **Verificaciones:** 0

```java
        cicloDelCatalogo("Nacionalidades");
```

## PF_CP_053 — Validación de la funcinalidad del botón activar registro

- **Modulo:** Expediente pantalla Catálogos - Nacionalidades
- **Lo que pide la matriz:** Muestra la tabla de nacionalidades con la nacaionalidad activa
- **Prueba:** `CatalogosAltasPruebas#nacionalidades`  (etiquetas: ola5, catalogos, escribe_datos)
- **Lo que valida el codigo:** Nacionalidades: agregar, editar, inactivar y activar un elemento
- **Correr solo este caso:** `mvn test -Dtest='CatalogosAltasPruebas#nacionalidades'`
- **Pasos que ejecuta:** 
- **Verificaciones:** 0

```java
        cicloDelCatalogo("Nacionalidades");
```

## PF_CP_054 — Validación de la pantalla del menú Catálogos - Profesiones

- **Modulo:** Expediente pantalla Catálogos - Profesiones
- **Lo que pide la matriz:** Muestra el botón de Agregar elemento, y la tabla de Profesiones con su: -Descripción, -estatus, -detalle -inactivar
- **Prueba:** `CatalogosConsultasPruebas#laPantallaDelCatalogoMuestraSuTabla`  (etiquetas: consultas)
- **Lo que valida el codigo:** Pantalla del catalogo con su boton Agregar elemento y su tabla
- **Correr solo este caso:** `mvn test -Dtest='CatalogosConsultasPruebas#laPantallaDelCatalogoMuestraSuTabla'`
- **Renglon de la tabla para este caso:** `{"PF_CP_054", "Profesiones", new String[] {"Descripción", "Estatus"}}`
- **Ojo:** este metodo cubre varios casos con la tabla `@DataProvider("pantallas")`; el comando los corre todos y la consola imprime el ID de cada uno.
- **Pasos que ejecuta:** `abrirCatalogo` -> `elBotonAgregarElementoDebeEstarVisible` -> `encabezadosDeLaTabla`
- **Verificaciones:** 2

```java
        catalogos.abrirCatalogo(catalogo).elBotonAgregarElementoDebeEstarVisible();

        List<String> actuales = catalogos.encabezadosDeLaTabla();
        for (String columna : columnas) {
            Assert.assertTrue(actuales.stream()
                            .anyMatch(actual -> actual.equalsIgnoreCase(columna)),
                    caso + ": la tabla de " + catalogo + " no muestra la columna \"" + columna
                            + "\". Muestra hoy: " + actuales + ".");
        }
```
- **Prueba:** `CatalogosPruebas#cadaCatalogoMuestraSuTabla`  (etiquetas: catalogos)
- **Lo que valida el codigo:** Cada catalogo muestra su tabla y su boton Agregar elemento
- **Correr solo este caso:** `mvn test -Dtest='CatalogosPruebas#cadaCatalogoMuestraSuTabla'`
- **Ojo:** este metodo cubre varios casos con la tabla `@DataProvider("catalogos")`; el comando los corre todos y la consola imprime el ID de cada uno.
- **Pasos que ejecuta:** `abrirCatalogo` -> `elBotonAgregarElementoDebeEstarVisible` -> `laPantallaDebeTenerUnaTablaConInformacion`
- **Verificaciones:** 2

```java
        new PaginaCatalogos()
                .abrirCatalogo(catalogo)
                .elBotonAgregarElementoDebeEstarVisible();
        inicio.laPantallaDebeTenerUnaTablaConInformacion();
```

## PF_CP_055 — Validación del botón agregar elemento del menú de catálogos - Profesiones

- **Modulo:** Expediente pantalla Catálogos - Profesiones
- **Lo que pide la matriz:** Muestra la Profesion agregada en la tabla de Profesiones
- **Prueba:** `CatalogosAltasPruebas#profesiones`  (etiquetas: ola5, catalogos, escribe_datos)
- **Lo que valida el codigo:** Profesiones: agregar, editar, inactivar y activar un elemento
- **Correr solo este caso:** `mvn test -Dtest='CatalogosAltasPruebas#profesiones'`
- **Pasos que ejecuta:** 
- **Verificaciones:** 0

```java
        cicloDelCatalogo("Profesiones");
```

## PF_CP_056 — Validar Numero de caracteres

- **Modulo:** Expediente pantalla Catálogos - Profesiones
- **Lo que pide la matriz:** Permite solo 250 caracteres Tipo alfanumericos y caracteres especiales
- **Prueba:** `CatalogosConsultasPruebas#elCampoDelCatalogoRespetaSuMaximo`  (etiquetas: consultas)
- **Lo que valida el codigo:** Maximo de caracteres del alta del catalogo
- **Correr solo este caso:** `mvn test -Dtest='CatalogosConsultasPruebas#elCampoDelCatalogoRespetaSuMaximo'`
- **Renglon de la tabla para este caso:** `{"PF_CP_056", "Profesiones", "Descripción", 250}`
- **Ojo:** este metodo cubre varios casos con la tabla `@DataProvider("limitesDeCaracteres")`; el comando los corre todos y la consola imprime el ID de cada uno.
- **Pasos que ejecuta:** `abrirCatalogo` -> `abrirElAltaDeElemento` -> `cuantosCaracteresAceptaElCampo` -> `cerrarElModal`
- **Verificaciones:** 1

```java
        catalogos.abrirCatalogo(catalogo).abrirElAltaDeElemento();
        int aceptados = catalogos.cuantosCaracteresAceptaElCampo(campo, maximo);
        catalogos.cerrarElModal();

        Assert.assertEquals(aceptados, maximo,
                caso + ": el campo \"" + campo + "\" de " + catalogo + " acepto " + aceptados
                        + " caracteres y la matriz pide " + maximo + ".");
```

## PF_CP_057 — Validación del botón ver detalle del menú de catálogos - Profesiones

- **Modulo:** Expediente pantalla Catálogos - Profesiones
- **Lo que pide la matriz:** Muestra la pantalla con la descripción, el botón editar datos y el botón "X"
- **Prueba:** `CatalogosConsultasPruebas#elDetalleDelCatalogoMuestraSusDatos`  (etiquetas: consultas)
- **Lo que valida el codigo:** Ver detalle del catalogo con sus campos, EDITAR DATOS y la X
- **Correr solo este caso:** `mvn test -Dtest='CatalogosConsultasPruebas#elDetalleDelCatalogoMuestraSusDatos'`
- **Renglon de la tabla para este caso:** `{"PF_CP_057", "Profesiones", new String[] {"Descripción"}}`
- **Ojo:** este metodo cubre varios casos con la tabla `@DataProvider("detalles")`; el comando los corre todos y la consola imprime el ID de cada uno.
- **Pasos que ejecuta:** `abrirCatalogo` -> `abrirElDetalleDelPrimerRegistro` -> `elModalDebeTenerLosCampos` -> `losCamposDelDetalleDebenSerDeSoloLectura` -> `elModalDebeTenerElBoton` -> `elBotonCerrarDelModalDebeEstarVisible` -> `cerrarElModal`
- **Verificaciones:** 4

```java
        catalogos.abrirCatalogo(catalogo)
                .abrirElDetalleDelPrimerRegistro()
                .elModalDebeTenerLosCampos(campos)
                .losCamposDelDetalleDebenSerDeSoloLectura()
                .elModalDebeTenerElBoton("EDITAR DATOS")
                .elBotonCerrarDelModalDebeEstarVisible()
                .cerrarElModal();
```

## PF_CP_058 — Validación de la edición de alguna Profesiones sobre el detalle del menú de catálogos - Profesiones

- **Modulo:** Expediente pantalla Catálogos - Profesiones
- **Lo que pide la matriz:** Muestra la tabla de Profesiones con la información editada
- **Prueba:** `CatalogosAltasPruebas#profesiones`  (etiquetas: ola5, catalogos, escribe_datos)
- **Lo que valida el codigo:** Profesiones: agregar, editar, inactivar y activar un elemento
- **Correr solo este caso:** `mvn test -Dtest='CatalogosAltasPruebas#profesiones'`
- **Pasos que ejecuta:** 
- **Verificaciones:** 0

```java
        cicloDelCatalogo("Profesiones");
```

## PF_CP_059 — Validación de la funcinalidad del botón inactivar

- **Modulo:** Expediente pantalla Catálogos - Profesiones
- **Lo que pide la matriz:** Muestra la tabla de Profesiones con la Profesion inactiva
- **Prueba:** `CatalogosAltasPruebas#profesiones`  (etiquetas: ola5, catalogos, escribe_datos)
- **Lo que valida el codigo:** Profesiones: agregar, editar, inactivar y activar un elemento
- **Correr solo este caso:** `mvn test -Dtest='CatalogosAltasPruebas#profesiones'`
- **Pasos que ejecuta:** 
- **Verificaciones:** 0

```java
        cicloDelCatalogo("Profesiones");
```

## PF_CP_060 — Validación de la funcinalidad del botón activar registro

- **Modulo:** Expediente pantalla Catálogos - Profesiones
- **Lo que pide la matriz:** Muestra la tabla de Profesiones con la Profesion activa
- **Prueba:** `CatalogosAltasPruebas#profesiones`  (etiquetas: ola5, catalogos, escribe_datos)
- **Lo que valida el codigo:** Profesiones: agregar, editar, inactivar y activar un elemento
- **Correr solo este caso:** `mvn test -Dtest='CatalogosAltasPruebas#profesiones'`
- **Pasos que ejecuta:** 
- **Verificaciones:** 0

```java
        cicloDelCatalogo("Profesiones");
```

## PF_CP_061 — Validación de la pantalla del menú Catálogos - Campaña

- **Modulo:** Expediente pantalla Catálogos - Campaña
- **Lo que pide la matriz:** Muestra el botón de Agregar elemento, y la tabla de Campañas con su: -Codigo, -promotor -estatus, -detalle -inactivar
- **Prueba:** `CatalogosConsultasPruebas#laPantallaDelCatalogoMuestraSuTabla`  (etiquetas: consultas)
- **Lo que valida el codigo:** Pantalla del catalogo con su boton Agregar elemento y su tabla
- **Correr solo este caso:** `mvn test -Dtest='CatalogosConsultasPruebas#laPantallaDelCatalogoMuestraSuTabla'`
- **Renglon de la tabla para este caso:** `{"PF_CP_061", "Campaña", new String[] {"Código", "Promotor", "Estatus"}}`
- **Ojo:** este metodo cubre varios casos con la tabla `@DataProvider("pantallas")`; el comando los corre todos y la consola imprime el ID de cada uno.
- **Pasos que ejecuta:** `abrirCatalogo` -> `elBotonAgregarElementoDebeEstarVisible` -> `encabezadosDeLaTabla`
- **Verificaciones:** 2

```java
        catalogos.abrirCatalogo(catalogo).elBotonAgregarElementoDebeEstarVisible();

        List<String> actuales = catalogos.encabezadosDeLaTabla();
        for (String columna : columnas) {
            Assert.assertTrue(actuales.stream()
                            .anyMatch(actual -> actual.equalsIgnoreCase(columna)),
                    caso + ": la tabla de " + catalogo + " no muestra la columna \"" + columna
                            + "\". Muestra hoy: " + actuales + ".");
        }
```
- **Prueba:** `CatalogosPruebas#cadaCatalogoMuestraSuTabla`  (etiquetas: catalogos)
- **Lo que valida el codigo:** Cada catalogo muestra su tabla y su boton Agregar elemento
- **Correr solo este caso:** `mvn test -Dtest='CatalogosPruebas#cadaCatalogoMuestraSuTabla'`
- **Ojo:** este metodo cubre varios casos con la tabla `@DataProvider("catalogos")`; el comando los corre todos y la consola imprime el ID de cada uno.
- **Pasos que ejecuta:** `abrirCatalogo` -> `elBotonAgregarElementoDebeEstarVisible` -> `laPantallaDebeTenerUnaTablaConInformacion`
- **Verificaciones:** 2

```java
        new PaginaCatalogos()
                .abrirCatalogo(catalogo)
                .elBotonAgregarElementoDebeEstarVisible();
        inicio.laPantallaDebeTenerUnaTablaConInformacion();
```

## PF_CP_062 — Validación del botón agregar elemento del menú de catálogos - Campaña

- **Modulo:** Expediente pantalla Catálogos - Campaña
- **Lo que pide la matriz:** Muestra la Campaña agregada en la tabla de Campañas
- **Prueba:** `CatalogosAltasPruebas#campana`  (etiquetas: ola5, catalogos, escribe_datos)
- **Lo que valida el codigo:** Campaña: agregar, editar, inactivar y activar un elemento
- **Correr solo este caso:** `mvn test -Dtest='CatalogosAltasPruebas#campana'`
- **Pasos que ejecuta:** 
- **Verificaciones:** 0

```java
        cicloDelCatalogo("Campaña");
```

## PF_CP_063 — Validar Numero de caracteres campo Codigo

- **Modulo:** Expediente pantalla Catálogos - Campaña
- **Lo que pide la matriz:** Permite solo 250 caracteres (Permite mas de 250 caracteres) Tipo alfanumericos y caracteres especiales
- **Prueba:** `CatalogosConsultasPruebas#pfCp063MaximoDelCodigoDeCampana`  (etiquetas: consultas, defecto_conocido)
- **Lo que valida el codigo:** El campo Codigo de Campaña acepta como maximo 250 caracteres
- **Correr solo este caso:** `mvn test -Dtest='CatalogosConsultasPruebas#pfCp063MaximoDelCodigoDeCampana'`
- **Pasos que ejecuta:** `abrirCatalogo` -> `abrirElAltaDeElemento` -> `cuantosCaracteresAceptaElCampo` -> `cerrarElModal`
- **Verificaciones:** 1

```java
        catalogos.abrirCatalogo("Campaña").abrirElAltaDeElemento();
        int aceptados = catalogos.cuantosCaracteresAceptaElCampo("Código", 250);
        catalogos.cerrarElModal();

        Assert.assertEquals(aceptados, 250,
                "El campo Codigo de Campaña acepto " + aceptados + " caracteres: la matriz pide "
                        + "250 y ya senala en su resultado esperado que permite mas (DEF_03).");
```

## PF_CP_064 — Validar Numero de caracteres campo promotor

- **Modulo:** Expediente pantalla Catálogos - Campaña
- **Lo que pide la matriz:** Permite solo 250 caracteres Tipo alfanumericos y caracteres especiales
- **Prueba:** `CatalogosConsultasPruebas#elCampoDelCatalogoRespetaSuMaximo`  (etiquetas: consultas)
- **Lo que valida el codigo:** Maximo de caracteres del alta del catalogo
- **Correr solo este caso:** `mvn test -Dtest='CatalogosConsultasPruebas#elCampoDelCatalogoRespetaSuMaximo'`
- **Renglon de la tabla para este caso:** `{"PF_CP_064", "Campaña", "Promotor", 250}`
- **Ojo:** este metodo cubre varios casos con la tabla `@DataProvider("limitesDeCaracteres")`; el comando los corre todos y la consola imprime el ID de cada uno.
- **Pasos que ejecuta:** `abrirCatalogo` -> `abrirElAltaDeElemento` -> `cuantosCaracteresAceptaElCampo` -> `cerrarElModal`
- **Verificaciones:** 1

```java
        catalogos.abrirCatalogo(catalogo).abrirElAltaDeElemento();
        int aceptados = catalogos.cuantosCaracteresAceptaElCampo(campo, maximo);
        catalogos.cerrarElModal();

        Assert.assertEquals(aceptados, maximo,
                caso + ": el campo \"" + campo + "\" de " + catalogo + " acepto " + aceptados
                        + " caracteres y la matriz pide " + maximo + ".");
```

## PF_CP_065 — Validación del botón ver detalle del menú de catálogos - Campaña

- **Modulo:** Expediente pantalla Catálogos - Campaña
- **Lo que pide la matriz:** Muestra la pantalla con el codigo, promotor, el botón editar datos y el botón "X"
- **Prueba:** `CatalogosConsultasPruebas#elDetalleDelCatalogoMuestraSusDatos`  (etiquetas: consultas)
- **Lo que valida el codigo:** Ver detalle del catalogo con sus campos, EDITAR DATOS y la X
- **Correr solo este caso:** `mvn test -Dtest='CatalogosConsultasPruebas#elDetalleDelCatalogoMuestraSusDatos'`
- **Renglon de la tabla para este caso:** `{"PF_CP_065", "Campaña", new String[] {"Código", "Promotor"}}`
- **Ojo:** este metodo cubre varios casos con la tabla `@DataProvider("detalles")`; el comando los corre todos y la consola imprime el ID de cada uno.
- **Pasos que ejecuta:** `abrirCatalogo` -> `abrirElDetalleDelPrimerRegistro` -> `elModalDebeTenerLosCampos` -> `losCamposDelDetalleDebenSerDeSoloLectura` -> `elModalDebeTenerElBoton` -> `elBotonCerrarDelModalDebeEstarVisible` -> `cerrarElModal`
- **Verificaciones:** 4

```java
        catalogos.abrirCatalogo(catalogo)
                .abrirElDetalleDelPrimerRegistro()
                .elModalDebeTenerLosCampos(campos)
                .losCamposDelDetalleDebenSerDeSoloLectura()
                .elModalDebeTenerElBoton("EDITAR DATOS")
                .elBotonCerrarDelModalDebeEstarVisible()
                .cerrarElModal();
```

## PF_CP_066 — Validación de la edición de alguna Campaña sobre el detalle del menú de catálogos - Campaña

- **Modulo:** Expediente pantalla Catálogos - Campaña
- **Lo que pide la matriz:** Muestra la tabla de Campañas con la información editada
- **Prueba:** `CatalogosAltasPruebas#campana`  (etiquetas: ola5, catalogos, escribe_datos)
- **Lo que valida el codigo:** Campaña: agregar, editar, inactivar y activar un elemento
- **Correr solo este caso:** `mvn test -Dtest='CatalogosAltasPruebas#campana'`
- **Pasos que ejecuta:** 
- **Verificaciones:** 0

```java
        cicloDelCatalogo("Campaña");
```

## PF_CP_067 — Validación de la funcinalidad del botón inactivar

- **Modulo:** Expediente pantalla Catálogos - Campaña
- **Lo que pide la matriz:** Muestra la tabla de Campañas con la Campaña inactiva
- **Prueba:** `CatalogosAltasPruebas#campana`  (etiquetas: ola5, catalogos, escribe_datos)
- **Lo que valida el codigo:** Campaña: agregar, editar, inactivar y activar un elemento
- **Correr solo este caso:** `mvn test -Dtest='CatalogosAltasPruebas#campana'`
- **Pasos que ejecuta:** 
- **Verificaciones:** 0

```java
        cicloDelCatalogo("Campaña");
```

## PF_CP_068 — Validación de la funcinalidad del botón activar registro

- **Modulo:** Expediente pantalla Catálogos - Campaña
- **Lo que pide la matriz:** Muestra la tabla de Campañas con la Campaña activa
- **Prueba:** `CatalogosAltasPruebas#campana`  (etiquetas: ola5, catalogos, escribe_datos)
- **Lo que valida el codigo:** Campaña: agregar, editar, inactivar y activar un elemento
- **Correr solo este caso:** `mvn test -Dtest='CatalogosAltasPruebas#campana'`
- **Pasos que ejecuta:** 
- **Verificaciones:** 0

```java
        cicloDelCatalogo("Campaña");
```

## PF_CP_069 — Validación de la pantalla del menú Catálogos - Codigo de pais

- **Modulo:** Expediente pantalla Catálogos - Codigo de pais
- **Lo que pide la matriz:** Muestra el botón de Agregar elemento, y la tabla de codigo de pais con su: -Imagen, -Codigo -estatus, -detalle -inactivar
- **Prueba:** `CatalogosConsultasPruebas#laPantallaDelCatalogoMuestraSuTabla`  (etiquetas: consultas)
- **Lo que valida el codigo:** Pantalla del catalogo con su boton Agregar elemento y su tabla
- **Correr solo este caso:** `mvn test -Dtest='CatalogosConsultasPruebas#laPantallaDelCatalogoMuestraSuTabla'`
- **Renglon de la tabla para este caso:** `{"PF_CP_069", "Codigo de pais", new String[] {"Imagen", "Código", "Estatus"}}`
- **Ojo:** este metodo cubre varios casos con la tabla `@DataProvider("pantallas")`; el comando los corre todos y la consola imprime el ID de cada uno.
- **Pasos que ejecuta:** `abrirCatalogo` -> `elBotonAgregarElementoDebeEstarVisible` -> `encabezadosDeLaTabla`
- **Verificaciones:** 2

```java
        catalogos.abrirCatalogo(catalogo).elBotonAgregarElementoDebeEstarVisible();

        List<String> actuales = catalogos.encabezadosDeLaTabla();
        for (String columna : columnas) {
            Assert.assertTrue(actuales.stream()
                            .anyMatch(actual -> actual.equalsIgnoreCase(columna)),
                    caso + ": la tabla de " + catalogo + " no muestra la columna \"" + columna
                            + "\". Muestra hoy: " + actuales + ".");
        }
```
- **Prueba:** `CatalogosPruebas#cadaCatalogoMuestraSuTabla`  (etiquetas: catalogos)
- **Lo que valida el codigo:** Cada catalogo muestra su tabla y su boton Agregar elemento
- **Correr solo este caso:** `mvn test -Dtest='CatalogosPruebas#cadaCatalogoMuestraSuTabla'`
- **Ojo:** este metodo cubre varios casos con la tabla `@DataProvider("catalogos")`; el comando los corre todos y la consola imprime el ID de cada uno.
- **Pasos que ejecuta:** `abrirCatalogo` -> `elBotonAgregarElementoDebeEstarVisible` -> `laPantallaDebeTenerUnaTablaConInformacion`
- **Verificaciones:** 2

```java
        new PaginaCatalogos()
                .abrirCatalogo(catalogo)
                .elBotonAgregarElementoDebeEstarVisible();
        inicio.laPantallaDebeTenerUnaTablaConInformacion();
```

## PF_CP_070 — Validación del botón agregar elemento del menú de catálogos - Codigo de pais

- **Modulo:** Expediente pantalla Catálogos - Codigo de pais
- **Lo que pide la matriz:** Muestra la nacionalidad agregada en la tabla de Codigo de pais
- **Prueba:** `CatalogosAltasPruebas#codigoDePais`  (etiquetas: ola5, catalogos, escribe_datos)
- **Lo que valida el codigo:** Codigo de pais: agregar, editar, inactivar y activar un elemento
- **Correr solo este caso:** `mvn test -Dtest='CatalogosAltasPruebas#codigoDePais'`
- **Pasos que ejecuta:** 
- **Verificaciones:** 0

```java
        cicloDelCatalogo("Codigo de pais");
```

## PF_CP_071 — Validar Numero de caracteres campo Codigo

- **Modulo:** Expediente pantalla Catálogos - Codigo de pais
- **Lo que pide la matriz:** Permite solo 100 caracteres Tipo alfanumericos y +
- **Prueba:** `CatalogosConsultasPruebas#pfCp071MaximoDelCodigoDePais`  (etiquetas: consultas, regla_por_confirmar)
- **Lo que valida el codigo:** El campo Codigo de Codigo de pais acepta 100 caracteres
- **Correr solo este caso:** `mvn test -Dtest='CatalogosConsultasPruebas#pfCp071MaximoDelCodigoDePais'`
- **Pasos que ejecuta:** `abrirCatalogo` -> `abrirElAltaDeElemento` -> `cuantosCaracteresAceptaElCampo` -> `cerrarElModal`
- **Verificaciones:** 1

```java
        catalogos.abrirCatalogo("Codigo de pais").abrirElAltaDeElemento();
        int aceptados = catalogos.cuantosCaracteresAceptaElCampo("Código", 100);
        catalogos.cerrarElModal();

        Assert.assertEquals(aceptados, 100,
                "El campo Codigo de Codigo de pais acepto " + aceptados + " caracteres y la "
                        + "matriz pide 100. Por confirmar con negocio: un codigo de pais real "
                        + "no necesita 100 caracteres.");
```

## PF_CP_072 — Validación del botón ver detalle del menú de catálogos - Codigo de pais

- **Modulo:** Expediente pantalla Catálogos - Codigo de pais
- **Lo que pide la matriz:** Muestra la pantalla con la imagen, codigo, el botón editar datos y el botón "X"
- **Prueba:** `CatalogosConsultasPruebas#elDetalleDelCatalogoMuestraSusDatos`  (etiquetas: consultas)
- **Lo que valida el codigo:** Ver detalle del catalogo con sus campos, EDITAR DATOS y la X
- **Correr solo este caso:** `mvn test -Dtest='CatalogosConsultasPruebas#elDetalleDelCatalogoMuestraSusDatos'`
- **Renglon de la tabla para este caso:** `{"PF_CP_072", "Codigo de pais", new String[] {"Código"}}`
- **Ojo:** este metodo cubre varios casos con la tabla `@DataProvider("detalles")`; el comando los corre todos y la consola imprime el ID de cada uno.
- **Pasos que ejecuta:** `abrirCatalogo` -> `abrirElDetalleDelPrimerRegistro` -> `elModalDebeTenerLosCampos` -> `losCamposDelDetalleDebenSerDeSoloLectura` -> `elModalDebeTenerElBoton` -> `elBotonCerrarDelModalDebeEstarVisible` -> `cerrarElModal`
- **Verificaciones:** 4

```java
        catalogos.abrirCatalogo(catalogo)
                .abrirElDetalleDelPrimerRegistro()
                .elModalDebeTenerLosCampos(campos)
                .losCamposDelDetalleDebenSerDeSoloLectura()
                .elModalDebeTenerElBoton("EDITAR DATOS")
                .elBotonCerrarDelModalDebeEstarVisible()
                .cerrarElModal();
```

## PF_CP_073 — Validación de la edición de algun codigo de pais sobre el detalle del menú de catálogos - Codigo de pais

- **Modulo:** Expediente pantalla Catálogos - Codigo de pais
- **Lo que pide la matriz:** Muestra la tabla de Codigo de pais con la información editada
- **Prueba:** `CatalogosAltasPruebas#codigoDePais`  (etiquetas: ola5, catalogos, escribe_datos)
- **Lo que valida el codigo:** Codigo de pais: agregar, editar, inactivar y activar un elemento
- **Correr solo este caso:** `mvn test -Dtest='CatalogosAltasPruebas#codigoDePais'`
- **Pasos que ejecuta:** 
- **Verificaciones:** 0

```java
        cicloDelCatalogo("Codigo de pais");
```

## PF_CP_074 — Validación de la funcinalidad del botón inactivar

- **Modulo:** Expediente pantalla Catálogos - Codigo de pais
- **Lo que pide la matriz:** Muestra la tabla de Codigo de pais con el Codigo de pais inactiva
- **Prueba:** `CatalogosAltasPruebas#codigoDePais`  (etiquetas: ola5, catalogos, escribe_datos)
- **Lo que valida el codigo:** Codigo de pais: agregar, editar, inactivar y activar un elemento
- **Correr solo este caso:** `mvn test -Dtest='CatalogosAltasPruebas#codigoDePais'`
- **Pasos que ejecuta:** 
- **Verificaciones:** 0

```java
        cicloDelCatalogo("Codigo de pais");
```

## PF_CP_075 — Validación de la funcinalidad del botón activar registro

- **Modulo:** Expediente pantalla Catálogos - Codigo de pais
- **Lo que pide la matriz:** Muestra la tabla de Codigo de pais con el Codigo de pais activa
- **Prueba:** `CatalogosAltasPruebas#codigoDePais`  (etiquetas: ola5, catalogos, escribe_datos)
- **Lo que valida el codigo:** Codigo de pais: agregar, editar, inactivar y activar un elemento
- **Correr solo este caso:** `mvn test -Dtest='CatalogosAltasPruebas#codigoDePais'`
- **Pasos que ejecuta:** 
- **Verificaciones:** 0

```java
        cicloDelCatalogo("Codigo de pais");
```

## PF_CP_076 — Validación de la pantalla del menú Catálogos - Productos

- **Modulo:** Expediente pantalla Catálogos - Productos
- **Lo que pide la matriz:** Muestra el botón de Agregar elemento, y la tabla de productos con su: -Imagen, -nombre -codigo -link -estatus, -detalle -inactivar
- **Prueba:** `CatalogosConsultasPruebas#laPantallaDelCatalogoMuestraSuTabla`  (etiquetas: consultas)
- **Lo que valida el codigo:** Pantalla del catalogo con su boton Agregar elemento y su tabla
- **Correr solo este caso:** `mvn test -Dtest='CatalogosConsultasPruebas#laPantallaDelCatalogoMuestraSuTabla'`
- **Renglon de la tabla para este caso:** `{"PF_CP_076", "Productos"`
- **Ojo:** este metodo cubre varios casos con la tabla `@DataProvider("pantallas")`; el comando los corre todos y la consola imprime el ID de cada uno.
- **Pasos que ejecuta:** `abrirCatalogo` -> `elBotonAgregarElementoDebeEstarVisible` -> `encabezadosDeLaTabla`
- **Verificaciones:** 2

```java
        catalogos.abrirCatalogo(catalogo).elBotonAgregarElementoDebeEstarVisible();

        List<String> actuales = catalogos.encabezadosDeLaTabla();
        for (String columna : columnas) {
            Assert.assertTrue(actuales.stream()
                            .anyMatch(actual -> actual.equalsIgnoreCase(columna)),
                    caso + ": la tabla de " + catalogo + " no muestra la columna \"" + columna
                            + "\". Muestra hoy: " + actuales + ".");
        }
```
- **Prueba:** `CatalogosPruebas#cadaCatalogoMuestraSuTabla`  (etiquetas: catalogos)
- **Lo que valida el codigo:** Cada catalogo muestra su tabla y su boton Agregar elemento
- **Correr solo este caso:** `mvn test -Dtest='CatalogosPruebas#cadaCatalogoMuestraSuTabla'`
- **Ojo:** este metodo cubre varios casos con la tabla `@DataProvider("catalogos")`; el comando los corre todos y la consola imprime el ID de cada uno.
- **Pasos que ejecuta:** `abrirCatalogo` -> `elBotonAgregarElementoDebeEstarVisible` -> `laPantallaDebeTenerUnaTablaConInformacion`
- **Verificaciones:** 2

```java
        new PaginaCatalogos()
                .abrirCatalogo(catalogo)
                .elBotonAgregarElementoDebeEstarVisible();
        inicio.laPantallaDebeTenerUnaTablaConInformacion();
```

## PF_CP_077 — Validación del botón agregar elemento del menú de catálogos - Productos

- **Modulo:** Expediente pantalla Catálogos - Productos
- **Lo que pide la matriz:** Muestra el Producto agregada en la tabla de Productos
- **Prueba:** `CatalogosAltasPruebas#productos`  (etiquetas: ola5, catalogos, escribe_datos)
- **Lo que valida el codigo:** Productos: agregar, editar, inactivar y activar un elemento
- **Correr solo este caso:** `mvn test -Dtest='CatalogosAltasPruebas#productos'`
- **Pasos que ejecuta:** 
- **Verificaciones:** 0

```java
        cicloDelCatalogo("Productos");
```

## PF_CP_078 — Validar Numero de caracteres campo Nombre

- **Modulo:** Expediente pantalla Catálogos - Productos
- **Lo que pide la matriz:** Permite solo 100 caracteres Tipo alfanumericos y caracteres especiales
- **Prueba:** `CatalogosConsultasPruebas#elCampoDelCatalogoRespetaSuMaximo`  (etiquetas: consultas)
- **Lo que valida el codigo:** Maximo de caracteres del alta del catalogo
- **Correr solo este caso:** `mvn test -Dtest='CatalogosConsultasPruebas#elCampoDelCatalogoRespetaSuMaximo'`
- **Renglon de la tabla para este caso:** `{"PF_CP_078", "Productos", "Nombre", 100}`
- **Ojo:** este metodo cubre varios casos con la tabla `@DataProvider("limitesDeCaracteres")`; el comando los corre todos y la consola imprime el ID de cada uno.
- **Pasos que ejecuta:** `abrirCatalogo` -> `abrirElAltaDeElemento` -> `cuantosCaracteresAceptaElCampo` -> `cerrarElModal`
- **Verificaciones:** 1

```java
        catalogos.abrirCatalogo(catalogo).abrirElAltaDeElemento();
        int aceptados = catalogos.cuantosCaracteresAceptaElCampo(campo, maximo);
        catalogos.cerrarElModal();

        Assert.assertEquals(aceptados, maximo,
                caso + ": el campo \"" + campo + "\" de " + catalogo + " acepto " + aceptados
                        + " caracteres y la matriz pide " + maximo + ".");
```

## PF_CP_079 — Validar Numero de caracteres campo Codigo

- **Modulo:** Expediente pantalla Catálogos - Productos
- **Lo que pide la matriz:** Permite solo 10 caracteres Tipo alfanumericos
- **Prueba:** `CatalogosConsultasPruebas#pfCp079MaximoDelCodigoDeProductos`  (etiquetas: consultas)
- **Lo que valida el codigo:** El campo Codigo de Productos acepta 10 caracteres
- **Correr solo este caso:** `mvn test -Dtest='CatalogosConsultasPruebas#pfCp079MaximoDelCodigoDeProductos'`
- **Pasos que ejecuta:** `abrirCatalogo` -> `abrirElAltaDeElemento` -> `cuantosCaracteresAceptaElCampo` -> `cerrarElModal`
- **Verificaciones:** 1

```java
        catalogos.abrirCatalogo("Productos").abrirElAltaDeElemento();
        int aceptados = catalogos.cuantosCaracteresAceptaElCampo("Código", 10);
        catalogos.cerrarElModal();

        Assert.assertEquals(aceptados, 10,
                "El campo Codigo de Productos acepto " + aceptados + " caracteres.");
```

## PF_CP_080 — Validar Numero de caracteres campo Link

- **Modulo:** Expediente pantalla Catálogos - Productos
- **Lo que pide la matriz:** Permite solo 200 caracteres Tipo alfanumericos y caracteres especiales
- **Prueba:** `CatalogosConsultasPruebas#elCampoDelCatalogoRespetaSuMaximo`  (etiquetas: consultas)
- **Lo que valida el codigo:** Maximo de caracteres del alta del catalogo
- **Correr solo este caso:** `mvn test -Dtest='CatalogosConsultasPruebas#elCampoDelCatalogoRespetaSuMaximo'`
- **Renglon de la tabla para este caso:** `{"PF_CP_080", "Productos", "Link", 200}`
- **Ojo:** este metodo cubre varios casos con la tabla `@DataProvider("limitesDeCaracteres")`; el comando los corre todos y la consola imprime el ID de cada uno.
- **Pasos que ejecuta:** `abrirCatalogo` -> `abrirElAltaDeElemento` -> `cuantosCaracteresAceptaElCampo` -> `cerrarElModal`
- **Verificaciones:** 1

```java
        catalogos.abrirCatalogo(catalogo).abrirElAltaDeElemento();
        int aceptados = catalogos.cuantosCaracteresAceptaElCampo(campo, maximo);
        catalogos.cerrarElModal();

        Assert.assertEquals(aceptados, maximo,
                caso + ": el campo \"" + campo + "\" de " + catalogo + " acepto " + aceptados
                        + " caracteres y la matriz pide " + maximo + ".");
```

## PF_CP_081 — Validación del botón ver detalle del menú de catálogos - Productos

- **Modulo:** Expediente pantalla Catálogos - Productos
- **Lo que pide la matriz:** Muestra la pantalla con la imagen, nombre, codigo, link, el botón editar datos y el botón "X"
- **Prueba:** `CatalogosConsultasPruebas#elDetalleDelCatalogoMuestraSusDatos`  (etiquetas: consultas)
- **Lo que valida el codigo:** Ver detalle del catalogo con sus campos, EDITAR DATOS y la X
- **Correr solo este caso:** `mvn test -Dtest='CatalogosConsultasPruebas#elDetalleDelCatalogoMuestraSusDatos'`
- **Renglon de la tabla para este caso:** `{"PF_CP_081", "Productos", new String[] {"Nombre", "Código", "Link"}}`
- **Ojo:** este metodo cubre varios casos con la tabla `@DataProvider("detalles")`; el comando los corre todos y la consola imprime el ID de cada uno.
- **Pasos que ejecuta:** `abrirCatalogo` -> `abrirElDetalleDelPrimerRegistro` -> `elModalDebeTenerLosCampos` -> `losCamposDelDetalleDebenSerDeSoloLectura` -> `elModalDebeTenerElBoton` -> `elBotonCerrarDelModalDebeEstarVisible` -> `cerrarElModal`
- **Verificaciones:** 4

```java
        catalogos.abrirCatalogo(catalogo)
                .abrirElDetalleDelPrimerRegistro()
                .elModalDebeTenerLosCampos(campos)
                .losCamposDelDetalleDebenSerDeSoloLectura()
                .elModalDebeTenerElBoton("EDITAR DATOS")
                .elBotonCerrarDelModalDebeEstarVisible()
                .cerrarElModal();
```

## PF_CP_082 — Validación de la edición de alguna Producto sobre el detalle del menú de catálogos - Productos

- **Modulo:** Expediente pantalla Catálogos - Productos
- **Lo que pide la matriz:** Muestra la tabla de Productos con la información editada
- **Prueba:** `CatalogosAltasPruebas#productos`  (etiquetas: ola5, catalogos, escribe_datos)
- **Lo que valida el codigo:** Productos: agregar, editar, inactivar y activar un elemento
- **Correr solo este caso:** `mvn test -Dtest='CatalogosAltasPruebas#productos'`
- **Pasos que ejecuta:** 
- **Verificaciones:** 0

```java
        cicloDelCatalogo("Productos");
```

## PF_CP_083 — Validación de la funcinalidad del botón inactivar

- **Modulo:** Expediente pantalla Catálogos - Productos
- **Lo que pide la matriz:** Muestra la tabla de Productos con el Producto inactiva
- **Prueba:** `CatalogosAltasPruebas#productos`  (etiquetas: ola5, catalogos, escribe_datos)
- **Lo que valida el codigo:** Productos: agregar, editar, inactivar y activar un elemento
- **Correr solo este caso:** `mvn test -Dtest='CatalogosAltasPruebas#productos'`
- **Pasos que ejecuta:** 
- **Verificaciones:** 0

```java
        cicloDelCatalogo("Productos");
```

## PF_CP_084 — Validación de la funcinalidad del botón activar registro

- **Modulo:** Expediente pantalla Catálogos - Productos
- **Lo que pide la matriz:** Muestra la tabla de Productos con el Producto activa
- **Prueba:** `CatalogosAltasPruebas#productos`  (etiquetas: ola5, catalogos, escribe_datos)
- **Lo que valida el codigo:** Productos: agregar, editar, inactivar y activar un elemento
- **Correr solo este caso:** `mvn test -Dtest='CatalogosAltasPruebas#productos'`
- **Pasos que ejecuta:** 
- **Verificaciones:** 0

```java
        cicloDelCatalogo("Productos");
```

## PF_CP_085 — Validación de la pantalla del menú Catálogos - Dias festivos

- **Modulo:** Expediente pantalla Catálogos - Dias festivos
- **Lo que pide la matriz:** Muestra el botón de Agregar elemento, y la tabla de Dias festivos con su: -Dia festivo, -estatus, -detalle -inactivar
- **Prueba:** `CatalogosConsultasPruebas#laPantallaDelCatalogoMuestraSuTabla`  (etiquetas: consultas)
- **Lo que valida el codigo:** Pantalla del catalogo con su boton Agregar elemento y su tabla
- **Correr solo este caso:** `mvn test -Dtest='CatalogosConsultasPruebas#laPantallaDelCatalogoMuestraSuTabla'`
- **Renglon de la tabla para este caso:** `{"PF_CP_085", "Dias festivos"`
- **Ojo:** este metodo cubre varios casos con la tabla `@DataProvider("pantallas")`; el comando los corre todos y la consola imprime el ID de cada uno.
- **Pasos que ejecuta:** `abrirCatalogo` -> `elBotonAgregarElementoDebeEstarVisible` -> `encabezadosDeLaTabla`
- **Verificaciones:** 2

```java
        catalogos.abrirCatalogo(catalogo).elBotonAgregarElementoDebeEstarVisible();

        List<String> actuales = catalogos.encabezadosDeLaTabla();
        for (String columna : columnas) {
            Assert.assertTrue(actuales.stream()
                            .anyMatch(actual -> actual.equalsIgnoreCase(columna)),
                    caso + ": la tabla de " + catalogo + " no muestra la columna \"" + columna
                            + "\". Muestra hoy: " + actuales + ".");
        }
```
- **Prueba:** `CatalogosPruebas#cadaCatalogoMuestraSuTabla`  (etiquetas: catalogos)
- **Lo que valida el codigo:** Cada catalogo muestra su tabla y su boton Agregar elemento
- **Correr solo este caso:** `mvn test -Dtest='CatalogosPruebas#cadaCatalogoMuestraSuTabla'`
- **Ojo:** este metodo cubre varios casos con la tabla `@DataProvider("catalogos")`; el comando los corre todos y la consola imprime el ID de cada uno.
- **Pasos que ejecuta:** `abrirCatalogo` -> `elBotonAgregarElementoDebeEstarVisible` -> `laPantallaDebeTenerUnaTablaConInformacion`
- **Verificaciones:** 2

```java
        new PaginaCatalogos()
                .abrirCatalogo(catalogo)
                .elBotonAgregarElementoDebeEstarVisible();
        inicio.laPantallaDebeTenerUnaTablaConInformacion();
```

## PF_CP_086 — Validación del botón agregar elemento del menú de catálogos - Dias festivos

- **Modulo:** Expediente pantalla Catálogos - Dias festivos
- **Lo que pide la matriz:** Muestra la nacionalidad agregada en la tabla de Dias festivos
- **Prueba:** `CatalogosAltasPruebas#diasFestivos`  (etiquetas: ola5, catalogos, escribe_datos)
- **Lo que valida el codigo:** Dias festivos: agregar, editar, inactivar y activar un elemento
- **Correr solo este caso:** `mvn test -Dtest='CatalogosAltasPruebas#diasFestivos'`
- **Pasos que ejecuta:** 
- **Verificaciones:** 0

```java
        cicloDelCatalogo(ElementoDeCatalogo.DIAS_FESTIVOS);
```

## PF_CP_087 — Validar Numero de caracteres campo Fecha

- **Modulo:** Expediente pantalla Catálogos - Dias festivos
- **Lo que pide la matriz:** Permite solo 8 caracteres Tipo numerico
- **Prueba:** `CatalogosConsultasPruebas#pfCp087DigitosDelDiaFestivo`  (etiquetas: consultas)
- **Lo que valida el codigo:** El dia festivo acepta 8 digitos con formato dd/mm/aaaa
- **Correr solo este caso:** `mvn test -Dtest='CatalogosConsultasPruebas#pfCp087DigitosDelDiaFestivo'`
- **Pasos que ejecuta:** `abrirCatalogo` -> `abrirElAltaDeElemento` -> `cuantosDigitosAceptaElCampo` -> `cerrarElModal`
- **Verificaciones:** 1

```java
        catalogos.abrirCatalogo("Dias festivos").abrirElAltaDeElemento();
        int digitos = catalogos.cuantosDigitosAceptaElCampo("Dia festivo(dd/mm/yyyy)", 8);
        catalogos.cerrarElModal();

        Assert.assertEquals(digitos, 8,
                "El campo Dia festivo acepto " + digitos + " digitos y la matriz pide 8.");
```

## PF_CP_088 — Validar Botón calendario

- **Modulo:** Expediente pantalla Catálogos - Dias festivos
- **Lo que pide la matriz:** Validar que permita seleccionar una fecha del calendario Solo estarán habilitadas las fechas desde hoy en adelante.
- **Prueba:** `CatalogosConsultasPruebas#pfCp088CalendarioDeDiasFestivos`  (etiquetas: consultas)
- **Lo que valida el codigo:** El calendario de Dias festivos solo habilita de hoy en adelante
- **Correr solo este caso:** `mvn test -Dtest='CatalogosConsultasPruebas#pfCp088CalendarioDeDiasFestivos'`
- **Pasos que ejecuta:** `abrirCatalogo` -> `abrirElAltaDeElemento` -> `elModalDebeTenerCalendario` -> `abrirElCalendario` -> `soloDebeHabilitarDesdeHoy` -> `cerrarElCalendario` -> `cerrarElModal`
- **Verificaciones:** 2

```java
        catalogos.abrirCatalogo("Dias festivos")
                .abrirElAltaDeElemento()
                .elModalDebeTenerCalendario()
                .abrirElCalendario()
                .soloDebeHabilitarDesdeHoy()
                .cerrarElCalendario();
        catalogos.cerrarElModal();
```

## PF_CP_089 — Validación del botón ver detalle del menú de catálogos - Dias festivos

- **Modulo:** Expediente pantalla Catálogos - Dias festivos
- **Lo que pide la matriz:** Muestra la pantalla con el dia festivo, el botón editar datos y el botón "X"
- **Prueba:** `CatalogosConsultasPruebas#elDetalleDelCatalogoMuestraSusDatos`  (etiquetas: consultas)
- **Lo que valida el codigo:** Ver detalle del catalogo con sus campos, EDITAR DATOS y la X
- **Correr solo este caso:** `mvn test -Dtest='CatalogosConsultasPruebas#elDetalleDelCatalogoMuestraSusDatos'`
- **Renglon de la tabla para este caso:** `{"PF_CP_089", "Dias festivos", new String[] {"Dia festivo(dd/mm/yyyy)"}}`
- **Ojo:** este metodo cubre varios casos con la tabla `@DataProvider("detalles")`; el comando los corre todos y la consola imprime el ID de cada uno.
- **Pasos que ejecuta:** `abrirCatalogo` -> `abrirElDetalleDelPrimerRegistro` -> `elModalDebeTenerLosCampos` -> `losCamposDelDetalleDebenSerDeSoloLectura` -> `elModalDebeTenerElBoton` -> `elBotonCerrarDelModalDebeEstarVisible` -> `cerrarElModal`
- **Verificaciones:** 4

```java
        catalogos.abrirCatalogo(catalogo)
                .abrirElDetalleDelPrimerRegistro()
                .elModalDebeTenerLosCampos(campos)
                .losCamposDelDetalleDebenSerDeSoloLectura()
                .elModalDebeTenerElBoton("EDITAR DATOS")
                .elBotonCerrarDelModalDebeEstarVisible()
                .cerrarElModal();
```

## PF_CP_090 — Validación de la edición de alguna nacionalidad sobre el detalle del menú de catálogos - Dias festivos

- **Modulo:** Expediente pantalla Catálogos - Dias festivos
- **Lo que pide la matriz:** Muestra la tabla de Dias festivos con la información editada
- **Prueba:** `CatalogosAltasPruebas#diasFestivos`  (etiquetas: ola5, catalogos, escribe_datos)
- **Lo que valida el codigo:** Dias festivos: agregar, editar, inactivar y activar un elemento
- **Correr solo este caso:** `mvn test -Dtest='CatalogosAltasPruebas#diasFestivos'`
- **Pasos que ejecuta:** 
- **Verificaciones:** 0

```java
        cicloDelCatalogo(ElementoDeCatalogo.DIAS_FESTIVOS);
```

## PF_CP_091 — Validación de la funcinalidad del botón inactivar

- **Modulo:** Expediente pantalla Catálogos - Dias festivos
- **Lo que pide la matriz:** Muestra la tabla de Dias festivos con el dia festivo inactiva
- **Prueba:** `CatalogosAltasPruebas#diasFestivos`  (etiquetas: ola5, catalogos, escribe_datos)
- **Lo que valida el codigo:** Dias festivos: agregar, editar, inactivar y activar un elemento
- **Correr solo este caso:** `mvn test -Dtest='CatalogosAltasPruebas#diasFestivos'`
- **Pasos que ejecuta:** 
- **Verificaciones:** 0

```java
        cicloDelCatalogo(ElementoDeCatalogo.DIAS_FESTIVOS);
```

## PF_CP_092 — Validación de la funcinalidad del botón activar registro

- **Modulo:** Expediente pantalla Catálogos - Dias festivos
- **Lo que pide la matriz:** Muestra la tabla de Dias festivos con el Dia festivo activa
- **Prueba:** `CatalogosAltasPruebas#diasFestivos`  (etiquetas: ola5, catalogos, escribe_datos)
- **Lo que valida el codigo:** Dias festivos: agregar, editar, inactivar y activar un elemento
- **Correr solo este caso:** `mvn test -Dtest='CatalogosAltasPruebas#diasFestivos'`
- **Pasos que ejecuta:** 
- **Verificaciones:** 0

```java
        cicloDelCatalogo(ElementoDeCatalogo.DIAS_FESTIVOS);
```

## PF_CP_093 — Validación de la pantalla del menú Catálogos - Versiones

- **Modulo:** Expediente pantalla Catálogos - Versiones
- **Lo que pide la matriz:** Muestra el botón de Agregar elemento, y la tabla de Versiones con su: -Descripción, -Valor -estatus, -detalle -inactivar
- **Prueba:** `CatalogosConsultasPruebas#laPantallaDelCatalogoMuestraSuTabla`  (etiquetas: consultas)
- **Lo que valida el codigo:** Pantalla del catalogo con su boton Agregar elemento y su tabla
- **Correr solo este caso:** `mvn test -Dtest='CatalogosConsultasPruebas#laPantallaDelCatalogoMuestraSuTabla'`
- **Renglon de la tabla para este caso:** `{"PF_CP_093", "Versiones", new String[] {"Descripción", "Valor", "Estatus"}}`
- **Ojo:** este metodo cubre varios casos con la tabla `@DataProvider("pantallas")`; el comando los corre todos y la consola imprime el ID de cada uno.
- **Pasos que ejecuta:** `abrirCatalogo` -> `elBotonAgregarElementoDebeEstarVisible` -> `encabezadosDeLaTabla`
- **Verificaciones:** 2

```java
        catalogos.abrirCatalogo(catalogo).elBotonAgregarElementoDebeEstarVisible();

        List<String> actuales = catalogos.encabezadosDeLaTabla();
        for (String columna : columnas) {
            Assert.assertTrue(actuales.stream()
                            .anyMatch(actual -> actual.equalsIgnoreCase(columna)),
                    caso + ": la tabla de " + catalogo + " no muestra la columna \"" + columna
                            + "\". Muestra hoy: " + actuales + ".");
        }
```
- **Prueba:** `CatalogosPruebas#cadaCatalogoMuestraSuTabla`  (etiquetas: catalogos)
- **Lo que valida el codigo:** Cada catalogo muestra su tabla y su boton Agregar elemento
- **Correr solo este caso:** `mvn test -Dtest='CatalogosPruebas#cadaCatalogoMuestraSuTabla'`
- **Ojo:** este metodo cubre varios casos con la tabla `@DataProvider("catalogos")`; el comando los corre todos y la consola imprime el ID de cada uno.
- **Pasos que ejecuta:** `abrirCatalogo` -> `elBotonAgregarElementoDebeEstarVisible` -> `laPantallaDebeTenerUnaTablaConInformacion`
- **Verificaciones:** 2

```java
        new PaginaCatalogos()
                .abrirCatalogo(catalogo)
                .elBotonAgregarElementoDebeEstarVisible();
        inicio.laPantallaDebeTenerUnaTablaConInformacion();
```

## PF_CP_094 — Validación del botón agregar elemento del menú de catálogos - Versiones

- **Modulo:** Expediente pantalla Catálogos - Versiones
- **Lo que pide la matriz:** Muestra la Version agregada en la tabla de Versiones
- **Prueba:** `CatalogosAltasPruebas#versiones`  (etiquetas: ola5, catalogos, escribe_datos)
- **Lo que valida el codigo:** Versiones: agregar, editar, inactivar y activar un elemento
- **Correr solo este caso:** `mvn test -Dtest='CatalogosAltasPruebas#versiones'`
- **Pasos que ejecuta:** 
- **Verificaciones:** 0

```java
        cicloDelCatalogo("Versiones");
```

## PF_CP_095 — Validar Numero de caracteres campo Fecha

- **Modulo:** Expediente pantalla Catálogos - Versiones
- **Lo que pide la matriz:** Permite solo 8 caracteres Tipo numerico
- **Prueba:** `CatalogosConsultasPruebas#pfCp095CampoFechaDeVersiones`  (etiquetas: consultas, regla_por_confirmar)
- **Lo que valida el codigo:** Versiones tiene un campo Fecha de 8 digitos
- **Correr solo este caso:** `mvn test -Dtest='CatalogosConsultasPruebas#pfCp095CampoFechaDeVersiones'`
- **Pasos que ejecuta:** `abrirCatalogo` -> `abrirElAltaDeElemento` -> `tieneElCampo` -> `cerrarElModal`
- **Verificaciones:** 1

```java
        catalogos.abrirCatalogo("Versiones").abrirElAltaDeElemento();
        boolean tieneFecha = catalogos.tieneElCampo("Dia festivo(dd/mm/yyyy)")
                || catalogos.tieneElCampo("Fecha");
        catalogos.cerrarElModal();

        Assert.assertTrue(tieneFecha,
                "El alta de Versiones no tiene campo Fecha: muestra Descripción y Valor. "
                        + "PF_CP_095 y PF_CP_096 parecen copiados de Dias festivos; por "
                        + "confirmar con negocio si la matriz debe corregirse.");
```

## PF_CP_096 — Validar Botón calendario

- **Modulo:** Expediente pantalla Catálogos - Versiones
- **Lo que pide la matriz:** Validar que permita seleccionar una fecha del calendario Solo estarán habilitadas las fechas desde hoy en adelante.
- **Prueba:** `CatalogosConsultasPruebas#pfCp096CalendarioDeVersiones`  (etiquetas: consultas, regla_por_confirmar)
- **Lo que valida el codigo:** Versiones tiene boton de calendario
- **Correr solo este caso:** `mvn test -Dtest='CatalogosConsultasPruebas#pfCp096CalendarioDeVersiones'`
- **Pasos que ejecuta:** `abrirCatalogo` -> `abrirElAltaDeElemento` -> `tieneCalendario` -> `cerrarElModal`
- **Verificaciones:** 1

```java
        catalogos.abrirCatalogo("Versiones").abrirElAltaDeElemento();
        boolean tieneCalendario = catalogos.tieneCalendario();
        catalogos.cerrarElModal();

        Assert.assertTrue(tieneCalendario,
                "El alta de Versiones no tiene calendario: muestra Descripción y Valor. "
                        + "Mismo pendiente que PF_CP_095.");
```

## PF_CP_097 — Validación del botón ver detalle del menú de catálogos - Versiones

- **Modulo:** Expediente pantalla Catálogos - Versiones
- **Lo que pide la matriz:** Muestra la pantalla con la descripción, valor, el botón editar datos y el botón "X"
- **Prueba:** `CatalogosConsultasPruebas#elDetalleDelCatalogoMuestraSusDatos`  (etiquetas: consultas)
- **Lo que valida el codigo:** Ver detalle del catalogo con sus campos, EDITAR DATOS y la X
- **Correr solo este caso:** `mvn test -Dtest='CatalogosConsultasPruebas#elDetalleDelCatalogoMuestraSusDatos'`
- **Renglon de la tabla para este caso:** `{"PF_CP_097", "Versiones", new String[] {"Descripción", "Valor"}}`
- **Ojo:** este metodo cubre varios casos con la tabla `@DataProvider("detalles")`; el comando los corre todos y la consola imprime el ID de cada uno.
- **Pasos que ejecuta:** `abrirCatalogo` -> `abrirElDetalleDelPrimerRegistro` -> `elModalDebeTenerLosCampos` -> `losCamposDelDetalleDebenSerDeSoloLectura` -> `elModalDebeTenerElBoton` -> `elBotonCerrarDelModalDebeEstarVisible` -> `cerrarElModal`
- **Verificaciones:** 4

```java
        catalogos.abrirCatalogo(catalogo)
                .abrirElDetalleDelPrimerRegistro()
                .elModalDebeTenerLosCampos(campos)
                .losCamposDelDetalleDebenSerDeSoloLectura()
                .elModalDebeTenerElBoton("EDITAR DATOS")
                .elBotonCerrarDelModalDebeEstarVisible()
                .cerrarElModal();
```

## PF_CP_098 — Validación de la edición de alguna nacionalidad sobre el detalle del menú de catálogos - Versiones

- **Modulo:** Expediente pantalla Catálogos - Versiones
- **Lo que pide la matriz:** Muestra la tabla de Versiones con la información editada
- **Prueba:** `CatalogosAltasPruebas#versiones`  (etiquetas: ola5, catalogos, escribe_datos)
- **Lo que valida el codigo:** Versiones: agregar, editar, inactivar y activar un elemento
- **Correr solo este caso:** `mvn test -Dtest='CatalogosAltasPruebas#versiones'`
- **Pasos que ejecuta:** 
- **Verificaciones:** 0

```java
        cicloDelCatalogo("Versiones");
```

## PF_CP_099 — Validación de la funcinalidad del botón inactivar

- **Modulo:** Expediente pantalla Catálogos - Versiones
- **Lo que pide la matriz:** Muestra la tabla de Versiones con la Version inactiva
- **Prueba:** `CatalogosAltasPruebas#versiones`  (etiquetas: ola5, catalogos, escribe_datos)
- **Lo que valida el codigo:** Versiones: agregar, editar, inactivar y activar un elemento
- **Correr solo este caso:** `mvn test -Dtest='CatalogosAltasPruebas#versiones'`
- **Pasos que ejecuta:** 
- **Verificaciones:** 0

```java
        cicloDelCatalogo("Versiones");
```

## PF_CP_100 — Validación de la funcinalidad del botón activar registro

- **Modulo:** Expediente pantalla Catálogos - Versiones
- **Lo que pide la matriz:** Muestra la tabla de Versiones con la Version activa
- **Prueba:** `CatalogosAltasPruebas#versiones`  (etiquetas: ola5, catalogos, escribe_datos)
- **Lo que valida el codigo:** Versiones: agregar, editar, inactivar y activar un elemento
- **Correr solo este caso:** `mvn test -Dtest='CatalogosAltasPruebas#versiones'`
- **Pasos que ejecuta:** 
- **Verificaciones:** 0

```java
        cicloDelCatalogo("Versiones");
```

## PF_CP_101 — Validación de la pantalla del menú Tasas de interés

- **Modulo:** Expediente pantalla Tasas e interés
- **Lo que pide la matriz:** Muestra el título de Tasas de interés con una lista desplegable en el cual contiene: tasas de interés y costo financiero total
- **Prueba:** `NavegacionPruebas#pfCp101PantallaTasas`  (etiquetas: navegacion)
- **Lo que valida el codigo:** Pantalla de Tasas de interes
- **Correr solo este caso:** `mvn test -Dtest='NavegacionPruebas#pfCp101PantallaTasas'`
- **Pasos que ejecuta:** `irAlMenu` -> `laDireccionDebeContener`
- **Verificaciones:** 1

```java
        inicio.irAlMenu("Tasas de interes").laDireccionDebeContener("expedient/rates");
```

## PF_CP_102 — Validación de la funcionalidad de la opción de tasas de interés sobre el menú de tasas de interés

- **Modulo:** Expediente pantalla Tasas e interés
- **Lo que pide la matriz:** Muestra 2 tablas de tasas de intereses, los financieros y los punitorios
- **Prueba:** `TasasCostosYReportesPruebas#pfCp102ConsultaDeTasasDeInteres`  (etiquetas: consultas)
- **Lo que valida el codigo:** Tasas de interes muestra las tablas del periodo elegido
- **Correr solo este caso:** `mvn test -Dtest='TasasCostosYReportesPruebas#pfCp102ConsultaDeTasasDeInteres'`
- **Pasos que ejecuta:** `irAlMenu` -> `abrir` -> `elegirTipoDeTasa` -> `elegirElPrimerPeriodo` -> `debeVerseElTexto` -> `elCampoDebeEstarVisible`
- **Verificaciones:** 3

```java
        inicio.irAlMenu("Tasas de interes");

        new PaginaTasas().abrir()
                .elegirTipoDeTasa("Tasas de interés")
                .elegirElPrimerPeriodo()
                .debeVerseElTexto("Tasas de intereses financieros")
                .debeVerseElTexto("Tasas de intereses punitorios")
                .elCampoDebeEstarVisible(Selectores.TASAS_CAMPO_TEM_PESOS);
```

## PF_CP_103 — Validar que se pueda editar el porcentaje de interes sobre la pantalla y opción de tasas de interés

- **Modulo:** Expediente pantalla Tasas e interés
- **Lo que pide la matriz:** Muestra 2 tablas de tasas de intereses, los financieros y los punitorios actualizada
- **Automatizado:** no. Modifica tasas vigentes: falta un periodo de prueba acordado.

## PF_CP_104 — Validar caracteres campos tasa de interes

- **Modulo:** Expediente pantalla Tasas e interés
- **Lo que pide la matriz:** Permite solo 9 caracteres Tipo numerico separado con un punto cada 3 numeros
- **Prueba:** `TasasCostosYReportesPruebas#pfCp104CaracteresDelPorcentajeDeLaTasa`  (etiquetas: consultas)
- **Lo que valida el codigo:** El porcentaje de la tasa solo acepta numeros
- **Correr solo este caso:** `mvn test -Dtest='TasasCostosYReportesPruebas#pfCp104CaracteresDelPorcentajeDeLaTasa'`
- **Pasos que ejecuta:** `irAlMenu` -> `abrir` -> `elegirTipoDeTasa` -> `elegirElPrimerPeriodo` -> `loQueAcepta` -> `replaceAll`
- **Verificaciones:** 1

```java
        inicio.irAlMenu("Tasas de interes");

        PaginaTasas tasas = new PaginaTasas().abrir()
                .elegirTipoDeTasa("Tasas de interés")
                .elegirElPrimerPeriodo();

        String conLetras = tasas.loQueAcepta(Selectores.TASAS_CAMPO_TEM_PESOS, "abc");
        Assert.assertTrue(conLetras.replaceAll("[\\d.,]", "").isEmpty(),
                "El campo de porcentaje acepto letras: \"" + conLetras + "\".");
```

## PF_CP_105 — Validación de la funcionalidad de la opción de Costo Financiero Total sobre el menú de tasas de interés

- **Modulo:** Expediente pantalla Tasas e interés
- **Lo que pide la matriz:** Muestra la tabal de costo financiero total con CFT2, CFT(Pesos) y CFT(Dolares)
- **Prueba:** `TasasCostosYReportesPruebas#pfCp105ConsultaDeCostoFinancieroTotal`  (etiquetas: consultas)
- **Lo que valida el codigo:** Costo Financiero Total muestra su tabla
- **Correr solo este caso:** `mvn test -Dtest='TasasCostosYReportesPruebas#pfCp105ConsultaDeCostoFinancieroTotal'`
- **Pasos que ejecuta:** `irAlMenu` -> `abrir` -> `elegirTipoDeTasa` -> `elegirElPrimerPeriodo` -> `elCampoDebeEstarVisible`
- **Verificaciones:** 1

```java
        inicio.irAlMenu("Tasas de interes");

        new PaginaTasas().abrir()
                .elegirTipoDeTasa("Costo Financiero Total")
                .elegirElPrimerPeriodo()
                .elCampoDebeEstarVisible(Selectores.TASAS_CAMPO_CFT_PESOS);
```

## PF_CP_106 — Validar que se pueda agregar el porcentaje para costo financiero total

- **Modulo:** Expediente pantalla Tasas e interés
- **Lo que pide la matriz:** Muestra la pantalla de información agregada
- **Automatizado:** no. Modifica tasas vigentes: falta un periodo de prueba acordado.

## PF_CP_107 — Validar caracteres campos costo financiero total

- **Modulo:** Expediente pantalla Tasas e interés
- **Lo que pide la matriz:** Permite solo 9 caracteres Tipo numerico separado con un punto cada 3 numeros
- **Prueba:** `TasasCftPruebas#pfCp107CaracteresDelCostoFinancieroTotal`  (etiquetas: ola5, tasas)
- **Lo que valida el codigo:** Los campos de Costo Financiero Total solo aceptan numeros y hasta 9 caracteres
- **Correr solo este caso:** `mvn test -Dtest='TasasCftPruebas#pfCp107CaracteresDelCostoFinancieroTotal'`
- **Pasos que ejecuta:** `irAlMenu` -> `abrir` -> `elegirTipoDeTasa` -> `elegirElPrimerPeriodo` -> `loQueAcepta` -> `replaceAll` -> `cuantosDigitosAcepta`
- **Verificaciones:** 2

```java
        inicio.irAlMenu("Tasas de interes");

        PaginaTasas tasas = new PaginaTasas().abrir()
                .elegirTipoDeTasa("Costo Financiero Total")
                .elegirElPrimerPeriodo();

        String conLetras = tasas.loQueAcepta(Selectores.TASAS_CAMPO_CFT_PESOS, "abc");
        Assert.assertTrue(conLetras.replaceAll("[\\d.,]", "").isEmpty(),
                "El campo de costo financiero total acepto letras: \"" + conLetras + "\".");

        int digitos = tasas.cuantosDigitosAcepta(
                Selectores.TASAS_CAMPO_CFT_PESOS, CARACTERES_ESPERADOS);
        Assert.assertTrue(digitos <= CARACTERES_ESPERADOS,
                "El campo de costo financiero total acepto " + digitos + " digitos y la matriz "
                        + "permite " + CARACTERES_ESPERADOS + ".");
```

## PF_CP_108 — Validación de la pantalla del menú Expediente

- **Modulo:** Solicitudes pantalla Expediente
- **Lo que pide la matriz:** Muestra el título de Solicitudes con un botón de crear solicitud, importar,exportar y filtrar. Adicional muestra la tabla de las solicitudes existentes
- **Prueba:** `NavegacionPruebas#pfCp108PantallaSolicitudes`  (etiquetas: navegacion, humo)
- **Lo que valida el codigo:** Pantalla de Solicitudes con sus botones
- **Correr solo este caso:** `mvn test -Dtest='NavegacionPruebas#pfCp108PantallaSolicitudes'`
- **Pasos que ejecuta:** `irAlMenu` -> `laDireccionDebeContener`
- **Verificaciones:** 1

```java
        inicio.irAlMenu("Expediente").laDireccionDebeContener("expedient/request");
```

## PF_CP_109 — Validacion de la tabla de solicitudes

- **Modulo:** Solicitudes pantalla Expediente
- **Lo que pide la matriz:** Muestra la tabla con las solicitudes realizadas, debera tener la siguiente informacion: -Referencia -Nombre -Apellido(s) -DNI -Producto -Fecha creacion -Fecha modificacion -Campaña -Creado por -Estatus -Ver detalle -Zip -Doc.Griffin -Seleccionar todo
- **Prueba:** `ExpedienteConsultasPruebas#pfCp109ColumnasDeLaTablaDeSolicitudes`  (etiquetas: consultas, humo)
- **Lo que valida el codigo:** La tabla de solicitudes muestra todas sus columnas
- **Correr solo este caso:** `mvn test -Dtest='ExpedienteConsultasPruebas#pfCp109ColumnasDeLaTablaDeSolicitudes'`
- **Pasos que ejecuta:** `encabezadosDeLaTabla` -> `cuantasSolicitudesMuestraLaTabla`
- **Verificaciones:** 2

```java
        List<String> esperadas = List.of("Referencia", "Nombre", "Apellido(s)", "DNI", "Producto",
                "Fecha creación", "Fecha modificación", "Campaña", "Creada por", "Estatus");

        List<String> actuales = expediente.encabezadosDeLaTabla();
        for (String columna : esperadas) {
            Assert.assertTrue(actuales.stream()
                            .anyMatch(actual -> actual.equalsIgnoreCase(columna)),
                    "La tabla de solicitudes no muestra la columna \"" + columna
                            + "\". Muestra hoy: " + actuales + ".");
        }
        Assert.assertTrue(expediente.cuantasSolicitudesMuestraLaTabla() > 0,
                "La tabla de solicitudes no muestra ninguna solicitud.");
```

## PF_CP_110 — Validación de la funcionalidad del botón crear solicitud

- **Modulo:** Solicitudes pantalla Expediente
- **Lo que pide la matriz:** Muestra una alerta indicando Solicitud creada exitosamente y muestra la solicitud en la tabala de solicitudes
- **Automatizado:** no. Crea una solicitud real: requiere ambiente o datos desechables.

## PF_CP_111 — Validar Campo Nombres

- **Modulo:** Solicitudes pantalla Expediente
- **Lo que pide la matriz:** Validar que solo permita 32 caracteres Permite solo caracteres alfabeticos
- **Prueba:** `ValidacionesDeCamposPruebas#elCampoDebePermitirElMaximo`  (etiquetas: validaciones)
- **Lo que valida el codigo:** Maximo de caracteres por campo
- **Correr solo este caso:** `mvn test -Dtest='ValidacionesDeCamposPruebas#elCampoDebePermitirElMaximo'`
- **Renglon de la tabla para este caso:** `{"PF_CP_111 Nombre", Selectores.SOLICITUDES_CAMPO_NOMBRE, 32, "letras"}`
- **Ojo:** este metodo cubre varios casos con la tabla `@DataProvider("camposConMaximo")`; el comando los corre todos y la consola imprime el ID de cada uno.
- **Pasos que ejecuta:** `cuantosCaracteresAcepta`
- **Verificaciones:** 1

```java
        int aceptados = solicitudes.cuantosCaracteresAcepta(campo, maximo, tipo);
        Assert.assertEquals(aceptados, maximo,
                caso + ": el campo acepto " + aceptados + " caracteres y el maximo esperado es "
                        + maximo + ".");
```
- **Prueba:** `ValidacionesDeCamposPruebas#elCampoSoloDebePermitirSuTipoDeCaracter`  (etiquetas: validaciones)
- **Lo que valida el codigo:** Tipo de caracteres permitidos por campo
- **Correr solo este caso:** `mvn test -Dtest='ValidacionesDeCamposPruebas#elCampoSoloDebePermitirSuTipoDeCaracter'`
- **Renglon de la tabla para este caso:** `{"PF_CP_111 Nombre solo alfabetico"`
- **Ojo:** este metodo cubre varios casos con la tabla `@DataProvider("camposQueFiltranCaracteres")`; el comando los corre todos y la consola imprime el ID de cada uno.
- **Pasos que ejecuta:** `loQueAcepta`
- **Verificaciones:** 1

```java
        String quedo = solicitudes.loQueAcepta(campo, seEscribe);
        Assert.assertEquals(quedo, debeQuedar,
                caso + ": se escribio \"" + seEscribe + "\" y el campo dejo \"" + quedo + "\".");
```

## PF_CP_112 — Validar Campo DNI

- **Modulo:** Solicitudes pantalla Expediente
- **Lo que pide la matriz:** Validar que solo permita 8 caracteres Permite solo caracteres numericos
- **Prueba:** `ValidacionesDeCamposPruebas#elCampoDebePermitirElMaximo`  (etiquetas: validaciones)
- **Lo que valida el codigo:** Maximo de caracteres por campo
- **Correr solo este caso:** `mvn test -Dtest='ValidacionesDeCamposPruebas#elCampoDebePermitirElMaximo'`
- **Renglon de la tabla para este caso:** `{"PF_CP_112 DNI", Selectores.SOLICITUDES_CAMPO_DNI, 8, "numeros"}`
- **Ojo:** este metodo cubre varios casos con la tabla `@DataProvider("camposConMaximo")`; el comando los corre todos y la consola imprime el ID de cada uno.
- **Pasos que ejecuta:** `cuantosCaracteresAcepta`
- **Verificaciones:** 1

```java
        int aceptados = solicitudes.cuantosCaracteresAcepta(campo, maximo, tipo);
        Assert.assertEquals(aceptados, maximo,
                caso + ": el campo acepto " + aceptados + " caracteres y el maximo esperado es "
                        + maximo + ".");
```
- **Prueba:** `ValidacionesDeCamposPruebas#elCampoSoloDebePermitirSuTipoDeCaracter`  (etiquetas: validaciones)
- **Lo que valida el codigo:** Tipo de caracteres permitidos por campo
- **Correr solo este caso:** `mvn test -Dtest='ValidacionesDeCamposPruebas#elCampoSoloDebePermitirSuTipoDeCaracter'`
- **Renglon de la tabla para este caso:** `{"PF_CP_112 DNI solo numerico"`
- **Ojo:** este metodo cubre varios casos con la tabla `@DataProvider("camposQueFiltranCaracteres")`; el comando los corre todos y la consola imprime el ID de cada uno.
- **Pasos que ejecuta:** `loQueAcepta`
- **Verificaciones:** 1

```java
        String quedo = solicitudes.loQueAcepta(campo, seEscribe);
        Assert.assertEquals(quedo, debeQuedar,
                caso + ": se escribio \"" + seEscribe + "\" y el campo dejo \"" + quedo + "\".");
```

## PF_CP_113 — Validar Campo Apellidos

- **Modulo:** Solicitudes pantalla Expediente
- **Lo que pide la matriz:** Validar que solo permita 32 caracteres Permite solo caracteres Alfabeticos
- **Prueba:** `ValidacionesDeCamposPruebas#elCampoDebePermitirElMaximo`  (etiquetas: validaciones)
- **Lo que valida el codigo:** Maximo de caracteres por campo
- **Correr solo este caso:** `mvn test -Dtest='ValidacionesDeCamposPruebas#elCampoDebePermitirElMaximo'`
- **Renglon de la tabla para este caso:** `{"PF_CP_113 Apellidos", Selectores.SOLICITUDES_CAMPO_APELLIDOS, 32, "letras"}`
- **Ojo:** este metodo cubre varios casos con la tabla `@DataProvider("camposConMaximo")`; el comando los corre todos y la consola imprime el ID de cada uno.
- **Pasos que ejecuta:** `cuantosCaracteresAcepta`
- **Verificaciones:** 1

```java
        int aceptados = solicitudes.cuantosCaracteresAcepta(campo, maximo, tipo);
        Assert.assertEquals(aceptados, maximo,
                caso + ": el campo acepto " + aceptados + " caracteres y el maximo esperado es "
                        + maximo + ".");
```
- **Prueba:** `ValidacionesDeCamposPruebas#elCampoSoloDebePermitirSuTipoDeCaracter`  (etiquetas: validaciones)
- **Lo que valida el codigo:** Tipo de caracteres permitidos por campo
- **Correr solo este caso:** `mvn test -Dtest='ValidacionesDeCamposPruebas#elCampoSoloDebePermitirSuTipoDeCaracter'`
- **Renglon de la tabla para este caso:** `{"PF_CP_113 Apellidos solo alfabetico"`
- **Ojo:** este metodo cubre varios casos con la tabla `@DataProvider("camposQueFiltranCaracteres")`; el comando los corre todos y la consola imprime el ID de cada uno.
- **Pasos que ejecuta:** `loQueAcepta`
- **Verificaciones:** 1

```java
        String quedo = solicitudes.loQueAcepta(campo, seEscribe);
        Assert.assertEquals(quedo, debeQuedar,
                caso + ": se escribio \"" + seEscribe + "\" y el campo dejo \"" + quedo + "\".");
```

## PF_CP_114 — Validar Campo CUIL

- **Modulo:** Solicitudes pantalla Expediente
- **Lo que pide la matriz:** Validar que solo permita 11 caracteres Permite solo caracteres Alfabeticos
- **Prueba:** `ValidacionesDeCamposPruebas#pfCp114CampoCuil`  (etiquetas: validaciones)
- **Lo que valida el codigo:** Campo CUIL 11 digitos
- **Correr solo este caso:** `mvn test -Dtest='ValidacionesDeCamposPruebas#pfCp114CampoCuil'`
- **Pasos que ejecuta:** `cuantosDigitosAcepta` -> `valorDelCampo`
- **Verificaciones:** 1

```java
        int digitos = solicitudes.cuantosDigitosAcepta(Selectores.SOLICITUDES_CAMPO_CUIL, 11);
        Assert.assertEquals(digitos, 11,
                "El campo acepto " + digitos + " digitos (se ve como \""
                        + solicitudes.valorDelCampo(Selectores.SOLICITUDES_CAMPO_CUIL)
                        + "\") y el maximo esperado es 11.");
```

## PF_CP_115 — Validar Campo Fecha de Nacimiento

- **Modulo:** Solicitudes pantalla Expediente
- **Lo que pide la matriz:** Validar que solo permita 8 caracteres Permite solo caracteres Numericos
- **Prueba:** `ValidacionesDeCamposPruebas#elCampoSoloDebePermitirSuTipoDeCaracter`  (etiquetas: validaciones)
- **Lo que valida el codigo:** Tipo de caracteres permitidos por campo
- **Correr solo este caso:** `mvn test -Dtest='ValidacionesDeCamposPruebas#elCampoSoloDebePermitirSuTipoDeCaracter'`
- **Renglon de la tabla para este caso:** `{"PF_CP_115 Fecha de nacimiento solo numerica"`
- **Ojo:** este metodo cubre varios casos con la tabla `@DataProvider("camposQueFiltranCaracteres")`; el comando los corre todos y la consola imprime el ID de cada uno.
- **Pasos que ejecuta:** `loQueAcepta`
- **Verificaciones:** 1

```java
        String quedo = solicitudes.loQueAcepta(campo, seEscribe);
        Assert.assertEquals(quedo, debeQuedar,
                caso + ": se escribio \"" + seEscribe + "\" y el campo dejo \"" + quedo + "\".");
```
- **Prueba:** `ValidacionesDeCamposPruebas#pfCp115CampoFechaDeNacimiento`  (etiquetas: validaciones)
- **Lo que valida el codigo:** Fecha de nacimiento con formato DD/MM/AAAA y calendario
- **Correr solo este caso:** `mvn test -Dtest='ValidacionesDeCamposPruebas#pfCp115CampoFechaDeNacimiento'`
- **Pasos que ejecuta:** `loQueAcepta` -> `hayCalendarioDeFechaDeNacimiento`
- **Verificaciones:** 2

```java
        String quedo = solicitudes.loQueAcepta(
                Selectores.SOLICITUDES_CAMPO_FECHA_NACIMIENTO, "31/12/1990");
        Assert.assertEquals(quedo, "31/12/1990",
                "El campo no acepto una fecha valida de 8 digitos: dejo \"" + quedo + "\".");
        Assert.assertTrue(solicitudes.hayCalendarioDeFechaDeNacimiento(),
                "No se mostro el calendario del campo Fecha de nacimiento.");
```

## PF_CP_116 — Validar Campo Fecha de Nacimiento

- **Modulo:** Solicitudes pantalla Expediente
- **Lo que pide la matriz:** Validar que solo permita 8 caracteres Permite solo caracteres Numericos
- **Prueba:** `ValidacionesDeCamposPruebas#pfCp115CampoFechaDeNacimiento`  (etiquetas: validaciones)
- **Lo que valida el codigo:** Fecha de nacimiento con formato DD/MM/AAAA y calendario
- **Correr solo este caso:** `mvn test -Dtest='ValidacionesDeCamposPruebas#pfCp115CampoFechaDeNacimiento'`
- **Pasos que ejecuta:** `loQueAcepta` -> `hayCalendarioDeFechaDeNacimiento`
- **Verificaciones:** 2

```java
        String quedo = solicitudes.loQueAcepta(
                Selectores.SOLICITUDES_CAMPO_FECHA_NACIMIENTO, "31/12/1990");
        Assert.assertEquals(quedo, "31/12/1990",
                "El campo no acepto una fecha valida de 8 digitos: dejo \"" + quedo + "\".");
        Assert.assertTrue(solicitudes.hayCalendarioDeFechaDeNacimiento(),
                "No se mostro el calendario del campo Fecha de nacimiento.");
```

## PF_CP_117 — Validar Campo Dirección

- **Modulo:** Solicitudes pantalla Expediente
- **Lo que pide la matriz:** Validar que permita caracteres alfanuméricos y un máximo de 100 caracteres
- **Prueba:** `ValidacionesDeCamposPruebas#elCampoDebePermitirElMaximo`  (etiquetas: validaciones)
- **Lo que valida el codigo:** Maximo de caracteres por campo
- **Correr solo este caso:** `mvn test -Dtest='ValidacionesDeCamposPruebas#elCampoDebePermitirElMaximo'`
- **Renglon de la tabla para este caso:** `{"PF_CP_117 Direccion", Selectores.SOLICITUDES_CAMPO_DIRECCION, 100, "letras"}`
- **Ojo:** este metodo cubre varios casos con la tabla `@DataProvider("camposConMaximo")`; el comando los corre todos y la consola imprime el ID de cada uno.
- **Pasos que ejecuta:** `cuantosCaracteresAcepta`
- **Verificaciones:** 1

```java
        int aceptados = solicitudes.cuantosCaracteresAcepta(campo, maximo, tipo);
        Assert.assertEquals(aceptados, maximo,
                caso + ": el campo acepto " + aceptados + " caracteres y el maximo esperado es "
                        + maximo + ".");
```
- **Prueba:** `ValidacionesDeCamposPruebas#elCampoSoloDebePermitirSuTipoDeCaracter`  (etiquetas: validaciones)
- **Lo que valida el codigo:** Tipo de caracteres permitidos por campo
- **Correr solo este caso:** `mvn test -Dtest='ValidacionesDeCamposPruebas#elCampoSoloDebePermitirSuTipoDeCaracter'`
- **Renglon de la tabla para este caso:** `{"PF_CP_117 Direccion alfanumerica"`
- **Ojo:** este metodo cubre varios casos con la tabla `@DataProvider("camposQueFiltranCaracteres")`; el comando los corre todos y la consola imprime el ID de cada uno.
- **Pasos que ejecuta:** `loQueAcepta`
- **Verificaciones:** 1

```java
        String quedo = solicitudes.loQueAcepta(campo, seEscribe);
        Assert.assertEquals(quedo, debeQuedar,
                caso + ": se escribio \"" + seEscribe + "\" y el campo dejo \"" + quedo + "\".");
```

## PF_CP_118 — Validar opción Adicionales PEP

- **Modulo:** Solicitudes pantalla Expediente
- **Lo que pide la matriz:** Validar que en esta sección se muestren dos opciones: No adicionales y con adicionales.
- **Prueba:** `ValidacionesDeCamposPruebas#pfCp118OpcionesDeAdicionalesPep`  (etiquetas: validaciones)
- **Lo que valida el codigo:** Adicionales PEP muestra dos opciones y ninguna preseleccionada
- **Correr solo este caso:** `mvn test -Dtest='ValidacionesDeCamposPruebas#pfCp118OpcionesDeAdicionalesPep'`
- **Pasos que ejecuta:** `opcionesPep` -> `hayAlgunaOpcionPepMarcada`
- **Verificaciones:** 2

```java
        List<String> opciones = solicitudes.opcionesPep();
        Assert.assertEquals(opciones.size(), 2,
                "La seccion Adicionales PEP debe mostrar dos opciones y muestra: " + opciones);
        Assert.assertFalse(solicitudes.hayAlgunaOpcionPepMarcada(),
                "Ninguna opcion de Adicionales PEP debe venir seleccionada.");
```

## PF_CP_119 — Adicionales

- **Modulo:** Solicitudes pantalla Expediente
- **Lo que pide la matriz:** Validar que al dar ap en adicionales se abre una nueva sección para registrar los datos de cada adicional con el botón Agregar PEP
- **Prueba:** `ValidacionesDeCamposPruebas#pfCp119SeccionDeAdicionales`  (etiquetas: validaciones)
- **Lo que valida el codigo:** Con adicionales habilita la seccion para agregar un PEP
- **Correr solo este caso:** `mvn test -Dtest='ValidacionesDeCamposPruebas#pfCp119SeccionDeAdicionales'`
- **Pasos que ejecuta:** `elegirLaOpcionPep` -> `elBotonDebeEstarVisible`
- **Verificaciones:** 1

```java
        solicitudes.elegirLaOpcionPep(2);
        new PaginaPrincipal().elBotonDebeEstarVisible("AGREGAR ADICIONAL");
```

## PF_CP_120 — Agregar adicional PEP

- **Modulo:** Solicitudes pantalla Expediente
- **Lo que pide la matriz:** El sistema abre un modal para registrar Nombre, Apellido, DNI, Cargo y Relación, todos son obligatorios para poder habilitar el botón Continuar
- **Prueba:** `ValidacionesDeCamposPruebas#pfCp120ModalDeAdicionalPep`  (etiquetas: validaciones)
- **Lo que valida el codigo:** El modal de PEP pide sus cinco datos y son obligatorios
- **Correr solo este caso:** `mvn test -Dtest='ValidacionesDeCamposPruebas#pfCp120ModalDeAdicionalPep'`
- **Pasos que ejecuta:** `elegirLaOpcionPep` -> `abrirElModalDePep` -> `elBotonEstaDeshabilitado` -> `loQueAcepta` -> `cerrarElModal`
- **Verificaciones:** 2

```java
        solicitudes.elegirLaOpcionPep(2).abrirElModalDePep();
        By aceptar = Selectores.botonDelModal("ACEPTAR");
        Assert.assertTrue(solicitudes.elBotonEstaDeshabilitado(aceptar),
                "El boton ACEPTAR debe estar deshabilitado con el modal vacio.");

        solicitudes.loQueAcepta(Selectores.campoDelModal("name"), "Ana");
        solicitudes.loQueAcepta(Selectores.campoDelModal("lastName"), "Gomez");
        solicitudes.loQueAcepta(Selectores.campoDelModal("dni"), "12345678");
        solicitudes.loQueAcepta(Selectores.campoDelModal("position"), "Directora");
        solicitudes.loQueAcepta(Selectores.campoDelModal("relationship"), "Conyuge");

        Assert.assertFalse(solicitudes.elBotonEstaDeshabilitado(aceptar),
                "Con los cinco datos completos el boton ACEPTAR debe habilitarse.");
        // Se cancela: la solicitud nunca se crea, no se guarda informacion.
        solicitudes.cerrarElModal();
```

## PF_CP_121 — Editar/Eliminar PEP

- **Modulo:** Solicitudes pantalla Expediente
- **Lo que pide la matriz:** El sistema muestra dos íconos en el registro del PEP para poder editar o eliminar este.
- **Prueba:** `SolicitudesPepPruebas#pfCp121EditarYEliminarAdicionalPep`  (etiquetas: ola6, solicitudes)
- **Lo que valida el codigo:** El adicional PEP se puede editar y eliminar
- **Correr solo este caso:** `mvn test -Dtest='SolicitudesPepPruebas#pfCp121EditarYEliminarAdicionalPep'`
- **Pasos que ejecuta:** `elegirLaOpcionPep` -> `registrarUnAdicionalPep` -> `cuantosAdicionalesPepHay` -> `tablaDeAdicionalesPep` -> `elAdicionalPepTieneSusIconos` -> `abrirLaEdicionDelAdicionalPep` -> `cerrarElModal` -> `eliminarElAdicionalPep`
- **Verificaciones:** 4

```java
        String prefijo = Configuracion.obtener("amex.datos.prefijo");

        solicitudes.elegirLaOpcionPep(2)
                .registrarUnAdicionalPep(prefijo, prefijo + " APELLIDO", "12345678",
                        "Cargo de prueba", "Relacion de prueba");

        Assert.assertEquals(solicitudes.cuantosAdicionalesPepHay(), 1,
                "La tabla de adicionales PEP no muestra el adicional registrado. Muestra: \""
                        + solicitudes.tablaDeAdicionalesPep() + "\".");
        Assert.assertTrue(solicitudes.elAdicionalPepTieneSusIconos(),
                "La fila del adicional PEP no muestra los dos iconos (editar y eliminar).");

        Assert.assertEquals(solicitudes.abrirLaEdicionDelAdicionalPep(), prefijo,
                "El icono de editar no abre el adicional PEP con sus datos.");
        solicitudes.cerrarElModal().eliminarElAdicionalPep();

        Assert.assertEquals(solicitudes.cuantosAdicionalesPepHay(), 0,
                "El icono de eliminar no quito el adicional PEP. La tabla muestra: \""
                        + solicitudes.tablaDeAdicionalesPep() + "\".");
```

## PF_CP_122 — Validar Check Condicionada a ingresos

- **Modulo:** Solicitudes pantalla Expediente
- **Lo que pide la matriz:** El check por defaul está sin seleccionar y así se crean solicitudes con estatus Creado
- **Prueba:** `SolicitudesPepPruebas#pfCp122CheckCondicionadaAIngresos`  (etiquetas: ola6, solicitudes, regla_por_confirmar)
- **Lo que valida el codigo:** El alta de solicitud ofrece el check Condicionada a ingresos
- **Correr solo este caso:** `mvn test -Dtest='SolicitudesPepPruebas#pfCp122CheckCondicionadaAIngresos'`
- **Pasos que ejecuta:** `hayCheckCondicionadaAIngresos` -> `cuantosChecksTieneElFormulario`
- **Verificaciones:** 1

```java
        Assert.assertTrue(solicitudes.hayCheckCondicionadaAIngresos(),
                "El formulario de alta de solicitud no tiene el check \"Condicionada a ingresos\" "
                        + "que pide la matriz: la pantalla no muestra ningun check (encontrados: "
                        + solicitudes.cuantosChecksTieneElFormulario() + "). Sin ese control "
                        + "tampoco se puede automatizar PF_CP_123.");
```

## PF_CP_123 — Condicionada a ingresos

- **Modulo:** Solicitudes pantalla Expediente
- **Lo que pide la matriz:** Al seleccionar el Check se crean solicitudes con estatus Condicionada a ingresos
- **Automatizado:** no. Depende del check Condicionada a ingresos, que no existe hoy.

## PF_CP_124 — Validación de la funcionalidad del botón eliminar solicitudes

- **Modulo:** Solicitudes pantalla Expediente
- **Lo que pide la matriz:** Muestra la pantalla de solicitudes eliminadas
- **Prueba:** `ExpedienteDictamenPruebas#pfCp124EliminarSolicitudesConLayout`  (etiquetas: ola6, expediente, regla_por_confirmar)
- **Lo que valida el codigo:** La pantalla ofrece eliminar solicitudes con un layout
- **Correr solo este caso:** `mvn test -Dtest='ExpedienteDictamenPruebas#pfCp124EliminarSolicitudesConLayout'`
- **Pasos que ejecuta:** `hayBotonParaEliminarSolicitudes`
- **Verificaciones:** 1

```java
        Assert.assertTrue(expediente.hayBotonParaEliminarSolicitudes(),
                "La pantalla de Expediente no ofrece eliminar solicitudes: no existe el boton que "
                        + "pide la matriz (misma diferencia que PF_CP_125, sin boton Importar). "
                        + "Botones que ofrece hoy: CREAR SOLICITUD, Aprobar solicitudes, Denegar "
                        + "solicitudes, Exportar y Filtrar.");
```

## PF_CP_125 — Validación de la funcionalidad del botón importar

- **Modulo:** Solicitudes pantalla Expediente
- **Lo que pide la matriz:** Muestra la pantalla para cargar el documento y registrar solicitudes masivas, así como el botón para descargar el Layout de ejemplo.
- **Prueba:** `DescargasPruebas#expedienteOfreceImportar`  (etiquetas: descargas, regla_por_confirmar)
- **Lo que valida el codigo:** Expediente ofrece importar solicitudes masivas
- **Correr solo este caso:** `mvn test -Dtest='DescargasPruebas#expedienteOfreceImportar'`
- **Pasos que ejecuta:** `irAlMenu` -> `abrir` -> `hayBotonImportar`
- **Verificaciones:** 1

```java
        entrar().irAlMenu("Expediente");
        Assert.assertTrue(new PaginaExpediente().abrir().hayBotonImportar(),
                "La pantalla de Expediente no muestra el boton Importar que describe la matriz: "
                        + "hay que confirmar con negocio si la carga masiva de solicitudes se "
                        + "quito o depende de otro permiso.");
```

## PF_CP_126 — Cargar documento

- **Modulo:** Solicitudes pantalla Expediente
- **Lo que pide la matriz:** Muestra la pantalla de cuantas solicitudes se cargaron y cuantas con error, así como la descarga de un excel donde viene detallado cada error.
- **Prueba:** `ExpedienteDictamenPruebas#pfCp126CargarLayoutDeSolicitudes`  (etiquetas: ola6, expediente, regla_por_confirmar)
- **Lo que valida el codigo:** La pantalla permite cargar el layout de solicitudes
- **Correr solo este caso:** `mvn test -Dtest='ExpedienteDictamenPruebas#pfCp126CargarLayoutDeSolicitudes'`
- **Pasos que ejecuta:** `hayBotonImportar`
- **Verificaciones:** 1

```java
        Assert.assertTrue(expediente.hayBotonImportar(),
                "La pantalla de Expediente no permite cargar el layout de solicitudes: no existe "
                        + "el boton Importar (misma diferencia que PF_CP_125). Sin esa pantalla no "
                        + "hay resumen de cargas correctas/con error ni descarga del detalle de "
                        + "errores.");
```

## PF_CP_127 — Validación de la funcionalidad del botón Exportar

- **Modulo:** Solicitudes pantalla Expediente
- **Lo que pide la matriz:** Realiza la descarga de un archivo excel en el que contiene: Id_Solicitud(cid:9)Referencia(cid:9)Nombre(cid:9)Apellid o(s)(cid:9)DNI(cid:9)Producto(cid:9)Fecha creación(cid:9)Fecha modificación(cid:9)Campaña(cid:9)Creada por(cid:9)Estatus
- **Prueba:** `DescargasPruebas#exportarSolicitudesAExcel`  (etiquetas: descargas, regla_por_confirmar)
- **Lo que valida el codigo:** Exportar descarga la tabla de solicitudes
- **Correr solo este caso:** `mvn test -Dtest='DescargasPruebas#exportarSolicitudesAExcel'`
- **Pasos que ejecuta:** `irAlMenu` -> `abrir` -> `exportarAExcel`
- **Verificaciones:** 1

```java
        entrar().irAlMenu("Expediente");
        new PaginaExpediente().abrir().exportarAExcel();

        Path excel = Descargas.esperarArchivo(".xlsx");
        debeTenerLasColumnas(excel, null, "amex.excel.solicitudes");
```

## PF_CP_128 — Validación de la funcionalidad del botón Filtrar

- **Modulo:** Solicitudes pantalla Expediente
- **Lo que pide la matriz:** Muestra la tabla con el filtro realizado
- **Prueba:** `ExpedienteConsultasPruebas#pfCp128FiltrarSolicitudes`  (etiquetas: consultas)
- **Lo que valida el codigo:** El filtro de solicitudes busca por sus campos
- **Correr solo este caso:** `mvn test -Dtest='ExpedienteConsultasPruebas#pfCp128FiltrarSolicitudes'`
- **Pasos que ejecuta:** `valorDeLaPrimeraFila` -> `abrirElFiltro` -> `elFiltroDebeTenerSusCampos` -> `filtrarPorDni` -> `documentosDeLaTablaCuandoTodosSean` -> `allMatch` -> `limpiarElFiltro` -> `valorDelFiltroDeDni`
- **Verificaciones:** 4

```java
        // El DNI del filtro se toma de la primera fila: la prueba no depende de que
        // el ambiente tenga una solicitud en particular.
        String dni = expediente.valorDeLaPrimeraFila(PaginaExpediente.COLUMNA_DNI);

        List<String> documentos = expediente.abrirElFiltro()
                .elFiltroDebeTenerSusCampos()
                .filtrarPorDni(dni)
                .documentosDeLaTablaCuandoTodosSean(dni);

        Assert.assertFalse(documentos.isEmpty(),
                "El filtro por DNI \"" + dni + "\" no devolvio ninguna solicitud.");
        Assert.assertTrue(documentos.stream().allMatch(dni::equals),
                "La tabla filtrada por el DNI \"" + dni + "\" muestra otros: " + documentos + ".");

        // Al buscar, la aplicacion cierra el panel: hay que abrirlo otra vez para
        // presionar Limpiar.
        expediente.abrirElFiltro().limpiarElFiltro();
        Assert.assertEquals(expediente.valorDelFiltroDeDni(), "",
                "El campo DNI del filtro no quedo vacio despues de Limpiar.");
```

## PF_CP_129 — Validación de la funcionalidad del botón detalle

- **Modulo:** Solicitudes pantalla Expediente
- **Lo que pide la matriz:** Muestra el detalle de la solicitud con la imágen de la tarjeta, el DNI, Firma, Carátula, Renaper, Devolver, Dictaminar.
- **Prueba:** `ExpedienteConsultasPruebas#pfCp129DetalleDeLaSolicitud`  (etiquetas: consultas)
- **Lo que valida el codigo:** El detalle de la solicitud muestra sus pestanas
- **Correr solo este caso:** `mvn test -Dtest='ExpedienteConsultasPruebas#pfCp129DetalleDeLaSolicitud'`
- **Pasos que ejecuta:** `abrirElDetalleDeLaPrimeraSolicitud` -> `elDetalleDebeMostrarLaImagenDeLaTarjeta` -> `elDetalleDebeMostrar`
- **Verificaciones:** 2

```java
        expediente.abrirElDetalleDeLaPrimeraSolicitud()
                .elDetalleDebeMostrarLaImagenDeLaTarjeta()
                .elDetalleDebeMostrar("DNI", "Firma", "Carátula", "RENAPER", "Devolver",
                        "Dictaminar");
```

## PF_CP_130 — Validación del detalle de expediente con un estatus creado

- **Modulo:** Solicitudes pantalla Expediente
- **Lo que pide la matriz:** Muestra el detalle de la solicitud con la imágen de la tarjeta, el DNI, y los campos de Firma, Carátula, Renaper, Devolver, Dictaminar deshabilitados.
- **Prueba:** `ExpedienteEstadosPruebas#pfCp130DetalleCreado`  (etiquetas: ola6, expediente)
- **Lo que valida el codigo:** Detalle de una solicitud en estatus Creado
- **Correr solo este caso:** `mvn test -Dtest='ExpedienteEstadosPruebas#pfCp130DetalleCreado'`
- **Pasos que ejecuta:** 
- **Verificaciones:** 0

```java
        verificarElDetalleDelEstatus("PF_CP_130");
```

## PF_CP_131 — Validación del detalle de expediente con un estatus ingreso

- **Modulo:** Solicitudes pantalla Expediente
- **Lo que pide la matriz:** Muestra el detalle de la solicitud con la imágen de la tarjeta, el DNI, y los campos de Firma, Carátula, Renaper, Devolver Dictaminar deshabilitados.
- **Prueba:** `ExpedienteEstadosPruebas#pfCp131DetalleIngreso`  (etiquetas: ola6, expediente)
- **Lo que valida el codigo:** Detalle de una solicitud en estatus Ingreso
- **Correr solo este caso:** `mvn test -Dtest='ExpedienteEstadosPruebas#pfCp131DetalleIngreso'`
- **Pasos que ejecuta:** 
- **Verificaciones:** 0

```java
        verificarElDetalleDelEstatus("PF_CP_131");
```

## PF_CP_132 — Validación del detalle de expediente con un estatus aviso de privacidad

- **Modulo:** Solicitudes pantalla Expediente
- **Lo que pide la matriz:** Muestra el detalle de la solicitud con la imágen de la tarjeta, el DNI, y los campos de Firma, Carátula, Renaper, Devolver, Dictaminar deshabilitados y se genera el estatus de solicitud de vuelta
- **Prueba:** `ExpedienteEstadosPruebas#pfCp132DetalleAvisoDePrivacidad`  (etiquetas: ola6, expediente)
- **Lo que valida el codigo:** Detalle de una solicitud en estatus Aviso de privacidad
- **Correr solo este caso:** `mvn test -Dtest='ExpedienteEstadosPruebas#pfCp132DetalleAvisoDePrivacidad'`
- **Pasos que ejecuta:** 
- **Verificaciones:** 0

```java
        verificarElDetalleDelEstatus("PF_CP_132");
```

## PF_CP_133 — Validación del detalle de expediente con un estatus identificación oficial

- **Modulo:** Solicitudes pantalla Expediente
- **Lo que pide la matriz:** Muestra el detalle de la solicitud con la imágen de la tarjeta, el DNI capturado, y los campos de Firma, Carátula, Renaper, devolver, Dictaminar deshabilitados
- **Prueba:** `ExpedienteEstadosPruebas#pfCp133DetalleIdentificacionOficial`  (etiquetas: ola6, expediente, regla_por_confirmar)
- **Lo que valida el codigo:** Detalle de una solicitud en estatus Identificación oficial
- **Correr solo este caso:** `mvn test -Dtest='ExpedienteEstadosPruebas#pfCp133DetalleIdentificacionOficial'`
- **Pasos que ejecuta:** 
- **Verificaciones:** 0

```java
        verificarElDetalleDelEstatus("PF_CP_133");
```

## PF_CP_134 — Validación del detalle de expediente con un estatus Validación de identidad

- **Modulo:** Solicitudes pantalla Expediente
- **Lo que pide la matriz:** Muestra el detalle de la solicitud con la imágen de la tarjeta, el DNI capturado, y los campos de Firma, Carátula, Renaper, devolver, Dictaminar deshabilitados
- **Prueba:** `ExpedienteEstadosPruebas#pfCp134DetalleValidacionDeIdentidad`  (etiquetas: ola6, expediente, regla_por_confirmar)
- **Lo que valida el codigo:** Detalle de una solicitud en estatus Validación de identidad
- **Correr solo este caso:** `mvn test -Dtest='ExpedienteEstadosPruebas#pfCp134DetalleValidacionDeIdentidad'`
- **Pasos que ejecuta:** 
- **Verificaciones:** 0

```java
        verificarElDetalleDelEstatus("PF_CP_134");
```

## PF_CP_135 — Validación del detalle de expediente con un estatus Terminos y condiciones

- **Modulo:** Solicitudes pantalla Expediente
- **Lo que pide la matriz:** Muestra el detalle de la solicitud con la imágen de la tarjeta, el DNI, y los campos de Firma, Carátula, Renaper, Devolver, Dictaminar deshabilitados
- **Prueba:** `ExpedienteEstadosPruebas#pfCp135DetalleTerminosYCondiciones`  (etiquetas: ola6, expediente, regla_por_confirmar)
- **Lo que valida el codigo:** Detalle de una solicitud en estatus Terminos y condiciones
- **Correr solo este caso:** `mvn test -Dtest='ExpedienteEstadosPruebas#pfCp135DetalleTerminosYCondiciones'`
- **Pasos que ejecuta:** 
- **Verificaciones:** 0

```java
        verificarElDetalleDelEstatus("PF_CP_135");
```

## PF_CP_136 — Validación del detalle de expediente con un estatus Pendiente de firma

- **Modulo:** Solicitudes pantalla Expediente
- **Lo que pide la matriz:** Muestra el detalle de la solicitud con la imágen de la tarjeta, el DNI habilitado, Carátula habilitadoi, y los campos de Firma, Renaper, Devolver habilitado y Dictaminar deshabilitados
- **Prueba:** `ExpedienteEstadosPruebas#pfCp136DetallePendienteDeFirma`  (etiquetas: ola6, expediente, regla_por_confirmar)
- **Lo que valida el codigo:** Detalle de una solicitud en estatus Pendiente de firma
- **Correr solo este caso:** `mvn test -Dtest='ExpedienteEstadosPruebas#pfCp136DetallePendienteDeFirma'`
- **Pasos que ejecuta:** 
- **Verificaciones:** 0

```java
        verificarElDetalleDelEstatus("PF_CP_136");
```

## PF_CP_137 — Validación del detalle de expediente con un estatus Firmada

- **Modulo:** Solicitudes pantalla Expediente
- **Lo que pide la matriz:** Muestra el detalle de la solicitud con la imágen de la tarjeta, el DNI, y los campos de Firma, Carátula, Dictaminar deshabilitados
- **Prueba:** `ExpedienteEstadosPruebas#pfCp137DetalleFirmada`  (etiquetas: ola6, expediente)
- **Lo que valida el codigo:** Detalle de una solicitud en estatus Firmada
- **Correr solo este caso:** `mvn test -Dtest='ExpedienteEstadosPruebas#pfCp137DetalleFirmada'`
- **Pasos que ejecuta:** 
- **Verificaciones:** 0

```java
        verificarElDetalleDelEstatus("PF_CP_137");
```

## PF_CP_138 — Validación del detalle de expediente con un estatus por dictaminar

- **Modulo:** Solicitudes pantalla Expediente
- **Lo que pide la matriz:** Muestra el detalle de la solicitud con la imágen de la tarjeta, el DNI, y los campos de Firma, Carátula, Renaper, Devolver Dictaminar habilitados
- **Prueba:** `ExpedienteEstadosPruebas#pfCp138DetallePorDictaminar`  (etiquetas: ola6, expediente)
- **Lo que valida el codigo:** Detalle de una solicitud en estatus Por dictaminar
- **Correr solo este caso:** `mvn test -Dtest='ExpedienteEstadosPruebas#pfCp138DetallePorDictaminar'`
- **Pasos que ejecuta:** 
- **Verificaciones:** 0

```java
        verificarElDetalleDelEstatus("PF_CP_138");
```

## PF_CP_139 — Validación del detalle de expediente con un estatus denegada

- **Modulo:** Solicitudes pantalla Expediente
- **Lo que pide la matriz:** Muestra el detalle de la solicitud con la imágen de la tarjeta, el DNI, y los campos de Firma, Carátula, Renaper, Devolver Dictaminar habilitados
- **Prueba:** `ExpedienteEstadosPruebas#pfCp139DetalleDenegada`  (etiquetas: ola6, expediente, regla_por_confirmar)
- **Lo que valida el codigo:** Detalle de una solicitud en estatus Denegada
- **Correr solo este caso:** `mvn test -Dtest='ExpedienteEstadosPruebas#pfCp139DetalleDenegada'`
- **Pasos que ejecuta:** 
- **Verificaciones:** 0

```java
        verificarElDetalleDelEstatus("PF_CP_139");
```

## PF_CP_140 — Validación del detalle de expediente con un estatus aprobada

- **Modulo:** Solicitudes pantalla Expediente
- **Lo que pide la matriz:** Muestra el detalle de la solicitud con la imágen de la tarjeta, el DNI, y los campos de Firma, Carátula, Renaper, Devolver Dictaminar habilitados
- **Prueba:** `ExpedienteEstadosPruebas#pfCp140DetalleAprobada`  (etiquetas: ola6, expediente, regla_por_confirmar)
- **Lo que valida el codigo:** Detalle de una solicitud en estatus Aprobada
- **Correr solo este caso:** `mvn test -Dtest='ExpedienteEstadosPruebas#pfCp140DetalleAprobada'`
- **Pasos que ejecuta:** 
- **Verificaciones:** 0

```java
        verificarElDetalleDelEstatus("PF_CP_140");
```

## PF_CP_141 — Validación del detalle de expediente con un estatus Cancelado por datos erróneos

- **Modulo:** Solicitudes pantalla Expediente
- **Lo que pide la matriz:** Muestra el detalle de la solicitud con la imágen de la tarjeta, el DNI, y los campos de Firma, Carátula,Renaper, Devolver Dictaminar deshabilitados y se genera el estatus de solicitud de vuelta
- **Prueba:** `ExpedienteEstadosPruebas#pfCp141DetalleCanceladoPorDatosErroneos`  (etiquetas: ola6, expediente)
- **Lo que valida el codigo:** Detalle de una solicitud en estatus Cancelado por datos erróneos
- **Correr solo este caso:** `mvn test -Dtest='ExpedienteEstadosPruebas#pfCp141DetalleCanceladoPorDatosErroneos'`
- **Pasos que ejecuta:** 
- **Verificaciones:** 0

```java
        verificarElDetalleDelEstatus("PF_CP_141");
```

## PF_CP_142 — Validación de la funcionalidad del botón ZIP

- **Modulo:** Solicitudes pantalla Expediente
- **Lo que pide la matriz:** Muestra una carpeta comprimida en donde se encuentra todos los documentos cargados
- **Prueba:** `DescargasPruebas#descargarElZipDelExpediente`  (etiquetas: descargas)
- **Lo que valida el codigo:** El ZIP de la solicitud trae los documentos cargados
- **Correr solo este caso:** `mvn test -Dtest='DescargasPruebas#descargarElZipDelExpediente'`
- **Pasos que ejecuta:** `irAlMenu` -> `abrir` -> `descargarElZipDeUnaSolicitudFirmada`
- **Verificaciones:** 1

```java
        entrar().irAlMenu("Expediente");
        new PaginaExpediente().abrir().descargarElZipDeUnaSolicitudFirmada(0);

        debeTraerLosDocumentos(Descargas.esperarArchivo(".zip"), "amex.zip.expediente");
```

## PF_CP_143 — Validación de la funcionalidad del botón ZIP Griffin

- **Modulo:** Solicitudes pantalla Expediente
- **Lo que pide la matriz:** Muestra una carpeta comprimirda con el documento de Renaper, los archivos de iSneg rmesoosst yr aerl ad oucnu mpoenptuop fi rcmoand loa.
- **Prueba:** `DescargasPruebas#descargarElZipGriffin`  (etiquetas: descargas)
- **Lo que valida el codigo:** El ZIP Griffin trae el documento de Renaper y el firmado
- **Correr solo este caso:** `mvn test -Dtest='DescargasPruebas#descargarElZipGriffin'`
- **Pasos que ejecuta:** `irAlMenu` -> `abrir` -> `descargarElZipDeUnaSolicitudFirmada`
- **Verificaciones:** 1

```java
        entrar().irAlMenu("Expediente");
        new PaginaExpediente().abrir().descargarElZipDeUnaSolicitudFirmada(1);

        debeTraerLosDocumentos(Descargas.esperarArchivo(".zip"), "amex.zip.griffin");
```

## PF_CP_144 — Validacion de la funcionalidad del boton de Aprobar solicitudes

- **Modulo:** Solicitudes pantalla Expediente
- **Lo que pide la matriz:** leyenda "Aprobar solicitudes Se aprobara X solicitudes seleccionadas" y se actualizara el estado de las solicitudes como Se mostraArpar oubna pdoapsup con la
- **Prueba:** `ExpedienteDictamenPruebas#pfCp144PopupDeAprobarSolicitudes`  (etiquetas: ola6, expediente)
- **Lo que valida el codigo:** El boton Aprobar solicitudes confirma cuantas se aprobaran
- **Correr solo este caso:** `mvn test -Dtest='ExpedienteDictamenPruebas#pfCp144PopupDeAprobarSolicitudes'`
- **Pasos que ejecuta:** `leerElPopupDeDictamenYCancelar` -> `toUpperCase`
- **Verificaciones:** 2

```java
        seleccionarUnaSolicitudPorDictaminar();

        String leyenda = expediente.leerElPopupDeDictamenYCancelar(
                Selectores.SOLICITUDES_BOTON_APROBAR);

        Assert.assertTrue(leyenda.toUpperCase().contains("APROBAR"),
                "El popup de Aprobar solicitudes no menciona la accion. Dice: \"" + leyenda + "\".");
        Assert.assertTrue(leyenda.contains("1"),
                "El popup no indica cuantas solicitudes se aprobaran. Dice: \"" + leyenda + "\".");
```

## PF_CP_145 — Validacion de la funcionalidad del boton de Denegar solicitudes

- **Modulo:** Solicitudes pantalla Expediente
- **Lo que pide la matriz:** leyenda "Denegar solicitudes Se denegaran X solicitudes seleccionadas" y se actualizara el estado de las solicitudes como Denegadas
- **Prueba:** `ExpedienteDictamenPruebas#pfCp145PopupDeDenegarSolicitudes`  (etiquetas: ola6, expediente)
- **Lo que valida el codigo:** El boton Denegar solicitudes confirma cuantas se denegaran
- **Correr solo este caso:** `mvn test -Dtest='ExpedienteDictamenPruebas#pfCp145PopupDeDenegarSolicitudes'`
- **Pasos que ejecuta:** `leerElPopupDeDictamenYCancelar` -> `toUpperCase`
- **Verificaciones:** 2

```java
        seleccionarUnaSolicitudPorDictaminar();

        String leyenda = expediente.leerElPopupDeDictamenYCancelar(
                Selectores.SOLICITUDES_BOTON_DENEGAR);

        Assert.assertTrue(leyenda.toUpperCase().contains("DENEGAR"),
                "El popup de Denegar solicitudes no menciona la accion. Dice: \"" + leyenda + "\".");
        Assert.assertTrue(leyenda.contains("1"),
                "El popup no indica cuantas solicitudes se denegaran. Dice: \"" + leyenda + "\".");
```

## PF_CP_146 — Validación de la funcionalidad del botón Doc. Griffin

- **Modulo:** Solicitudes pantalla Expediente
- **Lo que pide la matriz:** Muestra una carpeta comprimida en donde se encuentra el expediente de identificacion, renaper y la firma
- **Prueba:** `ExpedienteDictamenPruebas#pfCp146DocGriffinDelDetalle`  (etiquetas: ola6, expediente, descargas)
- **Lo que valida el codigo:** Doc. Griffin descarga el expediente comprimido
- **Correr solo este caso:** `mvn test -Dtest='ExpedienteDictamenPruebas#pfCp146DocGriffinDelDetalle'`
- **Pasos que ejecuta:** `abrirElDetalleDeLaPrimeraSolicitud` -> `abrirElMenuDelDetalle` -> `descargarElDocGriffinDelDetalle` -> `endsWith`
- **Verificaciones:** 2

```java
        Descargas.limpiar();
        filtrarPorSolicitudesDictaminables();
        expediente.abrirElDetalleDeLaPrimeraSolicitud();

        List<String> opciones = expediente.abrirElMenuDelDetalle();
        Assert.assertTrue(opciones.stream().anyMatch(opcion -> opcion.contains("Griffin")),
                "El menu del detalle no ofrece Doc. Griffin. Ofrece: " + opciones + ".");

        expediente.descargarElDocGriffinDelDetalle();
        Path zip = Descargas.esperarArchivo("zip");

        List<String> documentos = Descargas.contenidoDelZip(zip);
        for (String documento : Configuracion.lista("amex.zip.griffin.detalle")) {
            Assert.assertTrue(documentos.stream().anyMatch(nombre -> nombre.endsWith(documento)),
                    "El ZIP de Doc. Griffin no trae \"" + documento + "\". Trae: " + documentos
                            + ".");
        }
```

## PF_CP_147 — Validación de la pantalla del menú Costos

- **Modulo:** Expediente pantalla Costos
- **Lo que pide la matriz:** Muestra una pantalla con los productos disponibles, La imagen, su nombre y el boton de seleccionar
- **Prueba:** `NavegacionPruebas#pfCp147PantallaCostos`  (etiquetas: navegacion)
- **Lo que valida el codigo:** Pantalla de Costos
- **Correr solo este caso:** `mvn test -Dtest='NavegacionPruebas#pfCp147PantallaCostos'`
- **Pasos que ejecuta:** `irAlMenu` -> `laDireccionDebeContener`
- **Verificaciones:** 1

```java
        inicio.irAlMenu("Costos").laDireccionDebeContener("expedient/costs");
```

## PF_CP_148 — Validacion del boton de Seleccionar de un producto

- **Modulo:** Expediente pantalla Costos
- **Lo que pide la matriz:** Muestra una nueva pantalla donde se observar el costo de la tarjeta al seleccionar un año y un mes
- **Prueba:** `TasasCostosYReportesPruebas#pfCp148CostosDelProducto`  (etiquetas: consultas)
- **Lo que valida el codigo:** Seleccionar un producto abre su pantalla de costos
- **Correr solo este caso:** `mvn test -Dtest='TasasCostosYReportesPruebas#pfCp148CostosDelProducto'`
- **Pasos que ejecuta:** `irAlMenu` -> `abrir` -> `seleccionarElPrimerProducto` -> `debePedirAnioYMes` -> `elegirElPrimerPeriodo` -> `debeMostrarLosCostosDelPeriodo`
- **Verificaciones:** 2

```java
        inicio.irAlMenu("Costos");

        new PaginaCostos().abrir()
                .seleccionarElPrimerProducto()
                .debePedirAnioYMes()
                .elegirElPrimerPeriodo()
                .debeMostrarLosCostosDelPeriodo();
```

## PF_CP_149 — Validacion del boton de Agregar costo

- **Modulo:** Expediente pantalla Costos
- **Lo que pide la matriz:** Muestra un popup donde se debera ingresar la informacion del costo: -Ingreso minimo -Costo por tarjeta -Cuotal metal y se guarda el registro que se mostrará en la pantalla
- **Prueba:** `CostosYCuotasPruebas#pfCp149PopupDeAgregarCosto`  (etiquetas: ola6, costos)
- **Lo que valida el codigo:** El popup de Agregar costo pide todos los importes
- **Correr solo este caso:** `mvn test -Dtest='CostosYCuotasPruebas#pfCp149PopupDeAgregarCosto'`
- **Pasos que ejecuta:** `abrirElPopupDeAgregarCosto` -> `elPopupDebePedirTodosLosDatosDelCosto` -> `escribirLosDatosDelCosto` -> `elPopupPermiteGuardar` -> `cerrarElPopupSinGuardar`
- **Verificaciones:** 2

```java
        PaginaCostos costos = abrirElProducto();

        costos.abrirElPopupDeAgregarCosto()
                .elPopupDebePedirTodosLosDatosDelCosto()
                .escribirLosDatosDelCosto("1");

        Assert.assertTrue(costos.elPopupPermiteGuardar(),
                "Con todos los importes capturados el popup no habilita Guardar.");
        costos.cerrarElPopupSinGuardar();
```

## PF_CP_150 — Validacion del boton de Editar costo

- **Modulo:** Expediente pantalla Costos
- **Lo que pide la matriz:** Muestra un popup donde se podra editar la informacion del costo: -Ingreso minimo -Costo por tarjeta -Cuotal metal y se guarda el registro que se mostrará en la pantalla
- **Prueba:** `CostosYCuotasPruebas#pfCp150PopupDeEditarCosto`  (etiquetas: ola6, costos)
- **Lo que valida el codigo:** El popup de Editar costo trae los importes del registro
- **Correr solo este caso:** `mvn test -Dtest='CostosYCuotasPruebas#pfCp150PopupDeEditarCosto'`
- **Pasos que ejecuta:** `abrirElPopupDeEditarCosto` -> `elPopupDebePedirTodosLosDatosDelCosto` -> `elPopupPermiteGuardar` -> `cerrarElPopupSinGuardar`
- **Verificaciones:** 2

```java
        PaginaCostos costos = abrirElProducto();

        costos.abrirElPopupDeEditarCosto()
                .elPopupDebePedirTodosLosDatosDelCosto();

        Assert.assertTrue(costos.elPopupPermiteGuardar(),
                "El popup de edicion del costo no habilita Guardar.");
        costos.cerrarElPopupSinGuardar();
```

## PF_CP_151 — Validación de la pantalla del menú Cuotas generales

- **Modulo:** Expediente pantalla Cuotas generales
- **Lo que pide la matriz:** Muestra una pantalla donde se puede seleccionar un mes y año junto con una tabla con el nombre del producto, su costo y un boton para guardar la informacion
- **Prueba:** `NavegacionPruebas#pfCp151PantallaCuotasGenerales`  (etiquetas: navegacion)
- **Lo que valida el codigo:** Pantalla de Cuotas generales
- **Correr solo este caso:** `mvn test -Dtest='NavegacionPruebas#pfCp151PantallaCuotasGenerales'`
- **Pasos que ejecuta:** `irAlMenu` -> `laDireccionDebeContener`
- **Verificaciones:** 1

```java
        inicio.irAlMenu("Cuotas Generales").laDireccionDebeContener("general-fees");
```

## PF_CP_152 — Validacion del boton de guardar

- **Modulo:** Expediente pantalla Cuotas generales
- **Lo que pide la matriz:** Se actualizara la pantalla con la nueva informacion ingresada
- **Prueba:** `CostosYCuotasPruebas#pfCp152CuotasGenerales`  (etiquetas: ola6, cuotas)
- **Lo que valida el codigo:** Cuotas Generales permite capturar los importes y guardarlos
- **Correr solo este caso:** `mvn test -Dtest='CostosYCuotasPruebas#pfCp152CuotasGenerales'`
- **Pasos que ejecuta:** `iniciarSesionConCredencialesValidas` -> `irAlMenu` -> `abrir` -> `conceptos` -> `losImportesDebenSerEditables` -> `hayBotonGuardar` -> `importes` -> `allMatch` -> `elBotonGuardarEstaHabilitado`
- **Verificaciones:** 4

```java
        inicio = new PaginaLogin().iniciarSesionConCredencialesValidas();
        inicio.irAlMenu("Cuotas Generales");
        PaginaCuotasGenerales cuotas = new PaginaCuotasGenerales().abrir();

        List<String> conceptos = cuotas.conceptos();
        Assert.assertFalse(conceptos.isEmpty(),
                "La pantalla de Cuotas Generales no muestra ningun concepto.");
        cuotas.losImportesDebenSerEditables();

        Assert.assertTrue(cuotas.hayBotonGuardar(),
                "La pantalla de Cuotas Generales no muestra el boton Guardar. Conceptos: "
                        + conceptos + ".");
        // Guardar solo se habilita cuando los importes estan capturados: en QA la
        // pantalla llega vacia, asi que el boton deshabilitado es el comportamiento
        // correcto y se informa como dato semilla faltante.
        if (cuotas.importes().stream().allMatch(importe -> importe == null || importe.isBlank())) {
            throw new SkipException("La pantalla de Cuotas Generales no tiene importes cargados en "
                    + "este ambiente (" + conceptos + "): hace falta el dato semilla y un periodo "
                    + "de prueba acordado para validar el guardado.");
        }
        Assert.assertTrue(cuotas.elBotonGuardarEstaHabilitado(),
                "El boton Guardar de Cuotas Generales esta deshabilitado con los importes "
                        + cuotas.importes() + ".");
```

## PF_CP_153 — Validación de la pantalla del menú Reportes

- **Modulo:** Expediente pantalla Reportes
- **Lo que pide la matriz:** Muestra los siguientes campos y botones leaderboarder Seleccione el tipo de reporte Reporte general Reporte totales URL Reporte totales mensaje whatsapp Filtros Fecha inicio Fecha fin Referencia DNI Nombre Apellido Rol Estatus Campaña P.E.P.: Si No Sujeto obligado: Si No Generar reporte, Limpiar filtros
- **Prueba:** `NavegacionPruebas#pfCp153PantallaReportes`  (etiquetas: navegacion)
- **Lo que valida el codigo:** Pantalla de Reportes con sus filtros
- **Correr solo este caso:** `mvn test -Dtest='NavegacionPruebas#pfCp153PantallaReportes'`
- **Pasos que ejecuta:** `irAlMenu` -> `laDireccionDebeContener`
- **Verificaciones:** 1

```java
        inicio.irAlMenu("Reportes").laDireccionDebeContener("expedient/reports");
```

## PF_CP_154 — Validación de generación de reporte con el filtro de reporte general

- **Modulo:** Expediente pantalla Reportes
- **Lo que pide la matriz:** Se descarga un archivo excel con la siguiiente información:Id(cid:9)Referencia(cid:9)Nombre(cid:9)Apellidos DNI(cid:9)Estatus de la solicitud(cid:9)Fecha Creación(cid:9)Fecha modificación(cid:9)Producto(cid:9)Campaña(cid:9)Geolocaliz ación(cid:9)Resultado de biometría(cid:9)Porcentaje de coincidencia(cid:9)Dispositivo usado(cid:9)Sitema operativo(cid:9)Sujeto obligado(cid:9)P.E.P.(cid:9)P.E.P.(cargo / función / jerarquía)(cid:9)Creado por
- **Prueba:** `DescargasPruebas#generarElReporteGeneral`  (etiquetas: descargas, regla_por_confirmar)
- **Lo que valida el codigo:** El reporte general se descarga con sus columnas
- **Correr solo este caso:** `mvn test -Dtest='DescargasPruebas#generarElReporteGeneral'`
- **Pasos que ejecuta:** `irAlMenu` -> `abrir` -> `elegirElTipoDeReporte` -> `generarElReporte`
- **Verificaciones:** 1

```java
        entrar().irAlMenu("Reportes");
        new PaginaReportes().abrir()
                .elegirElTipoDeReporte("Reporte general")
                .generarElReporte();

        debeTenerLasColumnas(Descargas.esperarArchivo(".xlsx"), null,
                "amex.excel.reporte.general");
```

## PF_CP_155 — Validación de generación de reporte sin información con el filtro de reporte general, reporte totales url o reporte totales mensajes whatsapp

- **Modulo:** Expediente pantalla Reportes
- **Lo que pide la matriz:** Muestra un mensaje indicando AMEX Argentina No se encontraron resultados.
- **Prueba:** `DescargasPruebas#generarUnReporteSinResultados`  (etiquetas: descargas)
- **Lo que valida el codigo:** Un reporte sin informacion avisa que no hay resultados
- **Correr solo este caso:** `mvn test -Dtest='DescargasPruebas#generarUnReporteSinResultados'`
- **Pasos que ejecuta:** `irAlMenu` -> `abrir` -> `elegirElTipoDeReporte` -> `filtrarPorDni` -> `generarElReporte` -> `mensajeDelPopup` -> `aceptarElPopup`
- **Verificaciones:** 2

```java
        entrar().irAlMenu("Reportes");
        PaginaReportes reportes = new PaginaReportes().abrir()
                .elegirElTipoDeReporte("Reporte general")
                .filtrarPorDni(Configuracion.obtener("amex.reportes.dni.inexistente"))
                .generarElReporte();

        Assert.assertTrue(reportes.mensajeDelPopup().contains("No se encontraron resultados"),
                "El reporte sin informacion no aviso que no hay resultados; el popup dice: \""
                        + reportes.mensajeDelPopup() + "\".");
        Assert.assertTrue(Descargas.archivos().isEmpty(),
                "Un reporte sin resultados no deberia descargar archivo, y descargo: "
                        + Descargas.archivos() + ".");
        reportes.aceptarElPopup();
```

## PF_CP_156 — Validación del botón limpiar filtros

- **Modulo:** Expediente pantalla Reportes
- **Lo que pide la matriz:** Se borran los datos que se agregaron para el filtro
- **Prueba:** `TasasCostosYReportesPruebas#pfCp156LimpiarFiltrosDeReportes`  (etiquetas: consultas)
- **Lo que valida el codigo:** Limpiar filtros borra los datos del filtro de Reportes
- **Correr solo este caso:** `mvn test -Dtest='TasasCostosYReportesPruebas#pfCp156LimpiarFiltrosDeReportes'`
- **Pasos que ejecuta:** `irAlMenu` -> `abrir` -> `tiposDeReporte` -> `llenarLosFiltros` -> `limpiarLosFiltros` -> `losFiltrosDebenEstarVacios`
- **Verificaciones:** 2

```java
        inicio.irAlMenu("Reportes");

        PaginaReportes reportes = new PaginaReportes().abrir();
        List<String> tipos = reportes.tiposDeReporte();
        Assert.assertTrue(tipos.contains("Reporte general"),
                "La lista de tipos de reporte no ofrece \"Reporte general\". Ofrece: "
                        + tipos + ".");

        reportes.llenarLosFiltros("REF12345", "12345678", "JUAN", "PEREZ")
                .limpiarLosFiltros()
                .losFiltrosDebenEstarVacios();
```

## PF_CP_157 — Validación de generación de reporte con el filtro de reporte totales URL

- **Modulo:** Expediente pantalla Reportes
- **Lo que pide la matriz:** Se descarga un archivo excel con la siguiiente información: Pestaña totales: Total de Urls creadas Pestaña data: Id(cid:9)Referencia(cid:9)Fecha Creación(cid:9)Creado por(cid:9)Key Url(cid:9)Fecha expiración(cid:9)Estatus Url
- **Prueba:** `DescargasPruebas#generarElReporteDeTotalesUrl`  (etiquetas: descargas)
- **Lo que valida el codigo:** El reporte de totales URL trae sus dos hojas
- **Correr solo este caso:** `mvn test -Dtest='DescargasPruebas#generarElReporteDeTotalesUrl'`
- **Pasos que ejecuta:** `irAlMenu` -> `abrir` -> `elegirElTipoDeReporte` -> `generarElReporte`
- **Verificaciones:** 2

```java
        entrar().irAlMenu("Reportes");
        new PaginaReportes().abrir()
                .elegirElTipoDeReporte("Reporte totales URL")
                .generarElReporte();

        Path excel = Descargas.esperarArchivo(".xlsx");
        debeTenerLasColumnas(excel, "totales", "amex.excel.reporte.url.totales");
        debeTenerLasColumnas(excel, "data", "amex.excel.reporte.url");
```

## PF_CP_158 — Validación de generación de reporte con el filtro de reporte totales mensaje whatsapp

- **Modulo:** Expediente pantalla Reportes
- **Lo que pide la matriz:** Se descarga un archivo excel con la siguiiente información: Pestaña Totales: Total de mensajes WhatsApp enviados Pestaña data: Campaña(cid:9)Referencia(cid:9)DNI(cid:9)Plantilla(cid:9)Fecha envio(cid:9)Teléfono origen(cid:9)Télefono destino(cid:9)Enviado(cid:9)Respuesta API(cid:9)Fecha Epiron(cid:9)Resultado Epiron
- **Prueba:** `DescargasPruebas#generarElReporteDeTotalesDeWhatsApp`  (etiquetas: descargas)
- **Lo que valida el codigo:** El reporte de totales de WhatsApp trae sus dos hojas
- **Correr solo este caso:** `mvn test -Dtest='DescargasPruebas#generarElReporteDeTotalesDeWhatsApp'`
- **Pasos que ejecuta:** `irAlMenu` -> `abrir` -> `elegirElTipoDeReporte` -> `elegirElRangoDeFechas` -> `generarElReporte`
- **Verificaciones:** 2

```java
        entrar().irAlMenu("Reportes");
        new PaginaReportes().abrir()
                .elegirElTipoDeReporte("Reporte totales mensajes WhatsApp")
                .elegirElRangoDeFechas()
                .generarElReporte();

        Path excel = Descargas.esperarArchivo(".xlsx");
        debeTenerLasColumnas(excel, "totales", "amex.excel.reporte.whatsapp.totales");
        debeTenerLasColumnas(excel, "data", "amex.excel.reporte.whatsapp");
```

## PF_CP_159 — Validación de la pantalla del menú Dashboard Firmas

- **Modulo:** Expediente pantalla Dashboard Firmas
- **Lo que pide la matriz:** Muestra la pantalla de una gráfica con el total de firmas,Firmas concluidas Firmas en proceso Firmas disponibles Firmas expiradas Firmas canceladas El sistemFiarm mause csotrna eerlr otírtulo que
- **Prueba:** `NavegacionPruebas#pfCp159PantallaDashboard`  (etiquetas: navegacion)
- **Lo que valida el codigo:** Pantalla de Dashboard de firmas
- **Correr solo este caso:** `mvn test -Dtest='NavegacionPruebas#pfCp159PantallaDashboard'`
- **Pasos que ejecuta:** `irAlMenu` -> `laDireccionDebeContener`
- **Verificaciones:** 1

```java
        inicio.irAlMenu("Dashboard").laDireccionDebeContener("fad-dashboard");
```

## PF_CP_160 — Validación de la pantalla del menú Dashboard Firmas

- **Modulo:** Expediente pantalla Dashboard Firmas
- **Lo que pide la matriz:** representa de cada color y la cantidad de cada uno.
- **Prueba:** `TasasCostosYReportesPruebas#pfCp160GraficaDelDashboard`  (etiquetas: consultas)
- **Lo que valida el codigo:** El dashboard muestra la grafica con sus indicadores
- **Correr solo este caso:** `mvn test -Dtest='TasasCostosYReportesPruebas#pfCp160GraficaDelDashboard'`
- **Pasos que ejecuta:** `irAlMenu` -> `abrir` -> `debeVerseLaGrafica` -> `debeVerseElTexto`
- **Verificaciones:** 4

```java
        inicio.irAlMenu("Dashboard");

        new PaginaDashboard().abrir()
                .debeVerseLaGrafica()
                .debeVerseElTexto("Total de firmas")
                .debeVerseElTexto("Firmas concluidas")
                .debeVerseElTexto("Firmas en proceso");
```

# Casos que no vienen de la matriz

## VAL_001

- **Prueba:** `LoginPruebas#val001AmbosCamposVacios`  (etiquetas: login)
- **Lo que valida el codigo:** Ambos campos vacios: mensaje de obligatorio y boton deshabilitado
- **Correr solo este caso:** `mvn test -Dtest='LoginPruebas#val001AmbosCamposVacios'`
- **Pasos que ejecuta:** `tocarLosDosCamposYSalir` -> `seMuestraElMensaje` -> `botonIniciarSesionHabilitado`
- **Verificaciones:** 2

```java
        PaginaLogin login = new PaginaLogin();
        login.tocarLosDosCamposYSalir();
        Assert.assertTrue(login.seMuestraElMensaje(PaginaLogin.TEXTO_CAMPO_OBLIGATORIO),
                "Falta el mensaje \"" + PaginaLogin.TEXTO_CAMPO_OBLIGATORIO + "\".");
        Assert.assertFalse(login.botonIniciarSesionHabilitado(),
                "Con los campos vacios el boton debe estar deshabilitado.");
```

## VAL_002

- **Prueba:** `LoginPruebas#val002SoloUsuario`  (etiquetas: login)
- **Lo que valida el codigo:** Solo el usuario capturado
- **Correr solo este caso:** `mvn test -Dtest='LoginPruebas#val002SoloUsuario'`
- **Pasos que ejecuta:** `escribirUsuario` -> `botonIniciarSesionHabilitado`
- **Verificaciones:** 1

```java
        PaginaLogin login = new PaginaLogin();
        login.escribirUsuario(Configuracion.usuario());
        Assert.assertFalse(login.botonIniciarSesionHabilitado(),
                "Sin contrasena el boton debe seguir deshabilitado.");
```

## VAL_003

- **Prueba:** `LoginPruebas#val003SoloContrasena`  (etiquetas: login)
- **Lo que valida el codigo:** Solo la contrasena capturada
- **Correr solo este caso:** `mvn test -Dtest='LoginPruebas#val003SoloContrasena'`
- **Pasos que ejecuta:** `escribirContrasena` -> `botonIniciarSesionHabilitado`
- **Verificaciones:** 1

```java
        PaginaLogin login = new PaginaLogin();
        login.escribirContrasena("Cualquiera123");
        Assert.assertFalse(login.botonIniciarSesionHabilitado(),
                "Sin usuario el boton debe seguir deshabilitado.");
```

## VAL_004

- **Prueba:** `LoginPruebas#val004FormatoDeCorreoInvalido`  (etiquetas: login)
- **Lo que valida el codigo:** Formato de correo invalido
- **Correr solo este caso:** `mvn test -Dtest='LoginPruebas#val004FormatoDeCorreoInvalido'`
- **Pasos que ejecuta:** `escribirUsuario` -> `escribirContrasena` -> `seMuestraElMensaje` -> `botonIniciarSesionHabilitado`
- **Verificaciones:** 2

```java
        PaginaLogin login = new PaginaLogin();
        login.escribirUsuario("correo-sin-arroba");
        login.escribirContrasena("Cualquiera123");
        Assert.assertTrue(login.seMuestraElMensaje(PaginaLogin.TEXTO_CORREO_INVALIDO),
                "Falta el mensaje \"" + PaginaLogin.TEXTO_CORREO_INVALIDO + "\".");
        Assert.assertFalse(login.botonIniciarSesionHabilitado(),
                "Con un correo invalido el boton debe estar deshabilitado.");
```

## DEF_01

- **Prueba:** `LoginPruebas#def01CorreoConSignoMas`  (etiquetas: login, defecto_conocido)
- **Lo que valida el codigo:** El correo con el signo + debe ser aceptado
- **Correr solo este caso:** `mvn test -Dtest='LoginPruebas#def01CorreoConSignoMas'`
- **Pasos que ejecuta:** `escribirUsuario` -> `escribirContrasena` -> `seMuestraElMensaje` -> `botonIniciarSesionHabilitado`
- **Verificaciones:** 2

```java
        PaginaLogin login = new PaginaLogin();
        login.escribirUsuario("qa.prueba+amex@na-at.com");
        login.escribirContrasena("Cualquiera123");
        Assert.assertFalse(login.seMuestraElMensaje(PaginaLogin.TEXTO_CORREO_INVALIDO),
                "DEF-01: se rechaza un correo valido que contiene \"+\".");
        Assert.assertTrue(login.botonIniciarSesionHabilitado(),
                "DEF-01: el boton queda deshabilitado con un correo valido.");
```

## SEG_001

- **Prueba:** `NavegacionPruebas#seg001LaSesionSeMantieneAlRecargar`  (etiquetas: navegacion)
- **Lo que valida el codigo:** La sesion se mantiene al recargar la pantalla
- **Correr solo este caso:** `mvn test -Dtest='NavegacionPruebas#seg001LaSesionSeMantieneAlRecargar'`
- **Pasos que ejecuta:** `irAlMenu` -> `recargarLaPantalla` -> `laSesionSigueAbierta`
- **Verificaciones:** 1

```java
        inicio.irAlMenu("Usuarios").recargarLaPantalla();
        Assert.assertTrue(inicio.laSesionSigueAbierta(),
                "La sesion se perdio al recargar la pantalla.");
```

# Y si un caso sale FALLIDO

1. Lee el `Motivo:` que imprime la consola: dice que se esperaba y que encontro.
2. Mira la captura en `resultados/` y repite el caso con `-Damex.headless=false`.
3. Si la aplicacion cambio a proposito, ajusta el valor esperado en
   `configuracion.properties` (o el renglon de la tabla del caso).
4. Si es un defecto, no cambies la expectativa: etiqueta el caso como
   `defecto_conocido` y reportalo.
