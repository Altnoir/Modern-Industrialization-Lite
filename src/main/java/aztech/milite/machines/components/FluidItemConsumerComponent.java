package aztech.milite.machines.components;

import aztech.milite.api.datamaps.FluidFuel;
import aztech.milite.definition.FluidDefinition;
import aztech.milite.inventory.ConfigurableFluidStack;
import aztech.milite.inventory.ConfigurableItemStack;
import aztech.milite.machines.MachineComponent;
import aztech.milite.MIText;
import aztech.milite.MITooltips;
import aztech.milite.util.ItemStackHelper;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A component that turns fluids and/or item into energy.
 */
public class FluidItemConsumerComponent implements MachineComponent.ServerOnly {
    protected long rfBuffer = 0;
    /**
     * The maximum EU that can be produced by one production operation, to limit the
     * maximum conversion rate of the machine.
     */
    public final long maxRfProduction;

    protected final double rfMultiplier;

    public final EUProductionMap<Item> itemEUProductionMap;
    public final EUProductionMap<Fluid> fluidEUProductionMap;

    private final boolean hideEfficiencyTooltip;

    public FluidItemConsumerComponent(long maxRfProduction,
            double rfMultiplier,
            EUProductionMap<Item> itemEUProductionMap,
            EUProductionMap<Fluid> fluidEUProductionMap,
            boolean hideEfficiencyTooltip) {
        this.itemEUProductionMap = itemEUProductionMap;
        this.fluidEUProductionMap = fluidEUProductionMap;
        this.maxRfProduction = maxRfProduction;
        this.rfMultiplier = rfMultiplier;
        this.hideEfficiencyTooltip = hideEfficiencyTooltip;
    }

    public boolean doAllowMoreThanOne() {
        return itemEUProductionMap.getNumberOfFuel() == NumberOfFuel.MANY
                || fluidEUProductionMap.getNumberOfFuel() == NumberOfFuel.MANY;
    }

    public static FluidItemConsumerComponent ofSingleFluid(long maxRfProduction,
            FluidDefinition acceptedFluid,
            long fluidEUperMb) {
        return ofFluid(maxRfProduction, new RfProductionMapBuilder<>(BuiltInRegistries.FLUID).add(acceptedFluid.getId(), fluidEUperMb).build());
    }

    public static FluidItemConsumerComponent ofFluidFuels(long maxRfProduction, double rfMultiplier) {
        return new FluidItemConsumerComponent(maxRfProduction,
                rfMultiplier,
                EUProductionMap.empty(),
                fluidFuels(),
                false);
    }

    public static FluidItemConsumerComponent ofFluid(long maxRfProduction, EUProductionMap<Fluid> fluidEUProductionMap) {
        return new FluidItemConsumerComponent(maxRfProduction,
                1,
                EUProductionMap.empty(),
                fluidEUProductionMap,
                true);
    }

    @Override
    public void writeNbt(CompoundTag tag, HolderLookup.Provider registries) {
        tag.putLong("euBuffer", rfBuffer);
    }

    @Override
    public void readNbt(CompoundTag tag, HolderLookup.Provider registries, boolean isUpgradingMachine) {
        rfBuffer = tag.getLong("euBuffer");
    }

