package aztech.milite.blocks.storage.tank;

import aztech.milite.blocks.storage.AbstractStorageBlockEntity;
import aztech.milite.blocks.storage.ResourceStorage;
import aztech.milite.MIComponents;
import aztech.milite.thirdparty.fabrictransfer.api.fluid.FluidVariant;
import aztech.milite.util.NbtHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class AbstractTankBlockEntity extends AbstractStorageBlockEntity<FluidVariant> {
    public AbstractTankBlockEntity(BlockEntityType<?> bet,
            BlockPos pos,
            BlockState state) {
        super(bet, pos, state);
    }

    @Override
    public DataComponentType<ResourceStorage<FluidVariant>> componentType() {
        return MIComponents.FLUID_STORAGE.get();
    }

    @Override
    public void removeComponentsFromTag(CompoundTag tag) {
        super.removeComponentsFromTag(tag);
        tag.remove("fluid");
    }

    @Override
    public FluidVariant loadResource(CompoundTag tag, HolderLookup.Provider registries) {
        return NbtHelper.getFluidCompatible(tag, "fluid", registries);
    }

    @Override
    public void saveResource(FluidVariant resource, CompoundTag tag, HolderLookup.Provider registries) {
        NbtHelper.putFluid(tag, "fluid", getResource(), registries);
    }

    @Override
    public FluidVariant getBlankResource() {
        return FluidVariant.blank();
    }

    public abstract boolean onPlayerUse(Player player);
}
