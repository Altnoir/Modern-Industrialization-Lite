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

package aztech.milite.machines.blockentities;

import aztech.milite.MICapabilities;
import aztech.milite.api.energy.CableTier;
import aztech.milite.api.machine.holder.EnergyComponentHolder;
import aztech.milite.inventory.MIInventory;
import aztech.milite.machines.BEP;
import aztech.milite.machines.MachineBlockEntity;
import aztech.milite.machines.components.EnergyComponent;
import aztech.milite.machines.components.OrientationComponent;
import aztech.milite.machines.components.RedstoneControlComponent;
import aztech.milite.machines.gui.MachineGuiParameters;
import aztech.milite.machines.guicomponents.EnergyBar;
import aztech.milite.machines.guicomponents.SlotPanel;
import aztech.milite.machines.helper.EnergyHelper;
import aztech.milite.machines.models.MachineCasing;
import aztech.milite.machines.models.MachineModelClientData;
import aztech.milite.thirdparty.fabrictransfer.api.transaction.Transaction;
import aztech.milite.util.Simulation;
import aztech.milite.util.Tickable;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

public abstract class AbstractStorageMachineBlockEntity extends MachineBlockEntity implements Tickable, EnergyComponentHolder {
    protected final EnergyComponent energy;
    private final RedstoneControlComponent redstoneControl;

    protected final IEnergyStorage insertable;
    protected final IEnergyStorage extractable;

    protected final long euCapacity;
    protected final CableTier from, to;

    protected boolean extractableOnOutputDirection;

    public AbstractStorageMachineBlockEntity(BEP bep, CableTier from, CableTier to, String name, long euCapacity) {
        this(bep, from, to, name, euCapacity, true);
    }

    public AbstractStorageMachineBlockEntity(BEP bep, CableTier from, CableTier to, String name, long euCapacity,
            boolean extractableOnOutputDirection) {
        super(bep, new MachineGuiParameters.Builder(name, false).build(), OrientationComponent.Params.noFacing(false, false));

        this.from = from;
        this.to = to;
        this.euCapacity = euCapacity;

        this.energy = new EnergyComponent(this, euCapacity);
        this.redstoneControl = new RedstoneControlComponent();

        this.insertable = energy.buildInsertable();
        this.extractable = new IEnergyStorage() {
            private final IEnergyStorage delegate = energy.buildExtractable();

            @Override
            public int receiveEnergy(int maxReceive, boolean simulate) {
                return delegate.receiveEnergy(maxReceive, simulate);
            }

            @Override
            public int extractEnergy(int maxExtract, boolean simulate) {
                if (!redstoneControl.doAllowNormalOperation(AbstractStorageMachineBlockEntity.this)) {
                    return 0;
                }
                return delegate.extractEnergy(maxExtract, simulate);
            }

            @Override
            public int getEnergyStored() {
                return delegate.getEnergyStored();
            }

            @Override
            public int getMaxEnergyStored() {
                return delegate.getMaxEnergyStored();
            }

            @Override
            public boolean canExtract() {
                return delegate.canExtract();
            }

            @Override
            public boolean canReceive() {
                return delegate.canReceive();
            }
        };

        this.registerComponents(energy, redstoneControl);

        EnergyBar.Params energyBarParams = new EnergyBar.Params(76, 39);
        registerGuiComponent(new EnergyBar(energyBarParams, energy::getEu, energy::getCapacity),
                new SlotPanel(this).withRedstoneControl(redstoneControl));

        this.extractableOnOutputDirection = extractableOnOutputDirection;
    }

    @Override
    public MIInventory getInventory() {
        return MIInventory.EMPTY;
    }

    public static MachineCasing getCasingFromTier(CableTier from, CableTier to) {
        if (from.rf > to.rf) {
            return from.casing;
        } else {
            return to.casing;
        }
    }

    @Override
    public MachineModelClientData getMachineModelData() {
        MachineModelClientData data = new MachineModelClientData();
        orientation.writeModelData(data);
        return data;
    }

    @Override
    public void tick() {
        if (!level.isClientSide()) {
            if (extractableOnOutputDirection) {
                EnergyHelper.autoOutput(this, orientation, extractable);
            } else {
                for (Direction side : Direction.values()) {
                    if (side == orientation.outputDirection) {
                        continue;
                    }

                    EnergyHelper.autoOutput(this, side, extractable);
                }
            }
        }
    }

    @Override
    public EnergyComponent getEnergyComponent() {
        return energy;
    }

    @Override
    protected ItemInteractionResult useItemOn(Player player, InteractionHand hand, Direction face) {
        var energyItem = player.getItemInHand(hand).getCapability(Capabilities.EnergyStorage.ITEM);
        int stackSize = player.getItemInHand(hand).getCount();
        if (energyItem != null) {
            if (!player.level().isClientSide()) {
                boolean insertedSomething = false;

                for (int i = 0; i < 10000; ++i) { // Try up to 10000 times to bypass I/O limits
                    try (Transaction transaction = Transaction.openRoot()) {
                        int inserted = energyItem.receiveEnergy((int) Math.min(Integer.MAX_VALUE, energy.getEu() / stackSize), false);

                        if (inserted == 0) {
                            break;
                        } else {
                            insertedSomething = true;
                        }

                        energy.consumeEu((long) inserted * stackSize, Simulation.ACT);
                        transaction.commit();
                    }
                }

                if (!insertedSomething) {
                    for (int i = 0; i < 10000; ++i) { // Try up to 10000 times to bypass I/O limits
                        try (Transaction transaction = Transaction.openRoot()) {
                            int extracted = energyItem.extractEnergy(
                                    (int) Math.min(Integer.MAX_VALUE, energy.getRemainingCapacity() / stackSize), false);

                            if (extracted == 0) {
                                break;
                            }

                            energy.insertEu((long) extracted * stackSize, Simulation.ACT);
                            transaction.commit();
                        }
                    }
                }
            }
            return ItemInteractionResult.sidedSuccess(player.level().isClientSide());
        }
        var result = super.useItemOn(player, hand, face);
        if (!result.consumesAction()) {
            result = redstoneControl.onUse(this, player, hand);
        }
        return result;
    }

    public static void registerEnergyApi(BlockEntityType<?> bet) {
        MICapabilities.onEvent(event -> {
            event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, bet, (be, direction) -> {
                AbstractStorageMachineBlockEntity abe = (AbstractStorageMachineBlockEntity) be;

                if (abe.extractableOnOutputDirection) {
                    if (abe.orientation.outputDirection == direction) {
                        return abe.extractable;
                    } else {
                        return abe.insertable;
                    }
                } else {
                    if (abe.orientation.outputDirection == direction) {
                        return abe.insertable;
                    } else {
                        return abe.extractable;
                    }
                }
            });
        });
    }
}
