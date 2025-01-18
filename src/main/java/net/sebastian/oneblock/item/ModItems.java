package net.sebastian.oneblock.item;

import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.sebastian.oneblock.OneBlockMod;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =DeferredRegister.create(ForgeRegistries.ITEMS, OneBlockMod.MODID);

    public static final RegistryObject<Item> Test = ITEMS.register("test", () -> new Item(new Item.Properties().defaultDurability(1)));

    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }
}
