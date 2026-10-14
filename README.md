# CityPath : Calculateur d'Itinéraire GPS & Analyse de Graphes

**CityPath** est une application logicielle légère, robuste et performante développée en Java pur (Java 17+), permettant de modéliser un réseau routier sous forme de graphe pondéré et de déterminer le plus court chemin entre deux villes grâce à une implémentation optimisée de l'algorithme de Dijkstra. Conçue selon les standards d'ingénierie logicielle, elle intègre un chargeur de données CSV tolérant aux anomalies, une structure de graphe à liste d'adjacence, une file de priorité min-heap (`PriorityQueue`), une interface console interactive (CLI) avec mesure temporelle à la microseconde, et une suite rigoureuse de **15 tests automatisés** sous JUnit 5.

---

## 1. Présentation de CityPath

CityPath est un moteur de calcul d'itinéraire autonome pensé pour répondre aux problématiques de recherche opérationnelle et de navigation routière, sans dépendre de bibliothèques tierces superflues ou de frameworks lourds.

L'application prend en charge l'intégralité du cycle de traitement :
- **Importation de cartographies** : chargement et validation de réseaux routiers depuis des fichiers CSV ou flux tabulaires.
- **Modélisation de graphe par liste d'adjacence** : représentation optimisée en mémoire (`Map<Ville, List<Route>>`) garantissant une empreinte minimale ($O(V + E)$).
- **Moteur algorithmique de Dijkstra** : calcul du chemin le plus court avec arrêt anticipé (*early exit*) et relâchement d'arêtes optimisé par une file de priorité (`PriorityQueue`).
- **Restitution détaillée du trajet** : affichage de la distance totale, du nombre d'étapes et du kilométrage intermédiaire tronçon par tronçon.
- **Interface console ergonomique (CLI)** : navigation interactive avec recherche insensible à la casse, détection des villes orphelines et retour de diagnostic en direct.

---

## 2. Problème Identifié

Dans les systèmes de navigation GPS, la logistique de livraison ou la planification de tournées, la recherche du chemin optimal se heurte fréquemment à plusieurs contraintes d'architecture et de performance :

- **Inefficacité des approches matricielles** : l'utilisation d'une matrice d'adjacence $V \times V$ entraîne une consommation mémoire quadratique ($O(V^2)$), particulièrement pénalisante sur des réseaux routiers étendus où la plupart des nœuds ne sont connectés qu'à un nombre restreint de voisins (graphe creux).
- **Lenteur des algorithmes non indexés** : une recherche naïve de Dijkstra sans file de priorité min-heap parcourt l'ensemble des sommets à chaque itération ($O(V^2)$), induisant une latence inacceptable lors des calculs d'itinéraires en temps réel.
- **Fragilité face aux données sources** : les fichiers de routes issus de systèmes tiers ou de saisies manuelles contiennent souvent des hétérogénéités (séparateurs variés, espaces parasites, commentaires, doublons ou liens orphelins).
- **Détection des raccourcis indirects** : sur un réseau routier réel, la liaison directe entre deux métropoles n'est pas toujours la plus rapide ni la plus courte. Un moteur GPS doit être capable d'arbitrer mathématiquement en faveur d'un itinéraire multi-étapes si celui-ci offre un kilométrage inférieur.

---

## 3. Solution Proposée

CityPath résout ces problématiques via une architecture logicielle découplée et un pipeline de traitement strict :

```text
CSV (Réseau routier)
       ↓
CsvLoader (Parsing tolérant, détection auto [,] ou [;], filtrage commentaires #)
       ↓
Modèle Graphe (Liste d'adjacence Map<Ville, List<Route>>, routes bi ou mono-directionnelles)
       ↓
Moteur Dijkstra (File de priorité min-heap PriorityQueue & SommetDistance)
       ↓
ResultatTrajet (Reconstruction optimale inverse en O(V) & métriques kilométriques)
       ↓
Console CLI Interactive (Recherche dynamique, calcul en µs & mode démonstration)
```

