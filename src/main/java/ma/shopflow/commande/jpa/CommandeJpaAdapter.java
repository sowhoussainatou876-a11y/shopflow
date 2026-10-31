package ma.shopflow.commande.jpa;

import org.springframework.stereotype.Component;

import ma.shopflow.commande.Commande;
import ma.shopflow.commande.CommandeRepository;

/**
 * Implémentation du CommandeRepository du TP2, adossée aux tables commande et
 * ligne_commande.
 */
@Component
public class CommandeJpaAdapter implements CommandeRepository {

    private final CommandeJpaRepository jpa;

    public CommandeJpaAdapter(CommandeJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Commande enregistrer(Commande commande) {
        jpa.save(new CommandeEntity(commande.id(), commande.clientId(), commande.total(),
                commande.referencePaiement(), commande.statut(),
                commande.lignes().stream()
                        .map(l -> new LigneCommandeEmbeddable(l.produit().reference(), l.quantite(),
                                l.produit().prixUnitaire()))
                        .toList()));
        return commande;
    }
}
