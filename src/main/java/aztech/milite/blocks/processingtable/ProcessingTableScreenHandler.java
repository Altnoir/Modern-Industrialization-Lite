package aztech.milite.blocks.processingtable;

import aztech.milite.MIBlock;
import aztech.milite.MIRegistries;
import aztech.milite.inventory.MoveRecipeHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProcessingTableScreenHandler extends AbstractContainerMenu implements MoveRecipeHandler {
    public static final int KEY_INPUT_X = 15, KEY_INPUT_Y = 47;
    public static final int GRID_X = 44, GRID_Y = 29, GRID_STEP = 18;
    public static final int OUTPUT_X = 134, OUTPUT_Y = 47;

    public static final int PLAYER_SLOTS = 36, MAIN_SLOTS = 27;
    public static final int KEY_INPUT_INDEX = 36;
    public static final int FIRST_GRID_INPUT_INDEX = KEY_INPUT_INDEX + 1;
    public static final int LAST_GRID_INPUT_INDEX = FIRST_GRID_INPUT_INDEX + ProcessingTableRecipe.SLOT_COUNT - 1;
    public static final int OUTPUT_INDEX = LAST_GRID_INPUT_INDEX + 1;

    public final Slot keyInput;
    public final List<Slot> gridInputs;
    public final Slot output;

    private final ContainerLevelAccess context;
    private final Level world;
    private final Player player;
    private long lastSoundTime = 0;

    private Set<Integer> activeSlots = Set.of();
    private List<ItemStack> inputCache = List.of();
    private boolean returningCellContents = false;

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
                this.addSlot(new Slot(playerInventory, i * 9 + j + 9, 8 + j * 18, 100 + i * 18));
            }
        }

        for (int j = 0; j < 9; j++) {
            this.addSlot(new Slot(playerInventory, j, 8 + j * 18, 58 + 100));
        }

        this.keyInput = inputSlot(KEY_INPUT_X, KEY_INPUT_Y);
        List<Slot> gridInputs = new ArrayList<>(ProcessingTableRecipe.SLOT_COUNT);
        for (int slot = ProcessingTableRecipe.MIN_SLOT; slot <= ProcessingTableRecipe.MAX_SLOT; slot++) {
            int column = (slot - ProcessingTableRecipe.MIN_SLOT) % 3;
            int row = (slot - ProcessingTableRecipe.MIN_SLOT) / 3;
            gridInputs.add(gridSlot(slot, GRID_X + column * GRID_STEP, GRID_Y + row * GRID_STEP));
        }
        this.gridInputs = List.copyOf(gridInputs);

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

        this.addSlot(keyInput);
        for (Slot cell : gridInputs) {
            this.addSlot(cell);
        }
        this.addSlot(output);
    }

    private Slot inputSlot(int x, int y) {
        return new Slot(notifyingContainer(), 0, x, y);
    }

    private Slot gridSlot(int slot, int x, int y) {
        return new Slot(notifyingContainer(), 0, x, y) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return ProcessingTableScreenHandler.this.activeSlots.contains(slot);
            }

            @Override
            public boolean isActive() {
                return ProcessingTableScreenHandler.this.activeSlots.contains(slot);
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

    public boolean isGridSlotActive(int slot) {
        return activeSlots.contains(slot);
    }

    public boolean isCraftBlocked() {
        return !keyInput.getItem().isEmpty() && output.getItem().isEmpty();
    }

    @Override
    public void slotsChanged(Container inventory) {
        if (inputsChanged()) {
            updateStatus();
        }

        super.slotsChanged(inventory);
    }

    public void updateStatus() {
        this.activeSlots = computeActiveSlots();
        if (!this.world.isClientSide) {
            handBackInactiveCells();
        }
        this.inputCache = inputStacks();
        populateResult();
    }

    private List<ItemStack> inputStacks() {
        List<ItemStack> stacks = new ArrayList<>(ProcessingTableRecipe.SLOT_COUNT + 1);
        stacks.add(keyInput.getItem().copy());
        for (Slot cell : gridInputs) {
            stacks.add(cell.getItem().copy());
        }
        return stacks;
    }

    private boolean inputsChanged() {
        List<ItemStack> stacks = inputStacks();
        if (stacks.size() != inputCache.size()) {
            return true;
        }
        for (int index = 0; index < stacks.size(); index++) {
            if (!ItemStack.matches(inputCache.get(index), stacks.get(index))) {
                return true;
            }
        }
        return false;
    }

    private Set<Integer> computeActiveSlots() {
        ItemStack key = keyInput.getItem();
        if (key.isEmpty()) {
            return Set.of();
        }
        Set<Integer> slots = new HashSet<>();
        for (var holder : recipes()) {
            if (holder.value().matchesCasing(key)) {
                slots.addAll(holder.value().slots().keySet());
            }
        }
        return slots;
    }

    private void handBackInactiveCells() {
        // 防重入
        if (returningCellContents) {
            return;
        }
        returningCellContents = true;
        try {
            for (Slot cell : gridInputs) {
                if (!cell.isActive()) {
                    handBack(cell);
                }
            }
        } finally {
            returningCellContents = false;
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
        if (key.isEmpty()) {
            return null;
        }
        List<ItemStack> grid = new ArrayList<>(ProcessingTableRecipe.SLOT_COUNT);
        for (Slot cell : gridInputs) {
            grid.add(cell.getItem());
        }

        for (var holder : recipes()) {
            if (holder.value().matches(key, grid)) {
                return holder;
            }
        }
        return null;
    }

    protected List<RecipeHolder<ProcessingTableRecipe>> recipes() {
        return this.world.getRecipeManager().getAllRecipesFor(MIRegistries.PROCESSING_TABLE_RECIPE_TYPE.get());
    }

    private void onCraft() {
        var recipe = findRecipe();
        if (recipe == null) {
            return;
        }
        lastCraftedRecipe = recipe;
        keyInput.getItem().shrink(1);
        for (Slot cell : gridInputs) {
            if (cell.hasItem()) {
                cell.getItem().shrink(1);
            }
        }
        keyInput.setChanged();
        for (Slot cell : gridInputs) {
            cell.setChanged();
        }
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
            } else if (index >= KEY_INPUT_INDEX && index <= LAST_GRID_INPUT_INDEX) {
                if (!this.moveItemStackTo(itemStack2, 0, PLAYER_SLOTS, false)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (!this.moveItemStackTo(itemStack2, KEY_INPUT_INDEX, LAST_GRID_INPUT_INDEX + 1, false)) {
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
            for (Slot cell : this.gridInputs) {
                this.clearContainer(player, cell.container);
            }
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
        if (!placeIngredient(keyInput, recipe.casing())) {
            return;
        }
        updateStatus();
        for (var entry : recipe.slots().entrySet()) {
            if (!placeIngredient(gridInputs.get(entry.getKey() - ProcessingTableRecipe.MIN_SLOT), entry.getValue())) {
                return;
            }
        }
        populateResult();
        if (fillAction == 1) {
            clicked(output.index, 0, ClickType.PICKUP, player);
        } else if (fillAction == 2) {
            clicked(output.index, 0, ClickType.QUICK_MOVE, player);
        }
    }

    private boolean placeIngredient(Slot target, Ingredient ingredient) {
        if (target.hasItem()) {
            return ingredient.test(target.getItem());
        }
        return takeFromPlayer(ingredient, target);
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