- **Empreinte mémoire maîtrisée** : modélisation par liste d'adjacence réduisant la complexité spatiale à $O(V + E)$.
- **Performance quasi-linéaire** : exécution de Dijkstra en $O((V + E) \log V)$ grâce à l'association `PriorityQueue` et `SommetDistance`.
- **Intégrité relationnelle des sommets** : redéfinition stricte des méthodes `equals()` et `hashCode()` sur l'entité `Ville` pour fiabiliser les opérations dans les tables de hachage.
- **Zéro plantage & robustesse** : isolation des erreurs de syntaxe dans les CSV sans interrompre la lecture, gestion naturelle des composantes non connexes (villes isolées comme Ajaccio/Corse) et cas triviaux (départ = arrivée).

---

## 4. Technologies Utilisées

Le socle technique privilégie la légèreté, l'absence de dépendances externes superflues et la robustesse :

- **Langage** : Java 17+ (LTS, Programmation Orientée Objet moderne, Streams API, collections natives).
- **Gestionnaire de build & cycle de vie** : Apache Maven 3.9+ (`pom.xml` standardisé).
- **Tests automatisés** : JUnit 5 (Jupiter 5.10.2, Surefire Plugin 3.2.5).
- **Algorithmique & Structures de données** :
  - `Map<Ville, List<Route>>` : liste d'adjacence dynamique.
  - `PriorityQueue<SommetDistance>` : min-heap pour l'extraction prioritaire du sommet le plus proche.
  - `Map<Ville, Double>` & `Map<Ville, Ville>` : tables de distances minimales et de prédécesseurs.
  - `HashSet<Ville>` : ensemble des sommets explorés garantissant un temps d'accès en $O(1)$.
- **Entrées/Sorties (I/O)** : Java NIO (`java.nio.file.Files`, `java.nio.file.Path`, `BufferedReader`, `StandardCharsets.UTF_8`).
- **Interface Utilisateur** : Application Console CLI interactive avec mesure du temps d'exécution en microsecondes via `System.nanoTime()`.

---

## 5. Fonctionnement du Moteur Algorithmique & de l'Import CSV

### 5.1. Importation & Tolérance du Chargeur CSV (CsvLoader)
Le parseur CSV implémente une stratégie de lecture résiliente adaptée aux contextes réels :
- **Détection universelle du séparateur** : supporte nativement la virgule (`,`) et le point-virgule (`;`).
- **Encodage UTF-8** : gestion transparente des caractères accentués français (ex: *Bordeaux*, *Nîmes*, *Orléans*).
- **Filtrage des métadonnées** : élimination automatique des lignes vides et des commentaires préfixés par `#`.
- **Bidirectionnalité par défaut** : pour toute route déclarée entre $A$ et $B$, deux arêtes orientées symétriques ($A \to B$ et $B \to A$) sont injectées dans le graphe avec le même poids.
- **Isolation des anomalies** : si une ligne présente un nombre insuffisant d'arguments ou une distance non convertible en nombre, un message d'avertissement est émis sur la sortie d'erreur (`System.err`) et le traitement continue sans interruption.

### 5.2. Moteur Algorithmique de Dijkstra (Dijkstra)
L'algorithme de Dijkstra mis en œuvre repose sur les principes suivants :
1. **Initialisation** :
   - La distance vers la ville de départ est fixée à $0.0$, toutes les autres à $+\infty$.
   - La ville de départ est insérée dans la `PriorityQueue` avec une priorité de $0.0$.
2. **Exploration & Relâchement d'Arêtes** :
   - Extraction du sommet $u$ non visité possédant la plus petite distance provisoire.
   - Si $u$ correspond à la ville de destination, l'algorithme déclenche un arrêt anticipé (*early exit*) : le chemin minimal est mathématiquement garanti.
   - Pour chaque route sortante $(u, v)$ de longueur $w$, si $\text{dist}[u] + w < \text{dist}[v]$, la distance vers $v$ est mise à jour, $u$ est enregistré comme prédécesseur de $v$, et $(v, \text{dist}[v])$ est enfilé dans la `PriorityQueue`.
3. **Reconstruction du Chemin** :
   - Remontée de la table des prédécesseurs depuis l'arrivée jusqu'au départ.
   - Inversion de la liste pour restituer la séquence chronologique des étapes.

---

## 6. Format des Données & Exemple CSV

