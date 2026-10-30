package ma.shopflow.commande;

import ma.shopflow.panier.LignePanier;
import ma.shopflow.panier.Panier;

/**
 * Transforme le panier d'un client en commande confirmée.
 */
public class ServiceCommande {

    private static final System.Logger LOG = System.getLogger(ServiceCommande.class.getName());

    private final StockService stock;
    private final PaiementGateway paiement;
    private final CommandeRepository commandes;
    private final NotificationService notifications;

    public ServiceCommande(StockService stock, PaiementGateway paiement,
            CommandeRepository commandes, NotificationService notifications) {
        this.stock = stock;
        this.paiement = paiement;
        this.commandes = commandes;
        this.notifications = notifications;
    }

    public Commande passerCommande(Client client, Panier panier) {
        if (panier.estVide()) {
            throw new IllegalArgumentException("Le panier est vide");
        }
        for (LignePanier ligne : panier.lignes()) {
            if (!stock.estDisponible(ligne.produit().reference(), ligne.quantite())) {
                throw new RuptureDeStockException(ligne.produit().reference());
            }
        }
        for (LignePanier ligne : panier.lignes()) {
            stock.reserver(ligne.produit().reference(), ligne.quantite());
        }
        ResultatPaiement resultat = paiement.debiter(client.id(), panier.total());
        if (!resultat.accepte()) {
            for (LignePanier ligne : panier.lignes()) {
                stock.liberer(ligne.produit().reference(), ligne.quantite());
            }
            throw new PaiementRefuseException(resultat.motifRefus());
        }
        Commande commande = commandes.enregistrer(
                Commande.confirmee(client.id(), panier.lignes(), panier.total(), resultat.reference()));
        try {
            notifications.envoyerConfirmation(client.email(), commande);
        } catch (RuntimeException e) {
            // la commande est payée : une panne d'e-mail ne doit pas l'annuler
            LOG.log(System.Logger.Level.WARNING,
                    "E-mail non envoyé pour la commande {0} : {1}", commande.id(), e.getMessage());
        }
        return commande;
    }
}