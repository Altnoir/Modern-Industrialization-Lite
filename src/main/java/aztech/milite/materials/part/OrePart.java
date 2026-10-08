package aztech.milite.materials.part;

import static aztech.milite.materials.property.MaterialProperty.MAIN_PART;

import aztech.milite.blocks.OreBlock;
import aztech.milite.datagen.dynreg.DynamicRegistryDatagen;
import aztech.milite.datagen.loot.MIBlockLoot;
import aztech.milite.datagen.tag.TagsToGenerate;
import aztech.milite.definition.BlockDefinition;
import aztech.milite.items.SortOrder;
import aztech.milite.materials.set.MaterialOreSet;
import aztech.milite.MI;
import aztech.milite.MIBlock;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.placement.OrePlacements;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.world.BiomeModifiers;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class OrePart implements PartKeyProvider {
    public static final ResourceLocation TYPE_STONE = ResourceLocation.fromNamespaceAndPath("minecraft", "stone");
    public static final ResourceLocation TYPE_DEEPSLATE = ResourceLocation.fromNamespaceAndPath("minecraft", "deepslate");

    private final ResourceLocation stoneType;
    private final Block stoneBlock;
    private final PartKey key;

    @Override
    public PartKey key() {
        return key;
    }

    public PartTemplate of(UniformInt xpProvider, int veinsPerChunk, int veinSize, int maxYLevel, MaterialOreSet set, TagKey<Biome> biomeTag) {
        return of(new OrePartParams(xpProvider, set, veinsPerChunk, veinSize, maxYLevel, biomeTag));
    }

    public PartTemplate of(int veinsPerChunk, int veinSize, int maxYLevel, MaterialOreSet set) {
        return of(new OrePartParams(UniformInt.of(0, 2), set, veinsPerChunk, veinSize, maxYLevel));
    }

    public PartTemplate of(UniformInt xpProvider, int veinsPerChunk, int veinSize, int maxYLevel, MaterialOreSet set) {
        return of(new OrePartParams(xpProvider, set, veinsPerChunk, veinSize, maxYLevel));
    }

    public PartTemplate of(UniformInt xpProvider, MaterialOreSet set) {
        return of(new OrePartParams(xpProvider, set));
    }

    public PartTemplate of(MaterialOreSet set) {
        return of(new OrePartParams(UniformInt.of(0, 0), set));
    }

    public OrePart(ResourceLocation stoneType) {
        this.stoneType = stoneType;
        if (stoneType.equals(TYPE_STONE)) {
            key = new PartKey("ore");
        } else {
            key = new PartKey("ore_%s".formatted(stoneType.getPath()));
        }
        stoneBlock = BuiltInRegistries.BLOCK.getOrThrow(ResourceKey.create(Registries.BLOCK, stoneType));
    }

    public PartTemplate of(OrePartParams oreParams) {
        String displayName;
        if (stoneType.equals(TYPE_STONE)) {
            displayName = "%s Ore";
        } else {
            // Poor man's English name
            String stoneName = StringUtils.capitalize(stoneType.getPath().replace('-', ' ').replace('_', ' '));
            displayName = stoneName + " %s Ore";
        }
        return new PartTemplate(displayName, key)
                .withRegister((partContext, part, itemPath, itemId, itemTag, englishName) -> {

                    PartKey mainPartKey = partContext.get(MAIN_PART).key();
                    PartKeyProvider partToDrop;
                    if (mainPartKey.equals(MIParts.INGOT.key())) {
                        partToDrop = MIParts.RAW_METAL;
                    } else if (mainPartKey.equals(MIParts.DUST.key())) {
                        partToDrop = MIParts.DUST;
                    } else if (mainPartKey.equals(MIParts.GEM.key())) {
                        partToDrop = MIParts.GEM;
                    } else {
                        throw new UnsupportedOperationException("Could not find matching main part.");
                    }
                    MaterialItemPart lootPart = partContext.getMaterialPart(partToDrop);
                    if (lootPart == null) {
                        throw new IllegalStateException(String.format("Tried to generate loot for %s ore. "
                                + "Since the main part is \"%s\", the drop should be \"%s\" but it could not be found. "
                                + "Either add a \"%s\" part, or change the main part of %s.",
                                partContext.getMaterialName(),
                                mainPartKey,
                                partToDrop.key(),
                                partToDrop.key(),
                                partContext.getMaterialName()));
                    }

                    BlockDefinition<OreBlock> oreBlockBlockDefinition;
                    oreBlockBlockDefinition = MIBlock.block(
                            englishName,
                            itemPath,
                            MIBlock.BlockDefinitionParams.of(BlockBehaviour.Properties.ofFullCopy(stoneBlock))
                                    .withBlockConstructor(s -> new OreBlock(s, oreParams, partContext.getMaterialName()))
                                    .withLoot(new MIBlockLoot.Ore(lootPart.getItemId()))
                                    .sortOrder(SortOrder.ORES.and(partContext.getMaterialName()))
                                    .destroyTime(stoneBlock.defaultDestroyTime() + 1.5f)
                                    .requiresCorrectToolForDrops());

                    // Sanity check: Ensure that ores don't drop xp, iff the main part is an ingot
                    // (i.e. the drop is raw ore).
                    if (mainPartKey.equals(MIParts.INGOT.key()) != (oreParams.xpDropped.getMaxValue() == 0)) {
                        throw new IllegalArgumentException("Mismatch between raw ore and xp drops for material: " + partContext.getMaterialName());
                    }

                    String tag = "c:ores/" + partContext.getMaterialName();

                    TagsToGenerate.generateTag(tag, oreBlockBlockDefinition, partContext.getMaterialEnglishName() + " Ores");
                    TagsToGenerate.addTagToTag(tag, Tags.Items.ORES.location().toString(), "Ores");
                    if (stoneType.equals(TYPE_DEEPSLATE)) {
                        TagsToGenerate.generateTagNoTranslation(Tags.Items.ORES_IN_GROUND_DEEPSLATE, oreBlockBlockDefinition);
                    } else if (stoneType.equals(TYPE_STONE)) {
                        TagsToGenerate.generateTagNoTranslation(Tags.Items.ORES_IN_GROUND_STONE, oreBlockBlockDefinition);
                    }

                    if (oreParams.generate) {
                        String genIdPrefix = stoneType.equals(TYPE_STONE) ? "" : "%s_".formatted(stoneType.getPath());
                        ResourceLocation oreGenId = MI.id(
                                genIdPrefix + "ore_generator_" + partContext.getMaterialName());

                        var featureKey = ResourceKey.create(Registries.CONFIGURED_FEATURE, oreGenId);
                        var placedFeatureKey = ResourceKey.create(Registries.PLACED_FEATURE, oreGenId);
                        var modifierKey = ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS, oreGenId);

                        DynamicRegistryDatagen.addAction(() -> {
                            RuleTest ruleTest;
                            if (stoneType.equals(TYPE_STONE)) {
                                ruleTest = new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES);
                            } else if (stoneType.equals(TYPE_DEEPSLATE)) {
                                ruleTest = new TagMatchTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES);
                            } else {
                                ruleTest = new BlockMatchTest(stoneBlock);
                            }
                            var target = List.of(
                                    OreConfiguration.target(ruleTest, oreBlockBlockDefinition.asBlock().defaultBlockState()));

                            DynamicRegistryDatagen.add(Registries.CONFIGURED_FEATURE, context -> {
                                FeatureUtils.register(context, featureKey, Feature.ORE, new OreConfiguration(target, oreParams.veinSize));
                            });

                            DynamicRegistryDatagen.add(Registries.PLACED_FEATURE, context -> {
                                var holder = context.lookup(Registries.CONFIGURED_FEATURE).getOrThrow(featureKey);
                                var placement = OrePlacements.commonOrePlacement(
                                        oreParams.veinsPerChunk,
                                        HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.absolute(oreParams.maxYLevel)));
                                PlacementUtils.register(context, placedFeatureKey, holder, placement);
                            });

                            DynamicRegistryDatagen.add(NeoForgeRegistries.Keys.BIOME_MODIFIERS, context -> {
                                var modifier = new BiomeModifiers.AddFeaturesBiomeModifier(
                                        context.lookup(Registries.BIOME).getOrThrow(oreParams.biomeTag),
                                        HolderSet.direct(context.lookup(Registries.PLACED_FEATURE).getOrThrow(placedFeatureKey)),
                                        GenerationStep.Decoration.UNDERGROUND_ORES);
                                context.register(modifierKey, modifier);
                            });
                        });
                    }

                })
                .withTexture(new TextureGenParams.Ore(stoneType, oreParams.set))
                .withCustomPath((stoneType.equals(TYPE_STONE) ? "" : "%s_".formatted(stoneType.getPath())) + "%s_ore", "ores/%s");
    }

    public static List<PartTemplate> ofAll(OrePartParams params) {
        return List.of(new OrePart(TYPE_DEEPSLATE).of(params), new OrePart(TYPE_STONE).of(params));
    }

    public static List<PartTemplate> ofAll(UniformInt xpProvider, int veinsPerChunk, int veinSize, int maxYLevel, MaterialOreSet set) {
        return ofAll(new OrePartParams(xpProvider, set, veinsPerChunk, veinSize, maxYLevel));
    }

    public static List<PartTemplate> ofAll(int veinsPerChunk, int veinSize, int maxYLevel, MaterialOreSet set) {
        return ofAll(new OrePartParams(UniformInt.of(0, 0), set, veinsPerChunk, veinSize, maxYLevel));
    }

    public record OrePartParams(UniformInt xpDropped, MaterialOreSet set, boolean generate, TagKey<Biome> biomeTag,
                                int veinsPerChunk, int veinSize, int maxYLevel) {
            private OrePartParams(UniformInt xpDropped, MaterialOreSet set, boolean generate, int veinsPerChunk, int veinSize, int maxYLevel,
                                  TagKey<Biome> biomeTag) {

                this(xpDropped, set, generate, biomeTag, veinsPerChunk, veinSize, maxYLevel);
            }

            public OrePartParams(UniformInt xpDropped, MaterialOreSet set) {
                this(xpDropped, set, false, 0, 0, 0, BiomeTags.IS_OVERWORLD);
            }

            public OrePartParams(UniformInt xpDropped, MaterialOreSet set, int veinsPerChunk, int veinSize, int maxYLevel) {
                this(xpDropped, set, true, veinsPerChunk, veinSize, maxYLevel, BiomeTags.IS_OVERWORLD);
            }

            public OrePartParams(UniformInt xpDropped, MaterialOreSet set, int veinsPerChunk, int veinSize, int maxYLevel, TagKey<Biome> biomeTag) {
                this(xpDropped, set, true, veinsPerChunk, veinSize, maxYLevel, biomeTag);
            }
        }
}
