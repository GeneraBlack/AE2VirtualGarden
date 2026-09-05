# 🌱 AE2 Virtual Garden

**Bring virtual botany, automated forestry, and crop harvesting directly into your Applied Energistics 2 ME Network!**

Requires **Applied Energistics 2** and **NeoForge (Minecraft 1.21.1)**.

---

### 🌟 What is AE2 Virtual Garden?

**AE2 Virtual Garden** introduces generative **Virtual Garden Storage Cells** to Applied Energistics 2. Instead of building massive, laggy in-world farm structures with pistons, water streams, and hoppers, you can now virtualize plant and tree growth straight inside your ME Drives or ME Chests!

Simply partition a Garden Cell with any sapling, seed, or crop, insert it into a powered ME Drive, and the cell will passively produce natural crop drops, logs, apples, and seeds on a regular schedule.

---

### ✨ Key Features

* 📦 **5 Cell Tiers (1k to 256k):** Production scales with cell size.
  * **1k Garden Cell:** 1 drop every 3 seconds (0.5 AE/t idle drain)
  * **4k Garden Cell:** 4 drops every 3 seconds (1.0 AE/t idle drain)
  * **16k Garden Cell:** 16 drops every 3 seconds (2.0 AE/t idle drain)
  * **64k Garden Cell:** 64 drops every 3 seconds (4.0 AE/t idle drain)
  * **256k Garden Cell:** 256 drops every 3 seconds (8.0 AE/t idle drain)
* 🛑 **Zero Network Flooding (Smart Auto-Stop):**
  * Generated items are placed **strictly** into the cell itself.
  * Once the cell is full (byte or type capacity reached), the cell **automatically halts production**.
  * Items will **never** overflow into other drives or storage cells in your ME network!
* ⚡ **Zero Energy Waste:** If a cell is full, it consumes **zero AE energy** for drops until items are extracted from the cell.
* 🌲 **Full Vanilla Support (Out of the Box):**
  * **All 12 Trees & Fungi:** Oak (logs, saplings, apples, sticks), Spruce, Birch, Jungle (logs, cocoa beans), Acacia, Dark Oak, Mangrove (roots, propagules), Cherry, Bamboo, Crimson Fungus, Warped Fungus, and Chorus Flowers.
  * **All 17 Crops & Flora:** Wheat, Carrots, Potatoes (rare poison potato), Beetroot, Melons, Pumpkins, Nether Wart, Sugar Cane, Cactus, Kelp, Sweet Berries, Glow Berries, Cocoa Beans, Torchflowers, Pitcher Plants, Red and Brown Mushrooms.
* 🔍 **Automatic Modded Crop & Sapling Detection:**
  * Automatically detects modded crops (`CropBlock`), discovering their mature drops via block loot tables.
  * Automatically detects modded saplings and pairs them with their mod's wood logs, stems, and leaves.
  * Works seamlessly with mods like *Farmer's Delight*, *Mystical Agriculture*, *Biomes O' Plenty*, and more!
* 🛠️ **Two Easy Configuration Methods:**
  * **AE2 Cell Workbench:** Configure the seed/sapling in the workbench filter.
  * **In-Hand Fast Config:** Sneak + Right-Click with a seed/sapling in your off-hand to set it immediately! (Sneak + Right-Click with an empty off-hand resets the cell).
* 📊 **Authentic AE2 Tooltip:**
  * Real-time byte & type usage ("*X of Y Bytes used*") with dynamic color feedback (Green → Orange → Red).
  * Storage cell contents preview showing upgrade cards and item icons with amounts.
* 📋 **Datapack Extensible:** Add custom plants or customize drop tables via standard JSON recipes (`ae2virtualgarden:garden_drop`).

---

### ⚙️ Configuration Options

All settings are easily configurable in `config/ae2virtualgarden-common.toml`:
* **Drop Interval:** Adjust the generation speed (default: 60 ticks / 3.0 seconds).
* **AE Energy Drain:** Adjust or disable the AE power cost per drop (default: 10.0 AE).
* **Drop Rates:** Configure how many items each cell tier yields per cycle.

---

### ❓ Frequently Asked Questions (FAQ)

**Q: Can I use this mod in my modpack?**
> **Yes, absolutely!** You are welcome to include AE2 Virtual Garden in any public or private modpack on CurseForge, Modrinth, or elsewhere.

**Q: Does it work with ME Chests as well as ME Drives?**
> Yes! Virtual Garden Cells work in both standard ME Drives and ME Chests.

**Q: What happens when the cell gets full?**
> It stops producing completely. It will not waste energy, and it will not push excess items into other storage cells on the network. As soon as you withdraw items via an ME Terminal or export bus, production resumes automatically.

**Q: How do I remove the configured plant from a cell?**
> You can clear it either in an AE2 Cell Workbench by removing the filter item, or by holding the cell in your main hand with an empty off-hand and pressing **Sneak + Right-Click**.

---

### 📦 Dependencies

* **Minecraft 1.21.1**
* **NeoForge 21.1.172+**
* **Applied Energistics 2 (AE2) 19.2.10+**
