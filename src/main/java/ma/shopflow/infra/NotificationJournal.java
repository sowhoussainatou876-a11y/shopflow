package ma.shopflow.infra;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import ma.shopflow.commande.Commande;
import ma.shopflow.commande.NotificationService;

/**
 * Envoi d'e-mails simulé : le message est écrit dans le journal de
 * l'application.
 */
@Component
public class NotificationJournal implements NotificationService {

    private static final Logger LOG = LoggerFactory.getLogger(NotificationJournal.class);

    @Override
    public void envoyerConfirmation(String email, Commande commande) {
        LOG.info("E-mail à {} : votre commande {} de {} DH est confirmée", email, commande.id(), commande.total());
    }
}
