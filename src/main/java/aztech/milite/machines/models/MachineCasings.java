package aztech.milite.machines.models;

import aztech.milite.compat.kubejs.KubeJSProxy;
import aztech.milite.materials.MIMaterials;
import aztech.milite.materials.part.MIParts;
import aztech.milite.MI;
import aztech.milite.MIBlock;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.function.Supplier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings("Convert2MethodRef")
public class MachineCasings {
    public static final Map<ResourceLocation, MachineCasing> registeredCasings = new HashMap<>();

    public record CasingName(MachineCasing casing, String englishName) {}

    public static final List<CasingName> translations = new ArrayList<>();

    public static final MachineCasing BRICKED_BRONZE = create(MI.id("bricked_bronze"), "Bricked Bronze");
    public static final MachineCasing BRICKED_STEEL = create(MI.id("bricked_steel"), "Bricked Steel");
    public static final MachineCasing BRICKS = createBlockImitation(MI.id("bricks"), () -> Blocks.BRICKS);
    public static final MachineCasing BRONZE = createBlockImitation(MI.id("bronze"),
            () -> MIMaterials.BRONZE.getPart(MIParts.MACHINE_CASING).asBlock());
    public static final MachineCasing BRONZE_PLATED_BRICKS = createBlockImitation(MI.id("bronze_plated_bricks"),
            () -> MIMaterials.BRONZE.getPart(MIParts.MACHINE_CASING_SPECIAL).asBlock());
    public static final MachineCasing CLEAN_STAINLESS_STEEL = createBlockImitation(MI.id("clean_stainless_steel_machine_casing"),
            () -> MIMaterials.STAINLESS_STEEL.getPart(MIParts.MACHINE_CASING_SPECIAL).asBlock());
    public static final MachineCasing CONFIGURABLE_TANK = create(MI.id("configurable_tank"), "Configurable Tank");
    public static final MachineCasing STAINLESS_STEEL_PIPE = createBlockImitation(MI.id("stainless_steel_machine_casing_pipe"),
            () -> MIMaterials.STAINLESS_STEEL.getPart(MIParts.MACHINE_CASING_PIPE).asBlock());
    public static final MachineCasing FIREBRICKS = createBlockImitation(MI.id("firebricks"), () -> MIBlock.BLOCK_FIRE_CLAY_BRICKS.asBlock());
    public static final MachineCasing FROSTPROOF = createBlockImitation(MI.id("frostproof_machine_casing"),
            () -> MIMaterials.ALUMINUM.getPart(MIParts.MACHINE_CASING_SPECIAL).asBlock());
    public static final MachineCasing HEATPROOF = createBlockImitation(MI.id("heatproof_machine_casing"),
            () -> MIMaterials.INVAR.getPart(MIParts.MACHINE_CASING_SPECIAL).asBlock());
    public static final MachineCasing STEEL = createBlockImitation(MI.id("steel"), () -> MIMaterials.STEEL.getPart(MIParts.MACHINE_CASING).asBlock());
    public static final MachineCasing STEEL_CRATE = create(MI.id("steel_crate"), "Steel Crate");
    public static final MachineCasing TITANIUM_PIPE = createBlockImitation(MI.id("titanium_machine_casing_pipe"),
            () -> MIMaterials.TITANIUM.getPart(MIParts.MACHINE_CASING_PIPE).asBlock());
    public static final MachineCasing SOLID_TITANIUM = createBlockImitation(MI.id("solid_titanium_machine_casing"),
            () -> MIMaterials.TITANIUM.getPart(MIParts.MACHINE_CASING_SPECIAL).asBlock());
    public static final MachineCasing NUCLEAR = createBlockImitation(MI.id("nuclear_casing"),
            () -> MIMaterials.NUCLEAR_ALLOY.getPart(MIParts.MACHINE_CASING_SPECIAL).asBlock());
    public static final MachineCasing PLASMA_HANDLING_IRIDIUM = createBlockImitation(MI.id("plasma_handling_iridium_machine_casing"),
            () -> MIMaterials.IRIDIUM.getPart(MIParts.MACHINE_CASING_SPECIAL).asBlock());

    static {
        KubeJSProxy.instance.fireRegisterMachineCasingsEvent();
    }

    public static MachineCasing createBlockImitation(ResourceLocation key, Supplier<? extends Block> block) {
        Objects.requireNonNull(block);
        return register(key, block);
    }

    public static MachineCasing create(ResourceLocation key, String englishName) {
        Objects.requireNonNull(englishName);
        var casing = register(key, null);
        translations.add(new CasingName(casing, englishName));
        return casing;
    }

    private static MachineCasing register(ResourceLocation key, @Nullable Supplier<? extends Block> blockImitation) {
        if (registeredCasings.containsKey(key)) {
            throw new IllegalArgumentException("Duplicate machine casing definition: " + key);
        }

        MachineCasing casing = new MachineCasing(key, blockImitation);
        registeredCasings.put(key, casing);
        return casing;
    }

    public static MachineCasing get(ResourceLocation key) {
        MachineCasing casing = registeredCasings.get(key);
        if (casing != null) {
            return casing;
        } else {
            throw new IllegalArgumentException("Machine casing model \"" + key + "\" does not exist.");
        }
    }

    public static MachineCasing get(String name) {
        return get(ResourceLocation.isValidPath(name) ? MI.id(name) : ResourceLocation.parse(name));
    }
}
