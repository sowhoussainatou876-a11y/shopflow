package ma.shopflow.infra;

import java.time.Clock;
import java.time.ZoneId;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * L'horloge de l'application. La recevoir par injection, au lieu d'appeler
 * LocalDate.now(),
 * permet aux tests de choisir la date du jour.
 */
@Configuration
public class HorlogeConfiguration {

    @Bean
    Clock horloge() {
        return Clock.system(ZoneId.of("Africa/Casablanca"));
    }
}