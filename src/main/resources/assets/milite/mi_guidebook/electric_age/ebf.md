---
navigation:
  title: "Electric Blast Furnace"
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

# Electric Blast Furnace

<GameScene zoom="2" interactive={true} fullWidth={true}>
    <MultiblockShape controller="electric_blast_furnace" />
</GameScene>

The Electric Blast Furnace is the electric version of the Steam Blast Furnace. There are three of them, one per coil: the Electric Blast Furnace is built around Cupronickel Coils, the Stainless Steel Blast Furnace around Kanthal Coils and the Iridium Blast Furnace around Superconductor Coils. Each one runs the recipes of its own level, and a higher furnace also runs every recipe of the furnaces below it, so the Iridium Blast Furnace can still do the work of the other two.

<Recipe id="milite:electric_age/machine/electric_blast_furnace_asbl" />

<Recipe id="milite:electric_age/machine/stainless_steel_blast_furnace_asbl" />

<Recipe id="milite:electric_age/machine/iridium_blast_furnace_asbl" />

Every furnace is made of one layer of Heatproof Machine Casings, two hollow layers of its own coil and one other layer of Heatproof Machine Casings. The controller must go on the bottom layer, and hatches on the top or bottom layer. A furnace only fits the coil it is built around, so there is nothing to select in the interface.

<Recipe id="milite:materials/cupronickel/craft/coil" />

Don't forget to add Energy Input Hatch(es) or the EBF won't have energy! The LV Energy Input Hatch will only connect to LV cables.

<Recipe id="milite:hatches/basic/energy_input_hatch" />

MV Energy Input Hatches will only connect to MV cables, which can be useful if you need a ton of energy for your EBF! You can't craft them yet, but keep in mind they exist...

<Recipe id="milite:hatches/advanced/energy_input_hatch" />

