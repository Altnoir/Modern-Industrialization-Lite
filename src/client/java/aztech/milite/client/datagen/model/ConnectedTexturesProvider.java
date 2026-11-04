package aztech.milite.client.datagen.model;

import aztech.milite.MI;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;

import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ConnectedTexturesProvider implements DataProvider {
    // athena:ctm
    private static final List<String> CASINGS = List.of(
            "steel_machine_casing",
            "aluminum_alloy_machine_casing",
            "blastproof_casing",
            "bronze_plated_bricks",
            "clean_stainless_steel_machine_casing",
            "frostproof_machine_casing",
            "heatproof_machine_casing",
            "nuclear_casing",
            "plasma_handling_iridium_machine_casing",
            "solid_titanium_machine_casing",
            "tungsten_casing",
            "cupronickel_coil",
            "kanthal_coil",
            "superconductor_coil");

    private static final List<String> CASING_SLOTS = List.of("horizontal", "center", "particle", "vertical", "empty");

    private final PackOutput output;

    public ConnectedTexturesProvider(PackOutput output) {
        this.output = output;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        List<CompletableFuture<?>> saves = new ArrayList<>();
        for (String blockId : CASINGS) {
            saves.add(applyConnectedTextures(cache, blockId, "athena:ctm", CASING_SLOTS));
        }
        return CompletableFuture.allOf(saves.toArray(CompletableFuture[]::new));
    }

    private CompletableFuture<?> applyConnectedTextures(CachedOutput cache, String blockId, String loader,
            List<String> slots) {
        JsonObject blockState = new JsonObject();
        blockState.add("variants", variants(blockId));

        JsonObject textures = new JsonObject();
        for (String slot : slots) {
            textures.addProperty(slot, MI.id("block/" + blockId + "/" + slot).toString());
        }
        blockState.addProperty("athena:loader", loader);
        blockState.add("ctm_textures", textures);

        return DataProvider.saveStable(cache, blockState, blockStatePath(blockId));
    }

    private Path blockStatePath(String blockId) {
        return output.getOutputFolder(PackOutput.Target.RESOURCE_PACK)
                .resolve(MI.ID)
                .resolve("blockstates")
                .resolve(blockId + ".json");
    }

    // MIModelProvider variants
    private JsonObject variants(String blockId) {
        Path path = blockStatePath(blockId);
        if (Files.exists(path)) {
            try (Reader reader = Files.newBufferedReader(path)) {
                JsonObject blockState = JsonParser.parseReader(reader).getAsJsonObject();
                if (blockState.has("variants")) {
                    return blockState.getAsJsonObject("variants");
                }
            } catch (IOException e) {
                throw new UncheckedIOException("Failed to read the blockstate of " + blockId, e);
            }
        }

        JsonObject variants = new JsonObject();
        if (blockId.endsWith("_pipe")) {
            variants.add("axis=x", variant(blockId, 90, 90));
            variants.add("axis=y", variant(blockId, 0, 0));
            variants.add("axis=z", variant(blockId, 90, 0));
        } else {
            variants.add("", variant(blockId, 0, 0));
        }
        return variants;
    }

    private JsonObject variant(String blockId, int x, int y) {
        JsonObject variant = new JsonObject();
        variant.addProperty("model", MI.id("block/" + blockId).toString());
        if (x != 0) {
            variant.addProperty("x", x);
        }
        if (y != 0) {
            variant.addProperty("y", y);
        }
        return variant;
    }

    @Override
    public String getName() {
        return "Connected Textures";
    }
}
