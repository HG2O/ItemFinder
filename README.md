# 🔍 ItemFinder
> Plugin Leaves pour rechercher un item partout sur ta map Minecraft.

![Minecraft](https://img.shields.io/badge/Minecraft-1.21.4-brightgreen?style=flat-square&logo=minecraft)
![Leaves](https://img.shields.io/badge/Leaves-Compatible-green?style=flat-square)
![Java](https://img.shields.io/badge/Java-21-orange?style=flat-square&logo=java)
![License](https://img.shields.io/badge/License-MIT-blue?style=flat-square)

---

## 📖 Description

**ItemFinder** est un plugin léger et performant pour serveurs **Leaves 1.21.4** qui permet aux administrateurs de localiser n'importe quel item sur toute la map en une seule commande.

- 🎒 Recherche dans les inventaires des joueurs en ligne
- 🟣 Recherche dans les ender chests (joueurs en ligne **et hors-ligne**)
- 📦 Recherche dans tous les coffres, tonneaux et shulker boxes chargés
- 🟪 Recherche récursive à l'intérieur des shulker boxes
- 🔎 Compatible avec les items moddés (nom personnalisé, lore, enchantements)
- ⚙️ Messages entièrement configurables via `config.yml`

---

## ✨ Fonctionnalités

| Fonctionnalité | Description |
|---|---|
| 🎒 Inventaires joueurs | Recherche dans tous les inventaires des joueurs connectés |
| 🟣 Ender Chests | Recherche dans les ender chests, même pour les joueurs déconnectés |
| 📦 Conteneurs | Coffres, tonneaux, shulker boxes dans les chunks chargés |
| 🔎 Items moddés | Détection par nom, matériau, lore et enchantements |
| 📄 GUI paginé | Interface graphique avec navigation par pages (45 résultats/page) |
| ⚡ Asynchrone | La recherche tourne en arrière-plan sans freezer le serveur |

---

## 🚀 Installation

1. Télécharge le fichier `.jar` depuis les [Releases](https://github.com/HG2O/ItemFinder/releases)
2. Place-le dans le dossier `/plugins` de ton serveur Leaves
3. Redémarre le serveur
4. Modifie le `config.yml` généré selon tes préférences
5. Utilise `/reload confirm` ou redémarre pour appliquer les changements

---

## 🎮 Utilisation

| Commande | Description |
|---|---|
| `/finditem <nom>` | Recherche un item par nom, matériau, lore ou enchantement |

### Exemples
```
/finditem diamond_sword
/finditem Épée du Roi
/finditem sharpness
/finditem protection
```

---

## ⚙️ Configuration

Le fichier `config.yml` est généré automatiquement au premier démarrage.

```yaml
# Paramètres généraux
search-timeout: 30
scan-unloaded-chunks: false

# Messages personnalisables (supporte les codes &)
messages:
  prefix: "&8[&b&lItemFinder&8] &r"
  no-permission: "%prefix%&cTu n'as pas la permission d'utiliser cette commande."
  usage: "%prefix%&eUsage : &f/finditem <nom ou matériau>"
  searching: "%prefix%&bRecherche de &f\"%query%\"&b en cours..."
  no-results: "%prefix%&cAucun item trouvé pour &f\"%query%\"&c."
  results-found: "%prefix%&a%count% résultat(s) trouvé(s) !"
  player-only: "%prefix%&cCette commande est réservée aux joueurs."
```

### Variables disponibles

| Variable | Description |
|---|---|
| `%prefix%` | Remplacé par le préfixe configuré |
| `%query%` | Remplacé par le terme recherché |
| `%count%` | Remplacé par le nombre de résultats |

---

## 🛠️ Compilation (développeurs)

### Prérequis
- Java 21+
- Maven 3.8+

### Build

```bash
git clone https://github.com/HG2O/ItemFinder.git
cd ItemFinder
mvn clean package
```

Le `.jar` compilé se trouve dans `target/ItemFinder-1.0-SNAPSHOT.jar`.

### Dépendances

```xml
<dependency>
    <groupId>org.leavesmc.leaves</groupId>
    <artifactId>leaves-api</artifactId>
    <version>1.21.4-R0.1-SNAPSHOT</version>
    <scope>provided</scope>
</dependency>
```

---

## 🔧 Compatibilité

| Plateforme | Supporté |
|---|---|
| Leaves 1.21.4 | ✅ Oui |
| Paper 1.21.4 | ✅ Oui |
| Folia | ✅ Oui |
| Spigot / Bukkit | ⚠️ Non recommandé |

---

## 🔐 Permissions

| Permission | Description | Défaut |
|---|---|---|
| `itemfinder.use` | Permet d'utiliser `/finditem` | OP |

---

## 📜 Licence

Ce projet est sous licence **MIT** — libre d'utilisation, modification et redistribution.

---

## 👤 Auteur

Développé par **HG2O**.

> *« Rien ne se cache, tout se trouve »* 🔍
