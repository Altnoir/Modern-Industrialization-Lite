package aztech.milite.definition;

import aztech.milite.datagen.loot.MIBlockLoot;
import aztech.milite.datagen.model.BaseModelProvider;
import aztech.milite.items.SortOrder;
import aztech.milite.MIItem;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.registries.DeferredBlock;
import org.jspecify.annotations.Nullable;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.List;

public class BlockDefinition<T extends Block> extends Definition implements ItemLike, Supplier<T> {
    private final DeferredBlock<T> block;
    public final ItemDefinition<BlockItem> blockItem;

    public final BiConsumer<Block, BaseModelProvider> modelGenerator;
    @Nullable
    public final MIBlockLoot blockLoot;
    public final List<TagKey<Block>> tags;

    @Nullable
    private BiConsumer<Block, Item> onBlockRegistrationEvent;

    public BlockDefinition(String englishName, DeferredBlock<T> block,
            BiFunction<? super T, Item.Properties, BlockItem> blockItemCtor,
            BiConsumer<Block, BaseModelProvider> modelGenerator,
            BiConsumer<Item, ItemModelProvider> itemModelGenerator,
            @Nullable MIBlockLoot blockLoot,
            List<TagKey<Block>> tags,
            SortOrder sortOrder) {
        super(englishName, block.getId().getPath(), false);
        this.block = block;
        this.blockItem = MIItem.item(
                englishName,
                path(),
                s -> blockItemCtor.apply(block.get(), s),
                itemModelGenerator,
                sortOrder);
        this.modelGenerator = modelGenerator;
        this.blockLoot = blockLoot;
        this.tags = tags;
    }

    public BlockDefinition<T> withBlockRegistrationEvent(BiConsumer<Block, Item> onBlockRegistrationEvent) {
        this.onBlockRegistrationEvent = onBlockRegistrationEvent;
        return this;
    }

    @Override
    public Item asItem() {
        return blockItem.asItem();
    }

    public T asBlock() {
        return block.get();
    }

    @Override
    public String getTranslationKey() {
        return blockItem.getTranslationKey();
    }

    public void onRegister() {
        if (onBlockRegistrationEvent != null) {
            onBlockRegistrationEvent.accept(block.get(), blockItem.asItem());
        }
    }

    @Override
    public T get() {
        return asBlock();
    }
}
