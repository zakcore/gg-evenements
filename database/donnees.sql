-- =====================================================================
--  GG'Événements — Données de démonstration
--  À exécuter après schema.sql. Les dates sont relatives à NOW() pour
--  que la démonstration reste valable quel que soit le jour d'exécution.
--
--  Mot de passe de TOUS les comptes : MotDePasse1!   (haché avec BCrypt)
-- =====================================================================
USE gg_evenements;

SET @mdp = '$2a$10$ZC5Y/mXvTJBE21dmrF05fe1O67/9VCxWxhJlcL6xDlfozZT066MmG';

INSERT INTO role (id, nom) VALUES
    (1, 'ROLE_UTILISATEUR'),
    (2, 'ROLE_ORGANISATEUR'),
    (3, 'ROLE_ADMIN');

INSERT INTO utilisateur (id, prenom, nom, courriel, mot_de_passe, actif) VALUES
    (1,  'Nathalie', 'Gagnon',    'admin@cgodin.qc.ca',         @mdp, TRUE),
    (2,  'Félix',    'Tremblay',  'organisateur@cgodin.qc.ca',  @mdp, TRUE),
    (3,  'Mélodie',  'Roy',       'melodie.roy@cgodin.qc.ca',   @mdp, TRUE),
    (4,  'Samuel',   'Côté',      'samuel.cote@cgodin.qc.ca',   @mdp, TRUE),
    (5,  'Léa',      'Bouchard',  'lea.bouchard@cgodin.qc.ca',  @mdp, TRUE),
    (6,  'Antoine',  'Lavoie',    'antoine.lavoie@cgodin.qc.ca',@mdp, TRUE),
    (7,  'Camille',  'Pelletier', 'etudiant@cgodin.qc.ca',      @mdp, TRUE),
    (8,  'Mathis',   'Bélanger',  'mathis.belanger@cgodin.qc.ca', @mdp, TRUE),
    (9,  'Rosalie',  'Gauthier',  'rosalie.gauthier@cgodin.qc.ca', @mdp, TRUE),
    (10, 'William',  'Fortin',    'william.fortin@cgodin.qc.ca', @mdp, TRUE),
    (11, 'Florence', 'Ouellet',   'florence.ouellet@cgodin.qc.ca', @mdp, TRUE),
    (12, 'Thomas',   'Girard',    'thomas.girard@cgodin.qc.ca', @mdp, TRUE),
    (13, 'Juliette', 'Bergeron',  'juliette.bergeron@cgodin.qc.ca', @mdp, TRUE),
    (14, 'Olivier',  'Morin',     'inactif@cgodin.qc.ca',       @mdp, FALSE);

-- Tout le monde a le rôle de base ; les responsables de club sont Organisateurs ; Nathalie est Administratrice.
INSERT INTO utilisateur_role (utilisateur_id, role_id)
SELECT id, 1 FROM utilisateur;
INSERT INTO utilisateur_role (utilisateur_id, role_id) VALUES
    (1, 3),
    (2, 2), (3, 2), (4, 2), (5, 2), (6, 2);

INSERT INTO club (id, nom, description, courriel_contact, responsable_id, actif) VALUES
    (1, 'Ligue d''improvisation Les Godins',
        'La ligue d''impro du cégep : matchs mensuels, ateliers d''initiation et tournoi interscolaire au printemps. Aucune expérience requise pour venir encourager !',
        'impro@cgodin.qc.ca', 2, TRUE),
    (2, 'Club de plein air',
        'Randonnées, escalade, canot et sorties hivernales. Le club prête l''équipement de base aux membres.',
        'pleinair@cgodin.qc.ca', 3, TRUE),
    (3, 'Association générale étudiante',
        'L''AGE représente la population étudiante et organise les grandes soirées de la session.',
        'age@cgodin.qc.ca', 4, TRUE),
    (4, 'Club de programmation',
        'Ateliers techniques, préparation aux compétitions et soirées de code collaboratif.',
        'code@cgodin.qc.ca', 5, TRUE),
    (5, 'Ciné-club Gérald-Godin',
        'Projections et discussions autour du cinéma québécois. Club en pause cette session.',
        'cineclub@cgodin.qc.ca', 6, FALSE);

