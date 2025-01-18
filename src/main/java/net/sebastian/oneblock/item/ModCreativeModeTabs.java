package net.sebastian.oneblock.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.sebastian.oneblock.OneBlockMod;
import net.sebastian.oneblock.block.ModBlocks;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, OneBlockMod.MODID);

    public static final RegistryObject<CreativeModeTab> ONE_BLOCK_TAB = CREATIVE_MODE_TABS.register("oneblock_tab",
            ()-> CreativeModeTab.builder().icon(()-> new ItemStack(Items.GRASS_BLOCK))
                    .title(Component.translatable("creativetab.oneblock_tab"))
                    .displayItems((itemDisplayParameters, output) -> {
                        output.accept(ModItems.Test.get());
                        output.accept(ModBlocks.ONE_BLOCK.get());
                    })
                    .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
