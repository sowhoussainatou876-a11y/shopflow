package ma.shopflow.catalogue;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ma.shopflow.TestcontainersConfiguration;
import ma.shopflow.commande.RuptureDeStockException;

@DataJpaTest
@Import({ TestcontainersConfiguration.class, StockJpaAdapter.class })
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@DisplayName("Stock PostgreSQL")
class StockJpaAdapterIT {

    @Autowired
    StockJpaAdapter stock;
    @Autowired
    ProduitJpaRepository produits;

    @BeforeEach
    void remettreLeStockDeLEcranA8() {
        ProduitEntity ecran = produits.findById("ECRA-04").orElseThrow();
        ecran.setStock(8);
        produits.save(ecran);
    }

    @Test
    @DisplayName("réserver décrémente le stock en base")
    void reserverDecrementeLeStock() {
        stock.reserver("ECRA-04", 3);

        assertThat(stockEnBase("ECRA-04")).isEqualTo(5);
    }

    @Test
    @DisplayName("réserver au-delà du stock est refusé et ne change rien")
    void reserverAuDelaDuStockEstRefuse() {
        assertThatThrownBy(() -> stock.reserver("ECRA-04", 9))
                .isInstanceOf(RuptureDeStockException.class);
        assertThat(stockEnBase("ECRA-04")).isEqualTo(8);
    }

    @Test
    @DisplayName("libérer rend le stock réservé")
    void libererRendLeStock() {
        stock.reserver("ECRA-04", 3);
        stock.liberer("ECRA-04", 3);

        assertThat(stockEnBase("ECRA-04")).isEqualTo(8);
    }

    @Test
    @DisplayName("20 clients simultanés ne peuvent pas acheter plus que les 8 écrans en stock")
    void lesReservationsSimultaneesNeSurvendentPas() throws Exception {
        int clients = 20;
        CountDownLatch top = new CountDownLatch(1);
        List<Callable<Boolean>> achats = new ArrayList<>();
        for (int i = 0; i < clients; i++) {
            achats.add(() -> {
                top.await();
                try {
                    stock.reserver("ECRA-04", 1);
                    return true;
                } catch (RuntimeException rupture) {
                    return false;
                }
            });
        }

        ExecutorService guichets = Executors.newFixedThreadPool(clients);
        List<Future<Boolean>> resultats = new ArrayList<>();
        for (Callable<Boolean> achat : achats) {
            resultats.add(guichets.submit(achat));
        }
        top.countDown();
        int ventes = 0;
        for (Future<Boolean> resultat : resultats) {
            ventes += resultat.get() ? 1 : 0;
        }
        guichets.shutdown();

        assertThat(ventes).as("nombre d'écrans vendus").isEqualTo(8);
        assertThat(stockEnBase("ECRA-04")).as("stock final").isZero();
    }

    private int stockEnBase(String reference) {
        return produits.findById(reference).orElseThrow().getStock();
    }
}