INSERT INTO categorie (id, nom, description, couleur) VALUES
    (1, 'Soirée thématique', 'Soirées sociales, partys et événements festifs.', '#B5677D'),
    (2, 'Sport',             'Tournois, matchs et activités physiques.',      '#3E7C59'),
    (3, 'Atelier',           'Ateliers pratiques et formations courtes.',     '#B9822B'),
    (4, 'Conférence',        'Conférences, panels et rencontres avec des invités.', '#3D6E9C'),
    (5, 'Sortie culturelle', 'Théâtre, musées, spectacles et sorties en ville.', '#8A5A44');

INSERT INTO lieu (id, nom, campus, adresse, capacite_max) VALUES
    (1, 'Agora',                      'Campus principal', '15615, boulevard Gouin Ouest, Montréal', 300),
    (2, 'Gymnase double',             'Campus principal', '15615, boulevard Gouin Ouest, Montréal', 200),
    (3, 'Local C-201',                'Campus principal', '15615, boulevard Gouin Ouest, Montréal', 40),
    (4, 'Salle multimédia (biblio.)', 'Campus principal', '15615, boulevard Gouin Ouest, Montréal', 30),
    (5, 'Café étudiant Le Repère',    'Campus principal', '15615, boulevard Gouin Ouest, Montréal', 80),
    (6, 'Parc-nature du Bois-de-l''Île-Bizard', 'Hors campus', '2115, chemin du Bord-du-Lac, L''Île-Bizard', 60);

