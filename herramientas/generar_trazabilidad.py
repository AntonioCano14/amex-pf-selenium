#!/usr/bin/env python3
"""Genera la trazabilidad y la guia caso por caso a partir de la matriz y del codigo.

Salidas:
  TRAZABILIDAD.md        tabla matriz vs. automatizacion
  TRAZABILIDAD.csv       lo mismo en CSV, para actualizar la matriz en Excel
  GUIA_CASO_POR_CASO.md  por cada caso: que pide la matriz, que hace el codigo,
                         los pasos que ejecuta y el comando para correrlo solo

Uso:
    python3 herramientas/generar_trazabilidad.py

Hay que volver a correrlo cada vez que se agregan o cambian casos.
"""

import csv
import os
import re
from collections import defaultdict

RAIZ = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
PRUEBAS = os.path.join(RAIZ, "src", "test", "java", "com", "amex", "pf", "pruebas")
MATRIZ = os.path.join(RAIZ, "datos", "matriz_funcional.csv")
SALIDA_MD = os.path.join(RAIZ, "TRAZABILIDAD.md")
SALIDA_CSV = os.path.join(RAIZ, "TRAZABILIDAD.csv")
SALIDA_GUIA = os.path.join(RAIZ, "GUIA_CASO_POR_CASO.md")

ID = r"PF_CP_\d+(?:[-/]\d+)*"
ID_INTERNO = r"(?:VAL|SEG|DEF)_\d+"

# Motivo por el que un caso de la matriz todavia no esta automatizado. Al automatizar
# un caso se borra su renglon de aqui.
MOTIVOS = {
    "PF_CP_005": "La matriz lo marca Cancelado.",
    "PF_CP_006": "La matriz lo marca Cancelado.",
    "PF_CP_007": "La matriz lo marca Cancelado.",
    "PF_CP_023": "Carga masiva: falta el layout oficial valido.",
    "PF_CP_024": "Carga masiva: falta el layout oficial invalido.",
    "PF_CP_025": "Carga masiva: falta el layout oficial invalido.",
    "PF_CP_031": "Repite PF_CP_012 (duplicado en la matriz).",
    "PF_CP_032": "Repite PF_CP_013 (duplicado en la matriz).",
    "PF_CP_033": "Repite PF_CP_014 (duplicado en la matriz).",
    "PF_CP_034": "Repite PF_CP_015 (duplicado en la matriz).",
    "PF_CP_035": "Repite PF_CP_016 (duplicado en la matriz).",
    "PF_CP_036": "Repite PF_CP_017 (duplicado en la matriz).",
    "PF_CP_037": "Repite PF_CP_018 (duplicado en la matriz).",
    "PF_CP_038": "Repite PF_CP_019 (duplicado en la matriz).",
    "PF_CP_103": "Modifica tasas vigentes: falta un periodo de prueba acordado.",
    "PF_CP_106": "Modifica tasas vigentes: falta un periodo de prueba acordado.",
    "PF_CP_110": "Crea una solicitud real: requiere ambiente o datos desechables.",
    "PF_CP_123": "Depende del check Condicionada a ingresos, que no existe hoy.",
}

# Casos que no vienen de la matriz y agregamos como red de seguridad.
EXTRA = {
    "VAL_": "Validacion de campos del login que la matriz no numera.",
    "SEG_": "Comportamiento de sesion.",
    "DEF_": "Defecto abierto de la aplicacion.",
}


IGNORAR_EN_LOS_PASOS = ("contains", "equals", "equalsIgnoreCase", "isEmpty", "isBlank",
                        "stream", "map", "toList", "size", "get", "trim", "length",
                        "forEach", "anyMatch", "filter", "add", "replace", "split")


def ids_de(texto):
    return re.findall(ID, texto)


