package aztech.milite.compat.kubejs.machine;

import aztech.milite.machines.blockentities.multiblocks.ElectricBlastFurnaceBlockEntity;
import aztech.milite.machines.init.MachineLevel;
import dev.latvian.mods.kubejs.event.KubeEvent;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

public class AddEbfTiersEventJS implements KubeEvent {
    private final Consumer<ElectricBlastFurnaceBlockEntity.Tier> tierConsumer;

    public AddEbfTiersEventJS(Consumer<ElectricBlastFurnaceBlockEntity.Tier> tierConsumer) {
        this.tierConsumer = tierConsumer;
    }

    /**
     * Adds one more electric blast furnace. The coil is what the furnace is built around, so a new furnace always
     * comes with its own coil and its own block.
     *
     * @param id          the block id of the new furnace
     * @param englishName its name, which is also the name of its recipe category
     * @param coilBlockId the coil its structure is built with
     * @param maxBaseFE   the energy per tick the coil lets the furnace draw
     * @param level       the machine level this coil grants, as one of the {@link MachineLevel} names such as
     *                    {@code "titanium"}
     */
    public void add(String id, String englishName, ResourceLocation coilBlockId, long maxBaseFE, String level) {
        tierConsumer.accept(new ElectricBlastFurnaceBlockEntity.Tier(id, englishName, coilBlockId, maxBaseFE,
                MachineLevel.byName(level)));
    }
}
