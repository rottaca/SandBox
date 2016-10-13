package com.rottaca.sandbox.ctrl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.Group;

/**
 * Created by Andreas on 09.10.2016.
 */

public class GameRenderer {

    private GameController gameController;
    private Group backgroundGroup;
    private Group gameFieldGroup;
    private Group bulletGroup;
    private Group tankGroup;

    private FrameBuffer fbo = null;
    private TextureRegion fboTexture = null;
    private OrthographicCamera fboRenderCam = null;

    ShaderProgram postProcessingShaderGameField;

    private final boolean postProcessGameField = true;

    public GameRenderer(GameController gameController) {

        this.gameController = gameController;

        backgroundGroup = gameController.getBackgroundGroup();
        bulletGroup = gameController.getBulletGroup();
        tankGroup = gameController.getTankGroup();
        gameFieldGroup = gameController.getGameFieldGroup();

        if (postProcessGameField) {
            postProcessingShaderGameField = new ShaderProgram(Gdx.files.internal("shaders/gameFieldVertexShader.vert"),
                    Gdx.files.internal("shaders/gameFieldFragmentShader.frag"));

            if (!postProcessingShaderGameField.isCompiled()) {
                Gdx.app.error("MyTag", "Error while compiling shader: \n" + postProcessingShaderGameField.getLog());
            }
        }
    }


    public void renderGame() {
        int width = Gdx.graphics.getWidth();
        int height = Gdx.graphics.getHeight();
        Batch batch = gameController.getBatch();

        Camera camera = gameController.getViewport().getCamera();
        camera.update();

        if (postProcessGameField) {
            if (fbo == null) {
                fbo = new FrameBuffer(Pixmap.Format.RGBA8888, width, height, false);
                fboTexture = new TextureRegion(fbo.getColorBufferTexture());
                fboTexture.flip(false, true);
                fboRenderCam = new OrthographicCamera(width, height);
            }

            // Draw Background
            batch.setProjectionMatrix(camera.combined);
            batch.setShader(null);
            batch.begin();
            backgroundGroup.draw(batch, 1);
            batch.end();

            // Active FBO
            fbo.begin();
            GL20 gl = Gdx.gl;
            gl.glClearColor(1, 1, 1, 0);        // Clear to transparent white
            gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            // Draw gamefield to fbo
            batch.begin();
            //gameController.getRoot().draw(batch, 1);
            gameFieldGroup.draw(batch, 1);
            batch.end();
            fbo.end();

            // Draw gamefield with postprocessing
            batch.setProjectionMatrix(fboRenderCam.combined);
            batch.setShader(postProcessingShaderGameField);
            batch.begin();
            batch.draw(fboTexture, -width / 2, -height / 2, width, height);
            batch.end();

            // Draw tanks and bullets
            batch.setProjectionMatrix(camera.combined);
            batch.setShader(null);
            batch.begin();
            bulletGroup.draw(batch, 1);
            tankGroup.draw(batch, 1);
            batch.end();
        } else {
            // Only render simple
            batch.setProjectionMatrix(camera.combined);
            batch.setShader(null);
            batch.begin();
            backgroundGroup.draw(batch, 1);
            gameFieldGroup.draw(batch, 1);
            bulletGroup.draw(batch, 1);
            tankGroup.draw(batch, 1);
            batch.end();

        }
    }
}
