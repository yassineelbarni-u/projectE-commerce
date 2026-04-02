INSERT INTO categorie (nom)
SELECT 'Electronique' WHERE NOT EXISTS (SELECT 1 FROM categorie WHERE nom = 'Electronique');
INSERT INTO categorie (nom)
SELECT 'Mode' WHERE NOT EXISTS (SELECT 1 FROM categorie WHERE nom = 'Mode');
INSERT INTO categorie (nom)
SELECT 'Maison' WHERE NOT EXISTS (SELECT 1 FROM categorie WHERE nom = 'Maison');
INSERT INTO categorie (nom)
SELECT 'Sport' WHERE NOT EXISTS (SELECT 1 FROM categorie WHERE nom = 'Sport');

INSERT INTO produit (categorie_id, name, image_url, description, price, stock)
SELECT (SELECT id FROM categorie WHERE nom = 'Electronique'),
       'Laptop Pro 14',
       'https://picsum.photos/seed/storeshop-laptop-pro-14/1200/900',
       'Ordinateur portable leger pour travail et etudes.',
       1299.00,
       18
WHERE NOT EXISTS (SELECT 1 FROM produit WHERE name = 'Laptop Pro 14');

INSERT INTO produit (categorie_id, name, image_url, description, price, stock)
SELECT (SELECT id FROM categorie WHERE nom = 'Electronique'),
       'Casque Bluetooth Noise-Free',
       'https://picsum.photos/seed/storeshop-headphones-noisefree/1200/900',
       'Casque sans fil avec reduction de bruit et autonomie 30h.',
       179.90,
       35
WHERE NOT EXISTS (SELECT 1 FROM produit WHERE name = 'Casque Bluetooth Noise-Free');

INSERT INTO produit (categorie_id, name, image_url, description, price, stock)
SELECT (SELECT id FROM categorie WHERE nom = 'Electronique'),
       'Smartphone Nova X',
       'https://picsum.photos/seed/storeshop-smartphone-novax/1200/900',
       'Ecran AMOLED, triple capteur photo et charge rapide.',
       749.00,
       26
WHERE NOT EXISTS (SELECT 1 FROM produit WHERE name = 'Smartphone Nova X');

INSERT INTO produit (categorie_id, name, image_url, description, price, stock)
SELECT (SELECT id FROM categorie WHERE nom = 'Mode'),
       'Veste Denim Urban',
       'https://picsum.photos/seed/storeshop-denim-jacket-urban/1200/900',
       'Veste en jean coupe moderne, usage quotidien.',
       69.90,
       42
WHERE NOT EXISTS (SELECT 1 FROM produit WHERE name = 'Veste Denim Urban');

INSERT INTO produit (categorie_id, name, image_url, description, price, stock)
SELECT (SELECT id FROM categorie WHERE nom = 'Mode'),
       'Sneakers Aero Run',
       'https://picsum.photos/seed/storeshop-sneakers-aero-run/1200/900',
       'Chaussures confortables pour ville et marche.',
       89.90,
       54
WHERE NOT EXISTS (SELECT 1 FROM produit WHERE name = 'Sneakers Aero Run');

INSERT INTO produit (categorie_id, name, image_url, description, price, stock)
SELECT (SELECT id FROM categorie WHERE nom = 'Mode'),
       'Sac a Dos Minimal',
       'https://picsum.photos/seed/storeshop-backpack-minimal/1200/900',
       'Sac resistant avec compartiment laptop 15 pouces.',
       49.90,
       60
WHERE NOT EXISTS (SELECT 1 FROM produit WHERE name = 'Sac a Dos Minimal');

INSERT INTO produit (categorie_id, name, image_url, description, price, stock)
SELECT (SELECT id FROM categorie WHERE nom = 'Maison'),
       'Lampe LED Ambiance',
       'https://picsum.photos/seed/storeshop-led-lamp-ambiance/1200/900',
       'Lampe design avec variation d''intensite lumineuse.',
       39.90,
       27
WHERE NOT EXISTS (SELECT 1 FROM produit WHERE name = 'Lampe LED Ambiance');

INSERT INTO produit (categorie_id, name, image_url, description, price, stock)
SELECT (SELECT id FROM categorie WHERE nom = 'Maison'),
       'Chaise Ergonomique',
       'https://picsum.photos/seed/storeshop-ergonomic-chair/1200/900',
       'Chaise de bureau avec soutien lombaire reglable.',
       159.00,
       14
WHERE NOT EXISTS (SELECT 1 FROM produit WHERE name = 'Chaise Ergonomique');

INSERT INTO produit (categorie_id, name, image_url, description, price, stock)
SELECT (SELECT id FROM categorie WHERE nom = 'Maison'),
       'Set Cuisine Inox',
       'https://picsum.photos/seed/storeshop-kitchen-set-inox/1200/900',
       'Set complet ustensiles inox pour cuisine quotidienne.',
       79.00,
       22
WHERE NOT EXISTS (SELECT 1 FROM produit WHERE name = 'Set Cuisine Inox');

INSERT INTO produit (categorie_id, name, image_url, description, price, stock)
SELECT (SELECT id FROM categorie WHERE nom = 'Sport'),
       'Tapis Yoga Flex',
       'https://picsum.photos/seed/storeshop-yoga-mat-flex/1200/900',
       'Tapis antiderapant pour yoga, pilates et stretching.',
       24.90,
       73
WHERE NOT EXISTS (SELECT 1 FROM produit WHERE name = 'Tapis Yoga Flex');

INSERT INTO produit (categorie_id, name, image_url, description, price, stock)
SELECT (SELECT id FROM categorie WHERE nom = 'Sport'),
       'Halteres 2x10kg',
       'https://picsum.photos/seed/storeshop-dumbbells-2x10/1200/900',
       'Paire d''halteres pour renforcement musculaire maison.',
       59.90,
       19
WHERE NOT EXISTS (SELECT 1 FROM produit WHERE name = 'Halteres 2x10kg');

INSERT INTO produit (categorie_id, name, image_url, description, price, stock)
SELECT (SELECT id FROM categorie WHERE nom = 'Sport'),
       'Montre Sport Pulse',
       'https://picsum.photos/seed/storeshop-sport-watch-pulse/1200/900',
       'Suivi du rythme cardiaque, sommeil et activites.',
       129.00,
       31
WHERE NOT EXISTS (SELECT 1 FROM produit WHERE name = 'Montre Sport Pulse');
