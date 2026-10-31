package ma.shopflow.catalogue;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import ma.shopflow.panier.Produit;

/**
 * Un produit du catalogue, tel qu'il est stocké dans la table produit.
 */
@Entity
@Table(name = "produit")
public class ProduitEntity {

    @Id
    private String reference;

    private String nom;

    @Column(name = "prix_unitaire")
    private BigDecimal prixUnitaire;

    private int stock;

    protected ProduitEntity() {
        // exigé par JPA
    }

    public ProduitEntity(String reference, String nom, BigDecimal prixUnitaire, int stock) {
        this.reference = reference;
        this.nom = nom;
        this.prixUnitaire = prixUnitaire;
        this.stock = stock;
    }

    public Produit versProduit() {
        return new Produit(reference, nom, prixUnitaire);
    }

    public String getReference() {
        return reference;
    }

    public String getNom() {
        return nom;
    }

    public BigDecimal getPrixUnitaire() {
        return prixUnitaire;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }
}