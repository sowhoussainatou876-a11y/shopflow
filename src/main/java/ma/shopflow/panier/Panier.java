package ma.shopflow.panier;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Le panier d'un client ShopFlow.
 */
public class Panier {

    public static final int QUANTITE_MAX_PAR_PRODUIT = 10;
    public static final BigDecimal SEUIL_LIVRAISON_OFFERTE = new BigDecimal("500.00");
    public static final BigDecimal FRAIS_LIVRAISON = new BigDecimal("30.00");

    private final Map<String, LignePanier> lignes = new LinkedHashMap<>();
    private CodePromo codePromo;

    public void ajouter(Produit produit, int quantite) {
        if (quantite <= 0) {
            throw new IllegalArgumentException("La quantité doit être supérieure ou égale à 1");
        }
        LignePanier existante = lignes.get(produit.reference());
        int nouvelleQuantite = (existante == null ? 0 : existante.quantite()) + quantite;
        if (nouvelleQuantite > QUANTITE_MAX_PAR_PRODUIT) {
            throw new QuantiteMaximaleDepasseeException(produit.reference(), QUANTITE_MAX_PAR_PRODUIT);
        }
        lignes.put(produit.reference(), new LignePanier(produit, nouvelleQuantite));
    }

    public void retirer(String reference) {
        if (lignes.remove(reference) == null) {
            throw new NoSuchElementException("Produit absent du panier : " + reference);
        }
    }

    public boolean estVide() {
        return lignes.isEmpty();
    }

    public int nombreArticles() {
        return lignes.values().stream().mapToInt(LignePanier::quantite).sum();
    }

    public List<LignePanier> lignes() {
        return List.copyOf(new ArrayList<>(lignes.values()));
    }

    public BigDecimal sousTotal() {
        return lignes.values().stream()
                .map(LignePanier::sousTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    public void appliquerCodePromo(CodePromo code) {
        if (estVide()) {
            throw new IllegalStateException("Impossible d'appliquer un code promo sur un panier vide");
        }
        this.codePromo = code;
    }

    public BigDecimal remise() {
        if (codePromo == null) {
            return BigDecimal.ZERO.setScale(2);
        }
        return pourcentageDe(sousTotal(), codePromo.pourcentage());
    }

    public BigDecimal fraisLivraison() {
        if (estVide()) {
            return BigDecimal.ZERO.setScale(2);
        }
        return sousTotal().compareTo(SEUIL_LIVRAISON_OFFERTE) >= 0
                ? BigDecimal.ZERO.setScale(2)
                : FRAIS_LIVRAISON;
    }

    public BigDecimal total() {
        return sousTotal().subtract(remise()).add(fraisLivraison());
    }

    private static BigDecimal pourcentageDe(BigDecimal montant, int pourcentage) {
        return montant.multiply(BigDecimal.valueOf(pourcentage))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }
}