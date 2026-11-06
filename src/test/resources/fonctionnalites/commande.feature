# language: fr
Fonctionnalité: Passer une commande
  En tant que client de ShopFlow
  Je veux commander les produits de mon panier
  Afin de les recevoir chez moi

  Contexte:
    Étant donné je suis la cliente "amina@exemple.ma"

  Scénario: Une commande simple est confirmée et le stock diminue
    Quand j'ajoute 2 "SOUR-02" à mon panier
    Et je valide ma commande
    Alors ma commande est confirmée
    Et le total payé est de 270,00 DH
    Et il ne reste plus que 38 "SOUR-02" en stock

  Scénario: Un produit en rupture bloque toute la commande
    Étant donné il reste 0 "CASQ-05" en stock
    Quand j'ajoute 1 "SOUR-02" à mon panier
    Et j'ajoute 1 "CASQ-05" à mon panier
    Et je valide ma commande
    Alors ma commande est refusée avec le code 409
    Et le message d'erreur est "Produit indisponible : CASQ-05"
    Et il ne reste plus que 40 "SOUR-02" en stock

  Plan du scénario: La livraison est offerte à partir de 500 DH d'achats
    Quand j'ajoute <quantité> "<produit>" à mon panier
    Et je valide ma commande
    Alors le total payé est de <total> DH

    Exemples:
      | quantité | produit | total  |
      | 1        | SOUR-02 | 150,00 |
      | 4        | SOUR-02 | 510,00 |
      | 1        | CLAV-01 | 480,00 |
      | 2        | CLAV-01 | 900,00 |