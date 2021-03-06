package com.rottaca.sandbox.data;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
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

    public boolean isActive;
    public boolean lookingRight;
    private Vector2 power;

    private Image tankGun;
    private Image tankBody;

    private TextureRegion texWhite; // Use colored texture to render health bar

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
        tankGun.setPosition(tankBody.getWidth() / 2, 19);

        texWhite = SandBox.getTexture(SandBox.TEXTURE_WHITE);

        name = tankJson.getString(ConfigLoader.TANK_TAG_NAME);
        health = tankJson.getInt(ConfigLoader.TANK_TAG_HEALTH);

        maxHealth = health;
        lookingRight = tankJson.getString(ConfigLoader.TANK_TAG_LOOK_DIR).compareTo("Right") == 0;
        power = new Vector2(lookingRight ? 1 : -1, 0);
    }

    public Vector2 getBulletStartPos() {
        tmp.set(tankGun.getOriginX(), tankGun.getOriginY());
        tmp = tankGun.localToStageCoordinates(tmp);

        return tmp;
    }

    public Vector2 getPower() {
        return power;
    }

    public void setPower(float powerX, float powerY) {
        power.set(powerX, powerY);
    }

    public Actor getTankGun() {
        return tankGun;
    }
    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
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

    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        // Health bar parameters bar height and border width
        int barHeight = 3;
        int borderWidth = 1;
        int barYOffset = -2;

        float hratio = (float) health / maxHealth;
        float xL = getX();
        float y = getY() - barHeight + barYOffset;

        batch.setColor(0, 0, 0, parentAlpha);
        batch.draw(texWhite,
                xL - borderWidth,
                y - barHeight / 2 - borderWidth,
                getWidth() + 2 * borderWidth,
                barHeight + 2 * borderWidth);

        // Only render health bar if tank is alive
        if (health > 0) {
            // Border
            if (hratio > 0.5)
                batch.setColor(0, 1, 0, parentAlpha);
            else if (hratio > 0.3)
                batch.setColor(1, 0.4f, 0, parentAlpha);
            else
                batch.setColor(1, 0, 0, parentAlpha);

            // Health bar
            batch.draw(texWhite,
                    xL,
                    y - barHeight / 2,
                    hratio * getWidth(),
                    barHeight);

            batch.setColor(1, 1, 1, parentAlpha);

            // TODO Render gun parameters if tank is active (power and possible flight route)
            if (isActive) {

            }
        }
    }
}
