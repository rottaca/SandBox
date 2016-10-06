package com.rottaca.sandbox.data;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.JsonValue;
import com.rottaca.sandbox.ctrl.ConfigLoader;
import com.rottaca.sandbox.ctrl.SandBox;

/**
 * Created by Andreas on 16.09.2016.
 */
public class Tank extends Group {
    public String name;
    public int health;
    public final int maxHealth;

    public boolean lookingRight;
    public float gunAngle;
    public float power;

    private Image tankGun;
    private Image tankBody;

    private Vector2 tmp = new Vector2();
    private Vector2 centerPos = new Vector2();

    public Tank(JsonValue tankJson) {

        TextureRegion textureRegion;
        textureRegion = SandBox.getTexture(SandBox.TEXTURE_TANKGUN);
        tankGun = new Image(textureRegion);
        tankGun.setBounds(0, 0, textureRegion.getRegionWidth(), textureRegion.getRegionHeight());

        textureRegion = SandBox.getTexture(SandBox.TEXTURE_TANKBODY);
        tankBody = new Image(textureRegion);
        tankBody.setBounds(0, 0, textureRegion.getRegionWidth(), textureRegion.getRegionHeight());

        addActor(tankGun);
        addActor(tankBody);

        tmp.set(tankJson.getInt(ConfigLoader.TANK_TAG_POSX),
                tankJson.getInt(ConfigLoader.TANK_TAG_POSY));
        tmp = stageToLocalCoordinates(tmp);

        setBounds(tmp.x - tankBody.getWidth() / 2, tmp.y - tankBody.getHeight() / 2, tankBody.getWidth(), tankBody.getHeight());

        tankGun.setOrigin(0, tankGun.getHeight() / 2);
        tankGun.setPosition(tankBody.getWidth() / 2, tankBody.getHeight() / 2);


        name = tankJson.getString(ConfigLoader.TANK_TAG_NAME);
        health = tankJson.getInt(ConfigLoader.TANK_TAG_HEALTH);


        maxHealth = health;
        gunAngle = 0;
        lookingRight = tankJson.getString(ConfigLoader.TANK_TAG_LOOK_DIR).compareTo("Right") == 0;
        power = 0;
    }

    public boolean isAlive() {
        return health > 0;
    }

    public Vector2 getCenterPos() {
        centerPos.set(getX() + getWidth() / 2, getY() + getHeight() / 2);
        return centerPos;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        float gunAngleDraw = gunAngle;
        if (!lookingRight)
            gunAngleDraw = -gunAngle + 180;

        tankGun.setRotation(gunAngleDraw);

    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        //Gdx.app.log("MyTag", "Origin: " + tankGun.getOriginX() + "x" + tankGun.getOriginY());
//        tmp.set(0, 0);
//        Vector2 stagePos = localToStageCoordinates(tmp);
//
//        float gunAngleDraw = gunAngle;
//        if (!lookingRight)
//            gunAngleDraw = -gunAngle + 180;
//
//        batch.draw(texTankGun, stagePos.x,
//                stagePos.y - texTankGun.getRegionHeight() / 2 + 2,
//                0,
//                texTankGun.getRegionHeight() / 2,
//                texTankGun.getRegionWidth(),
//                texTankGun.getRegionHeight(),
//                1, 1,
//                gunAngleDraw
//        );
//
//        batch.draw(texTankBody,
//                stagePos.x - texTankBody.getRegionWidth() / 2,
//                stagePos.y - texTankBody.getRegionHeight() / 2);
//
//        int barHeight = 6;
//        int borderWidth = 2;
//
//        float hratio = (float) health / maxHealth;
//        float xL = stagePos.x - texTankBody.getRegionWidth() / 2;
//        float y = stagePos.y - texTankBody.getRegionHeight() / 2 - barHeight;

        // TODO Use white texture to render rectangles
//        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
//        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
//        shapeRenderer.setTransformMatrix(batch.getTransformMatrix());
//
//        shapeRenderer.setColor(Color.BLACK);
//        // Border
//        shapeRenderer.rect(xL - borderWidth,
//                y - barHeight / 2 - borderWidth,
//                texTankBody.getRegionWidth() + 2 * borderWidth,
//                barHeight + 2 * borderWidth);
//
//        if (hratio > 0.5)
//            shapeRenderer.setColor(Color.GREEN);
//        else if (hratio > 0.3)
//            shapeRenderer.setColor(Color.ORANGE);
//        else
//            shapeRenderer.setColor(Color.RED);
//
//        // Health bar
//        shapeRenderer.rect(xL,
//                y - barHeight / 2,
//                hratio * texTankBody.getRegionWidth(),
//                barHeight);
//
//        shapeRenderer.end();
    }
}
