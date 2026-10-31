package ma.shopflow.api;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

/**
 * Le corps JSON d'une demande de commande.
 */
public record DemandeCommande(
        @NotBlank String clientId,
        @NotBlank @Email String email,
        @NotEmpty List<@Valid Ligne> lignes) {

    public record Ligne(@NotBlank String reference, @Min(1) int quantite) {
    }
}
