package ma.shopflow.fidelite;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Calcule les points de fidélité gagnés par une commande.
 */
public class CalculateurFidelite {

    private static final BigDecimal MONTANT_PAR_POINT = BigDecimal.TEN;

    static final int PLAFOND_PAR_COMMANDE = 500;

    public int points(BigDecimal montant) {
        return points(montant, NiveauFidelite.STANDARD);
    }

    public int points(BigDecimal montant, NiveauFidelite niveau) {
        if (montant.signum() < 0) {
            throw new IllegalArgumentException("Le montant ne peut pas être négatif");
        }
        return Math.min(pointsDeBase(montant) * niveau.multiplicateur(), PLAFOND_PAR_COMMANDE);
    }

    private int pointsDeBase(BigDecimal montant) {
        return montant.divide(MONTANT_PAR_POINT, 0, RoundingMode.DOWN).intValueExact();
    }
}