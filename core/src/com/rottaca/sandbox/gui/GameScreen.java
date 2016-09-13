package com.rottaca.sandbox.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.rottaca.sandbox.ctrl.GameController;
import com.rottaca.sandbox.ctrl.GameFieldCamera;
import com.rottaca.sandbox.ctrl.SandBox;
import com.rottaca.sandbox.data.Bullet;
import com.rottaca.sandbox.data.Chunk;
import com.rottaca.sandbox.data.MapConfig;

import java.util.ArrayList;

/**
 * Created by Andreas on 04.09.2016.
 */
public class GameScreen extends ScreenAdapter implements InputProcessor {

    private GameController gameController;
    private SandBox sandBox;
    private SpriteBatch batch;
    private Stage stage;

    private OrthographicCamera OverlayCamera;
    private GameFieldCamera gameFieldCamera;
    private ExtendViewport viewport;

    private Vector2 touchPosDrag = new Vector2();
    private Vector2 cameraPos = new Vector2();
    private boolean dragging = false;

    // Gui Elements
    private TextButton buttonBack;
    private Label loadingLabel;
    // Layout
    private Table table;
    private Table tableLoadingOverlay;

    private final int VIEWPORT_WIDTH = 840;
    private final int VIEWPORT_HEIGHT = 640;


    private GameFieldSpriteBatch gameFieldSpriteBatch;

    public GameScreen(SandBox sandBox) {
        this.sandBox = sandBox;
        create();
    }

    public void create() {
        OverlayCamera = new OrthographicCamera();
        gameFieldCamera = null;
        batch = sandBox.getBatch();

        // pick a viewport that suits your thing, ExtendViewport is a good start
        viewport = new ExtendViewport(VIEWPORT_WIDTH, VIEWPORT_HEIGHT, OverlayCamera);
        stage = new Stage(viewport);

        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(stage);
        multiplexer.addProcessor(this);
        Gdx.input.setInputProcessor(multiplexer);

        table = new Table();
        table.setFillParent(true);
        table.setDebug(true);
        stage.addActor(table);

        tableLoadingOverlay = new Table();
        tableLoadingOverlay.setFillParent(true);
        tableLoadingOverlay.setDebug(true);
        stage.addActor(tableLoadingOverlay);

        buttonBack = new TextButton("Back", SandBox.skin, "default");
        loadingLabel = new Label("Loading Level...", SandBox.skin, "default");

        // Define layout
        table.add(buttonBack).pad(10).expand().bottom().left();
        tableLoadingOverlay.add(loadingLabel).center();

        // Listeners
        buttonBack.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                sandBox.goToScreen(SandBox.ScreenName.MAIN);
            }
        });

        // Disable continuous rendering
        Gdx.graphics.setContinuousRendering(false);
        Gdx.graphics.requestRendering();

        gameFieldSpriteBatch = new GameFieldSpriteBatch();

        // Start game
        gameController = new GameController(this);
        loadingLevel();


        gameController.startLevel(1);
    }

    @Override
    public void render(float delta) {
        GL20 gl = Gdx.gl;
        gl.glClearColor(1, 1, 1, 1);
        gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


        if (gameController != null && gameController.isRunning()) {

            // TODO Synchronize data if necessary ?!
            Chunk[][] chunks = gameController.getChunks();
            ArrayList<Bullet> bullets = gameController.getBullets();
            MapConfig mc = gameController.getLevel().mapConfig;

            gameFieldSpriteBatch.drawGame(gameFieldCamera, chunks, bullets, mc);
        }

        // Draw Overlay at the end
        batch.setProjectionMatrix(OverlayCamera.combined);
        batch.disableBlending();
        batch.begin();
        stage.act(delta);
        stage.draw();
        batch.end();
    }

    public void loadingLevel() {
        tableLoadingOverlay.setVisible(true);
    }

    public void levelLoaded() {
        tableLoadingOverlay.setVisible(false);
        gameFieldCamera = new GameFieldCamera(gameController.getGameFieldHeight(), gameController.getGameFieldWidth(), viewport.getWorldWidth() / viewport.getWorldHeight());
        gameFieldCamera.updateCamera();
    }

    public void requestRendering() {
        Gdx.graphics.requestRendering();
    }

    @Override
    public void pause() {
        gameController.pause();
    }

    @Override
    public void resume() {
        gameController.resume();
    }

    @Override
    public void dispose() {
        if (gameController != null && gameController.isRunning())
            gameController.stopLevel();

        stage.dispose();
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (button != Input.Buttons.LEFT || pointer > 0) return false;
        dragging = false;

        touchPosDrag.x = screenX;
        touchPosDrag.y = screenY;
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (dragging) {

        } else {
            if (gameFieldCamera != null) {
                Vector3 tpGameFieldCoord = new Vector3();
                gameFieldCamera.unproject(tpGameFieldCoord.set(screenX, screenY, 0));
                Gdx.app.debug("MyTag", "Touch at GameField: " + tpGameFieldCoord.toString());

                if (gameController.isRunning())
                    gameController.clickedOnGameField((int) tpGameFieldCoord.y, (int) tpGameFieldCoord.x);

            }
        }
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        Gdx.app.debug("MyTag", "Touch dragged at: " + screenX + " " + screenY);

        Vector2 delta = new Vector2();
        delta.x = touchPosDrag.x - screenX;
        delta.y = touchPosDrag.y - screenY;

        // Noticeable movement ?
        if (delta.x * delta.x + delta.y * delta.y > 2)
            dragging = true;

        touchPosDrag.x = screenX;
        touchPosDrag.y = screenY;
        if (gameFieldCamera != null) {
            cameraPos.x = gameFieldCamera.setCameraPosX(cameraPos.x + 0.2f * delta.x);
            cameraPos.y = gameFieldCamera.setCameraPosY(cameraPos.y - 0.2f * delta.y);
        }
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
