package net.sebastian.oneblock.block.custom;

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
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;

public class OneBlock extends Block {

    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");
    private static int blocksMined = 0; // Zählt die abgebauten Blöcke
    private static int teleprt = 0;

    public OneBlock() {
        super(Properties.copy(Blocks.STONE)
                .noOcclusion()
                .noCollission()
                .strength(3.0F));
        this.registerDefaultState(this.stateDefinition.any().setValue(ACTIVE, true));
    }

    public static void teleport(ServerLevel world, BlockPos pos) {
        if(teleprt >= 1) {
            teleportItems(world, pos);
            teleprt -= 1;
            world.players().forEach(player ->
                    player.sendSystemMessage(Component.literal("TP COunter: " + teleprt)));
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ACTIVE);
    }

    @Override
    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, world, pos, oldState, isMoving);

        // Prüfen, ob die Welt ein ServerLevel ist
        if (world instanceof ServerLevel serverLevel) {
            teleprt = 20;
            placeGrassBlock(serverLevel, pos.below()); // Grasblock unterhalb platzieren
            loadBlockCount(serverLevel); // Anzahl der abgebauten Blöcke laden
            teleportItems(serverLevel,pos);
            // Jeden Block um 5 Ticks verzögert die Funktion zum Teleportieren von Items planen
            serverLevel.scheduleTick(pos, this, 5); // Aufruf alle 5 Ticks
        }
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
            teleportItems(world, pos);
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

    private static void saveBlockCount(ServerLevel world) {
        OneBlockSavedData data = OneBlockData.loadOrCreateData(world);
        data.saveincrementBlocksMined(); // BlocksMined aktualisieren
    }

    private static void loadBlockCount(ServerLevel world) {
        OneBlockSavedData data = OneBlockData.loadOrCreateData(world);
        blocksMined = data.getBlocksMined(); // BlocksMined laden
    }

    public static void teleportItems(ServerLevel world, BlockPos pos) {
        // Definiere einen Bereich um den Block
        List<ItemEntity> nearbyItems = world.getEntitiesOfClass(ItemEntity.class,
                new net.minecraft.world.phys.AABB(pos.offset(-10, -10, -10), pos.offset(10, 10, 10)));

        // Teleportiere jedes Item
        for (ItemEntity item : nearbyItems) {
            if (item != null && !item.isRemoved()) {
                BlockPos itemPos = item.blockPosition();
                if (!itemPos.equals(pos.below())) { // Nur teleportieren, wenn das Item nicht schon unter dem OneBlock ist
                    item.setPos(pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5);
                }
            }
        }
    }

    // Diese Methode wird nun verwendet, um alle 5 Ticks Items zu teleportieren
    public void tick(BlockState state, ServerLevel world, BlockPos pos, java.util.Random random) {
        // Items teleportieren
        teleportItems(world, pos);

        // Setze den Block-Tick-Planer, um alle 5 Ticks zu wiederholen
        world.scheduleTick(pos, this, 5); // Wiederhole alle 5 Ticks
    }
}
