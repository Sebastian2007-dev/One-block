package net.sebastian.oneblock.block.custom;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;

import java.util.List;

public class OneBlock extends Block {

    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");
    private static int blocksMined = 0; // Zählt die abgebauten Blöcke

    public OneBlock() {
        super(Properties.copy(Blocks.STONE)
                .noOcclusion()
                .noCollission()
                .strength(3.0F));
        this.registerDefaultState(this.stateDefinition.any().setValue(ACTIVE, true));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ACTIVE);
    }

    @Override
    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, world, pos, oldState, isMoving);
        placeGrassBlock((ServerLevel) world, pos.below()); // Grasblock unterhalb platzieren
        loadBlockCount((ServerLevel) world); // Lade die Anzahl der abgebauten Blöcke
    }

    @Override
    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean isMoving) {
        super.neighborChanged(state, world, pos, neighborBlock, neighborPos, isMoving);

        // Überprüfen, ob der Grasblock unterhalb entfernt wurde
        BlockPos belowPos = pos.below();
        if (neighborPos.equals(belowPos) && !world.getBlockState(belowPos).is(Blocks.GRASS_BLOCK)) {
            incrementBlocksMined(world, pos); // Zähler erhöhen und Nachricht senden
            placeGrassBlock((ServerLevel) world, belowPos); // Grasblock wieder platzieren
        }
    }

    private void placeGrassBlock(ServerLevel world, BlockPos pos) {
        if (world.isEmptyBlock(pos)) {
            world.setBlockAndUpdate(pos, Blocks.GRASS_BLOCK.defaultBlockState());
        }
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE; // Unsichtbarer Block
    }

    @Override
    public VoxelShape getShape(BlockState state, net.minecraft.world.level.BlockGetter world, BlockPos pos, CollisionContext context) {
        return Shapes.empty(); // Keine Kollision
    }

    // Erhöht den Zähler und zeigt die Anzahl der abgebauten Blöcke im Chat an
    private static void incrementBlocksMined(Level world, BlockPos pos) {
        blocksMined++;
        saveBlockCount((ServerLevel) world); // Speichern nach jedem Blockabbau
        // Nachricht an alle Spieler senden, wenn die Welt ein Server ist
        if (!world.isClientSide) {
            world.players().forEach(player ->
                    player.sendSystemMessage(Component.literal("Blöcke abgebaut: " + blocksMined))
            );
        }
    }

    // Diese Methode speichert die Anzahl der abgebauten Blöcke in den Welt-Daten
    private static void saveBlockCount(ServerLevel world) {
        CompoundTag tag = world.getDataStorage().get(OneBlockData.KEY, world);
        tag.putInt("blocksMined", blocksMined);
        world.getDataStorage().set(OneBlockData.KEY, tag, world);
    }

    // Diese Methode lädt die Anzahl der abgebauten Blöcke aus den Welt-Daten
    private static void loadBlockCount(ServerLevel world) {
        CompoundTag tag = world.getDataStorage().get(OneBlockData.KEY, world);
        if (tag != null) {
            blocksMined = tag.getInt("blocksMined");
        }
    }

    // Diese Methode wird aufgerufen, um alle Items in der Nähe des OneBlock zu teleportieren
    public static void teleportItems(ServerLevel world, BlockPos pos) {
        // Vergrößere den Bereich, um sicherzustellen, dass alle nahegelegenen Items erfasst werden
        List<ItemEntity> nearbyItems = world.getEntitiesOfClass(ItemEntity.class,
                new net.minecraft.world.phys.AABB(pos.offset(-10, -10, -10), pos.offset(10, 10, 10)));

        // Überprüfe jedes Item und teleportiere es
        for (ItemEntity item : nearbyItems) {
            if (item != null && !item.isRemoved()) {  // Sicherstellen, dass das Item nicht entfernt wurde
                // Ausgabe in die Konsole zur Überprüfung
                System.out.println("Teleportiere Item: " + item);

                // Teleportiere das Item zu einer Position unterhalb des OneBlocks
                item.setPos(pos.getX(), pos.getY() - 1, pos.getZ()); // Y - 1 stellt sicher, dass es unter dem Block landet
            }
        }
    }

    // Diese Methode wird aufgerufen, um Items regelmäßig zu teleportieren (jeder Tick)
    public static void tick(ServerLevel world, BlockPos pos) {
        // Überprüfe, ob Items teleportiert werden sollen, hier wird es immer ausgeführt
        teleportItems(world, pos);
    }
}
