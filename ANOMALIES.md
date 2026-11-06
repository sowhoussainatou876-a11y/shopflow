# Fiche d'anomalies : TP1 ShopFlow

## Anomalie 1 : livraison facturée à 500 DH pile

- **Règle concernée** : R5. La livraison coûte 30 DH et elle est offerte dès que le sous-total atteint 500 DH.
- **Test révélateur** : `PanierTest > R5 : frais de livraison > laLivraisonEstOfferteAPartirDe500DH`, cas [3] (sous-total 500.00 DH, livraison attendue 0.00 DH, obtenue 30.00 DH).
- **Cause** : dans `Panier.fraisLivraison()`, la condition `compareTo(SEUIL_LIVRAISON_OFFERTE) > 0` signifie « strictement supérieur à 500 » au lieu de « supérieur ou égal ». Erreur de type off-by-one, visible seulement à la limite exacte.
- **Correctif** : remplacer `> 0` par `>= 0`. Le test paramétré reste en place comme test de non-régression.

## Anomalie 2 : remise appliquée aussi sur la livraison

- **Règle concernée** : R6 et R7. La remise porte sur le sous-total uniquement, jamais sur la livraison. Total = sous-total − remise + livraison.
- **Test révélateur** : `PanierTest > R6 et R7 : remise et total > laRemiseNePortePasSurLaLivraison` (1 souris à 120 DH, code BIENVENUE10 : total attendu 138.00 DH, obtenu 135.00 DH).
- **Cause** : dans `Panier.total()`, le pourcentage était calculé sur `sousTotal() + fraisLivraison()` au lieu du sous-total seul. Le client obtenait 3 DH de remise en trop sur chaque commande avec code promo et livraison payante.
- **Correctif** : remplacer le corps de `total()` par `sousTotal().subtract(remise()).add(fraisLivraison())`, traduction directe de R7.

## Anomalie 3 : stock jamais libéré après un paiement refusé

- **Règle concernée** : C5. Si le paiement est refusé, le stock réservé est libéré, `PaiementRefuseException` est levée, rien n'est enregistré et aucun e-mail n'est envoyé.
- **Test révélateur** : `ServiceCommandeTest > C5 : un paiement refusé libère le stock et n'enregistre rien` (`stock.liberer("SOUR-02", 2)` attendu mais jamais appelé).
- **Cause** : dans `ServiceCommande.passerCommande()`, quand `resultat.accepte()` est faux, l'exception est levée sans appeler `stock.liberer(...)` pour les lignes déjà réservées. Chaque carte refusée bloquait du stock indéfiniment.
- **Correctif** : boucler sur les lignes du panier et appeler `stock.liberer(reference, quantite)` avant de lever `PaiementRefuseException`.

## Anomalie 4 : une panne d'e-mail fait échouer une commande payée

- **Règle concernée** : C6. Une panne de l'envoi d'e-mail n'annule pas une commande payée.
- **Test révélateur** : `ServiceCommandeTest > C6 : une panne de l'envoi d'e-mail n'annule pas la commande` (erreur : `IllegalStateException: Serveur SMTP injoignable` remontée au client).
- **Cause** : dans `ServiceCommande.passerCommande()`, l'appel à `notifications.envoyerConfirmation(...)` n'était pas protégé. Son exception traversait le service alors que le paiement et l'enregistrement avaient déjà réussi, et le client risquait de repasser commande et de payer deux fois.
- **Correctif** : entourer l'envoi d'un `try / catch (RuntimeException)` qui journalise un avertissement (`System.Logger`) sans annuler la commande.

## Anomalie 5 : survente du stock sous accès concurrents (mise à jour perdue)

- **Scénario** : 20 clients commandent en même temps le même écran, dont il ne reste que 8 exemplaires. Le site en vend 20 au lieu de 8.
- **Test révélateur** : `StockJpaAdapterTest > 20 clients simultanés ne peuvent pas acheter plus que les 8 écrans en stock` (nombre d'écrans vendus : 20 obtenus, 8 attendus).
- **Cause** : mise à jour perdue (lost update). `StockJpaAdapter.reserver()` lit le stock, vérifie, puis écrit une valeur calculée en Java. Entre la lecture et l'écriture, d'autres transactions lisent la même valeur. `@Transactional` ne protège pas à l'isolation READ COMMITTED de PostgreSQL, et la contrainte `check (stock >= 0)` ne détecte rien car le stock final n'est jamais négatif. Bug invisible au TP2, car le stock y était un mock.
- **Correctif** : remplacer lecture puis écriture par une seule requête atomique `update produit set stock = stock - :q where reference = :r and stock >= :q` (méthodes `decrementerStock` et `incrementerStock` du dépôt). La base verrouille la ligne pendant l'instruction, et le nombre de lignes modifiées indique le succès ou la rupture.

## Anomalie 6 : un code promo cesse de fonctionner le jour même de son expiration

- **Règle concernée** : « un code est valable jusqu'à sa date d'expiration incluse » (fonctionnalité Codes promo, règle de Nadia).
- **Test révélateur** : scénario `Codes promo - Le code BLACKFRIDAY est valable jusqu'au 30 novembre inclus`, exemple du 30/11/2026 (code accepté attendu, 422 obtenu).
- **Cause** : dans `ServiceCodesPromo.valider()`, la condition `!aujourdhui.isBefore(expireLe)` refuse aussi le jour même de l'expiration. Erreur de borne, comme l'anomalie 1 du TP1 : le code BLACKFRIDAY aurait cessé de fonctionner le 30 novembre, jour le plus fort des ventes.
- **Correctif** : remplacer la condition par `aujourdhui.isAfter(expireLe)`. Un code n'est expiré que strictement après sa date d'expiration. Le plan du scénario (veille, jour même, lendemain) reste en place comme test de non-régression.
