package aztech.milite.debug;

import static net.minecraft.commands.arguments.coordinates.BlockPosArgument.blockPos;
import static net.minecraft.commands.arguments.coordinates.BlockPosArgument.getLoadedBlockPos;
import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

import aztech.milite.config.MIStartupConfig;
import aztech.milite.machines.MachineBlockEntity;
import aztech.milite.machines.multiblocks.MultiblockMachineBlockEntity;
import aztech.milite.machines.recipe.ConflictsChecker;
import aztech.milite.stats.PlayerStatisticsData;
import com.mojang.brigadier.Command;
import com.mojang.logging.LogUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import java.io.IOException;

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
