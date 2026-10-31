package ma.shopflow.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import ma.shopflow.TestcontainersConfiguration;
import ma.shopflow.commande.Commande;
import ma.shopflow.commande.NotificationService;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
@DisplayName("API des commandes")
class CommandeApiTest {

    @Autowired
    MockMvcTester mvc;
    @Autowired
    JdbcTemplate jdbc;
    @MockitoBean
    NotificationService notifications;

    @BeforeEach
    void remettreLeCatalogueEnEtat() {
        jdbc.update("delete from ligne_commande");
        jdbc.update("delete from commande");
        jdbc.update("update produit set stock = 40 where reference = 'SOUR-02'");
        jdbc.update("update produit set stock = 5 where reference = 'PORT-06'");
    }

    @Test
    @DisplayName("une commande valide est créée, enregistrée en base, et le stock diminue")
    void uneCommandeValideEstCreee() {
        var reponse = mvc.post().uri("/api/commandes")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"clientId": "C-001", "email": "amina@exemple.ma",
                         "lignes": [{"reference": "SOUR-02", "quantite": 2}]}
                        """)
                .exchange();

        assertThat(reponse).hasStatus(HttpStatus.CREATED)
                .bodyJson().extractingPath("$.total").isEqualTo(270.00);
        assertThat(reponse).bodyJson().extractingPath("$.statut").isEqualTo("CONFIRMEE");
        assertThat(stock("SOUR-02")).isEqualTo(38);
        assertThat(jdbc.queryForObject("select count(*) from commande", Integer.class)).isEqualTo(1);
        verify(notifications).envoyerConfirmation(eq("amina@exemple.ma"), any(Commande.class));
    }

    @Test
    @DisplayName("un produit en rupture donne 409 Conflict")
    void unProduitEnRuptureDonne409() {
        var reponse = mvc.post().uri("/api/commandes")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"clientId": "C-001", "email": "amina@exemple.ma",
                         "lignes": [{"reference": "CASQ-05", "quantite": 1}]}
                        """)
                .exchange();

        assertThat(reponse).hasStatus(HttpStatus.CONFLICT)
                .bodyJson().extractingPath("$.detail").isEqualTo("Produit indisponible : CASQ-05");
    }

    @Test
    @DisplayName("un paiement refusé donne 402 et rend le stock réservé")
    void unPaiementRefuseRendLeStock() {
        var reponse = mvc.post().uri("/api/commandes")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"clientId": "C-001", "email": "amina@exemple.ma",
                         "lignes": [{"reference": "PORT-06", "quantite": 2}]}
                        """)
                .exchange();

        assertThat(reponse).hasStatus(HttpStatus.PAYMENT_REQUIRED);
        assertThat(stock("PORT-06")).isEqualTo(5);
        assertThat(jdbc.queryForObject("select count(*) from commande", Integer.class)).isZero();
        verify(notifications, never()).envoyerConfirmation(any(), any());
    }

    @Test
    @DisplayName("une demande invalide donne 400 Bad Request")
    void uneDemandeInvalideDonne400() {
        var reponse = mvc.post().uri("/api/commandes")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"clientId": "C-001", "email": "pas-un-email",
                         "lignes": [{"reference": "SOUR-02", "quantite": 0}]}
                        """)
                .exchange();

        assertThat(reponse).hasStatus(HttpStatus.BAD_REQUEST);
    }

    private int stock(String reference) {
        return jdbc.queryForObject("select stock from produit where reference = ?", Integer.class, reference);
    }
}
