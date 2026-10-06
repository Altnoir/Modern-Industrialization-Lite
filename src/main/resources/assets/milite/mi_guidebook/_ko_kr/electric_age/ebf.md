---
navigation:
  title: "전기 용광로"
  icon: "milite:electric_blast_furnace"
  position: 105
  parent: milite:electric_age.md
item_ids:
  - milite:electric_blast_furnace
  - milite:cupronickel_coil
  - milite:lv_energy_input_hatch
  - milite:mv_energy_input_hatch
---

# 전기 용광로

전기 용광로는 전기로 작동하는 용광로입니다. 새로운 조합법들을 활용할 수 있고, 다른 전기 멀티블록 기계들처럼 최대 128RF/t 만큼의 오버클럭을 지원합니다.

<Recipe id="milite:electric_age/machine/electric_blast_furnace_asbl" />

전기 용광로는 내열 기계 케이싱 한 층과 중앙이 빈 백동 코일 두 층, 그리고 추가적인 내열 기계 케이싱 층으로 구성됩니다. 컨트롤러 블록은 제일 아래 층에 위치해야 합니다. 해치는 내열 기계 케이싱 위치에만 있을 수 있습니다.

<Recipe id="milite:materials/cupronickel/craft/coil" />

전기 용광로는 전기를 받아들일 에너지 반입 해치가 없다면 작동하지 않습니다. LV 에너지 반입 해치는 LV 전선에만 연결됩니다.

<Recipe id="milite:hatches/basic/energy_input_hatch" />

MV 에너지 반입 해치는 MV 전선에만 연결됩니다. 만약 전기 용광로에 많은 에너지를 공급하고 싶다면, MV 해치 사용을 추천드립니다. 아직 만들 수는 없지만요...

<Recipe id="milite:hatches/advanced/energy_input_hatch" />

