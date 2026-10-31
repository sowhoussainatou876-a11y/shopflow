package ma.shopflow.api;

import java.net.URI;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ma.shopflow.catalogue.ProduitEntity;
import ma.shopflow.catalogue.ProduitInconnuException;
import ma.shopflow.catalogue.ProduitJpaRepository;
import ma.shopflow.commande.Client;
import ma.shopflow.commande.Commande;
import ma.shopflow.commande.ServiceCommande;
import ma.shopflow.panier.Panier;

@RestController
@RequestMapping("/api/commandes")
public class CommandeController {

    private final ServiceCommande service;
    private final ProduitJpaRepository produits;

    public CommandeController(ServiceCommande service, ProduitJpaRepository produits) {
        this.service = service;
        this.produits = produits;
    }

    @PostMapping
    public ResponseEntity<CommandeCreee> passer(@Valid @RequestBody DemandeCommande demande) {
        Panier panier = new Panier();
        for (DemandeCommande.Ligne ligne : demande.lignes()) {
            ProduitEntity produit = produits.findById(ligne.reference())
                    .orElseThrow(() -> new ProduitInconnuException(ligne.reference()));
            panier.ajouter(produit.versProduit(), ligne.quantite());
        }
        Commande commande = service.passerCommande(new Client(demande.clientId(), demande.email()), panier);
        return ResponseEntity.created(URI.create("/api/commandes/" + commande.id()))
                .body(CommandeCreee.depuis(commande));
    }
}
