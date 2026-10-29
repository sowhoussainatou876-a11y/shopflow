package ma.shopflow.panier;

import java.math.BigDecimal;

/**
 * Une ligne du panier : un produit et la quantité commandée.
 */
public record LignePanier(Produit produit, int quantite) {

    public BigDecimal sousTotal() {
        return produit.prixUnitaire().multiply(BigDecimal.valueOf(quantite));
    }
}