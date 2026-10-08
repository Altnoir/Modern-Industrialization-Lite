package aztech.milite.machines.guicomponents;

import aztech.milite.inventory.HackySlot;
import aztech.milite.inventory.SlotGroup;
import aztech.milite.machines.components.OverdriveComponent;
import aztech.milite.machines.components.RedstoneControlComponent;
import aztech.milite.machines.gui.GuiComponentServer;
import aztech.milite.machines.gui.MachineGuiParameters;
import aztech.milite.machines.MachineBlockEntity;
import aztech.milite.MI;
import aztech.milite.MIItem;
import aztech.milite.MIText;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Unit;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

import java.util.ArrayList;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.List;

public class SlotPanel implements GuiComponentServer<List<SlotPanel.SlotType>, Unit> {
    public static final Type<List<SlotPanel.SlotType>, Unit> TYPE = new Type<>(MI.id("slot_panel"),
            NeoForgeStreamCodecs.enumCodec(SlotPanel.SlotType.class).apply(ByteBufCodecs.list()), StreamCodec.unit(Unit.INSTANCE));

    private final MachineBlockEntity machine;
    private final List<Consumer<MenuFacade>> slotFactories = new ArrayList<>();
    private final List<SlotType> slotTypes = new ArrayList<>();

    public SlotPanel(MachineBlockEntity machine) {
        this.machine = machine;
    }

    public SlotPanel withRedstoneControl(RedstoneControlComponent redstoneControlComponent) {
        return addSlot(SlotType.REDSTONE_MODULE, () -> redstoneControlComponent.getDrop().copy(), redstoneControlComponent::setStackServer);
    }

    public SlotPanel withOverdrive(OverdriveComponent overdriveComponent) {
        return addSlot(SlotType.OVERDRIVE_MODULE, () -> overdriveComponent.getDrop().copy(), overdriveComponent::setStackServer);
    }

    private SlotPanel addSlot(SlotType type, Supplier<ItemStack> getStack, BiConsumer<MachineBlockEntity, ItemStack> setStack) {
        int slotIndex = slotTypes.size();
        slotFactories.add(facade -> facade.addSlotToMenu(new HackySlot(getSlotX(machine.guiParams), getSlotY(slotIndex)) {
            @Override
            protected ItemStack getRealStack() {
                return getStack.get();
            }

            @Override
            protected void setRealStack(ItemStack stack) {
                setStack.accept(machine, stack);
            }

            @Override
            public boolean mayPlace(ItemStack stack) {
                return type.mayPlace(stack);
            }

            @Override
            public int getMaxStackSize() {
                return type.slotLimit;
            }
        }, type.group));
        slotTypes.add(type);
        return this;
    }

    @Override
    public List<SlotType> params() {
        return slotTypes;
    }

    @Override
    public Unit extractData() {
        return Unit.INSTANCE;
    }

    @Override
    public Type<List<SlotType>, Unit> getType() {
        return TYPE;
    }

    @Override
    public void setupMenu(MenuFacade menu) {
        for (var factory : slotFactories) {
            factory.accept(menu);
        }
    }

    public static int getSlotY(int slotIndex) {
        return 19 + slotIndex * 20;
    }

    public static int getSlotX(MachineGuiParameters guiParameters) {
        return guiParameters.backgroundWidth + 6;
    }

    public enum SlotType {
        REDSTONE_MODULE(SlotGroup.REDSTONE_MODULE, 1, MIItem.REDSTONE_CONTROL_MODULE::is, 36, 80, MIText.AcceptsRedstoneControlModule),
        OVERDRIVE_MODULE(SlotGroup.OVERDRIVE_MODULE, 1, MIItem.OVERDRIVE_MODULE::is, 0, 98, MIText.AcceptsOverdriveModule);

        public final SlotGroup group;
        public final int slotLimit;
        public final Predicate<ItemStack> insertionChecker;
        public final int u, v;
        public final MIText tooltip;

        SlotType(SlotGroup group, int slotLimit, Predicate<ItemStack> insertionChecker, int u, int v, MIText tooltip) {
            this.group = group;
            this.slotLimit = slotLimit;
            this.insertionChecker = insertionChecker;
            this.u = u;
            this.v = v;
            this.tooltip = tooltip;
        }

        public boolean mayPlace(ItemStack stack) {
            return insertionChecker.test(stack);
        }
    }
}
