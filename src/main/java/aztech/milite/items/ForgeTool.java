package aztech.milite.items;

import aztech.milite.datagen.tag.TagsToGenerate;
import aztech.milite.MI;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.level.block.Block;

import java.util.Locale;

public class ForgeTool extends TieredItem {
    public static final TagKey<Item> TAG = TagKey.create(BuiltInRegistries.ITEM.key(), MI.id("forge_hammer_tools"));

    public ForgeTool(Tier material, Properties p) {
        super(forgeHammerMaterial(material), p.stacksTo(1));
        TagsToGenerate.generateTag(TAG, this, "Forge Hammer Tools");
    }

    public boolean isValidRepairItem(ItemStack stack, ItemStack repairCandidate) {
        return false;
    }

    private static Tier forgeHammerMaterial(Tier normalTier) {
        return new Tier() {
            @Override
            public int getUses() {
                return (normalTier.getUses() * 20) / 3;
            }

            @Override
            public float getSpeed() {
                return normalTier.getSpeed();
            }

            @Override
            public float getAttackDamageBonus() {
                return normalTier.getAttackDamageBonus();
            }

            @Override
            public TagKey<Block> getIncorrectBlocksForDrops() {
                return normalTier.getIncorrectBlocksForDrops();
            }

            @Override
            public int getEnchantmentValue() {
                return normalTier.getEnchantmentValue();
            }

            @Override
            public Ingredient getRepairIngredient() {
                return normalTier.getRepairIngredient();
            }

            @Override
            public String toString() {
                return normalTier.toString().toLowerCase(Locale.ROOT) + "_forge_tool";
            }
        };
    }

    public static Tier STEEL = new Tier() {
        @Override
        public int getUses() {
            return 650;
        }

        @Override
        public float getSpeed() {
            return 7.0F;
        }

        @Override
        public float getAttackDamageBonus() {
            return 2.5F;
        }

        @Override
        public TagKey<Block> getIncorrectBlocksForDrops() {
            return BlockTags.create(MI.id("incorrect_for_steel_tool")); // this probably doesn't matter...
        }

        @Override
        public int getEnchantmentValue() {
            return 16;
        }

        @Override
        public Ingredient getRepairIngredient() {
            return Ingredient.of(TAG);
        }

        @Override
        public String toString() {
            return "milite:steel";
        }
    };
}
