#!/usr/bin/env bash
# Prueba de no afectacion: corre una suite, guarda el resumen de casos y lo compara
# contra la corrida anterior, para ver si algo cambio despues de un cambio tecnico.
#
#   ./herramientas/no_afectacion.sh              # suite regresion
#   ./herramientas/no_afectacion.sh humo         # otra suite
#
# Los resultados quedan en resultados/no_afectacion/.
set -u

SUITE="${1:-regresion}"
CARPETA="resultados/no_afectacion"
AHORA="$CARPETA/${SUITE}_$(date +%Y%m%d_%H%M%S)"
ANTERIOR="$CARPETA/${SUITE}_referencia.txt"

mkdir -p "$CARPETA"

echo "Corriendo la suite $SUITE..."
mvn test -Dsuite="$SUITE" | tee "$AHORA.log"

# Del log solo interesa el estado de cada caso, sin los tiempos (cambian siempre).
grep -E "^\[(PF_CP|VAL|DEF|SEG)" "$AHORA.log" \
    | sed -E 's/ \([0-9]+[.,][0-9]+ s\)$//' | sort -u > "$AHORA.txt"

if [ ! -f "$ANTERIOR" ]; then
    cp "$AHORA.txt" "$ANTERIOR"
    echo
    echo "Se guardo esta corrida como referencia: $ANTERIOR"
    echo "Vuelve a correr el script despues del cambio para comparar."
    exit 0
fi

echo
echo "Diferencias contra la referencia ($ANTERIOR):"
if diff -u "$ANTERIOR" "$AHORA.txt" > "$AHORA.diff"; then
    echo "NINGUNA: los mismos casos con el mismo resultado. No hay afectacion."
    exit 0
fi

cat "$AHORA.diff"
echo
echo "Revisa cada diferencia: '-' es como estaba antes y '+' como quedo ahora."
echo "Si el resultado nuevo es el correcto, actualiza la referencia con:"
echo "  cp $AHORA.txt $ANTERIOR"
exit 1
