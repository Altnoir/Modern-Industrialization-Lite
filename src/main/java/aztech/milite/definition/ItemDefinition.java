package aztech.milite.definition;

import aztech.milite.items.SortOrder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.registries.DeferredItem;
import org.jspecify.annotations.Nullable;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ItemDefinition<T extends Item> extends Definition implements ItemLike {
    private final DeferredItem<T> item;
    public final SortOrder sortOrder;

    public final BiConsumer<Item, ItemModelProvider> modelGenerator;
    @Nullable
    private Consumer<? super T> onItemRegistrationEvent;

    public ItemDefinition(String englishName, DeferredItem<T> item,
            BiConsumer<Item, ItemModelProvider> modelGenerator, SortOrder sortOrder) {
        super(englishName, item.getId().getPath());
        this.item = item;
        this.modelGenerator = modelGenerator;
        this.onItemRegistrationEvent = null;
        this.sortOrder = sortOrder;
    }

    public ItemDefinition<T> withItemRegistrationEvent(Consumer<? super T> onItemRegistrationEvent) {
        this.onItemRegistrationEvent = onItemRegistrationEvent;
        return this;
    }

    public void onRegister() {
        if (this.onItemRegistrationEvent != null) {
            this.onItemRegistrationEvent.accept(item.get());
        }
    }

    public ItemStack stack() {
        return stack(1);
    }

    public ItemStack stack(int stackSize) {
        return new ItemStack(item.get(), stackSize);
    }

    public boolean is(ItemStack stack) {
        return stack.is(item);
    }

    @Override
    public T asItem() {
        return item.get();
    }

    @Override
    public String getTranslationKey() {
        return item.get().getDescriptionId();
    }
}
