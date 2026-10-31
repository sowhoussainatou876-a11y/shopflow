package ma.shopflow.api;

import java.math.BigDecimal;

import ma.shopflow.commande.Commande;

/**
 * La réponse JSON à une commande acceptée.
 */
public record CommandeCreee(String id, String statut, BigDecimal total, String referencePaiement) {

    static CommandeCreee depuis(Commande commande) {
        return new CommandeCreee(commande.id(), commande.statut().name(), commande.total(),
                commande.referencePaiement());
    }
}
