package ma.shopflow.catalogue;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import ma.shopflow.commande.RuptureDeStockException;
import ma.shopflow.commande.StockService;

/**
 * Implémentation du StockService du TP2, adossée à la table produit.
 */
@Component
@Transactional
public class StockJpaAdapter implements StockService {

    private final ProduitJpaRepository produits;

    public StockJpaAdapter(ProduitJpaRepository produits) {
        this.produits = produits;
    }

    @Override
    public boolean estDisponible(String reference, int quantite) {
        return produits.findById(reference)
                .map(produit -> produit.getStock() >= quantite)
                .orElse(false);
    }

    @Override
    public void reserver(String reference, int quantite) {
        if (produits.decrementerStock(reference, quantite) == 0) {
            throw produits.existsById(reference)
                    ? new RuptureDeStockException(reference)
                    : new ProduitInconnuException(reference);
        }
    }

    @Override
    public void liberer(String reference, int quantite) {
        if (produits.incrementerStock(reference, quantite) == 0) {
            throw new ProduitInconnuException(reference);
        }
    }
}