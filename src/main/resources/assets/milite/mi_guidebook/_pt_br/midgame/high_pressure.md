---
navigation:
  title: "Alta Pressão!"
  icon: "milite:pressurizer"
  position: 202
  parent: milite:midgame.md
item_ids:
  - milite:pressurizer
  - milite:high_pressure_large_steam_boiler
  - milite:high_pressure_advanced_large_steam_boiler
  - milite:large_steam_turbine
  - milite:heat_exchanger
  - milite:stainless_steel_steam_turbine
---

# Alta Pressão!

## Pressurizador

<GameScene zoom="2" interactive={true} fullWidth={true}>
    <MultiblockShape controller="pressurizer" />
</GameScene>

O Pressurizador é um novo multibloco feito de titânio que pode transformar água em água de Alta Pressão (HP), vapor em vapor de Alta Pressão e vice-versa.

<Recipe id="milite:electric_age/machine/pressurizer_asbl" />

## Caldeira de Vapor Grande HP

<GameScene zoom="2" interactive={true} fullWidth={true}>
    <MultiblockShape controller="high_pressure_large_steam_boiler" />
</GameScene>

Quando você tiver acesso à água HP, poderá usar uma Caldeira de Vapor Grande de Alta Pressão para produzir vapor HP.

Um milibalde de vapor HP vale 8 mb de vapor comum, ou seja, 8 RF.

<Recipe id="milite:electric_age/machine/high_pressure_large_steam_boiler_asbl" />

## Caldeira de Vapor Grande HP Avançada

<GameScene zoom="2" interactive={true} fullWidth={true}>
    <MultiblockShape controller="high_pressure_advanced_large_steam_boiler" />
</GameScene>

Mais adiante, você também poderá construir a versão avançada da Caldeira de Vapor Grande HP.

## Turbina de Vapor Grande

<GameScene zoom="2" interactive={true} fullWidth={true}>
    <MultiblockShape controller="large_steam_turbine" />
</GameScene>

Uma Turbina de Vapor Grande aceitará tanto vapor comum (1 mb = 1 RF) quanto vapor HP (1 mb = 8 RF) e os converterá em RF, podendo gerar até 16384 RF/t! **No entanto, ela não devolverá nem água comum nem água HP.**

<Recipe id="milite:electric_age/machine/large_steam_turbine_asbl" />

## Trocador de Calor

<GameScene zoom="2" interactive={true} fullWidth={true}>
    <MultiblockShape controller="heat_exchanger" />
</GameScene>

Lembre-se de que pressurizar água em água HP consome muita energia, e a turbina não a devolverá! Você pode usar um Trocador de Calor para recuperar a água HP para outro ciclo.

<Recipe id="milite:electric_age/machine/heat_exchanger_asbl" />

## Turbina de Vapor de Aço Inoxidável

Outra opção para instalações menores é usar uma Turbina de Vapor de Aço Inoxidável. Como outros geradores desse nível, ela produzirá 512 RF/t. Esta aceitará apenas vapor comum.

<Recipe id="milite:electric_age/machine/stainless_steel_steam_turbine_asbl" />

