# Selenium + Java/TestNG vs. Robot Framework: qué cambia para el equipo

Existen las **dos versiones del mismo conjunto de casos**, con el mismo resultado
en DEV (14 correctos, 0 fallas, resto omitido por permisos o hallazgos abiertos):

- `amex-pf-selenium/` — Selenium 4 + Java 17 + TestNG + Maven.
- `amex-pf-automatizacion/` — Robot Framework 7 + Browser library (Playwright).

## Comparación

| Aspecto | Selenium + Java/TestNG | Robot Framework + Browser |
|---|---|---|
| Cómo se ve un caso | método Java con `Assert` | tabla de palabras clave en español |
| Quién puede modificarlo | quien sepa Java básico | cualquier tester |
| Casos repetitivos | `@DataProvider` (renglón por caso) | fila en la tabla del caso |
| Selectores centralizados | sí (`Selectores.java`) | sí (`selectores.resource`) |
| Espera de elementos | explícita, se programa (`WebDriverWait`) | automática en cada palabra clave |
| Reporte | Surefire / Allure (Allure necesita instalación aparte) | `log.html` y `report.html` incluidos, con captura del paso |
| Descargas de Excel/ZIP | se configura el perfil del navegador y se valida el archivo | soporte nativo de Playwright |
| Instalación | JDK + Maven + IDE | Python + `pip install` + `rfbrowser init` |
| Ecosistema en el mercado | muy amplio (más fácil contratar/apoyarse) | amplio en QA, menos en desarrollo |
| Líneas de código de esta suite | ~900 | ~450 |

## Cuándo conviene cada una

**Selenium + Java/TestNG** si el equipo va a contar con alguien con perfil
técnico estable, si la empresa ya estandarizó Java (mismo repositorio,
mismo CI, mismas revisiones que el resto del software) o si se busca el perfil
más común del mercado.

**Robot Framework** si el objetivo principal es que **varios testers sin
experiencia en programación** mantengan los casos día a día: ahí un caso nuevo es
una fila, no un método.

## Recomendación

Ambas cumplen el objetivo técnico. La decisión es de **perfil del equipo**, no de
capacidad de la herramienta:

- Si la mayoría del equipo no programa → Robot Framework, y quien tenga perfil
  técnico mantiene la capa de selectores y palabras clave.
- Si el equipo va a aprender Java o ya lo usa → esta versión Selenium, con la
  disciplina de mantener los Page Objects y nunca escribir un `By` en las pruebas.

Lo que **no** conviene es mantener las dos en paralelo: duplica el esfuerzo cada
vez que la aplicación cambia. Sirve elegir una y quedarse con la otra solo como
referencia.
