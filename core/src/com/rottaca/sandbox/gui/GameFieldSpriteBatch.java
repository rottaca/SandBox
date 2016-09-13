package com.rottaca.sandbox.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.rottaca.sandbox.ctrl.GameFieldCamera;
import com.rottaca.sandbox.data.Bullet;
import com.rottaca.sandbox.data.Chunk;
import com.rottaca.sandbox.data.GameGrid;
import com.rottaca.sandbox.data.MapConfig;

import java.util.ArrayList;

/**
 * Created by Andreas on 11.09.2016.
 */
public class GameFieldSpriteBatch extends SpriteBatch {

    private ShaderProgram shaderProgram;

    private Texture bulletTexture;
    // TODO Combine textures into a single texture by using texture regions
    private Texture tankBodyTexture;
    private Texture tankGunTexture;

    GameFieldSpriteBatch() {
        super();
        shaderProgram = new ShaderProgram(Gdx.files.internal("shaders/gameFieldVertexShader.vert"), Gdx.files.internal("shaders/gameFieldFragmentShader.frag"));
        if (!shaderProgram.isCompiled()) {
            Gdx.app.error("MyTag", "Can't compile shader:\n" + shaderProgram.getLog());
            return;
        }
        setShader(shaderProgram);


        bulletTexture = new Texture("bullet.png");
        tankBodyTexture = new Texture("tankBody.png");
        tankGunTexture = new Texture("tankGun.png");

        enableBlending();
    }


    public void drawGame(GameFieldCamera camera, Chunk[][] chunks, ArrayList<Bullet> bullets, MapConfig mc) {
        // TODO update camera only if necessary
        camera.updateCamera();
        setProjectionMatrix(camera.combined);

        begin();
        setShader(shaderProgram);

        // Draw all chunks
        int visibleChunkNrMinX, visibleChunkNrMinY;
        int visibleChunkNrMaxX, visibleChunkNrMaxY;

        // TODO simplify expressions
        visibleChunkNrMinX = Math.max((int) Math.ceil((camera.getCameraPos().x) / GameGrid.CHUNK_SIZE) - 1, 0);
        visibleChunkNrMinY = Math.max((int) Math.ceil((camera.getCameraPos().y) / GameGrid.CHUNK_SIZE) - 1, 0);

        visibleChunkNrMaxX = Math.min((int) Math.floor((camera.getCameraPos().x + camera.getImageSize().x) / GameGrid.CHUNK_SIZE) + 1, chunks[0].length);
        visibleChunkNrMaxY = Math.min((int) Math.floor((camera.getCameraPos().y + camera.getImageSize().y) / GameGrid.CHUNK_SIZE) + 1, chunks.length);

        // Only compute and render visible chunks
        for (int y = visibleChunkNrMinY; y < visibleChunkNrMaxY; y++) {
            for (int x = visibleChunkNrMinX; x < visibleChunkNrMaxX; x++) {
                Chunk c = chunks[y][x];
                draw(c.getUpdatedChunk(), c.getPosX(), c.getPosY());
            }
        }
        flush();

        setShader(null);
        for (int i = 0; i < bullets.size(); i++) {
            Bullet b = bullets.get(i);
            draw(bulletTexture, b.getX() - bulletTexture.getWidth() / 2, b.getY() - bulletTexture.getHeight() / 2);
        }

        draw(tankGunTexture, mc.leftPlayerPos.x, mc.leftPlayerPos.y - tankGunTexture.getHeight() / 2 + 2);
        draw(tankBodyTexture, mc.leftPlayerPos.x - tankBodyTexture.getWidth() / 2, mc.leftPlayerPos.y - tankBodyTexture.getHeight() / 2);

        end();
    }
}
