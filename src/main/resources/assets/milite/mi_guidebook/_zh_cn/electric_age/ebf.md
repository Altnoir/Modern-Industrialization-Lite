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
  - milite:lv_energy_input_hatch
  - milite:mv_energy_input_hatch
---

# 电力高炉

<GameScene zoom="2" interactive={true} fullWidth={true}>
    <MultiblockShape controller="electric_blast_furnace" />
</GameScene>

电力高炉是蒸汽高炉的电动版本。它一共有三种，每种对应一种线圈：电力高炉用白铜线圈搭建，不锈钢高炉用坎塔尔合金线圈搭建，铱高炉用超导线圈搭建。每种高炉运行自己等级的配方，而高级高炉还能处理所有低级高炉的配方，所以铱高炉也能做另外两种高炉的活。三种高炉也都能处理蒸汽高炉的配方，所以只要有一台电力高炉，就不必再为了那几条只有蒸汽高炉会做的配方而留着蒸汽高炉了。

<Recipe id="milite:electric_age/machine/electric_blast_furnace_asbl" />

<Recipe id="milite:electric_age/machine/stainless_steel_blast_furnace_asbl" />

<Recipe id="milite:electric_age/machine/iridium_blast_furnace_asbl" />

每种电力高炉都由一层耐热机器外壳、两层中空的对应线圈和另一层耐热机器外壳制成。控制器必须放在底层，且仓室需放在顶层或底层。一种高炉只认它对应的那种线圈，所以界面里不需要选择任何东西。

<Recipe id="milite:materials/cupronickel/craft/coil" />

不要忘记添加能量输入仓，否则电力高炉将没有能量！低压能量输入仓仅能与低压电缆连接。

<Recipe id="milite:hatches/basic/energy_input_hatch" />

中压能量输入仓能与中压电缆连接，如果你的电力高炉需要大量能量，这会非常有用！你还不能制作它们，但请记住它们的存在……

<Recipe id="milite:hatches/advanced/energy_input_hatch" />

