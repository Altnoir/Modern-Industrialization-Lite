---
navigation:
  title: "High Pressure!"
  icon: "milite:pressurizer"
  position: 202
  parent: milite:midgame.md
item_ids:
  - milite:pressurizer
  - milite:high_pressure_large_steam_boiler
  - milite:high_pressure_advanced_large_steam_boiler
  - milite:large_steam_turbine
  - milite:heat_exchanger
  - milite:hv_steam_turbine
---

# High Pressure!

## Pressurizer

<GameScene zoom="2" interactive={true} fullWidth={true}>
    <MultiblockShape controller="pressurizer" />
</GameScene>

The Pressurizer is a new multiblock made of titanium that can turn water into HP (high pressure) water, steam into HP steam, and the other way around.

<Recipe id="milite:electric_age/machine/pressurizer_asbl" />

## HP Large Steam Boiler

<GameScene zoom="2" interactive={true} fullWidth={true}>
    <MultiblockShape controller="high_pressure_large_steam_boiler" />
</GameScene>

Once you have access to HP water, you can use a High Pressure Large Steam Boiler to produce HP steam.

One millibucket of HP steam is worth 8 mb of regular steam, that is 8RF.

<Recipe id="milite:electric_age/machine/high_pressure_large_steam_boiler_asbl" />

## HP Advanced Large Steam Boiler

<GameScene zoom="2" interactive={true} fullWidth={true}>
    <MultiblockShape controller="high_pressure_advanced_large_steam_boiler" />
</GameScene>

Later on, you will also be able to build the advanced version of the HP Large Steam Boiler.

## Large Steam Turbine

<GameScene zoom="2" interactive={true} fullWidth={true}>
    <MultiblockShape controller="large_steam_turbine" />
</GameScene>

A Large Steam Turbine will accept both regular steam (1 mb = 1RF) and HP steam (1 mb = 8RF) intoRF, and will generate up to 16384RF/t! **However it will not give you back regular nor HP water.**

<Recipe id="milite:electric_age/machine/large_steam_turbine_asbl" />

## Heat Exchanger

<GameScene zoom="2" interactive={true} fullWidth={true}>
    <MultiblockShape controller="heat_exchanger" />
</GameScene>

Remember that pressurizing water into HP water costs a lot of energy, but the turbine will not give it back! You can use a Heat Exchanger to recover the HP water for another cycle.

<Recipe id="milite:electric_age/machine/heat_exchanger_asbl" />

## HV Steam Turbine

Another option for small setups is to use an HV steam turbine. Like other HV generators, it will produce 512RF/t. This one will only accept regular steam.

<Recipe id="milite:electric_age/machine/hv_steam_turbine_asbl" />

