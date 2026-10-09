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

package aztech.milite.client;

import aztech.milite.*;
import aztech.milite.blocks.storage.barrel.BarrelTooltipData;
import aztech.milite.client.blocks.forgehammer.ForgeHammerScreen;
import aztech.milite.client.blocks.processingtable.ProcessingTableScreen;
import aztech.milite.client.blocks.storage.barrel.BarrelRenderer;
import aztech.milite.client.blocks.storage.barrel.BarrelTooltipComponent;
import aztech.milite.client.blocks.storage.barrel.DeferredBarrelTextRenderer;
import aztech.milite.client.blocks.storage.MIStorageBlockItemClient;
import aztech.milite.client.blocks.storage.tank.TankRenderer;
import aztech.milite.client.datagen.MIDatagenClient;
import aztech.milite.client.fluid.MIFluidTypeClient;
import aztech.milite.client.items.armor.ClientKeyHandler;
import aztech.milite.client.items.armor.HudRenderer;
import aztech.milite.client.items.armor.JetpackParticleAdder;
import aztech.milite.client.items.SteamDrillHighlight;
import aztech.milite.client.items.SteamDrillTooltipComponent;
import aztech.milite.client.machines.gui.MachineMenuClient;
import aztech.milite.client.machines.gui.MachineScreen;
import aztech.milite.client.machines.MachineBlockEntityRenderer;
import aztech.milite.client.machines.MachineOverlayClient;
import aztech.milite.client.machines.models.DelegatingUnbakedModel;
import aztech.milite.client.machines.models.MachineBakedModel;
import aztech.milite.client.machines.models.MachineUnbakedModel;
import aztech.milite.client.machines.models.UseBlockModelUnbakedModel;
import aztech.milite.client.machines.multiblocks.MultiblockHighlight;
import aztech.milite.client.machines.multiblocks.MultiblockMachineBER;
import aztech.milite.client.machines.multiblocks.MultiblockTankBER;
import aztech.milite.config.MIClientConfig;
import aztech.milite.config.MIStartupConfig;
import aztech.milite.datagen.MIDatagenServer;
import aztech.milite.datagen.model.DelegatingModelBuilder;
import aztech.milite.items.RedstoneControlModuleItem;
import aztech.milite.items.SteamDrillItem;
import aztech.milite.machines.blockentities.multiblocks.LargeTankMultiblockBlockEntity;
import aztech.milite.machines.MachineBlock;
import aztech.milite.machines.models.MachineCasings;
import aztech.milite.machines.multiblocks.MultiblockMachineBlockEntity;
import aztech.milite.misc.runtime_datagen.RuntimeDataGen;
import aztech.milite.util.TextHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.tags.TagKey;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLConstructModEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

import java.util.ArrayList;
import java.util.function.Supplier;
import java.util.List;
import java.util.Objects;

@EventBusSubscriber(value = Dist.CLIENT, modid = MI.ID)
public class MIClient {
    @SubscribeEvent
    private static void init(FMLConstructModEvent ignored) {
        var modBus = ModLoadingContext.get().getActiveContainer().getEventBus();
        Objects.requireNonNull(modBus);
        var modContainer = ModLoadingContext.get().getActiveContainer();

        modContainer.registerConfig(ModConfig.Type.CLIENT, MIClientConfig.SPEC);
        modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);

        MIShaders.init(modBus);
        modBus.addListener(MIFluidTypeClient::register);
        modBus.addListener(MIStorageBlockItemClient::register);

        NeoForge.EVENT_BUS.addListener(SteamDrillHighlight::onBlockHighlight);
        NeoForge.EVENT_BUS.addListener(MachineOverlayClient::onBlockOutline);
        DeferredBarrelTextRenderer.init();
        MultiblockHighlight.init();

        NeoForge.EVENT_BUS.addListener(RenderFrameEvent.Pre.class, event -> {
            JetpackParticleAdder.addJetpackParticles(Minecraft.getInstance());
        });
        NeoForge.EVENT_BUS.addListener(ClientTickEvent.Post.class, event -> {
            ClientKeyHandler.onEndTick(Minecraft.getInstance());
        });
        NeoForge.EVENT_BUS.addListener(ItemTooltipEvent.class, event -> {
            MITooltips.attachTooltip(event.getItemStack(), event.getToolTip());

            if (event.getFlags().isAdvanced() && MIClientConfig.INSTANCE.itemTagTooltips.getAsBoolean()) {
                var ids = event.getItemStack().getTags().map(TagKey::location).sorted().toList();
                for (ResourceLocation id : ids) {
                    event.getToolTip().add(Component.literal("#" + id).setStyle(TextHelper.GRAY_TEXT));
                }
            }
        });

        modBus.addListener(GatherDataEvent.class, event -> {
            MIDatagenClient.configure(
                    event.getGenerator(),
                    event.getExistingFileHelper(),
                    event.getLookupProvider(),
                    event.includeServer(),
                    false);
        });

