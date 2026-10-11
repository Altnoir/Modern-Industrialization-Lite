package aztech.milite.machines;

import aztech.milite.blocks.FastBlockEntity;
import aztech.milite.blocks.WrenchableBlockEntity;
import aztech.milite.inventory.AbstractConfigurableStack;
import aztech.milite.inventory.MIInventory;
import aztech.milite.machines.components.DropableComponent;
import aztech.milite.machines.components.OrientationComponent;
import aztech.milite.machines.components.PlacedByComponent;
import aztech.milite.machines.gui.GuiComponentServer;
import aztech.milite.machines.gui.MachineGuiParameters;
import aztech.milite.machines.gui.MachineMenuServer;
import aztech.milite.machines.models.MachineModelClientData;
import aztech.milite.MICapabilities;
import aztech.milite.util.NbtHelper;
import aztech.milite.util.WorldHelper;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.Util;
import net.minecraft.world.Clearable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * The base block entity for the machine system. Contains components, and an
 * inventory.
 */
@SuppressWarnings("rawtypes")
public abstract class MachineBlockEntity extends FastBlockEntity
        implements MenuProvider, WrenchableBlockEntity, Clearable {
    public final ComponentStorage.GuiServer guiComponents = new ComponentStorage.GuiServer();
    public final ComponentStorage.Server components = new ComponentStorage.Server();
    public final MachineGuiParameters guiParams;
    /**
     * Server-side only: true if the next call to sync() will trigger a remesh.
     */
    private boolean syncCausesRemesh = true;
    /**
     * Caches the current redstone status. Invalidated by {@link MachineBlock}.
     * {@code null} if the current status is not known.
     */
    @Nullable
    private Boolean hasRedstoneHighSignal = null;

    public final OrientationComponent orientation;
    public final PlacedByComponent placedBy;

    public long lastSoundTick;

    public MachineBlockEntity(BEP bep, MachineGuiParameters guiParams, OrientationComponent.Params orientationParams) {
        super(bep.type(), bep.pos(), bep.state());
        this.guiParams = guiParams;
        this.orientation = new OrientationComponent(orientationParams, this);
        this.placedBy = new PlacedByComponent();

        registerComponents(orientation, placedBy);
    }

    protected final void registerGuiComponent(GuiComponentServer... components) {
        guiComponents.register(components);
    }

    protected final void registerComponents(MachineComponent... components) {
        this.components.register(components);
    }

    /**
     * @return The inventory that will be synced with the client.
     */
    public abstract MIInventory getInventory();

    @Override
    public final Component getDisplayName() {
        return Component.translatable(Util.makeDescriptionId("block", guiParams.blockId));
    }

    @Override
    public final AbstractContainerMenu createMenu(int syncId, Inventory inv, Player player) {
        return new MachineMenuServer(syncId, inv, this, guiParams);
    }

    public final void writeScreenOpeningData(RegistryFriendlyByteBuf buf) {
        MIInventory inv = getInventory();
        CompoundTag tag = new CompoundTag();
        NbtHelper.putList(tag, "items", inv.getItemStacks(), configurableItemStack -> configurableItemStack.toNbt(buf.registryAccess()));
        NbtHelper.putList(tag, "fluids", inv.getFluidStacks(), configurableFluidStack -> configurableFluidStack.toNbt(buf.registryAccess()));
        buf.writeNbt(tag);
        inv.itemPositions.write(buf);
        inv.fluidPositions.write(buf);
        buf.writeInt(guiComponents.size());
        for (GuiComponentServer<?, ?> component : guiComponents) {
            writeInitialGuiComponent(buf, component);
        }
        guiParams.write(buf);
    }

    private static <P, D> void writeInitialGuiComponent(RegistryFriendlyByteBuf buf, GuiComponentServer<P, D> component) {
        var type = component.getType();
        buf.writeResourceLocation(type.id());
        type.paramsCodec().encode(buf, component.params());
        type.dataCodec().encode(buf, component.extractData());
    }

    /**
     * @param face The face that was targeted, taking the overlay into account.
     */
    protected ItemInteractionResult useItemOn(Player player, InteractionHand hand, Direction face) {
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    public void openMenu(ServerPlayer player) {
        player.openMenu(this, this::writeScreenOpeningData);
    }

    public abstract MachineModelClientData getMachineModelData();

    @MustBeInvokedByOverriders
    public void onPlaced(@Nullable LivingEntity placer, ItemStack itemStack) {
        orientation.onPlaced(placer, itemStack);
        placedBy.onPlaced(placer);
    }

    @Override
    public boolean useWrench(Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (orientation.useWrench(player, hand, MachineOverlay.findHitSide(hitResult))) {
            getLevel().blockUpdated(getBlockPos(), Blocks.AIR);
            setChanged();
            if (!getLevel().isClientSide()) {
                sync();
            }
            return true;
        }
        return false;
    }

    @Override
    public final ModelData getModelData() {
        return ModelData.builder()
                .with(MachineModelClientData.KEY, getMachineModelData())
                .build();
    }

    @Override
    public void sync() {
        sync(true);
    }

    public void sync(boolean forceRemesh) {
        syncCausesRemesh = syncCausesRemesh || forceRemesh;
        super.sync();
    }

    @Override
    public void clearContent() {
        this.getInventory().getItemStacks().forEach(AbstractConfigurableStack::empty);
        this.getInventory().getFluidStacks().forEach(AbstractConfigurableStack::empty);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("remesh", syncCausesRemesh);
        syncCausesRemesh = false;
        for (MachineComponent component : components) {
            component.writeClientNbt(tag, registries);
        }
        return tag;
    }

    @Override
    public final void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        for (MachineComponent component : components) {
            component.writeNbt(tag, registries);
        }
    }

    @Override
    public final void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        load(tag, registries, false);
    }

    public final void load(CompoundTag tag, HolderLookup.Provider registries, boolean isUpgradingMachine) {
        if (!tag.contains("remesh")) {
            for (MachineComponent component : components) {
                component.readNbt(tag, registries, isUpgradingMachine);
            }
        } else {
            boolean forceChunkRemesh = tag.getBoolean("remesh");
            for (MachineComponent component : components) {
                component.readClientNbt(tag, registries);
            }
            if (forceChunkRemesh) {
                WorldHelper.forceChunkRemesh(level, worldPosition);
                requestModelDataUpdate();
            }
        }
    }

    @Override
    protected final boolean shouldSkipComparatorUpdate() {
        return !hasComparatorOutput();
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public static void registerItemApi(BlockEntityType<?> bet) {
        MICapabilities.onEvent(event -> {
            event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, bet,
                    (be, direction) -> ((MachineBlockEntity) be).getInventory().itemStorage.itemHandler);
        });
    }

    public static void registerFluidApi(BlockEntityType<?> bet) {
        MICapabilities.onEvent(event -> {
            event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, bet,
                    (be, direction) -> ((MachineBlockEntity) be).getInventory().fluidStorage.fluidHandler);
        });
    }

    public List<ItemStack> dropExtra() {
        List<ItemStack> drops = new ArrayList<>();
        components.forType(DropableComponent.class, u -> drops.add(u.getDrop()));
        return drops;
    }

    public List<Component> getTooltips() {
        return List.of();
    }

    protected boolean hasComparatorOutput() {
        return false;
    }

    protected int getComparatorOutput() {
        return 0;
    }

    public boolean hasRedstoneHighSignal() {
        if (this.hasRedstoneHighSignal == null) {
            refreshRedstoneStatus();
        }
        return this.hasRedstoneHighSignal;
    }

    void refreshRedstoneStatus() {
        this.hasRedstoneHighSignal = level.hasNeighborSignal(worldPosition);
    }
}
