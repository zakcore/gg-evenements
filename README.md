# GG'Événements

Plateforme de gestion des événements étudiants du Cégep Gérald-Godin : consultation des événements à venir,
inscription avec liste d'attente automatique, espace organisateur pour les responsables de club et espace
d'administration pour la Vie étudiante.

**Technologies :** Java 17+ · Spring Boot 3.5 · Spring MVC · Thymeleaf (+ Layout Dialect, extras Spring Security 6) ·
Spring Data JPA / Hibernate · Spring Security · Bean Validation · MySQL 8 · Maven

---

## 1. Installation

### Prérequis
- JDK 17 ou plus récent (testé avec JDK 26)
- MySQL 8 en marche sur `localhost:3306`
- Maven n'a pas besoin d'être installé : le projet inclut le Maven Wrapper (`mvnw` / `mvnw.cmd`)

### Base de données
Exécuter, dans l'ordre, les deux scripts du dossier `database/` (MySQL Workbench ou ligne de commande) :

```bash
mysql -u root -p < database/schema.sql     # crée la base gg_evenements et toutes les tables
mysql -u root -p < database/donnees.sql    # données de démonstration
```

> Les dates des données de démonstration sont relatives à `NOW()` : la démonstration reste cohérente
> (événements à venir, terminés, annulés) quel que soit le jour où le script est exécuté.

Par défaut, l'application se connecte avec l'utilisateur `root` **sans mot de passe**. Pour d'autres identifiants,
définir les variables d'environnement `DB_USER`, `DB_PASSWORD` (et au besoin `DB_HOST`, `DB_PORT`)
ou modifier `src/main/resources/application.properties`.

### Démarrage (une seule commande)
```bash
./mvnw spring-boot:run        # Windows : mvnw.cmd spring-boot:run
```
puis ouvrir <http://localhost:8080>.

Tests unitaires : `./mvnw test`

---

## 2. Comptes de test

Mot de passe de **tous** les comptes : `MotDePasse1!`

| Rôle | Courriel | Particularité |
|---|---|---|
| Administrateur | `admin@cgodin.qc.ca` | Nathalie Gagnon — accès à l'administration |
| Organisateur | `organisateur@cgodin.qc.ca` | Félix Tremblay — responsable de la *Ligue d'improvisation Les Godins* |
| Public (étudiant) | `etudiant@cgodin.qc.ca` | Camille Pelletier — inscriptions à venir, passées, avis à laisser |
| Compte désactivé | `inactif@cgodin.qc.ca` | Démontre le message « Ce compte a été désactivé » |

Autres organisateurs : `melodie.roy@`, `samuel.cote@`, `lea.bouchard@`, `antoine.lavoie@cgodin.qc.ca`.

