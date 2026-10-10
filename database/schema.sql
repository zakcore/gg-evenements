-- =====================================================================
--  GG'Événements — Schéma de la base de données (MySQL 8)
--  Exécuter ce script en premier, puis donnees.sql
-- =====================================================================

DROP DATABASE IF EXISTS gg_evenements;
CREATE DATABASE gg_evenements CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE gg_evenements;

-- ---------------------------------------------------------------------
--  Sécurité : utilisateurs et rôles (relation plusieurs-à-plusieurs)
-- ---------------------------------------------------------------------
CREATE TABLE role (
    id   BIGINT      NOT NULL AUTO_INCREMENT,
    nom  VARCHAR(40) NOT NULL,
    CONSTRAINT pk_role PRIMARY KEY (id),
    CONSTRAINT uq_role_nom UNIQUE (nom)
) ENGINE = InnoDB;

CREATE TABLE utilisateur (
    id             BIGINT       NOT NULL AUTO_INCREMENT,
    prenom         VARCHAR(60)  NOT NULL,
    nom            VARCHAR(60)  NOT NULL,
    courriel       VARCHAR(120) NOT NULL,
    mot_de_passe   VARCHAR(100) NOT NULL,          -- hachage BCrypt, jamais en clair
    actif          BOOLEAN      NOT NULL DEFAULT TRUE,
    date_creation  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_utilisateur PRIMARY KEY (id),
    CONSTRAINT uq_utilisateur_courriel UNIQUE (courriel)
) ENGINE = InnoDB;

CREATE TABLE utilisateur_role (
    utilisateur_id BIGINT NOT NULL,
    role_id        BIGINT NOT NULL,
    CONSTRAINT pk_utilisateur_role PRIMARY KEY (utilisateur_id, role_id),
    CONSTRAINT fk_ur_utilisateur FOREIGN KEY (utilisateur_id) REFERENCES utilisateur (id),
    CONSTRAINT fk_ur_role        FOREIGN KEY (role_id)        REFERENCES role (id)
) ENGINE = InnoDB;

-- ---------------------------------------------------------------------
--  Référentiels : clubs, catégories, lieux
-- ---------------------------------------------------------------------
CREATE TABLE club (
    id                BIGINT       NOT NULL AUTO_INCREMENT,
    nom               VARCHAR(100) NOT NULL,
    description       VARCHAR(1000),
    courriel_contact  VARCHAR(120) NOT NULL,
    responsable_id    BIGINT       NOT NULL,
    actif             BOOLEAN      NOT NULL DEFAULT TRUE,
    CONSTRAINT pk_club PRIMARY KEY (id),
    CONSTRAINT uq_club_nom UNIQUE (nom),
    CONSTRAINT uq_club_responsable UNIQUE (responsable_id),   -- un responsable gère un seul club
    CONSTRAINT fk_club_responsable FOREIGN KEY (responsable_id) REFERENCES utilisateur (id)
) ENGINE = InnoDB;

CREATE TABLE categorie (
    id           BIGINT       NOT NULL AUTO_INCREMENT,
    nom          VARCHAR(60)  NOT NULL,
    description  VARCHAR(500),
    couleur      VARCHAR(7)   NOT NULL,             -- format #RRGGBB
    CONSTRAINT pk_categorie PRIMARY KEY (id),
    CONSTRAINT uq_categorie_nom UNIQUE (nom)
) ENGINE = InnoDB;

CREATE TABLE lieu (
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    nom           VARCHAR(100) NOT NULL,
    campus        VARCHAR(100) NOT NULL,
    adresse       VARCHAR(200) NOT NULL,
    capacite_max  INT          NOT NULL,
    CONSTRAINT pk_lieu PRIMARY KEY (id),
    CONSTRAINT ck_lieu_capacite CHECK (capacite_max > 0)
) ENGINE = InnoDB;

-- ---------------------------------------------------------------------
--  Événements
-- ---------------------------------------------------------------------
CREATE TABLE evenement (
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    titre           VARCHAR(150) NOT NULL,
    description     TEXT         NOT NULL,
    date_debut      DATETIME     NOT NULL,
    date_fin        DATETIME     NOT NULL,
    capacite_max    INT          NOT NULL,
    image_affiche   VARCHAR(255),
    statut          VARCHAR(20)  NOT NULL DEFAULT 'PLANIFIE',
    date_creation   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    categorie_id    BIGINT       NOT NULL,
    lieu_id         BIGINT       NOT NULL,
    club_id         BIGINT       NOT NULL,
    createur_id     BIGINT       NOT NULL,
    CONSTRAINT pk_evenement PRIMARY KEY (id),
    CONSTRAINT ck_evenement_capacite CHECK (capacite_max > 0),
    CONSTRAINT ck_evenement_dates    CHECK (date_fin > date_debut),
    CONSTRAINT ck_evenement_statut   CHECK (statut IN ('PLANIFIE', 'ANNULE', 'TERMINE')),
    CONSTRAINT fk_evenement_categorie FOREIGN KEY (categorie_id) REFERENCES categorie (id),
    CONSTRAINT fk_evenement_lieu      FOREIGN KEY (lieu_id)      REFERENCES lieu (id),
    CONSTRAINT fk_evenement_club      FOREIGN KEY (club_id)      REFERENCES club (id),
    CONSTRAINT fk_evenement_createur  FOREIGN KEY (createur_id)  REFERENCES utilisateur (id)
) ENGINE = InnoDB;

CREATE INDEX ix_evenement_statut_debut ON evenement (statut, date_debut);

-- ---------------------------------------------------------------------
--  Inscriptions (avec liste d'attente) et avis
-- ---------------------------------------------------------------------
CREATE TABLE inscription (
    id                   BIGINT       NOT NULL AUTO_INCREMENT,
    evenement_id         BIGINT       NOT NULL,
    utilisateur_id       BIGINT       NOT NULL,
    statut               VARCHAR(20)  NOT NULL,
    date_inscription     DATETIME(6)  NOT NULL,     -- microsecondes : ordre FIFO de la liste d'attente
    presence_confirmee   BOOLEAN      NOT NULL DEFAULT FALSE,
    CONSTRAINT pk_inscription PRIMARY KEY (id),
    CONSTRAINT uq_inscription UNIQUE (evenement_id, utilisateur_id),
    CONSTRAINT ck_inscription_statut CHECK (statut IN ('CONFIRMEE', 'EN_ATTENTE')),
    CONSTRAINT fk_inscription_evenement   FOREIGN KEY (evenement_id)   REFERENCES evenement (id),
    CONSTRAINT fk_inscription_utilisateur FOREIGN KEY (utilisateur_id) REFERENCES utilisateur (id)
) ENGINE = InnoDB;

CREATE INDEX ix_inscription_attente ON inscription (evenement_id, statut, date_inscription);

CREATE TABLE avis (
    id              BIGINT        NOT NULL AUTO_INCREMENT,
    evenement_id    BIGINT        NOT NULL,
    utilisateur_id  BIGINT        NOT NULL,
    note            INT           NOT NULL,
    commentaire     VARCHAR(500),
    date_avis       DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_avis PRIMARY KEY (id),
    CONSTRAINT uq_avis UNIQUE (evenement_id, utilisateur_id),
    CONSTRAINT ck_avis_note CHECK (note BETWEEN 1 AND 5),
    CONSTRAINT fk_avis_evenement   FOREIGN KEY (evenement_id)   REFERENCES evenement (id),
    CONSTRAINT fk_avis_utilisateur FOREIGN KEY (utilisateur_id) REFERENCES utilisateur (id)
) ENGINE = InnoDB;
