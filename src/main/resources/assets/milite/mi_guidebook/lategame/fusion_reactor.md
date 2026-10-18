---
navigation:
  title: "FUUUUUSION"
  icon: "milite:fusion_reactor"
  position: 302
  parent: milite:endgame.md
item_ids:
  - milite:fusion_reactor
  - milite:plasma_turbine
---

# FUUUUUSION

## Fusion Reactor

<GameScene zoom="1" interactive={true} fullWidth={true}>
    <MultiblockShape controller="fusion_reactor" />
</GameScene>

The Fusion Reactor is the ultimate energy source! It can combine Deuterium, Tritium, and/or Helium-3 into Helium Plasma, the most powerful fuel in the game!. However, it requires a large amount of energy to ignite the reaction.

<Recipe id="milite:processing_table/fusion_reactor" />

## Plasma Turbine

<GameScene zoom="2" interactive={true} fullWidth={true}>
    <MultiblockShape controller="plasma_turbine" />
</GameScene>

The Plasma Turbine can turn Helium Plasma into RF, at the rate of 100 kRF per millibucket. Its maximum production rate is roughly 1 MRF/t.

<Recipe id="milite:processing_table/plasma_turbine" />

