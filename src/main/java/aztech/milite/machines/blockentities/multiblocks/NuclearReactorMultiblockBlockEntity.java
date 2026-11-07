package aztech.milite.machines.blockentities.multiblocks;

import aztech.milite.compat.rei.machines.ReiMachineRecipes;
import aztech.milite.inventory.MIInventory;
import aztech.milite.machines.BEP;
import aztech.milite.machines.blockentities.hatches.NuclearHatch;
import aztech.milite.machines.components.*;
import aztech.milite.machines.gui.MachineGuiParameters;
import aztech.milite.machines.guicomponents.NuclearReactorGui;
import aztech.milite.machines.guicomponents.ShapeSelection;
import aztech.milite.machines.guicomponents.RedstoneControlGui;
import aztech.milite.machines.models.MachineCasings;
import aztech.milite.machines.models.MachineModelClientData;
import aztech.milite.machines.multiblocks.*;
import aztech.milite.MI;
import aztech.milite.MIBlock;
import aztech.milite.MIText;
import aztech.milite.nuclear.*;
import aztech.milite.util.Tickable;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;

import java.util.ArrayList;
import java.util.function.Supplier;
import java.util.List;
import java.util.Optional;

public class NuclearReactorMultiblockBlockEntity extends MultiblockMachineBlockEntity implements Tickable {
    private static final ShapeTemplate[] shapeTemplates;

    private final ActiveShapeComponent activeShape;
    private final RedstoneControlComponent redstoneControl;
    private final IsActiveComponent isActive;
    private final NuclearEfficiencyHistoryComponent efficiencyHistory;

    private NuclearGrid nuclearGrid;
    private Supplier<NuclearReactorGui.Data> dataSupplier;

    static {

        shapeTemplates = new ShapeTemplate[3];

        SimpleMember casing = SimpleMember.forBlock(MIBlock.BLOCK_DEFINITIONS.get(MI.id("nuclear_casing")));
        SimpleMember controlRod = SimpleMember.forBlock(MIBlock.BLOCK_DEFINITIONS.get(MI.id("control_rod")));
        HatchFlags top = new HatchFlags.Builder().with(HatchTypes.NUCLEAR_FLUID, HatchTypes.NUCLEAR_ITEM).build();
        for (int i = 0; i < shapeTemplates.length; i++) {
            ShapeTemplate.Builder builder = new ShapeTemplate.Builder(MachineCasings.NUCLEAR);
            for (int x = -2 - i; x <= 2 + i; x++) {
                int xAbs = Math.abs(x);
                int minZ = Math.max(0, xAbs - 1 - i);

                int maxZ = 2 * (2 + i) - minZ;

                for (int z = minZ; z <= maxZ; z++) {
                    if (!(z == minZ || z == maxZ || xAbs == 2 + i)) {
                        builder.add(x, -1, z, casing, null);
                        builder.add(x, 0, z, controlRod, null);
                        builder.add(x, 1, z, controlRod, null);
                        builder.add(x, 2, z, controlRod, null);
                        builder.add(x, 3, z, casing, top);

                    } else {
                        builder.add(x, -1, z, casing, null);
                        if (x != 0 || z != 0)
                            builder.add(x, 0, z, casing, null);
                        builder.add(x, 1, z, casing, null);
                        builder.add(x, 2, z, casing, null);
                        builder.add(x, 3, z, casing, null);
                    }

                }
            }
            shapeTemplates[i] = builder.build();
        }
    }

    public NuclearReactorGui.Data sendData() {
        if (shapeValid.shapeValid) {
            return dataSupplier.get();
        } else {
            return new NuclearReactorGui.Data(false, 0, 0, List.of(), 0, 0);
        }
    }

    @Override
    public MIInventory getInventory() {
        return MIInventory.EMPTY;
    }

    @Override
    public final MachineModelClientData getMachineModelData() {
        return new MachineModelClientData(null, orientation.facingDirection).active(isActive.isActive);
    }

