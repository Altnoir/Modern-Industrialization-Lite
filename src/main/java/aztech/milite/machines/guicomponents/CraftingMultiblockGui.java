package aztech.milite.machines.guicomponents;

import aztech.milite.machines.components.CrafterComponent;
import aztech.milite.machines.gui.GuiComponentServer;
import aztech.milite.MI;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.util.Unit;

import java.util.function.IntSupplier;
import java.util.function.Supplier;
import java.util.Optional;

public class CraftingMultiblockGui implements GuiComponentServer<Unit, CraftingMultiblockGui.Data> {
    public static final Type<Unit, Data> TYPE = new Type<>(MI.id("crafting_multiblock_gui"), StreamCodec.unit(Unit.INSTANCE), Data.STREAM_CODEC);

    private final CrafterComponent crafter;
    private final Supplier<Boolean> isShapeValid;
    private final Supplier<Float> progressSupplier;
    private final IntSupplier remainingOverclockTicks;

    public CraftingMultiblockGui(Supplier<Boolean> isShapeValid, Supplier<Float> progressSupplier, CrafterComponent crafter,
            IntSupplier remainingOverclockTicks) {
        this.isShapeValid = isShapeValid;
        this.crafter = crafter;
        this.progressSupplier = progressSupplier;
        this.remainingOverclockTicks = remainingOverclockTicks;
    }

    @Override
    public Unit params() {
        return Unit.INSTANCE;
    }

    @Override
    public Data extractData() {
        boolean shapeValid = isShapeValid.get();
        Optional<RecipeData> activeRecipe;
        if (shapeValid && crafter.hasActiveRecipe()) {
            activeRecipe = Optional.of(new RecipeData(progressSupplier.get(), crafter.getCurrentRecipeFe(), crafter.getBaseRecipeFe()));
        } else {
            activeRecipe = Optional.empty();
        }
        return new Data(shapeValid, crafter.matchesMultipleRecipes(), activeRecipe, remainingOverclockTicks.getAsInt());
    }

    @Override
    public Type<Unit, Data> getType() {
        return TYPE;
    }

    public record Data(boolean isShapeValid, boolean matchesMultipleRecipes, Optional<RecipeData> activeRecipe, int remainingOverclockTicks) {
        public static final StreamCodec<RegistryFriendlyByteBuf, Data> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.BOOL,
                Data::isShapeValid,
                ByteBufCodecs.BOOL,
                Data::matchesMultipleRecipes,
                ByteBufCodecs.optional(RecipeData.STREAM_CODEC),
                Data::activeRecipe,
                ByteBufCodecs.VAR_INT,
                Data::remainingOverclockTicks,
                Data::new);
    }

    public record RecipeData(float progress, long currentRecipeFe, long baseRecipeFe) {
        public static final StreamCodec<RegistryFriendlyByteBuf, RecipeData> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.FLOAT,
                RecipeData::progress,
                ByteBufCodecs.VAR_LONG,
                RecipeData::currentRecipeFe,
                ByteBufCodecs.VAR_LONG,
                RecipeData::baseRecipeFe,
                RecipeData::new);
    }

    public static final int X = 5;
    public static final int Y = 16;
    public static final int W = 166;
    public static final int H = 83;
}