### Scénario de démonstration conseillé
1. **Liste d'attente** — en tant que Camille, ouvrir *Match d'impro : Les Godins contre Ahuntsic* (complet, 2 en attente)
   et se désinscrire : Thomas Girard (premier en attente) est promu automatiquement *Confirmé*
   (visible par l'organisateur dans *Gérer les inscriptions*). Se réinscrire place Camille en liste d'attente.
2. **Avis** — dans *Mes événements > Passés*, laisser un avis sur *Soirée quiz de la rentrée*. L'option n'est
   pas offerte pour la conférence où Camille est restée en liste d'attente.
3. **Organisateur** — tenter de réduire la capacité du match d'impro sous 4 : refus affiché à côté du champ.
   Accéder à `/organisateur/evenements/3/inscriptions` (événement d'un autre club) : page 403.
4. **Administrateur** — tenter de supprimer une catégorie utilisée : message clair; désactiver un compte;
   désigner un responsable de club (le rôle Organisateur est accordé automatiquement).

---

## 3. Architecture

```
ca.qc.cgodin.ggevenements
 ├─ controller   EvenementController, InscriptionController, AvisController, ClubController,
 │               OrganisateurController, AdminController, AuthController, GestionErreursControllerAdvice
 ├─ service      EvenementService, InscriptionService, AvisService, UtilisateurService, ClubService,
 │               CategorieService, LieuService, StatistiqueService, FichierService
 ├─ repository   JpaRepository + EvenementSpecifications (recherche dynamique)
 ├─ model        Utilisateur, Role, Club, Evenement, Categorie, Lieu, Inscription, Avis (+ énumérations de statut)
 ├─ dto          objets de formulaire validés (EvenementForm, ClubForm, AvisForm…), contrainte @DatesCoherentes
 ├─ security     SecurityConfig (SecurityFilterChain, BCrypt), UtilisateurDetails(Service)
 ├─ exception    RessourceIntrouvable (404), AccesRefuse (403), RegleAffaire
 └─ config       WebConfig (affiches téléversées, locale fr-CA)
resources/templates   layout.html, fragments/, evenements/, organisateur/, admin/, clubs/, auth/, error/
```

Couches strictes **Controller → Service → Repository** : aucun contrôleur n'accède à un repository.

---

## 4. Particularités et choix techniques

- **Capacité et concurrence** — l'inscription verrouille la ligne de l'événement (`SELECT … FOR UPDATE`,
  `@Lock(PESSIMISTIC_WRITE)`) dans une transaction `@Transactional` : la vérification de la capacité et la création
  de l'inscription sont atomiques. Vérifié : 10 inscriptions simultanées sur un événement de 2 places donnent
  exactement 2 confirmées et 8 en attente.
- **Promotion de la liste d'attente** — la désinscription et la promotion du premier en attente (plus ancienne
  `date_inscription`, précision à la microseconde) forment une seule transaction. Si un organisateur augmente la
  capacité, les personnes en attente occupent automatiquement les nouvelles places.
- **Recherche avancée** — `Specification` Spring Data JPA : seuls les critères choisis composent la requête SQL;
  tri et pagination (9 par page) sont faits par la base. Le tri « places restantes » utilise une colonne calculée
  `@Formula` (sous-requête SQL).
- **Statistiques** — `COUNT … GROUP BY`, `AVG` (JPQL) et une requête native pour le taux moyen de remplissage :
  aucun calcul en mémoire.
- **Statut « Terminé »** — une tâche planifiée (`@Scheduled`, chaque minute et au démarrage) fait passer à `TERMINE`
  les événements planifiés dont la date de fin est dépassée.
- **Sécurité** — mots de passe BCrypt; routes protégées par rôle (`/admin/**`, `/organisateur/**`); `sec:authorize`
  dans les vues en plus de la protection serveur; jeton CSRF sur tous les formulaires. Le mot de passe est vérifié
  **avant** l'état du compte : le message « compte désactivé » n'apparaît qu'avec le bon mot de passe, sans révéler
  l'existence d'un compte.
- **Club de l'organisateur** — jamais un champ de formulaire : déduit de l'utilisateur connecté. Toute action sur un
  événement d'un autre club est refusée par la couche service (HTTP 403). Un utilisateur est responsable d'au plus
  un club (contrainte `UNIQUE` + vérification service).
- **Suppressions** — aucun compte ni événement n'est supprimé physiquement (désactivation / statut `ANNULE`).
  Catégories et lieux utilisés sont protégés par une vérification service avant toute erreur d'intégrité.
- **Affiches** — téléversement facultatif (JPG, PNG, WEBP, GIF, 5 Mo max) enregistré dans `uploads/`, servi sous
  `/uploads/**`. Sans affiche, une image par défaut est affichée.
- **Schéma** — créé par `database/schema.sql`; Hibernate se contente de le valider (`ddl-auto=validate`).
- **Interface** — gabarit commun Thymeleaf (Layout Dialect), navigation adaptée au rôle indiquant la section
  active, messages d'erreur à côté de chaque champ, infobulles d'aide, indicateurs de places (barre + « Complet » /
  « Liste d'attente »), confirmations avant les actions sensibles, pages 403/404 personnalisées, mise en page
  adaptée au mobile. Les polices (Google Fonts) et icônes (Phosphor) sont chargées depuis un CDN; sans Internet,
  l'interface reste fonctionnelle avec les polices système.
- Une modification de rôle par l'administrateur prend effet à la prochaine connexion de l'utilisateur concerné.
