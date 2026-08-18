package com.amex.pf.datos;

import java.util.List;

import com.amex.pf.base.Configuracion;

/**
 * Lo que la matriz espera del detalle de una solicitud segun su estatus
 * (PF_CP_130 a PF_CP_141).
 *
 * El estatus y las pestanas habilitadas de cada caso NO estan en el codigo: se
 * leen de configuracion.properties (amex.expediente.PF_CP_XXX.*), para que
 * cualquier tester pueda ajustar la regla sin programar.
 */
public final class EstadoDelExpediente {

    private final String caso;
    private final String estatus;
    private final List<String> pestanasHabilitadas;

    private EstadoDelExpediente(String caso, String estatus, List<String> pestanasHabilitadas) {
        this.caso = caso;
        this.estatus = estatus;
        this.pestanasHabilitadas = pestanasHabilitadas;
    }

    /** @param caso identificador de la matriz, por ejemplo "PF_CP_130". */
    public static EstadoDelExpediente delCaso(String caso) {
        String estatus = Configuracion.obtener("amex.expediente." + caso + ".estatus");
        if (estatus.isBlank()) {
            throw new IllegalStateException("Falta amex.expediente." + caso
                    + ".estatus en configuracion.properties.");
        }
        return new EstadoDelExpediente(caso, estatus,
                List.of(Configuracion.lista("amex.expediente." + caso + ".habilitadas")));
    }

    /** Todas las pestanas del detalle, en el orden en que las pinta la pantalla. */
    public static List<String> todasLasPestanas() {
        return List.of(Configuracion.lista("amex.expediente.pestanas"));
    }

    public String caso() {
        return caso;
    }

    public String estatus() {
        return estatus;
    }

    public boolean debeEstarHabilitada(String pestana) {
        return pestanasHabilitadas.contains(pestana);
    }

    public List<String> pestanasHabilitadas() {
        return pestanasHabilitadas;
    }
}
