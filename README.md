# SpentTime

[![Build Status](https://github.com/imDMK/SpentTime/actions/workflows/gradle.yml/badge.svg)](https://github.com/imDMK/SpentTime/actions/workflows/gradle.yml)
![JDK](https://img.shields.io/badge/JDK-1.17-blue.svg)
![Supported versions](https://img.shields.io/badge/Minecraft-1.17--1.20.1-green.svg)
[![SpigotMC](https://img.shields.io/badge/SpigotMC-yellow.svg)](https://www.spigotmc.org/resources/spenttime.111938/)
[![Bukkit](https://img.shields.io/badge/Bukkit-blue.svg)](https://dev.bukkit.org/projects/spenttime)
[![PaperMC](https://img.shields.io/badge/Paper-004ee9.svg)](https://hangar.papermc.io/imDMK/SpentTime)
[![bStats](https://img.shields.io/badge/bStats-00695c)](https://bstats.org/plugin/bukkit/SpentTime/19362)

An efficient plugin for your time spent in the game with many features and configuration possibilities.

# Features
* Top players spent time (with Gui).
* Configurable items in top players spent time.
* Ability to change the type of top players who spent time GUI.
* Ability to decorate the top players spent time gui.
* Possibility to set a custom number of players displayed in the spent time top.
* Checking the time spent while the player is offline.
* Resetting a player's spent time.
* Reset spent time for all players on the server.
* [Placeholder API](https://github.com/PlaceholderAPI/PlaceholderAPI) support.
* [Adventure](https://github.com/KyoriPowered/adventure) components support.

# FAQ
### **Q: What are the available placeholder formats?**
**A:** At the moment there are currently 2 placeholders available:
* `%spent-time%` - Displays the value in milliseconds of the player's time spent.
* `%spent-time-formatted%` - Displays the converted value in human-readable (e.g. 10h 30m) of the player's spent time.

#### **Q: What are the notification types?**
**A:** CHAT, ACTIONBAR, TITLE, SUBTITLE, DISABLED

### **Q: What are the types of top players who spend time gui?**
**A:** STANDARD, PAGINATED

### **Q: What database types are supported by this plugin?**
**A:** We currently support the following databases: SQLite, MYSQL, MARIADB

#### **Q: Why doesn't my time in the top count immediately?**
**A:** This is specifically done to make the plugin efficient. The player's time updates when entering and exiting the server and there is an additional task that updates the spent time of all players. You can change its frequency in the configuration by changing `playerSpentTimeSaveDuration`.

# Information
If you have any suggestions or find a bug, please report it using [this](https://github.com/imDMK/SpentTime/issues) site.
