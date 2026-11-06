package ma.shopflow.acceptation;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.math.BigDecimal;

import org.springframework.jdbc.core.JdbcTemplate;

import io.cucumber.java.fr.Alors;
import io.cucumber.java.fr.Et;
import io.cucumber.java.fr.Etantdonné;
import io.cucumber.java.fr.Quand;
import io.restassured.http.ContentType;

/**
 * Les étapes des scénarios de commande : chaque méthode traduit une phrase en
 * appel à l'API.
 */
public class EtapesCommande {

    private final ContexteScenario contexte;
    private final JdbcTemplate jdbc;

    public EtapesCommande(ContexteScenario contexte, JdbcTemplate jdbc) {
        this.contexte = contexte;
        this.jdbc = jdbc;
    }

    @Etantdonné("je suis la cliente {string}")
    public void jeSuisLaCliente(String email) {
        contexte.setEmail(email);
    }

    @Etantdonné("il reste {int} {string} en stock")
    public void ilResteEnStock(int quantite, String reference) {
        jdbc.update("update produit set stock = ? where reference = ?", quantite, reference);
    }

    @Quand("j'ajoute {int} {string} à mon panier")
    public void jAjouteAMonPanier(int quantite, String reference) {
        contexte.ajouterLigne(reference, quantite);
    }

    @Et("je valide ma commande")
    public void jeValideMaCommande() {
        contexte.setReponse(
                given()
                        .contentType(ContentType.JSON)
                        .body(contexte.demandeDeCommande())
                        .when()
                        .post("/commandes"));
    }

    @Alors("ma commande est confirmée")
    public void maCommandeEstConfirmee() {
        contexte.getReponse().then()
                .statusCode(201)
                .body("statut", equalTo("CONFIRMEE"));
    }

    @Alors("le total payé est de {bigdecimal} DH")
    public void leTotalPayeEst(BigDecimal total) {
        contexte.getReponse().then().statusCode(201);
        BigDecimal paye = new BigDecimal(contexte.getReponse().path("total").toString());
        assertThat(paye).as("total payé").isEqualByComparingTo(total);
    }

    @Alors("ma commande est refusée avec le code {int}")
    public void maCommandeEstRefusee(int code) {
        contexte.getReponse().then().statusCode(code);
    }

    @Et("le message d'erreur est {string}")
    public void leMessageDErreurEst(String message) {
        contexte.getReponse().then().body("detail", equalTo(message));
    }

    @Et("il ne reste plus que {int} {string} en stock")
    public void ilNeRestePlusQue(int quantite, String reference) {
        Integer stock = jdbc.queryForObject("select stock from produit where reference = ?", Integer.class, reference);
        assertThat(stock).as("stock de " + reference).isEqualTo(quantite);
    }
}
