package aztech.milite.client.machines.multiblocks;

import aztech.milite.MI;
import aztech.milite.client.util.RenderHelper;
import aztech.milite.machines.multiblocks.HatchFlags;
import aztech.milite.machines.multiblocks.MultiblockMemberTooltipData;
import aztech.milite.machines.multiblocks.SimpleMember;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexSorting;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.common.NeoForge;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultiblockHighlight {
    private static final Map<BlockPos, Highlight> highlightQueue = new HashMap<>();
    private static final MultiBufferSource.BufferSource immediate = MultiBufferSource.immediate(new ByteBufferBuilder(128));
    private static final VertexSorting reverseDistanceToOrigin = RenderHelper.reverseVertexSorting(VertexSorting.DISTANCE_TO_ORIGIN);
    @Nullable
    private static HighlightTargetPos highlightTargetPos;

    public static void init(IEventBus bus) {
        NeoForge.EVENT_BUS.addListener(MultiblockHighlight::end);
        bus.addListener((RegisterGuiLayersEvent event) -> event.registerAbove(VanillaGuiLayers.HOTBAR, MI.id("multiblock_highlight_tooltip"),
                MultiblockHighlight::renderTooltipOverlay));
    }

    public static void enqueueErrorHighlight(BlockPos pos, @Nullable BlockState state, @Nullable BlockEntity blockEntity) {
        highlightQueue.put(pos.immutable(), new ErrorHighlight(state != null ? Pair.of(state, blockEntity) : null));
    }

    public static void enqueueHatchHighlight(BlockPos pos) {
        highlightQueue.put(pos.immutable(), new HatchHighlight());
    }

    /**
     * Marks a position that is blocked by a block which does not belong to the shape.
     */
    public static void enqueueBlockedHighlight(BlockPos pos) {
        highlightQueue.put(pos.immutable(), new BlockedHighlight());
    }

    /**
     * Marks a position that is blocked by a block which does not belong to the shape, without the red cube.
     */
    public static void enqueueOutlineHighlight(BlockPos pos) {
        highlightQueue.put(pos.immutable(), new OutlineHighlight());
    }

    public static BlockPos enqueueHighlightTargetPos(BlockPos pos, SimpleMember member, @Nullable HatchFlags hatchFlags) {
        if (highlightTargetPos != null) {
            // 取最近的
            var player = Minecraft.getInstance().player;
            var eyePos = player.getEyePosition();
            double currentDistance = highlightTargetPos.pos().distToCenterSqr(eyePos);
            double newDistance = pos.distToCenterSqr(eyePos);
            if (newDistance > currentDistance) {
                return highlightTargetPos.pos();
            }
        }
        highlightTargetPos = new HighlightTargetPos(pos, member, hatchFlags);
        return pos;
    }

    private static void end(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_LEVEL) {
            return;
        }
        if (!highlightQueue.isEmpty()) {
            RenderSystem.clear(256, Minecraft.ON_OSX);
            var poseStack = event.getPoseStack();
            poseStack.pushPose();
            poseStack.mulPose(event.getModelViewMatrix());
            if (highlightTargetPos != null) {
                var pos = highlightTargetPos.pos();
                var entry = highlightQueue.get(pos);
                if (entry != null) {
                    poseStack.pushPose();
                    prepareForPos(poseStack, pos);
                    entry.prepare(poseStack);
                    LevelRenderer.renderLineBox(poseStack, immediate.getBuffer(RenderType.LINES), 0, 0, 0, 1, 1, 1, 1, 1, 1, 1);
                    poseStack.popPose();
                }
            }
            for (var entry : highlightQueue.entrySet()) {
                poseStack.pushPose();
                prepareForPos(poseStack, entry.getKey());
                entry.getValue().prepare(poseStack);
                entry.getValue().render(poseStack);
                poseStack.popPose();
            }
            poseStack.popPose();
            RenderSystem.backupProjectionMatrix();
            RenderSystem.setProjectionMatrix(RenderSystem.getProjectionMatrix(), reverseDistanceToOrigin);
            immediate.endBatch();
            RenderSystem.restoreProjectionMatrix();
            highlightQueue.clear();
        }
    }

    private static void prepareForPos(PoseStack poseStack, BlockPos pos) {
        var cameraPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        double x = pos.getX() - cameraPos.x;
        double y = pos.getY() - cameraPos.y;
        double z = pos.getZ() - cameraPos.z;
        poseStack.translate(x, y, z);
    }

    private static void renderTooltipOverlay(GuiGraphics graphics, DeltaTracker delta) {
        if (Minecraft.getInstance().options.hideGui) {
            highlightTargetPos = null;
            return;
        }
        if (highlightTargetPos != null) {
            var image = new MultiblockMemberTooltipData(
                    Minecraft.getInstance().level.registryAccess(),
                    highlightTargetPos.member(),
                    highlightTargetPos.hatchFlags());
            graphics.renderComponentTooltipFromElements(Minecraft.getInstance().font, List.of(Either.right(image)),
                    graphics.guiWidth() / 2, graphics.guiHeight() / 2, ItemStack.EMPTY);
            highlightTargetPos = null;
        }
    }

    private interface Highlight {
        void prepare(PoseStack poseStack);

        void render(PoseStack poseStack);
    }

    private record HighlightTargetPos(BlockPos pos, SimpleMember member, @Nullable HatchFlags hatchFlags) {
    }

    private record ErrorHighlight(@Nullable Pair<BlockState, @Nullable BlockEntity> value) implements Highlight {
        @Override
        public void prepare(PoseStack poseStack) {
            poseStack.translate(0.25, 0.25, 0.25);
            poseStack.scale(0.5f, 0.5f, 0.5f);
        }

        @Override
        public void render(PoseStack poseStack) {
            if (value == null) {
                RenderHelper.drawCube(poseStack, immediate, 1, 50f / 256, 50f / 256, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
            } else {
                BlockState state = value.getFirst();
                BlockEntity blockEntity = value.getSecond();
                var berDispatcher = Minecraft.getInstance().getBlockEntityRenderDispatcher();
                if (blockEntity != null && berDispatcher.getRenderer(blockEntity) != null) {
                    berDispatcher.render(blockEntity, 0, poseStack, immediate);
                } else {
                    ModelData modelData = blockEntity == null ? ModelData.EMPTY : blockEntity.getModelData();
                    Minecraft.getInstance().getBlockRenderer().renderSingleBlock(state, poseStack, immediate, 15728880, OverlayTexture.NO_OVERLAY, modelData, null);
                }
            }
        }
    }

    private record HatchHighlight() implements Highlight {
        @Override
        public void prepare(PoseStack poseStack) {
            poseStack.translate(-0.005, -0.005, -0.005);
            poseStack.scale(1.01f, 1.01f, 1.01f);
        }

        @Override
        public void render(PoseStack poseStack) {
            RenderHelper.drawOverlay(poseStack, immediate, OverlayTexture.NO_OVERLAY);
        }
    }

    /**
     * The red cube for a position blocked by a foreign block.
     */
    private record BlockedHighlight() implements Highlight {
        @Override
        public void prepare(PoseStack poseStack) {
            poseStack.translate(0.25, 0.25, 0.25);
            poseStack.scale(0.5f, 0.5f, 0.5f);
        }

        @Override
        public void render(PoseStack poseStack) {
            RenderHelper.drawCube(poseStack, immediate, 1, 50f / 256, 50f / 256, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
        }
    }

    /**
     * The red outline of a blocked position. Drawn without holding a wrench, as a permanent hint.
     */
    private record OutlineHighlight() implements Highlight {
        @Override
        public void prepare(PoseStack poseStack) {
        }

        @Override
        public void render(PoseStack poseStack) {
            RenderHelper.drawBlockOutline(poseStack, immediate, 1f, 0.15f, 0.15f, 1f);
        }
    }
}
