package aztech.milite.items.armor;

import aztech.milite.MI;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorMaterial;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;
import java.util.Map;

public final class MIArmorMaterials {
    private static final DeferredRegister<ArmorMaterial> DR = DeferredRegister.create(Registries.ARMOR_MATERIAL, MI.ID);

    public static final Holder<ArmorMaterial> DIESEL_JETPACK = DR.register("diesel_jetpack", location -> new ArmorMaterial(
            Map.of(),
            0,
            SoundEvents.ARMOR_EQUIP_GENERIC,
            () -> {
                throw new UnsupportedOperationException("Cannot repair Diesel Jetpack");
            },
            List.of(new ArmorMaterial.Layer(location)),
            0,
            0));
    public static final Holder<ArmorMaterial> GRAVICHESTPLATE = DR.register("gravichestplate", location -> new ArmorMaterial(
            Map.of(),
            0,
            SoundEvents.ARMOR_EQUIP_GENERIC,
            () -> {
                throw new UnsupportedOperationException("Cannot repair GraviChestPlate");
            },
            List.of(new ArmorMaterial.Layer(location)),
            0,
            0));
    public static final Holder<ArmorMaterial> QUANTUM = DR.register("quantum", location -> new ArmorMaterial(
            Map.of(),
            0,
            SoundEvents.ARMOR_EQUIP_GENERIC,
            () -> {
                throw new UnsupportedOperationException("Cannot repair quantum armor");
            },
            List.of(new ArmorMaterial.Layer(location)),
            0,
            0));

    public static void init(IEventBus modEventBus) {
        DR.register(modEventBus);
    }

    private MIArmorMaterials() {}
}
