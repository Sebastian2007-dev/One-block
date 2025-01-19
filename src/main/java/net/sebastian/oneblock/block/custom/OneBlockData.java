package net.sebastian.oneblock.block.custom;

import net.minecraft.server.level.ServerLevel;


public class OneBlockData {
    public static final String KEY = "sebastian_oneblock_data";

    public static OneBlockSavedData loadOrCreateData(ServerLevel serverLevel) {
        // Lade vorhandene Daten oder erstelle eine neue Instanz
        return serverLevel.getDataStorage().computeIfAbsent(
                OneBlockSavedData::load, // Daten laden
                OneBlockSavedData::new,            // Neue Daten erstellen
                KEY                                // Schlüssel
        );
    }
}
