package ma.shopflow.commande.jpa;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class LigneCommandeEmbeddable {

    private String reference;
    private int quantite;
    @Column(name = "prix_unitaire")
    private BigDecimal prixUnitaire;

    protected LigneCommandeEmbeddable() {
    }

    public LigneCommandeEmbeddable(String reference, int quantite, BigDecimal prixUnitaire) {
        this.reference = reference;
        this.quantite = quantite;
        this.prixUnitaire = prixUnitaire;
    }

    public String getReference() {
        return reference;
    }

    public int getQuantite() {
        return quantite;
    }

    public BigDecimal getPrixUnitaire() {
        return prixUnitaire;
    }
}