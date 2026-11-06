-- Codes promo : un pourcentage de remise, valable jusqu'à une date incluse
create table code_promo (
    code varchar(30) primary key,
    pourcentage integer not null check (
        pourcentage between 1 and 50
    ),
    expire_le date not null
);
insert into code_promo (code, pourcentage, expire_le)
values ('BIENVENUE10', 10, date '2026-12-31'),
    ('BLACKFRIDAY', 20, date '2026-11-30');