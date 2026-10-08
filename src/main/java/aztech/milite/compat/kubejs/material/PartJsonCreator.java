package aztech.milite.compat.kubejs.material;

import aztech.milite.materials.part.MIParts;
import aztech.milite.materials.part.OrePart;
import aztech.milite.materials.part.PartTemplate;
import aztech.milite.materials.part.RawMetalPart;
import aztech.milite.materials.set.MaterialBlockSet;
import aztech.milite.materials.set.MaterialOreSet;
import aztech.milite.materials.set.MaterialRawSet;
import aztech.milite.nuclear.NuclearConstant;
import com.google.gson.JsonObject;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.biome.Biome;

public class PartJsonCreator {
    public PartTemplate regularPart(String name) {
        try {
            return (PartTemplate) MIParts.class.getField(name.toUpperCase()).get(null);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new IllegalArgumentException("No such default Part Template: " + name.toUpperCase());
        }
    }

    public PartTemplate customRegularPart(String englishName, String name) {
        return new PartTemplate(englishName, name);
    }

    public PartTemplate batteryPart(long energyCapacity) {
        return MIParts.BATTERY.of(energyCapacity);
    }

    public PartTemplate barrelPart(int stackCapacity) {
        return MIParts.BARREL.of(stackCapacity);
    }

    public PartTemplate barrelPart(String englishName, String path, int stackCapacity) {
        return MIParts.BARREL.of(englishName, path, stackCapacity);
    }

    public PartTemplate blockPart(String materialSet) {
        MaterialBlockSet blockSet = MaterialBlockSet.getByName(materialSet);
        if (blockSet == null) {
            throw new IllegalArgumentException("No such Material Block Set: " + materialSet);
        }
        return MIParts.BLOCK.of(blockSet);
    }

    public PartTemplate machineCasing(String englishName, String path) {
        return MIParts.MACHINE_CASING.of(englishName, path);
    }

    public PartTemplate machineCasing(String englishName, String path, float resistance) {
        return MIParts.MACHINE_CASING.of(englishName, path, resistance);
    }

    public PartTemplate machineCasing(float resistance) {
        return MIParts.MACHINE_CASING.of(resistance);
    }

    public PartTemplate machineCasing() {
        return MIParts.MACHINE_CASING.of();
    }

    public PartTemplate pipeCasing(float resistance) {
        return MIParts.MACHINE_CASING_PIPE.asPipe(resistance);
    }

    public PartTemplate pipeCasing() {
        return MIParts.MACHINE_CASING_PIPE.asPipe();
    }

    public PartTemplate specialCasing(String englishName, String path) {
        return MIParts.MACHINE_CASING_SPECIAL.of(englishName, path);
    }

    public PartTemplate specialCasing(String englishName, String path, float resistance) {
        return MIParts.MACHINE_CASING_SPECIAL.of(englishName, path, resistance);
    }

    public PartTemplate orePart(JsonObject json, ResourceLocation stoneType) {
        OrePart act = new OrePart(stoneType);

        int minXp = json.has("min_xp") ? json.get("min_xp").getAsInt() : 0;
        int maxXp = json.has("max_xp") ? json.get("max_xp").getAsInt() : 0;
        MaterialOreSet oreSet = MaterialOreSet.getByName(json.get("ore_set").getAsString());

        if (oreSet == null) {
            throw new IllegalArgumentException("No such Material Ore Set: " + json.get("ore_set").getAsString());
        }

        boolean generate = !json.has("generate") || json.get("generate").getAsBoolean();
        if (generate) {
            TagKey<Biome> biomeTag;
            if (json.has("biome_tag")) {
                biomeTag = TagKey.create(Registries.BIOME, ResourceLocation.parse(json.get("biome_tag").getAsString()));
            } else {
                biomeTag = BiomeTags.IS_OVERWORLD;
            }
            int veinSize = json.get("vein_size").getAsInt();
            int veinPerChunk = json.get("veins_per_chunk").getAsInt();
            int maxY = json.get("max_y").getAsInt();
            return act.of(UniformInt.of(minXp, maxXp), veinSize, veinPerChunk, maxY, oreSet, biomeTag);
        } else {
            return act.of(UniformInt.of(minXp, maxXp), oreSet);
        }
    }

    public PartTemplate rawMetalPart(String materialSet, boolean block) {
        RawMetalPart act;
        if (block) {
            act = MIParts.RAW_METAL_BLOCK;
        } else {
            act = MIParts.RAW_METAL;
        }
        return act.of(MaterialRawSet.getByName(materialSet));
    }

    public PartTemplate tankPart(int bucketCapacity) {
        return MIParts.TANK.of(bucketCapacity);
    }

    public PartTemplate tankPart(String englishName, String path, int bucketCapacity) {
        return MIParts.TANK.of(englishName, path, bucketCapacity);
    }

    public PartTemplate controlRodPart(int maxTemperature, double heatConduction, double thermalAbsorbProba, double fastAbsorbProba,
            double thermalScatteringProba, double fastScatteringProba, NuclearConstant.ScatteringType scatteringType, double size) {
        return MIParts.CONTROL_ROD.of(maxTemperature, heatConduction, thermalAbsorbProba, fastAbsorbProba, thermalScatteringProba,
                fastScatteringProba, scatteringType, size);
    }
}
