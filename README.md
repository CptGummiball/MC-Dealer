# MCDealer

![Spigot](https://img.shields.io/badge/Spigot-1.20--1.20.1-yellow.svg)
![MIT License](https://img.shields.io/badge/PaperMC-1.20--1.20.1-blue.svg)
![Version](https://img.shields.io/badge/Version-1.2.5-gray.svg)
![Support](https://img.shields.io/badge/discontinued-red.svg)
![MIT License](https://img.shields.io/badge/License-MIT-green.svg)

This is the Plugin Version of [mc-dealer-yml2json](https://github.com/wolf128058/mc-dealer-yml2json)
It provides an WebServer + Website that comprehensively displays offers and demands from players and admin shops
of the Plugin [Villager Market](https://www.spigotmc.org/resources/villager-market-the-ultimate-shop-plugin.82965/) ([GitHub](https://github.com/Bestem0r/VillagerMarket))

## Features:
See [mc-dealer-yml2json](https://github.com/wolf128058/mc-dealer-yml2json)

## Installation:
Simply place the MCDealer.jar file into the "plugins" folder of your Bukkit, Spigot, or Paper server.

## Config.yml:
### MCDealer Plugin Settings

- `web-server-port`:
  - Port of the Web Server.
  - Must be an open port. Check with your server provider to find an open port. Most providers have port 8080 open


- `UpdateInterval`:
  - How often should the shop data be updated? (in ticks: 20T = 1 second || Default: 6000 = 5 minutes)
  - VillagerMarket updates data every 10 minutes by default


- `currencySymbol`:
  - Currency symbol displayed on the website.


- `currencySymbolPosition`:
  - Position of the currency symbol (before/after).


- `defaultLanguage`: 
  - Default language of the website. (cn, de, en, es, fr, it, pl, pt, ua)


- `internal-language`:
  - Language of the plugins internal messages (cn, de, en, es, fr, it, pl, pt, ua)
  - This only affects in-game messages. Console remains English.


## Permissions:
- **mcdealer.use:** 'Allows general access to the' `/mcdealer` 'command.'
- **mcdealer.hideshop:** 'Allows hiding your shops with' `/mcdealer hideshop`'.'
- **mcdealer.showshop:** 'Show your shops again with' `/mcdealer showshop`'.'
- **mcdealer.list:** 'Show list of all hidden Shops with' `/mcdealer listhidden`'.'
- **mcdealer.restart:** 'Allows restarting the web server with' `/mcdealer restart`'.'
- **mcdealer.admin:** 'Allows to use every command.'

## Important Notes:
**Make sure the defined port is open for the server**

## Support and Issues:

For support or reporting issues, please visit the [MCDealer GitHub repository](https://github.com/CptGummiball/MC-Dealer/) and open a new [issue.](https://github.com/CptGummiball/MC-Dealer/issues)

## License:
This plugin is released under the MIT License. See the [LICENSE](LICENSE) file for details.

Enjoy using MCDealer!
