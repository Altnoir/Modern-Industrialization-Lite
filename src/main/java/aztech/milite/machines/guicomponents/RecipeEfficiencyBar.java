package aztech.milite.machines.guicomponents;

import aztech.milite.machines.components.CrafterComponent;
import aztech.milite.machines.gui.GuiComponentServer;
import aztech.milite.MI;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;

public class RecipeEfficiencyBar implements GuiComponentServer<RecipeEfficiencyBar.Params, RecipeEfficiencyBar.Data> {
    public static final Type<Params, Data> TYPE = new Type<>(MI.id("recipe_efficiency_bar"), Params.STREAM_CODEC, Data.STREAM_CODEC);

    private final Params params;
    private final CrafterComponent crafter;

    public RecipeEfficiencyBar(Params params, CrafterComponent crafter) {
        this.params = params;
        this.crafter = crafter;
    }

    @Override
    public Params params() {
        return params;
    }

    @Override
    public Data extractData() {
        if (crafter.hasActiveRecipe()) {
            return new Data(true,
                    crafter.getEfficiencyTicks(), crafter.getMaxEfficiencyTicks(),
                    crafter.getCurrentRecipeEu(), crafter.getBaseRecipeEu(), crafter.getBehavior().getMaxRecipeEu());
        } else {
            return new Data(false, 0, 0, 0, 0, crafter.getBehavior().getMaxRecipeEu());
        }
    }

    @Override
    public Type<Params, Data> getType() {
        return TYPE;
    }

    public record Data(boolean hasActiveRecipe, int efficiencyTicks, int maxEfficiencyTicks, long currentRecipeEu, long baseRecipeEu, long maxRecipeEu) {
        public static final StreamCodec<RegistryFriendlyByteBuf, Data> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.BOOL,
                Data::hasActiveRecipe,
                ByteBufCodecs.VAR_INT,
                Data::efficiencyTicks,
                ByteBufCodecs.VAR_INT,
                Data::maxEfficiencyTicks,
                ByteBufCodecs.VAR_LONG,
                Data::currentRecipeEu,
                ByteBufCodecs.VAR_LONG,
                Data::baseRecipeEu,
                ByteBufCodecs.VAR_LONG,
                Data::maxRecipeEu,
                Data::new);
    }

    public record Params(int renderX, int renderY) {
        public static final StreamCodec<RegistryFriendlyByteBuf, Params> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.VAR_INT,
                Params::renderX,
                ByteBufCodecs.VAR_INT,
                Params::renderY,
                Params::new);
    }
}
