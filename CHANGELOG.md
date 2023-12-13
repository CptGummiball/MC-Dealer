# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [0.2.0] - 2023-12-12

### Changed
- replaced 'author' with 'authors' in plugin.yml
- replaced 'ShopDataConverter' with 'pythonExecutor' in main class
- changed version to 0.2.0
### Added
- re-added python script with shorter interpreter
  - python version in config not needed, script is now loaded out of jar
- added softdepend 'VillagerMarket'
  - plugin should not be loaded without Villager Market
- added loadorder
  - Plugin now loads after the world is loaded to avoid the scheduler running during server startup
### Removed
- removed ShopDataConverter class (going back to the python way)
----------------------------------------------------------------
## [0.1.6] - 2023-12-12

### Changed

- added ShopDataConverter to replace Python Script
- replaced old scheduler
- removed 'updateinterval' from config (its now 5 minutes and no longer can be changed)
----------------------------------------------------------------
## [0.1.5] - 2023-12-12

### Changed

- added ResourceUtils class
- added resource lists for ResourceUtils
- added plugin Enable checker
- added ResourceUtils features to ResourceUpdater

### Removed

- removed unnecessary prefixes
- removed josh-more-foods python script (not needed in plugin version)
----------------------------------------------------------------
## [0.1.4] - 2023-12-12

### Changed

- Changed loading order ResourceUpdater
- Rebuild script for resources
- changed the console prefix to [MCDealer]
- deleted reload command (add it later)

### Removed

- deleted reload command (re-add it later)
----------------------------------------------------------------
## [0.1.3] - 2023-12-12

### Changed

- Switched to Spigot for better compatibility
----------------------------------------------------------------
## [0.1.2] - 2023-12-10

### Changed

- reorganized classes
- reworked everything
----------------------------------------------------------------
## [0.1.1] - 2023-12-10

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
## [0.1] - 2023-12-10

### Added

- Added Changelog
- Added resource extractor
- Added config.yml
- Prepared scheduler for py script
