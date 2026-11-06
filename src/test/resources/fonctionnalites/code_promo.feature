# language: fr
Fonctionnalité: Codes promo
  Afin d'attirer des clients pendant les opérations commerciales
  En tant que responsable marketing
  Je veux que les clients puissent saisir un code promo en passant commande

  Règles :
  - la remise porte sur le sous-total, jamais sur la livraison
  - un code est valable jusqu'à sa date d'expiration incluse
  - un code inconnu ou expiré refuse la commande, sans rien débiter

  Contexte:
    Étant donné les codes promo suivants :
      | code        | remise | expire le  |
      | BIENVENUE10 | 10     | 31/12/2026 |
      | BLACKFRIDAY | 20     | 30/11/2026 |
    Et je suis la cliente "amina@exemple.ma"

  Scénario: Un code valide réduit le sous-total
    Étant donné que nous sommes le 15/11/2026
    Quand j'ajoute 2 "SOUR-02" à mon panier
    Et j'utilise le code promo "BIENVENUE10"
    Et je valide ma commande
    Alors ma commande est confirmée
    Et le total payé est de 246,00 DH

  Scénario: Un code inconnu refuse la commande sans toucher au stock
    Étant donné que nous sommes le 15/11/2026
    Quand j'ajoute 2 "SOUR-02" à mon panier
    Et j'utilise le code promo "NOEL50"
    Et je valide ma commande
    Alors ma commande est refusée avec le code 422
    Et le message d'erreur est "Code promo inconnu : NOEL50"
    Et il ne reste plus que 40 "SOUR-02" en stock

  Plan du scénario: Le code BLACKFRIDAY est valable jusqu'au 30 novembre inclus
    Étant donné que nous sommes le <date>
    Quand j'ajoute 1 "CLAV-01" à mon panier
    Et j'utilise le code promo "BLACKFRIDAY"
    Et je valide ma commande
    Alors le code promo est <décision>

    Exemples:
      | date       | décision |
      | 29/11/2026 | accepté  |
      | 30/11/2026 | accepté  |
      | 01/12/2026 | refusé   |