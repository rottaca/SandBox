package com.rottaca.sandbox.gui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.rottaca.sandbox.ctrl.LevelOverview;
import com.rottaca.sandbox.ctrl.SandBox;

/**
 * Created by Andreas on 13.10.2016.
 */

public class LevelOverviewButton extends TextButton {

    private SandBox sandbox;
    private LevelOverview levelOverview;
    private TextureRegion texStarFilled;
    private TextureRegion texStarUnfilled;

    public LevelOverviewButton(SandBox sandbox, LevelOverview levelOverview) {
        super(String.valueOf(levelOverview.levelNr), SandBox.skin, "default");
        this.sandbox = sandbox;
        this.levelOverview = levelOverview;
        texStarFilled = SandBox.getTexture(SandBox.TEXTURE_STAR_FILLED);
        texStarUnfilled = SandBox.getTexture(SandBox.TEXTURE_STAR_UNFILLED);

        addListener(new MyClickListener(sandbox, levelOverview.levelNr));
        getLabel().setAlignment(Align.top);
    }

    private class MyClickListener extends ClickListener {
        private SandBox sandbox;
        private int levelNr;

        public MyClickListener(SandBox sandbox, int levelNr) {
            this.sandbox = sandbox;
            this.levelNr = levelNr;
        }

        @Override
        public void clicked(InputEvent event, float x, float y) {
            sandbox.goToScreen(SandBox.ScreenName.GAME, levelNr);
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        float padX = 3;
        float padY = 7;
        float h = getHeight();
        float w = getWidth();
        float x = getX() + padX;
        float y = getY() + padY;
        float starW = (w - padX * 4) / 3;
        float starH = starW * w / h;

        int maxStars = 3;
        int collectedStars = Math.max(levelOverview.rating, 0);

        for (int i = 0; i < collectedStars; i++) {
            batch.draw(texStarFilled, x + i * (starW + padX), y, starW, starH);
        }
        for (int i = collectedStars; i < maxStars; i++) {
            batch.draw(texStarUnfilled, x + i * (starW + padX), y, starW, starH);
        }
    }
}
