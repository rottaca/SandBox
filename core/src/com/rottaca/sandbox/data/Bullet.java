package com.rottaca.sandbox.data;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.rottaca.sandbox.ctrl.SandBox;

public class Bullet extends Image {
    public int tankId;
    public float damage;
    public Vector2 speed = new Vector2();
    public Vector2 acc = new Vector2();

    private Vector2 centerPos = new Vector2();
    private boolean exploding;

    private Animation explosionAnimation;
    private TextureRegion keyFrame;
    private float stateTime;

    public Bullet(int tankId, float x, float y, float damage, float speedX, float speedY, float accX, float accY) {
        TextureRegion textureRegion = SandBox.getTexture(SandBox.TEXTURE_BULLET);
        setDrawable(new TextureRegionDrawable(textureRegion));

        setBounds(x - textureRegion.getRegionWidth() / 2, y - textureRegion.getRegionHeight() / 2,
                textureRegion.getRegionWidth(), textureRegion.getRegionHeight());

        speed.set(speedX, speedY);
        acc.set(accX, accY);
        this.damage = damage;
        this.tankId = tankId;
        exploding = false;
        stateTime = 0;

        explosionAnimation = new Animation(0.1f, SandBox.getTexturesBulletExplosion());
    }

    public Vector2 getCenterPos() {
        centerPos.set(getX() + getWidth() / 2, getY() + getHeight() / 2);
        return centerPos;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (!exploding) {
            speed.add(acc.x * delta, acc.y * delta);
            setPosition(getX() + speed.x * delta, getY() + speed.y * delta);
        }
    }

    public void setExploding() {
        exploding = true;
    }

    public boolean getExploding() {
        return exploding;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (!exploding)
            super.draw(batch, parentAlpha);
        else {
            stateTime += Gdx.graphics.getDeltaTime();
            keyFrame = explosionAnimation.getKeyFrame(stateTime, true);
            batch.draw(keyFrame, getX() - keyFrame.getRegionWidth() / 2, getY() - keyFrame.getRegionHeight() / 2);
        }
    }
}
