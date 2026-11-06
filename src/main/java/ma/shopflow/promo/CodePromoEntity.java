package ma.shopflow.promo;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "code_promo")
public class CodePromoEntity {

    @Id
    private String code;
    private int pourcentage;
    @Column(name = "expire_le")
    private LocalDate expireLe;

    protected CodePromoEntity() {
    }

    public String getCode() {
        return code;
    }

    public int getPourcentage() {
        return pourcentage;
    }

    public LocalDate getExpireLe() {
        return expireLe;
    }
}
