package ma.shopflow.panier;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Un produit du catalogue ShopFlow.
 */
public record Produit(String reference, String nom, BigDecimal prixUnitaire) {

    public Produit {
        Objects.requireNonNull(reference, "La référence est obligatoire");
        Objects.requireNonNull(nom, "Le nom est obligatoire");
        if (prixUnitaire == null || prixUnitaire.signum() <= 0) {
            throw new IllegalArgumentException("Le prix doit être strictement positif");
        }
    }
}