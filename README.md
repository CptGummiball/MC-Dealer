# MCDealer Plugin Readme
This is the Plugin Version of [mc-dealer-yml2json](https://github.com/wolf128058/mc-dealer-yml2json)
It provides an WebServer + Website that comprehensively displays offers and demands from players and admin shops
of the Plugin [Villager Market](https://www.spigotmc.org/resources/villager-market-the-ultimate-shop-plugin.82965/) ([GitHub](https://github.com/Bestem0r/VillagerMarket))

## Features:
See [mc-dealer-yml2json](https://github.com/wolf128058/mc-dealer-yml2json)

## USAGE

### Installation:
1.  Download the latest release JAR file from the  [Releases page](https://github.com/CptGummiball/MC-Dealer/releases).
2.  Place the downloaded JAR file in the  `plugins`  folder of your Bukkit/Spigot server.
3.  Start or restart your server.

### Configuration:
1.  Navigate to the  `plugins`  folder and find the  `MCDealer`  folder.
2.  Open the  `config.yml`  file to configure settings.

#### Configuration Options
- `web-server-port`: Port of the Web Server. Must be an open port. Check with your server provider to find an open port. Most providers have port 8080 open

- `ExternalHost`
   - `enabled`: enable or disable external hosting
   - `type`: FTP, FTPS or SFTP
   - `server`: Your FTP/FTPS/SFTP adress
   - `port`: Your FTP/FTPS/SFTP port
   - `username`: Your FTP/FTPS/SFTP username
   - `password`: Your FTP/FTPS/SFTP password
   - `remote-path`: Internal Path of your web server
   - `linktoweb`: Link that will be displayed with `/mcdealer link`
   - `StrictHostKeyChecking`: Enable(yes) or disable(no) strict host key checking (Only for SFTP)

- `UpdateInterval`: How often should the shop data be updated? (in ticks: 20T = 1 second || Default: 6000 = 5 minutes). VillagerMarket updates data every 10 minutes by default

- `currencySymbol`: Currency symbol displayed on the website.

- `currencySymbolPosition`: Position of the currency symbol (before/after).

- `defaultLanguage`: Default language of the website. (cn, de, en, es, fr, it, pl, pt, ua)

- `internal-language`: Language of the plugins internal messages (cn, de, en, es, fr, it, pl, pt, ua)

### Commands:

- `/mcdealer`: Allows general access (Permission:  `mcdealer.use`)
- `/mcdealer hideshop`: Allows hiding your shops (Permission:  `mcdealer.hideshop`)
- `/mcdealer showshop`: Show your shops again (Permission:  `mcdealer.showshop`)
- `/mcdealer listhidden`: Show list of all hidden Shops (Permission:  `mcdealer.list`)
- `/mcdealer restart`: Allows restarting the web server (Permission:  `mcdealer.restart`)
- `/mcdealer refresh`Allows refreshing the internal web server files if something goes wrong (Permission: `mcdealer.refresh`)
- `/mcdealer uploadall`: Allows to upload webfiles to external host (Permission: `mcdealer.uploadall`)
- The Permission `mcdealer.admin`allows to use every command.'

### Using external Host for Webfiles
Make shure you set up the ExternalHost Settings correctly and run `/mcdealer uploadall` one time (after first install)
## Important Notes:
**Make sure the defined port is open for the server**

## Support and Issues:

For support or reporting issues, please visit the [MCDealer GitHub repository](https://github.com/CptGummiball/MC-Dealer/) and open a new [issue.](https://github.com/CptGummiball/MC-Dealer/issues)

## License:
This plugin is released under the MIT License. See the [LICENSE](LICENSE) file for details.

Enjoy using MCDealer!