    public NuclearReactorMultiblockBlockEntity(BEP bep) {
        super(bep, new MachineGuiParameters.Builder("nuclear_reactor", false).backgroundHeight(274).build(),
                new OrientationComponent.Params(false, false, false));

        this.activeShape = new ActiveShapeComponent(shapeTemplates);
        this.efficiencyHistory = new NuclearEfficiencyHistoryComponent();
        this.isActive = new IsActiveComponent();
        this.redstoneControl = new RedstoneControlComponent();
        registerComponents(activeShape, isActive, efficiencyHistory, redstoneControl);
        this.registerGuiComponent(new NuclearReactorGui(this::sendData), new RedstoneControlGui(redstoneControl));

        registerGuiComponent(new ShapeSelection(new ShapeSelection.Behavior() {
            @Override
            public void handleClick(int clickedLine, int delta) {
                activeShape.incrementShape(NuclearReactorMultiblockBlockEntity.this, delta);
            }

            @Override
            public int getCurrentIndex(int line) {
                return activeShape.getActiveShapeIndex();
            }
        }, new ShapeSelection.LineInfo(
                List.of(MIText.ShapeTextSmall.text().append(" 3×3"), MIText.ShapeTextMedium.text().append(" 5×5"),
                        MIText.ShapeTextLarge.text().append(" 7×7")),
                List.of())));
    }

    @Override
    public void tick() {
        if (!level.isClientSide) {
            link();
            if (shapeValid.shapeValid) {
                boolean useFuel = redstoneControl.doAllowNormalOperation(this);
                isActive.updateActive(NuclearGridHelper.simulate(nuclearGrid, efficiencyHistory, useFuel) && useFuel,
                        this);
                efficiencyHistory.tick();
            } else {
                isActive.updateActive(false, this);
                efficiencyHistory.clear();
            }
        }
    }

    @Override
    public ShapeTemplate getActiveShape() {
        return activeShape.getActiveShape();
    }

    @Override
    public ShapeTemplate getBigShape() {
        return shapeTemplates[shapeTemplates.length - 1];
    }

    @Override
    protected void onRematch(ShapeMatcher shapeMatcher) {
        nuclearGrid = null;
        if (shapeMatcher.isMatchSuccessful()) {
            shapeValid.shapeValid = true;
            // Keep one empty cell around the hatch grid for neutron and heat escape.
            int size = 5 + 2 * activeShape.getActiveShapeIndex();
            NuclearHatch[][] hatchesGrid = new NuclearHatch[size][size];

            for (HatchBlockEntity hatch : shapeMatcher.getMatchedHatches()) {
                int x0 = hatch.getBlockPos().getX() - getBlockPos().getX();
                int z0 = hatch.getBlockPos().getZ() - getBlockPos().getZ();

                int x, y;

                if (orientation.facingDirection == Direction.NORTH) {
                    x = size / 2 + x0;
                    y = z0;
                } else if (orientation.facingDirection == Direction.SOUTH) {
                    x = size / 2 - x0;
                    y = -z0;
                } else if (orientation.facingDirection == Direction.EAST) {
                    x = size / 2 + z0;
                    y = -x0;

                } else {
                    x = size / 2 - z0;
                    y = x0;
                }

                hatchesGrid[x][y] = (NuclearHatch) hatch;
            }

            nuclearGrid = new NuclearGrid(size, size, hatchesGrid);

            dataSupplier = () -> {
                List<NuclearReactorGui.TileData> tilesData = new ArrayList<>(size * size);
                for (int i = 0; i < size; i++) {
                    for (int j = 0; j < size; j++) {
                        final int x = size - 1 - i;
                        final int y = size - 1 - j;

                        tilesData.add(new NuclearReactorGui.TileData(Optional.ofNullable(hatchesGrid[x][y])));
                    }
                }
                return new NuclearReactorGui.Data(true, size, size, tilesData,
                        efficiencyHistory.getAverage(NuclearEfficiencyHistoryComponent.Type.feProduction),
                        efficiencyHistory.getAverage(NuclearEfficiencyHistoryComponent.Type.feFuelConsumption));
            };
        }
    }

    public static void registerReiShapes() {
        for (int i = 0; i < shapeTemplates.length; ++i) {
            ReiMachineRecipes.registerMultiblockShape("nuclear_reactor", shapeTemplates[i], "" + i);
        }
    }

    @Override
    protected ItemInteractionResult useItemOn(Player player, InteractionHand hand, Direction face) {
        return super.useItemOn(player, hand, face);
    }
}
