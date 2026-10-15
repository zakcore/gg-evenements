-- =====================================================================
--  GG'Événements — Schéma de la base de données (MySQL 8)
--  Conforme au document « GG'Événements — Schéma de la base de données »
--  (9 tables). Exécuter ce script en premier, puis donnees.sql.
-- =====================================================================

DROP DATABASE IF EXISTS gg_evenements;
CREATE DATABASE gg_evenements CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE gg_evenements;

-- ---------------------------------------------------------------------
--  1. role — rôles applicatifs : PUBLIC, ORGANISATEUR, ADMIN
-- ---------------------------------------------------------------------
CREATE TABLE role (
    id_role      INT          NOT NULL AUTO_INCREMENT,
    nom_role     VARCHAR(30)  NOT NULL,
    description  VARCHAR(150),
    CONSTRAINT pk_role PRIMARY KEY (id_role),
    CONSTRAINT uq_role_nom UNIQUE (nom_role)
) ENGINE = InnoDB;

-- ---------------------------------------------------------------------
--  2. utilisateur — comptes de tous les usagers
-- ---------------------------------------------------------------------
CREATE TABLE utilisateur (
    id_utilisateur    INT          NOT NULL AUTO_INCREMENT,
    prenom            VARCHAR(50)  NOT NULL,
    nom               VARCHAR(50)  NOT NULL,
    courriel          VARCHAR(100) NOT NULL,
    mot_passe         VARCHAR(60)  NOT NULL,          -- hachage BCrypt, jamais en clair
    actif             TINYINT(1)   NOT NULL DEFAULT 1,
    date_inscription  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_utilisateur PRIMARY KEY (id_utilisateur),
    CONSTRAINT uq_utilisateur_courriel UNIQUE (courriel)
) ENGINE = InnoDB;

-- ---------------------------------------------------------------------
--  3. utilisateur_role — association plusieurs-à-plusieurs (PK composite)
-- ---------------------------------------------------------------------
CREATE TABLE utilisateur_role (
    id_utilisateur  INT NOT NULL,
    id_role         INT NOT NULL,
    CONSTRAINT pk_utilisateur_role PRIMARY KEY (id_utilisateur, id_role),
    CONSTRAINT fk_ur_utilisateur FOREIGN KEY (id_utilisateur) REFERENCES utilisateur (id_utilisateur),
    CONSTRAINT fk_ur_role        FOREIGN KEY (id_role)        REFERENCES role (id_role)
) ENGINE = InnoDB;

-- ---------------------------------------------------------------------
--  4. lieu — lieux physiques du collège
-- ---------------------------------------------------------------------
CREATE TABLE lieu (
    id_lieu       INT          NOT NULL AUTO_INCREMENT,
    nom           VARCHAR(80)  NOT NULL,
    campus        VARCHAR(50)  NOT NULL,
    adresse       VARCHAR(150) NOT NULL,
    capacite_max  INT          NOT NULL,
    CONSTRAINT pk_lieu PRIMARY KEY (id_lieu),
    CONSTRAINT ck_lieu_capacite CHECK (capacite_max > 0)
) ENGINE = InnoDB;

-- ---------------------------------------------------------------------
--  5. categorie — classement et filtres des événements
-- ---------------------------------------------------------------------
CREATE TABLE categorie (
    id_categorie  INT          NOT NULL AUTO_INCREMENT,
    nom           VARCHAR(50)  NOT NULL,
    description   VARCHAR(150),
    couleur       VARCHAR(7)   NOT NULL,              -- format #RRGGBB
    CONSTRAINT pk_categorie PRIMARY KEY (id_categorie),
    CONSTRAINT uq_categorie_nom UNIQUE (nom)
) ENGINE = InnoDB;

-- ---------------------------------------------------------------------
--  6. club — clubs et associations étudiantes
-- ---------------------------------------------------------------------
CREATE TABLE club (
    id_club           INT          NOT NULL AUTO_INCREMENT,
    nom               VARCHAR(80)  NOT NULL,
    description       VARCHAR(300),
    courriel_contact  VARCHAR(100) NOT NULL,
    logo              VARCHAR(150),
    id_responsable    INT          NOT NULL,
    actif             TINYINT(1)   NOT NULL DEFAULT 1,
    CONSTRAINT pk_club PRIMARY KEY (id_club),
    CONSTRAINT uq_club_nom UNIQUE (nom),
    CONSTRAINT fk_club_responsable FOREIGN KEY (id_responsable) REFERENCES utilisateur (id_utilisateur)
) ENGINE = InnoDB;

