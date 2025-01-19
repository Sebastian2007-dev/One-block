package net.sebastian.oneblock.block.custom;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;

public class OneBlockSavedData extends SavedData {
    private static final String BLOCKS_MINED_KEY = "blocksMined";
    private int blocksMined;

    public OneBlockSavedData() {
        this.blocksMined = 0; // Standardwert
    }

    public static OneBlockSavedData load(CompoundTag tag) {
        OneBlockSavedData data = new OneBlockSavedData();
        data.blocksMined = tag.getInt(BLOCKS_MINED_KEY);
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        tag.putInt(BLOCKS_MINED_KEY, blocksMined);
        return tag;
    }

    public void saveincrementBlocksMined() {
        blocksMined++;
        setDirty(); // Markiere die Daten als geändert
    }

    public int getBlocksMined() {
        return blocksMined;
    }
}
