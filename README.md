# ⏳ SpentTime

[![Build Status](https://github.com/imDMK/SpentTime/actions/workflows/gradle.yml/badge.svg)](https://github.com/imDMK/SpentTime/actions/workflows/gradle.yml)
![JDK](https://img.shields.io/badge/JDK-1.17-blue.svg)
![Supported versions](https://img.shields.io/badge/Minecraft-1.17--1.21.5-green.svg)
[![SpigotMC](https://img.shields.io/badge/SpigotMC-yellow.svg)](https://www.spigotmc.org/resources/spenttime.111938/)
[![Bukkit](https://img.shields.io/badge/Bukkit-blue.svg)](https://dev.bukkit.org/projects/spenttime)
[![PaperMC](https://img.shields.io/badge/Paper-004ee9.svg)](https://hangar.papermc.io/imDMK/SpentTime)
[![Modrinth](https://img.shields.io/badge/Modrinth-1bd96a.svg)](https://modrinth.com/plugin/spenttime)
[![bStats](https://img.shields.io/badge/bStats-00695c)](https://bstats.org/plugin/bukkit/SpentTime/19362)

> **Track it. Visualize it. Control it.**
>
> SpentTime is a powerful and ultra-efficient plugin that allows players to check their playtime and compare it with others — all in a stunning, fully customizable GUI.

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

#### 🏆 Top Spent Time GUI  
![Top GUI](assets/top.gif)

#### ⌛ Checking Your Time  
![Check Time](assets/time.gif)

#### 🧹 Resetting Time  
![Reset Time](assets/reset.gif)

---

### 🔐 Command Permissions

| Command                     | Permission                    |
|-----------------------------|-------------------------------|
| `/spenttime`                | `command.spenttime`           |
| `/spenttime <target>`       | `command.spenttime.target`    |
| `/spenttime top`            | `command.spenttime.top`       |          
| `/spenttime set`            | `command.spenttime.set`       |
| `/spenttime reset`          | `command.spenttime.reset`     |
| `/spenttime reset-all`      | `command.spenttime.reset.all` |
| `/spenttime reload`         | `command.spenttime.reload`    |
| `/spenttime migrate`        | `command.spenttime.migrate`   |
| `/spenttime migrate cancel` | `command.spenttime.migrate`   |

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

---

### 🧩 PlaceholderAPI

| Placeholder       | Description                                |
|-------------------|--------------------------------------------|
| `%spent-time%`    | Displays player's playtime in readable format (e.g., `10h 35m`) |

---

### ❓ Why isn’t my time updated instantly?

To maximize performance, time is updated on player join/leave and periodically via a background task. You can configure the frequency in `spentTimeSaveDelay`.

---

### 💡 Feedback & Support

Have a suggestion, found a bug, or want to contribute?  
👉 [Open an issue here](https://github.com/imDMK/SpentTime/issues)

---

### ⭐ Like the plugin?

If you enjoy using SpentTime, consider leaving a positive review or star on [SpigotMC](https://www.spigotmc.org/resources/spenttime.111938/) or [GitHub](https://github.com/imDMK/SpentTime) — it really helps!
