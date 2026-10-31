package ma.shopflow.catalogue;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;

import ma.shopflow.TestcontainersConfiguration;

@DataJpaTest
@Import(TestcontainersConfiguration.class)
@DisplayName("Catalogue PostgreSQL")
class ProduitJpaRepositoryTest {

    @Autowired
    ProduitJpaRepository produits;

    @Test
    @DisplayName("Flyway a créé le schéma et chargé les 6 produits du catalogue")
    void flywayAChargeLeCatalogue() {
        assertThat(produits.count()).isEqualTo(6);
        assertThat(produits.findById("CLAV-01"))
                .get()
                .extracting(ProduitEntity::getNom, ProduitEntity::getStock)
                .containsExactly("Clavier mécanique", 25);
    }

    @Test
    @DisplayName("la recherche ignore la casse et les accents")
    void laRechercheIgnoreLaCasseEtLesAccents() {
        assertThat(produits.rechercher("ECRAN"))
                .extracting(ProduitEntity::getReference)
                .containsExactly("ECRA-04");
        assertThat(produits.rechercher("reduction"))
                .extracting(ProduitEntity::getNom)
                .containsExactly("Casque à réduction de bruit");
    }

    @Test
    @DisplayName("la base refuse un stock négatif")
    void laBaseRefuseUnStockNegatif() {
        produits.saveAndFlush(new ProduitEntity("TEST-99", "Produit de test", new BigDecimal("10.00"), 3));
        ProduitEntity produit = produits.findById("TEST-99").orElseThrow();
        produit.setStock(-1);

        assertThatThrownBy(() -> produits.saveAndFlush(produit))
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessageContaining("produit_stock_check");
    }
}