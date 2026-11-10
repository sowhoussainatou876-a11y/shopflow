# Stratégie de test de ShopFlow

## 1. La stratégie en un tableau

| Question                                | Réponse de ShopFlow                                                                                                                        |
| --------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------ |
| Qu'est-ce qu'on teste ?                 | Les règles métier (panier, commande, fidélité, codes promo), les jointures avec la base et le web, les scénarios de la responsable produit |
| À quel étage ?                          | Beaucoup de tests unitaires (47), moins de tests d'intégration (15), quelques scénarios d'acceptation (11) : la pyramide                   |
| Avec quels outils ?                     | JUnit, AssertJ, Mockito, Testcontainers, MockMvc, REST Assured, Cucumber                                                                   |
| Quand ?                                 | À chaque sauvegarde (`mvn test`), avant chaque push (`mvn verify`), à chaque push sur GitHub (pipeline)                                    |
| Qu'est-ce qui bloque une livraison ?    | Un test en échec, une couverture sous 90 % des lignes ou 85 % des branches, un Quality Gate en échec                                       |
| Comment juge-t-on les tests eux-mêmes ? | La couverture (JaCoCo) et la force des tests (PIT)                                                                                         |

## 2. Les étages du pipeline

| Étage               | Contenu                                          | Outil                                  | Bloquant          |
| ------------------- | ------------------------------------------------ | -------------------------------------- | ----------------- |
| Tests unitaires     | Règles du panier, de la commande, de la fidélité | Surefire, JUnit, Mockito               | Oui               |
| Tests d'intégration | Base PostgreSQL réelle, API, scénarios Cucumber  | Failsafe, Testcontainers, REST Assured | Oui               |
| Couverture          | 90 % des lignes, 85 % des branches               | JaCoCo                                 | Oui               |
| Qualité du code     | Quality Gate sur le nouveau code                 | SonarQube                              | Oui               |
| Force des tests     | Mutants du domaine                               | PIT                                    | Non, à surveiller |

Les tests unitaires tournent en premier : s'ils échouent, les tests d'intégration, plus lents, ne démarrent pas.

## 3. Les anomalies trouvées

| TP  | Anomalie                                              | Trouvée par                             |
| --- | ----------------------------------------------------- | --------------------------------------- |
| TP1 | Livraison payante à 500 DH pile                       | Valeurs limites, `@ParameterizedTest`   |
| TP1 | Remise appliquée sur la livraison                     | Test du total avec livraison payante    |
| TP2 | Stock jamais libéré après un paiement refusé          | `verify` d'un mock                      |
| TP2 | Une panne d'e-mail annulait une commande payée        | `doThrow` sur un mock                   |
| TP3 | 20 écrans vendus pour 8 en stock                      | Test de concurrence avec Testcontainers |
| TP4 | Code BLACKFRIDAY refusé le dernier jour               | Scénario Cucumber aux valeurs limites   |
| TP5 | Journal de la panne non vérifié ; classe de test vide | PIT ; SonarQube                         |

Aucune de ces anomalies n'aurait été trouvée par un seul type de test : une stratégie combine des techniques complémentaires, chacune à l'étage où elle est la plus efficace.
