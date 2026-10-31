package aztech.milite.client.compat.viewer.usage;

import aztech.milite.MI;
import aztech.milite.MIBlock;
import aztech.milite.MIRegistries;
import aztech.milite.blocks.forgehammer.ForgeHammerRecipe;
import aztech.milite.client.compat.viewer.abstraction.ViewerCategory;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.Comparator;
import java.util.function.Consumer;

public class ForgeHammerCategory extends ViewerCategory<RecipeHolder<ForgeHammerRecipe>> {
    public static final ResourceLocation ID = MI.id("forge_hammer");

    private static final int SLOT_SIZE = 18;
    private static final int ARROW_WIDTH = 24;
    private static final int ARROW_HEIGHT = 17;
    private static final int GAP = 8;

    private final int inputX;
    private final int arrowX;
    private final int outputX;
    private final int slotY;
    private final int arrowY;

    protected ForgeHammerCategory() {
        super(recipeHolderClass(), ID, MIBlock.FORGE_HAMMER.asBlock().getName(),
                MIBlock.FORGE_HAMMER.asItem().getDefaultInstance(), 86, 30);

        int contentWidth = SLOT_SIZE * 2 + ARROW_WIDTH + GAP * 2;
        this.inputX = (width - contentWidth) / 2 + 1;
        this.arrowX = inputX + SLOT_SIZE + GAP;
        this.outputX = arrowX + ARROW_WIDTH + GAP;
        this.slotY = (height - SLOT_SIZE) / 2 + 1;
        this.arrowY = slotY + (SLOT_SIZE - ARROW_HEIGHT) / 2;
    }

    @Override
    public void buildWorkstations(WorkstationConsumer consumer) {
        consumer.accept(MIBlock.FORGE_HAMMER);
    }

    @Override
    public void buildRecipes(RecipeManager recipeManager, RegistryAccess registryAccess, Consumer<RecipeHolder<ForgeHammerRecipe>> consumer) {
        recipeManager.getAllRecipesFor(MIRegistries.FORGE_HAMMER_RECIPE_TYPE.get())
                .stream()
                .sorted(Comparator.comparing(RecipeHolder::id))
                .forEach(consumer);
    }

    @Override
    public void buildLayout(RecipeHolder<ForgeHammerRecipe> holder, LayoutBuilder builder) {
        var recipe = holder.value();

        builder.inputSlot(inputX, slotY).ingredient(recipe.ingredient(), recipe.count(), 1);

        builder.outputSlot(outputX, slotY).item(recipe.result());
    }

    @Override
    public void buildWidgets(RecipeHolder<ForgeHammerRecipe> holder, WidgetList widgets) {
        widgets.arrow(arrowX, arrowY);
    }

    @Override
    public ResourceLocation getRecipeId(RecipeHolder<ForgeHammerRecipe> holder) {
        return holder.id();
    }
}
