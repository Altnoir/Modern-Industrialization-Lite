package aztech.milite.machines.blockentities.multiblocks;

import aztech.milite.machines.BEP;
import aztech.milite.machines.components.CrafterComponent;
import aztech.milite.machines.components.OrientationComponent;
import aztech.milite.machines.components.OverclockComponent;
import aztech.milite.machines.guicomponents.CraftingMultiblockGui;
import aztech.milite.machines.helper.SteamHelper;
import aztech.milite.machines.init.MachineLevel;
import aztech.milite.machines.multiblocks.HatchBlockEntity;
import aztech.milite.machines.multiblocks.ShapeMatcher;
import aztech.milite.machines.multiblocks.ShapeTemplate;
import aztech.milite.machines.recipe.MachineRecipeType;
import aztech.milite.MI;
import aztech.milite.util.Simulation;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class SteamCraftingMultiblockBlockEntity extends AbstractCraftingMultiblockBlockEntity {
    private final OverclockComponent overclockComponent;

    public SteamCraftingMultiblockBlockEntity(BEP bep, ResourceLocation blockId, ShapeTemplate shapeTemplate,
            MachineRecipeType recipeType,
            List<OverclockComponent.Catalyst> overclockCatalysts) {
        super(bep, blockId, new OrientationComponent.Params(false, false, false), new ShapeTemplate[] { shapeTemplate });

        this.overclockComponent = new OverclockComponent(overclockCatalysts);
        this.recipeType = recipeType;
        registerGuiComponent(
                new CraftingMultiblockGui(() -> shapeValid.shapeValid, crafter::getProgress, crafter, overclockComponent::getTicks));
        this.registerComponents(overclockComponent);
    }

    public SteamCraftingMultiblockBlockEntity(BEP bep, String name, ShapeTemplate shapeTemplate, MachineRecipeType recipeType,
            List<OverclockComponent.Catalyst> overclockCatalysts) {
        this(bep, MI.id(name), shapeTemplate, recipeType, overclockCatalysts);
    }

    @Override
    protected CrafterComponent.Behavior getBehavior() {
        return new Behavior();
    }

    private final MachineRecipeType recipeType;
    private boolean steelTier;

    @Override
    protected void onRematch(ShapeMatcher shapeMatcher) {
        super.onRematch(shapeMatcher);
        if (shapeMatcher.isMatchSuccessful()) {
            steelTier = false;

            for (HatchBlockEntity hatch : shapeMatcher.getMatchedHatches()) {
                if (hatch.upgradesToSteel()) {
                    steelTier = true;
                }
            }
        }
    }

    @Override
    public List<Component> getTooltips(Item.TooltipContext context) {
        return overclockComponent.getTooltips();
    }

    private class Behavior implements CrafterComponent.Behavior {
        @Override
        public long consumeFe(long max, Simulation simulation) {
            return SteamHelper.consumeSteamFe(inventory.getFluidInputs(), max, simulation);
        }

        @Override
        public MachineRecipeType recipeType() {
            return recipeType;
        }

        @Override
        public MachineLevel getMachineLevel() {
            return steelTier ? MachineLevel.STEEL : MachineLevel.BRONZE;
        }

        @Override
        public int getSpeedBonus() {
            return (int) Math.max(1, overclockComponent.getRecipeFe(1));
        }

        @Override
        public ServerLevel getCrafterWorld() {
            return (ServerLevel) level;
        }

        @Override
        @Nullable
        public UUID getOwnerUuid() {
            return placedBy.placerId;
        }
    }

    public final void tickExtra() {
        overclockComponent.tick(this);
    }

    @Override
    protected ItemInteractionResult useItemOn(Player player, InteractionHand hand, Direction face) {
        var result = super.useItemOn(player, hand, face);
        if (!result.consumesAction()) {
            return overclockComponent.onUse(this, player, hand);
        }
        return result;
    }
}
