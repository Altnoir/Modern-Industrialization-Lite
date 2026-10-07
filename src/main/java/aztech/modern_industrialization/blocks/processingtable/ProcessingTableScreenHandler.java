/*
 * MIT License
 *
 * Copyright (c) 2026 Altnoir Null々
 * Copyright (c) 2020 Azercoco & Technici4n
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package aztech.modern_industrialization.blocks.processingtable;

import aztech.modern_industrialization.MIBlock;
import aztech.modern_industrialization.MIRegistries;
import aztech.modern_industrialization.inventory.MoveRecipeHandler;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

/**
 * The Processing Table: the player puts a machine casing and a characterising item in the two input slots and takes
 * the resulting machine from the output slot. Input order does not matter.
 */
public class ProcessingTableScreenHandler extends AbstractContainerMenu implements MoveRecipeHandler {
    /**
     * Slot positions, taken from the processing_table.png background.
     */
    public static final int FIRST_INPUT_X = 27, FIRST_INPUT_Y = 47;
    public static final int SECOND_INPUT_X = 76, SECOND_INPUT_Y = 47;
    public static final int OUTPUT_X = 134, OUTPUT_Y = 47;

    public final Slot firstInput;
    public final Slot secondInput;
    public final Slot output;

    private final ContainerLevelAccess context;
    private final Level world;
    private final Player player;
    private long lastSoundTime = 0;

    private ItemStack firstCache = ItemStack.EMPTY, secondCache = ItemStack.EMPTY;

    @Nullable
    private RecipeHolder<ProcessingTableRecipe> lastCraftedRecipe;

    public ProcessingTableScreenHandler(int syncId, Inventory playerInventory) {
        this(syncId, playerInventory, ContainerLevelAccess.NULL);
    }

    public ProcessingTableScreenHandler(int syncId, Inventory playerInventory, ContainerLevelAccess context) {
        super(MIRegistries.PROCESSING_TABLE_MENU.get(), syncId);
        this.context = context;
        this.world = playerInventory.player.level();
        this.player = playerInventory.player;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(playerInventory, i * 9 + j + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (int j = 0; j < 9; j++) {
            this.addSlot(new Slot(playerInventory, j, 8 + j * 18, 58 + 84));
        }

        this.firstInput = inputSlot(FIRST_INPUT_X, FIRST_INPUT_Y);
        this.secondInput = inputSlot(SECOND_INPUT_X, SECOND_INPUT_Y);

        // Note: don't use new SimpleInventory(1), as ResultContainer always returns the full stack in removeItem!
        // This ensures that the whole item is always removed from the slot, even if someone right-clicks the output slot.
        this.output = new Slot(new ResultContainer(), 0, OUTPUT_X, OUTPUT_Y) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }

            @Override
            public void onTake(Player player, ItemStack stack) {
                ProcessingTableScreenHandler.this.onCraft();
                // Don't play the sound multiple times within the same tick
                context.execute((world, pos) -> {
                    if (lastSoundTime < world.getGameTime()) {
                        world.playSound(null, pos, SoundEvents.SMITHING_TABLE_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
                        lastSoundTime = world.getGameTime();
                    }
                });
            }
        };

        this.addSlot(firstInput);
        this.addSlot(secondInput);
        this.addSlot(output);
    }

    /**
     * @return a single-slot input container that notifies this menu when its contents change
     */
    private Slot inputSlot(int x, int y) {
        return new Slot(new SimpleContainer(1) {
            @Override
            public void setChanged() {
                super.setChanged();
                ProcessingTableScreenHandler.this.slotsChanged(this);
            }
        }, 0, x, y);
    }

    @Nullable
    public RecipeHolder<ProcessingTableRecipe> getLastCraftedRecipe() {
        return lastCraftedRecipe;
    }

    @Override
    public void slotsChanged(Container inventory) {
        if (!ItemStack.matches(this.firstCache, firstInput.getItem()) || !ItemStack.matches(this.secondCache, secondInput.getItem())) {
            updateStatus();
        }

        super.slotsChanged(inventory);
    }

