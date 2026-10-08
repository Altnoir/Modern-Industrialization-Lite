package aztech.milite.nuclear;

import aztech.milite.machines.blockentities.hatches.NuclearHatch;
import org.jspecify.annotations.Nullable;

public final class NuclearGrid {
    private final int sizeX;
    private final int sizeY;
    private final @Nullable NuclearHatch[][] hatchesGrid;

    public NuclearGrid(int sizeX, int sizeY, @Nullable NuclearHatch[][] hatchesGrid) {
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.hatchesGrid = hatchesGrid;
    }

    public int getSizeX() {
        return sizeX;
    }

    public int getSizeY() {
        return sizeY;
    }

    @Nullable
    public NuclearTile getNuclearTile(int x, int y) {
        return hatchesGrid[x][y];
    }

    public void registerNeutronFate(int neutronNumber, NeutronType type, NeutronFate escape) {}

    public void registerNeutronCreation(int neutronNumber, NeutronType type) {}
}