-- ---------------------------------------------------------------------
--  7. evenement — table centrale
-- ---------------------------------------------------------------------
CREATE TABLE evenement (
    id_evenement   INT           NOT NULL AUTO_INCREMENT,
    titre          VARCHAR(120)  NOT NULL,
    description    VARCHAR(1000) NOT NULL,
    id_club        INT           NOT NULL,
    id_categorie   INT           NOT NULL,
    id_lieu        INT           NOT NULL,
    id_createur    INT           NOT NULL,
    date_debut     DATETIME      NOT NULL,
    date_fin       DATETIME      NOT NULL,
    capacite_max   INT           NOT NULL,
    statut         VARCHAR(20)   NOT NULL DEFAULT 'PLANIFIE',
    image_affiche  VARCHAR(150),
    date_creation  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_evenement PRIMARY KEY (id_evenement),
    CONSTRAINT ck_evenement_capacite CHECK (capacite_max > 0),
    CONSTRAINT ck_evenement_dates    CHECK (date_fin > date_debut),
    CONSTRAINT ck_evenement_statut   CHECK (statut IN ('PLANIFIE', 'ANNULE', 'TERMINE')),
    CONSTRAINT fk_evenement_club      FOREIGN KEY (id_club)      REFERENCES club (id_club),
    CONSTRAINT fk_evenement_categorie FOREIGN KEY (id_categorie) REFERENCES categorie (id_categorie),
    CONSTRAINT fk_evenement_lieu      FOREIGN KEY (id_lieu)      REFERENCES lieu (id_lieu),
    CONSTRAINT fk_evenement_createur  FOREIGN KEY (id_createur)  REFERENCES utilisateur (id_utilisateur)
) ENGINE = InnoDB;

CREATE INDEX ix_evenement_statut_debut ON evenement (statut, date_debut);

-- ---------------------------------------------------------------------
--  8. inscription — classe d'association utilisateur / evenement
--     (statut CONFIRMEE ou EN_ATTENTE : liste d'attente par date_inscription)
-- ---------------------------------------------------------------------
CREATE TABLE inscription (
    id_inscription      INT          NOT NULL AUTO_INCREMENT,
    id_evenement        INT          NOT NULL,
    id_utilisateur      INT          NOT NULL,
    date_inscription    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    statut              VARCHAR(20)  NOT NULL,
    presence_confirmee  TINYINT(1)   NOT NULL DEFAULT 0,
    CONSTRAINT pk_inscription PRIMARY KEY (id_inscription),
    CONSTRAINT uq_inscription UNIQUE (id_evenement, id_utilisateur),   -- jamais deux inscriptions au même événement
    CONSTRAINT ck_inscription_statut CHECK (statut IN ('CONFIRMEE', 'EN_ATTENTE')),
    CONSTRAINT fk_inscription_evenement   FOREIGN KEY (id_evenement)   REFERENCES evenement (id_evenement),
    CONSTRAINT fk_inscription_utilisateur FOREIGN KEY (id_utilisateur) REFERENCES utilisateur (id_utilisateur)
) ENGINE = InnoDB;

CREATE INDEX ix_inscription_attente ON inscription (id_evenement, statut, date_inscription);

-- ---------------------------------------------------------------------
--  9. avis — avis des participants sur un événement terminé
-- ---------------------------------------------------------------------
CREATE TABLE avis (
    id_avis         INT          NOT NULL AUTO_INCREMENT,
    id_evenement    INT          NOT NULL,
    id_utilisateur  INT          NOT NULL,
    note            TINYINT      NOT NULL,
    commentaire     VARCHAR(500),
    date_avis       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_avis PRIMARY KEY (id_avis),
    CONSTRAINT uq_avis UNIQUE (id_evenement, id_utilisateur),          -- un seul avis par utilisateur et événement
    CONSTRAINT ck_avis_note CHECK (note BETWEEN 1 AND 5),
    CONSTRAINT fk_avis_evenement   FOREIGN KEY (id_evenement)   REFERENCES evenement (id_evenement),
    CONSTRAINT fk_avis_utilisateur FOREIGN KEY (id_utilisateur) REFERENCES utilisateur (id_utilisateur)
) ENGINE = InnoDB;
