/*
 * MIT License
 *
 * Copyright (c) 2026 Altnoir Null々
 * Copyright (c) 2020 Azercoco & Technici4n
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package aztech.milite;

import aztech.milite.blocks.storage.barrel.BarrelBlockEntity;
import aztech.milite.blocks.storage.tank.AbstractTankBlockEntity;
import aztech.milite.machines.gui.MachineMenuCommon;
import aztech.milite.machines.init.MachineLevelValidator;
import aztech.milite.thirdparty.fabrictransfer.api.fluid.FluidVariant;
import aztech.milite.thirdparty.fabrictransfer.api.fluid.FluidVariantAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import org.jspecify.annotations.Nullable;

/**
 * Abstractions over client-only code called in common code.
 */
public class MICommonProxy {
    public static MICommonProxy INSTANCE = instantiateProxy();

    private static MICommonProxy instantiateProxy() {
        if (FMLEnvironment.dist.isClient()) {
            try {
                Class<?> clientProxy = Class.forName("aztech.milite.client.MIClientProxy");
                return (MICommonProxy) clientProxy.getConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException("Failed to create Modern Industrialization ClientProxy.", e);
            }
        } else {
            return new MICommonProxy();
        }
    }

    @Nullable
    private static MinecraftServer currentServer = null; // note: will be null in multiplayer client

    public static void initEvents() {
        NeoForge.EVENT_BUS.addListener(ServerAboutToStartEvent.class, e -> currentServer = e.getServer());
        // Recipes are loaded by the time the server has started, so this is the first point where they can be checked.
        NeoForge.EVENT_BUS.addListener(ServerStartedEvent.class, e -> MachineLevelValidator.validate(e.getServer()));
        NeoForge.EVENT_BUS.addListener(ServerStoppedEvent.class, e -> currentServer = null);
    }

    public Player getClientPlayer() {
        throw new UnsupportedOperationException("Client player is not available on the server!");
    }

    /**
     * Try to find a suitable user.
     */
    @Nullable
    public Player findUser(ItemStack mainHand) {
        if (currentServer != null) {
            for (var player : currentServer.getPlayerList().getPlayers()) {
                if (player.getMainHandItem() == mainHand) {
                    return player;
                }
            }
        }
        return null;
    }

    public boolean shouldSteamDrillForceBreakReset() {
        return false;
    }

    public void delayNextBlockAttack(Player player) {}

    public boolean hasShiftDown() {
        return false;
    }

    // In case there is ever a client-side specific version of this...
    public List<Component> getFluidTooltip(FluidVariant variant) {
        List<Component> list = new ArrayList<>();
        list.add(FluidVariantAttributes.getName(variant));
        return list;
    }

    public void registerPartTankClient(Supplier<? extends BlockEntityType<? extends AbstractTankBlockEntity>> blockEntityType, int meanRgb) {}

    public void registerPartBarrelClient(Supplier<BlockEntityType<BarrelBlockEntity>> blockEntityType, int meanRgb) {}

    public MachineMenuCommon createClientMachineMenu(int syncId, Inventory playerInventory, RegistryFriendlyByteBuf buf) {
        throw new UnsupportedOperationException("Only supported on the server");
    }

    public BlockState getMachineCasingBlockState(BlockState state, BlockAndTintGetter renderView, BlockPos pos) {
        return state;
    }
}
