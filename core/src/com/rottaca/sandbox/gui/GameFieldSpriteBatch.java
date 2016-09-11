package com.rottaca.sandbox.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.rottaca.sandbox.data.Bullet;
import com.rottaca.sandbox.data.Chunk;
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


    public void drawGame(Chunk[][] chunks, ArrayList<Bullet> bullets, MapConfig mc) {
        begin();
        setShader(shaderProgram);
        // Draw all chunks
        for (int y = 0; y < chunks.length; y++) {
            for (int x = 0; x < chunks[1].length; x++) {
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
