package ma.shopflow;

import org.springframework.boot.SpringApplication;

/**
 * Lance ShopFlow sur le poste du développeur, avec un PostgreSQL démarré par
 * Testcontainers.
 */
public class TestShopflowApplication {

    public static void main(String[] args) {
        SpringApplication.from(ShopflowApplication::main)
                .with(TestcontainersConfiguration.class)
                .run(args);
    }
}