Le réseau routier pédagogique officiel est fourni dans le fichier [`data/routes.csv`](file:///c:/Users/MON%20PC/Documents/StockVision/CityPath/data/routes.csv).

### Spécification des colonnes
| Colonne | Obligatoire ? | Type attendu | Exemple | Description |
| :--- | :---: | :---: | :--- | :--- |
| `VilleDepart` | **Oui** | Chaîne de caractères | `Toulouse` | Nom de la ville de départ du tronçon |
| `VilleArrivee` | **Oui** | Chaîne de caractères | `Paris` | Nom de la ville d'arrivée du tronçon |
| `DistanceKm` | **Oui** | Décimal ou entier $\ge 0$ | `680` | Distance en kilomètres séparant les deux villes |

### Extrait du fichier data/routes.csv
```csv
# Fichier de routes du réseau routier français
# Format: VilleDepart,VilleArrivee,DistanceKm
Toulouse,Bordeaux,250
Toulouse,Montpellier,240
Toulouse,Paris,680
Bordeaux,Nantes,350
Bordeaux,Paris,590
Montpellier,Marseille,170
Montpellier,Lyon,300
Marseille,Nice,200
Marseille,Lyon,315
Lyon,Paris,460
Lyon,Strasbourg,490
Paris,Lille,220
Paris,Strasbourg,490
Nantes,Paris,380
```

> [!TIP]
> **Détection d'itinéraires alternatifs** : dans le jeu de test unitaire, la liaison directe Toulouse-Paris fait 700 km, alors que l'itinéraire passant par Bordeaux (250 km + 350 km = 600 km) permet un gain de **100 km**. L'algorithme sélectionne automatiquement l'option optimale sans aucune intervention manuelle.

---

## 7. Architecture Générale du Projet

Le code respecte une séparation stricte des responsabilités entre le domaine métier, l'algorithmique, les entrées/sorties et l'interface utilisateur :

```text
CityPath/
├── pom.xml                                    # Descripteur Maven (compilation Java 17 & JUnit 5)
├── mvnw.bat                                   # Script wrapper Maven d'exécution rapide
├── data/
│   └── routes.csv                             # Cartographie routière par défaut (14 liaisons nationales)
├── src/
│   ├── main/java/fr/miage/gps/
│   │   ├── Main.java                          # Point d'entrée & Interface console interactive (CLI)
│   │   ├── algo/                              # Moteur algorithmique de calcul
│   │   │   ├── Dijkstra.java                  # Implémentation optimisée de Dijkstra
│   │   │   └── ResultatTrajet.java            # DTO immuable encapsulant le trajet et ses métriques
│   │   ├── io/                                # Couche d'accès aux fichiers et persistance
│   │   │   └── CsvLoader.java                 # Chargeur et validateur de fichiers CSV
│   │   └── model/                             # Modèle du domaine (Graphe et Réseau routier)
│   │       ├── Ville.java                     # Sommet du graphe (avec equals & hashCode insensibles à la casse)
│   │       ├── Route.java                     # Arête pondérée orientée (source, destination, distance)
│   │       ├── Graphe.java                    # Graphe en liste d'adjacence (Map<Ville, List<Route>>)
│   │       └── SommetDistance.java            # Étape ordonnée pour la PriorityQueue (Comparable)
│   └── test/java/fr/miage/gps/
│       ├── algo/
│       │   └── DijkstraTest.java              # 6 tests unitaires sur l'algorithme de Dijkstra
│       ├── io/
│       │   └── CsvLoaderTest.java             # 3 tests unitaires sur le parsing et la tolérance CSV
│       └── model/
│           └── GrapheTest.java                # 6 tests unitaires sur le modèle de données et la PriorityQueue
└── README.md                                  # Documentation complète du projet
```

---

## 8. Installation

### Prérequis
- **Java JDK 17** ou version ultérieure installé sur votre machine.
- **Apache Maven 3.8+** (ou utilisation de votre IDE favori : IntelliJ IDEA, Eclipse, VS Code).

### 1. Cloner le projet
```bash
git clone https://github.com/Samybekda/CityPath.git
cd CityPath
```

### 2. Compiler le projet
Sous Windows (PowerShell / CMD) :
```powershell
mvn clean compile
```
Sous Linux / macOS :
```bash
mvn clean compile
```

---

## 9. Lancement de l'Application

### Démarrage de la Console Interactive
Pour démarrer le calculateur d'itinéraire en mode interactif :

```powershell
mvn exec:java -Dexec.mainClass="fr.miage.gps.Main"
```
*Ou directement via la machine virtuelle Java :*
```powershell
java -cp target/classes fr.miage.gps.Main
```

### Exemple de Session Console Interactive
```text
=============================================================
        CALCULATEUR D'ITINÉRAIRE GPS
      Moteur de plus court chemin (Algorithme de Dijkstra)
=============================================================
✔ Données chargées avec succès depuis : data/routes.csv
  • Villes enregistrées : 9
  • Routes actives      : 28

Villes disponibles sur la carte :
---------------------------------
Bordeaux  |  Lille  |  Lyon  |  Marseille  |  Montpellier  |  Nantes  |  Nice  |  Paris  |  Strasbourg  |  Toulouse
---------------------------------

Tapez 'quitter' ou 'exit' à tout moment pour sortir du programme.

Ville de départ : Toulouse
Ville d'arrivée : Lille

Calcul du plus court chemin en cours...
✔ Itinéraire optimal trouvé en 245 µs !
-------------------------------------------------------------
Départ       : Toulouse
Arrivée      : Lille
Distance tot : 900.0 km
Étapes (2 segments) :
   1. [Départ]  Toulouse
   2. -> Paris        (+680.0 km)
   3. -> Lille        (+220.0 km)
-------------------------------------------------------------
```

---

## 10. Tests Automatisés (JUnit 5)

La fiabilité logicielle est validée par une suite de **15 tests automatisés** couvrant les cas nominaux, les cas limites (*edge cases*) et la résilience aux pannes :

```powershell
mvn test
```

### Détail des 15 tests exécutés

#### Algorithme de Dijkstra (DijkstraTest - 6 tests)
1. `testCheminIndirectPlusCourt` : vérifie que Dijkstra privilégie un trajet indirect à étapes s'il est plus court qu'une liaison directe.
2. `testDepartEgalArrivee` : valide le cas où la destination est identique au départ (distance $= 0.0$ km et étape unique).
3. `testVilleIsoleeNonAtteignable` : confirme la détection propre d'une ville sans aucune connexion (ex: Ajaccio) sans provoquer de boucle infinie ni d'erreur d'exécution.
4. `testRechercheParNomString` : valide la recherche par chaînes de caractères avec insensibilité à la casse.
5. `testVilleInexistante` : vérifie la gestion sécurisée lorsqu'une ville demandée est totalement absente du réseau.
6. `testMultiEtapesMarseilleParis` : contrôle l'exactitude d'un parcours à escales multiples (Marseille $\to$ Lyon $\to$ Paris).

#### Modèle du Graphe & Structures (GrapheTest - 6 tests)
7. `testEgaliteVilles` : vérifie le contrat `equals()` et `hashCode()` entre deux instances représentant la même ville (insensible à la casse).
8. `testAjouterVille` : teste l'insertion unitaire d'un sommet et l'initialisation de sa liste d'adjacence.
9. `testAjouterRouteBidirectionnelle` : contrôle la création automatique des arêtes aller et retour lors de l'ajout d'une route à double sens.
10. `testAjouterRouteSensUnique` : confirme que l'ajout d'une route à sens unique n'ajoute que l'arête orientée source $\to$ destination.
11. `testGetVilleParNom` : valide la recherche d'une ville par son nom avec tolérance aux majuscules/minuscules et aux espaces superflus.
12. `testPriorityQueueSommetDistance` : valide le comportement du min-heap via l'interface `Comparable` de `SommetDistance` (le sommet le plus proche sort toujours en tête).

#### Chargeur & Validation CSV (CsvLoaderTest - 3 tests)
13. `testChargementFichierOfficiel` : s'assure du chargement complet du fichier `data/routes.csv` et de la cohérence des itinéraires calculés sur ces données réelles.
14. `testFichierAvecCommentairesEtLignesVides` : confirme l'omission correcte des commentaires `#` et des lignes blanches via un répertoire temporaire JUnit (`@TempDir`).
15. `testFichierInexistant` : vérifie la levée propre d'une exception `IOException` si le fichier source est introuvable.
