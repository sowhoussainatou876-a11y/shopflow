package ma.shopflow.acceptation;

import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.jdbc.core.JdbcTemplate;

import io.cucumber.java.Before;
import io.restassured.RestAssured;

/**
 * Crochets (hooks) exécutés avant chaque scénario.
 */
public class Crochets {

    private final JdbcTemplate jdbc;

    @LocalServerPort
    int port;

    public Crochets(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Before
    public void preparerLeScenario() {
        RestAssured.port = port;
        RestAssured.basePath = "/api";
        // chaque scénario part d'une base propre : pas de commande, catalogue de
        // démarrage
        jdbc.update("delete from ligne_commande");
        jdbc.update("delete from commande");
        jdbc.update("""
                update produit set stock = case reference
                    when 'CLAV-01' then 25
                    when 'SOUR-02' then 40
                    when 'CABL-03' then 200
                    when 'ECRA-04' then 8
                    when 'CASQ-05' then 0
                    when 'PORT-06' then 5
                end
                where reference in ('CLAV-01', 'SOUR-02', 'CABL-03', 'ECRA-04', 'CASQ-05', 'PORT-06')
                """);
    }
}