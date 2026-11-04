package aztech.milite.machines.multiblocks;

import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.Tags;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.List;
import java.util.Objects;
import java.util.stream.StreamSupport;

/**
 * The representation of a simple logic-less member that is part of a shape,
 * e.g. a casing.
 */
public interface SimpleMember {
    boolean matchesState(BlockState state, @Nullable BlockEntity blockEntity);

    @Nullable
    default BlockEntity newBlockEntity(RegistryAccess registries, @Nullable Level level, BlockPos pos, BlockState state) {
        return null;
    }

    BlockState getPreviewState();

    default Ingredient getItemPreviewState(RegistryAccess registries) {
        return Ingredient.of(this.getPreviewState().getBlock().asItem());
    }

    default List<Component> getTooltipPreview(RegistryAccess registries) {
        return List.of(this.getPreviewState().getBlock().getName());
    }

    static SimpleMember forBlock(Supplier<? extends Block> block) {
        Objects.requireNonNull(block);

        return new SimpleMember() {
            @Override
            public boolean matchesState(BlockState state, @Nullable BlockEntity blockEntity) {
                return state.is(block.get());
            }

            @Override
            public BlockState getPreviewState() {
                return block.get().defaultBlockState();
            }
        };
    }

    static SimpleMember forBlockTag(Supplier<? extends Block> block, TagKey<Block> tag) {
        Objects.requireNonNull(block);
        Objects.requireNonNull(tag);

        return new SimpleMember() {
            @Override
            public boolean matchesState(BlockState state, @Nullable BlockEntity blockEntity) {
                return state.is(tag);
            }

            @Override
            public BlockState getPreviewState() {
                return block.get().defaultBlockState();
            }

            @Override
            public Ingredient getItemPreviewState(RegistryAccess registries) {
                var blocks = BuiltInRegistries.BLOCK.getTagOrEmpty(tag);
                return Ingredient.of(StreamSupport.stream(blocks.spliterator(), false)
                        .map(Holder::value)
                        .map(Block::asItem)
                        .filter((item) -> item != Items.AIR)
                        .map(ItemStack::new));
            }

            @Override
            public List<Component> getTooltipPreview(RegistryAccess registries) {
                List<Component> lines = new ArrayList<>();
                var translation = Tags.getTagTranslationKey(tag);
                var tagText = Component.literal("#" + tag.location());
                if (I18n.exists(translation)) {
                    lines.add(Component.translatable(translation));
                    lines.add(tagText.withStyle(ChatFormatting.DARK_GRAY));
                } else {
                    lines.add(tagText);
                }
                return lines;
            }
        };
    }

    static SimpleMember forBlockId(ResourceLocation id) {
        return forBlock(() -> BuiltInRegistries.BLOCK.get(id));
    }

    static SimpleMember forBlockTagId(ResourceLocation id, ResourceLocation tagId) {
        Objects.requireNonNull(id);
        Objects.requireNonNull(tagId);

        var tag = TagKey.create(Registries.BLOCK, tagId);
        return forBlockTag(() -> BuiltInRegistries.BLOCK.get(id), tag);
    }

    static SimpleMember forBlockState(BlockState state) {
        Objects.requireNonNull(state);

        return new SimpleMember() {
            @Override
            public boolean matchesState(BlockState state2, @Nullable BlockEntity blockEntity) {
                return state == state2;
            }

            @Override
            public BlockState getPreviewState() {
                return state;
            }
        };
    }

    static SimpleMember forBlockStateId(String blockId, Function<BlockState, BlockState> modifier) {
        Objects.requireNonNull(blockId);
        Objects.requireNonNull(modifier);

        return new SimpleMember() {
            @Override
            public boolean matchesState(BlockState state2, @Nullable BlockEntity blockEntity) {
                return getPreviewState() == state2;
            }

            @Override
            public BlockState getPreviewState() {
                var block = BuiltInRegistries.BLOCK.get(ResourceLocation.parse(blockId));
                return modifier.apply(block.defaultBlockState());
            }
        };
    }

    static SimpleMember verticalChain() {
        return new SimpleMember() {
            @Override
            public boolean matchesState(BlockState state, @Nullable BlockEntity blockEntity) {
                return state.is(Blocks.CHAIN) && state.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y;
            }

            @Override
            public BlockState getPreviewState() {
                return Blocks.CHAIN.defaultBlockState().setValue(RotatedPillarBlock.AXIS, Direction.Axis.Y);
            }
        };
    }
}
