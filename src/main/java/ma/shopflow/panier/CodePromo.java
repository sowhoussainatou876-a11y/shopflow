package ma.shopflow.panier;

import java.util.Objects;

/**
 * Un code promotionnel : un pourcentage de remise compris entre 1 et 50.
 */
public record CodePromo(String code, int pourcentage) {

    public CodePromo {
        Objects.requireNonNull(code, "Le code est obligatoire");
        if (pourcentage < 1 || pourcentage > 50) {
            throw new IllegalArgumentException("Le pourcentage doit être compris entre 1 et 50");
        }
    }
}