package aztech.milite.network;

import aztech.milite.MI;
import aztech.milite.network.armor.ActivateItemPacket;
import aztech.milite.network.armor.UpdateKeysPacket;
import aztech.milite.network.machines.AdjustSlotCapacityPacket;
import aztech.milite.network.machines.BuildMultiblockPacket;
import aztech.milite.network.machines.ChangeShapePacket;
import aztech.milite.network.machines.CoilGlowPacket;
import aztech.milite.network.machines.DoSlotDraggingPacket;
import aztech.milite.network.machines.LockAllPacket;
import aztech.milite.network.machines.MachineComponentSyncPacket;
import aztech.milite.network.machines.MoveRecipePacket;
import aztech.milite.network.machines.ReiLockSlotsPacket;
import aztech.milite.network.machines.SetAutoExtractPacket;
import aztech.milite.network.machines.SetLockingModePacket;
import aztech.milite.network.machines.SetRedstoneControlPacket;
import aztech.milite.network.machines.UpdateFluidSlotPacket;
import aztech.milite.network.machines.UpdateItemSlotPacket;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MIPackets {
    static final Map<Class<? extends BasePacket>, CustomPacketPayload.Type<?>> packetTypes = new HashMap<>();
    private static final List<Registration<?>> registrations = new ArrayList<>();

    private record Registration<P extends BasePacket>(CustomPacketPayload.Type<P> packetType, Class<P> clazz,
            StreamCodec<? super RegistryFriendlyByteBuf, P> packetCodec) {}

    private static <P extends BasePacket> void register(String path, Class<P> clazz,
            StreamCodec<? super RegistryFriendlyByteBuf, P> packetConstructor) {
        var type = new CustomPacketPayload.Type<P>(MI.id(path));
        packetTypes.put(clazz, type);
        registrations.add(new Registration<>(type, clazz, packetConstructor));
    }

    static {
        // Armor
        register("activate_chest", ActivateItemPacket.class, ActivateItemPacket.STREAM_CODEC);
        register("update_keys", UpdateKeysPacket.class, UpdateKeysPacket.STREAM_CODEC);
        // Configurable inventory
        register("adjust_slot_capacity", AdjustSlotCapacityPacket.class, AdjustSlotCapacityPacket.STREAM_CODEC);
        register("do_slot_dragging", DoSlotDraggingPacket.class, DoSlotDraggingPacket.STREAM_CODEC);
        register("lock_all", LockAllPacket.class, LockAllPacket.STREAM_CODEC);
        register("machine_component_sync", MachineComponentSyncPacket.class, MachineComponentSyncPacket.STREAM_CODEC);
        register("set_locking_mode", SetLockingModePacket.class, SetLockingModePacket.STREAM_CODEC);
        register("update_fluid_slot", UpdateFluidSlotPacket.class, UpdateFluidSlotPacket.STREAM_CODEC);
        register("update_item_slot", UpdateItemSlotPacket.class, UpdateItemSlotPacket.STREAM_CODEC);
        // Machine
        register("build_multiblock", BuildMultiblockPacket.class, BuildMultiblockPacket.STREAM_CODEC);
        register("change_shape", ChangeShapePacket.class, ChangeShapePacket.STREAM_CODEC);
        register("coil_glow", CoilGlowPacket.class, CoilGlowPacket.STREAM_CODEC);
        register("move_recipe", MoveRecipePacket.class, MoveRecipePacket.STREAM_CODEC);
        register("rei_lock_slots", ReiLockSlotsPacket.class, ReiLockSlotsPacket.STREAM_CODEC);
        register("set_auto_extract", SetAutoExtractPacket.class, SetAutoExtractPacket.STREAM_CODEC);
        register("set_redstone_control", SetRedstoneControlPacket.class, SetRedstoneControlPacket.STREAM_CODEC);
    }

    public static void init(RegisterPayloadHandlersEvent event) {
        var registrar = event.registrar("1");

        for (var reg : registrations) {
            register(registrar, reg);
        }
    }

    private static <P extends BasePacket> void register(PayloadRegistrar registrar, Registration<P> reg) {
        registrar.playBidirectional(reg.packetType, reg.packetCodec, (packet, context) -> {
            packet.handle(new BasePacket.Context(reg.clazz, context));
        });
    }
}
