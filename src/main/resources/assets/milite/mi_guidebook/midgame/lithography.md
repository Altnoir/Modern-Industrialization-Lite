---
navigation:
  title: "Processors and Lithography"
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

# Processors and Lithography

Analog and Electronic Circuits handle the early game. Starting with stainless steel, Lithography Machines make Processors directly from Silicon Plates and metal plates. There are two item inputs, one output and no fluid tanks. Every recipe produces one processor without byproducts.

## Preparing Silicon Plates

Use a Compressor to turn one Silicon Ingot into one Silicon Plate. Lithography recipes use Silicon Plates and do not need Monocrystalline Silicon or Silicon Wafers.

<Recipe id="milite:materials/silicon/compressor/main" />

## The First Lithography Machine

Build the stainless steel machine with an Aluminum Alloy Machine Hull, two Stainless Steel Plates, a Glass Block, a Robot Arm and an Electronic Circuit. It does not require any of the processors it produces.

<Recipe id="milite:processing_table/stainless_steel_lithography" />
<Recipe id="milite:lithography/processor" />

One Silicon Plate and two Electrum Plates produce one Processor. Processors replace Digital Circuits in stainless steel machinery.

## Upgrading

Upgrade your existing machine with plates of the next material, a Glass Block, a Robot Arm and the previous grade of processor. These machines can also run recipes from lower levels.

<Recipe id="milite:processing_table/titanium_lithography" />
<Recipe id="milite:lithography/advanced_processor" />

Titanium Lithography Machines use one Silicon Plate and two Annealed Copper Plates to make one Advanced Processor, replacing Processing Units.

<Recipe id="milite:processing_table/iridium_lithography" />
<Recipe id="milite:lithography/quantum_processor" />

Iridium Lithography Machines use one Silicon Plate and two Superconductor Plates to make one Quantum Processor, replacing Quantum Circuits. Higher-grade recipes do not consume lower-grade processors. Each batch takes five seconds in its matching machine before overclocking; faster machines run lower-grade recipes more quickly.
