---
navigation:
  title: "Electric Machines"
  icon: "milite:electric_assembler"
  position: 105
  parent: milite:electric_age.md
item_ids:
  - milite:configurable_chest
  - milite:electric_polarizer
  - milite:electric_assembler
---

# Electric Machines

Electric machines process recipes using FE instead of steam. The electric level has a fixed speed multiplier of 4; aluminum alloy, stainless steel, titanium and iridium increase it to 8, 16, 32 and 64. Faster processing draws proportionally more FE/t, while total FE per craft stays the same. Recipes also specify a minimum machine level.

You can use REI to view the recipes for the electric version of the machines you are already familiar with.




The Configurable Chest is not a machine, but once you have Analog Circuits, you can craft it!

It has 27 configurable slots, and auto-eject functionality! Very useful for automation!

<Recipe id="milite:electric_age/machine/configurable_chest_asbl" />

The Polarizer polarizes stuff. It doesn't have many recipes yet, but it is already useful to automate Motors!

<Recipe id="milite:processing_table/electric_polarizer" />

The first machine you'll want to make a lot of! The Assembler allows you to automate almost all recipes, up to 3 per Assembler if you use slot locking.

<Recipe id="milite:processing_table/electric_assembler" />

We strongly suggest that you automate Machine Hull, Analog Circuits, Motors, Pistons, Robot Arms and Conveyor Belts using Assemblers as soon as possible, that will make crafting electric machines basically free!

## Redstone Control Modules

<ItemImage id="milite:redstone_control_module" />

A Redstone Control Module can be used to control the behavior of electric machines and multiblocks with redstone signals.

Configure the module in your hand, then insert it into the machine or multiblock controller.

## Overdrive Module

An Overdrive Module doubles the speed of supported electric machines and multiblocks. Install it in the module slot or right-click the machine while holding it. The module is reusable and can be removed from the slot.

The module doubles FE/t demand while keeping the same total FE per craft. Supply enough FE to reach full speed. Continuous processing and lubricant do not increase machine speed.

