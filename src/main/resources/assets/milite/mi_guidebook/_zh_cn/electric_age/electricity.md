---
navigation:
  title: "电力"
  icon: "milite:electric_steam_turbine"
  position: 104
  parent: milite:electric_age.md
item_ids:
  - milite:electric_steam_turbine
  - milite:electric_diesel_generator
---

# 电力

电力蒸汽涡轮机使用蒸汽来发电。它将每 mb 蒸汽转换为 1 RF，每刻最多转换 32 mb。

<Recipe id="milite:electric_age/machine/electric_steam_turbine_asbl" />

电力蒸汽涡轮机会自动向紧贴其输出面放置的任意机器输送电力。它的内部缓存可容纳 256 RF，并且具有低压（LV）*等级*。

电力柴油发电机是电力蒸汽涡轮机的替代选择。它使用多种燃料发电。目前你可以燃烧杂酚油。（可用燃料列表请查阅 REI）。

<Recipe id="milite:electric_age/machine/electric_diesel_generator_asbl" />

## Modern Industrialization Lite 中的电力

Modern Industrialization Lite **并不**添加任何自带的电缆或变压器。每台电力机器都只是对外提供标准的 NeoForge 能量能力，因此可以**由任何其它模组**的 RF 电缆、能量管道或储能方块来供电。

这意味着：

- 直接用其它模组的电缆或管道给机器供电，或者把发电机紧挨着机器放置。
- 再也不需要匹配电压：你不必仅仅为了在等级之间传输电力而升级机器外壳，也不必让电力经过变压器。
- 等级（LV、MV、HV、EV、SV）依然存在，但它们只描述机器是按多少 RF/t 设计的，以及其内部缓存有多大。缓存已满的机器只是停止接收更多 RF——不会发生爆炸。

请记住，机器只在标记为输入的面接收能量，而发电机只从其输出面输出能量。