def expandir(identificador):
    """Los IDs de las descripciones vienen en tres formas:

    PF_CP_121                     -> un caso
    PF_CP_042/043/044/045         -> varios casos (la forma recomendada)
    PF_CP_115-116                 -> rango corto y contiguo
    """
    if "/" in identificador:
        partes = identificador.split("/")
        return [partes[0]] + ["PF_CP_%03d" % int(n) for n in partes[1:]]
    if "-" in identificador:
        inicio, fin = identificador.split("-")
        numero = int(inicio.rsplit("_", 1)[1])
        if int(fin) - numero > 5:
            return []
        return ["PF_CP_%03d" % n for n in range(numero, int(fin) + 1)]
    return [identificador]


def leer_pruebas():
    """Un diccionario por cada @Test, con sus IDs y su codigo."""
    casos = []
    for archivo in sorted(os.listdir(PRUEBAS)):
        if not archivo.endswith(".java"):
            continue
        clase = archivo[:-5]
        fuente = open(os.path.join(PRUEBAS, archivo), encoding="utf-8").read()

        proveedores = defaultdict(list)
        filas = defaultdict(dict)
        for nombre, cuerpo in re.findall(
                r'@DataProvider\(name = "(\w+)"\)(.*?)\n    }', fuente, re.S):
            proveedores[nombre] = ids_de(cuerpo)
            for renglon in cuerpo.splitlines():
                for identificador in ids_de(renglon):
                    filas[nombre][identificador] = renglon.strip().rstrip(",")

        for anotacion, metodo, cuerpo in re.findall(
                r"@Test\((.*?)\)\s*\n\s*public void (\w+)\([^)]*\) \{\n(.*?)\n    }",
                fuente, re.S):
            descripcion = "".join(
                re.findall(r'"([^"]*)"', anotacion.split("description =")[-1])).strip()
            grupos = re.search(r'groups = (\{[^}]*\}|"[^"]*")', anotacion)
            proveedor = re.search(r'dataProvider = "(\w+)"', anotacion)
            ids = [i for bruto in ids_de(descripcion) for i in expandir(bruto)]
            if proveedor and proveedores[proveedor.group(1)]:
                # Cada fila del @DataProvider trae su propio ID; manda sobre el
                # rango que encabeza la descripcion.
                ids = proveedores[proveedor.group(1)]
            casos.append({
                "clase": clase,
                "metodo": metodo,
                "grupos": ", ".join(re.findall(r'"([^"]+)"', grupos.group(1))
                                    if grupos else []),
                "descripcion": descripcion,
                "ids": ids,
                "proveedor": proveedor.group(1) if proveedor else "",
                "filas": filas[proveedor.group(1)] if proveedor else {},
                "codigo": cuerpo,
            })
    return casos


def pasos_de(codigo):
    """Los metodos de pantalla que llama la prueba, en orden: son sus pasos.

    Se ignora lo que no es un paso en la aplicacion: los Assert, los valores que
    vienen de Configuracion y las utilidades de Java (todo lo que se llama sobre
    algo que empieza con mayuscula).
    """
    pasos = []
    for quien, llamada in re.findall(r"(?:(\w+)|\))\s*\.\s*(\w+)\(", codigo):
        if quien and quien[0].isupper():
            continue
        if llamada in pasos or llamada in IGNORAR_EN_LOS_PASOS:
            continue
        pasos.append(llamada)
    return pasos


def verificaciones_de(codigo):
    return len(re.findall(r"Assert\.\w+|debe\w+\(|Debe\w+\(", codigo))


def limpiar(texto):
    return " ".join(texto.split()).replace("|", "/")


def sin_el_id(descripcion):
    return re.sub(r"^(%s|%s)\s*" % (ID, ID_INTERNO), "", descripcion).strip()


def escribir_csv(renglones):
    with open(SALIDA_CSV, "w", encoding="utf-8", newline="") as salida:
        escritor = csv.DictWriter(salida, fieldnames=list(renglones[0].keys()))
        escritor.writeheader()
        escritor.writerows(renglones)


