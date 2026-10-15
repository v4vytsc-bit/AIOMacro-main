# AIO-Macro 🌾

**An all-in-one Hypixel SkyBlock farming & utility mod for Minecraft 1.8.9 (Forge).**

Strafe farms, drop farms, water-column farms, single-row farms — pick a macro, type your angle, press **Delete**, and it farms. Under the hood it's a modified fork of [JackTYM's AIO-Macro](https://github.com/JackTYM) with precise decimal aiming, human-paced turning and three new farm layouts.

> ⚠️ **Heads up:** macros are against the rules on Hypixel (and most SkyBlock servers). Using this can get your account banned. You use it at your own risk.

---

## ✨ What's new in this fork

| | Feature | What it means for you |
|---|---|---|
| 🎯 | **Decimal Pitch / Yaw** | Type exact angles like `-90.35` / `2.5` into text boxes instead of dragging whole-number sliders. Turning lands on them exactly. |
| 🐍 | **Serpentine (Layered)** | Rows that zig-zag across a layer *and* drop to more layers below — steps forward or back between rows. |
| 🍄 | **Mushroom Serpentine** | Two-layer loop: farm the top, drop down, farm the bottom, swim back up the water column, repeat forever. |
| 🎃 | **Pumpkin/Melon** | One row per layer, any number of layers — strafe, fall down the chute, turn around. |
| 🧭 | **Look Turning** | Slow down how long each turn takes (random min–max time) and turn pitch & yaw together, one after the other, or in random order. |
| ⏳ | **Wait for the turn** | Farming macros stand still until you're actually looking at your angle, *then* start working. |

---

## 🚜 Farming macros

Choose one in **Macro Settings → Macro Type**, set **Pitch Angle** and **Yaw Angle**, stand at the start of the farm and press **Delete**.

| Macro | Movement | Best for |
|---|---|---|
| **Netherwart / S-Shaped** | `A`, then `D + W`, alternating at each row end | Classic S-shaped wart / carrot / potato farms |
| **Sugarcane** | `A`, then `S`, alternating | Diagonal cane farms |
| **Cocoa Bean** | `W`, then `S`, alternating | Cocoa walls |
| **One Row** | `W` / `S` along one row, sneak + `A` at the end | Single long rows |
| **Serpentine (Layered)** | `A`/`D` along rows, `S` or `W` to the next row, drops between layers | Multi-row, multi-layer farms |
| **Mushroom Serpentine** | Serpentine top layer → drop → serpentine bottom layer → swim up → loop | Two-layer farms with a water column |
| **Pumpkin/Melon** | `A`/`D` along the row, fall, turn around | Stacked one-row drop farms |

### 🐍 Serpentine (Layered)

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

- Detects the **end of a row** by noticing you've stopped moving, waits a short random moment, steps forward/back by **Row Shift** blocks, then strafes back.
- Detects a **drop** when you land more than 1.5 blocks lower, and flips the forward/back step for the new layer.
- Doesn't need to know how many rows or layers there are.

### 🍄 Mushroom Serpentine

```
TOP LAYER     farm every row  ─────────►  drop down
                    ▲                          │
                    │ swim up                  ▼
BOTTOM LAYER  water column  ◄─────────  farm every row
```

- At the end of the bottom layer it finds the **water column** — in line with the row, or one block forward/back from the row end — and steps in.
- Holds **jump** to swim up and steps out only when it reaches open floor at the top layer's height.
- The top layer has regrown by then, so it just keeps going. Forever. 🔁

### 🎃 Pumpkin/Melon

```
LAYER 1   [Start] ════════════════════════► [Drop Chute R]
                                                  │ fall
LAYER 2   [Drop Chute L] ◄════════════════════ [Landing]
               │ fall
LAYER 3   [Landing] ════════════════════════► ...
```

- One row per layer, any number of layers. Turns around after every landing.
- Standing at a row end for a second without falling = bottom of the farm → stops and tells you in chat.

---

## ⚙️ Key settings

Open the settings with **`\`** (backslash) or `/aiom`.

| Section | Setting | Notes |
|---|---|---|
| Macro Settings | **Macro Type** | Which macro Delete starts |
| Macro Settings | **Pitch Angle / Yaw Angle** | Decimals allowed. Pitch −90…90, Yaw −180…180 |
| Serpentine (Layered) | **First Row** | Strafe direction on your starting row |
| Serpentine (Layered) | **First Layer Shift** | Backward (S) or Forward (W) to reach the next row on the first layer |
| Serpentine (Layered) | **Row Shift** | Blocks between rows (1–10) |
| Serpentine (Layered) | **Start Layer** | Mushroom Serpentine: Top or Bottom |
| Serpentine (Layered) | **Water Column Exit** | Mushroom Serpentine: Opposite Way (turn around) or Same Way |
| Pumpkin/Melon | **First Direction** | Left (A) or Right (D) |
| Look Turning | **Turn Time Min / Max** | 0–5000 ms per turn. Never faster than *Turn Speed* |
| Look Turning | **Turn Order** | Both Together, Pitch Then Yaw, Yaw Then Pitch, Random Order |
| Debugging | **Turn Speed** | Fastest allowed turn (default 10 = 2°/tick) |

---

## ⌨️ Keybinds

Change them in **Options → Controls → All-In-One Macro**.

| Key | Action |
|---|---|
| `Delete` | Toggle the macro |
| `\` | Open settings |
| `F6` / `F7` / `F8` | Hotbar profiles 1 / 2 / 3 |
| `` ` `` | Vertical clip |
| `+` (numpad) | Horizontal clip |
| `Pause` | Refresh Diana waypoints |
| `End` | Toggle packet cancelling |
| `F8` | Toggle path recording *(shares F8 with hotbar profile 3 — rebind one)* |

## 💬 Commands

| Command | Does |
|---|---|
| `/aiom` | Open settings |
| `/aiom hotbar save\|load\|delete <name>` · `/aiom hotbar list` | Hotbar profiles |
| `/aiom vclip <n>` · `/aiom hclip <n>` · `/aiom customclip <x> <y> <z>` | Teleport-clip |
| `/aiom pathfind block\|entity\|clear` | Pathfinding |
| `/aiom drawbeacon` | Draw a beacon beam |
| `/aiom fragbot` (`fb`) | Party the fragbot |
| `/aioc` | AIOM chat / account linking |

## 🛡️ Failsafes

Island check · Jacob's contest pause · Bedrock check · Auto `/setspawn` · AntiStuck · Desync check · Banwave check · Auto-disable timer · Random delays · Sound alerts · Discord webhook alerts with screenshots.

---

## 📦 Install

1. Install **Minecraft 1.8.9 + Forge** (runs on **Java 8**).
2. Drop the newest `aiom-1.8.0-creleaseN.jar` into `.minecraft/mods` (remove older AIO-Macro jars).
3. Launch, press `\`, set your macro and angles.

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

## 🗂️ Where things live

```
src/main/java/me/jacktym/aiomacro/
├── config/AIOMVigilanceConfig.java   ← every setting in the GUI
├── features/MacroHandler.java        ← starts/stops macros, "wait for the turn"
├── features/SetPlayerLook.java       ← aiming: decimal angles, turn time & order
├── features/SerpentineFarm.java      ← Serpentine (Layered) + Mushroom Serpentine
├── features/PumpkinMelon.java        ← Pumpkin/Melon
├── features/NetherWart.java, SugarCane.java, OneRow.java
└── features/Failsafe.java            ← all failsafes
```

---

## 🐛 Known issues

- **Jacob's contest auto-resume never fires.** The macro pauses when a contest starts but stays off afterwards — restart it yourself.
- **F8** is bound to both *HotBar Profile Three* and *Toggle Path Recording* by default.
- `Trades` (a coin feature) was missing from the original source, so it's disabled.
- Some online features (fragbot list, account linking, remote control, banwave check) talk to the original author's services, which may no longer be online. **Remote Controlling** is on by default — turn it off under *Account Linking* if you don't use it.

## 📜 Changelog

| Build | What changed |
|---|---|
| **crelease4** | 🎃 Pumpkin/Melon macro |
| **crelease3** | 🍄 Mushroom Serpentine · 🧭 Look Turning (turn time & order) · ⏳ macros wait for the turn |
| **crelease2** | 🐍 Serpentine (Layered) · 🎯 decimal Pitch/Yaw text boxes · build fixed for JDK 17 & dead repos |

## 🙏 Credits

- Original mod: **JackTYM** — AIO-Macro.
- Built with [Essential](https://essential.gg/) / Vigilance and Minecraft Forge.
