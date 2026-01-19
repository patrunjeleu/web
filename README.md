# Système de Recommandation de Destinations de Vacances

**Projet SW 2025 - Technologies Web Sémantiques**

Application web complète utilisant XML, XSD, XSLT, XPath, XQuery, RDF, JENA, OWL et SPARQL pour recommander des destinations de vacances personnalisées.

## 📋 Table des Matières

- [Fonctionnalités](#fonctionnalités)
- [Technologies Utilisées](#technologies-utilisées)
- [Prérequis](#prérequis)
- [Installation dans Eclipse](#installation-dans-eclipse)
- [Structure du Projet](#structure-du-projet)
- [Guide d'Utilisation](#guide-dutilisation)
- [Partie 1: XML](#partie-1-xml)
- [Partie 2: RDF/OWL](#partie-2-rdfowl)
- [API Endpoints](#api-endpoints)
- [Ontologie et Raisonnement](#ontologie-et-raisonnement)

## 🎯 Fonctionnalités

### Partie 1: Gestion XML (10 points)

1. ✅ **Données XML**: 22 destinations de vacances et utilisateurs au format XML
2. ✅ **Schéma XSD**: Validation complète des données XML
3. ✅ **Chargement XML**: Lecture en mémoire des destinations
4. ✅ **Ajout de Destination**: Formulaire avec validation
5. ✅ **Ajout d'Utilisateur**: Formulaire avec validation
6. ✅ **Filtrage par Budget**: Utilise XPath
7. ✅ **Recommandations**: 2/3 critères (budget, disponibilité, activité), trié par ordre croissant
8. ✅ **Transformation XSLT**: Affichage coloré (jaune/vert) selon l'activité préférée
9. ✅ **Détails de Destination**: Recherche par nom avec XPath
10. ✅ **Filtrage par Activité**: Utilise XPath
11. ✅ **Interface Graphique**: UI moderne et responsive

### Partie 2: RDF/OWL (10 points)

1. ✅ **RDF/XML**: Scénario Maria/Mihai avec destinations Roumanie/Paris
2. ✅ **Visualisation RDF**: Upload et affichage du graphe avec D3.js
3. ✅ **Modification RDF**: Ajout et modification de destinations (JENA API)
4. ✅ **Liste RDF**: Affichage de toutes les destinations avec pages détaillées
5. ✅ **Requêtes RDF**: Interrogation des exigences avec affichage textuel et graphique
6. ✅ **Ontologie OWL**: Classes, propriétés, restrictions
7. ✅ **Requêtes SPARQL**: 3 requêtes documentées dans `sparql_owl.txt`
8. ✅ **Raisonnement**: Classification automatique avec EligibleForRomania

## 🛠 Technologies Utilisées

- **Java 11+**: Langage backend
- **Maven**: Gestion des dépendances
- **Servlet API 4.0**: Backend web
- **Saxon HE 12.3**: XSLT 2.0, XPath, XQuery
- **Apache Jena 4.10.0**: RDF/OWL manipulation
- **Jung 2.1.1**: Visualisation de graphes
- **Gson**: JSON serialization
- **D3.js v7**: Visualisation interactive des graphes RDF
- **HTML5/CSS3/JavaScript**: Frontend moderne

## 📦 Prérequis

- **JDK 11 ou supérieur**
- **Apache Maven 3.6+**
- **Eclipse IDE for Enterprise Java Developers** (2021-03 ou plus récent)
- **Apache Tomcat 9.0+** (pour déploiement)
- **Protégé 5.5+** (pour visualiser l'ontologie OWL)
- **GraphDB Free** (optionnel, pour tester les requêtes SPARQL)

## 🚀 Installation dans Eclipse

### Étape 1: Importer le Projet

1. Ouvrez Eclipse IDE
2. Allez dans **File → Import**
3. Sélectionnez **Maven → Existing Maven Projects**
4. Cliquez sur **Next**
5. Parcourez jusqu'au dossier du projet (`vacation-recommender`)
6. Assurez-vous que le fichier `pom.xml` est sélectionné
7. Cliquez sur **Finish**

Eclipse va automatiquement:
- Télécharger toutes les dépendances Maven
- Construire le workspace
- Configurer le projet

### Étape 2: Configurer le Serveur Tomcat

1. Dans Eclipse, allez dans **Window → Preferences**
2. Naviguez vers **Server → Runtime Environments**
3. Cliquez sur **Add...**
4. Sélectionnez **Apache Tomcat v9.0**
5. Pointez vers votre installation Tomcat
6. Cliquez sur **Finish**

### Étape 3: Déployer l'Application

1. Faites un clic droit sur le projet `vacation-recommender`
2. Sélectionnez **Run As → Run on Server**
3. Choisissez votre serveur Tomcat configuré
4. Cliquez sur **Finish**

L'application sera accessible à: `http://localhost:8080/vacation-recommender/`

### Alternative: Ligne de Commande

```bash
# Compiler le projet
mvn clean install

# Le fichier WAR sera généré dans target/vacation-recommender.war
# Déployez-le manuellement dans Tomcat/webapps/
```

## 📁 Structure du Projet

```
vacation-recommender/
├── pom.xml                                 # Configuration Maven
├── README.md                               # Ce fichier
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/vacation/
│   │   │       ├── model/
│   │   │       │   ├── Destination.java    # Modèle Destination
│   │   │       │   └── User.java           # Modèle Utilisateur
│   │   │       ├── service/
│   │   │       │   ├── XMLService.java     # Service XML avec XPath/XQuery
│   │   │       │   └── RDFService.java     # Service RDF avec JENA
│   │   │       └── servlet/
│   │   │           ├── XMLServlet.java     # API REST pour XML
│   │   │           └── RDFServlet.java     # API REST pour RDF
│   │   ├── resources/
│   │   │   ├── data/
│   │   │   │   ├── destinations.xml        # 22 destinations + utilisateurs
│   │   │   │   ├── destinations.xsd        # Schéma de validation
│   │   │   │   ├── destinations.rdf        # Données RDF (Partie 2, Tâche 1)
│   │   │   │   ├── vacation-ontology.owl   # Ontologie OWL (Partie 2, Tâche 6)
│   │   │   │   └── sparql_owl.txt          # 3 requêtes SPARQL (Partie 2, Tâche 7)
│   │   │   └── xslt/
│   │   │       └── destinations.xsl        # Transformation XSLT
│   │   └── webapp/
│   │       ├── WEB-INF/
│   │       │   └── web.xml                 # Configuration Servlet
│   │       ├── index.html                  # Interface principale
│   │       ├── css/
│   │       │   └── style.css               # Styles CSS
│   │       └── js/
│   │           └── app.js                  # Logic JavaScript
│   └── test/
└── target/                                 # Fichiers compilés (généré)
```

## 📖 Guide d'Utilisation

### Partie 1: XML

#### Tâche 1-3: Données et Validation
Les données XML sont automatiquement chargées au démarrage dans:
- `src/main/resources/data/destinations.xml` (22 destinations)
- `src/main/resources/data/destinations.xsd` (schéma de validation)

#### Tâche 4: Ajouter une Destination
1. Ouvrez l'application
2. Dans **Partie 1**, localisez le formulaire "Ajouter une Nouvelle Destination"
3. Remplissez tous les champs (validation automatique)
4. Cliquez sur "Ajouter la Destination"
5. La destination est ajoutée au XML en mémoire et sauvegardée

#### Tâche 5: Ajouter un Utilisateur
1. Localisez le formulaire "Ajouter un Nouvel Utilisateur"
2. Entrez prénom, nom, disponibilité, budget et activité préférée
3. Cliquez sur "Ajouter l'Utilisateur"

#### Tâche 6: Filtrer par Budget
1. Dans la section "Destinations par Budget"
2. Entrez un budget maximum (ex: 700€)
3. Cliquez sur "Rechercher"
4. Les destinations ≤ budget s'affichent (utilise XPath)

#### Tâche 7: Obtenir Recommandations
1. Cliquez sur "Obtenir Recommandations"
2. Le système analyse le premier utilisateur XML
3. Affiche les destinations correspondant à 2/3 critères minimum
4. Résultats triés par budget croissant

#### Tâche 8: Transformation XSLT
1. Cliquez sur "Afficher avec XSLT"
2. Les destinations s'affichent avec:
   - **Fond jaune**: correspond à l'activité préférée
   - **Fond vert**: ne correspond pas

#### Tâche 9: Détails d'une Destination
1. Sélectionnez une destination dans le menu déroulant
2. Cliquez sur "Voir Détails"
3. Toutes les informations s'affichent (utilise XPath)

#### Tâche 10: Filtrer par Activité
1. Choisissez une activité dans le menu déroulant
2. Cliquez sur "Rechercher"
3. Les destinations proposant cette activité s'affichent

### Partie 2: RDF/OWL

#### Tâche 1: Fichier RDF/XML
Le fichier `destinations.rdf` contient:
- Maria Popescu (700€, 5 jours, randonnée)
- Mihai Pavel (500€, 7 jours, camping)
- Roumanie (400€, 4 jours, randonnée principale)
- Paris (600€, 5 jours, gastronomie principale)
- Recommandation Maria → Roumanie

#### Tâche 2: Upload et Visualisation
1. Dans **Partie 2**, localisez "Télécharger et Visualiser RDF"
2. Sélectionnez le fichier `destinations.rdf`
3. Cliquez sur "Télécharger et Visualiser"
4. Le graphe RDF s'affiche avec D3.js
5. Les nœuds sont colorés par type (User, Destination, Activity)

#### Tâche 3: Ajouter/Modifier Destination RDF

**Ajouter:**
1. Onglet "Ajouter"
2. Remplissez le formulaire
3. Cliquez sur "Ajouter à RDF"
4. Utilise JENA API pour créer les triplets RDF

**Modifier:**
1. Onglet "Modifier"
2. Entrez l'URI de la destination (ex: `http://www.vacation.com/destinations/roumanie`)
3. Choisissez la propriété à modifier
4. Entrez la nouvelle valeur
5. Cliquez sur "Modifier"

#### Tâche 4: Liste des Destinations RDF
1. Cliquez sur "Charger Destinations"
2. Utilise une requête SPARQL pour obtenir toutes les destinations
3. Chaque carte affiche: nom, description, budget, durée, URI

#### Tâche 5: Interroger les Exigences
1. Entrez "Roumanie" (ou autre destination)
2. Cliquez sur "Interroger"
3. **Résultat textuel**: Budget, durée, activités
4. **Graphe coloré**: Les nœuds liés à la destination sont en **vert**

#### Tâche 6: Ontologie OWL
L'ontologie `vacation-ontology.owl` contient:
- **Classes**: User, Destination, Activity, Recommendation
- **Sous-classes**: BudgetTraveler, MidRangeTraveler, LuxuryTraveler
- **Propriétés d'objet**: hasActivity, primaryActivity, forUser, recommendsDestination
- **Propriétés de données**: budget, duration, preferredActivity, availability
- **Restrictions**: EligibleForRomania (budget ≥ 400, availability ≥ 4, preferredActivity = "randonnée")

**Visualiser dans Protégé:**
1. Ouvrez Protégé
2. File → Open
3. Sélectionnez `vacation-ontology.owl`
4. Explorez les onglets: Classes, Object Properties, Data Properties, Individuals

**Visualiser dans GraphDB:**
1. Créez un nouveau repository
2. Import → RDF
3. Sélectionnez `vacation-ontology.owl`
4. Explore → Visual Graph

#### Tâche 7: Requêtes SPARQL
Le fichier `sparql_owl.txt` contient 3 requêtes:

1. **Requête 1**: Tous les utilisateurs avec budget et activité préférée
2. **Requête 2**: Toutes les destinations avec activités et budget
3. **Requête 3**: Recommandations avec critères de correspondance

**Exécuter dans Protégé:**
1. Window → Tabs → SPARQL Query
2. Copiez-collez une requête
3. Execute

**Exécuter dans GraphDB:**
1. SPARQL → Query
2. Copiez-collez une requête
3. Run

#### Tâche 8: Classification avec Raisonneur

**Dans Protégé:**
1. Ouvrez `vacation-ontology.owl`
2. Reasoner → Pellet (ou HermiT)
3. Reasoner → Start Reasoner
4. Classes → EligibleForRomania
5. Instances (inferred) → maria-popescu apparaît automatiquement

**Explication:**
Maria Popescu (700€, 5 jours, randonnée) satisfait toutes les restrictions de la classe `EligibleForRomania`:
- Budget ≥ 400€ ✓
- Availability ≥ 4 jours ✓
- PreferredActivity = "randonnée" ✓

Le raisonneur infère automatiquement que Maria appartient à cette classe.

**Sauvegarder:**
- **Ontologie affirmée**: File → Save as → Format: RDF/XML
- **Ontologie déduite**: Reasoner → Export Inferred Axioms as Ontology

## 🌐 API Endpoints

### XML Endpoints

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| GET | `/api/xml/destinations` | Toutes les destinations |
| GET | `/api/xml/user` | Premier utilisateur |
| GET | `/api/xml/activities` | Toutes les activités |
| GET | `/api/xml/byBudget?budget=X` | Destinations par budget |
| GET | `/api/xml/recommended` | Recommandations |
| GET | `/api/xml/byActivity?activity=X` | Destinations par activité |
| GET | `/api/xml/byName?name=X` | Détails d'une destination |
| GET | `/api/xml/transform` | Transformation XSLT |
| POST | `/api/xml/addDestination` | Ajouter destination |
| POST | `/api/xml/addUser` | Ajouter utilisateur |

### RDF Endpoints

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| GET | `/api/rdf/destinations` | Toutes les destinations RDF |
| GET | `/api/rdf/destination?uri=X` | Détails d'une destination |
| GET | `/api/rdf/requirements?name=X` | Exigences d'une destination |
| GET | `/api/rdf/graph` | Données du graphe |
| GET | `/api/rdf/graph/highlight?name=X` | Graphe avec nœuds colorés |
| GET | `/api/rdf/sparql?query=X` | Exécuter requête SPARQL |
| POST | `/api/rdf/upload` | Upload fichier RDF |
| POST | `/api/rdf/addDestination` | Ajouter destination RDF |
| POST | `/api/rdf/updateDestination` | Modifier destination RDF |

## 🔬 Ontologie et Raisonnement

### Classes Principales

```
owl:Thing
├── User
│   ├── BudgetTraveler (budget ≤ 500€)
│   ├── MidRangeTraveler (500€ < budget ≤ 1000€)
│   ├── LuxuryTraveler (budget > 1000€)
│   └── EligibleForRomania (budget ≥ 400€, availability ≥ 4, preferredActivity = "randonnée")
├── Destination
│   ├── AffordableDestination (budget < 500€)
│   ├── MidRangeDestination (500€ ≤ budget ≤ 1000€)
│   └── LuxuryDestination (budget > 1000€)
├── Activity
└── Recommendation
```

### Propriétés d'Objet

- `hasActivity`: Destination → Activity
- `primaryActivity`: Destination → Activity (fonctionnelle)
- `forUser`: Recommendation → User (fonctionnelle)
- `recommendsDestination`: Recommendation → Destination (fonctionnelle)
- `prefersActivity`: User → Activity

### Propriétés de Données

- `firstName`, `lastName`: string
- `budget`: decimal
- `availability`, `duration`: integer
- `preferredActivity`, `activityName`: string
- `name`, `description`: string
- `matchesBudget`, `matchesAvailability`, `matchesPreferredActivity`: boolean

## 📝 Notes pour la Présentation

### Points Clés à Démontrer

1. **XML**: Montrer le fichier XML avec 22+ destinations
2. **XSD**: Ouvrir le schéma et expliquer la validation
3. **Ajout de données**: Démontrer l'ajout d'une destination et d'un utilisateur
4. **XPath**: Expliquer les requêtes dans `XMLService.java`
5. **XSLT**: Montrer la transformation avec les couleurs
6. **RDF**: Afficher le fichier RDF et sa structure
7. **Graphe**: Démontrer la visualisation interactive
8. **Modification RDF**: Ajouter une destination en direct
9. **Ontologie**: Ouvrir dans Protégé, montrer les classes
10. **SPARQL**: Exécuter les 3 requêtes
11. **Raisonnement**: Démontrer la classification automatique

### Fichiers à Préparer pour la Soumission

1. ✅ Code source complet
2. ✅ `destinations.xml` (22+ destinations)
3. ✅ `destinations.xsd`
4. ✅ `destinations.rdf`
5. ✅ `vacation-ontology.owl`
6. ✅ `sparql_owl.txt`
7. 📸 Captures d'écran Protégé (ontologie visualisée)
8. 📸 Captures d'écran GraphDB (requêtes SPARQL exécutées)
9. 📸 Captures d'écran du raisonnement (ontologie affirmée vs. déduite)
10. 📄 Ce README.md

## 🐛 Dépannage

### Erreur: "Cannot find XML file"
- Vérifiez que `destinations.xml` est dans `src/main/resources/data/`
- Rebuild le projet: Project → Clean → Build

### Erreur: "Saxon not found"
- Vérifiez que Saxon est dans `pom.xml`
- Exécutez: `mvn clean install`

### Le graphe ne s'affiche pas
- Vérifiez que D3.js est chargé (connexion internet requise)
- Ouvrez la console du navigateur pour voir les erreurs

### Tomcat ne démarre pas
- Vérifiez que le port 8080 n'est pas utilisé
- Changez le port dans la configuration Tomcat

## 📧 Contact et Support

Pour toute question sur le projet, référez-vous à:
- La documentation Jena: https://jena.apache.org/tutorials/
- La documentation Saxon: https://www.saxonica.com/documentation/
- La spécification OWL 2: https://www.w3.org/TR/owl2-overview/

## ✅ Checklist de Soumission

- [ ] Code source compilé sans erreurs
- [ ] Application testée dans Eclipse + Tomcat
- [ ] Toutes les 11 tâches de la Partie 1 fonctionnent
- [ ] Toutes les 8 tâches de la Partie 2 fonctionnent
- [ ] Fichier RDF créé et testé
- [ ] Ontologie OWL créée et validée dans Protégé
- [ ] 3 requêtes SPARQL testées et documentées
- [ ] Captures d'écran de Protégé et GraphDB incluses
- [ ] Démonstration du raisonnement préparée
- [ ] README.md complet et à jour
- [ ] Projet zippé et prêt pour Moodle
- [ ] Présentation préparée pour les TP

---

**Bonne chance pour la présentation! 🎓**