def escribir_trazabilidad(renglones, extra):
    automatizados = [r for r in renglones if r["automatizado"] == "si"]
    with open(SALIDA_MD, "w", encoding="utf-8") as md:
        md.write("# Trazabilidad: matriz funcional vs. automatizacion\n\n")
        md.write("Generado por `herramientas/generar_trazabilidad.py`; no se edita a mano.\n")
        md.write("La explicacion de cada caso esta en `GUIA_CASO_POR_CASO.md`.\n\n")
        md.write("- Casos de la matriz: **%d**\n" % len(renglones))
        md.write("- Automatizados: **%d**\n" % len(automatizados))
        md.write("- Pendientes: **%d**\n\n" % (len(renglones) - len(automatizados)))
        md.write("Para correr un caso solo: `mvn test -Dtest='Clase#metodo'`.\n\n")
        md.write("| ID | Modulo | Caso de la matriz | Estatus matriz | Automatizado |"
                 " Prueba | Que valida el codigo | Etiquetas | Motivo si falta |\n")
        md.write("|---|---|---|---|---|---|---|---|---|\n")
        for r in renglones:
            md.write("| %s | %s | %s | %s | %s | `%s` | %s | %s | %s |\n" % (
                r["id"], r["modulo"], limpiar(r["caso de la matriz"]),
                r["estatus en la matriz"], r["automatizado"], r["prueba"],
                limpiar(r["que valida el codigo"]), r["etiquetas"],
                limpiar(r["motivo si falta"])))

        md.write("\n## Casos que no vienen de la matriz\n\n")
        md.write("| Prueba | Que valida | Etiquetas |\n|---|---|---|\n")
        for caso in extra:
            md.write("| `%s#%s` | %s | %s |\n" % (caso["clase"], caso["metodo"],
                                                  limpiar(sin_el_id(caso["descripcion"])),
                                                  caso["grupos"]))
        incompletos = [r["id"] for r in renglones if not r["caso de la matriz"].strip()]
        if incompletos:
            md.write("\n## Casos con datos incompletos en `datos/matriz_funcional.csv`\n\n")
            md.write("Al pasar la matriz del PDF a CSV estos casos quedaron sin nombre ni "
                     "estatus; hay que completarlos en el CSV:\n\n")
            md.write("%s\n" % ", ".join(incompletos))
        md.write("\nPrefijos: ")
        md.write("; ".join("`%s` %s" % (p, t) for p, t in EXTRA.items()))
        md.write("\n")


def escribir_guia(matriz, casos, por_id):
    por_matriz = {fila["id"]: fila for fila in matriz}
    with open(SALIDA_GUIA, "w", encoding="utf-8") as guia:
        guia.write(ENCABEZADO_GUIA)
        for identificador in sorted(por_matriz, key=lambda i: int(i.rsplit("_", 1)[1])):
            fila = por_matriz[identificador]
            implementaciones = por_id.get(identificador, [])
            guia.write("\n## %s%s\n\n" % (
                identificador,
                " — " + limpiar(fila["nombre"]) if fila["nombre"].strip() else ""))
            guia.write("- **Modulo:** %s\n" % fila["modulo"])
            if fila["esperado"].strip():
                guia.write("- **Lo que pide la matriz:** %s\n" % limpiar(fila["esperado"]))
            if not implementaciones:
                guia.write("- **Automatizado:** no. %s\n"
                           % MOTIVOS.get(identificador, "Pendiente de definir."))
                continue
            for caso in implementaciones:
                escribir_caso(guia, caso, identificador)
        guia.write("\n# Casos que no vienen de la matriz\n")
        for caso in casos:
            if not caso["ids"]:
                guia.write("\n## %s\n\n" % caso["descripcion"].split(" ")[0])
                escribir_caso(guia, caso, "")
        guia.write(PIE_GUIA)


