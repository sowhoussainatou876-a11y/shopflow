package ma.shopflow.promo;

import java.time.Clock;
import java.time.LocalDate;

import org.springframework.stereotype.Service;

import ma.shopflow.panier.CodePromo;

/**
 * Vérifie qu'un code promo existe et qu'il est encore valable aujourd'hui.
 */
@Service
public class ServiceCodesPromo {

    private final CodePromoJpaRepository codes;
    private final Clock horloge;

    public ServiceCodesPromo(CodePromoJpaRepository codes, Clock horloge) {
        this.codes = codes;
        this.horloge = horloge;
    }

    public CodePromo valider(String code) {
        CodePromoEntity trouve = codes.findById(code)
                .orElseThrow(() -> new CodePromoInvalideException("Code promo inconnu : " + code));
        LocalDate aujourdhui = LocalDate.now(horloge);
        if (aujourdhui.isAfter(trouve.getExpireLe())) {
            throw new CodePromoInvalideException("Code promo expiré : " + code);
        }
        return new CodePromo(trouve.getCode(), trouve.getPourcentage());
    }
}