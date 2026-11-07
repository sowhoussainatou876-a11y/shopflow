package ma.shopflow.panier;

import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("Produit")
class ProduitTest {

    @ParameterizedTest(name = "un prix de {0} DH est refusé")
    @NullSource
    @ValueSource(strings = { "0.00", "-10.00" })
    void unPrixNulOuNegatifEstRefuse(BigDecimal prix) {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new Produit("TEST-01", "Produit de test", prix))
                .withMessage("Le prix doit être strictement positif");
    }
}
