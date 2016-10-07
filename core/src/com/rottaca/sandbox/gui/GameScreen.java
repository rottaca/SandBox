package com.rottaca.sandbox.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.rottaca.sandbox.ctrl.GameController;
import com.rottaca.sandbox.ctrl.GameFieldCamera;
import com.rottaca.sandbox.ctrl.InputHandler;
import com.rottaca.sandbox.ctrl.MessageAnimator;
import com.rottaca.sandbox.ctrl.SandBox;
import com.rottaca.sandbox.data.Tank;

/**
 * Created by Andreas on 04.09.2016.
 */
public class GameScreen extends ScreenAdapter implements InputProcessor {

    private GameController gameController;
    private SandBox sandBox;
    private ExtendViewport uiViewport;
    private Stage uiStage;

    private GameFieldCamera gameFieldCamera;
    private ExtendViewport gameViewPort;

    private Vector2 touchPosDrag = new Vector2();
    private Vector2 cameraPos = new Vector2();
    private boolean draggingMap = false;
    private boolean draggingGun = false;

    private Vector2 touchDeltaAim = new Vector2();

    // Gui Elements
    private TextButton buttonBack;
    private TextButton buttonShoot;
    private Slider angleSlider;
    private Slider powerSlider;
    private Label statusLabel;
    private Label loadingLabel;
    // Layout
    private Table tableGameOverlay;
    private Table tableLoadingOverlay;

    // Viewport for game stage
    private final int VIEWPORT_WIDTH = 300;
    private final int VIEWPORT_HEIGHT = 200;

    private MessageAnimator messageAnimator = new MessageAnimator();

    private InputHandler inputHandler;

    private boolean dialogVisible;
    private Dialog finishedDialog;


    public GameScreen(SandBox sandBox) {
        this.sandBox = sandBox;
        create();
    }

