package aztech.milite.compat.kubejs.machine;

import aztech.milite.machines.models.MachineCasings;
import aztech.milite.machines.multiblocks.HatchFlags;
import aztech.milite.machines.multiblocks.HatchTypes;
import aztech.milite.machines.multiblocks.ShapeTemplate;
import aztech.milite.machines.multiblocks.SimpleMember;
import net.minecraft.resources.ResourceLocation;

public interface ShapeTemplateHelper {
    default ShapeTemplate.LayeredBuilder layeredShape(String hatchCasing, String[][] layers) {
        return new ShapeTemplate.LayeredBuilder(MachineCasings.get(hatchCasing), layers);
    }

    default ShapeTemplate.Builder startShape(String hatchCasing) {
        return new ShapeTemplate.Builder(MachineCasings.get(hatchCasing));
    }

    default SimpleMember memberOfBlock(String blockId) {
        return SimpleMember.forBlockId(ResourceLocation.parse(blockId));
    }

    default SimpleMember memberOfBlockTag(String blockId, String blockTag) {
        return SimpleMember.forBlockTagId(ResourceLocation.parse(blockId), ResourceLocation.parse(blockTag));
    }

    default HatchFlags noHatch() {
        return HatchFlags.NO_HATCH;
    }

    default HatchFlags hatchOf(String... hatches) {
        var builder = new HatchFlags.Builder();
        for (String hatch : hatches) {
            builder.with(HatchTypes.get(hatch));
        }
        return builder.build();
    }
}
