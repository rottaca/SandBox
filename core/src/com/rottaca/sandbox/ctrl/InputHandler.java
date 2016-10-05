package com.rottaca.sandbox.ctrl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.rottaca.sandbox.gui.GameScreen;

/**
 * Created by Andreas on 18.09.2016.
 */
public class InputHandler implements GestureDetector.GestureListener {

    private GameFieldCamera camera;
    private GameController gameController;

    private Vector3[] touchPosWorld;
    private Vector2[] touchPosScreen;
    private int fingersDown;
    private float multiTouchDist2Screen;
    private boolean dragging;
    private boolean aiming;
    private GameScreen gameScreen;

    private final int TOL_DRAGGING_SQUARED = 2 * 2;
    private final int TOL_AIMING_SQUARED = 20 * 20;
    private final float ZOOM_MULTIPLICATOR = 0.00001f;
    private final float DRAG_MULTIPLICATOR = 0.2f;
    private final float SHOOT_MULTIPLICATOR = 0.04f;

    private boolean dataSet;

    public InputHandler(GameScreen gameScreen) {
        touchPosWorld = new Vector3[2];
        touchPosWorld[0] = new Vector3();
        touchPosWorld[1] = new Vector3();
        touchPosScreen = new Vector2[2];
        touchPosScreen[0] = new Vector2();
        touchPosScreen[1] = new Vector2();
        dragging = false;
        aiming = false;
        this.gameScreen = gameScreen;
        multiTouchDist2Screen = 0;
        fingersDown = 0;
        dataSet = false;
    }

    public void setData(GameFieldCamera camera, GameController gameController) {
        this.camera = camera;
        this.gameController = gameController;
        dataSet = true;
    }

    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (!dataSet || button != Input.Buttons.LEFT || pointer > 1) return false;
        fingersDown++;

        touchPosWorld[pointer] = camera.unproject(new Vector3(screenX, screenY, 0));
        touchPosScreen[pointer].set(screenX, screenY);

        multiTouchDist2Screen = new Vector2(touchPosScreen[0]).dst2(touchPosScreen[1]);

        int tankId = gameController.getActiveTankId();

        float dist2 = new Vector2(gameController.getTanks().get(tankId).getX(), gameController.getTanks().get(tankId).getY()).dst2(touchPosWorld[pointer].x, touchPosWorld[pointer].y);

        if (dist2 < TOL_AIMING_SQUARED) {
            Gdx.app.debug("MyTag", "Aiming " + pointer + " at tank " + tankId);
            aiming = true;
        }

