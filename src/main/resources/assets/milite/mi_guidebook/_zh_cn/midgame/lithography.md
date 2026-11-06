---
navigation:
  title: "处理器与光刻机"
  icon: "milite:stainless_steel_lithography"
  position: 202
  parent: milite:midgame.md
item_ids:
  - milite:stainless_steel_lithography
  - milite:titanium_lithography
  - milite:iridium_lithography
  - milite:processor
  - milite:advanced_processor
  - milite:quantum_processor
  - milite:silicon_plate
---

# 处理器与光刻机

前期继续使用模拟电路与电子电路。从不锈钢阶段开始，光刻机将硅板和金属板直接加工成处理器。机器有两个物品输入槽、一个输出槽，没有流体槽；每批稳定产出一个处理器，没有副产物。

## 准备硅板

使用压缩机将一个硅锭压成一块硅板。光刻配方使用硅板，不需要单晶硅或硅晶圆。

<Recipe id="milite:materials/silicon/compressor/main" />

## 第一台光刻机

使用铝合金机器外壳、两块不锈钢板、一个玻璃方块、一个机械臂和一个电子电路，在加工台中制造不锈钢光刻机。它不需要消耗自身产出的处理器。

<Recipe id="milite:processing_table/stainless_steel_lithography" />
<Recipe id="milite:lithography/processor" />

一块硅板和两块琥珀金板可生产一个处理器，接替原数字电路在不锈钢机器中的用途。

## 升级光刻机

使用原有光刻机、下一等级的金属板、玻璃方块、机械臂和上一等级的处理器升级机器。高等级光刻机也可以加工低等级配方。

<Recipe id="milite:processing_table/titanium_lithography" />
<Recipe id="milite:lithography/advanced_processor" />

钛光刻机使用一块硅板和两块退火铜板，生产一个进阶处理器，接替原处理单元的用途。

<Recipe id="milite:processing_table/iridium_lithography" />
<Recipe id="milite:lithography/quantum_processor" />

铱光刻机使用一块硅板和两块超导体板，生产一个量子处理器，接替原量子电路的用途。高阶处理器不消耗低阶处理器；没有超频时，各等级配方在对应等级的光刻机中每批需要五秒，高等级机器加工低等级配方会更快。
