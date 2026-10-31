package ma.shopflow.commande.jpa;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

import ma.shopflow.commande.StatutCommande;

@Entity
@Table(name = "commande")
public class CommandeEntity {

    @Id
    private String id;
    @Column(name = "client_id")
    private String clientId;
    private BigDecimal total;
    @Column(name = "reference_paiement")
    private String referencePaiement;
    @Enumerated(EnumType.STRING)
    private StatutCommande statut;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "ligne_commande", joinColumns = @JoinColumn(name = "commande_id"))
    private List<LigneCommandeEmbeddable> lignes = new ArrayList<>();

    protected CommandeEntity() {
    }

    public CommandeEntity(String id, String clientId, BigDecimal total, String referencePaiement,
            StatutCommande statut, List<LigneCommandeEmbeddable> lignes) {
        this.id = id;
        this.clientId = clientId;
        this.total = total;
        this.referencePaiement = referencePaiement;
        this.statut = statut;
        this.lignes = new ArrayList<>(lignes);
    }

    public String getId() {
        return id;
    }

    public String getClientId() {
        return clientId;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public String getReferencePaiement() {
        return referencePaiement;
    }

    public StatutCommande getStatut() {
        return statut;
    }

    public List<LigneCommandeEmbeddable> getLignes() {
        return lignes;
    }
}