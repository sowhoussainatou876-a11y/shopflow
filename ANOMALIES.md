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
