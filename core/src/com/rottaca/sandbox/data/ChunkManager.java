package com.rottaca.sandbox.data;

import com.badlogic.gdx.Gdx;

import java.util.HashMap;

/**
 * Created by Andreas on 07.09.2016.
 */
public class ChunkManager {
    private Chunk[][] chunks;
    private int maxChunkSize;

    public ChunkManager() {

    }


    /**
     * @param fieldData
     * @param fieldCfg
     * @param maxChunkSize
     */
    public void constructChunks(int[][] fieldData, HashMap<Integer, FieldConfig> fieldCfg, int maxChunkSize) {
        this.maxChunkSize = maxChunkSize;
        // Create chunk array
        int chunkCountX = (int) Math.ceil((float) fieldData[1].length / maxChunkSize);
        int chunkCountY = (int) Math.ceil((float) fieldData.length / maxChunkSize);

        int chunkSizeLastX = fieldData[1].length % maxChunkSize;
        int chunkSizeLastY = fieldData.length % maxChunkSize;

        chunks = new Chunk[chunkCountY][chunkCountX];

        for (int y = 0; y < chunkCountY; y++) {
            for (int x = 0; x < chunkCountX; x++) {
                int chunkSizeX = maxChunkSize;
                int chunkSizeY = maxChunkSize;

                if (x == chunkCountX - 1)
                    chunkSizeX = chunkSizeLastX;
                if (y == chunkCountY - 1)
                    chunkSizeY = chunkSizeLastY;

                chunks[y][x] = new Chunk(
                        chunkSizeX,             // chunk size x
                        chunkSizeY,             // chunk size y
                        x * maxChunkSize,         // chunk pos x
                        y * maxChunkSize,         // chunk pos y
                        fieldData, fieldCfg);    // Game grid information
            }
        }

        Gdx.app.debug("MyTag", "ChunkLayout: " + chunkCountX + "x" + chunkCountY);
    }

    public void invaidateCoordinate(int y, int x) {
        int chunkX = (int) Math.floor((float) x / maxChunkSize);
        int chunkY = (int) Math.floor((float) y / maxChunkSize);

        chunks[chunkY][chunkX].setChunkDirty(true);
    }

    public Chunk[][] getChunks() {
        return chunks;
    }
}
