# Modern Industrialization: Lite

A slimmed-down fork of [Modern Industrialization](https://github.com/AztechMC/Modern-Industrialization):
**the machines stay, the logistics go.**

Steam machines, electric machines, multiblocks (coke oven, EBF, distillation tower, assembly lines, fusion
reactor), materials, tools and the in-game guidebook all work just like you know them. What this fork removes is
MI's own transport layer:

- no cables, no transformers, no fluid pipes, no item pipes;
- machines, tanks, barrels and hatches simply expose the standard NeoForge energy, fluid and item capabilities;
- energy is plain RF: power a machine from another mod's cable, energy pipe or energy storage block, or simply
  place a generator next to it;
- the LV/MV/HV/EV/SV tiers still exist, but only as machine classes - they say how much RF/t a machine is built
  for and how large its buffer is. A full buffer just stops accepting power, nothing explodes.

**Bring your own logistics mod, and MI Lite supplies the factory.**

The mod id is `milite`. This is a separate mod, so do not install it next to Modern Industrialization.

## Requirements

- Minecraft 1.21.1
- NeoForge 21.1.228 or newer
- [GuideME](https://modrinth.com/mod/guideme) (required, it renders the guidebook)
- Optional: EMI, JEI or REI for recipe viewing, Jade for machine tooltips

## Installation

1. Install NeoForge for Minecraft 1.21.1.
2. Put this mod and [GuideME](https://modrinth.com/mod/guideme) into your `mods` folder.
3. Craft the guidebook in game, it walks you through the whole progression.

## Building from source

Java 21 is required.

```
./gradlew build
```

The jar is written to `build/libs`. `./gradlew runClient` starts a development client, and
`./gradlew runGameTestServer` runs the game test suite.

## Bug reports and contributions

Please report bugs on the [issue tracker](https://github.com/Altnoir/Modern-Industrialization-Lite/issues).
Translations live in `src/main/resources/assets/milite/lang`, so pull requests with translation fixes are welcome.
Modpack and resource pack developers should have a look at [docs/README.md](docs/README.md).

## Credits and license

This fork is based on [Modern Industrialization](https://github.com/AztechMC/Modern-Industrialization) by Azerococo
and Technici4n: the design, the code and most of the assets are theirs.

The code is under the MIT license and the assets are under CC0, see [LICENSE](LICENSE).

---

## 现代工业化

**Modern Industrialization: Lite** 是 [Modern Industrialization](https://github.com/AztechMC/Modern-Industrialization)
的精简分支。

模组 id 是 `milite`，它和原版 MI 是两个独立模组，按理说可以同时安装。

- 需要：Minecraft 1.21.1、NeoForge 21.1.228+、[GuideME](https://modrinth.com/mod/guideme)
- 可选：EMI / JEI / REI、Jade
- 自行构建：需要 Java 21，执行 `./gradlew build`，产物在 `build/libs`
- 问题反馈：[issue tracker](https://github.com/Altnoir/Modern-Industrialization-Lite/issues)
- 协议：代码 MIT，素材 CC0
