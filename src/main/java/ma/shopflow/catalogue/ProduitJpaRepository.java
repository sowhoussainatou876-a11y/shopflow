package ma.shopflow.catalogue;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.data.jpa.repository.Modifying;

public interface ProduitJpaRepository extends JpaRepository<ProduitEntity, String> {

    /**
     * Recherche par nom, insensible à la casse et aux accents (extension unaccent
     * de PostgreSQL).
     */
    @Query(value = """
            select * from produit
            where unaccent(lower(nom)) like unaccent(lower('%' || :texte || '%'))
            order by nom
            """, nativeQuery = true)
    List<ProduitEntity> rechercher(@Param("texte") String texte);

    /**
     * Décrémente le stock en une seule requête, seulement s'il est suffisant.
     * Renvoie 1 si c'est fait, 0 sinon.
     */
    @Modifying
    @Query("update ProduitEntity p set p.stock = p.stock - :quantite "
            + "where p.reference = :reference and p.stock >= :quantite")
    int decrementerStock(@Param("reference") String reference, @Param("quantite") int quantite);

    @Modifying
    @Query("update ProduitEntity p set p.stock = p.stock + :quantite where p.reference = :reference")
    int incrementerStock(@Param("reference") String reference, @Param("quantite") int quantite);
}
