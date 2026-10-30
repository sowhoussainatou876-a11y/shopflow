package ma.shopflow.commande;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import ma.shopflow.panier.LignePanier;

/**
 * Une commande passée par un client, une fois le paiement accepté.
 */
public record Commande(String id, String clientId, List<LignePanier> lignes, BigDecimal total,
        String referencePaiement, StatutCommande statut) {

    public static Commande confirmee(String clientId, List<LignePanier> lignes, BigDecimal total,
            String referencePaiement) {
        return new Commande(UUID.randomUUID().toString(), clientId, List.copyOf(lignes), total,
                referencePaiement, StatutCommande.CONFIRMEE);
    }
}
