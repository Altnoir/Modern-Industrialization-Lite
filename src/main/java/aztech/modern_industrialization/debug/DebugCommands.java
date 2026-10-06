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

package aztech.modern_industrialization.debug;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;
import static net.minecraft.commands.arguments.coordinates.BlockPosArgument.blockPos;
import static net.minecraft.commands.arguments.coordinates.BlockPosArgument.getLoadedBlockPos;

import aztech.modern_industrialization.config.MIStartupConfig;
import aztech.modern_industrialization.machines.MachineBlockEntity;
import aztech.modern_industrialization.machines.multiblocks.MultiblockMachineBlockEntity;
import aztech.modern_industrialization.machines.recipe.ConflictsChecker;
import aztech.modern_industrialization.stats.PlayerStatisticsData;
import com.mojang.brigadier.Command;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

public class DebugCommands {
    // @formatter:off
    public static void init() {
        NeoForge.EVENT_BUS.addListener(RegisterCommandsEvent.class, event -> {
            if (!MIStartupConfig.INSTANCE.debugCommands.getAsBoolean()) {
                return;
            }

            event.getDispatcher().register(literal("mi")
                    .requires(source -> source.hasPermission(4))
                    .then(literal("machines")
                            .then(literal("claim_all")
                                .executes(ctx -> {
                                    return claimMachines(ctx.getSource().getPlayerOrException());
                                })
                            )
                            .then(literal("dump_stats")
                                .executes(ctx -> {
                                    return dumpStats(ctx.getSource().getPlayerOrException());
                                })
                            )
                            .then(literal("build_multiblock")
                                    .then(argument("controller_pos", blockPos())
                                            .executes(ctx -> {
                                                return buildMultiblock(ctx.getSource(), getLoadedBlockPos(ctx, "controller_pos"));
                                            }))
                            )
                    )
                    .then(literal("recipes")
                            .then(literal("check_for_conflicts")
                                .executes(ctx -> {
                                    return checkForConflicts(ctx.getSource());
                                })
                            )
                    )
            );
        });
    }
    // @formatter:on

    private static int claimMachines(ServerPlayer player) {
        for (var level : player.server.getAllLevels()) {
            var chunkSource = level.getChunkSource();
            for (var pos : chunkSource.chunkMap.updatingChunkMap.keySet()) {
                var chunk = chunkSource.getChunk(ChunkPos.getX(pos), ChunkPos.getZ(pos), false);

                if (chunk != null) {
                    for (var be : chunk.getBlockEntities().values()) {
                        if (be instanceof MachineBlockEntity machine) {
                            machine.placedBy.onPlaced(player);
                            machine.setChanged();
                        }
                    }
                }
            }
        }
        return Command.SINGLE_SUCCESS;
    }

    private static int dumpStats(ServerPlayer player) {
        player.displayClientMessage(Component.literal(
                PlayerStatisticsData.get(player.server).get(player).toTag().toString()), false);
        return Command.SINGLE_SUCCESS;
    }

    private static int buildMultiblock(CommandSourceStack src, BlockPos controllerPos) {
        var be = src.getLevel().getBlockEntity(controllerPos);
        if (be instanceof MultiblockMachineBlockEntity multiblock) {
            var shapeMatcher = multiblock.createShapeMatcher();
            int updatedBlocks = shapeMatcher.buildMultiblock(src.getLevel());

            src.sendSuccess(() -> Component.literal("Successfully built multiblock at position %s. %d blocks updated.".formatted(
                    controllerPos, updatedBlocks)), true);
        } else {
            src.sendFailure(Component.literal("Block at position %s is not a multiblock controller.".formatted(controllerPos)));
        }
        return Command.SINGLE_SUCCESS;
    }

    private static int checkForConflicts(CommandSourceStack src) {
        try {
            var reportPath = ConflictsChecker.writeConflictReport(src.getLevel());
            src.sendSuccess(() -> Component.literal("Conflicts written to file " + reportPath), true);
        } catch (IOException exception) {
            src.sendFailure(Component.literal("Failed to write recipe conflicts"));
            LogUtils.getLogger().error("Failed to write recipe conflicts", exception);
        }
        return Command.SINGLE_SUCCESS;
    }
}
