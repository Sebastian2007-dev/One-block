package net.sebastian.oneblock.block.custom;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;

public class OneBlockSavedData extends SavedData {
    private static final String BLOCKS_MINED_KEY = "blocksMined";
    private int blocksMined;

    public OneBlockSavedData() {
        this.blocksMined = 0;  // Initialwert
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        tag.putInt(BLOCKS_MINED_KEY, blocksMined);
        return tag;
    }

    @Override
    public void load(CompoundTag tag) {
        this.blocksMined = tag.getInt(BLOCKS_MINED_KEY);
    }

    public void incrementBlocksMined() {
        blocksMined++;
    }

    public int getBlocksMined() {
        return blocksMined;
    }
}
