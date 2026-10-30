package ma.shopflow.commande;

/**
 * Le stockage des commandes.
 */
public interface CommandeRepository {

    Commande enregistrer(Commande commande);
}