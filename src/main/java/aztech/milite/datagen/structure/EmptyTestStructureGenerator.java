package aztech.milite.datagen.structure;

import aztech.milite.MI;
import com.google.common.hash.HashCode;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtUtils;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

public class EmptyTestStructureGenerator implements DataProvider {
    private final PackOutput.PathProvider pathProvider;

    public EmptyTestStructureGenerator(PackOutput output) {
        this.pathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, "structure");
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        String structureIn;
        try (var in = getClass().getResourceAsStream("/data/modern_tech/structure/empty.snbt")) {
            structureIn = new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        CompoundTag structureTag;
        try {
            structureTag = NbtUtils.snbtToStructure(structureIn);
        } catch (CommandSyntaxException e) {
            throw new RuntimeException(e);
        }

        structureTag = NbtUtils.addCurrentDataVersion(structureTag);

        var out = new ByteArrayOutputStream();
        try {
            NbtIo.writeCompressed(structureTag, new DataOutputStream(out));
            output.writeIfNeeded(
                    pathProvider.file(MI.id("empty"), "nbt"),
                    out.toByteArray(),
                    HashCode.fromBytes(out.toByteArray()));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        return CompletableFuture.completedFuture(null);
    }

    @Override
    public String getName() {
        return "Empty Test Structure";
    }
}
