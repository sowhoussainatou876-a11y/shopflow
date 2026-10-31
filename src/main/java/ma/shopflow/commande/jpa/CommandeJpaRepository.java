package ma.shopflow.commande.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CommandeJpaRepository extends JpaRepository<CommandeEntity, String> {
}
