package aztech.milite.client;

import aztech.milite.blocks.storage.barrel.BarrelBlockEntity;
import aztech.milite.blocks.storage.tank.AbstractTankBlockEntity;
import aztech.milite.client.blocks.storage.barrel.BarrelRenderer;
import aztech.milite.client.blocks.storage.tank.TankRenderer;
import aztech.milite.client.machines.gui.MachineMenuClient;
import aztech.milite.client.machines.models.MachineBakedModel;
import aztech.milite.client.machines.models.UseBlockModelBakedModel;
import aztech.milite.client.textures.TextureHelper;
import aztech.milite.config.MIClientConfig;
import aztech.milite.items.SteamDrillHooks;
import aztech.milite.machines.gui.MachineMenuCommon;
import aztech.milite.machines.models.MachineModelClientData;
import aztech.milite.machines.multiblocks.HatchBlockEntity;
import aztech.milite.machines.multiblocks.MultiblockMachineBlockEntity;
import aztech.milite.MICommonProxy;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.Nullable;

import java.util.function.Supplier;
import java.util.Objects;

public class MIClientProxy extends MICommonProxy {
    @Override
    public Player getClientPlayer() {
        return Objects.requireNonNull(Minecraft.getInstance().player);
    }

    @Override
    public @Nullable Player findUser(ItemStack mainHand) {
        if (Minecraft.getInstance().isSameThread()) {
            for (var player : Minecraft.getInstance().level.players()) {
                if (player.getMainHandItem() == mainHand) {
                    return player;
                }
            }
            return null;
        }
        return super.findUser(mainHand);
    }

    @Override
    public boolean shouldSteamDrillForceBreakReset() {
        if (Minecraft.getInstance().isSameThread()) {
            if (Minecraft.getInstance().hitResult instanceof BlockHitResult bhr) {
                return bhr.getDirection() != SteamDrillHooks.breakingSide;
            }
        }
        return false;
    }

    @Override
    public void delayNextBlockAttack(Player player) {
        if (player == Minecraft.getInstance().player) {
            // Add a 5 tick delay like vanilla.
            Minecraft.getInstance().gameMode.destroyDelay = 5;
        }
    }

    @Override
    public boolean hasShiftDown() {
        return Screen.hasShiftDown();
    }

    @Override
    public void registerPartTankClient(Supplier<? extends BlockEntityType<? extends AbstractTankBlockEntity>> blockEntityType, int meanRgb) {
        MIClient.registerBlockEntityRenderer(blockEntityType, context -> new TankRenderer(TextureHelper.getOverlayTextColor(meanRgb)));
    }

    @Override
    public void registerPartBarrelClient(Supplier<BlockEntityType<BarrelBlockEntity>> blockEntityType, int meanRgb) {
        MIClient.registerBlockEntityRenderer(blockEntityType, context -> new BarrelRenderer(TextureHelper.getOverlayTextColor(meanRgb)));
    }

    @Override
    public MachineMenuCommon createClientMachineMenu(int syncId, Inventory playerInventory, RegistryFriendlyByteBuf buf) {
        return MachineMenuClient.create(syncId, playerInventory, buf);
    }

    @Override
    public BlockState getMachineCasingBlockState(BlockState state, BlockAndTintGetter renderView, BlockPos pos) {
        var be = renderView.getBlockEntity(pos); // Note: not safe to access fields!
        if (!MIClientConfig.INSTANCE.interMachineConnectedTextures.getAsBoolean()) {
            // Use the machine's own state, unless we are a hatch or a multiblock controller of course.
            if (!(be instanceof HatchBlockEntity) && !(be instanceof MultiblockMachineBlockEntity)) {
                return state;
            }
        }

        var modelData = renderView.getModelData(pos);
        var clientData = modelData.get(MachineModelClientData.KEY);
        if (clientData == null) {
            // Not a machine's data!
            return state;
        }
        var casing = clientData.casing;
        if (casing == null) {
            // No override, then pull the casing from the machine's baked model.
            var machineModel = Minecraft.getInstance().getBlockRenderer().getBlockModel(state);
            if (machineModel instanceof MachineBakedModel mbm) {
                casing = mbm.getBaseCasing();
            } else {
                // Couldn't find casing... :(
                return state;
            }
        }

        // Pull the block state from the casing model if possible
        var casingModel = MachineBakedModel.getCasingModel(casing);
        if (casingModel instanceof UseBlockModelBakedModel ubmbm) {
            return ubmbm.getTargetState();
        }
        // Couldn't find target state
        return state;
    }
}
