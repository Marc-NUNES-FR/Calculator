# 🧮 Calculator — Calculatrice & Convertisseur Android

[![Build APK](https://github.com/Marc-NUNES-FR/Calculator/actions/workflows/build-apk.yml/badge.svg)](https://github.com/Marc-NUNES-FR/Calculator/actions/workflows/build-apk.yml)
[![Licence GPL 3.0](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0.html)
[![Crowdin](https://d2gma3rgtloi6d.cloudfront.net/astro-website/_astro/crowdin-logo-light.CZHTv0JL.svg)](https://fr.crowdin.com/project/marc-calculator)

Une application Android moderne, rapide et complète développée en **Kotlin** et **Jetpack Compose**.

---

## ✨ Fonctionnalités

### 🧮 Calculatrice
- **Mode classique** : `+`, `−`, `×`, `÷`, `%`, `±`, virgule, annulation (`AC`), suppression.
- **Mode scientifique** : `sin`, `cos`, `tan`, `π`, `ln`, `log`, `√`, `^`, `EXP`, `x!`, parenthèses.
- **Historique & Copie** : Consultation des calculs passés et copie rapide du résultat dans le presse-papier.

### 🔄 Convertisseur
- **Unités** : Longueur, Masse, Volume, Température, Aire, Vitesse, Temps, Données informatiques (B, KB, MB…).
- **Systèmes numériques** : Conversions simultanées DEC / BIN / OCT / HEX.
- **Date** : Calcul d'écart entre deux dates avec sélecteur d'intervalle.
- **IMC** : Calcul de l'Indice de Masse Corporelle (Poids + Taille) avec interprétation par catégories.
- **Convertisseur IP** : Format décimal pointé, hexadécimal, binaire (4 octets), entier 32 bits, masque réseau.
- **Plages Réseau IP (CIDR)** : Calcul de réseau, broadcast, wildcard, première/dernière adresse, hôtes, classe et curseur CIDR.

### ⚙️ Paramètres
- **Thèmes** : Clair, Sombre ou Synchronisé avec le système.
- **Personnalisation** : Palettes de couleurs d'accentuation, sélecteur personnalisé et arrondi des touches.
- **Gestion** : Réinitialisation rapide des préférences.

---

## 📥 Téléchargement

Pour installer l'application sur votre appareil Android :
1. Rendez-vous dans la section **[Releases](../../releases)** du dépôt GitHub.
2. Téléchargez le fichier `.apk` de la dernière version.
3. Ouvrez le fichier sur votre appareil pour lancer l'installation.

---

## 🛠️ Compilation (Build)

### Prérequis
- Android Studio Ladybug (ou version ultérieure)
- JDK 17
- Android SDK (API 24+)

### Commandes
```bash
# Cloner le dépôt
git clone [https://github.com/Marc-NUNES-FR/Calculator.git](https://github.com/Marc-NUNES-FR/Calculator.git)
cd Calculator

# Compiler l'APK Debug
./gradlew assembleDebug
