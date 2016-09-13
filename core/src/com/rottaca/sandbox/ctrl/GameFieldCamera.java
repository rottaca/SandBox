package com.rottaca.sandbox.ctrl;

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

    private Vector2 cameraPos;
    private float scaleFactor;

    private boolean up2Date;

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
        this.cameraPos = new Vector2(0, 0);
        this.scaleFactor = 1f;
        this.up2Date = false;
    }

    public synchronized void updateCamera() {
        if (up2Date)
            return;

        up2Date = true;
        cameraHeight = gameFieldHeight / scaleFactor;
        cameraWidth = gameFieldHeight * aspectRatio / scaleFactor;

        setToOrtho(false, cameraWidth, cameraHeight);
        translate(cameraPos.x, cameraPos.y);
        update();
    }

    public Vector2 getCameraPos() {
        return cameraPos;
    }

    public Vector2 getImageSize() {
        return new Vector2(cameraWidth, cameraHeight);
    }

    public synchronized float setCameraPosX(float x) {
        up2Date = false;

        if (x < 0)
            x = 0;
        else if (x > gameFieldWidth - cameraWidth)
            x = gameFieldWidth - cameraWidth;

        cameraPos.x = x;
        return x;
    }

    public synchronized float setCameraPosY(float y) {
        up2Date = false;

        if (y < 0)
            y = 0;
        else if (y > gameFieldHeight - cameraHeight)
            y = gameFieldHeight - cameraHeight;

        cameraPos.y = y;
        return y;
    }

    public synchronized float setCameraScale(float scaleFactor) {
        up2Date = false;

        if (scaleFactor < 1)
            scaleFactor = 1;
        else if (scaleFactor > 2)
            scaleFactor = 2;

        this.scaleFactor = scaleFactor;

        return scaleFactor;
    }

}
