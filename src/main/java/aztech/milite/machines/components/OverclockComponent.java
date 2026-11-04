package aztech.milite.machines.components;

import aztech.milite.machines.MachineBlockEntity;
import aztech.milite.machines.MachineComponent;
import aztech.milite.MIText;
import aztech.milite.MITooltips;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.ItemInteractionResult;

import java.util.*;

public class OverclockComponent implements MachineComponent {
    private final List<Catalyst> catalysts;

    private final NavigableMap<Double, MutableTickCount> tickMap = new TreeMap<>();

    public OverclockComponent(List<Catalyst> catalysts) {
        this.catalysts = catalysts;
    }

    @Override
    public void writeNbt(CompoundTag tag, HolderLookup.Provider registries) {
        for (var entry : tickMap.entrySet()) {
            var multiplierKey = String.format("overclock%.2f", entry.getKey().doubleValue());
            tag.putInt(multiplierKey, entry.getValue().value);
        }
    }

    @Override
    public void readNbt(CompoundTag tag, HolderLookup.Provider registries, boolean isUpgradingMachine) {
        for (Catalyst catalyst : catalysts) {
            var multiplierKey = String.format("overclock%.2f", catalyst.multiplier);
            if (tag.contains(multiplierKey) && !tickMap.containsKey(catalyst.multiplier)) {
                tickMap.put(catalyst.multiplier, new MutableTickCount(tag.getInt(multiplierKey)));
            }
        }
    }

    public int getTicks() {
        var lastEntry = tickMap.lastEntry();
        if (lastEntry != null) {
            return lastEntry.getValue().value;
        }
        return 0;
    }

    public ItemInteractionResult onUse(MachineBlockEntity be, Player player, InteractionHand hand) {
        ItemStack stackInHand = player.getItemInHand(hand);
        var resourceInHand = BuiltInRegistries.ITEM.getKey(stackInHand.getItem());

        for (Catalyst catalyst : catalysts) {
            if (resourceInHand.equals(catalyst.resourceLocation) && stackInHand.getCount() >= 1) {
                if (!player.isCreative()) {
                    stackInHand.shrink(1);
                }

                if (tickMap.containsKey(catalyst.multiplier)) {
                    var overclockTicks = tickMap.get(catalyst.multiplier);
                    overclockTicks.value += catalyst.ticks;
                } else {
                    tickMap.put(catalyst.multiplier, new MutableTickCount(catalyst.ticks));
                }

                be.setChanged();
                if (!be.getLevel().isClientSide()) {
                    be.sync();
                }
                return ItemInteractionResult.sidedSuccess(be.getLevel().isClientSide);
            }
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    public void tick(MachineBlockEntity be) {
        var lastEntry = tickMap.lastEntry();
        if (lastEntry != null) {
            var overclockTicks = lastEntry.getValue();
            overclockTicks.value--;
            if (overclockTicks.value <= 0) {
                tickMap.remove(lastEntry.getKey());
            } else {
                if (be.getLevel().isClientSide()) {
                    for (int iter = 0; iter < 3; iter++) {
                        var random = be.getLevel().getRandom();
                        var particlePos = be.getOverclockParticlePos();
                        double d = particlePos.x;
                        double e = particlePos.y;
                        double f = particlePos.z;
                        double i = random.nextDouble() * 0.6D - 0.3D;
                        double k = random.nextDouble() * 0.6D - 0.3D;
                        be.getLevel().addParticle(ParticleTypes.SMOKE, d + i, e, f + k, 0.15 * (random.nextDouble() - 0.5), 0.15D,
                                0.15 * (random.nextDouble() - 0.5));
                    }
                }
            }
        }
    }

    public List<Component> getTooltips() {
        var tooltips = new ArrayList<Component>();
        for (Catalyst catalyst : catalysts) {

            var catalystItem = BuiltInRegistries.ITEM.get(catalyst.resourceLocation);
            tooltips.add(MITooltips.line(MIText.OverclockMachine).arg(catalystItem, MITooltips.ITEM_PARSER).arg(catalyst.multiplier)
                    .arg(catalyst.ticks).build());
        }
        return tooltips;
    }

    public long getRecipeFe(int fe) {
        var lastEntry = tickMap.lastEntry();
        if (lastEntry != null && lastEntry.getValue().value > 0) {
            return Math.round(lastEntry.getKey().doubleValue() * fe);
        } else {
            return fe;
        }
    }

    public static List<Catalyst> getDefaultCatalysts() {
        return List.of(new OverclockComponent.Catalyst(2D, ResourceLocation.parse("minecraft:gunpowder"), 120 * 20));
    }

    public record Catalyst(double multiplier, ResourceLocation resourceLocation, int ticks) {}

    private static class MutableTickCount {
        public int value;

        public MutableTickCount(int value) {
            this.value = value;
        }
    }
}
