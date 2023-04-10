package ch.realmtech.game.ecs.system;

import ch.realmtech.game.ecs.component.*;
import ch.realmtech.game.level.cell.CellType;
import ch.realmtech.game.level.map.WorldMap;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.artemis.annotations.All;
import com.artemis.utils.Bag;
import com.artemis.utils.ImmutableBag;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import net.mostlyoriginal.api.Singleton;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Date;

@Singleton
@All(SaveComponent.class)
public class SaveManager extends EntitySystem {
    public final static int SAVE_PROTOCOLE_VERSION = 4;
    private ComponentMapper<SaveComponent> mSave;

    @Override
    protected void processSystem() {

    }

    public void saveWorldMap(int saveEntityId) throws IOException {
        SaveComponent saveComponent = mSave.get(saveEntityId);
        if (saveComponent.file.exists()) {
            Files.newBufferedWriter(saveComponent.file.toPath(), StandardOpenOption.TRUNCATE_EXISTING).close();
        }
        final DataOutputStream outputStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(saveComponent.file)));
        try {
            outputStream.write("RealmTech".getBytes());
            outputStream.write(ByteBuffer.allocate(Integer.BYTES).putInt(SAVE_PROTOCOLE_VERSION).array());
            outputStream.write(ByteBuffer.allocate(Long.BYTES).putLong(System.currentTimeMillis()).array());
            outputStream.write(ByteBuffer.allocate(Integer.BYTES).putInt(WorldMap.WORLD_WITH).array());
            outputStream.write(ByteBuffer.allocate(Integer.BYTES).putInt(WorldMap.WORLD_HIGH).array());
            outputStream.write(ByteBuffer.allocate(Byte.BYTES).put(WorldMap.NUMBER_LAYER).array());
            outputStream.write(ByteBuffer.allocate(Long.BYTES).putLong(saveComponent.context.getWorldMapManager().getSeed()).array());
            Entity player = saveComponent.context.getPlayer();
            PositionComponent playerPosition = player.getComponent(PositionComponent.class);
            outputStream.write(ByteBuffer.allocate(Float.BYTES).putFloat(playerPosition.x).array());
            outputStream.write(ByteBuffer.allocate(Float.BYTES).putFloat(playerPosition.y).array());
            saveComponent.context.getWorldMapManager().saveWorldMap(outputStream);
        } finally {
            outputStream.flush();
            outputStream.close();
        }
    }

    public void loadSave(WorldMapComponent worldMapComponent, File saveFile) throws IOException{
        CellManager cellManager = world.getSystem(CellManager.class);
        try (DataInputStream inputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(saveFile)))) {
            byte[] rawFile = inputStream.readAllBytes();
            String magicGameName = new String(rawFile, 0, 9);
            int version = ByteBuffer.wrap(rawFile, 9, Integer.BYTES).getInt();
            if (version != SAVE_PROTOCOLE_VERSION)
                throw new UnsupportedEncodingException("La version de la carte est la version : " + version
                        + " alors que le jeu ne support que la version d'une cate : " + SAVE_PROTOCOLE_VERSION);

            Date date = new Date(ByteBuffer.wrap(rawFile, 13, Long.BYTES).getLong());
            int i = 21;
            int worldWith = ByteBuffer.wrap(rawFile, i, Integer.BYTES).getInt();
            i += Integer.BYTES;
            int worldHigh = ByteBuffer.wrap(rawFile, i, Integer.BYTES).getInt();
            i += Integer.BYTES;
            byte numberLayer = ByteBuffer.wrap(rawFile, i, Byte.BYTES).get();
            i += Byte.BYTES;
            long seed = ByteBuffer.wrap(rawFile, i, Long.BYTES).getLong();
            i += Long.BYTES;
            float playerPositionX = ByteBuffer.wrap(rawFile, i, Float.BYTES).getFloat();
            i += Float.BYTES;
            float playerPositionY = ByteBuffer.wrap(rawFile, i, Float.BYTES).getFloat();
            i += Float.BYTES;

            while (i < rawFile.length) {
                int chunkId = world.create();
                ChunkComponent chunkComponent = world.edit(chunkId).create(ChunkComponent.class);
                int chunkPossX = ByteBuffer.wrap(rawFile, i, Integer.BYTES).getInt();
                i += Integer.BYTES;
                int chunkPossY = ByteBuffer.wrap(rawFile, i, Integer.BYTES).getInt();
                i += Integer.BYTES;
                short nombreDeCells = ByteBuffer.wrap(rawFile, i, Short.BYTES).getShort();
                i += Short.BYTES;
                chunkComponent.init(worldMapComponent.saveId,chunkPossX, chunkPossY);
                for (int j = 0; j < nombreDeCells; j++) {
                    CellType cellType = CellType.getCellTypeByID(ByteBuffer.wrap(rawFile, i, Byte.BYTES).get());
                    i += Byte.BYTES;
                    byte innerPoss = ByteBuffer.wrap(rawFile, i, Byte.BYTES).get();
                    i += Byte.BYTES;
                    byte layer = ByteBuffer.wrap(rawFile, i, Byte.BYTES).get();
                    i += Byte.BYTES;
                    int cellId = world.create();
                    CellComponent cellComponent = world.edit(cellId).create(CellComponent.class);
                    world.inject(cellComponent);
                    cellComponent.init(
                            chunkId,
                            cellManager.getInnerChunkPossX(innerPoss),
                            cellManager.getInnerChunkPossY(innerPoss),
                            layer,
                            cellType
                    );
                }
            }
        }
    }

    public ImmutableBag<File> getTousLesSauvegarde() {
        Bag<File> files = new Bag<>();
        FileHandle[] list = Gdx.files.local("").list(pathname -> pathname.getName().matches(".*\\.rts"));
        for (FileHandle fileHandle : list) {
            files.add(fileHandle.file());
        }
        return files;
    }
}
