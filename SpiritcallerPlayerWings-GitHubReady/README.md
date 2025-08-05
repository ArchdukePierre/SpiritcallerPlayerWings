
# Spiritcaller Player Wings (Forge 1.20.1 / 47.4.0)

**What it is:** server-authoritative cosmetic that shows the *Illage & Spillage: Respillaged* **Spiritcaller** wings/halo/circle on selected players.

**Important:** This mod does **not** redistribute any Respillaged assets. Clients must have **this mod** installed to render the wings, and (for the exact visuals) must also have **Illage & Spillage: Respillaged** installed. Until the final asset hook is wired, a placeholder glow wing + halo renders so you can test networking.

## Build

1. Install JDK 17.
2. `./gradlew genIntellijRuns` (or `genEclipseRuns`), then `./gradlew build`.

## Install

- Put the built JAR in both **server** and **clients** `mods/`.
- Also install **Illage & Spillage: Respillaged** on the **clients** (and server, if the pack already uses it).

## Commands

- `/wings give <player>` – enable on a player (persists).
- `/wings remove <player>` – disable.
- `/wings toggle <player>` – toggle.

## Wiring the EXACT visuals

Open `src/main/java/.../client/render/SpiritcallerWingsLayer.java` and replace the placeholder quads with calls that draw the **Spiritcaller** wings/halo. Two suggested routes:

### (A) GeckoLib (recommended)
If Respillaged exposes its models via GeckoLib, load them with resource IDs (examples, to be confirmed by checking the JAR):
- `illage_and_spillage:geo/spiritcaller.geo.json`
- `illage_and_spillage:animations/spiritcaller.animation.json`
- `illage_and_spillage:textures/entity/spiritcaller.png`

Render only the **wing bones** and **halo bone** at the player's transforms each tick.

### (B) Direct class reference
If Respillaged exposes a **Spiritcaller layer/model** class publicly, reflectively instantiate and render only the wing parts.

> I can fill this in for you once you confirm the **exact resource paths** present in your Respillaged JAR.



## Build via GitHub Actions (no local Java required)

1. Create a **new GitHub repository** (public or private).
2. Upload the contents of this project (drag & drop the files/folders in the repo root).
3. Go to the **Actions** tab → wait for the **Build (Forge 1.20.1)** workflow to run.
4. When it finishes, open the run → **Artifacts** → download **spiritcaller-player-wings**.
5. Inside, grab the JAR from `build/libs/` and place it in your `mods/` on server + clients.
