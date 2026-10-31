package ma.shopflow.api;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ma.shopflow.catalogue.ProduitJpaRepository;

@RestController
@RequestMapping("/api/produits")
public class ProduitController {

    public record ProduitVue(String reference, String nom, BigDecimal prixUnitaire, int stock) {
    }

    private final ProduitJpaRepository produits;

    public ProduitController(ProduitJpaRepository produits) {
        this.produits = produits;
    }

    @GetMapping
    public List<ProduitVue> rechercher(@RequestParam(name = "q", defaultValue = "") String texte) {
        return produits.rechercher(texte).stream()
                .map(p -> new ProduitVue(p.getReference(), p.getNom(), p.getPrixUnitaire(), p.getStock()))
                .toList();
    }
}