    public void updateStatus() {
        this.firstCache = firstInput.getItem().copy();
        this.secondCache = secondInput.getItem().copy();
        populateResult();
    }

    private void populateResult() {
        var recipe = findRecipe();
        this.output.set(recipe == null ? ItemStack.EMPTY : recipe.value().result().copy());
        this.broadcastChanges();
    }

    @Nullable
    private RecipeHolder<ProcessingTableRecipe> findRecipe() {
        ItemStack first = firstInput.getItem();
        ItemStack second = secondInput.getItem();
        if (first.isEmpty() || second.isEmpty()) {
            return null;
        }

        for (var holder : this.world.getRecipeManager().getAllRecipesFor(MIRegistries.PROCESSING_TABLE_RECIPE_TYPE.get())) {
            if (holder.value().matches(first, second)) {
                return holder;
            }
        }
        return null;
    }

    private void onCraft() {
        var recipe = findRecipe();
        if (recipe == null) {
            return;
        }
        lastCraftedRecipe = recipe;
        firstInput.getItem().shrink(1);
        secondInput.getItem().shrink(1);
        firstInput.setChanged();
        secondInput.setChanged();
        updateStatus();
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack itemStack2 = slot.getItem();
            itemStack = itemStack2.copy();
            if (index == 2) {
                // Output slot: move the crafted machine to the player inventory.
                if (!this.moveItemStackTo(itemStack2, 3, 39, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(itemStack2, itemStack);
            } else if (index < 3) {
                // An input slot: send it back to the player inventory.
                if (!this.moveItemStackTo(itemStack2, 3, 39, false)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // Player inventory: try the input slots first, then the other inventory section.
                if (!this.moveItemStackTo(itemStack2, 0, 2, false)) {
                    if (index < 30) {
                        if (!this.moveItemStackTo(itemStack2, 30, 39, false)) {
                            return ItemStack.EMPTY;
                        }
                    } else if (!this.moveItemStackTo(itemStack2, 3, 30, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            }

            if (itemStack2.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (itemStack2.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, itemStack2);
            this.broadcastChanges();
        }

        return itemStack;
    }

    @Override
    public boolean stillValid(Player player) {
        return this.context.evaluate((level, pos) -> {
            return level.getBlockState(pos).is(MIBlock.PROCESSING_TABLE.asBlock()) && player.canInteractWithBlock(pos, 4.0);
        }, true);
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.context.execute((world, blockPos) -> {
            this.clearContainer(player, this.firstInput.container);
            this.clearContainer(player, this.secondInput.container);
        });
    }

    @Override
    public void moveRecipe(ResourceLocation recipeId, int fillAction, int amount) {
        var holder = world.getRecipeManager().getAllRecipesFor(MIRegistries.PROCESSING_TABLE_RECIPE_TYPE.get()).stream()
                .filter(r -> r.id().equals(recipeId)).findFirst().orElse(null);
        if (holder == null) {
            return;
        }

        var recipe = holder.value();
        // The recipe viewer only knows about the ingredients, so put them in the two input slots directly.
        if (!tryPlace(recipe.first()) || !tryPlace(recipe.second())) {
            return;
        }
        populateResult();
        if (fillAction == 1) {
            clicked(output.index, 0, net.minecraft.world.inventory.ClickType.PICKUP, player);
        } else if (fillAction == 2) {
            clicked(output.index, 0, net.minecraft.world.inventory.ClickType.QUICK_MOVE, player);
        }
    }

    /**
     * Move one matching item from the player inventory into the first free input slot.
     *
     * @return whether a matching item was found and moved
     */
    private boolean tryPlace(net.minecraft.world.item.crafting.Ingredient ingredient) {
        List<Slot> inputs = List.of(firstInput, secondInput);
        for (var input : inputs) {
            if (input.hasItem() && ingredient.test(input.getItem())) {
                return true;
            }
        }
        for (var input : inputs) {
            if (input.hasItem()) {
                continue;
            }
            for (int index = 3; index < 39; index++) {
                var slot = slots.get(index);
                if (ingredient.test(slot.getItem())) {
                    input.set(slot.remove(1));
                    return true;
                }
            }
        }
        return false;
    }
}
