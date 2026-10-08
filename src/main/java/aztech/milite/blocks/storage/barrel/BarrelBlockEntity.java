package aztech.milite.blocks.storage.barrel;

import aztech.milite.blocks.storage.AbstractStorageBlockEntity;
import aztech.milite.blocks.storage.ResourceStorage;
import aztech.milite.MIComponents;
import aztech.milite.thirdparty.fabrictransfer.api.item.ItemVariant;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class BarrelBlockEntity extends AbstractStorageBlockEntity<ItemVariant> {
    public BarrelBlockEntity(BlockEntityType type,
            BlockPos pos,
            BlockState state) {
        super(type, pos, state);
    }

    @Override
    public DataComponentType<ResourceStorage<ItemVariant>> componentType() {
        return MIComponents.ITEM_STORAGE.get();
    }

    @Override
    public ItemVariant getBlankResource() {
        return ItemVariant.blank();
    }

    @Override
    public void removeComponentsFromTag(CompoundTag tag) {
        super.removeComponentsFromTag(tag);
        tag.remove("item");
    }

    @Override
    public ItemVariant loadResource(CompoundTag tag, HolderLookup.Provider registries) {
        return ItemVariant.fromNbt(tag.getCompound("item"), registries);
    }

    @Override
    public void saveResource(ItemVariant resource, CompoundTag tag, HolderLookup.Provider registries) {
        tag.put("item", resource.toNbt(registries));
    }
}
