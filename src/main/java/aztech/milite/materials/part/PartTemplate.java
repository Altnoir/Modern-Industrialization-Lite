package aztech.milite.materials.part;

import aztech.milite.MIBlock;
import aztech.milite.MIItem;
import aztech.milite.datagen.tag.TagsToGenerate;
import aztech.milite.definition.ItemDefinition;
import aztech.milite.items.SortOrder;
import aztech.milite.machines.init.MachineLevel;
import aztech.milite.materials.MaterialBuilder;
import aztech.milite.materials.MaterialProgression;
import aztech.milite.util.TagHelper;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.List;

public class PartTemplate implements PartKeyProvider {
    private final PartEnglishNameFormatter englishNameFormatter;
    private final PartKey partKey;
    private final Register register;
    private final TextureGenParams textureGenParams;
    private final PartItemPathFormatter itemPathFormatter;

    public PartTemplate(PartEnglishNameFormatter englishNameFormatter, String key) {
        this(englishNameFormatter, key, (partContext, part, itemPath, itemId, itemTag, englishName) -> {
            var item = createSimpleItem(englishName, itemPath, partContext, part);
            setupTag(partContext, part, itemTag, item);
        }, new TextureGenParams.SimpleRecoloredItem());
    }

    public PartTemplate(PartEnglishNameFormatter englishNameFormatter, PartKey key) {
        this(englishNameFormatter, key, (partContext, part, itemPath, itemId, itemTag, englishName) -> {
            var item = createSimpleItem(englishName, itemPath, partContext, part);
            setupTag(partContext, part, itemTag, item);
        }, new TextureGenParams.SimpleRecoloredItem(), new PartItemPathFormatter.Default());
    }

    public PartTemplate(String englishName, String key) {
        this(new PartEnglishNameFormatter.Default(englishName), key);
    }

    public PartTemplate(String englishName, PartKey key) {
        this(new PartEnglishNameFormatter.Default(englishName), key);
    }

    private PartTemplate(PartEnglishNameFormatter englishNameFormatter, String key, Register register, TextureGenParams textureGenParams) {
        this(englishNameFormatter, new PartKey(key), register, textureGenParams, new PartItemPathFormatter.Default());
    }

    private PartTemplate(PartEnglishNameFormatter englishNameFormatter, PartKey key, Register register, TextureGenParams textureGenParams,
                         PartItemPathFormatter itemPathFormatter) {
        this.partKey = key;
        this.englishNameFormatter = englishNameFormatter;
        this.register = register;
        this.textureGenParams = textureGenParams;
        this.itemPathFormatter = itemPathFormatter;
    }

    public static ItemDefinition<Item> createSimpleItem(String englishName, String itemPath, MaterialBuilder.PartContext partContext, PartKey part) {
        return MIItem.item(englishName, itemPath, SortOrder.MATERIALS
                .and(MaterialProgression.rankOf(partContext.getMaterialName()))
                .and(PartProgression.rankOf(part)));
    }

    private static void setupTag(MaterialBuilder.PartContext context, PartKey part, String itemTag, ItemLike item) {
        for (PartKey partTagged : MIParts.TAGGED_PARTS) {
            if (partTagged.equals(part)) {
                var generatedTag = itemTag.replaceFirst("#", "");
                TagsToGenerate.generateTag(generatedTag, item, getTagEnglishName(context, itemTag));

                var categoryTag = MIParts.CATEGORY_TAGS.get(partTagged.key());
                if (categoryTag != null) {
                    TagsToGenerate.addTagToTag(generatedTag, categoryTag.tag(), categoryTag.englishName());
                }
            }
        }
    }

    private static String getTagEnglishName(MaterialBuilder.PartContext context, String tag) {
        var parts = tag.split(":")[1].split("/");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Tag " + tag + " has more than 2 slash-separated parts");
        }

        var sb = new StringBuilder();

        var part = parts[0];
        var material = parts[1];
        boolean rawOre = false;

        if (material.startsWith("raw_")) {
            // Hacky fix for raw ores...
            sb.append("Raw ");
            material = material.substring(4);
            part = "ores";
        }

        if (!material.equals(context.getMaterialName())) {
            throw new IllegalArgumentException("Tag " + tag + " does not contain the material name after slash: " + context.getMaterialName());
        }

