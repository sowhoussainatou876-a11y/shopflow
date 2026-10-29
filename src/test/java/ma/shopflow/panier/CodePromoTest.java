package ma.shopflow.panier;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("Code promo")
class CodePromoTest {

    @ParameterizedTest(name = "{0} % est accepté")
    @ValueSource(ints = { 1, 25, 50 })
    void unPourcentageEntre1Et50EstAccepte(int pourcentage) {
        assertDoesNotThrow(() -> new CodePromo("TEST", pourcentage));
    }

    @ParameterizedTest(name = "{0} % est refusé")
    @ValueSource(ints = { -5, 0, 51, 100 })
    void unPourcentageHorsBornesEstRefuse(int pourcentage) {
        assertThrows(IllegalArgumentException.class, () -> new CodePromo("TEST", pourcentage));
    }
}