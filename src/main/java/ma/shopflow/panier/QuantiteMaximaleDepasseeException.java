package ma.shopflow.panier;

/**
 * Levée quand un client dépasse la quantité autorisée pour un même produit.
 */
public class QuantiteMaximaleDepasseeException extends RuntimeException {

    public QuantiteMaximaleDepasseeException(String reference, int maximum) {
        super("Quantité maximale dépassée pour " + reference + " (maximum " + maximum + ")");
    }
}