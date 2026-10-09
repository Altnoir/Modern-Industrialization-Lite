package aztech.milite.blocks.processingtable;

import aztech.milite.inventory.MoveRecipeHandler;
import aztech.milite.MIBlock;
import aztech.milite.MIRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.SimpleContainer;
import org.jspecify.annotations.Nullable;

import java.util.List;

/**
 * The Processing Table: the player puts a machine casing in the middle input slot to unlock the two side slots, then
 * puts a characterising item in each of them and takes the resulting machine from the output slot. Which side slot
 * holds which of the two characterising items does not matter.
 */
public class ProcessingTableScreenHandler extends AbstractContainerMenu implements MoveRecipeHandler {
    /**
     * Slot positions, taken from the processing_table.png background.
     */
    public static final int LEFT_INPUT_X = 26, SIDE_INPUT_Y = 47;
    public static final int KEY_INPUT_X = 44;
    public static final int RIGHT_INPUT_X = 62;
    public static final int OUTPUT_X = 134, OUTPUT_Y = 47;
    /**
     * The 36 player slots are added first, so the four table slots come after them.
     */
    public static final int PLAYER_SLOTS = 36, MAIN_SLOTS = 27;

    /**
     * The middle slot, which only takes a casing some recipe asks for.
     */
    public final Slot keyInput;
    /**
     * The two outer slots, which stay locked until the key slot holds a casing.
     */
    public final Slot leftInput, rightInput;
    public final Slot output;

    private final ContainerLevelAccess context;
    private final Level world;
    private final Player player;
    private long lastSoundTime = 0;

    private ItemStack keyCache = ItemStack.EMPTY, leftCache = ItemStack.EMPTY, rightCache = ItemStack.EMPTY;
    private boolean sideUnlocked = false;
    private boolean returningSideContents = false;

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

        this.leftInput = sideInputSlot(LEFT_INPUT_X, SIDE_INPUT_Y);
        this.keyInput = inputSlot(KEY_INPUT_X, SIDE_INPUT_Y);
        this.rightInput = sideInputSlot(RIGHT_INPUT_X, SIDE_INPUT_Y);

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

