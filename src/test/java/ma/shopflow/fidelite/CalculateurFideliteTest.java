package ma.shopflow.fidelite;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

@DisplayName("Points de fidélité")
class CalculateurFideliteTest {

    private final CalculateurFidelite calculateur = new CalculateurFidelite();

    @Test
    @DisplayName("F1 : 100 DH rapportent 10 points")
    void centDirhamsRapportentDixPoints() {
        assertThat(calculateur.points(new BigDecimal("100.00"))).isEqualTo(10);
    }

    @ParameterizedTest(name = "{0} DH -> {1} points")
    @CsvSource({
            "0.00, 0",
            "9.99, 0",
            "10.00, 1",
            "99.99, 9",
            "250.00, 25"
    })
    @DisplayName("F1 : un point par tranche entière de 10 DH")
    void unPointParTrancheEntiereDeDixDirhams(String montant, int pointsAttendus) {
        assertThat(calculateur.points(new BigDecimal(montant))).isEqualTo(pointsAttendus);
    }

    @Test
    @DisplayName("F2 : un client GOLD gagne deux fois plus de points")
    void unClientGoldGagneDeuxFoisPlusDePoints() {
        assertThat(calculateur.points(new BigDecimal("250.00"), NiveauFidelite.GOLD)).isEqualTo(50);
    }

    @Test
    @DisplayName("F4 : un montant négatif est refusé")
    void unMontantNegatifEstRefuse() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> calculateur.points(new BigDecimal("-20.00")))
                .withMessage("Le montant ne peut pas être négatif");
    }

    @ParameterizedTest(name = "{0} DH en {1} -> {2} points")
    @CsvSource({
            "6000.00, STANDARD, 500",
            "2400.00, GOLD, 480",
            "3000.00, GOLD, 500"
    })
    @DisplayName("F3 : les points sont plafonnés à 500 par commande, après le multiplicateur")
    void lesPointsSontPlafonnes(String montant, NiveauFidelite niveau, int pointsAttendus) {
        assertThat(calculateur.points(new BigDecimal(montant), niveau)).isEqualTo(pointsAttendus);
    }
}
