package ma.shopflow.acceptation;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;

import io.cucumber.java.ParameterType;
import io.cucumber.java.fr.Alors;
import io.cucumber.java.fr.Etantdonné;
import io.cucumber.java.fr.Quand;

/**
 * Les étapes propres à la fonctionnalité « Codes promo ».
 */
public class EtapesCodePromo {

    private static final DateTimeFormatter JJ_MM_AAAA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final ContexteScenario contexte;
    private final JdbcTemplate jdbc;
    private final HorlogeDeTest horloge;

    public EtapesCodePromo(ContexteScenario contexte, JdbcTemplate jdbc, HorlogeDeTest horloge) {
        this.contexte = contexte;
        this.jdbc = jdbc;
        this.horloge = horloge;
    }

    /**
     * Transforme « 30/11/2026 » en LocalDate, partout où une étape attend {date}.
     */
    @ParameterType("\\d{2}/\\d{2}/\\d{4}")
    public LocalDate date(String texte) {
        return LocalDate.parse(texte, JJ_MM_AAAA);
    }

    @Etantdonné("les codes promo suivants :")
    public void lesCodesPromoSuivants(List<Map<String, String>> codes) {
        jdbc.update("delete from code_promo");
        for (Map<String, String> code : codes) {
            jdbc.update("insert into code_promo (code, pourcentage, expire_le) values (?, ?, ?)",
                    code.get("code"),
                    Integer.parseInt(code.get("remise")),
                    date(code.get("expire le")));
        }
    }

    @Etantdonné("nous sommes le {date}")
    public void nousSommesLe(LocalDate aujourdhui) {
        horloge.regler(aujourdhui);
    }

    @Quand("j'utilise le code promo {string}")
    public void jUtiliseLeCodePromo(String code) {
        contexte.setCodePromo(code);
    }

    @Alors("le code promo est accepté")
    public void leCodePromoEstAccepte() {
        contexte.getReponse().then().statusCode(201);
    }

    @Alors("le code promo est refusé")
    public void leCodePromoEstRefuse() {
        contexte.getReponse().then().statusCode(422);
    }
}