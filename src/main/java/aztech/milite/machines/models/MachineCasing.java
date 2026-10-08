package aztech.milite.machines.models;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.Util;
import net.minecraft.world.level.block.Block;
import org.jspecify.annotations.Nullable;

import java.util.function.Supplier;

public class MachineCasing {
    public final ResourceLocation key;
    /**
     * Not null when registered as an imitation. The actual model might not be an imitation since it is resource pack driven.
     * Mostly used to pull the name of the casing from the block it imitates. Will also generate a corresponding casing model.
     */
    @Nullable
    public final Supplier<? extends Block> imitatedBlock;

    MachineCasing(ResourceLocation key, @Nullable Supplier<? extends Block> imitatedBlock) {
        this.key = key;
        this.imitatedBlock = imitatedBlock;
    }

    public String getTranslationKey() {
        if (imitatedBlock != null) {
            throw new IllegalArgumentException("Cannot get translation key for casing imitating a block.");
        }
        return Util.makeDescriptionId("machine_casing", key);
    }

    public MutableComponent getName() {
        if (imitatedBlock != null) {
            return imitatedBlock.get().getName();
        }
        return Component.translatable(getTranslationKey());
    }
}