def escribir_caso(guia, caso, identificador):
    guia.write("- **Prueba:** `%s#%s`  (etiquetas: %s)\n"
               % (caso["clase"], caso["metodo"], caso["grupos"] or "sin etiquetas"))
    guia.write("- **Lo que valida el codigo:** %s\n" % sin_el_id(caso["descripcion"]))
    guia.write("- **Correr solo este caso:** `mvn test -Dtest='%s#%s'`\n"
               % (caso["clase"], caso["metodo"]))
    if caso["filas"].get(identificador):
        guia.write("- **Renglon de la tabla para este caso:** `%s`\n"
                   % caso["filas"][identificador])
    if caso["proveedor"]:
        guia.write("- **Ojo:** este metodo cubre varios casos con la tabla "
                   "`@DataProvider(\"%s\")`; el comando los corre todos y la consola "
                   "imprime el ID de cada uno.\n" % caso["proveedor"])
    guia.write("- **Pasos que ejecuta:** %s\n" % " -> ".join(
        "`%s`" % paso for paso in pasos_de(caso["codigo"])))
    guia.write("- **Verificaciones:** %d\n\n" % verificaciones_de(caso["codigo"]))
    guia.write("```java\n%s\n```\n" % caso["codigo"].rstrip())


ENCABEZADO_GUIA = """# Guia caso por caso: que hace el codigo de cada caso

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
"""

PIE_GUIA = """
# Y si un caso sale FALLIDO

1. Lee el `Motivo:` que imprime la consola: dice que se esperaba y que encontro.
2. Mira la captura en `resultados/` y repite el caso con `-Damex.headless=false`.
3. Si la aplicacion cambio a proposito, ajusta el valor esperado en
   `configuracion.properties` (o el renglon de la tabla del caso).
4. Si es un defecto, no cambies la expectativa: etiqueta el caso como
   `defecto_conocido` y reportalo.
"""


def main():
    matriz = list(csv.DictReader(open(MATRIZ, encoding="utf-8")))
    casos = leer_pruebas()

    por_id = defaultdict(list)
    for caso in casos:
        for identificador in caso["ids"]:
            por_id[identificador].append(caso)

    renglones = []
    for fila in matriz:
        implementaciones = por_id.get(fila["id"], [])
        renglon = {
            "id": fila["id"],
            "modulo": fila["modulo"],
            "caso de la matriz": fila["nombre"],
            "estatus en la matriz": fila["estatus"],
            "automatizado": "si" if implementaciones else "no",
            "prueba": " ; ".join("%s#%s" % (c["clase"], c["metodo"])
                                 for c in implementaciones),
            "que valida el codigo": sin_el_id(implementaciones[0]["descripcion"])
                                    if implementaciones else "",
            "etiquetas": implementaciones[0]["grupos"] if implementaciones else "",
            "comando": "mvn test -Dtest='%s#%s'" % (implementaciones[0]["clase"],
                                                    implementaciones[0]["metodo"])
                       if implementaciones else "",
            "motivo si falta": "" if implementaciones
                               else MOTIVOS.get(fila["id"], "Pendiente de definir."),
        }
        renglones.append(renglon)

    escribir_csv(renglones)
    escribir_trazabilidad(renglones, [c for c in casos if not c["ids"]])
    escribir_guia(matriz, casos, por_id)

    automatizados = [r for r in renglones if r["automatizado"] == "si"]
    sobrantes = [i for i in MOTIVOS if i in por_id]
    if sobrantes:
        print("AVISO: ya estan automatizados y siguen en MOTIVOS: %s" % ", ".join(sobrantes))
    print("Casos de la matriz: %d | automatizados: %d | pendientes: %d"
          % (len(renglones), len(automatizados), len(renglones) - len(automatizados)))
    print("Escritos %s, %s y %s" % (SALIDA_MD, SALIDA_CSV, SALIDA_GUIA))


if __name__ == "__main__":
    main()