        part = part.replace('_', ' ');

        sb.append(context.getMaterialEnglishName());
        sb.append(' ');

        boolean capitalize = true;

        for (char c : part.toCharArray()) {
            if (c == ' ') {
                sb.append(c);
                capitalize = true;
            } else if (capitalize) {
                sb.append(Character.toUpperCase(c));
                capitalize = false;
            } else {
                sb.append(c);
            }
        }

        return sb.toString();
    }

    private static int materialPartOrder(String materialName, String part) {
        List<String> order = switch (materialName) {
            case "bronze", "stainless_steel", "titanium", "iridium" ->
                    List.of("machine_casing_special", "machine_casing_pipe", "machine_casing");
            case "aluminum" -> List.of("machine_casing_special", "machine_casing");
            default -> List.of();
        };
        int index = order.indexOf(part);
        return index < 0 ? order.size() : index;
    }

    public PartTemplate asBlock(SortOrder sortOrder, TextureGenParams textureGenParams, float hardness, float resistance, int miningLevel) {
        return asBlock(sortOrder, textureGenParams, hardness, resistance, miningLevel, true);
    }

    private static SortOrder blockSortOrder(SortOrder sortOrder, String materialName, PartKey part) {
        return blockSortOrder(sortOrder, materialName, part, false);
    }

    private static SortOrder blockSortOrder(SortOrder sortOrder, String materialName, PartKey part, boolean absoluteSortOrder) {
        int rank = MaterialProgression.rankOf(materialName);
        if (absoluteSortOrder || sortOrder.groupIndex() != SortOrder.STRUCTURES.groupIndex()) {
            return sortOrder.and(rank);
        }
        MachineLevel level = MachineLevel.byNameOrNull(materialName);
        return sortOrder.withLevel(level == null ? MachineLevel.values().length : level.ordinal())
                .and(materialPartOrder(materialName, part.key)).and(rank);
    }

    public PartTemplate asPipeBlock(SortOrder sortOrder, float hardness, float resistance) {
        Register pipeBlockRegister = (partContext, part, itemPath, itemId, itemTag, itemEnglishName) -> {

            var blockDefinition = MIBlock.block(
                    itemEnglishName,
                    itemPath,
                    MIBlock.BlockDefinitionParams.defaultStone()
                            .clearTags()
                            .addMoreTags(TagHelper.getMiningLevelTag(1))
                            .sortOrder(blockSortOrder(sortOrder, partContext.getMaterialName(), part))
                            .destroyTime(hardness)
                            .explosionResistance(resistance)
                            .withBlockConstructor(RotatedPillarBlock::new)
                            .withModel((block, gen) -> {
                                String name = gen.name(block);
                                var model = gen.models().cubeAll(name, gen.blockTexture(name + "/particle"));
                                var variants = gen.getVariantBuilder(block);

                                variants.partialState().with(BlockStateProperties.AXIS, Direction.Axis.Y)
                                        .modelForState().modelFile(model).addModel();
                                variants.partialState().with(BlockStateProperties.AXIS, Direction.Axis.X)
                                        .modelForState().modelFile(model).rotationX(90).rotationY(90).addModel();
                                variants.partialState().with(BlockStateProperties.AXIS, Direction.Axis.Z)
                                        .modelForState().modelFile(model).rotationX(90).addModel();
                                gen.itemModels().getBuilder(name).parent(model);
                            })

            );

            setupTag(partContext, part, itemTag, blockDefinition);

        };
        return new PartTemplate(englishNameFormatter, partKey, pipeBlockRegister, new TextureGenParams.CasingBlock(), itemPathFormatter);
    }

    public PartTemplate withoutTextureRegister() {
        return this.withTexture(new TextureGenParams.NoTexture());
    }

    public PartTemplate asBlock(SortOrder sortOrder, TextureGenParams textureGenParams) {
        return asBlock(sortOrder, textureGenParams, 5, 6, 1);
    }

    public PartTemplate withRegister(Register register) {
        return new PartTemplate(englishNameFormatter, partKey, register, textureGenParams, itemPathFormatter);
    }

    public PartTemplate withTexture(TextureGenParams textureGenParams) {
        return new PartTemplate(englishNameFormatter, partKey, register, textureGenParams, itemPathFormatter);
    }

    public PartTemplate withOverlay(PartKey normal, String overlay) {
        return withTexture(new TextureGenParams.SimpleRecoloredItem(normal, overlay));
    }

    public PartTemplate withOverlay(PartKeyProvider normal, String overlay) {
        return withOverlay(normal.key(), overlay);
    }

    public PartTemplate withCustomPath(String itemPath, String itemTag) {
        return new PartTemplate(englishNameFormatter, partKey, register, textureGenParams, new PartItemPathFormatter.Overridden(itemPath, itemTag));
    }

    public PartTemplate withCustomPath(String itemPath) {
        return withCustomPath(itemPath, itemPath);
    }

    public TextureGenParams getTextureGenParams() {
        return textureGenParams;
    }

    public MaterialItemPart create(String material, String materialEnglishName) {
        String itemPath = this.itemPathFormatter.getPartItemPath(material, partKey);
        String itemId = this.itemPathFormatter.getPartItemId(material, partKey);
        String itemTag = this.itemPathFormatter.getPartItemTag(material, partKey);
        String itemEnglishName = englishNameFormatter.format(materialEnglishName);

        return new MaterialItemPartImpl(partKey, itemTag, itemId, ctx -> {
            register.register(ctx, partKey, itemPath, itemId, itemTag, itemEnglishName);
        }, this.textureGenParams, true);
    }

    public PartTemplate asBlock(SortOrder sortOrder, TextureGenParams textureGenParams, float hardness, float resistance, int miningLevel,
                                boolean doAllowSpawn) {
        return asBlock(sortOrder, textureGenParams, hardness, resistance, miningLevel, doAllowSpawn, false);
    }

    /** sortOrder 自带等级档，不再按材料补 */
    public PartTemplate asBlock(SortOrder sortOrder, TextureGenParams textureGenParams, float hardness, float resistance, int miningLevel,
                                boolean doAllowSpawn, boolean absoluteSortOrder) {
        Register blockRegister = (partContext, part, itemPath, itemId, itemTag, itemEnglishName) -> {

            var blockParams = MIBlock.BlockDefinitionParams.defaultStone()
                    .clearTags()
                    .addMoreTags(TagHelper.getMiningLevelTag(miningLevel))
                    .sortOrder(blockSortOrder(sortOrder, partContext.getMaterialName(), part, absoluteSortOrder))
                    .destroyTime(hardness)
                    .explosionResistance(resistance);

            if (!doAllowSpawn) {
                blockParams.isValidSpawn((p1, p2, p3, p4) -> false);
            }

            var blockDefinition = MIBlock.block(
                    itemEnglishName, itemPath, blockParams

            );

            setupTag(partContext, part, itemTag, blockDefinition);

        };
        return new PartTemplate(englishNameFormatter, partKey, blockRegister, textureGenParams, itemPathFormatter);
    }

    public PartTemplate asColumnBlock(SortOrder sortOrder) {
        Register columnBlockRegister = (partContext, part, itemPath, itemId, itemTag, itemEnglishName) -> {

            var blockDefinition = MIBlock.block(
                    itemEnglishName,
                    itemPath,
                    MIBlock.BlockDefinitionParams.defaultStone()
                            .clearTags()
                            .addMoreTags(TagHelper.getMiningLevelTag(1))
                            .sortOrder(blockSortOrder(sortOrder, partContext.getMaterialName(), part))
                            .withModel((block, gen) -> {
                                String name = gen.name(block);
                                gen.simpleBlockWithItem(block,
                                        gen.models().cubeColumn(name, gen.blockTexture(name + "_side"), gen.blockTexture(name + "_top")));
                            })
                            .destroyTime(5.0f)
                            .explosionResistance(6.0f)

            );

            setupTag(partContext, part, itemTag, blockDefinition);

        };
        return new PartTemplate(englishNameFormatter, partKey, columnBlockRegister, new TextureGenParams.ColumnBlock(), itemPathFormatter);
    }

    @Override
    public PartKey key() {
        return partKey;
    }

    @FunctionalInterface
    public interface Register {
        void register(MaterialBuilder.PartContext partContext, PartKey part, String itemPath, String itemId, String itemTag, String itemEnglishName);
    }
}
