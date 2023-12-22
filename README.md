# MCDealer Plugin Readme
This is the Plugin Version of [mc-dealer-yml2json](https://github.com/wolf128058/mc-dealer-yml2json)
It provides an WebServer + Website that comprehensively displays offers and demands from players and admin shops
of the Plugin [Villager Market](https://www.spigotmc.org/resources/villager-market-the-ultimate-shop-plugin.82965/) ([GitHub](https://github.com/Bestem0r/VillagerMarket))

## Features:
See [mc-dealer-yml2json](https://github.com/wolf128058/mc-dealer-yml2json)

## Installation:
Simply place the MCDealer.jar file into the "plugins" folder of your Bukkit, Spigot, or Paper server.

## Config.yml:
### MCDealer Plugin Settings

- web-server-port:
  - Port of the Web Server.
  - Must be an open port. Check with your server provider to find an open port. Most providers have port 8080 open


- UpdateInterval:
  - How often should the shop data be updated? (in ticks: 20T = 1 second || Default: 6000 = 5 minutes)
  - VillagerMarket updates data every 10 minutes by default


- currencySymbol:
  - Currency symbol displayed on the website.


- currencySymbolPosition:
  - Position of the currency symbol (before/after).


- defaultLanguage: 
  - Default language of the website. (cn, de, en, es, fr, it, pl, pt, ua)


- internal-language: en
  - Language of the plugins internal messages (cn, de, en, es, fr, it, pl, pt, ua)
  - This only affects in-game messages. Console remains English.


## Permissions:
  mcdealer.use:
    description: 'Allows general access to the /mcdealer command.'
    default: op
  mcdealer.hideshop:
    description: 'Allows hiding your shops with /mcdealer hideshop.'
    default: op
  mcdealer.showshop:
    description: 'Show your shops again with /mcdealer showshop.'
    default: op
  mcdealer.list:
    description: 'Show list of all hidden Shops with /mcdealer listhidden.'
    default: op
  mcdealer.restart:
    description: 'Allows restarting the web server with /mcdealer restart.'
    default: op
  mcdealer.admin:
    description: 'Allows to use every command.'
    default: op

## Important Notes:
**Make sure the defined port is open for the server**

## Support and Issues:

For support or reporting issues, please visit the [MCDealer GitHub repository](https://github.com/CptGummiball/MC-Dealer/) and open a new [issue.](https://github.com/CptGummiball/MC-Dealer/issues)

## License:
This plugin is released under the MIT License. See the [LICENSE](LICENSE) file for details.

Enjoy using MCDealer!
