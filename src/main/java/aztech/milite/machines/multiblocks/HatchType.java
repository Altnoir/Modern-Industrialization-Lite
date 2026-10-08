package aztech.milite.machines.multiblocks;

import com.mojang.datafixers.util.Either;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

public final class HatchType {
    private final ResourceLocation id;
    private final Either<Component, ResourceLocation> descriptionOrBlockId;

    HatchType(ResourceLocation id, Component description) {
        this.id = id;
        this.descriptionOrBlockId = Either.left(description);
    }

    HatchType(ResourceLocation id, ResourceLocation blockId) {
        this.id = id;
        this.descriptionOrBlockId = Either.right(blockId);
    }

    public ResourceLocation id() {
        return id;
    }

    public MutableComponent description() {
        return descriptionOrBlockId.map(Component::copy, id -> BuiltInRegistries.BLOCK.get(id).getName());
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof HatchType other && id.equals(other.id);
    }
}
