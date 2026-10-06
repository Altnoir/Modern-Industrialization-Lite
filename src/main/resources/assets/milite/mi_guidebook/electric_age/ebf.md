---
navigation:
  title: "Electric Blast Furnace"
  icon: "milite:electric_blast_furnace"
  position: 106
  parent: milite:electric_age.md
item_ids:
  - milite:electric_blast_furnace
  - milite:cupronickel_coil
  - milite:lv_energy_input_hatch
  - milite:mv_energy_input_hatch
---

# Electric Blast Furnace

<GameScene zoom="2" interactive={true} fullWidth={true}>
    <MultiblockShape controller="electric_blast_furnace" />
</GameScene>

The Electric Blast Furnace is the electric version of the Steam Blast Furnace. It unlocks new recipes and like other multiblock electric machines, its overclock is bounded to 128RF/t by default.

<Recipe id="milite:electric_age/machine/electric_blast_furnace_asbl" />

The EBF is made of one layer of Heatproof Machine Casings, two hollow layers of Cupronickel Coils and one other layer of Heatproof Machine Casings. The controller must go on the bottom layer, and hatches on the top or bottom layer.

<Recipe id="milite:materials/cupronickel/craft/coil" />

Don't forget to add Energy Input Hatch(es) or the EBF won't have energy! The LV Energy Input Hatch will only connect to LV cables.

<Recipe id="milite:hatches/basic/energy_input_hatch" />

MV Energy Input Hatches will only connect to MV cables, which can be useful if you need a ton of energy for your EBF! You can't craft them yet, but keep in mind they exist...

<Recipe id="milite:hatches/advanced/energy_input_hatch" />

