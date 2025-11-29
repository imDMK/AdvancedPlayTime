# ⏳ Advanced PlayTime Plugin

[![Build Status](https://github.com/imDMK/AdvancedPlayTime/actions/workflows/gradle.yml/badge.svg)](https://github.com/imDMK/AdvancedPlayTime/actions/workflows/gradle.yml)
![JDK](https://img.shields.io/badge/JDK-1.21-blue.svg)
![Supported versions](https://img.shields.io/badge/Minecraft-1.21--1.21.10-green.svg)
[![SpigotMC](https://img.shields.io/badge/SpigotMC-yellow.svg)](https://www.spigotmc.org/resources/%E2%8F%B0%EF%B8%8F-advancedplaytime-1-21-1-21-10.130458/)
[![Modrinth](https://img.shields.io/badge/Modrinth-1bd96a.svg)](https://modrinth.com/plugin/advancedplaytime)
[![bStats](https://img.shields.io/badge/bStats-00695c)](https://bstats.org/plugin/bukkit/PlayTime/19362)

> **Track it. Visualize it. Control it.**
>
> AdvancedPlayTime is a powerful and ultra-efficient plugin that allows players to check their playtime and compare it with others — all in a stunning, fully customizable GUI.

---

### ✨ Key Features
- 🧠 **Highly optimized** – Zero-lag performance, even on large servers.
- 🎨 **Fully customizable GUIs** – Design the look and feel to fit your server's style.
- 🔢 **Live top-time rankings** – View top active players in multiple display modes.
- 🔧 **Offline time tracking** – Keeps tracking even when you're offline.
- 🛠️ **Placeholders & Adventure support** – Seamless integration with popular libraries.
- 🔁 **Reset & edit support** – Adjust playtimes or wipe all data easily.
- 💬 **Flexible notifications** – Chat, ActionBar, Title or Subtitle? Your choice.
- 🧩 **Multiple GUI types** – Paginated, scrolling horizontal/vertical, and more.
- 💾 **Supports SQLite & MySQL** – Your data, your way.

---

### 🖼️ Preview

#### 🏆 Top PlayTime GUI  
![Top GUI](assets/top.gif)

#### ⌛ Checking Your Time  
![Check Time](assets/time.gif)

#### 🧹 Resetting Time  
![Reset Time](assets/reset.gif)

---

### 🔐 Command Permissions

| Command                     | Permission                        |
|-----------------------------|-----------------------------------|
| `/playtime`                 | `command.playtime`                |
| `/playtime <target>`        | `command.playtime.target`         |
| `/playtime top`             | `command.playtime.top`            |         
| `/playtime top invalidate` | `command.playtime.top.invalidate`  |
| `/playtime set`             | `command.playtime.set`            |
| `/playtime reset`           | `command.playtime.reset`          |
| `/playtime reset-all`       | `command.playtime.reset.all`      |
| `/playtime reload`          | `command.playtime.reload`         |

---

### 🖥️ GUI Types

| Type                  | Description                                                       |
|-----------------------|-------------------------------------------------------------------|
| `STANDARD`            | Basic GUI (recommended if less than 10 players in ranking)        |
| `PAGINATED`           | Multi-page GUI with item navigation                               |
| `SCROLLING_VERTICAL`  | Scroll through entries vertically                                 |
| `SCROLLING_HORIZONTAL`| Scroll through entries horizontally                               |

---

### 🔔 Notification Types

- `CHAT`  
- `ACTIONBAR`  
- `TITLE`  
- `SUBTITLE`  

---

### 🗃️ Supported Databases

- `SQLITE`  
- `MYSQL`  
- `H2`
- `SQL`
- `Postgresql`

---

### 🧩 PlaceholderAPI

| Placeholder          | Description                                                             |
|----------------------|-------------------------------------------------------------------------|
| `%advancedplaytime%` | Displays player's playtime in default readable format (e.g., `10h 35m`) |

---

### ❓ Why isn’t my time updated instantly?

To maximize performance, time is updated on player join/leave and periodically via a background task.

---

### 💡 Feedback & Support

Have a suggestion, found a bug, or want to contribute?  
👉 [Open an issue here](https://github.com/imDMK/AdvancedPlayTime/issues)

---

### ⭐ Like the plugin?

If you enjoy using PlayTime, consider leaving a positive review or star on [SpigotMC](https://www.spigotmc.org/resources/advancedplaytime.111938/) or [GitHub](https://www.spigotmc.org/resources/%E2%8F%B0%EF%B8%8F-advancedplaytime-1-21-1-21-10.130458/) — it really helps!
