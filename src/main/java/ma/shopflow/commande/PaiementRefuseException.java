package ma.shopflow.commande;

public class PaiementRefuseException extends RuntimeException {

    public PaiementRefuseException(String motif) {
        super("Paiement refusé : " + motif);
    }
}
