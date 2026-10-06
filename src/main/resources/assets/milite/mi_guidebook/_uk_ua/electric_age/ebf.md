---
navigation:
  title: "Електрична доменна піч"
  icon: "milite:electric_blast_furnace"
  position: 106
  parent: milite:electric_age.md
item_ids:
  - milite:electric_blast_furnace
  - milite:cupronickel_coil
  - milite:lv_energy_input_hatch
  - milite:mv_energy_input_hatch
---

# Електрична доменна піч

<GameScene zoom="2" interactive={true} fullWidth={true}>
    <MultiblockShape controller="electric_blast_furnace" />
</GameScene>

Електрична доменна піч — це електрична версія парової доменної печі. Вона розблоковує нові рецепти, і, як і інші багатоблокові електричні машини, її розгін обмежено 128RF/т усталено.

<Recipe id="milite:electric_age/machine/electric_blast_furnace_asbl" />

Електрична доменна піч складається з одного шару термостійких машинних обшивок, двох шарів мідно-нікелевих котушок і ще одного шару термостійких машинних обшивок. Контролер повинен бути на нижньому шарі, а шлюзи на верхньому або нижньому шарі.

<Recipe id="milite:materials/cupronickel/craft/coil" />

Не забудьте додати вхідні шлюзи енергії, інакше електрична доменна піч не матиме енергії! НН роз'єм входу енергії приєднуватиметься лише до кабелів низької напруги.

<Recipe id="milite:hatches/basic/energy_input_hatch" />

СН роз'єм входу енергії підключаються лише до кабелів середньої напруги, що може бути корисним, якщо вам потрібна дуже багато енергії для електричної доменної печі! Ви ще не можете створити їх, але майте на увазі, що вони існують…

<Recipe id="milite:hatches/advanced/energy_input_hatch" />

