---
navigation:
  title: "ТЕЕЕЕРМОЯДЕРНИЙ СИНТЕЗ"
  icon: "milite:fusion_reactor"
  position: 302
  parent: milite:endgame.md
item_ids:
  - milite:fusion_reactor
  - milite:plasma_turbine
---

# ТЕЕЕЕРМОЯДЕРНИЙ СИНТЕЗ

## Термоядерний реактор

<GameScene zoom="1" interactive={true} fullWidth={true}>
    <MultiblockShape controller="fusion_reactor" />
</GameScene>

Термоядерний реактор — найкраще джерело енергії! Він може поєднувати дейтерій, тритій та/або гелій-3 у гелієву плазму, найпотужніше паливо в грі! Однак, щоб запалити реакцію, потрібна велика кількість енергії.

<Recipe id="milite:processing_table/fusion_reactor" />

## Плазмова турбіна

<GameScene zoom="2" interactive={true} fullWidth={true}>
    <MultiblockShape controller="plasma_turbine" />
</GameScene>

Плазмова турбіна може перетворювати гелієву плазму в RF зі швидкістю 100 kRF на мВ. Його максимальна продуктивність становить приблизно 1 MRF/т.

<Recipe id="milite:processing_table/plasma_turbine" />

