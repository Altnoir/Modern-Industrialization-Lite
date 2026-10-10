---
navigation:
  title: "电力高炉"
  icon: "milite:electric_blast_furnace"
  position: 106
  parent: milite:electric_age.md
item_ids:
  - milite:electric_blast_furnace
  - milite:stainless_steel_blast_furnace
  - milite:iridium_blast_furnace
  - milite:cupronickel_coil
  - milite:kanthal_coil
  - milite:superconductor_coil
  - milite:electric_energy_input_hatch
  - milite:aluminum_alloy_energy_input_hatch
---

# 电力高炉

<GameScene zoom="2" interactive={true} fullWidth={true}>
    <MultiblockShape controller="electric_blast_furnace" />
</GameScene>

电力高炉是蒸汽高炉的电动版本。它一共有三种，每种对应一种线圈：电力高炉用白铜线圈搭建，不锈钢高炉用坎塔尔合金线圈搭建，铱高炉用超导线圈搭建。每种高炉运行自己等级的配方，而高级高炉还能处理所有低级高炉的配方，所以铱高炉也能做另外两种高炉的活。蒸汽高炉的配方仍归蒸汽高炉所有，只有它能处理，所以那几条只有它会做的配方还是得留着蒸汽高炉。

<Recipe id="milite:electric_age/machine/electric_blast_furnace_asbl" />

<Recipe id="milite:electric_age/machine/stainless_steel_blast_furnace_asbl" />

<Recipe id="milite:electric_age/machine/iridium_blast_furnace_asbl" />

每种电力高炉都由一层耐热机器外壳、两层中空的对应线圈和另一层耐热机器外壳制成。控制器必须放在底层，且仓室需放在顶层或底层。一种高炉只认它对应的那种线圈，所以界面里不需要选择任何东西。

<Recipe id="milite:materials/cupronickel/craft/coil" />

不要忘记添加能量输入仓，否则电力高炉将没有能量！电力能量输入仓最多向高炉输入32 RF/t。

<Recipe id="milite:hatches/electric/energy_input_hatch" />

铝合金能量输入仓最多传输128 RF/t，如果你的电力高炉需要大量能量，这会非常有用！

<Recipe id="milite:hatches/aluminum_alloy/energy_input_hatch" />

