package net.sebastian.oneblock;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.common.MinecraftForge;
import net.sebastian.oneblock.block.custom.OneBlock;

public class EventHandler {

    private static final BlockPos oneBlockPos = new BlockPos(0, 64, 0); // Beispielposition, sollte angepasst werden

    public EventHandler() {
        // Registriert die Event-Handler-Klasse mit dem EventBus
        MinecraftForge.EVENT_BUS.register(this);
    }

    // Dieser Event-Handler wird aufgerufen, wenn ein Server-Tick abgeschlossen ist
    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            // Überprüfen, ob das Server-Level existiert
            ServerLevel serverLevel = (ServerLevel) event.getServer().getLevel(net.minecraft.world.level.Level.OVERWORLD);
            if (serverLevel != null) {
                // Direkt die tick-Methode von OneBlock aufrufen
                OneBlock.tick(serverLevel, oneBlockPos); // Dies ruft die teleportItems-Methode auf
            }
        }
    }
}