        this.addSlot(leftInput);
        this.addSlot(keyInput);
        this.addSlot(rightInput);
        this.addSlot(output);
    }

    /**
     * @return a single-slot input container that notifies this menu when its contents change
     */
    private Slot inputSlot(int x, int y) {
        return new Slot(notifyingContainer(), 0, x, y);
    }

    /**
     * @return an input slot that refuses everything until the key slot holds a casing
     */
    private Slot sideInputSlot(int x, int y) {
        return new Slot(notifyingContainer(), 0, x, y) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return ProcessingTableScreenHandler.this.sideUnlocked;
            }
        };
    }

    private Container notifyingContainer() {
        return new SimpleContainer(1) {
            @Override
            public void setChanged() {
                super.setChanged();
                ProcessingTableScreenHandler.this.slotsChanged(this);
            }
        };
    }

    @Nullable
    public RecipeHolder<ProcessingTableRecipe> getLastCraftedRecipe() {
        return lastCraftedRecipe;
    }

    /**
     * @return whether the two side slots currently accept items, which is what tells the screen to draw the locking X
     */
    public boolean areSideSlotsUnlocked() {
        return sideUnlocked;
    }

    /**
     * @return whether the player has put something in the key slot that no recipe completes, which is what tells the
     *         screen to draw the crossed out arrow
     */
    public boolean isCraftBlocked() {
        return !keyInput.getItem().isEmpty() && output.getItem().isEmpty();
    }

    @Override
    public void slotsChanged(Container inventory) {
        if (!ItemStack.matches(this.keyCache, keyInput.getItem())
                || !ItemStack.matches(this.leftCache, leftInput.getItem())
                || !ItemStack.matches(this.rightCache, rightInput.getItem())) {
            updateStatus();
        }

        super.slotsChanged(inventory);
    }

    public void updateStatus() {
        this.keyCache = keyInput.getItem().copy();
        this.leftCache = leftInput.getItem().copy();
        this.rightCache = rightInput.getItem().copy();
        this.sideUnlocked = isKeyAccepted();
        if (!this.sideUnlocked && !this.world.isClientSide) {
            // The sides just locked again, so hand back what they were holding instead of leaving it stuck in a slot
            // the player can no longer fill.
            handBackSideContents();
        }
        populateResult();
    }

    private void handBackSideContents() {
        // Handing an item back empties its slot, which brings us back here, so the two slots have to be emptied once
        // and only once.
        if (returningSideContents) {
            return;
        }
        returningSideContents = true;
        try {
            handBack(leftInput);
            handBack(rightInput);
        } finally {
            returningSideContents = false;
        }
    }

    private void handBack(Slot slot) {
        ItemStack stack = slot.getItem();
        if (stack.isEmpty()) {
            return;
        }
        slot.set(ItemStack.EMPTY);
        if (!player.getInventory().add(stack)) {
            player.drop(stack, false);
        }
    }

    private void populateResult() {
        var recipe = findRecipe();
        this.output.set(recipe == null ? ItemStack.EMPTY : recipe.value().result().copy());
        this.broadcastChanges();
    }

    @Nullable
    private RecipeHolder<ProcessingTableRecipe> findRecipe() {
        ItemStack key = keyInput.getItem();
        ItemStack left = leftInput.getItem();
        ItemStack right = rightInput.getItem();
        if (key.isEmpty() || left.isEmpty() || right.isEmpty()) {
            return null;
        }

        for (var holder : recipes()) {
            if (holder.value().matches(key, left, right)) {
                return holder;
            }
        }
        return null;
    }

    /**
     * @return whether the key slot holds an item some recipe asks for, which is all it takes to unlock the sides
     */
    private boolean isKeyAccepted() {
        ItemStack key = keyInput.getItem();
        if (key.isEmpty()) {
            return false;
        }
        for (var holder : recipes()) {
            if (holder.value().matchesKey(key)) {
                return true;
            }
        }
        return false;
    }

    private List<RecipeHolder<ProcessingTableRecipe>> recipes() {
        return this.world.getRecipeManager().getAllRecipesFor(MIRegistries.PROCESSING_TABLE_RECIPE_TYPE.get());
    }

    private void onCraft() {
        var recipe = findRecipe();
        if (recipe == null) {
            return;
        }
        lastCraftedRecipe = recipe;
        keyInput.getItem().shrink(1);
        leftInput.getItem().shrink(1);
        rightInput.getItem().shrink(1);
        keyInput.setChanged();
        leftInput.setChanged();
        rightInput.setChanged();
        updateStatus();
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack itemStack2 = slot.getItem();
            itemStack = itemStack2.copy();
            if (index == output.index) {
                if (!this.moveItemStackTo(itemStack2, 0, PLAYER_SLOTS, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(itemStack2, itemStack);
            } else if (index >= leftInput.index && index <= rightInput.index) {
                if (!this.moveItemStackTo(itemStack2, 0, PLAYER_SLOTS, false)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (!this.moveItemStackTo(itemStack2, leftInput.index, rightInput.index + 1, false)) {
                    if (index < MAIN_SLOTS) {
                        if (!this.moveItemStackTo(itemStack2, MAIN_SLOTS, PLAYER_SLOTS, false)) {
                            return ItemStack.EMPTY;
                        }
                    } else if (!this.moveItemStackTo(itemStack2, 0, MAIN_SLOTS, false)) {
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
            this.clearContainer(player, this.keyInput.container);
            this.clearContainer(player, this.leftInput.container);
            this.clearContainer(player, this.rightInput.container);
        });
    }

    @Override
    public void moveRecipe(ResourceLocation recipeId, int fillAction, int amount) {
        var holder = recipes().stream().filter(r -> r.id().equals(recipeId)).findFirst().orElse(null);
        if (holder == null) {
            return;
        }

        // The recipe viewer only knows about the ingredients, so put them in the input slots directly.
        var recipe = holder.value();
        if (!placeIngredient(keyInput, recipe.key()) || !placeSideIngredient(recipe.first()) || !placeSideIngredient(recipe.second())) {
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
     * @return whether {@code target} already holds a matching item, or a matching item was moved there from the player
     *         inventory
     */
    private boolean placeIngredient(Slot target, Ingredient ingredient) {
        if (target.hasItem()) {
            return ingredient.test(target.getItem());
        }
        return takeFromPlayer(ingredient, target);
    }

    /**
     * @return whether either side slot already holds a matching item, or one was moved into a free side slot
     */
    private boolean placeSideIngredient(Ingredient ingredient) {
        for (Slot side : List.of(leftInput, rightInput)) {
            if (side.hasItem() && ingredient.test(side.getItem())) {
                return true;
            }
        }
        for (Slot side : List.of(leftInput, rightInput)) {
            if (!side.hasItem() && takeFromPlayer(ingredient, side)) {
                return true;
            }
        }
        return false;
    }

    private boolean takeFromPlayer(Ingredient ingredient, Slot target) {
        for (int index = 0; index < PLAYER_SLOTS; index++) {
            var slot = slots.get(index);
            if (ingredient.test(slot.getItem())) {
                target.set(slot.remove(1));
                return true;
            }
        }
        return false;
    }
}
