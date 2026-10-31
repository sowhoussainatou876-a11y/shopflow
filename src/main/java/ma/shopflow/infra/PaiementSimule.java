package ma.shopflow.infra;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.stereotype.Component;

import ma.shopflow.commande.PaiementGateway;
import ma.shopflow.commande.ResultatPaiement;

/**
 * Banque simulée, en attendant le contrat avec la banque partenaire :
 * elle accepte tout paiement jusqu'à 10 000 DH, plafond des cartes de test.
 */
@Component
public class PaiementSimule implements PaiementGateway {

    static final BigDecimal PLAFOND = new BigDecimal("10000.00");

    @Override
    public ResultatPaiement debiter(String clientId, BigDecimal montant) {
        if (montant.compareTo(PLAFOND) > 0) {
            return ResultatPaiement.refuse("Plafond de la carte dépassé");
        }
        return ResultatPaiement.accepte("PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
    }
}