        modBus.addListener(RegisterRenderBuffersEvent.class, event -> {
            event.registerRenderBuffer(MIRenderTypes.cutoutHighlight());
            event.registerRenderBuffer(MIRenderTypes.translucentHighlight());
        });

        // Warn if neither JEI nor REI is present!
        if (!ModList.get().isLoaded("emi") && !ModList.get().isLoaded("jei")
                && !ModList.get().isLoaded("roughlyenoughitems")) {
            NeoForge.EVENT_BUS.addListener(ClientPlayerNetworkEvent.LoggingIn.class, event -> {
                if (MIClientConfig.INSTANCE.missingRecipeViewerMessage.getAsBoolean()) {
                    event.getPlayer().displayClientMessage(MIText.NoEmi.text().withStyle(ChatFormatting.GOLD), false);
                }
            });
        }

        if (MIStartupConfig.INSTANCE.datagenOnStartup.getAsBoolean()) {
            modBus.addListener(AddPackFindersEvent.class, event -> {
                if (event.getPackType() == PackType.CLIENT_RESOURCES) {
                    RuntimeDataGen.run(MIDatagenClient::configure, MIDatagenServer::configure);
                }
            });
        }

        MI.LOGGER.info("Modern Industrialization client setup done!");
    }

    @SubscribeEvent
    private static void registerMenuScreens(RegisterMenuScreensEvent event) {
        event.register(MIRegistries.FORGE_HAMMER_MENU.get(), ForgeHammerScreen::new);
        event.register(MIRegistries.PROCESSING_TABLE_MENU.get(), ProcessingTableScreen::new);
        event.register((MenuType<MachineMenuClient>) MIRegistries.MACHINE_MENU.get(), MachineScreen::new);
    }

    @SubscribeEvent
    private static void registerModelLoaders(ModelEvent.RegisterGeometryLoaders event) {
        event.register(DelegatingModelBuilder.LOADER_ID, DelegatingUnbakedModel.LOADER);
        event.register(MachineUnbakedModel.LOADER_ID, MachineUnbakedModel.LOADER);
        event.register(UseBlockModelUnbakedModel.LOADER_ID, UseBlockModelUnbakedModel.LOADER);
    }

    @SubscribeEvent
    private static void registerAdditionalModels(ModelEvent.RegisterAdditional event) {
        for (var casing : MachineCasings.registeredCasings.values()) {
            event.register(MachineBakedModel.getCasingModelId(casing));
        }
    }

    private static final List<Runnable> blockEntityRendererRegistrations = new ArrayList<>();

    public static <T extends BlockEntity, U extends T> void registerBlockEntityRenderer(Supplier<? extends BlockEntityType<? extends U>> bet,
            BlockEntityRendererProvider<T> renderer) {
        blockEntityRendererRegistrations.add(() -> BlockEntityRenderers.register(bet.get(), renderer));
    }

    @SubscribeEvent
    private static void registerBlockEntityRenderers(FMLClientSetupEvent event) {
        for (var blockDef : MIBlock.BLOCKS.getEntries()) {
            if (blockDef.get() instanceof MachineBlock machine) {
                var blockEntity = machine.getBlockEntityInstance();
                BlockEntityType type = blockEntity.getType();

                if (blockEntity instanceof LargeTankMultiblockBlockEntity) {
                    BlockEntityRenderers.register(type, MultiblockTankBER::new);
                } else if (blockEntity instanceof MultiblockMachineBlockEntity) {
                    BlockEntityRenderers.register(type, MultiblockMachineBER::new);
                } else {
                    BlockEntityRenderers.register(type, c -> new MachineBlockEntityRenderer(c));
                }
            }
        }

        BlockEntityRenderers.register(MIRegistries.CREATIVE_BARREL_BE.get(), context -> new BarrelRenderer(0x000000));
        BlockEntityRenderers.register(MIRegistries.CREATIVE_TANK_BE.get(), context -> new TankRenderer(0x000000));

        blockEntityRendererRegistrations.forEach(Runnable::run);
    }

    @SubscribeEvent
    private static void registerClientTooltipComponents(RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(BarrelTooltipData.class, BarrelTooltipComponent::new);
        event.register(SteamDrillItem.SteamDrillTooltipData.class, SteamDrillTooltipComponent::new);
    }

    @SubscribeEvent
    private static void registerGuiOverlays(RegisterGuiLayersEvent event) {
        event.registerAbove(VanillaGuiLayers.SELECTED_ITEM_NAME, MI.id("activation_status"), HudRenderer::onRenderHud);
    }

    @SubscribeEvent
    private static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        MIKeybinds.init(event);
    }

    @SubscribeEvent
    private static void registerItemProperties(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ItemProperties.register(MIItem.REDSTONE_CONTROL_MODULE.asItem(), MI.id("redstone_control_module"),
                    (stack, level, entity, seed) -> {
                        return RedstoneControlModuleItem.isRequiresLowSignal(stack) ? 0 : 1;
                    });
        });
    }
}
