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

package aztech.milite.machines.guicomponents;

import aztech.milite.MI;
import aztech.milite.machines.gui.GuiComponentServer;
import aztech.milite.thirdparty.fabrictransfer.api.fluid.FluidVariant;
import java.util.function.Supplier;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Unit;

public record LargeTankFluidDisplay(
        Supplier<Data> fluidDataSupplier) implements GuiComponentServer<Unit, LargeTankFluidDisplay.Data> {
    public static final Type<Unit, Data> TYPE = new Type<>(MI.id("large_tank_fluid_display"), StreamCodec.unit(Unit.INSTANCE), Data.STREAM_CODEC);

    @Override
    public Unit params() {
        return Unit.INSTANCE;
    }

    @Override
    public Data extractData() {
        return fluidDataSupplier.get();
    }

    @Override
    public Type<Unit, Data> getType() {
        return TYPE;
    }

    public record Data(FluidVariant fluid, long amount, long capacity) {
        public static final StreamCodec<RegistryFriendlyByteBuf, Data> STREAM_CODEC = StreamCodec.composite(
                FluidVariant.STREAM_CODEC,
                Data::fluid,
                ByteBufCodecs.VAR_LONG,
                Data::amount,
                ByteBufCodecs.VAR_LONG,
                Data::capacity,
                Data::new);
    }
}
