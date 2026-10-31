package ma.shopflow.infra;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ma.shopflow.commande.CommandeRepository;
import ma.shopflow.commande.NotificationService;
import ma.shopflow.commande.PaiementGateway;
import ma.shopflow.commande.ServiceCommande;
import ma.shopflow.commande.StockService;

/**
 * Assemble le ServiceCommande du TP2 avec ses implémentations réelles.
 */
@Configuration
public class CommandeConfiguration {

    @Bean
    ServiceCommande serviceCommande(StockService stock, PaiementGateway paiement,
            CommandeRepository commandes, NotificationService notifications) {
        return new ServiceCommande(stock, paiement, commandes, notifications);
    }
}
