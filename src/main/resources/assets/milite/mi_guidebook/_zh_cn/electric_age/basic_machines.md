---
navigation:
  title: "电力机器"
  icon: "milite:electric_assembler"
  position: 105
  parent: milite:electric_age.md
item_ids:
  - milite:configurable_chest
  - milite:electric_polarizer
  - milite:electric_assembler
---

# 电力机器

电力机器使用 FE 加工配方。电力等级的固定速度倍率为4，铝合金、不锈钢、钛和铱等级分别为8、16、32和64。更高速度会按比例提高每刻能耗，但同一配方的总能耗不变。配方还会标明所需的最低机器等级。

你可以使用REI查看你已经熟悉的机器的电力版本的配方。


可配置箱子并非机器，但当你拥有模拟电路后即可制作它！

它具备27个可配置槽位及自动弹出功能！对自动化系统非常实用！

<Recipe id="milite:electric_age/machine/configurable_chest_asbl" />

两极磁化机用于极化物品。当前配方虽不多，但已可用于自动化生产马达！

<Recipe id="milite:processing_table/electric_polarizer" />

第一台你会想要大量生产的机器！装配机允许你自动化几乎所有配方，如果使用槽位锁定功能，每台装配机最多可处理3种配方。

<Recipe id="milite:processing_table/electric_assembler" />

我们强烈建议你尽快使用装配机自动化生产机器框架、模拟电路、马达、活塞、机械臂和传送带，这将使电力机器的制造几乎没有成本！

## 红石控制模块

<ItemImage id="milite:redstone_control_module" />

红石控制模块可用于使用红石信号控制电力机器与多方块结构的行为。

手持模块进行配置后，将其放入机器或多方块控制器中即可生效。

## 超速模块

超速模块可将支持它的电力机器和多方块结构的速度提高到两倍。将其放入模块槽，或手持模块右键机器即可安装。模块不会随加工消耗，可以从槽位取出。

超速模块将每刻 FE 需求提高到两倍，同一配方的总能耗不变。供电充足时才能达到完整速度。持续加工与润滑剂不会提高机器速度。

