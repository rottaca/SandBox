package com.rottaca.sandbox.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.rottaca.sandbox.ctrl.GameFieldCamera;
import com.rottaca.sandbox.data.Bullet;
import com.rottaca.sandbox.data.Chunk;
import com.rottaca.sandbox.data.GameGrid;
import com.rottaca.sandbox.data.Tank;

import java.util.ArrayList;

/**
 * Created by Andreas on 11.09.2016.
 */
public class GameFieldSpriteBatch extends SpriteBatch {

    private ShaderProgram shaderProgram;

    GameFieldSpriteBatch() {
        super();

        shaderProgram = new ShaderProgram(Gdx.files.internal("shaders/gameFieldVertexShader.vert"), Gdx.files.internal("shaders/gameFieldFragmentShader.frag"));
        if (!shaderProgram.isCompiled()) {
            Gdx.app.error("MyTag", "Can't compile shader:\n" + shaderProgram.getLog());
            return;
        }
        setShader(shaderProgram);
        enableBlending();
    }


    public void drawGame(GameFieldCamera camera, Chunk[][] chunks, ArrayList<Bullet> bullets, ArrayList<Tank> tanks) {
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
                c.draw(this, 1);
            }
        }
        flush();

        setShader(null);
        for (int i = 0; i < bullets.size(); i++) {
            Bullet b = bullets.get(i);
            b.draw(this, 1);
        }

        for (Tank t : tanks) {


            t.draw(this, 1);
        }

        end();
    }
}
