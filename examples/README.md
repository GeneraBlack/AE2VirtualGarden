# AE2 Virtual Garden - Modpack Creator Guide

This folder contains example configuration and datapack files for modpack creators looking to customize power drain, tick speed, tier yields, and individual plant drop tables.

---

## 1. Global Settings & Power Usage (`config/`)

Copy `examples/config/ae2virtualgarden-common.toml` to:
* **`defaultconfigs/ae2virtualgarden-common.toml`** (Recommended for modpacks – applies automatically to all newly created worlds!)
* Or `config/ae2virtualgarden-common.toml`

### Key Configuration Values:
| Key | Default | Description |
| :--- | :--- | :--- |
| `general.baseTickInterval` | `60` | Generation cycle interval in world ticks (20 ticks = 1 second). |
| `general.requireAeEnergy` | `true` | Whether ME Network power is required for generation. |
| `general.energyPerDrop` | `10.0` | AE power consumed per successfully accepted drop item. |
| `tiers.tier1kDrops` | `1` | Item drops rolled per cycle for 1k Cell. |
| `tiers.tier4kDrops` | `4` | Item drops rolled per cycle for 4k Cell. |
| `tiers.tier16kDrops` | `16` | Item drops rolled per cycle for 16k Cell. |
| `tiers.tier64kDrops` | `64` | Item drops rolled per cycle for 64k Cell. |
| `tiers.tier256kDrops` | `256` | Item drops rolled per cycle for 256k Cell. |

---

## 2. Custom Plant & Crop Drops (`datapacks/`)

You can define custom drop tables for any seed, sapling, flower, or custom mod item via JSON datapacks using recipe type `ae2virtualgarden:garden_drop`.

Place recipe files in:
`openloader/data/<pack_id>/data/<modpack_id>/recipe/<name>.json` or `kubejs/data/<modpack_id>/recipe/<name>.json`

### JSON Structure:
* `seed`: The item used to configure the cell (via Cell Workbench or Offhand Shift+Right-Click).
* `min_tier`: Minimum cell tier required to grow this plant:
  * `1` = 1k Cell
  * `2` = 4k Cell
  * `3` = 16k Cell
  * `4` = 64k Cell
  * `5` = 256k Cell
* `drops`: List of weighted drops:
  * `item`: Output item stack (`id` and optional `count`).
  * `weight`: Relative roll weight (e.g. 70 = 70% chance relative to total weights).
  * `min_count` / `max_count`: Range of items dropped when this entry is rolled.