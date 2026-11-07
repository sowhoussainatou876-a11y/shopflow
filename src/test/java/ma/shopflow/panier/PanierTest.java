package ma.shopflow.panier;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.math.BigDecimal;

import java.util.NoSuchElementException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@DisplayName("Panier ShopFlow")
class PanierTest {

    static final Produit CLAVIER = new Produit("CLAV-01", "Clavier mécanique", new BigDecimal("450.00"));
    static final Produit SOURIS = new Produit("SOUR-02", "Souris sans fil", new BigDecimal("120.00"));
    static final Produit CABLE = new Produit("CABL-03", "Câble USB-C", new BigDecimal("19.99"));

    private Panier panier;

    @BeforeEach
    void creerUnPanierVide() {
        panier = new Panier();
    }

    @Nested
    @DisplayName("R1 à R3 : ajout de produits")
    class AjoutDeProduits {

        @Test
        @DisplayName("un nouveau panier est vide")
        void unNouveauPanierEstVide() {
            assertTrue(panier.estVide());
            assertEquals(0, panier.nombreArticles());
        }

        @Test
        @DisplayName("R1 : une quantité nulle est refusée")
        void uneQuantiteNulleEstRefusee() {
            IllegalArgumentException erreur = assertThrows(IllegalArgumentException.class,
                    () -> panier.ajouter(SOURIS, 0));

            assertEquals("La quantité doit être supérieure ou égale à 1", erreur.getMessage());
            assertTrue(panier.estVide());
        }

        @Test
        @DisplayName("R2 : ajouter deux fois le même produit cumule les quantités")
        void ajouterDeuxFoisLeMemeProduitCumuleLesQuantites() {
            panier.ajouter(SOURIS, 2);
            panier.ajouter(SOURIS, 3);

            assertAll("ligne unique de 5 souris",
                    () -> assertEquals(1, panier.lignes().size()),
                    () -> assertEquals(5, panier.lignes().get(0).quantite()),
                    () -> assertEquals(5, panier.nombreArticles()));
        }

        @Test
        @DisplayName("R3 : 10 unités d'un même produit sont acceptées")
        void dixUnitesSontAcceptees() {
            assertDoesNotThrow(() -> panier.ajouter(CABLE, 10));
        }

        @Test
        @DisplayName("R3 : la 11e unité est refusée et le panier reste inchangé")
        void laOnziemeUniteEstRefusee() {
            panier.ajouter(CABLE, 8);

            assertThrows(QuantiteMaximaleDepasseeException.class, () -> panier.ajouter(CABLE, 3));
            assertEquals(8, panier.nombreArticles());
        }
    }

    @Nested
    @DisplayName("R4 : sous-total")
    class SousTotal {

        @Test
        @DisplayName("le sous-total est la somme des lignes")
        void leSousTotalEstLaSommeDesLignes() {
            panier.ajouter(CLAVIER, 1);
            panier.ajouter(SOURIS, 2);

            assertMontant("690.00", panier.sousTotal());
        }

        @Test
        @DisplayName("les centimes sont conservés")
        void lesCentimesSontConserves() {
            panier.ajouter(CABLE, 3);

            assertMontant("59.97", panier.sousTotal());
        }
    }

    @Nested
    @DisplayName("R5 : frais de livraison")
    class FraisDeLivraison {

        @Test
        @DisplayName("un panier vide ne paie pas de livraison")
        void unPanierVideNePaiePasDeLivraison() {
            assertMontant("0.00", panier.fraisLivraison());
        }

        @ParameterizedTest(name = "sous-total {0} DH -> livraison {1} DH")
        @CsvSource({
                "100.00, 30.00",
                "499.99, 30.00",
                "500.00, 0.00",
                "500.01, 0.00",
                "1200.00, 0.00"
        })
        void laLivraisonEstOfferteAPartirDe500DH(String sousTotal, String fraisAttendus) {
            panier.ajouter(new Produit("TEST-01", "Produit de test", new BigDecimal(sousTotal)), 1);

            assertMontant(fraisAttendus, panier.fraisLivraison());
        }
    }

    @Nested
    @DisplayName("R6 et R7 : remise et total")
    class RemiseEtTotal {

        @Test
        @DisplayName("sans code promo, total = sous-total + livraison")
        void sansCodePromoLeTotalInclutLaLivraison() {
            panier.ajouter(SOURIS, 1);

            assertMontant("150.00", panier.total());
        }

        @Test
        @DisplayName("R6 : la remise porte sur le sous-total, jamais sur la livraison")
        void laRemiseNePortePasSurLaLivraison() {
            panier.ajouter(SOURIS, 1);
            panier.appliquerCodePromo(new CodePromo("BIENVENUE10", 10));

            assertAll(
                    () -> assertMontant("12.00", panier.remise()),
                    () -> assertMontant("30.00", panier.fraisLivraison()),
                    () -> assertMontant("138.00", panier.total()));
        }

        @Test
        @DisplayName("R6 : la remise est arrondie au centime")
        void laRemiseEstArrondieAuCentime() {
            panier.ajouter(CABLE, 3);
            panier.appliquerCodePromo(new CodePromo("ETE15", 15));

            assertMontant("9.00", panier.remise());
        }

        @Test
        @DisplayName("R5 et R6 : le seuil de livraison s'évalue avant la remise")
        void leSeuilDeLivraisonSEvalueAvantLaRemise() {
            panier.ajouter(CLAVIER, 1);
            panier.ajouter(SOURIS, 1);
            panier.appliquerCodePromo(new CodePromo("BIENVENUE10", 10));

            assertAll(
                    () -> assertMontant("0.00", panier.fraisLivraison()),
                    () -> assertMontant("513.00", panier.total()));
        }
    }

    @Nested
    @DisplayName("R8 : code promo")
    class CodePromoSurPanier {

        @Test
        @DisplayName("un code promo est refusé sur un panier vide")
        void unCodePromoEstRefuseSurUnPanierVide() {
            CodePromo bienvenue = new CodePromo("BIENVENUE10", 10);

            assertThrows(IllegalStateException.class, () -> panier.appliquerCodePromo(bienvenue));
        }
    }

    @Nested
    @DisplayName("Retrait de produits")
    class RetraitDeProduits {

        @Test
        @DisplayName("retirer un produit supprime sa ligne")
        void retirerUnProduitSupprimeSaLigne() {
            panier.ajouter(SOURIS, 2);
            panier.ajouter(CABLE, 1);

            panier.retirer("SOUR-02");

            assertEquals(1, panier.lignes().size());
            assertEquals(1, panier.nombreArticles());
        }

        @Test
        @DisplayName("retirer un produit absent est une erreur")
        void retirerUnProduitAbsentEstUneErreur() {
            NoSuchElementException erreur = assertThrows(NoSuchElementException.class,
                    () -> panier.retirer("SOUR-02"));

            assertEquals("Produit absent du panier : SOUR-02", erreur.getMessage());
        }
    }

    /**
     * Compare deux montants par leur valeur, sans tenir compte du nombre de
     * décimales.
     */
    static void assertMontant(String attendu, BigDecimal reel) {
        if (new BigDecimal(attendu).compareTo(reel) != 0) {
            fail("Montant attendu : " + attendu + " DH, obtenu : " + reel + " DH");
        }
    }
}