package ma.shopflow.commande;

public class RuptureDeStockException extends RuntimeException {

    public RuptureDeStockException(String reference) {
        super("Produit indisponible : " + reference);
    }
}
