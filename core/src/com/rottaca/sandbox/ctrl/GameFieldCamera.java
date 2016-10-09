package com.rottaca.sandbox.ctrl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Andreas on 08.09.2016.
 */
public class GameFieldCamera extends OrthographicCamera {

    private float gameFieldHeight;
    private float gameFieldWidth;

    private float cameraHeight;
    private float cameraWidth;
    private float aspectRatio;
    // TODO use position from parent instead of currCamPos
    private Vector2 newCamPos;
    private Vector2 currCamPos;
    private float newZoomFactor;
    private float currZoomFactor;

    private final float ANIMATE_MOVE_SPEED = 3f;
    private final float ANIMATE_ZOOM_SPEED = 1f;

    private boolean up2Date;

    private Vector2 tmp = new Vector2();

    /**
     * Constructs a camera that renders the game field. It moves along the x axis along the map.
     * Scaling and moving along y is possible but the camera is always in the gamefield.
     *
     * @param gameFieldHeight
     * @param gameFieldWidth
     * @param viewportRatioWPerH
     */
    public GameFieldCamera(float gameFieldHeight, float gameFieldWidth, float viewportRatioWPerH) {
        this.gameFieldHeight = gameFieldHeight;
        this.gameFieldWidth = gameFieldWidth;
        this.aspectRatio = viewportRatioWPerH;
        this.newCamPos = new Vector2(0, 0);
        this.newZoomFactor = 1f;
        this.currCamPos = new Vector2(0, 0);
        this.currZoomFactor = 1f;
        this.up2Date = false;
    }

    public synchronized void act(float delta) {
        up2Date = false;

        tmp.set(newCamPos);
        tmp.sub(currCamPos);

        //Gdx.app.log("MyTag", " new: " + newCamPos + "curr: " + currCamPos);

        if (tmp.len() < 0.5f) {
            currCamPos.set(newCamPos);
        } else {
            tmp.x *= ANIMATE_MOVE_SPEED * delta;
            tmp.y *= ANIMATE_MOVE_SPEED * delta;

            Gdx.app.log("MyTag", " deltaPos: " + tmp);
            currCamPos.add(tmp);
        }

        if (newZoomFactor - currZoomFactor < 0.1f) {
            currZoomFactor = newZoomFactor;
        } else {
            Gdx.app.log("MyTag", "deltaZoom: " + (newZoomFactor - currZoomFactor) * ANIMATE_ZOOM_SPEED * delta);
            currZoomFactor += (newZoomFactor - currZoomFactor) * ANIMATE_ZOOM_SPEED * delta;
        }

        // TODO Zoom to camera center

        cameraHeight = gameFieldHeight / currZoomFactor;
        cameraWidth = gameFieldHeight * aspectRatio / currZoomFactor;
    }

    public synchronized void update() {
        if (up2Date) {
            return;
        }

        up2Date = true;
        setToOrtho(false, cameraWidth, cameraHeight);
        translate(currCamPos.x, currCamPos.y);
        super.update();
    }

    public Vector2 getCurrCamPos() {
        return currCamPos;
    }

    public Vector2 getImageSize() {
        return new Vector2(cameraWidth, cameraHeight);
    }

    public float getCurrZoomFactor() {
        return currZoomFactor;
    }


    public synchronized float setCameraPosX(float x, boolean animate) {
        up2Date = false;

        if (x < 0)
            x = 0;
        else if (x > gameFieldWidth - cameraWidth)
            x = gameFieldWidth - cameraWidth;

        newCamPos.x = x;
        if (!animate) {
            currCamPos.x = x;
        }
        return x;
    }

    public synchronized float setCameraPosY(float y, boolean animate) {
        up2Date = false;

        if (y < 0)
            y = 0;
        else if (y > gameFieldHeight - cameraHeight)
            y = gameFieldHeight - cameraHeight;

        newCamPos.y = y;
        if (!animate) {
            currCamPos.y = y;
        }
        return y;
    }

    public synchronized float setCameraZoom(float ZoomFactor, boolean animate) {
        up2Date = false;

        // TODO Allow zooming out
        if (ZoomFactor < 1)
            ZoomFactor = 1;
        else if (ZoomFactor > 2)
            ZoomFactor = 2;

        newZoomFactor = ZoomFactor;

        if (!animate) {
            currZoomFactor = ZoomFactor;
        }
        return ZoomFactor;
    }

    public void stopAnimation() {
        newZoomFactor = currZoomFactor;
        newCamPos.set(currCamPos);
    }
}
