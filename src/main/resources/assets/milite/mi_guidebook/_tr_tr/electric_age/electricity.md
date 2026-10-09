---
navigation:
  title: "Elektrik"
  icon: "milite:electric_steam_turbine"
  position: 104
  parent: milite:electric_age.md
item_ids:
  - milite:electric_steam_turbine
  - milite:electric_diesel_generator
---

# Elektrik

Elektrik Buhar Türbini elektrik üretmek için buhar kullanır. Her mb buharı 1 RF'ye dönüştürür ve her tikte en fazla 32 mb dönüştürür.

<Recipe id="milite:electric_age/machine/electric_steam_turbine_asbl" />

Elektrik Buhar Türbini, çıkış tarafına doğrudan bitişik yerleştirilmiş her makineye otomatik olarak elektrik gönderir. Dahili arabelleği 256 RF tutabilir ve Alçak Gerilim (LV) *Kademesine* sahiptir.

Elektrik Dizel Jeneratör, Elektrik Buhar Türbinine bir alternatiftir. Elektrik üretmek için çeşitli yakıtlar kullanır. Şimdilik Kreozot yakabilirsiniz. (Yakılabilir yakıtların listesi için REI'ye bakın).

<Recipe id="milite:electric_age/machine/electric_diesel_generator_asbl" />

## Modern Industrialization Lite'ta Enerji

Modern Industrialization Lite **kendine ait kablo veya transformatör eklemez**. Her elektrikli makine yalnızca standart NeoForge enerji yeteneğini sunar, bu yüzden **başka herhangi bir modun** RF kablosu, enerji borusu veya enerji depolama bloğu ile beslenebilir.

Yani:

- Bir makineyi doğrudan başka bir modun kablosundan veya borusundan besleyin ya da bir jeneratörü makinenin hemen yanına koyun.
- Artık eşleştirilecek bir voltaj yok: gücü kademeler arasında taşımak için makine gövdelerini yükseltmenize veya gücü transformatörlerden geçirmenize gerek yok.
- Kademeler (LV, MV, HV, EV, SV) hâlâ var, ancak yalnızca makinenin kaç RF/t için üretildiğini ve dahili arabelleğinin ne kadar büyük olduğunu belirtirler. Arabelleği dolu olan bir makine daha fazla RF almayı durdurur - hiçbir şey patlamaz.

Makinelerin enerjiyi yalnızca giriş olarak işaretlenmiş yüzlerden kabul ettiğini ve jeneratörlerin enerjiyi yalnızca çıkış tarafından dışarı verdiğini unutmayın.
