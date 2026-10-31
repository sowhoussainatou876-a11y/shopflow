-- Schéma de ShopFlow : catalogue et commandes
create table produit (
    reference varchar(20) primary key,
    nom varchar(120) not null,
    prix_unitaire numeric(10, 2) not null check (prix_unitaire > 0),
    stock integer not null check (stock >= 0)
);
create table commande (
    id varchar(36) primary key,
    client_id varchar(20) not null,
    total numeric(10, 2) not null,
    reference_paiement varchar(40),
    statut varchar(20) not null,
    cree_le timestamp with time zone not null default now()
);
create table ligne_commande (
    commande_id varchar(36) not null references commande (id),
    reference varchar(20) not null references produit (reference),
    quantite integer not null check (quantite > 0),
    prix_unitaire numeric(10, 2) not null
);