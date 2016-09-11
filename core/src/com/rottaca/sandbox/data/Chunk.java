package com.rottaca.sandbox.data;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Disposable;

import java.util.HashMap;

/**
 * Created by Andreas on 07.09.2016.
 */
public class Chunk implements Disposable {
    // Size of the chunk
    private int width, height;
    // Position of the chunk in the game grid
    private int posX, posY;
    // Chunk data has to be updated ?
    private boolean isChunkDirty;
    // Chunk texture has to be updated ?
    private boolean isTextureDirty;
    // GPU Texture that contains the image
    private Texture texture;

    // Store references from game grid
    private int[][] fieldData;
    private HashMap<Integer, FieldConfig> fieldCfg;

    /**
     * Constructor
     *
     * @param width
     * @param height
     * @param posX
     * @param posY
     */
    public Chunk(int width, int height, int posX, int posY, int[][] fieldData, HashMap<Integer, FieldConfig> fieldCfg) {
        this.width = width;
        this.height = height;
        this.posX = posX;
        this.posY = posY;
        this.fieldCfg = fieldCfg;
        this.fieldData = fieldData;
        this.isTextureDirty = true;
        this.isChunkDirty = true;
        texture = null;
    }

    /**
     * Returns the game chunk as texture and recomputes the texture if necessary
     *
     * @return
     */
    public synchronized Texture getUpdatedChunk() {
        // Nothing to do?
        if (!isTextureDirty)
            return texture;

        //Gdx.app.debug("MyTag","Updating: " + toString());

        // Otherwise we have to recalculate the chunk texture
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);


        // Fill with sky color
        pixmap.setColor(Color.WHITE);
        pixmap.fill();

        // TODO synchronize game field buffer
        synchronized (fieldData) {
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int mapId = fieldData[posY + y][posX + x];

                    if (fieldCfg.containsKey(mapId)) {
                        pixmap.setColor(fieldCfg.get(mapId).color);
                        pixmap.drawPixel(x, height - (y + 1));
                    }
                }
            }
        }
        //pixmap.setColor(Color.BLACK);
        //pixmap.drawRectangle(0, 0, width, height);
//        pixmap.drawCircle(0,0,10);
        isTextureDirty = false;

        if (texture != null)
            texture.dispose();

        texture = new Texture(pixmap);
        pixmap.dispose();

        return texture;
    }

    @Override
    public String toString() {
        return "Chunk (" + posX + "x" + posY + "), WxH: " + width + "x" + height + " is " + (isTextureDirty ? "" : "not ") + "dirty.";
    }

    public synchronized void setTextureDirty(boolean textureDirty) {
        isTextureDirty = textureDirty;
    }

    public boolean getTextureDirty() {
        return isTextureDirty;
    }

    public void setChunkDirty(boolean chunkDirty) {
        isChunkDirty = chunkDirty;
    }

    public boolean getChunkDirty() {
        return isChunkDirty;
    }

    public int getPosX() {
        return posX;
    }

    public int getPosY() {
        return posY;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    @Override
    public void dispose() {
        if (texture != null)
            texture.dispose();
    }
}
