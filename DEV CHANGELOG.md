# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).


## [dev1.1.0] - 2023-12-18 - 2023-12-19

### Changed
- cleaned up code
### Added
- Translator (For ingame messages)
- Translation Files
- rebuild ShopHandler
- reordered onEnable methode
- hidden_shops.json will now be generated during hideshop command
- added Commands (hideshop/showshop/restart)
- added new Classes (ShopHandler/WebServerManager)
- added restart script
- extend the delay for starting first script run
### Fixed
- config comments
- commands working now with permissions
- reformatted plugin.ymls
### Removed
- unused Player Event
- unused imports

----------------------------------------------------------------

## [dev0.3.0] - 2023-12-15

### Changed
- replaced java http with jetty
### added
- re added Jython
- re added Python script
    - Converted from Python 3 to Python 2
----------------------------------------------------------------
## [dev0.2.7] - 2023-12-15

### Changed
- code cleaned up and reorganized
### added
- added color formatting
----------------------------------------------------------------
## [dev0.2.6] - 2023-12-14

### added
- added placeholder class for future DataConverter
### Removed
- removed jython and python script
    - jython don't support python3
----------------------------------------------------------------
## [dev0.2.5] - 2023-12-14

### Known Issues
- "Error executing Python script: SyntaxError: no viable alternative at input '\n'"
    - Working on bugfix
### Changed
- configLoader and WebConfigUpdater are now added to the scheduler
    - restart or reload no longer needed for configuration changes take place
### Added
- re added the UpdateInterval option to config.yml
    - you can now set the update interval of the shop data
### Removed
- removed unnecessary resource lists
    - they are no longer needed
- removed 'ResourceUtils'
- removed reload command from plugin.yml
    - removed this command temporarily in the last update. Now its complete removed and no longer needed. Config changes now take place when the scheduler runs.
### Fixed
- redone the webconfig updater
    - no longer reformatting the config.json when sync
-----------------------------------------------------------------
## [dev0.2.4] - 2023-12-14

### Known Issues
- "Error executing Python script: SyntaxError: no viable alternative at input '\n'"
    - Working on bugfix
### Changed
- replaced the resource updating method (WebFileUtils)
    - kept 'ResourceUtils' for later usage
### Added
- added yaml lib
### Removed
- temporary removed the reload command (caused loading issue)
### Fixed
- fixed plugin prefix
- fixed python utf-8

### TODO
- take a look at the webserver
- fix Python
----------------------------------------------------------------
## [dev0.2.1] - 2023-12-13

### Added
- added initScheduler void to main class
    - shortened onEnable void and make sure the scheduler will not start during the rest of loading procedure
- re added the reload command
### Removed

----------------------------------------------------------------
## [dev0.2.0] - 2023-12-13

### Changed
- replaced java utils logger with slf4j logger
- replaced 'author' with 'authors' in plugin.yml
- replaced 'ShopDataConverter' with 'pythonExecutor' in main class
- changed version to 0.2.0
### Added
- added dependencies for slf4j logger
- re-added python script with shorter interpreter
    - python version in config not needed, script is now loaded out of jar
- added softdepend 'VillagerMarket'
    - plugin should not be loaded without Villager Market
- added loadorder
    - Plugin now loads after the world is loaded to avoid the scheduler running during server startup
### Removed
- removed ShopDataConverter class (going back to the python way)
----------------------------------------------------------------
## [dev0.1.6] - 2023-12-12

### Changed

- added ShopDataConverter to replace Python Script
- replaced old scheduler
- removed 'updateinterval' from config (its now 5 minutes and no longer can be changed)
----------------------------------------------------------------
## [dev0.1.5] - 2023-12-12

### Changed

- added ResourceUtils class
- added resource lists for ResourceUtils
- added plugin Enable checker
- added ResourceUtils features to ResourceUpdater

### Removed

- removed unnecessary prefixes
- removed josh-more-foods python script (not needed in plugin version)
----------------------------------------------------------------
## [dev0.1.4] - 2023-12-12

### Changed

- Changed loading order ResourceUpdater
- Rebuild script for resources
- changed the console prefix to [MCDealer]
- deleted reload command (add it later)

### Removed

- deleted reload command (re-add it later)
----------------------------------------------------------------
## [dev0.1.3] - 2023-12-12

### Changed

- Switched to Spigot for better compatibility
----------------------------------------------------------------
## [dev0.1.2] - 2023-12-10

### Changed

- reorganized classes
- reworked everything
----------------------------------------------------------------
## [dev0.1.1] - 2023-12-10

### Changed

- New Script for resource update (Only extract update-relevant files)
- Deleted custom Fonts (Maven can't package)
- Added web files
- Added placeholder for python script
- Everything in one java file for now (separating later)
- Added Config descriptions
- replaced the scheduler with a modern one
- shorten the scheduler

----------------------------------------------------------------
## [dev0.1] - 2023-12-10

### Added

- Added Changelog
- Added resource extractor
- Added config.yml
- Prepared scheduler for py script