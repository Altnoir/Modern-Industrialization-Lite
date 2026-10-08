package aztech.milite.machines.gui;

import aztech.milite.MI;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public class MachineGuiParameters {
    public final ResourceLocation blockId;
    public final int playerInventoryX, playerInventoryY;
    public final int backgroundWidth, backgroundHeight;
    public final boolean lockButton;

    private MachineGuiParameters(ResourceLocation blockId, int playerInventoryX, int playerInventoryY, int backgroundWidth, int backgroundHeight,
            boolean lockButton) {
        this.blockId = blockId;
        this.playerInventoryX = playerInventoryX;
        this.playerInventoryY = playerInventoryY;
        this.backgroundWidth = backgroundWidth;
        this.backgroundHeight = backgroundHeight;
        this.lockButton = lockButton;
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeResourceLocation(blockId);
        buf.writeInt(playerInventoryX);
        buf.writeInt(playerInventoryY);
        buf.writeInt(backgroundWidth);
        buf.writeInt(backgroundHeight);
        buf.writeBoolean(lockButton);
    }

    public static MachineGuiParameters read(FriendlyByteBuf buf) {
        return new MachineGuiParameters(buf.readResourceLocation(), buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt(), buf.readBoolean());
    }

    public static class Builder {
        private final ResourceLocation blockId;
        public int playerInventoryX = 8, playerInventoryY = 84;
        private final int backgroundSizeX = 176;
        private int backgroundSizeY = 166;
        public final boolean lockButton;

        public Builder(ResourceLocation blockId, boolean lockButton) {
            this.blockId = blockId;
            this.lockButton = lockButton;
        }

        public Builder(String blockId, boolean lockButton) {
            this(MI.id(blockId), lockButton);
        }

        public Builder backgroundHeight(int height) {
            this.backgroundSizeY = height;
            this.playerInventoryY = height - 82;
            return this;
        }

        public MachineGuiParameters build() {
            return new MachineGuiParameters(blockId, playerInventoryX, playerInventoryY, backgroundSizeX, backgroundSizeY, lockButton);
        }
    }
}
