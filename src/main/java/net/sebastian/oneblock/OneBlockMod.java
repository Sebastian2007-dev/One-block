package net.sebastian.oneblock;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.sebastian.oneblock.block.ModBlocks;
import net.sebastian.oneblock.block.custom.OneBlockData;
import net.sebastian.oneblock.block.custom.OneBlockSavedData;
import net.sebastian.oneblock.item.ModCreativeModeTabs;
import net.sebastian.oneblock.item.ModItems;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(OneBlockMod.MODID)
public class OneBlockMod
{
    public static final String MODID = "oneblock";

    private static final Logger LOGGER = LogUtils.getLogger();

    public OneBlockMod(FMLJavaModLoadingContext context)
    {
        IEventBus modEventBus = context.getModEventBus();

        ModCreativeModeTabs.register(modEventBus);
        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);

        modEventBus.addListener(this::commonSetup);

        // Registriert den Client-Setup Event auf dem modEventBus
        IEventBus eventBus = MinecraftForge.EVENT_BUS;
        eventBus.addListener(this::clientSetup);

        modEventBus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.register(this);
        modEventBus.addListener(this::addCreative);

        // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
        context.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event)
    {
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        // Client Setup
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        // Hier kannst du Code hinzufügen, wenn der Server startet
    }

    // Registriere den ServerStoppingEvent auf dem richtigen EventBus
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ModEvents
    {
        @SubscribeEvent
        public static void onServerStopping(ServerStoppingEvent event) {
            ServerLevel serverLevel = event.getServer().getLevel(Level.OVERWORLD);
            OneBlockSavedData data = OneBlockData.loadOrCreateData(serverLevel);
            data.saveincrementBlocksMined(); // Speichere die Änderungen
        }
    }
}
