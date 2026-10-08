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

package aztech.milite.network.machines;

import aztech.milite.machines.gui.MachineMenuServer;
import aztech.milite.machines.guicomponents.ShapeSelection;
import aztech.milite.network.BasePacket;
import aztech.milite.network.MIStreamCodecs;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.inventory.AbstractContainerMenu;

public record ChangeShapePacket(int syncId, int shapeLine, boolean clickedLeftButton) implements BasePacket {
    public static final StreamCodec<ByteBuf, ChangeShapePacket> STREAM_CODEC = StreamCodec.composite(
            MIStreamCodecs.BYTE,
            ChangeShapePacket::syncId,
            ByteBufCodecs.VAR_INT,
            ChangeShapePacket::shapeLine,
            ByteBufCodecs.BOOL,
            ChangeShapePacket::clickedLeftButton,
            ChangeShapePacket::new);

    @Override
    public void handle(Context ctx) {
        ctx.assertOnServer();

        AbstractContainerMenu menu = ctx.getPlayer().containerMenu;
        if (menu.containerId == syncId && menu instanceof MachineMenuServer machineMenu) {
            // Not every multiblock has a shape to select, so a missing component is ignored instead of throwing.
            var shapeSelection = machineMenu.blockEntity.guiComponents.getNullable(ShapeSelection.class);
            if (shapeSelection != null) {
                shapeSelection.behavior.handleClick(shapeLine, clickedLeftButton ? -1 : +1);
            }
        }
    }
}
