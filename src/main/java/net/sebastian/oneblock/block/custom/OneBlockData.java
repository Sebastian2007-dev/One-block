package net.sebastian.oneblock.block.custom;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;

public class OneBlockData {

    public static final String KEY = "sebastian:oneblock_data";

    // Speichern der Daten
    public static void saveData(Level world, OneBlockSavedData data) {
        if (world instanceof ServerLevel) {
            ServerLevel serverWorld = (ServerLevel) world;
            serverWorld.getDataStorage().set(KEY, data);  // Speichern der SavedData
        }
    }

    // Laden der Daten
    public static OneBlockSavedData loadData(Level world) {
        if (world instanceof ServerLevel) {
            ServerLevel serverWorld = (ServerLevel) world;
            // Versuche, die Daten zu laden
            OneBlockSavedData data = serverWorld.getDataStorage().get(KEY);
            if (data == null) {
                // Wenn keine Daten vorhanden sind, eine neue Instanz erstellen
                data = new OneBlockSavedData();
            }
            return data;
        }
        return null;  // Rückgabe null, wenn nicht ein ServerLevel
    }
}
