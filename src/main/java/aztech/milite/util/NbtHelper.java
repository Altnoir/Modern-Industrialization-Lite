package aztech.milite.util;

import aztech.milite.thirdparty.fabrictransfer.api.fluid.FluidVariant;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import org.jspecify.annotations.Nullable;

import java.util.function.Function;
import java.util.List;

public class NbtHelper {
    public static void putFluid(CompoundTag tag, String key, FluidVariant fluid, HolderLookup.Provider registries) {
        CompoundTag savedTag = new CompoundTag();
        savedTag.put("fk", fluid.toNbt(registries));
        tag.put(key, savedTag);
    }

    public static <T> void putList(CompoundTag tag, String key, List<T> list, Function<T, CompoundTag> encoder) {
        ListTag listTag = new ListTag();
        for (T t : list) {
            listTag.add(encoder.apply(t));
        }
        tag.put(key, listTag);
    }

    public static <T> void getList(CompoundTag tag, String key, List<T> list, Function<CompoundTag, T> decoder) {
        list.clear();
        ListTag listTag = tag.getList(key, Tag.TAG_COMPOUND);
        for (int i = 0; i < listTag.size(); ++i) {
            CompoundTag elementTag = listTag.getCompound(i);
            list.add(decoder.apply(elementTag));
        }
    }

    public static FluidVariant getFluidCompatible(@Nullable CompoundTag tag, String key, HolderLookup.Provider registries) {
        if (tag == null || !tag.contains(key))
            return FluidVariant.blank();

        if (tag.get(key) instanceof StringTag) {
            return FluidVariant.of(BuiltInRegistries.FLUID.get(ResourceLocation.parse(tag.getString(key))));
        } else {
            CompoundTag compound = tag.getCompound(key);
            if (compound.contains("fk")) {
                return FluidVariant.fromNbt(compound.getCompound("fk"), registries);
            } else {
                return FluidVariant.of(readLbaTag(tag.getCompound(key)));
            }
        }
    }

    private static Fluid readLbaTag(CompoundTag tag) {
        if (tag.contains("ObjName") && tag.getString("Registry").equals("f")) {
            return BuiltInRegistries.FLUID.get(ResourceLocation.parse(tag.getString("ObjName")));
        } else {
            return Fluids.EMPTY;
        }
    }

}
