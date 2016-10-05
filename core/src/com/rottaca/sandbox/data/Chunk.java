package com.rottaca.sandbox.data;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Disposable;

import java.util.HashMap;

/**
 * Created by Andreas on 07.09.2016.
 */
public class Chunk extends Actor implements Disposable {
    // Chunk data has to be updated ?
    private boolean isChunkDirty;
    // Chunk texture has to be updated ?
    private boolean isTextureDirty;
    // GPU Texture that contains the image
    private Texture texture;
    private Pixmap pixmap;

    // Store references from game grid
    private int[][] fieldData;
    private HashMap<Integer, FieldConfig> fieldCfg;

    private static Vector2 tmp = new Vector2(0, 0);

    /**
     * Constructor
     *
     * @param width
     * @param height
     * @param posX
     * @param posY
     */
    public Chunk(int width, int height, int posX, int posY, int[][] fieldData, HashMap<Integer, FieldConfig> fieldCfg) {
        tmp.set(posX, posY);
        tmp = stageToLocalCoordinates(tmp);
        setBounds(tmp.x, tmp.y, width, height);

        this.fieldCfg = fieldCfg;
        this.fieldData = fieldData;
        this.isTextureDirty = true;
        this.isChunkDirty = true;
        texture = null;
        pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
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

        // Fill with sky color
        pixmap.setColor(Color.argb8888(1, 1, 1, 0));
        pixmap.fill();

        tmp.set(0, 0);
        tmp = localToStageCoordinates(tmp);

        // TODO synchronize game field buffer
        synchronized (fieldData) {
            for (int y = 0; y < getHeight(); y++) {
                for (int x = 0; x < getWidth(); x++) {
                    int mapId = fieldData[(int) (tmp.y + y)][(int) (tmp.x + x)];

                    if (fieldCfg.containsKey(mapId)) {
                        pixmap.setColor(fieldCfg.get(mapId).color);
                        pixmap.drawPixel(x, (int) (getHeight() - (y + 1)));
                    }
                }
            }
        }
        // pixmap.setColor(Color.BLACK);
        //pixmap.drawRectangle(0, 0, width, height);
//        pixmap.drawCircle(0,0,10);
        isTextureDirty = false;

        if (texture != null)
            texture.dispose();

        texture = new Texture(pixmap);

        return texture;
    }

    @Override
    public String toString() {
        return "Chunk (" + getX() + "x" + getY() + "), WxH: " + getWidth() + "x" + getHeight() + " is " + (isTextureDirty ? "" : "not ") + "dirty.";
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

    @Override
    public void act(float delta) {
        super.act(delta);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        tmp.set(0, 0);
        tmp = localToStageCoordinates(tmp);
        batch.draw(getUpdatedChunk(), tmp.x, tmp.y);
    }

    @Override
    public void dispose() {
        if (texture != null)
            texture.dispose();
        if (pixmap != null)
            pixmap.dispose();
    }
}