    public long getRfProduction(List<ConfigurableFluidStack> fluidInputs,
            List<ConfigurableItemStack> itemInputs,
            long maxEnergyInsertable) {
        long maxRfProduced = Math.min(maxEnergyInsertable, maxRfProduction);

        if (maxRfProduced == 0) {
            return 0;
        }

        if (rfBuffer >= maxRfProduced) {
            rfBuffer -= maxRfProduced;
            return maxRfProduced;
        }

        long rfProduced = 0;

        // Consume from the buffer first
        rfProduced += rfBuffer;
        rfBuffer = 0;

        for (ConfigurableFluidStack stack : fluidInputs) {
            Fluid fluid = stack.getResource().getFluid();
            if (fluidEUProductionMap.accept(fluid) && stack.getAmount() > 0) {
                long fuelRf = (long) (fluidEUProductionMap.getRfProduction(fluid) * rfMultiplier);
                long usedDroplets = Math.min((maxRfProduced - rfProduced + fuelRf - 1) / fuelRf, stack.getAmount());
                rfProduced += usedDroplets * fuelRf;
                stack.decrement(usedDroplets);

                if (rfProduced >= maxRfProduced) {
                    rfBuffer += rfProduced - maxRfProduced;
                    return maxRfProduced;
                }
            }
        }

        for (ConfigurableItemStack stack : itemInputs) {
            Item fuel = stack.getResource().getItem();
            if (itemEUProductionMap.accept(fuel) && stack.getAmount() > 0) {
                if (!itemEUProductionMap.isStandardFuels() || ItemStackHelper.consumeFuel(stack, true)) {
                    long fuelEU = (long) (itemEUProductionMap.getRfProduction(fuel) * rfMultiplier);
                    long usedItem = Math.min((maxRfProduced - rfProduced + fuelEU - 1) / fuelEU, stack.getAmount());
                    rfProduced += fuelEU * usedItem;

                    if (itemEUProductionMap.isStandardFuels()) {
                        ItemStackHelper.consumeFuel(stack, false);
                    } else {
                        stack.decrement(usedItem);
                    }

                    if (rfProduced >= maxRfProduced) {
                        rfBuffer += rfProduced - maxRfProduced;
                        return maxRfProduced;
                    }
                }
            }
        }

        return rfProduced;
    }

    public List<Component> getTooltips() {
        List<Component> returnList = new ArrayList<>();

        returnList.add(new MITooltips.Line(MIText.MaxRfProduction).arg(
                this.maxRfProduction,
                MITooltips.EU_PER_TICK_PARSER).build());

        if (this.itemEUProductionMap.getNumberOfFuel() != NumberOfFuel.NONE) {
            if (this.itemEUProductionMap.isStandardFuels()) {
                returnList.add(new MITooltips.Line(MIText.AcceptAnyItemFuels).build());
            } else {
                var informationEntries = this.itemEUProductionMap.getAllAcceptedWithEU();
                if (informationEntries.size() == 1) {
                    var entry = informationEntries.iterator().next();
                    returnList.add(new MITooltips.Line(MIText.AcceptSingleItem)
                            .arg(entry.variant).arg(entry.rf, MITooltips.EU_PARSER).build());
                } else {
                    returnList.add(new MITooltips.Line(MIText.ConsumesTheFollowing).build());
                    for (var entry : informationEntries) {
                        returnList.add(
                                new MITooltips.Line(MIText.AcceptFollowingItemEntry)
                                        .arg(entry.variant).arg(entry.rf, MITooltips.EU_PARSER).build());
                    }
                }
            }
            if (!hideEfficiencyTooltip) {
                if (rfMultiplier == 1) {
                    returnList.add(new MITooltips.Line(MIText.FuelEfficiencyItemNormal).build());
                } else {
                    returnList.add(new MITooltips.Line(MIText.FuelEfficiencyItem).arg(rfMultiplier, MITooltips.MULTIPLIER_PARSER).arg(rfMultiplier, MITooltips.MULTIPLIER_PARSER).build());
                }
            }
        }

        if (this.fluidEUProductionMap.getNumberOfFuel() != NumberOfFuel.NONE) {
            if (this.fluidEUProductionMap.isStandardFuels()) {
                returnList.add(new MITooltips.Line(MIText.AcceptAnyFluidFuels).build());
            } else {
                var informationEntries = this.fluidEUProductionMap.getAllAcceptedWithEU();

                if (informationEntries.size() == 1) {
                    var entry = informationEntries.iterator().next();
                    returnList.add(new MITooltips.Line(MIText.AcceptSingleFluid)
                            .arg(entry.variant).arg(entry.rf, MITooltips.EU_PARSER).build());
                } else if (informationEntries.size() > 1) {
                    returnList.add(new MITooltips.Line(MIText.ConsumesTheFollowing).build());
                    for (var entry : informationEntries) {
                        returnList.add(
                                new MITooltips.Line(MIText.AcceptFollowingFluidEntry)
                                        .arg(entry.variant).arg(entry.rf, MITooltips.EU_PARSER).build());
                    }
                }
            }
            if (!hideEfficiencyTooltip) {
                if (rfMultiplier == 1) {
                    returnList.add(new MITooltips.Line(MIText.FuelEfficiencyFluidNormal).build());
                } else {
                    returnList.add(new MITooltips.Line(MIText.FuelEfficiencyFluid).arg(rfMultiplier, MITooltips.MULTIPLIER_PARSER).arg(rfMultiplier, MITooltips.MULTIPLIER_PARSER).build());
                }
            }
        }

        return returnList;
    }

