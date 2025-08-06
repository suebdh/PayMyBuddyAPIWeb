# Pay My Buddy

Application web permettant aux utilisateurs de gérer leurs finances personnelles et d’effectuer des paiements sécurisés
à leurs amis.

---

## Table des matières

- [Description](#description)
- [Objectifs](#objectifs)
- [Architecture](#architecture)
- [Technologies utilisées](#technologies-utilisées)
- [Installation](#installation)
- [Base de données](#base-de-données)
- [Fonctionnalités](#fonctionnalités)
- [Améliorations futures](#améliorations-futures)

---

## Description

**Pay My Buddy** est une application web Java avec Spring Boot permettant :

- La gestion d’un compte utilisateur et de ses informations personnelles.
- Le suivi des transactions financières.
- L’envoi d’argent sécurisé à ses amis.
- L’alimentation du compte par virement bancaire.

Ce projet comprend :

1. **Une base de données relationnelle** conçue à partir d’un Modèle Physique de Données (MPD) optimisé et sécurisé.
2. **Une application web** avec couche d’accès aux données (DAL), logique métier et interface utilisateur inspirée des
   maquettes Figma fournies.

---

## Objectifs

- Concevoir et implémenter une base de données sécurisée.
- Développer une application web avec **Java / Spring Boot**.
- Mettre en place la gestion des transactions financières.
- Respecter les bonnes pratiques d’ergonomie, d’accessibilité (WCAG) et de sécurité.

---

## Architecture

L’application est organisée en plusieurs couches :

- **Controller** : Gestion des requêtes HTTP et communication avec la couche service.
- **Service** : Contient la logique métier.
- **Repository / DAL** : Gestion des accès et requêtes vers la base de données.
- **Model** : Entités JPA représentant les tables de la base de données.

---

## Technologies utilisées

- **Java 21**
- **Spring Boot** (Web, Data JPA, Security)
- **Maven** pour la gestion des dépendances
- **MySQL** comme BDD
- **Thymeleaf** pour le rendu côté serveur
- **Git** pour le versionning
- **JUnit 5** / **Mockito** / **Spring Boot Test** pour les tests unitaires et les tests d'intégration
- **Jacoco** pour la couverture de code
- **Log4j2** pour le logging

---

## Installation

1. **Cloner le projet**
   ```bash
   git clone https://github.com/suebdh/PayMyBuddyAPIWeb.git
   cd PayMyBuddyAPIWeb 

2. **Installer les dépendances Maven**
    ```bash
    mvn clean install

## Configuration

1. **Créer la base de données MySQL**

    - **Méthode 1** : Lancer le script SQL (par exemple via MySQL Workbench ou en ligne de commande) pour créer la base et les tables :
      ```bash
      mysql -u root -p < src/main/resources/script/schema.sql
      ```

    - **Méthode 2** : Directement depuis MySQL
      ```sql
      CREATE DATABASE paymybuddy;
      USE paymybuddy;
      source src/main/resources/script/schema.sql
      ```

2. **Configurer les identifiants dans src/main/resources/application.properties**
Important : Créer deux variables d'environnement système DB_USERNAME et DB_PASSWORD avant de lancer l'application
   - DB_USERNAME : le nom d’utilisateur MySQL
   - DB_PASSWORD : le mot de passe MySQL
      ```properties
      spring.datasource.url=jdbc:mysql://localhost:3306/paymybuddy?serverTimezone=UTC
      spring.datasource.username=${DB_USERNAME}
      spring.datasource.password=${DB_PASSWORD}
      spring.jpa.properties.hibernate.dialect = org.hibernate.dialect.MySQLDialect
      spring.jpa.hibernate.ddl-auto=update
      spring.jpa.show-sql=true

3. **Lancer l’application**
    ```bash
    mvn spring-boot:run

4. **Accéder à l’application**
   
   - URL : http://localhost:8080 (port de l'application web Spring Boot)
   - Port MySQL : 3306 (pour la base de données)

## Base de données

Le **Modèle Physique de Données (MPD)** définit la structure des principales tables et les relations utilisées par
l’application **Pay My Buddy**.

### Tables principales

- **`app_user`** : Stocke les informations des utilisateurs (nom, email, mot de passe chiffré, solde, date de création).
- **`app_transaction`** : Historique des transactions effectuées et reçues (description, montant, frais, date de
  création).
- **`user_friendship`** : Liste des relations d’amitié enregistrées entre les utilisateurs.

### Diagramme EER

Le schéma ci-dessous illustre les tables et leurs relations dans la base de données :

![Diagramme EER de la base de données](./PayMyBuddy_MPD.png)

> **Note :**
> - Diagramme généré à partir de **MySQL Workbench**.
> - Le fichier source est disponible dans `docs/PayMyBuddy_MPD.mwb` pour modification si nécessaire.
> - Ce diagramme pourra être mis à jour en fonction des évolutions du modèle.

### Création de la base de données

Créez le schéma de la base de données MySQL à l’aide du fichier :  
`/src/main/resources/script/schema.sql`.

## Usage

- Créer un compte utilisateur
- Se connecter avec ses identifiants
- Ajouter un ami à partir de son adresse email
- Envoyer de l’argent à un ami existant
- Consulter l’historique de ses transactions
- Alimenter son compte via un compte bancaire lié

## Fonctionnalités

- Authentification sécurisée
- Gestion des utilisateurs
- Gestion des amis
- Envoi et réception de paiements
- Suivi du solde
- Historique détaillé des transactions

## API Endpoints

| Method | URL                   | Paramèters                | Description & Details                               |
|--------|-----------------------|---------------------------|-----------------------------------------------------|
| POST   | `/api/users/register` | -                         | crée un nouvel utilisateur                          |
| POST   | `/api/users/login`    | -                         | authentifie un utilisateur et retourne un token JWT |
| GET    | `/api/users/me`       | -                         | récupère les informations de l’utilisateur connecté |
| POST   | `/api/contacts`       | `email (body)`            | ajoute un ami par email                             |
| GET    | `/api/contacts`       | -                         | liste des amis                                      |
| POST   | `/api/transactions`   | `destinataireId, montant` | 	envoie de l’argent à un ami                        |
| GET    | `/api/transactions`   | -                         | historique des transactions de l’utilisateur        |

## Améliorations futures

- Interface responsive
- Notifications (email / push) pour les paiements
- Export de l’historique en PDF ou CSV
- Support multi-devises
- Intégration avec d’autres moyens de paiement