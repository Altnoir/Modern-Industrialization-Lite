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
  - milite:digital_circuit
  - milite:digital_circuit_board
  - milite:processor
  - milite:quantum_processor
  - milite:silicon_plate
---

# Processors and Lithography

Craft Analog, Electronic, and then Digital Circuits. Stainless Steel Machine Hulls use Digital Circuits. Lithography Machines make processors directly from Silicon Plates and metal plates, using two item inputs, one output and no fluids.

Assemble a Digital Circuit Board, then combine it with Electronic Circuits, Op Amps and Aluminum Wire to craft a Digital Circuit.

<Recipe id="milite:electric_age/circuit/assembler/digital_circuit_board" />
<Recipe id="milite:electric_age/circuit/craft/digital_circuit_asbl" />

## Preparing Silicon Plates

Use a Compressor to turn one Silicon Ingot into one Silicon Plate. Lithography recipes use Silicon Plates and do not need Monocrystalline Silicon or Silicon Wafers.

<Recipe id="milite:materials/silicon/compressor/main" />

## The First Lithography Machine

Build the stainless steel machine with an Aluminum Alloy Machine Hull, two Stainless Steel Plates, a Glass Block, a Robot Arm and an Electronic Circuit. It does not require any of the processors it produces.

<Recipe id="milite:processing_table/stainless_steel_lithography" />
<Recipe id="milite:lithography/processor" />

One Silicon Plate and two Annealed Copper Plates produce one Processor in a Stainless Steel Lithography Machine. Processors are used in titanium machinery.

## Upgrading

Upgrade your existing machine with plates of the next material, a Glass Block, a Robot Arm and the previous stage's circuit or processor. Higher-tier machines also run lower-tier recipes.

<Recipe id="milite:processing_table/titanium_lithography" />
Titanium Lithography Machines use one Silicon Plate and two Superconductor Plates to make one Quantum Processor.

<Recipe id="milite:lithography/quantum_processor" />

<Recipe id="milite:processing_table/iridium_lithography" />

Iridium Lithography Machines process both recipes faster. With sufficient power and no overclocking, the Processor takes 10 seconds in a stainless steel machine, and the Quantum Processor takes 10 seconds in a titanium machine.