    public interface EUProductionMap<T> {
        record InformationEntry<T>(long rf, T variant) {}

        long getRfProduction(T variant);

        default boolean isStandardFuels() {
            return false;
        }

        default boolean accept(T variant) {
            return getRfProduction(variant) != 0;
        }

        default NumberOfFuel getNumberOfFuel() {
            if (isStandardFuels()) {
                return NumberOfFuel.MANY;
            } else {
                if (getAllAccepted().size() == 1) {
                    return NumberOfFuel.SINGLE;
                } else if (getAllAccepted().size() == 0) {
                    return NumberOfFuel.NONE;
                } else {
                    return NumberOfFuel.MANY;
                }
            }
        }

        // create an empty EUProductionMap
        static <T> EUProductionMap<T> empty() {
            return new EUProductionMap<>() {
                @Override
                public long getRfProduction(T variant) {
                    return 0;
                }

                @Override
                public List<T> getAllAccepted() {
                    return List.of();
                }
            };
        }

        List<T> getAllAccepted();

        default List<InformationEntry<T>> getAllAcceptedWithEU() {
            return getAllAccepted().stream()
                    .map(variant -> new InformationEntry<>(getRfProduction(variant), variant)).sorted(Comparator.comparingLong(InformationEntry::rf))
                    .collect(Collectors.toList());
        }
    }

    public static class RfProductionMapBuilder<T> {
        private final Map<ResourceLocation, Long> map = new HashMap<>(); // Must Stores as string, because KubeJS could add not loader yet resource
                                                                        // location
        private final DefaultedRegistry<T> registryAccess;

        public RfProductionMapBuilder(DefaultedRegistry<T> registryAccess) {
            this.registryAccess = registryAccess;
        }

        public RfProductionMapBuilder<T> add(ResourceLocation resourceLocation,
                long rf) {
            map.put(resourceLocation, rf);
            return this;
        }

        public EUProductionMap<T> build() {
            return new EUProductionMap<>() {
                @Override
                public long getRfProduction(T variant) {
                    return map.getOrDefault(registryAccess.getKey(variant), 0L);
                }

                @Override
                public List<T> getAllAccepted() {
                    return map.keySet().stream().map(registryAccess::get).collect(Collectors.toList());
                }
            };
        }
    }

    public static EUProductionMap<Item> itemFuels() {
        return new EUProductionMap<>() {
            @Override
            public long getRfProduction(Item variant) {
                // TODO NEO NBT-aware fuels
                int burnTime = variant.getDefaultInstance().getBurnTime(null);
                return burnTime <= 0 ? 0 : burnTime * FuelBurningComponent.EU_PER_BURN_TICK;
            }

            @Override
            public boolean isStandardFuels() {
                return true;
            }

            @Override
            public List<Item> getAllAccepted() {
                throw new UnsupportedOperationException("The list of accepted items is not available for standard fuels");
            }
        };
    }

    public static EUProductionMap<Fluid> fluidFuels() {
        return new EUProductionMap<>() {
            @Override
            public long getRfProduction(Fluid variant) {
                return FluidFuel.getRf(variant);
            }

            @Override
            public boolean isStandardFuels() {
                return true;
            }

            @Override
            public List<Fluid> getAllAccepted() {
                throw new UnsupportedOperationException("The list of accepted fluids is not available for fluid fuels");
            }
        };
    }

    public enum NumberOfFuel {
        NONE(),
        SINGLE(),
        MANY(),
    }
}