        // TODO check for aiming
        Gdx.app.debug("MyTag", "Touch pointer " + pointer + " down at " + touchPosWorld[pointer]);
        return true;
    }

    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (!dataSet || button != Input.Buttons.LEFT || pointer > 1) return false;
        fingersDown--;

        touchPosWorld[pointer] = camera.unproject(new Vector3(screenX, screenY, 0));
        touchPosScreen[pointer].set(screenX, screenY);

        Gdx.app.debug("MyTag", "Touch pointer " + pointer + " up at " + touchPosWorld[pointer]);

        if (aiming) {
            int tankId = gameController.getActiveTankId();
            Vector3 delta3 = new Vector3(touchPosWorld[pointer]);
            delta3.sub(gameController.getTanks().get(tankId).getX(), gameController.getTanks().get(tankId).getY(), 0);
            gameScreen.shootTank(-delta3.x * SHOOT_MULTIPLICATOR, -delta3.y * SHOOT_MULTIPLICATOR);
        }

        if (fingersDown == 0) {
            dragging = false;
            aiming = false;
        }
        return false;
    }

    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (!dataSet || pointer > 1) return false;

        Vector3 newTouchPosWorld = camera.unproject(new Vector3(screenX, screenY, 0));
        Vector2 newTouchPosScreen = new Vector2(screenX, screenY);

        // Dragging or aiming with one finger
        if (fingersDown == 1) {
            Vector2 delta2 = new Vector2(touchPosScreen[pointer]);
            delta2.sub(newTouchPosScreen);

            if (!aiming && delta2.len2() > TOL_DRAGGING_SQUARED) {
                dragging = true;
            }

            if (dragging) {
                gameScreen.draggedGameField(delta2.x * DRAG_MULTIPLICATOR, -delta2.y * DRAG_MULTIPLICATOR);
                Gdx.app.debug("MyTag", "Touch pointer " + pointer + " dragged by " + delta2);
            } else if (aiming) {
                Vector3 delta3 = new Vector3(newTouchPosWorld);
                int tankId = gameController.getActiveTankId();
                delta3.sub(gameController.getTanks().get(tankId).getX(), gameController.getTanks().get(tankId).getY(), 0);

                gameScreen.aimTank(delta3.x, delta3.y);
                Gdx.app.debug("MyTag", "Touch pointer " + pointer + " charged tank by " + delta3);
            } else {
                // Ignore
            }
            touchPosWorld[pointer] = newTouchPosWorld;
            touchPosScreen[pointer] = newTouchPosScreen;

            // Zooming with two fingers
        } else if (fingersDown == 2) {
            touchPosWorld[pointer] = newTouchPosWorld;
            touchPosScreen[pointer] = newTouchPosScreen;

            float multiTouchDist2ScreenNew = touchPosScreen[0].dst2(touchPosScreen[1]);

            float zoom = multiTouchDist2ScreenNew - multiTouchDist2Screen;
            gameScreen.zoomedGameField(zoom * ZOOM_MULTIPLICATOR);
            Gdx.app.debug("MyTag", "Touch pointer " + pointer + " zoomed by " + zoom * ZOOM_MULTIPLICATOR);
        }

        multiTouchDist2Screen = new Vector2(touchPosScreen[0]).dst2(touchPosScreen[1]);

        return false;
    }

    public boolean isAiming() {
        return aiming;
    }

    public boolean isDragging() {
        return dragging;
    }


    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        if (!dataSet || button != Input.Buttons.LEFT || pointer > 1) return false;

        touchPosWorld[pointer] = camera.unproject(new Vector3(x, y, 0));
        touchPosScreen[pointer].set(x, y);

        multiTouchDist2Screen = new Vector2(touchPosScreen[0]).dst2(touchPosScreen[1]);

        int tankId = gameController.getActiveTankId();

        float dist2 = new Vector2(gameController.getTanks().get(tankId).getX(), gameController.getTanks().get(tankId).getY()).dst2(touchPosWorld[pointer].x, touchPosWorld[pointer].y);

        if (dist2 < TOL_AIMING_SQUARED) {
            Gdx.app.debug("MyTag", "Aiming " + pointer + " at tank " + tankId);
            aiming = true;
        }

        // TODO check for aiming
        Gdx.app.debug("MyTag", "Touch pointer " + pointer + " down at " + touchPosWorld[pointer]);
        return true;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        Gdx.app.debug("MyTag", "tap");
        return false;
    }

    @Override
    public boolean longPress(float x, float y) {
        Gdx.app.debug("MyTag", "longPress");
        return false;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        Gdx.app.debug("MyTag", "fling");
        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        Gdx.app.debug("MyTag", "pan");

        if (!dataSet) return false;

        Vector3 newTouchPosWorld = camera.unproject(new Vector3(x, y, 0));
        Vector2 newTouchPosScreen = new Vector2(x, y);

        Vector2 delta2 = new Vector2(touchPosScreen[0]);
        delta2.sub(newTouchPosScreen);

        if (!aiming && delta2.len2() > TOL_DRAGGING_SQUARED) {
            dragging = true;
        }

        if (dragging) {
            gameScreen.draggedGameField(-deltaX * DRAG_MULTIPLICATOR, deltaY * DRAG_MULTIPLICATOR);
        } else if (aiming) {
            Vector3 delta3 = new Vector3(newTouchPosWorld);
            int tankId = gameController.getActiveTankId();
            delta3.sub(gameController.getTanks().get(tankId).getX(), gameController.getTanks().get(tankId).getY(), 0);

            gameScreen.aimTank(deltaX, deltaY);
        } else {
            // Ignore
        }

        return false;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        Gdx.app.debug("MyTag", "panStop");
        return false;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        Gdx.app.debug("MyTag", "zoom");
        return false;
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        Gdx.app.debug("MyTag", "pinch");
        return false;
    }

    @Override
    public void pinchStop() {
        Gdx.app.debug("MyTag", "pinchStop");

    }
}