    public void create() {
        gameFieldCamera = null;

        // pick a gameViewPort that suits your thing, ExtendViewport is a good start
        uiViewport = new ExtendViewport(VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
        uiStage = new Stage(uiViewport);

        inputHandler = new InputHandler(this);


        tableGameOverlay = new Table();
        tableGameOverlay.setFillParent(true);
        //tableGameOverlay.setDebug(true);
        tableGameOverlay.defaults();
        uiStage.addActor(tableGameOverlay);

        tableLoadingOverlay = new Table();
        tableLoadingOverlay.setFillParent(true);
        tableLoadingOverlay.defaults();
        //tableLoadingOverlay.setDebug(true);
        uiStage.addActor(tableLoadingOverlay);

        buttonBack = new TextButton("Back", SandBox.skin, "default");
        buttonShoot = new TextButton("Shoot", SandBox.skin, "default");
        loadingLabel = new Label("Loading Level...", SandBox.skin, "defaultBlack");
        statusLabel = new Label("Player 1", SandBox.skin, "defaultBlack");
        angleSlider = new Slider(-20, 200, 0.1f, true, SandBox.skin, "default-vertical");
        angleSlider.setValue(10);
        powerSlider = new Slider(40, 150, 0.01f, true, SandBox.skin, "default-vertical");
        powerSlider.setValue(100);

        // Define layout
        tableGameOverlay.add(angleSlider).pad(1).left().expandY();
        tableGameOverlay.add(statusLabel).pad(1).center().expand();
        tableGameOverlay.add(powerSlider).pad(1).right().expandY();
        tableGameOverlay.row();
        tableGameOverlay.add(buttonBack).pad(1);
        tableGameOverlay.add(new Label("", SandBox.skin, "defaultBlack")).pad(1);
        tableGameOverlay.add(buttonShoot).pad(1);

        tableLoadingOverlay.add(loadingLabel).center();

        // Listeners
        buttonBack.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                sandBox.goToScreen(SandBox.ScreenName.MAIN);
            }
        });

        buttonShoot.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (gameController != null && gameController.isRunning()) {

                    float speedX = (float) Math.cos(Math.toRadians(angleSlider.getValue()));
                    float speedY = (float) Math.sin(Math.toRadians(angleSlider.getValue()));
                    float power = powerSlider.getValue();

                    if (!gameController.getTanks().get(gameController.getActiveTankId()).lookingRight)
                        speedX = -speedX;

                    gameController.shoot(speedY * power, speedX * power);

                    angleSlider.setValue(gameController.getTanks().get(gameController.getActiveTankId()).gunAngle);
                    powerSlider.setValue(gameController.getTanks().get(gameController.getActiveTankId()).power);

                }
            }
        });

        dialogVisible = false;
        finishedDialog = new Dialog("Some Dialog", SandBox.skin, "dialog") {
            protected void result(Object object) {
                System.out.println("Chosen: " + object);
                int nr = (Integer) object;
                // TODO
                switch (nr) {
                    case 1:
                        sandBox.goToScreen(SandBox.ScreenName.MAIN);
                        break;
                    case 2:
                        sandBox.goToScreen(SandBox.ScreenName.MAIN);
                        break;
                    case 3:
                        sandBox.goToScreen(SandBox.ScreenName.MAIN);
                        break;

                }
            }
        }.button("Menu ", 1).button("Next", 2).button("Retry", 3);

        // Start game
        gameViewPort = new ExtendViewport(VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
        gameController = new GameController(this, gameViewPort);

        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(uiStage);
        multiplexer.addProcessor(new GestureDetector(inputHandler));
        multiplexer.addProcessor(gameController);
        Gdx.input.setInputProcessor(multiplexer);

        gameController.loadLevel(1);
        levelLoaded();
    }

    @Override
    public void render(float delta) {
        GL20 gl = Gdx.gl;
        gl.glClearColor(1, 1, 1, 1);
        gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (gameController != null && gameController.isLevelLoaded()) {
            gameController.setCurrentTankParameters(angleSlider.getValue(), powerSlider.getValue());
            gameController.act(delta);
            gameController.getViewport().apply();
            gameController.draw();
        }

        // Update overlay data
        updateGui();
        // Draw Overlay at the end
        uiStage.act(delta);
        uiStage.getViewport().apply();
        uiStage.draw();

    }

    public void queueMessage(String msg, long duration) {
        clearMessageQueue();
        messageAnimator.addMessage(msg, duration);
    }

    private void updateGui() {
        if (gameController != null && gameController.isRunning()) {
            String msg = messageAnimator.getMessageAndUpdate();
            if (msg != null) {
                statusLabel.setVisible(true);
                statusLabel.setText(msg);
            } else {
                statusLabel.setVisible(false);
            }

        } else {
            statusLabel.setVisible(false);
        }

        if (gameController != null && gameController.isGameFinished() && !dialogVisible) {
            finishedDialog.text(gameController.getPlayerLost() ? "You lost the level! Retry?" : "You won!");
            finishedDialog.getTitleLabel().setText(gameController.getPlayerLost() ? "Level failed" : "Level cleared");
            finishedDialog.show(uiStage);
            dialogVisible = true;
        }
    }

    public void clearMessageQueue() {
        messageAnimator.clearMessageQueue();
    }

    public void loadingLevel() {
        tableLoadingOverlay.setVisible(true);
    }

    public void levelLoaded() {
        tableLoadingOverlay.setVisible(false);
        gameFieldCamera = new GameFieldCamera(gameController.getGameFieldHeight(), gameController.getGameFieldWidth(), gameViewPort.getWorldWidth() / gameViewPort.getWorldHeight());
        gameFieldCamera.update();
        gameController.getViewport().setCamera(gameFieldCamera);
        inputHandler.setData(gameFieldCamera, gameController);
    }

    public void draggedGameField(float dX, float dY) {
        cameraPos.x = gameFieldCamera.setCameraPosX(cameraPos.x + dX);
        cameraPos.y = gameFieldCamera.setCameraPosY(cameraPos.y + dY);
        Gdx.app.debug("MyTag", "dragged cam: " + cameraPos.x + "x" + cameraPos.y);
    }

    public void zoomedGameField(float dZoom) {
        gameFieldCamera.setCameraZoom(gameFieldCamera.getZoomFactor() + dZoom);
    }

    public void aimTank(float dX, float dY) {
        touchDeltaAim.set(dX, dY);
    }

    public void shootTank(float forceX, float forceY) {
        gameController.shoot(forceX, forceY);
    }

    @Override
    public void dispose() {
        uiStage.dispose();
        gameController.dispose();
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
        draggingMap = false;
        draggingGun = false;

        touchPosDrag.x = screenX;
        touchPosDrag.y = screenY;


        Vector3 tpWorld = new Vector3();
        gameFieldCamera.unproject(tpWorld.set(screenX, screenY, 0));

        int tankId = gameController.getActiveTankId();
        Tank t = gameController.getLevel().tanks.get(tankId);
        float dist = new Vector2(t.getX(), t.getY()).dst2(tpWorld.x, tpWorld.y);

        if (dist < 20 * 20) {
            draggingGun = true;
        }

        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (draggingMap) {

        } else {
            if (gameFieldCamera != null) {
                Vector3 tpWorld = new Vector3();
                gameFieldCamera.unproject(tpWorld.set(screenX, screenY, 0));
                Gdx.app.debug("MyTag", "Touch at GameField: " + tpWorld.toString());

                if (gameController.isRunning() && draggingGun) {

                    //gameController.clickedOnGameField((int) tpGameFieldCoord.y, (int) tpGameFieldCoord.x);
                    // TODO Use coordinates that do not depend on the screen resolution
                    // FIX: Use project function to project tank position in screen space

                    int tankId = gameController.getActiveTankId();
                    Tank t = gameController.getLevel().tanks.get(tankId);
                    Vector2 delta = new Vector2(t.getX(), t.getY()).sub(tpWorld.x, tpWorld.y);

                    gameController.shoot(
                            delta.y * 0.04f,
                            delta.x * 0.04f);
                }
            }
        }

        draggingGun = false;
        draggingMap = false;
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {

        Vector2 delta = new Vector2();
        delta.x = touchPosDrag.x - screenX;
        delta.y = touchPosDrag.y - screenY;

        Gdx.app.debug("MyTag", "Touch dragged at: " + screenX + " " + screenY);


        if (draggingGun) {

        } else if (delta.x * delta.x + delta.y * delta.y > 2) {
            draggingMap = true;
        }

        if (draggingMap) {
            if (gameFieldCamera != null) {
                cameraPos.x = gameFieldCamera.setCameraPosX(cameraPos.x + delta.x);
                cameraPos.y = gameFieldCamera.setCameraPosY(cameraPos.y + 0.2f * delta.y);
            }
        }
        touchPosDrag.x = screenX;
        touchPosDrag.y = screenY;
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
