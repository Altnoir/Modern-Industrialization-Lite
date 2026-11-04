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

<Recipe id="milite:processing_table/configurable_chest" />

The Polarizer polarizes stuff. It doesn't have many recipes yet, but it is already useful to automate Motors!

<Recipe id="milite:processing_table/electric_polarizer" />

The first machine you'll want to make a lot of! The Assembler allows you to automate almost all recipes, up to 3 per Assembler if you use slot locking.

<Recipe id="milite:processing_table/electric_assembler" />

We strongly suggest that you automate Machine Hull, Analog Circuits, Motors, Pistons, Robot Arms and Conveyor Belts using Assemblers as soon as possible, that will make crafting electric machines basically free!

## Redstone Control

The top right corner of an electric machine or multiblock screen has two buttons. The redstone button on the right switches redstone control on and off; once it is on, a signal button appears to its left and changes whether the machine needs a high or a low signal.

## Overclocking

An Overclocker Unit is a block that makes electric machines run faster. Place it against any side of a machine and that machine processes twice as fast, out of the same FE per craft. Units stack: every further unit touching the machine doubles the speed again, and the limit is one tick per craft.

A faster machine asks for more FE per tick, so keep its buffer full. Neither continuous processing nor lubricant changes machine speed.