-- ---------------------------------------------------------------------
--  Événements à venir (PLANIFIE)
-- ---------------------------------------------------------------------
INSERT INTO evenement (id, titre, description, date_debut, date_fin, capacite_max, image_affiche, statut, date_creation,
                       categorie_id, lieu_id, club_id, createur_id) VALUES
    (1, 'Match d''impro : Les Godins contre Ahuntsic',
        'Premier match de la saison à domicile ! Venez encourager nos joueuses et joueurs face à l''équipe du Collège Ahuntsic. Arbitre, musicien et pop-corn sur place. Places limitées dans la petite salle : inscrivez-vous rapidement.',
        DATE_ADD(DATE(NOW()), INTERVAL '3 19' DAY_HOUR), DATE_ADD(DATE(NOW()), INTERVAL '3 21' DAY_HOUR),
        4, '/img/affiches/impro.svg', 'PLANIFIE', DATE_SUB(NOW(), INTERVAL 10 DAY), 1, 3, 1, 2),
    (2, 'Atelier d''initiation à l''improvisation',
        'Un atelier de deux heures pour découvrir les bases : écoute, acceptation, construction de personnage. Ouvert à toutes et à tous, aucune expérience nécessaire. Portez des vêtements confortables.',
        DATE_ADD(DATE(NOW()), INTERVAL '9 18' DAY_HOUR), DATE_ADD(DATE(NOW()), INTERVAL '9 20' DAY_HOUR),
        20, NULL, 'PLANIFIE', DATE_SUB(NOW(), INTERVAL 8 DAY), 3, 3, 1, 2),
    (3, 'Randonnée d''automne au mont Saint-Bruno',
        'Boucle de 9 km dans les couleurs d''automne. Départ en autobus nolisé depuis le stationnement du cégep à 8 h. Apportez un lunch, de l''eau et de bonnes chaussures.',
        DATE_ADD(DATE(NOW()), INTERVAL '6 8' DAY_HOUR), DATE_ADD(DATE(NOW()), INTERVAL '6 16' DAY_HOUR),
        45, '/img/affiches/randonnee.svg', 'PLANIFIE', DATE_SUB(NOW(), INTERVAL 12 DAY), 5, 6, 2, 3),
    (4, 'Soirée d''Halloween de l''AGE',
        'Concours de costumes, DJ et maison hantée dans l''Agora. Prix pour les trois plus beaux costumes. Carte étudiante obligatoire à l''entrée.',
        DATE_ADD(DATE(NOW()), INTERVAL '12 20' DAY_HOUR), DATE_ADD(DATE(NOW()), INTERVAL '13 1' DAY_HOUR),
        250, '/img/affiches/soiree.svg', 'PLANIFIE', DATE_SUB(NOW(), INTERVAL 15 DAY), 1, 1, 3, 4),
    (5, 'Atelier Git et GitHub pour débutants',
        'Apprenez à versionner vos travaux : commits, branches, fusion et demandes de tirage. Apportez votre portable avec Git installé.',
        DATE_ADD(DATE(NOW()), INTERVAL '4 13' DAY_HOUR), DATE_ADD(DATE(NOW()), INTERVAL '4 15' DAY_HOUR),
        25, '/img/affiches/code.svg', 'PLANIFIE', DATE_SUB(NOW(), INTERVAL 6 DAY), 3, 4, 4, 5),
    (6, 'Tournoi de volleyball intercollégial',
        'Équipes mixtes de six joueurs. Inscrivez-vous individuellement, les équipes seront formées sur place. Arbitrage par l''équipe du département d''éducation physique.',
        DATE_ADD(DATE(NOW()), INTERVAL '15 9' DAY_HOUR), DATE_ADD(DATE(NOW()), INTERVAL '15 17' DAY_HOUR),
        72, '/img/affiches/volley.svg', 'PLANIFIE', DATE_SUB(NOW(), INTERVAL 9 DAY), 2, 2, 3, 4),
    (7, 'Conférence : l''IA générative et vos études',
        'Une chercheuse de l''Université de Montréal présente ce que l''IA générative change concrètement pour les étudiantes et étudiants du collégial : usages permis, limites et intégrité intellectuelle. Période de questions à la fin.',
        DATE_ADD(DATE(NOW()), INTERVAL '8 12' DAY_HOUR), DATE_ADD(DATE(NOW()), INTERVAL '8 13' DAY_HOUR),
        120, '/img/affiches/conference.svg', 'PLANIFIE', DATE_SUB(NOW(), INTERVAL 20 DAY), 4, 1, 4, 5),
    (8, 'Sortie au Théâtre du Nouveau Monde',
        'Représentation du soir suivie d''une rencontre avec la distribution. Billets à prix étudiant réservés par le cégep ; transport en métro avec les accompagnateurs.',
        DATE_ADD(DATE(NOW()), INTERVAL '20 19' DAY_HOUR), DATE_ADD(DATE(NOW()), INTERVAL '20 22' DAY_HOUR),
        30, '/img/affiches/theatre.svg', 'PLANIFIE', DATE_SUB(NOW(), INTERVAL 5 DAY), 5, 5, 3, 4),
    (9, 'Escalade de bloc : initiation',
        'Initiation à l''escalade de bloc au gymnase du cégep. Chaussons fournis (indiquez votre pointure à l''arrivée). Encadrement par des moniteurs certifiés.',
        DATE_ADD(DATE(NOW()), INTERVAL '10 17' DAY_HOUR), DATE_ADD(DATE(NOW()), INTERVAL '10 19' DAY_HOUR),
        16, '/img/affiches/escalade.svg', 'PLANIFIE', DATE_SUB(NOW(), INTERVAL 7 DAY), 2, 2, 2, 3),
    (10, 'Soirée de code collaboratif',
        'Trois heures pour avancer vos projets personnels en équipe, avec des mentors du programme de techniques de l''informatique. Pizza offerte.',
        DATE_ADD(DATE(NOW()), INTERVAL '17 18' DAY_HOUR), DATE_ADD(DATE(NOW()), INTERVAL '17 21' DAY_HOUR),
        35, NULL, 'PLANIFIE', DATE_SUB(NOW(), INTERVAL 3 DAY), 3, 3, 4, 5),
    (11, 'Café-rencontre : s''impliquer dans la vie étudiante',
        'Venez rencontrer les responsables des clubs et comités autour d''un café. Idéal pour les nouvelles et nouveaux arrivants.',
        DATE_ADD(DATE(NOW()), INTERVAL '2 11' DAY_HOUR), DATE_ADD(DATE(NOW()), INTERVAL '2 13' DAY_HOUR),
        60, '/img/affiches/cafe.svg', 'PLANIFIE', DATE_SUB(NOW(), INTERVAL 4 DAY), 4, 5, 3, 4),
    (12, 'Spectacle d''impro de fin de session',
        'Le grand spectacle de fin de session de la ligue, avec les finissantes et finissants. Entrée libre sur inscription.',
        DATE_ADD(DATE(NOW()), INTERVAL '30 19' DAY_HOUR), DATE_ADD(DATE(NOW()), INTERVAL '30 22' DAY_HOUR),
        150, NULL, 'PLANIFIE', DATE_SUB(NOW(), INTERVAL 1 DAY), 1, 1, 1, 2),
    -- Événement annulé (n'apparaît plus dans la liste à venir)
    (13, 'Canot sur la rivière des Prairies',
        'Descente en canot accompagnée. Annulée en raison du niveau d''eau trop élevé.',
        DATE_ADD(DATE(NOW()), INTERVAL '5 9' DAY_HOUR), DATE_ADD(DATE(NOW()), INTERVAL '5 15' DAY_HOUR),
        24, NULL, 'ANNULE', DATE_SUB(NOW(), INTERVAL 14 DAY), 2, 6, 2, 3),
    -- Événements terminés
    (14, 'Soirée quiz de la rentrée',
        'Quiz en équipes sur la culture générale, la musique et le cégep. Bravo à l''équipe gagnante « Les Godinophiles » !',
        DATE_ADD(DATE_SUB(DATE(NOW()), INTERVAL 20 DAY), INTERVAL 19 HOUR), DATE_ADD(DATE_SUB(DATE(NOW()), INTERVAL 20 DAY), INTERVAL 22 HOUR),
        40, '/img/affiches/quiz.svg', 'TERMINE', DATE_SUB(NOW(), INTERVAL 40 DAY), 1, 5, 1, 2),
    (15, 'Randonnée au mont Saint-Hilaire',
        'Sortie de début de session au sommet du Pain de Sucre.',
        DATE_ADD(DATE_SUB(DATE(NOW()), INTERVAL 14 DAY), INTERVAL 8 HOUR), DATE_ADD(DATE_SUB(DATE(NOW()), INTERVAL 14 DAY), INTERVAL 16 HOUR),
        10, '/img/affiches/randonnee.svg', 'TERMINE', DATE_SUB(NOW(), INTERVAL 30 DAY), 5, 6, 2, 3),
    (16, 'Conférence : bien gérer son stress en fin de session',
        'Conseils pratiques d''une psychologue du service d''aide aux étudiants.',
        DATE_ADD(DATE_SUB(DATE(NOW()), INTERVAL 7 DAY), INTERVAL 12 HOUR), DATE_ADD(DATE_SUB(DATE(NOW()), INTERVAL 7 DAY), INTERVAL 13 HOUR),
        3, NULL, 'TERMINE', DATE_SUB(NOW(), INTERVAL 25 DAY), 4, 4, 3, 4);

-- ---------------------------------------------------------------------
--  Inscriptions
-- ---------------------------------------------------------------------
INSERT INTO inscription (evenement_id, utilisateur_id, statut, date_inscription, presence_confirmee) VALUES
    -- Événement 1 (capacité 4) : complet + liste d'attente. Camille est confirmée :
    -- si elle se désinscrit, Thomas (premier en attente) est promu automatiquement.
    (1, 7,  'CONFIRMEE',  DATE_SUB(NOW(), INTERVAL 96 HOUR), FALSE),
    (1, 8,  'CONFIRMEE',  DATE_SUB(NOW(), INTERVAL 90 HOUR), FALSE),
    (1, 9,  'CONFIRMEE',  DATE_SUB(NOW(), INTERVAL 80 HOUR), FALSE),
    (1, 10, 'CONFIRMEE',  DATE_SUB(NOW(), INTERVAL 70 HOUR), FALSE),
    (1, 12, 'EN_ATTENTE', DATE_SUB(NOW(), INTERVAL 50 HOUR), FALSE),
    (1, 13, 'EN_ATTENTE', DATE_SUB(NOW(), INTERVAL 40 HOUR), FALSE),
    -- Événement 9 (capacité 16) : presque complet
    (9, 8,  'CONFIRMEE',  DATE_SUB(NOW(), INTERVAL 60 HOUR), FALSE),
    (9, 9,  'CONFIRMEE',  DATE_SUB(NOW(), INTERVAL 59 HOUR), FALSE),
    (9, 10, 'CONFIRMEE',  DATE_SUB(NOW(), INTERVAL 58 HOUR), FALSE),
    (9, 11, 'CONFIRMEE',  DATE_SUB(NOW(), INTERVAL 57 HOUR), FALSE),
    (9, 12, 'CONFIRMEE',  DATE_SUB(NOW(), INTERVAL 56 HOUR), FALSE),
    (9, 13, 'CONFIRMEE',  DATE_SUB(NOW(), INTERVAL 55 HOUR), FALSE),
    (9, 3,  'CONFIRMEE',  DATE_SUB(NOW(), INTERVAL 54 HOUR), FALSE),
    (9, 4,  'CONFIRMEE',  DATE_SUB(NOW(), INTERVAL 53 HOUR), FALSE),
    (9, 5,  'CONFIRMEE',  DATE_SUB(NOW(), INTERVAL 52 HOUR), FALSE),
    (9, 6,  'CONFIRMEE',  DATE_SUB(NOW(), INTERVAL 51 HOUR), FALSE),
    (9, 2,  'CONFIRMEE',  DATE_SUB(NOW(), INTERVAL 50 HOUR), FALSE),
    (9, 1,  'CONFIRMEE',  DATE_SUB(NOW(), INTERVAL 49 HOUR), FALSE),
    (9, 14, 'CONFIRMEE',  DATE_SUB(NOW(), INTERVAL 48 HOUR), FALSE),
    -- Divers
    (3, 7,  'CONFIRMEE',  DATE_SUB(NOW(), INTERVAL 30 HOUR), FALSE),
    (3, 11, 'CONFIRMEE',  DATE_SUB(NOW(), INTERVAL 29 HOUR), FALSE),
    (4, 8,  'CONFIRMEE',  DATE_SUB(NOW(), INTERVAL 20 HOUR), FALSE),
    (4, 9,  'CONFIRMEE',  DATE_SUB(NOW(), INTERVAL 19 HOUR), FALSE),
    (5, 10, 'CONFIRMEE',  DATE_SUB(NOW(), INTERVAL 18 HOUR), FALSE),
    (7, 11, 'CONFIRMEE',  DATE_SUB(NOW(), INTERVAL 17 HOUR), FALSE),
    (13, 7, 'CONFIRMEE',  DATE_SUB(NOW(), INTERVAL 200 HOUR), FALSE),
    -- Événements terminés
    (14, 7,  'CONFIRMEE', DATE_SUB(NOW(), INTERVAL 30 DAY), TRUE),
    (14, 8,  'CONFIRMEE', DATE_SUB(NOW(), INTERVAL 30 DAY), TRUE),
    (14, 9,  'CONFIRMEE', DATE_SUB(NOW(), INTERVAL 29 DAY), TRUE),
    (14, 10, 'CONFIRMEE', DATE_SUB(NOW(), INTERVAL 29 DAY), FALSE),
    (15, 7,  'CONFIRMEE', DATE_SUB(NOW(), INTERVAL 25 DAY), TRUE),
    (15, 11, 'CONFIRMEE', DATE_SUB(NOW(), INTERVAL 25 DAY), TRUE),
    (15, 12, 'CONFIRMEE', DATE_SUB(NOW(), INTERVAL 24 DAY), TRUE),
    (16, 8,  'CONFIRMEE', DATE_SUB(NOW(), INTERVAL 20 DAY), TRUE),
    (16, 9,  'CONFIRMEE', DATE_SUB(NOW(), INTERVAL 20 DAY), TRUE),
    (16, 10, 'CONFIRMEE', DATE_SUB(NOW(), INTERVAL 20 DAY), FALSE),
    (16, 7,  'EN_ATTENTE', DATE_SUB(NOW(), INTERVAL 19 DAY), FALSE);

-- ---------------------------------------------------------------------
--  Avis (événements terminés)
-- ---------------------------------------------------------------------
INSERT INTO avis (evenement_id, utilisateur_id, note, commentaire, date_avis) VALUES
    (14, 8,  5, 'Super ambiance, les questions sur le cégep étaient vraiment drôles.', DATE_SUB(NOW(), INTERVAL 19 DAY)),
    (14, 9,  4, 'Belle soirée, un peu trop long à la fin.', DATE_SUB(NOW(), INTERVAL 19 DAY)),
    (15, 7,  5, 'Vue magnifique au sommet et organisation impeccable.', DATE_SUB(NOW(), INTERVAL 13 DAY)),
    (15, 11, 4, NULL, DATE_SUB(NOW(), INTERVAL 12 DAY)),
    (16, 8,  3, 'Contenu utile, mais la salle était trop petite.', DATE_SUB(NOW(), INTERVAL 6 DAY));
