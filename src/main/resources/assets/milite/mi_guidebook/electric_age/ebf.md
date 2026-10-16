---
navigation:
  title: "Electric Blast Furnace"
  icon: "milite:electric_blast_furnace"
  position: 106
  parent: milite:electric_age.md
item_ids:
  - milite:electric_blast_furnace
  - milite:cupronickel_coil
  - milite:kanthal_coil
  - milite:superconductor_coil
  - milite:electric_energy_input_hatch
  - milite:aluminum_alloy_energy_input_hatch
---

# Electric Blast Furnace

<GameScene zoom="2" interactive={true} fullWidth={true}>
    <MultiblockShape controller="electric_blast_furnace" />
</GameScene>

The Electric Blast Furnace is the electric version of the Steam Blast Furnace. There is only one of them, and it runs at one of three tiers that you pick with the slider in its interface: the Cupronickel Coil tier, the Kanthal Coil tier and the Superconductor Coil tier. The tier you pick is the level the furnace runs at, and a higher tier also runs every recipe of the tiers below it. The coil in the structure has to be the coil of that tier, so the slider and the coils you build with must agree or the multiblock will not form. The recipes of the Steam Blast Furnace stay with the steam furnace, which is the only one that runs them, so keep it around for the few recipes that only it knows.

<Recipe id="milite:electric_age/machine/electric_blast_furnace_asbl" />

The furnace is made of one layer of Heatproof Machine Casings, two hollow layers of coil and one other layer of Heatproof Machine Casings; the two middle layers take the coil of the tier the slider is set to. The controller must go on the bottom layer, and hatches on the top or bottom layer.

<Recipe id="milite:materials/cupronickel/craft/coil" />

Don't forget to add Energy Input Hatch(es) or the EBF won't have energy! An Electric Energy Input Hatch feeds at most 32 RF/t into the furnace.

<Recipe id="milite:hatches/electric/energy_input_hatch" />

Aluminum Alloy Energy Input Hatches move up to 128 RF/t, which can be useful if you need a ton of energy for your EBF!

<Recipe id="milite:hatches/aluminum_alloy/energy_input_hatch" />

