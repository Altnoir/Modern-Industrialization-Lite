package aztech.milite.guidebook;

import aztech.milite.machines.MachineBlock;
import aztech.milite.machines.multiblocks.MultiblockMachineBlockEntity;
import aztech.milite.machines.multiblocks.ShapeMatcher;
import aztech.milite.util.TextHelper;
import guideme.color.ConstantColor;
import guideme.color.SymbolicColor;
import guideme.compiler.PageCompiler;
import guideme.compiler.tags.MdxAttrs;
import guideme.document.interaction.TextTooltip;
import guideme.document.LytErrorSink;
import guideme.libs.mdast.mdx.model.MdxJsxElementFields;
import guideme.scene.annotation.InWorldBoxAnnotation;
import guideme.scene.element.SceneElementTagCompiler;
import guideme.scene.GuidebookScene;
import net.minecraft.world.phys.Vec3;

import java.util.Set;

public class MultiblockShapeCompiler implements SceneElementTagCompiler {
    @Override
    public Set<String> getTagNames() {
        return Set.of("MultiblockShape");
    }

    @Override
    public void compile(GuidebookScene scene, PageCompiler compiler, LytErrorSink errorSink, MdxJsxElementFields el) {
        var controller = MdxAttrs.getRequiredBlockAndId(compiler, errorSink, el, "controller");
        if (controller == null) {
            // Parsing error
            return;
        }
        if (!(controller.getRight() instanceof MachineBlock machineBlock)
                || !(machineBlock.getBlockEntityInstance() instanceof MultiblockMachineBlockEntity multi)) {
            errorSink.appendError(compiler, "Block is not a multiblock controller: " + controller.getLeft(), el);
            return;
        }
        var useBigShape = MdxAttrs.getBoolean(compiler, errorSink, el, "useBigShape", false);

        var shape = useBigShape ? multi.getBigShape() : multi.getActiveShape();
        var controllerPos = MdxAttrs.getPos(compiler, errorSink, el);

        // Controller
        scene.getLevel().setBlockAndUpdate(controllerPos, controller.getRight().defaultBlockState());
        // Shape blocks
        for (var entry : shape.simpleMembers.entrySet()) {
            var pos = ShapeMatcher.toWorldPos(controllerPos, multi.orientation.facingDirection, entry.getKey());
            var state = ShapeMatcher.toWorldState(scene.getLevel(), pos, entry.getValue().getPreviewState(), multi.orientation.facingDirection);
            scene.getLevel().setBlockAndUpdate(pos, state);

            // Annotations for allowed blocks and hatches
            var member = shape.simpleMembers.get(entry.getKey());
            var hatch = shape.hatchFlags.get(entry.getKey());
            boolean hasHatches = hatch != null && !hatch.values().isEmpty();
            var minCorner = Vec3.atLowerCornerOf(pos);
            var annotation = new InWorldBoxAnnotation(minCorner.toVector3f(), minCorner.add(1, 1, 1).toVector3f(), hasHatches ? SymbolicColor.GREEN : ConstantColor.TRANSPARENT);
            annotation.setTooltip(new TextTooltip(TextHelper.getMultiblockMemberTooltip(scene.getLevel().registryAccess(), member, hatch)));
            scene.addAnnotation(annotation);
        }
    }
}
