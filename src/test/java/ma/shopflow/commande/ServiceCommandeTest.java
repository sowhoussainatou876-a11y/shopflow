package ma.shopflow.commande;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ma.shopflow.panier.Panier;
import ma.shopflow.panier.Produit;

@ExtendWith(MockitoExtension.class)
@DisplayName("Service de commande")
class ServiceCommandeTest {

    static final Client AMINA = new Client("C-001", "amina@exemple.ma");
    static final Produit SOURIS = new Produit("SOUR-02", "Souris sans fil", new BigDecimal("120.00"));

    static final Produit CLAVIER = new Produit("CLAV-01", "Clavier mécanique", new BigDecimal("450.00"));

    @Mock
    StockService stock;

    @Mock
    PaiementGateway paiement;

    @Mock
    CommandeRepository commandes;

    @Mock
    NotificationService notifications;

    @InjectMocks
    ServiceCommande service;

    @Captor
    ArgumentCaptor<Commande> commandeCaptor;

    Panier panier;

    @BeforeEach
    void preparerUnPanier() {
        panier = new Panier();
        panier.ajouter(SOURIS, 2);
    }

    @Test
    @DisplayName("C1 : un panier vide est refusé sans contacter aucun service")
    void unPanierVideEstRefuse() {
        Panier vide = new Panier();

        assertThatThrownBy(() -> service.passerCommande(AMINA, vide))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Le panier est vide");

        verifyNoInteractions(stock, paiement, commandes, notifications);
    }

    @Test
    @DisplayName("C2 : un produit en rupture bloque la commande avant tout paiement")
    void uneRuptureBloqueLaCommande() {
        panier.ajouter(CLAVIER, 1);
        when(stock.estDisponible("SOUR-02", 2)).thenReturn(true);
        when(stock.estDisponible("CLAV-01", 1)).thenReturn(false);

        assertThatThrownBy(() -> service.passerCommande(AMINA, panier))
                .isInstanceOf(RuptureDeStockException.class)
                .hasMessageContaining("CLAV-01");

        verify(stock, never()).reserver(anyString(), anyInt());
        verifyNoInteractions(paiement, commandes, notifications);
    }

    @Test
    @DisplayName("C3 et C4 : réserver, débiter, enregistrer puis notifier, dans cet ordre")
    void lesEtapesSontEnchaineesDansLOrdre() {
        when(stock.estDisponible(anyString(), anyInt())).thenReturn(true);
        when(paiement.debiter(anyString(), any())).thenReturn(ResultatPaiement.accepte("PAY-789"));
        when(commandes.enregistrer(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Commande commande = service.passerCommande(AMINA, panier);

        InOrder ordre = inOrder(stock, paiement, commandes, notifications);
        ordre.verify(stock).reserver("SOUR-02", 2);
        ordre.verify(paiement).debiter("C-001", new BigDecimal("270.00"));
        ordre.verify(commandes).enregistrer(any(Commande.class));
        ordre.verify(notifications).envoyerConfirmation("amina@exemple.ma", commande);
    }

    @Test
    @DisplayName("C4 : la commande enregistrée est confirmée, avec le total et la référence de paiement")
    void laCommandeEnregistreeEstComplete() {
        when(stock.estDisponible(anyString(), anyInt())).thenReturn(true);
        when(paiement.debiter(anyString(), any())).thenReturn(ResultatPaiement.accepte("PAY-789"));
        when(commandes.enregistrer(any())).thenAnswer(invocation -> invocation.getArgument(0));

        service.passerCommande(AMINA, panier);

        verify(commandes).enregistrer(commandeCaptor.capture());
        Commande enregistree = commandeCaptor.getValue();
        assertThat(enregistree.statut()).isEqualTo(StatutCommande.CONFIRMEE);
        assertThat(enregistree.clientId()).isEqualTo("C-001");
        assertThat(enregistree.total()).isEqualByComparingTo("270.00");
        assertThat(enregistree.referencePaiement()).isEqualTo("PAY-789");
        assertThat(enregistree.lignes()).hasSize(1);
    }

    @Test
    @DisplayName("C5 : un paiement refusé libère le stock et n'enregistre rien")
    void unPaiementRefuseLibereLeStock() {
        when(stock.estDisponible(anyString(), anyInt())).thenReturn(true);
        when(paiement.debiter(anyString(), any())).thenReturn(ResultatPaiement.refuse("Solde insuffisant"));

        assertThatThrownBy(() -> service.passerCommande(AMINA, panier))
                .isInstanceOf(PaiementRefuseException.class)
                .hasMessageContaining("Solde insuffisant");

        verify(stock).liberer("SOUR-02", 2);
        verifyNoInteractions(commandes, notifications);
    }

    @Test
    @DisplayName("C6 : une panne de l'envoi d'e-mail n'annule pas la commande")
    void unePanneDeMailNAnnulePasLaCommande() {
        when(stock.estDisponible(anyString(), anyInt())).thenReturn(true);
        when(paiement.debiter(anyString(), any())).thenReturn(ResultatPaiement.accepte("PAY-789"));
        when(commandes.enregistrer(any())).thenAnswer(invocation -> invocation.getArgument(0));
        doThrow(new IllegalStateException("Serveur SMTP injoignable"))
                .when(notifications).envoyerConfirmation(anyString(), any());

        Commande commande = service.passerCommande(AMINA, panier);

        assertThat(commande.statut()).isEqualTo(StatutCommande.CONFIRMEE);
        verify(commandes).enregistrer(any(Commande.class));
        verify(stock, never()).liberer(anyString(), anyInt());
    }

}
