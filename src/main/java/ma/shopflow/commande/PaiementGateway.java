package ma.shopflow.commande;

import java.math.BigDecimal;

/**
 * La passerelle de paiement de la banque partenaire.
 */
public interface PaiementGateway {

    ResultatPaiement debiter(String clientId, BigDecimal montant);
}
