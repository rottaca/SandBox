package com.rottaca.sandbox.data;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;

import java.util.HashMap;

public class GameGrid {
    // Contains the game ids.
    // (0,0) is left bottom !
    private int[][] fieldData;
    private int width;
    private int height;
    private HashMap<Integer, FieldConfig> fieldCfg;

    private ChunkManager chunkManager;

    private final int CHUNK_SIZE = 16;


    public GameGrid(Pixmap map, HashMap<Integer, FieldConfig> fieldCfg) {
        this.width = map.getWidth();
        this.height = map.getHeight();
        this.fieldCfg = fieldCfg;

        fieldData = new int[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // Read pixmap upside down
                int mapId = (map.getPixel(x, height - (y + 1)) & 0xFF00) >> 8;

                if (fieldCfg.containsKey(mapId))
                    fieldData[y][x] = mapId;
                else
                    fieldData[y][x] = -1;
            }
        }

        chunkManager = new ChunkManager();
        Gdx.app.debug("MyTag", "Creating chunks...");
        chunkManager.constructChunks(fieldData, fieldCfg, CHUNK_SIZE);
        Gdx.app.debug("MyTag", "Chunks created.");
    }

    public Chunk[][] getChunks() {
        return chunkManager.getChunks();
    }

    public ChunkManager getChunkManager() {
        return chunkManager;
    }

    /**
     * Calculates a single update step of the game field.
     */
    public boolean updateGrid() {
        boolean anyUpdate = false;

        Chunk[][] chunks = chunkManager.getChunks();
        // Iterate over every chunk
        for (int cY = 0; cY < chunks.length; cY++) {
            for (int cX = 0; cX < chunks[cY].length; cX++) {
                Chunk c = chunks[cY][cX];
                // Nothing to do for that chunk ? then skip
                if (!c.getChunkDirty())
                    continue;
                    // Otherwise process chunk
                else {
                    boolean stillUpdateNecessary = false;

                    // TODO Synchronize!
                    //synchronized (fieldData) {
                    // Go from bottom to top
                    for (int y = 0; y < c.getHeight(); y++) {
                        for (int x = 0; x < c.getWidth(); x++) {
                            int gridX = x + c.getPosX();
                            int gridY = y + c.getPosY();

                            int MapId = fieldData[gridY][gridX];
                            // Field empty and a field above exists?
                            if (MapId < 0 && gridY + 1 < getHeight()) {
                                // Get type of this field
                                int MapIdAbove = fieldData[gridY + 1][gridX];
                                // If field above is not empty too, swap fields
                                if (MapIdAbove >= 0) {
                                    fieldData[gridY + 1][gridX] = MapId;
                                    fieldData[gridY][gridX] = MapIdAbove;
                                    anyUpdate = true;
                                    stillUpdateNecessary = true;

                                    // Reactiveate chunk below to take care of falling elements
                                    if (cY > 0 && y == 0)
                                        chunks[cY - 1][cX].setChunkDirty(true);
                                        // Reactivate chunk above to let elements fall down into current junk
                                    else if (cY < c.getHeight() - 1 && y == c.getHeight() - 1) {
                                        chunks[cY + 1][cX].setChunkDirty(true);
                                    }
                                }
                            }
                        }
                    }
                    //}
                    c.setChunkDirty(stillUpdateNecessary);
                    c.setTextureDirty(true);
                }
            }
        }

        return anyUpdate;
    }

    public void executeExplosion(int y, int x, float damage) {
        int radius = Math.round(damage);
        int radius2 = radius * radius;

        int xMax, xMin, yMax, yMin;
        xMax = x + radius;
        yMax = y + radius;
        xMin = x - radius;
        yMin = y - radius;

        if (xMax >= width)
            xMax = width - 1;
        if (yMax >= height)
            yMax = height - 1;
        if (yMin < 0)
            yMin = 0;
        if (xMin < 0)
            xMin = 0;

        for (int y_ = yMin; y_ < yMax; y_++) {
            for (int x_ = xMin; x_ < xMax; x_++) {
                int dx = Math.abs(x - x_);
                int dy = Math.abs(y - y_);
                if (dx * dx + dy * dy <= radius2) {
                    fieldData[y_][x_] = -1;
                    chunkManager.invaidateCoordinate(y_, x_);
                    //for(int i = y_; i < height; i++)
                    //    chunkManager.invaidateCoordinate(i,x_);
                }
            }
        }
    }

    public int getField(int y, int x) {
        return fieldData[y][x];
    }

    public synchronized void setField(int y, int x, int id) {
        fieldData[y][x] = id;
    }

    public int getWdith() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
