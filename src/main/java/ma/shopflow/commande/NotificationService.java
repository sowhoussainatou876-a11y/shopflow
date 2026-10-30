package ma.shopflow.commande;

/**
 * L'envoi des e-mails aux clients.
 */
public interface NotificationService {

    void envoyerConfirmation(String email, Commande commande);
}
