# AIO-Macro 🌾

**An all-in-one SkyBlock farming & utility mod for Minecraft 1.8.9 (Forge) — tuned for Fakepixel.**

Strafe farms, drop farms, water-column farms, single-row farms — pick a macro, type your angle, press **Delete**, and it farms. Run it from your phone through a **Discord bot** (even for a whole group of players), peek at your inventory without stopping, and get alerts with a screenshot when something goes wrong.

It's a modified fork of [JackTYM's AIO-Macro](https://github.com/JackTYM) with precise decimal aiming, human-paced turning, three new farm layouts, Discord control and a much lighter footprint.

> ⚠️ **Heads up:** macros are against the rules on Hypixel, Fakepixel and most SkyBlock servers. Using this can get your account banned. You use it at your own risk.

---

## 📑 Contents

- [What's new in this fork](#-whats-new-in-this-fork)
- [Macros — every one explained](#-macros)
- [Aiming & turning](#-aiming--turning)
- [Discord bot](#-discord-bot) · [Webhook](#-webhook)
- [Inventory preview](#-inventory-preview)
- [Failsafes](#%EF%B8%8F-failsafes)
- [Other features](#-other-features)
- [Settings reference](#%EF%B8%8F-settings-reference) · [Keybinds](#%EF%B8%8F-keybinds) · [Commands](#-in-game-commands)
- [Install](#-install) · [Build from source](#%EF%B8%8F-build-from-source)
- [Known issues](#-known-issues) · [Changelog](#-changelog)

---

## ✨ What's new in this fork

| | Feature | What it means for you |
|---|---|---|
| 🎯 | **Decimal Pitch / Yaw** | Type exact angles like `-90.35` / `2.5` into text boxes. Turning lands on them exactly. |
| 🐍 | **Serpentine (Layered)** | Rows that zig-zag across a layer *and* drop to more layers below — steps forward or back between rows. |
| 🍄 | **Mushroom Serpentine** | Two-layer loop: farm one layer, drop, farm the other, swim back up the water column, forever. Start on either layer. |
| 🎃 | **Pumpkin/Melon** | One row per layer, any number of layers — strafe, fall down the chute, turn around. |
| 🧭 | **Look Turning** | Slower, randomised turns (min–max time) and pitch/yaw together, one after the other, or random order. |
| ⏳ | **Wait for the turn** | Farming macros stand still until you're looking at your angle, *then* start. |
| 🤖 | **Discord bot** | Start/stop, status, screenshots, `!time`, walking, jumping and leaving — for many players on one bot. |
| 📨 | **Working alerts** | Failsafe alerts reach Discord again (bot channel and/or webhook) with a Minecraft-only screenshot. |
| 🎒 | **Inventory preview** | Hold Left Alt to see your inventory as an overlay — the macro keeps running. |
| 🪶 | **Much lighter** | No more 1 GB/hour bazaar downloads, no game freezes when sending alerts, no AntiStuck slow-downs. |

---

## 🚜 Macros

Choose one in **Macro Settings → Macro Type**, set it up as described below, then press **Delete** (or `!start <name>` from Discord) to start and stop it.

| # | Macro | In one line |
|---|---|---|
| 0 | [Netherwart / S-Shaped](#0--netherwart--s-shaped) | Strafes left/right along rows, pushing forward into the next row |
| 1 | [Sugarcane](#1--sugarcane) | Strafes left, then walks back, alternating |
| 2 | [Nuker](#2--nuker) | Breaks a chosen block type all around you |
| 3 | [Fairy Soul Aura](#3--fairy-soul-aura) | Collects fairy souls you walk near |
| 4 | [Shiny Pig ESP](#4--shiny-pig-esp) | Highlights shiny pigs (yours red, others blue) |
| 5 | [Minion Aura](#5--minion-aura) | Opens nearby minions and collects from them |
| 6 | [Scatha Macro](#6--scatha-macro) | Digs a straight tunnel forward |
| 7 | [Carpentry Macro](#7--carpentry-macro) | Claims enchanted diamonds, crafts blocks, stores them |
| 8 | [Auction Flipper](#8--auction-flipper) | Buys underpriced auctions from a flip server |
| 9 | [Cocoa Bean](#9--cocoa-bean) | Walks forward, then back, alternating |
| 10 | [One Row](#10--one-row) | Walks a row, sidesteps one block, walks back |
| 11 | [Serpentine (Layered)](#11--serpentine-layered) | Zig-zags across rows and drops through layers |
| 12 | [Mushroom Serpentine](#12--mushroom-serpentine) | Two-layer loop with a water column back up |
| 13 | [Pumpkin/Melon](#13--pumpkinmelon) | One row per layer, falls down a chute each time |

**All farming macros** (0, 1, 9–13) hold left click, turn to your **Pitch Angle / Yaw Angle** first and only start once you're looking there, and keep that angle the whole time. Turning speed and order come from [Look Turning](#-aiming--turning).

**Failsafes** (bedrock, island, Jacob's contest, alerts) watch macros **0, 1, 11, 12 and 13**. Cocoa Bean and One Row aren't covered by them.

---

### 0 · Netherwart / S-Shaped
The classic farming macro for S-shaped nether wart, carrot and potato farms.
- **Moves:** row 1 `A` (left), row 2 `D + W` (right while pushing forward), row 3 `A`, and so on. It never presses `S`.
- **Row end:** when you stop moving, it switches direction after a random delay (*Random Delay Min/Max*).
- **Extras:** `/setspawn` at row ends (*Set Spawn*), AntiStuck, and **Drop 180** (turns around after a drop for farms that need it).
- **Set up:** face the crops at your angle, stand at the start of the first row.

### 1 · Sugarcane
For sugarcane farms where you go sideways one way and backwards the other.
- **Moves:** `A` (left) until you stop, then `S` (back) until you stop, alternating.
- **Extras:** Set Spawn, AntiStuck, Drop 180.

### 2 · Nuker
Breaks the chosen block type around you, closest first, while you walk around yourself.
- **Nuker Block:** Mycelium · Red Sand · Wood · Any Crop But Cane/Cactus (grown crops, pumpkins, melons) · Cane/Cactus (only above your feet, leaving the base) · Hoe Plow (right-clicks to till) · Custom Nuker (type a block name in *Custom Nuker Block*).
- **Range:** within about 4 blocks. **Nuker BPS** sets 20–80 blocks per second.
- **Stay on Y Level** never breaks blocks below your feet. **Next Block Color** highlights the next target.

### 3 · Fairy Soul Aura
Walk around an island and it collects every fairy soul within about 4 blocks, at most one click per second. Souls you've already found are remembered and skipped.

### 4 · Shiny Pig ESP
Highlights **shiny pigs**: **your own in red**, everyone else's in **blue**, so you can find them quickly. It only draws; it doesn't move you.

### 5 · Minion Aura
Stand among your minions. Every second it opens the nearest unclaimed minion within 6 blocks and clicks **Collect All**, or sells through the hopper when the minion has one. Then it moves on to the next. Minions are outlined: **green** = in range, **grey** = waiting, **red** = done.

### 6 · Scatha Macro
Digs a straight tunnel for Scatha / worm hunting in the Crystal Hollows.
- Holds `W`, looks slightly down (pitch 15), and breaks the two blocks two steps ahead of you (feet and head height).
- Digs in the direction of your **Yaw Angle**, rounded to the nearest of 0 / 90 / 180 / −90.

### 7 · Carpentry Macro
A money-making loop with **enchanted diamonds**, despite the name:
1. Opens the bazaar (`/bz`) and claims your filled **Enchanted Diamond** orders.
2. Opens the crafting menu through the SkyBlock menu item in **hotbar slot 9** and crafts **Enchanted Diamond Blocks**.
3. Puts them into the backpack in **Carpentry Backpack Slot**, then starts over.
- **Carpentry Tick Rate** sets how fast it clicks. Keep the SkyBlock menu in the last hotbar slot.

### 8 · Auction Flipper
Buys auctions listed below their value, using flips from an **auction flip server** you host yourself (*Auction Flipper Server Link*, default `localhost:4321`). Without that server it does nothing.
- Opens each flip with `/viewauction`, buys the BIN, and with **Claim and Relist** claims and relists what it bought.
- **Maximum Price / Minimum Profit** filter flips. **Buy Pet Skins** and **Buy Potential Manipulations** allow riskier items. **Send Status Messages** reports what it's doing.

### 9 · Cocoa Bean
For cocoa walls: walks `W` (forward) until you stop, then `S` (back) until you stop, alternating. Uses Set Spawn and AntiStuck. *(Not covered by the other failsafes.)*

### 10 · One Row
For a single long row you walk along forwards and backwards.
- Walks `W` to the end, then **sneaks one block left** (`Shift + A`) to the next row, then walks `S` back, and repeats.
- Uses Set Spawn and AntiStuck, and waits about 5 seconds after starting before it reacts to stopping. *(Not covered by the other failsafes.)*

### 11 · Serpentine (Layered)

```
LAYER 1   [Start] ──► ──► ──► ──► ──┐          rows step BACK (S)
                                    │
          ┌── ◄── ◄── ◄── ◄── ◄── ──┘
          │
          └── ──► ──► ──► ──► ──► ──┐
                                    │
          [drop] ◄── ◄── ◄── ◄── ◄──┘
             │
             ▼  fall to the next layer — the row step flips (S ⇄ W)
LAYER 2   [land] ──► ──► ──► ...
```

For farms where you strafe along a row, step **forward or back** to the next row, and drop to more layers below.
- **End of a row** = you've moved at least a block along it and then stopped. It pauses briefly at random, steps forward/back by **Row Shift** blocks, then strafes back the other way.
- **Drop** = landing more than 1.5 blocks lower. The forward/back step flips for the new layer.
- Doesn't need to know how many rows or layers there are. If it can't step to another row (end of the farm), it stops and says so.
- A short hiccup at the start of a row isn't treated as "stuck"; only a full second without getting anywhere hands over to AntiStuck.
- **Settings:** *First Row* (A or D), *First Layer Shift* (S or W), *Row Shift* (blocks between rows).

### 12 · Mushroom Serpentine

```
TOP LAYER     farm every row  ─────────►  drop down
                    ▲                          │
                    │ swim up                  ▼
BOTTOM LAYER  water column  ◄─────────  farm every row
```

Serpentine on two layers that loops forever. Made for mushroom farms with a water column.
- **Start on either layer.** It works out the water column by itself: water you *walk* into that carries on above your head is the way up; water you fall or sink into is a drop.
- At the end of the bottom layer it finds the column, either in line with the row or one block forward/back from where the row ends, and steps in.
- Holds **jump** to swim up, and steps out only where there's open floor beside it at the top layer's height. **Water Column Exit** chooses the direction (default: opposite to the last bottom-layer row).
- The top layer has regrown by then, so it just keeps going. 🔁 If it can't get out at the top within a few seconds, it stops and says so.
- **Settings:** the Serpentine settings plus *Water Column Exit*.

### 13 · Pumpkin/Melon

```
LAYER 1   [Start] ════════════════════════► [Drop Chute R]
                                                  │ fall
LAYER 2   [Drop Chute L] ◄════════════════════ [Landing]
               │ fall
LAYER 3   [Landing] ════════════════════════► ...
```

For stacked pumpkin/melon farms with one row per layer.
- Strafes along the row into the chute at the end, falls, pauses briefly at random, and strafes back the other way. Any number of layers.
- Standing at a row end for a second without falling means the bottom of the farm: it stops and tells you in chat.
- **Setting:** *Pumpkin/Melon First Direction* (A or D) for your starting row.

---

## 🧭 Aiming & turning

- **Pitch Angle / Yaw Angle** are text boxes — decimals allowed (`12.5` or `12,5`). Pitch is kept within −90…90, yaw is wrapped into −180…180, anything that isn't a number counts as 0.
- **Look Turn Time Min / Max** — each turn picks a random time in that range. It's never *faster* than **Turn Speed** (Debugging, default 10 ≈ 2°/tick), so 0/0 means "normal speed".
- **Look Turn Order** — Both Together · Pitch Then Yaw · Yaw Then Pitch · Random Order.
- Tiny corrections (under 1°, e.g. after being pushed) always use normal speed.
- Angles are read when the macro starts — toggle it off and on after changing them.

---

## 🤖 Discord bot

Control the macro from a Discord channel and get alerts there — from your phone, anywhere. **Several players can share one bot and one channel**: every command names the player it's for (their Minecraft name, any capitalization), or `all`.

| Command | Does |
|---|---|
| `!list <name>` | One line: online / macro running (which one, how long) / stopped. `!list all` = every player |
| `!time <name>` | How long the macro has been running, plus total since the game started. `!time all` = every player |
| `!status <name>` | Full status with a screenshot. `!status all` = one line per player |
| `!start <name>` | Start the macro selected in *Macro Type* |
| `!stop <name>` | Stop the macro (and cancel any movement) |
| `!screenshot <name>` | Screenshot of that player's game |
| `!move <name> <w\|a\|s\|d> <blocks>` | Walk, e.g. `!move Steve w 5` — relative to where they're looking; 0.5–64 blocks; macro must be stopped |
| `!jump <name> [times]` | Jump, e.g. `!jump Steve 3` (1–20) |
| `!leave <name>` | Stop the macro and leave the server (like pressing *Disconnect*) |
| `!help` | List the commands — the only command without a name |

- Replies are threaded under the command and show whose game sent them (*AIO-Macro · Steve*).
- `!help` and "you forgot the name" hints are answered **once**, not by every player.
- The player's game chat shows who used a command from Discord.
- Moves/jumps queue up and report back when done (*"🚶 Moved 5 blocks forward (W)"* or *"⚠️ Stopped after 2.3 of 5 blocks…"*).

**Alerts** — with **Send Alerts To Bot Channel** on, failsafe alerts (bedrock, not on island, Jacob's contest, desync, ban wave, auto-disable, auto status) are posted in the channel with a screenshot. **Alert Ping**: No Ping, `@here` or `@everyone`.

### Setup — once, by whoever builds the jar

Players don't set anything up: the bot is **built into the jar**.

1. [Discord Developer Portal](https://discord.com/developers/applications) → **New Application** → **Bot** → **Reset Token** (copy it) → turn on **Message Content Intent** → *Save*.
2. **OAuth2 → URL Generator** → scope **bot** → permissions **View Channels, Send Messages, Read Message History, Embed Links, Attach Files** → open the link and add the bot to your server.
3. Make a channel (e.g. `#aiom-control`), enable *Developer Mode* in Discord (*User Settings → Advanced*), right-click the channel → **Copy Channel ID**.
4. In the project folder, copy **`discord-bot.example.properties`** to **`discord-bot.properties`** and fill in `token=` and `channel_id=`.
5. [Build](#%EF%B8%8F-build-from-source) and hand out the jar. Every player who installs it is connected automatically (they can switch it off with **Discord Bot Enabled**).

`discord-bot.properties` is git-ignored, so the token never ends up in the source code. Without that file, the jar has no built-in bot and players can still type a **Bot Token** and **Channel ID** into the settings themselves — those also override the built-in bot.

> 🔐 **The token can be read out of any jar built with it** (a jar is a zip file). Only give those jars to people you trust and **never post them publicly**. If one leaks: *Reset Token* in the Developer Portal, put the new token in `discord-bot.properties` and rebuild — old jars stop working with the bot.

### Good to know

- **Anyone who can type in that channel can use the commands** — control who can post there with Discord's channel permissions. Messages from bots are ignored.
- **Everyone with the jar (or token) can fully use the bot.** If someone you no longer trust has it, *Reset Token*, update `discord-bot.properties` and hand out a new jar.
- Only messages sent after the game started are read, so an old `!start` never fires later.
- The bot shows as *offline* — that's normal. Each player's mod checks the channel every 3 seconds while their Minecraft is running (no server or hosting needed).
- With lots of players on one bot (more than ~5), Discord slows the checks down, so replies can take a few extra seconds.
- Problems (wrong token, no access to the channel, wrong channel ID) are shown once in game chat.

## 📨 Webhook

Prefer a plain webhook for alerts? **Failsafes → Webhooks**: turn on **Webhook Alerts**, paste a Discord **Webhook Link**, press **Test Webhook** (it tells you in chat whether it worked). It gets the same alerts as the bot channel. **Auto Webhook** (minutes) sends a "still running" status once per interval. Webhooks can only send — use the bot to control the macro.

Screenshots (bot and webhook) are taken of the **Minecraft window only** — never your desktop — and attached straight to the message. Sending happens in the background, so the game doesn't freeze.

---

## 🎒 Inventory preview

Hold **Left Alt** to see your whole inventory — armor, main inventory and hotbar (selected slot outlined, with counts and durability) — as a see-through overlay in the middle of the screen.

- Nothing opens, so it can't be clicked or edited, and **the macro keeps farming/mining while you look**.
- **Inventory Preview → Opacity** sets how see-through the background is (items stay solid so they're easy to read).
- Change the key in *Controls → All-In-One Macro → Preview Inventory (hold)*.

---

## 🛡️ Failsafes

| Failsafe | What it does |
|---|---|
| **Island Failsafe** | Not on your island → stop, or teleport back to the island |
| **Jacob Failsafe** | Pauses during Jacob's contests (see *Known issues*) |
| **Bedrock Failsafe** | Bedrock in front of you → stop |
| **Set Spawn** | `/setspawn` at row ends / landings, so you respawn in the farm |
| **AntiStuck** (+ Jump) | Wiggles W/A/S/D (and jumps) when you're really stuck |
| **Desync Failsafe** | Notices when farming stops counting and resyncs |
| **Banwave Failsafe** | Leaves SkyBlock when a ban wave is detected (*Minimum Bans*) |
| **Auto Disable** | Stops the macro after X minutes |
| **Random Delay Min/Max** | Random pauses between actions |
| **Play Sound** | Plays a sound on failsafes |
| **Alerts** | Bot channel and/or webhook, with a screenshot |

---

## 🧰 Other features

| Area | Features |
|---|---|
| **Farming helpers** | Auto God Potion · Auto Booster Cookie · Fast Break (20–80 bps) · Auto Sell · Drop 180 · Ungrab mouse · Auto combine books/runes · Auto Jerry Box |
| **Farming HUD** | Total profit, profit per hour/12h/24h, crops per hour, final crops, hoe counter, God Potion / Cookie / Jacob timers, total farming time — movable, custom colour |
| **Nuker** | Mycelium, red sand, wood, crops, cane/cactus, hoe plow or a custom block — set BPS, stay on Y level |
| **Cane Builder & Crop Aura** | Builds sugarcane layers; places crops around you |
| **ESP** | Dungeon mobs, slayer bosses, hidden mobs, Crystal Hollows, Crypt ESP |
| **Dungeons** | Water puzzle aura, dungeon door aura, auto F7 callouts, hide summons, auto salvage |
| **Diana** | Waypoints, guess, burrow aura, echo after burrow |
| **Kuudra** | Auto ready, auto aim |
| **Auctions** | Auto reopen AH, AH search extension, AH re-skin |
| **Quality of life** | Hotbar profiles, vertical/horizontal clip keys, SkyBlock anti-knockback, auto close chest, username hider, TPS viewer, path recording |
| **Packets** | Cancel any incoming/outgoing packet type (advanced) |

---

## ⚙️ Settings reference

Open the settings with **`\`** (backslash) or `/aiom`.

| Section | Setting | Notes |
|---|---|---|
| Macro Settings | **Macro Type** | Which macro Delete starts |
| Macro Settings | **Pitch Angle / Yaw Angle** | Decimals allowed |
| Serpentine (Layered) | **First Row** | Strafe direction on your starting row |
| Serpentine (Layered) | **First Layer Shift** | Backward (S) or Forward (W) to reach the next row on the first layer |
| Serpentine (Layered) | **Row Shift** | Blocks between rows (1–10) |
| Serpentine (Layered) | **Water Column Exit** | Mushroom Serpentine: Opposite Way (default) or Same Way |
| Pumpkin/Melon | **First Direction** | Left (A) or Right (D) |
| Look Turning | **Turn Time Min / Max**, **Turn Order** | See [Aiming & turning](#-aiming--turning) |
| Discord Bot | **Discord Bot Enabled** | On by default — uses the bot built into the jar |
| Discord Bot | **Bot Token**, **Channel ID** | Leave empty for the built-in bot; fill in to use a different one (token shown as dots) |
| Discord Bot | **Send Alerts To Bot Channel**, **Alert Ping**, **Test Discord Bot** | |
| Failsafes → Webhooks | **Webhook Alerts**, **Webhook Link**, **Test Webhook** | |
| Failsafes | **Auto Webhook** | Status every X minutes (0 = off) |
| Inventory Preview | **Inventory Preview**, **Opacity** | |
| Account Linking | **Remote Controlling** | The original author's remote control — **off by default**; use the Discord bot instead |
| Debugging | **Turn Speed** | Fastest allowed turn |

## ⌨️ Keybinds

Change them in **Options → Controls → All-In-One Macro**.

| Key | Action |
|---|---|
| `Delete` | Toggle the macro |
| `\` | Open settings |
| `Left Alt` (hold) | Inventory preview |
| `F6` / `F7` / `F8` | Hotbar profiles 1 / 2 / 3 |
| `` ` `` | Vertical clip |
| `+` (numpad) | Horizontal clip |
| `Pause` | Refresh Diana waypoints |
| `End` | Toggle packet cancelling |
| `F8` | Toggle path recording *(shares F8 with hotbar profile 3 — rebind one)* |

## 💬 In-game commands

| Command | Does |
|---|---|
| `/aiom` | Open settings |
| `/aiom hotbar save\|load\|delete <name>` · `/aiom hotbar list` | Hotbar profiles |
| `/aiom vclip <n>` · `/aiom hclip <n>` · `/aiom customclip <x> <y> <z>` | Teleport-clip |
| `/aiom pathfind block\|entity\|clear` | Pathfinding |
| `/aiom drawbeacon` | Draw a beacon beam |
| `/aiom fragbot` (`fb`) | Party the fragbot |
| `/aioc` | AIOM chat / account linking |

---

## 📦 Install

1. Install **Minecraft 1.8.9 + Forge** (the game runs on **Java 8**).
2. Drop the newest `aiom-1.8.0-creleaseN.jar` into `.minecraft/mods` and remove older AIO-Macro jars.
3. Launch, press `\`, choose your **Macro Type** and type your **Pitch/Yaw**.

**Updating from crelease4 or older?** Open the settings once and pick your **Macro Type** again — the list got one entry shorter (Bazaar Flipper was removed), so the saved choice now points one macro further down. Also re-enter Serpentine settings if they look off.

## 🛠️ Build from source

The build tooling (Essential Loom) needs **JDK 17**; the game itself still runs on Java 8.

1. Put a JDK 17 somewhere (this repo expects it in `.jdk/`, which is git-ignored).
2. Point Gradle at it in `gradle.properties`:
   ```properties
   org.gradle.java.home=D:/path/to/AIOMacro-master/.jdk
   ```
3. Build:
   ```bash
   ./gradlew build
   ```
4. Grab `build/libs/aiom-1.8.0-release.jar` (ignore the `-dev` jar) and rename it to the next `creleaseN`.

**Windows tip:** if the build dies with `Unable to establish loopback connection`, your temp folder path is upsetting Java 17. Add this line to `.jdk/conf/net.properties` (any short folder works):

```properties
jdk.net.unixdomain.tmpdir=D:/path/to/AIOMacro-master/.gradle/udtmp
```

The dead `repo.sk1er.club` Maven repository is commented out in `build.gradle` / `settings.gradle`; Essential's repository is used instead.

## 🗂️ Where things live

```
src/main/java/me/jacktym/aiomacro/
├── config/AIOMVigilanceConfig.java   ← every setting in the GUI
├── features/MacroHandler.java        ← starts/stops macros, "wait for the turn", run time
├── features/SetPlayerLook.java       ← aiming: decimal angles, turn time & order
├── features/SerpentineFarm.java      ← Serpentine (Layered) + Mushroom Serpentine
├── features/PumpkinMelon.java        ← Pumpkin/Melon
├── features/NetherWart.java, SugarCane.java, OneRow.java
├── features/Failsafe.java            ← all failsafes
├── features/DiscordBot.java          ← Discord bot commands
├── features/RemoteMovement.java      ← !move / !jump
├── rendering/InventoryPreview.java   ← inventory overlay
└── util/Discord.java                 ← sending to Discord (bot + webhook), screenshots
```

`discord-bot.example.properties` (project root) → copy to `discord-bot.properties` to build the bot into the jar.

---

## 🐛 Known issues

- **Jacob's contest auto-resume never fires.** The macro pauses when a contest starts but stays off afterwards — restart it yourself (or `!start <name>`).
- **F8** is bound to both *HotBar Profile Three* and *Toggle Path Recording* by default.
- `Trades` (a coin feature) was missing from the original source, so it's disabled.
- **Bazaar Flipper has been removed** — Hypixel's bazaar prices don't apply on Fakepixel, and the original downloaded them every 2 seconds (about 1 GB of data per hour).
- Some online features (fragbot list, account linking, the old remote control, banwave check) talk to the original author's services, which may no longer be online.
- The newest additions (Discord bot, movement commands, inventory preview) have had little real-world testing yet — if something misbehaves, note what happened (a short clip helps).

## 📜 Changelog

| Build | What changed |
|---|---|
| **crelease5** | 🤖 Discord bot for many players (`!list` `!time` `!status` `!start` `!stop` `!screenshot` `!move` `!jump` `!leave` `!help`) · 📨 alerts & webhook fixed (Minecraft-only screenshots, no freezes, Auto Webhook once per interval) · 🎒 inventory preview · 🍄 Mushroom Serpentine climbs from either layer (Start Layer setting removed) · 🐌 fixed AntiStuck slowing rows down · 🪶 bazaar downloads and Bazaar Flipper removed · 🔒 old Remote Controlling off by default |
| **crelease4** | 🎃 Pumpkin/Melon macro |
| **crelease3** | 🍄 Mushroom Serpentine · 🧭 Look Turning (turn time & order) · ⏳ macros wait for the turn |
| **crelease2** | 🐍 Serpentine (Layered) · 🎯 decimal Pitch/Yaw text boxes · build fixed for JDK 17 & dead repos |

## 🙏 Credits

- Original mod: **JackTYM** — AIO-Macro.
- Built with [Essential](https://essential.gg/) / Vigilance and Minecraft Forge.
