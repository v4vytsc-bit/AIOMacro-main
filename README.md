# AIO-Macro 🌾

**An all-in-one SkyBlock farming & utility mod for Minecraft 1.8.9 (Forge) — tuned for Fakepixel.**

Strafe farms, drop farms, water-column farms, single-row farms — pick a macro, type your angle, press **Delete**, and it farms. Run it from your phone through a **Discord bot** (even for a whole group of players), peek at your inventory without stopping, get alerts when something goes wrong, and see who's playing in an activity feed.

It's a modified fork of [JackTYM's AIO-Macro](https://github.com/JackTYM) with precise decimal aiming, human-paced turning, three new farm layouts, Discord control and a much lighter footprint.

> ⚠️ **Heads up:** macros are against the rules on Hypixel, Fakepixel and most SkyBlock servers. Using this can get your account banned. You use it at your own risk.

---

## 📑 Contents

- [What's new in this fork](#-whats-new-in-this-fork)
- [Macros — every one explained](#-macros)
- [Aiming & turning](#-aiming--turning)
- [Discord bot](#-discord-bot) · [Activity updates](#-activity-updates) · [Alert webhook](#-alert-webhook)
- [Inventory preview](#-inventory-preview)
- [Failsafes](#%EF%B8%8F-failsafes)
- [Other features](#-other-features)
- [Settings reference](#%EF%B8%8F-settings-reference) · [All settings](#-all-settings) · [Keybinds](#%EF%B8%8F-keybinds) · [Commands](#-in-game-commands)
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
| 🤖 | **Discord bot** | Start/stop, status, `!time`, change settings with `!config`, walking, jumping and leaving — for many players on one bot. |
| 📨 | **Alerts & activity** | Failsafe alerts reach Discord again, and an activity webhook shows who joined/left and who started/stopped the macro. |
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
- **Stuck twice in a row** (can't move along the row at all): it walks back the way it came until the walkway opens up to the left or right, then steps to the next row like normal (**After Walk-Back Step**, default forward `W`) and strafes (**After Walk-Back Strafe**, default left `A`). If it gets stuck again soon after, it stops and tells you where.
- **Settings:** *First Row* (A or D), *First Layer Shift* (S or W), *Row Shift* (blocks between rows), *After Walk-Back Step* (W / S / no step) and *After Walk-Back Strafe* (A / D / toward the opening).

### 12 · Mushroom Serpentine

```
TOP LAYER     farm every row  ─────────►  drop down
                    ▲                          │
                    │ swim up                  ▼
BOTTOM LAYER  water column  ◄─────────  farm every row
```

Serpentine on two layers that loops forever. Made for mushroom farms with a water column.
- **Start on either layer.** It works out the water column by itself: water you *walk* into at the **bottom** of a column (floor below, water above your head) is the way up; walking into the **top** of a column (water below you), falling or sinking into water is the drop down — so one column can be used both ways.
- At the end of the bottom layer it finds the column, either in line with the row or one block forward/back from where the row ends, and steps in.
- Holds **jump** to swim up, and steps out only where there's open floor beside it at the top layer's height. **Water Column Exit Side** chooses the direction (default: **left**).
- The top layer has regrown by then, so it just keeps going. 🔁 If it can't get out at the top within a few seconds, it stops and says so.
- **Settings:** the Serpentine settings plus *Water Column Exit Side*.

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
| `!status <name>` | Full status. `!status all` = one line per player |
| `!start <name>` | Start the macro selected in *Macro Type* |
| `!stop <name>` | Stop the macro (and cancel any movement) |
| `!config <name>` | Show that player's settings. `!config <name> <setting> <value>` changes one — see [Changing settings from Discord](#changing-settings-from-discord) |
| `!move <name> <w\|a\|s\|d> <blocks>` | Walk, e.g. `!move Steve w 5` — relative to where they're looking; 0.5–64 blocks; macro must be stopped |
| `!jump <name> [times]` | Jump, e.g. `!jump Steve 3` (1–20) |
| `!leave <name>` | Stop the macro and leave the server (like pressing *Disconnect*) |
| `!help` | List the commands — the only command without a name |

- Replies are threaded under the command and show whose game sent them (*AIO-Macro · Steve*).
- `!help` and "you forgot the name" hints are answered **once**, not by every player.
- The player's game chat shows who used a command from Discord.
- Moves/jumps queue up and report back when done (*"🚶 Moved 5 blocks forward (W)"* or *"⚠️ Stopped after 2.3 of 5 blocks…"*).

**Alerts** — with **Send Alerts To Bot Channel** on, failsafe alerts (bedrock, not on island, Jacob's contest, desync, ban wave, auto-disable, auto status) are posted in the channel. **Alert Ping**: No Ping, `@here` or `@everyone`.

### Changing settings from Discord

`!config Steve` shows Steve's settings. `!config Steve <setting> <value>` changes one (saved just like in the menu; Steve's game chat says who changed what). `all` changes it for everyone.

| Setting | Values | Example |
|---|---|---|
| `macro` | number or part of the name (macro must be stopped) | `!config Steve macro mushroom` |
| `pitch` / `yaw` | −90…90 / −180…180, decimals OK (a running macro turns right away) | `!config Steve pitch 2.5` |
| `rowshift` | 1–10 blocks | `!config Steve rowshift 6` |
| `firstrow` | `left` / `right` | `!config Steve firstrow left` |
| `firstshift` | `back` / `forward` | `!config Steve firstshift back` |
| `waterexit` | `left` / `right` / `opposite` / `same` | `!config Steve waterexit left` |
| `walkbackstep` | `forward` / `back` / `none` | `!config Steve walkbackstep forward` |
| `walkbackstrafe` | `left` / `right` / `opening` | `!config Steve walkbackstrafe left` |
| `pumpkindir` | `left` / `right` | `!config Steve pumpkindir right` |
| `turnmin` / `turnmax` | 0–5000 ms | `!config Steve turnmax 1500` |
| `turnorder` | `together` / `pitchfirst` / `yawfirst` / `random` | `!config Steve turnorder random` |
| `turnspeed` | 1–50 | `!config Steve turnspeed 10` |
| `antistuck` / `setspawn` / `alerts` | `on` / `off` | `!config all setspawn on` |
| `ping` | `none` / `here` / `everyone` | `!config Steve ping here` |
| `devmode` | `on` / `off` — step-by-step macro notes in chat | `!config Steve devmode off` |

### Setup — once, by whoever builds the jar

Players don't set anything up: the bot is **built into the jar**.

1. [Discord Developer Portal](https://discord.com/developers/applications) → **New Application** → **Bot** → **Reset Token** (copy it) → turn on **Message Content Intent** → *Save*.
2. **OAuth2 → URL Generator** → scope **bot** → permissions **View Channels, Send Messages, Read Message History, Embed Links, Attach Files** → open the link and add the bot to your server.
3. Make a channel (e.g. `#aiom-control`), enable *Developer Mode* in Discord (*User Settings → Advanced*), right-click the channel → **Copy Channel ID**.
4. In the project folder, copy **`discord-bot.example.properties`** to **`discord-bot.properties`** and fill in `token=` and `channel_id=` (and `activity_webhook=` for [activity updates](#-activity-updates)).
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

## 📣 Activity updates

A webhook that posts what everyone's doing:

> 🟢 **Steve** joined `mc.fakepixel.me` · ▶️ **Steve** started **Mushroom Serpentine** · ⏹️ **Steve** stopped **Mushroom Serpentine** after 2h 5m · 🔴 **Steve** left `mc.fakepixel.me`

- Make a webhook in Discord (*channel settings → Integrations → Webhooks → New Webhook → Copy Webhook URL*) and put it in `discord-bot.properties` as `activity_webhook=` before building — every jar then posts updates with no setup.
- Players can switch it off (**Discord Bot → Activity Updates**) or send updates elsewhere (**Activity Webhook Link**). **Test Activity Webhook** checks it.
- Moving between islands/lobbies on the same server doesn't count as leaving; closing the game while on a server does.

## 📨 Alert webhook

Prefer a plain webhook for failsafe alerts? **Failsafes → Webhooks**: turn on **Webhook Alerts**, paste a Discord **Webhook Link**, press **Test Webhook** (it tells you in chat whether it worked). It gets the same alerts as the bot channel. **Auto Webhook** (minutes) sends a "still running" status once per interval. Sending happens in the background, so the game doesn't freeze.

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
| **Alerts** | Bot channel and/or alert webhook |

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
| Serpentine (Layered) | **Water Column Exit Side** | Mushroom Serpentine: which way to step out at the top: **Left (A)** (default), Right (D), or Opposite / Same as the last bottom row |
| Serpentine (Layered) | **After Walk-Back Step / Strafe** | What to do after walking back when stuck (default: forward W, then left A) |
| Pumpkin/Melon | **First Direction** | Left (A) or Right (D) |
| Look Turning | **Turn Time Min / Max**, **Turn Order** | See [Aiming & turning](#-aiming--turning) |
| Discord Bot | **Discord Bot Enabled** | On by default — uses the bot built into the jar |
| Discord Bot | **Bot Token**, **Channel ID** | Leave empty for the built-in bot; fill in to use a different one (token shown as dots) |
| Discord Bot | **Send Alerts To Bot Channel**, **Alert Ping**, **Test Discord Bot** | |
| Failsafes → Webhooks | **Webhook Alerts**, **Webhook Link**, **Test Webhook** | |
| Failsafes | **Auto Webhook** | Status every X minutes (0 = off) |
| Discord Bot → Activity Updates | **Activity Updates**, **Activity Webhook Link**, **Test Activity Webhook** | Uses the built-in activity webhook when the link is empty |
| Inventory Preview | **Inventory Preview**, **Opacity** | |
| Account Linking | **Remote Controlling** | The original author's remote control — **off by default**; use the Discord bot instead |
| Debugging | **Turn Speed** | Fastest allowed turn |
| Debugging | **Developer Mode** | Step-by-step macro notes in chat — turn off for a clean chat (also `!config <name> devmode off`) |

<!-- settings:start (generated from AIOMVigilanceConfig.java) -->
## 📋 All settings

Every setting in the menu (`\` or `/aiom`), grouped like the menu. Settings marked 💬 can also be changed from Discord with [`!config`](#changing-settings-from-discord).

### Macro Settings

| Setting | Values | Default | What it does |
|---|---|---|---|
| **Macro Type** 💬 | Netherwart/S-Shaped · Sugarcane · Nuker · Fairy Soul Aura · Shiny Pig ESP · Minion Aura · Scatha Macro · Carpentry Macro · Auction Flipper · Cocoa Bean · One Row · Serpentine (Layered) · Mushroom Serpentine · Pumpkin/Melon | Netherwart/S-Shaped | Choose the macro you would like to use. |
| **Pitch Angle** 💬 | text | 0 | Sets the value you are looking up and down (Check f3). -90 to 90, decimals allowed (e.g. 2.5). |
| **Yaw Angle** 💬 | text | 0 | Sets the value you are looking left to right (Check f3). -180 to 180, decimals allowed (e.g. -90.35). |
| **Auto God Potion** | on / off | off | Automatically buys and consumes a God Potion. (Requires gold in purse). |
| **Auto Booster Cookie** | on / off | off | Automatically buys and consumes a Booster Cookie. (Requires gold in purse). |
| **Fast Break** | on / off | off | Breaks crops at 40bps to allow for faster rancher boot speeds |
| **Fast Break BPS** | 20bps · 40bps · 60bps · 80bps | 20bps | How many blocks per second (20 Ticks) Fast Break breaks. |
| **Auto Sell Minimum** | 0 – 10 | 0 | The minimum amount of stacks to automatically sell (Cookie Only, 0 To Disable) |
| **Drop 180** | on / off | off | Does a 180 turn after dropping during a farming macro |
| **Ungrab** | on / off | off | Ungrabs your mouse when using a farming macro |
| **Macro On** | button |  | Toggles the macro on and off |
| **Auto Combine Same Books** | on / off | off | Combines same books when in an anvil. |
| **Auto Combine Rune** | on / off | off | Combines same runes when in a rune pedestal. |
| **Auto Jerry Box** | on / off | off | Automatically uses jerry boxes in your hot bar. |
| **Serpentine First Row**<br><sub>Serpentine (Layered)</sub> 💬 | Left (A) · Right (D) | Left (A) | Which way to strafe along the first row (the row you start the macro on). |
| **Serpentine First Layer Shift**<br><sub>Serpentine (Layered)</sub> 💬 | Backward (S) · Forward (W) | Backward (S) | How to step onto the next row on the first layer. Flips automatically every time you drop to a new layer. |
| **Serpentine Row Shift**<br><sub>Serpentine (Layered)</sub> 💬 | 1 – 10 | 3 | How many blocks to walk forward/back to get from one row onto the next. |
| **Water Column Exit Side**<br><sub>Serpentine (Layered)</sub> 💬 | Opposite Way · Same Way · Left (A) · Right (D) | Left (A) | Mushroom Serpentine only: which way to step out at the top of the water column. Left/Right always go that way; Opposite/Same are relative to the last bottom-layer row. |
| **After Walk-Back Step**<br><sub>Serpentine (Layered)</sub> 💬 | Forward (W) · Back (S) · No Step | Forward (W) | When stuck, the macro walks back once until the walkway opens up. Then it steps to the next row this way (like a normal row change). |
| **After Walk-Back Strafe**<br><sub>Serpentine (Layered)</sub> 💬 | Left (A) · Right (D) · Toward The Opening | Left (A) | Which way to strafe along the row after walking back (and stepping). |
| **Pumpkin/Melon First Direction**<br><sub>Pumpkin/Melon</sub> 💬 | Left (A) · Right (D) | Right (D) | Which way to strafe along the row you start on. Turns around automatically after every drop. |
| **Look Turn Time Min**<br><sub>Look Turning</sub> 💬 | 0 – 5000 | 0 | Shortest time (ms) a turn to the set pitch/yaw takes. 0 = normal speed. Turning never goes faster than Turn Speed. |
| **Look Turn Time Max**<br><sub>Look Turning</sub> 💬 | 0 – 5000 | 0 | Longest time (ms) a turn to the set pitch/yaw takes. Each turn picks a random time between Min and Max. |
| **Look Turn Order**<br><sub>Look Turning</sub> 💬 | Both Together · Pitch Then Yaw · Yaw Then Pitch · Random Order | Both Together | Turn pitch and yaw at the same time, one after the other, or pick an order at random for every turn. |

### Failsafes

| Setting | Values | Default | What it does |
|---|---|---|---|
| **Webhook Alerts**<br><sub>Webhooks</sub> | on / off | off | Sends webhook on failsafe, message, party invite, island visit, etc. |
| **Webhook Link**<br><sub>Webhooks</sub> | text | empty | Link to send webhook alerts to. |
| **Test Webhook**<br><sub>Webhooks</sub> | button |  | Sends a test message to the webhook provided. |
| **Auto Webhook** | 0 – 360 | 0 | How often to automatically send a webhook (0 to disable) |
| **Random Delay Minimum** | 100 – 5000 | 1000 | The Minimum Random Delay (Milliseconds). |
| **Random Delay Maximum** | 100 – 5000 | 2000 | The Maximum Random Delay (Milliseconds). |
| **Auto Disable** | 0 – 360 | 0 | How long to farm until disabling (0 to disable) |
| **Island Failsafe** | Stop Farming · Teleport To Island · Disable | Stop Farming | Triggers when not on island. |
| **Jacob Failsafe** | on / off | off | Stops farming during Jacob's contests. |
| **Bedrock Failsafe** | on / off | off | Stops farming when looking at bedrock. |
| **Set Spawn** 💬 | on / off | off | Sets your island spawn after each row (Used to infinite farm when using teleport to island failsafe). |
| **Play Sound** | on / off | off | Plays a sound to alert you when a failsafe is triggered. |
| **AntiStuck** 💬 | on / off | off | Detects when a player is stuck and attempts to unstick them. |
| **AntiStuck Jump** | on / off | off | Includes jumping in AntiStuck motion (helpful in some farms, unhelpful in others). |
| **Desync Failsafe** | on / off | off | Detects when a player desyncs from farming and resyncs. |
| **Banwave Failsafe** | on / off | off | Detects when a banwave is in place, stops macroing, and leaves SkyBlock. |
| **Minimum Bans** | 1 – 5 | 3 | The minimum bans to trigger the banwave failsafe. |

### Discord Bot

| Setting | Values | Default | What it does |
|---|---|---|---|
| **Discord Bot Enabled** | on / off | on | Lets your group control your macro from Discord (!help for commands) and posts alerts there. Uses the bot built into this jar unless you fill in your own below. |
| **Bot Token** | text (hidden) | empty | Leave empty to use the bot built into this jar. Only fill in to use a different bot (Developer Portal > Bot > Reset Token). Keep it secret. |
| **Channel ID** | text | empty | Leave empty to use the built-in channel. Only fill in to use a different channel (right-click it > Copy Channel ID). |
| **Send Alerts To Bot Channel** 💬 | on / off | on | Post failsafe alerts (with a game screenshot) in the bot's channel. |
| **Alert Ping** 💬 | No Ping · @here · @everyone | No Ping | Who to ping when an alert is posted in the bot's channel. |
| **Test Discord Bot** | button |  | Sends a test message to the channel and tells you in chat whether it worked. |
| **Activity Updates**<br><sub>Activity Updates</sub> | on / off | on | Posts to the activity webhook when you join or leave a server and when you start or stop the macro. |
| **Activity Webhook Link**<br><sub>Activity Updates</sub> | text (hidden) | empty | Leave empty to use the activity webhook built into this jar. Fill in to send updates somewhere else. |
| **Test Activity Webhook**<br><sub>Activity Updates</sub> | button |  | Sends a test update and tells you in chat whether it worked. |

### Inventory Preview

| Setting | Values | Default | What it does |
|---|---|---|---|
| **Inventory Preview** | on / off | on | Hold the Preview Inventory key (Left Alt by default, change it in Controls) to see your inventory without opening it. Macros keep running. |
| **Inventory Preview Opacity** | 10 – 100 | 55 | How solid the preview's background is (percent). Lower = more see-through. |

### Farming HUD

| Setting | Values | Default | What it does |
|---|---|---|---|
| **Toggle Farming HUD** | on / off | off | Toggles showing of the Farming HUD. |
| **Farming HUD X** | 0 – ? | 0 | Sets the X value of farming hud. |
| **Farming HUD Y** | 0 – ? | 0 | Sets the Y value of farming hud. |
| **Change Farming HUD Position** | button |  | Changes the position of the Farming HUD. |
| **Reset Farming HUD Position** | button |  | Resets the position of the Farming HUD. |
| **Total Profit**<br><sub>Profit</sub> | on / off | off | Displays how much profit you have made farming so far. |
| **Profit Per Hour**<br><sub>Profit</sub> | on / off | off | Displays how much profit you make every hour. |
| **Profit Per 12 Hours**<br><sub>Profit</sub> | on / off | off | Displays how much profit you make every 12 hours. |
| **Profit Per 24 Hours**<br><sub>Profit</sub> | on / off | off | Displays how much profit you make every 24 hours. |
| **Crops Per Hour**<br><sub>Profit</sub> | on / off | off | Displays how many crops you yield in an hour. |
| **Total Final Crops Yielded**<br><sub>Statistics</sub> | on / off | off | Displays how many final crop upgrades you yielded. |
| **Mathematical Hoe Counter**<br><sub>Statistics</sub> | on / off | off | Displays the counter on your mathematical hoe. |
| **God Potion Time**<br><sub>Failsafes</sub> | on / off | off | Displays time remaining on God Potion. |
| **Booster Cookie Time**<br><sub>Failsafes</sub> | on / off | off | Displays time remaining on Booster Cookie. |
| **Jacobs Farming Event Timers**<br><sub>Failsafes</sub> | on / off | off | Displays time until Jacob's Farming Event Starts/Ends. |
| **Total Farming Time**<br><sub>Failsafes</sub> | on / off | off | Displays total time spent macroing. |
| **HUD Text Color**<br><sub>Color</sub> | colour |  | Sets the color for the HUD Text. |

### Nuker

| Setting | Values | Default | What it does |
|---|---|---|---|
| **Nuker Block** | Mycelium · Red Sand · Wood · Any Crop But Cane / Cactus · Cane/Cactus · Hoe Plow · Custom Nuker | Mycelium | Chooses the block to be nuked. |
| **Custom Nuker Block** | text | empty | A custom block name to nuke. |
| **Stay on Y Level** | on / off | off | Does not mine any blocks below your y level |
| **Nuker BPS** | 20bps · 40bps · 60bps · 80bps | 20bps | How many blocks per second (20 Ticks) Nuker breaks. |
| **Next Block Color** | colour |  | Sets the color displayed on the next nuked block. |

### Debugging

| Setting | Values | Default | What it does |
|---|---|---|---|
| **Developer Mode** 💬 | on / off | off | Used to print to the logs to debug. |
| **Turn Speed** 💬 | 1 – 50 | 10 | How fast you turn |
| **Test Look Omg!?!?** | button |  | Epic Gamering. |
| **Show Block Pos Omg!?!?** | button |  | Epic Gamering. |
| **Auto Wheat Phase**<br><sub>Auto Wheat</sub> | 0 · 1 · 2 · 3 · 4 · 5 · 6 · 7 · 8 · 9 · 10 · 11 · 12 · 13 · 14 · 15 · 16 · 17 · 18 · 19 · 20 · 21 · 22 · 23 · 24 · 25 · 26 · 27 · 28 · 29 · 30 | 0 | Sets what phase to start the auto wheat on |
| **Auto Wheat Private Lobby Mode**<br><sub>Auto Wheat</sub> | on / off | off | Instead of using private lobbies, switches between private ones (requires rank) [WIP!!!] |
| **Auto Wheat Omg!?!?**<br><sub>Auto Wheat</sub> | button |  | Epic Gamering. |

### Cane Builder

| Setting | Values | Default | What it does |
|---|---|---|---|
| **Cane Builder Layers** | 1 – 9 | 1 | How many layers to build |
| **Crop Aura BPS** | 20bps · 40bps · 60bps · 80bps | 20bps | How many blocks per second (20 Ticks) Crop Aura places. |
| **Toggle Cane Builder** | button |  | Omg Free Money |
| **Crop Aura Toggle** | button |  | Epic Gamering. |
| **Cane Builder Phase**<br><sub>Debug</sub> | 0 · 1 · 2 · 3 · 4 · 5 · 6 · 7 · 8 · 9 · 10 · 11 · 12 · 13 · 14 · 15 · 16 · 17 · 18 · 19 · 20 · 21 · 22 · 23 · 24 · 25 · 26 · 27 · 28 · 29 · 30 | 0 | Sets what phase to start the cane builder on |

### Account Linking

| Setting | Values | Default | What it does |
|---|---|---|---|
| **AIOM Linking Code** | text | empty | Get in the AIOM Discord by running /link |
| **Remote Controlling** | on / off | off | Allows your account to be controlled remotely via the discord bot (Only through a linked account) |
| **Link Account** | button |  | Links your Discord account to your Minecraft using the link code |
| **Unlink Account** | button |  | Unlinks your Discord account from your Minecraft |

### Rendering

| Setting | Values | Default | What it does |
|---|---|---|---|
| **Enable Rendering** | on / off | on | Disables all rendering, can be used to fix rendering bugs. |
| **Path Recording Points Per Block**<br><sub>Path Recording</sub> | 1 – 10 | 1 | How many points per block the path recorder records. More points = more data = smoother |
| **Clear Recorded Paths**<br><sub>Path Recording</sub> | button |  | Deletes current path recording |

### Quality Of Life

| Setting | Values | Default | What it does |
|---|---|---|---|
| **Auto Reopen AH**<br><sub>Auctions</sub> | on / off | off | Automatically reopens the Auction House when buying an item. (Useful for spam buying items to flip) |
| **Auction House Search Extension**<br><sub>Auctions</sub> | on / off | off | Enables the AH Search Extension to find specific items easier. |
| **Auction House Re-skin**<br><sub>Auctions</sub> | on / off | off | Re-skins the Auction House. |
| **Saved HotBar Data**<br><sub>Automatic HotBar</sub> | text | empty | WARNING: Do Not Edit! Editing Can Cause Unwanted Effects! |
| **HotBar Profile One**<br><sub>Automatic HotBar</sub> | text | empty | The EXACT name of the hotbar profile (saved with /aiom hotbar save {name} and find through /aiom hotbar list). |
| **HotBar Profile Two**<br><sub>Automatic HotBar</sub> | text | empty | The EXACT name of the hotbar profile (saved with /aiom hotbar save {name} and find through /aiom hotbar list). |
| **HotBar Profile Three**<br><sub>Automatic HotBar</sub> | text | empty | The EXACT name of the hotbar profile (saved with /aiom hotbar save {name} and find through /aiom hotbar list). |
| **Automatic Floor 7 Callout | Crystal Phase / P1**<br><sub>Auto F7 Callouts</sub> | text | empty | The text to automatically callout when entering Phase 1 of the Floor 7 Boss Fight |
| **Automatic Floor 7 Callout | Crusher Phase / P2**<br><sub>Auto F7 Callouts</sub> | text | empty | The text to automatically callout when entering Phase 2 of the Floor 7 Boss Fight |
| **Automatic Floor 7 Callout | Terminal Phase / P3**<br><sub>Auto F7 Callouts</sub> | text | empty | The text to automatically callout when entering Phase 3 of the Floor 7 Boss Fight |
| **Automatic Floor 7 Callout | Final Phase / P4**<br><sub>Auto F7 Callouts</sub> | text | empty | The text to automatically callout when entering Phase 4 of the Floor 7 Boss Fight |
| **Skyblock AntiKB** | on / off | off | Toggles AntiKB Inside of SkyBlock (Disabled when holding jerry-chine [vertical only], bonzo staff, or grappling hook |
| **Dungeon Door Aura** | on / off | off | Automatically clicks on wither/blood doors in range of the player |
| **Hide Summons** | on / off | off | Stops summoned mobs from rendering 10+ blocks away. |
| **Auto Close Chest** | on / off | off | Automatically closes dungeon chests |
| **Auto Salvage** | on / off | off | Automatically salvages bad dungeon items |
| **KeyBind VerticalClip Amount**<br><sub>VerticalClip</sub> | text | empty | Changes the clip amount of VClip on the KeyBind |
| **KeyBind HorizontalClip Amount**<br><sub>HorizontalClip</sub> | text | empty | Changes the clip amount of HClip on the KeyBind |
| **Crypt ESP**<br><sub>ESP</sub> | on / off | off | Highlights crypts in dungeons |
| **ESP Color**<br><sub>ESP</sub> | colour |  | The color of the ESP outline |
| **Water Puzzle Aura**<br><sub>Dungeon Solvers</sub> | on / off | off | Auto solves water, provided you follow the instructions |
| **Water Duration**<br><sub>Dungeon Solvers</sub> | 2 – 5 | 2 | The amount of water that is allowed to flow- adjust based on ping etc. (NOT IN SECONDS.) \n DO NOT CHANGE THIS MID SOLVE, IT WILL BREAK. CHANGE THIS BEFORE A RUN OR DURING AN UNSOLVED GATE. |
| **Username Hider**<br><sub>Username Hider</sub> | on / off | off | Hides your actual username anywhere it is displayed. |
| **Username Replacement**<br><sub>Username Hider</sub> | text | empty | What your username is replaced with when username hider is enabled |

### TPS Viewer

| Setting | Values | Default | What it does |
|---|---|---|---|
| **TPS Viewer** | on / off | off | Shows the current server TPS |
| **Change Location** | button |  | Sets the Server TPS HUD Location |
| **TPS Viewer Color** | colour |  | Sets the Server TPS Text Color |
| **TPS Viewer X** | 0 – ? | 0 | Sets the X value of tps viewer. |
| **TPS Viewer Y** | 0 – ? | 0 | Sets the Y value of tps viewer. |

### ESP

| Setting | Values | Default | What it does |
|---|---|---|---|
| **Dungeon Mob ESP** | on / off | off | Shows starred mobs, bats, fels, mini-bosses, through walls. |
| **Slayer Boss ESP** | on / off | off | Shows slayer minibosses and bosses through walls. |
| **Show Hidden Mobs** | on / off | off | Shows invisible mobs like fels, shadow assassins, blood mobs, ghosts. |
| **Crystal Hollows ESP** | on / off | off | Shows Corleone, Team Treasurite, Automaton, Goblins, Sludges, Yog, Bal, and Butterflies through walls. |
| **ESP Color** | colour |  | Sets the ESP Outline Color |
| **ESP Color** | 1 – 20 | 10 | Sets the ESP Outline Color |

### Diana

| Setting | Values | Default | What it does |
|---|---|---|---|
| **Diana Waypoints On** | on / off | off | Enables Diana Waypoints |
| **Diana Show Guess** | on / off | off | Enables Diana Waypoint Guesses |
| **Enable Burrow Aura** | on / off | off | Enables Burrow Aura |
| **Use Echo After Burrow** | on / off | off | Uses the Echo ability after uncovering a burrow |

### Carpentry

| Setting | Values | Default | What it does |
|---|---|---|---|
| **Carpentry Backpack Slot** | 1 – 16 | 1 | Selects the backpack slot that the carpentry macro outputs diamond blocks to. |
| **Carpentry Tick Rate** | 10 – 200 | 100 | Changes the carpentry macro speed. |

### Kuudra

| Setting | Values | Default | What it does |
|---|---|---|---|
| **Auto Ready**<br><sub>Settings</sub> | on / off | off | DEV: auto ready for kuudra testing |
| **Auto Aim**<br><sub>Settings</sub> | on / off | off | Auto Aims at the Kuudra withers (I think broken fixed soon tm?) |

### Packets

- **Toggle Cancelling** (on / off, default off) — Toggles the cancelling of packets.

<details><summary><b>Outgoing</b> — 23 “Cancel …” switches (default off)</summary>

`C0APacketAnimation`, `C0BPacketEntityAction`, `C0CPacketInput`, `C0DPacketCloseWindow`, `C0EPacketClickWindow`, `C0FPacketConfirmTransaction`, `C00PacketKeepAlive`, `C01PacketChatMessage`, `C02PacketUseEntity`, `C03PacketPlayer`, `C07PacketPlayerDigging`, `C08PacketPlayerBlockPlacement`, `C09PacketHeldItemChange`, `C10PacketCreativeInventoryAction`, `C11PacketEnchantItem`, `C12PacketUpdateSign`, `C13PacketPlayerAbilities`, `C14PacketTabComplete`, `C15PacketClientSettings`, `C16PacketClientStatus`, `C17PacketCustomPayload`, `C18PacketSpectate`, `C19PacketResourcePackStatus`

</details>

<details><summary><b>Incoming</b> — 71 “Cancel …” switches (default off)</summary>

`S0APacketUseBed`, `S0BPacketAnimation`, `S0CPacketSpawnPlayer`, `S0DPacketCollectItem`, `S0EPacketSpawnObject`, `S0FPacketSpawnMob`, `S00PacketKeepAlive`, `S1BPacketEntityAttach`, `S1CPacketEntityMetadata`, `S1DPacketEntityEffect`, `S1EPacketRemoveEntityEffect`, `S1FPacketSetExperience`, `S01PacketJoinGame`, `S2APacketParticles`, `S2BPacketChangeGameState`, `S2CPacketSpawnGlobalEntity`, `S2DPacketOpenWindow`, `S2EPacketCloseWindow`, `S2FPacketSetSlot`, `S02PacketChat`, `S3APacketTabComplete`, `S3BPacketScoreboardObjective`, `S3CPacketUpdateScore`, `S3DPacketDisplayScoreboard`, `S3EPacketTeams`, `S3FPacketCustomPayload`, `S03PacketTimeUpdate`, `S04PacketEntityEquipment`, `S05PacketSpawnPosition`, `S06PacketUpdateHealth`, `S07PacketRespawn`, `S08PacketPlayerPosLook`, `S09PacketHeldItemChange`, `S10PacketSpawnPainting`, `S11PacketSpawnExperienceOrb`, `S12PacketEntityVelocity`, `S13PacketDestroyEntities`, `S14PacketEntity`, `S18PacketEntityTeleport`, `S19PacketEntityHeadLook`, `S19PacketEntityStatus`, `S20PacketEntityProperties`, `S21PacketChunkData`, `S22PacketMultiBlockChange`, `S23PacketBlockChange`, `S24PacketBlockAction`, `S25PacketBlockBreakAnim`, `S26PacketMapChunkBulk`, `S27PacketExplosion`, `S28PacketEffect`, `S29PacketSoundEffect`, `S30PacketWindowItems`, `S31PacketWindowProperty`, `S32PacketConfirmTransaction`, `S33PacketUpdateSign`, `S34PacketMaps`, `S35PacketUpdateTileEntity`, `S36PacketSignEditorOpen`, `S37PacketStatistics`, `S38PacketPlayerListItem`, `S39PacketPlayerAbilities`, `S40PacketDisconnect`, `S41PacketServerDifficulty`, `S42PacketCombatEvent`, `S43PacketCamera`, `S44PacketWorldBorder`, `S45PacketTitle`, `S46PacketSetCompressionLevel`, `S47PacketPlayerListHeaderFooter`, `S48PacketResourcePackSend`, `S49PacketUpdateEntityNBT`

</details>

### Auction Flipper

| Setting | Values | Default | What it does |
|---|---|---|---|
| **Auction Flipper Server Link** | text | localhost:4321 | The link to the server running AIOM Auction Flipper (Check #self-hosting-auction-flip-guide in the discord) |
| **Maximum Price** | text | 10000000 | The maximum price to purchase an item |
| **Minimum Profit** | text | 1 | The minimum profit margins to purchase an item |
| **Claim and Relist** | on / off | off | Allows the flipper to automatically relist the item at it's Lowest BIN Price |
| **Claim Relist Tick Speed** | 0 – 100 | 10 | Changes how fast the macro claims and relists (DOES NOT AFFECT BUYING SPEED!) |
| **Buy Pet Skins** | on / off | off | Allows the flipper to buy pet skins. |
| **Buy Potential Manipulations** | on / off | off | Allows the flipper to buy items marked as potential manipulations. |
| **Send Status Messages** | on / off | on | Sends the current status in chat whenever a new thing is happening (waiting, buying, listing etc) |

<!-- settings:end -->

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
├── features/RemoteConfig.java       ← !config
├── features/ActivityTracker.java     ← activity updates (joined / left / started / stopped)
└── util/Discord.java                 ← sending to Discord (bot + webhooks), built-in bot settings
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
| **crelease6** | ⚙️ `!config` to change settings from Discord · 📣 activity webhook (joined / left / started / stopped) · 🗑️ screenshots removed everywhere · 🔍 Serpentine Developer Mode notes · 🔁 when stuck it walks back once to find the row, and only stops if it's stuck again |
| **crelease5** | 🤖 Discord bot for many players (`!list` `!time` `!status` `!start` `!stop` `!move` `!jump` `!leave` `!help`) · 📨 alerts & webhook fixed (no freezes, Auto Webhook once per interval) · 🎒 inventory preview · 🍄 Mushroom Serpentine climbs from either layer (Start Layer setting removed) · 🐌 fixed AntiStuck slowing rows down · 🪶 bazaar downloads and Bazaar Flipper removed · 🔒 old Remote Controlling off by default |
| **crelease4** | 🎃 Pumpkin/Melon macro |
| **crelease3** | 🍄 Mushroom Serpentine · 🧭 Look Turning (turn time & order) · ⏳ macros wait for the turn |
| **crelease2** | 🐍 Serpentine (Layered) · 🎯 decimal Pitch/Yaw text boxes · build fixed for JDK 17 & dead repos |

## 🙏 Credits

- Original mod: **JackTYM** — AIO-Macro.
- Built with [Essential](https://essential.gg/) / Vigilance and Minecraft Forge.
