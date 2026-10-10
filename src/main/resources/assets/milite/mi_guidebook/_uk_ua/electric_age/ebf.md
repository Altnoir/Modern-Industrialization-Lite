---
navigation:
  title: "Електрична доменна піч"
  icon: "milite:electric_blast_furnace"
  position: 106
  parent: milite:electric_age.md
item_ids:
  - milite:electric_blast_furnace
  - milite:cupronickel_coil
  - milite:electric_energy_input_hatch
  - milite:aluminum_alloy_energy_input_hatch
---

# Електрична доменна піч

<GameScene zoom="2" interactive={true} fullWidth={true}>
    <MultiblockShape controller="electric_blast_furnace" />
</GameScene>

Електрична доменна піч — це електрична версія парової доменної печі. Вона розблоковує нові рецепти, і, як і інші багатоблокові електричні машини, її розгін обмежено 128 RF/т усталено.

<Recipe id="milite:electric_age/machine/electric_blast_furnace_asbl" />

Електрична доменна піч складається з одного шару термостійких машинних обшивок, двох шарів мідно-нікелевих котушок і ще одного шару термостійких машинних обшивок. Контролер повинен бути на нижньому шарі, а шлюзи на верхньому або нижньому шарі.

<Recipe id="milite:materials/cupronickel/craft/coil" />

Не забудьте додати вхідні шлюзи енергії, інакше електрична доменна піч не матиме енергії! Електричний роз'єм для подачі енергії подає в піч щонайбільше 32 RF/т.

<Recipe id="milite:hatches/electric/energy_input_hatch" />

Роз'єм для подачі енергії з алюмінієвого сплаву передає до 128 RF/т, що може бути корисним, якщо вам потрібна дуже багато енергії для електричної доменної печі!

<Recipe id="milite:hatches/aluminum_alloy/energy_input_hatch" />

