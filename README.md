# Invoicy - Application de Facturation pour Freelances

Application Android complète de facturation développée en Kotlin avec Jetpack Compose.

## 🚀 Fonctionnalités

### ✨ Fonctionnalités Principales
- **Gestion des factures** : Création, modification, duplication et suivi des factures
- **Gestion des devis** : Création de devis et conversion en factures
- **Gestion des clients** : Base de données complète des clients avec historique
- **Génération PDF** : Création de PDF professionnels avec logo et personnalisation
- **Partage multi-canal** : Email, WhatsApp, téléchargement direct
- **Tableau de bord** : Vue d'ensemble des revenus et statistiques
- **Multi-langue** : Support complet de l'anglais, français et arabe (avec RTL)

### 🎨 Interface & UX
- Design Material 3 moderne
- Mode sombre/clair
- Support RTL complet pour l'arabe
- Onboarding au premier lancement
- Couleur principale personnalisable

### 💾 Données
- Base de données locale Room (pas de serveur requis)
- Sauvegarde automatique
- Calculs automatiques (HT, TVA, TTC, remises)
- Numérotation automatique des factures

### 💰 Monétisation
- Version gratuite : 5 factures/mois
- Système de paywall prêt pour Google Play Billing
- Flag Premium dans les préférences

## 🛠️ Stack Technique

- **Langage** : Kotlin
- **UI** : Jetpack Compose avec Material 3
- **Architecture** : MVVM + Repository Pattern
- **Base de données** : Room
- **Navigation** : Jetpack Navigation Compose
- **Injection de dépendances** : Hilt
- **Préférences** : DataStore
- **Génération PDF** : iText7
- **Async** : Kotlin Coroutines & Flow

## 📁 Structure du Projet

```
app/src/main/java/com/invoicy/app/
├── data/
│   ├── dao/              # Data Access Objects
│   ├── database/         # Configuration Room
│   ├── entity/           # Entités de la base de données
│   ├── preferences/      # Gestion des préférences utilisateur
│   └── repository/       # Repositories (couche d'accès aux données)
├── di/                   # Modules Hilt
├── ui/
│   ├── navigation/       # Configuration de la navigation
│   ├── screen/           # Écrans Compose
│   ├── theme/            # Thème Material 3
│   └── viewmodel/        # ViewModels
├── utils/                # Utilitaires (PDF, partage)
├── InvoicyApplication.kt
└── MainActivity.kt
```

## 🗄️ Schéma de Base de Données

### Entités
- **Client** : Informations client (nom, email, téléphone, adresse, pays)
- **Invoice** : Facture (numéro, dates, statut, notes, remise)
- **InvoiceItem** : Ligne de facture (description, quantité, prix, TVA)
- **Quote** : Devis (même structure que Invoice)
- **QuoteItem** : Ligne de devis (même structure que InvoiceItem)

### Relations
- Client → Factures (1-N)
- Client → Devis (1-N)
- Facture → Lignes de facture (1-N)
- Devis → Lignes de devis (1-N)

## 🌍 Internationalisation

L'application supporte 3 langues :
- **Anglais** (en) - par défaut
- **Français** (fr)
- **Arabe** (ar) - avec support RTL automatique

Tous les textes sont dans `res/values/strings.xml` et ses variantes.

## 📄 Génération PDF

Les PDF générés incluent :
- Logo de l'entreprise (optionnel)
- Informations de l'émetteur
- Informations du client
- Tableau détaillé des prestations
- Calculs automatiques (HT, TVA, TTC)
- Remises
- Pied de page personnalisé
- Numéro et dates

## 🔧 Configuration Requise

- **Min SDK** : 24 (Android 7.0)
- **Target SDK** : 34 (Android 14)
- **Compile SDK** : 34
- **Kotlin** : 1.9.20
- **Gradle** : 8.2.0

## 📦 Dépendances Principales

```kotlin
// Compose
androidx.compose.material3:material3
androidx.navigation:navigation-compose

// Room
androidx.room:room-runtime:2.6.1
androidx.room:room-ktx:2.6.1

// Hilt
com.google.dagger:hilt-android:2.48

// DataStore
androidx.datastore:datastore-preferences:1.0.0

// PDF
com.itextpdf:itext7-core:7.2.5

// Coil (images)
io.coil-kt:coil-compose:2.5.0
```

## 🚀 Installation & Lancement

1. Cloner le projet
2. Ouvrir dans Android Studio
3. Synchroniser Gradle
4. Lancer sur émulateur ou appareil physique

```bash
./gradlew assembleDebug
```

## 📱 Écrans de l'Application

1. **Splash Screen** - Écran de démarrage
2. **Onboarding** - 3 slides de présentation
3. **Dashboard** - Vue d'ensemble (revenus, statistiques)
4. **Liste Factures** - Toutes les factures avec recherche
5. **Détail Facture** - Aperçu et actions (PDF, partage)
6. **Édition Facture** - Création/modification
7. **Liste Devis** - Tous les devis
8. **Détail Devis** - Aperçu et conversion en facture
9. **Liste Clients** - Tous les clients avec recherche
10. **Détail Client** - Fiche + historique factures
11. **Paramètres** - Profil, langue, devise, thème

## 🎯 Paramètres Émetteur

Configurables dans les paramètres :
- Nom de l'entreprise
- Email professionnel
- Téléphone
- Adresse complète
- Numéro fiscal/SIRET
- Logo (import depuis galerie)
- Devise (EUR, USD, DZD, MAD, TND, etc.)
- Pied de page par défaut

## 📊 Calculs Automatiques

- **Sous-total HT** : Somme des (quantité × prix unitaire)
- **TVA** : Calculée par ligne selon le taux
- **Remise** : Pourcentage ou montant fixe
- **Total TTC** : Sous-total + TVA - Remise

## 🔐 Permissions

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.READ_MEDIA_IMAGES" />
```

## 🎨 Personnalisation

- **Thème** : Clair, Sombre, Système
- **Langue** : EN, FR, AR (changement sans redémarrage)
- **Couleur principale** : Personnalisable
- **Devise** : Multiple devises supportées

## 📝 TODO / Améliorations Futures

- [ ] Écrans de détail et édition complets pour factures/devis
- [ ] Graphiques de revenus (MPAndroidChart)
- [ ] Export CSV/Excel
- [ ] Sauvegarde cloud (Firebase/Drive)
- [ ] Notifications pour factures en retard
- [ ] Templates de factures personnalisables
- [ ] Intégration Google Play Billing pour Premium
- [ ] Widget dashboard
- [ ] Mode multi-entreprise

## 👨‍💻 Développement

Le code est entièrement commenté en français pour faciliter la maintenance.

### Conventions
- Architecture MVVM stricte
- Repository pattern pour l'accès aux données
- Flow pour la réactivité
- Hilt pour l'injection de dépendances
- Compose pour l'UI (pas de XML)

## 📄 Licence

Ce projet est un exemple de développement. Adaptez selon vos besoins.

## 🤝 Contribution

Les contributions sont les bienvenues ! N'hésitez pas à ouvrir des issues ou des pull requests.

---

**Développé avec ❤️ en Kotlin & Jetpack Compose**
