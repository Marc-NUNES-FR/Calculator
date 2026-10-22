# 🧮 Calculator — Calculatrice & Convertisseur

Application Android **Kotlin + Jetpack Compose**, refonte fidèle de l'application d'origine.

## ✨ Fonctionnalités

### 🧮 Calculatrice
- Mode classique : +, −, ×, ÷, %, ±, virgule, AC, suppression
- Mode scientifique : sin, cos, tan, π, ln, log, √, ^, EXP, x!, parenthèses
- Historique des calculs
- Copie du résultat (« Copié dans le presse-papier »)

### 🔄 Convertisseur
- **Unités** : Longueur, Masse, Volume, Température, Aires, Vitesse, Temps, Données (B, KB, MB…) — Valeur / De / À / Résultat copiable
- **Système numérique** : DEC / BIN / OCT / HEX
- **Date** : écart entre deux dates (sélecteur)
- **IMC** : Poids + Taille → Votre IMC (avec catégories)
- **Convertisseur IP** : décimal pointé, hexadécimal, binaire (4 octets), entier 32 bits, masque réseau
- **Plages Réseau IP (CNR)** : réseau, broadcast, wildcard, première/dernière adresse, hôtes, classe, binaire — curseur CIDR

### ⚙️ Paramètres
Thème Clair/Sombre/Auto · Couleur d'accentuation (palette + sélecteur personnalisé) · Arrondi des touches · Réinitialisation

## 🌍 Traduction (Crowdin)

Toutes les chaînes de l'interface sont centralisées dans un seul fichier : app/src/main/res/values/strings.xml
Le projet est prêt pour Crowdin (crowdin.yml inclus). Traductions communautaires bienvenues !

## 🛠️ Build
1. Ouvrir le projet dans **Android Studio**
2. Synchronisation Gradle, puis lancer sur émulateur ou appareil (minSdk 24)

## 📄 Licence
MIT — Copyright (c) 2026 Marc NUNES
