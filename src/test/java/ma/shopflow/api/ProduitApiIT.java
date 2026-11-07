package ma.shopflow.api;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.startsWith;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import ma.shopflow.TestcontainersConfiguration;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
@DisplayName("API REST (REST Assured)")
class ProduitApiIT {

    @LocalServerPort
    int port;

    @BeforeEach
    void configurerRestAssured() {
        RestAssured.port = port;
        RestAssured.basePath = "/api";
    }

    @Test
    @DisplayName("GET /produits renvoie tout le catalogue")
    void leCatalogueCompletEstRenvoye() {
        given()
                .accept(ContentType.JSON)
                .when()
                .get("/produits")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("$", hasSize(6))
                .body("reference", hasItems("CLAV-01", "SOUR-02", "ECRA-04"))
                .body("prixUnitaire", everyItem(greaterThan(0f)));
    }

    @Test
    @DisplayName("GET /produits?q=ecran trouve l'écran, sans tenir compte de l'accent")
    void laRechercheTrouveLEcran() {
        given()
                .queryParam("q", "ecran")
                .when()
                .get("/produits")
                .then()
                .statusCode(200)
                .body("$", hasSize(1))
                .body("[0].reference", equalTo("ECRA-04"))
                .body("[0].nom", equalTo("Écran 27 pouces"));
    }

    @Test
    @DisplayName("POST /commandes répond 201 avec l'adresse de la commande créée")
    void uneCommandeEstCreee() {
        String id = given()
                .contentType(ContentType.JSON)
                .body("""
                        {"clientId": "C-003", "email": "salma@exemple.ma",
                         "lignes": [{"reference": "CABL-03", "quantite": 3}]}
                        """)
                .when()
                .post("/commandes")
                .then()
                .statusCode(201)
                .header("Location", startsWith("/api/commandes/"))
                .body("statut", equalTo("CONFIRMEE"))
                .body("total", equalTo(89.97f))
                .extract().path("id");

        assertThat(id).hasSize(36);
    }

    @Test
    @DisplayName("POST /commandes avec un produit inconnu répond 404 au format Problem Details")
    void unProduitInconnuDonne404() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                        {"clientId": "C-003", "email": "salma@exemple.ma",
                         "lignes": [{"reference": "XXXX-99", "quantite": 1}]}
                        """)
                .when()
                .post("/commandes")
                .then()
                .statusCode(404)
                .contentType("application/problem+json")
                .body("title", equalTo("Not Found"))
                .body("detail", containsString("XXXX-99"));
    }
}