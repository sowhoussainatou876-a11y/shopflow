package ma.shopflow.acceptation;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

/**
 * Une horloge que les scénarios règlent à la date de leur choix (« nous sommes
 * le 30/11/2026 »).
 */
public class HorlogeDeTest extends Clock {

    private static final ZoneId CASABLANCA = ZoneId.of("Africa/Casablanca");
    private Instant maintenant = Instant.now();

    public void regler(LocalDate date) {
        maintenant = date.atTime(10, 0).atZone(CASABLANCA).toInstant();
    }

    @Override
    public ZoneId getZone() {
        return CASABLANCA;
    }

    @Override
    public Clock withZone(ZoneId zone) {
        return this;
    }

    @Override
    public Instant instant() {
        return maintenant;
    }
}
