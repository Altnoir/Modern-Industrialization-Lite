---
navigation:
  title: "Electricity"
  icon: "milite:lv_steam_turbine"
  position: 104
  parent: milite:electric_age.md
item_ids:
  - milite:lv_steam_turbine
  - milite:lv_diesel_generator
---

# Electricity

The Steam Turbine uses Steam to produce electricity. It converts every mb of Steam to 1 RF, up to 32 mb converted every tick.

<Recipe id="milite:electric_age/machine/lv_steam_turbine_asbl" />

The Steam Turbine will automatically send electricity to any machine placed directly against its output side. Its internal buffer can hold 256 RF, and it has the Low Voltage (LV) *Tier*.

The Diesel Generator is an alternative to the Steam Turbine. It uses various fuels to produce electricity. For now, you can burn Creosote. (See REI for the list of burnable fuels).

<Recipe id="milite:electric_age/machine/lv_diesel_generator_asbl" />

## Power in Modern Industrialization Lite

Modern Industrialization Lite does **not** add any cables or transformers of its own. Every electric machine simply exposes the standard NeoForge energy capability, so it can be powered by an RF cable, energy pipe, or energy storage block from **any other mod**.

That means:

- Feed a machine directly from another mod's cable or pipe, or place a generator right next to it.
- There is no voltage to match any more: you no longer need to upgrade machine hulls or route power through transformers just to move it between tiers.
- Tiers (LV, MV, HV, EV, SV) still exist, but they only describe how much RF/t a machine is built for and how large its internal buffer is. A machine whose buffer is full simply stops accepting more RF - nothing will explode.

Remember that machines only accept energy on the sides that are marked as inputs, and that generators only push energy out of their output side.
