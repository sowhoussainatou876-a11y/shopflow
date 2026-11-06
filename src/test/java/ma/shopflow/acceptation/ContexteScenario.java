package ma.shopflow.acceptation;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import io.cucumber.spring.ScenarioScope;
import io.restassured.response.Response;

/**
 * L'état d'un scénario, partagé entre les classes d'étapes. Un nouvel objet est
 * créé pour chaque scénario.
 */
@Component
@ScenarioScope
public class ContexteScenario {

    private String email = "client@exemple.ma";
    private final List<Map<String, Object>> lignes = new ArrayList<>();
    private String codePromo;
    private Response reponse;

    public void ajouterLigne(String reference, int quantite) {
        lignes.add(Map.of("reference", reference, "quantite", quantite));
    }

    /** Le corps JSON de la commande, construit à partir du panier du scénario. */
    public Map<String, Object> demandeDeCommande() {
        Map<String, Object> demande = new LinkedHashMap<>();
        demande.put("clientId", "C-001");
        demande.put("email", email);
        demande.put("lignes", lignes);
        if (codePromo != null) {
            demande.put("codePromo", codePromo);
        }
        return demande;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setCodePromo(String codePromo) {
        this.codePromo = codePromo;
    }

    public Response getReponse() {
        return reponse;
    }

    public void setReponse(Response reponse) {
        this.reponse = reponse;
    }
}
