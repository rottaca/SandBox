package com.rottaca.sandbox.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.rottaca.sandbox.ctrl.ConfigLoader;
import com.rottaca.sandbox.ctrl.LevelOverview;
import com.rottaca.sandbox.ctrl.SandBox;

import java.util.ArrayList;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

/**
 * Created by Andreas on 10.09.2016.
 */
public class LevelScreen extends ScreenAdapter {
    private Table table;
    private Table levelTable;
    private TextButton buttonBack;

    private TextButton buttonNext;
    private TextButton buttonPrev;

    private SandBox sandBox;

    private Stage stage;
    private Image menuBackground;

    private ArrayList<LevelOverview> levelOverviews;

    private final int imgButtonWidth = 45;
    private final int imgButtonHeight = 45;
    private final int cols = 4;
    private final int rows = 3;
    private ArrayList<LevelOverviewButton> levelWidgets;

    private int currPage = 0;

    public LevelScreen(SandBox sandBox) {
        this.sandBox = sandBox;

        levelOverviews = ConfigLoader.getLevelOverview();

        create();
    }

    public void create() {
        stage = new Stage(new ExtendViewport(300, 200));
        Gdx.input.setInputProcessor(stage);

        menuBackground = new Image(SandBox.getTexture(SandBox.TEXTURE_MENUBACKGROUND));
        float imgAspect = menuBackground.getWidth() / menuBackground.getHeight();
        float xL = (stage.getWidth() - stage.getHeight() * imgAspect) / 2;
        menuBackground.setBounds(xL, 0, stage.getHeight() * imgAspect, stage.getHeight() + 1);
        stage.addActor(menuBackground);

        table = new Table();
        table.setFillParent(true);
        //table.setDebug(true);
        table.defaults();

        stage.addActor(table);

        // Define layout
        levelTable = new Table();
        levelTable.defaults();
        //levelTable.setDebug(true);

        buttonBack = new TextButton("Back", SandBox.skin, "default");
        buttonNext = new TextButton("Next", SandBox.skin, "default");
        buttonPrev = new TextButton("Prev", SandBox.skin, "default");

        buttonBack.addAction(sequence(fadeOut(0.0f), fadeIn(SandBox.BUTTON_FADE_DELAY)));
        buttonNext.addAction(fadeOut(0.0f));
        buttonPrev.addAction(fadeOut(0.0f));

        // Define layout
        table.add(buttonPrev).pad(5);
        table.add(levelTable).pad(5).expand();
        table.add(buttonNext).pad(5);
        table.row();
        table.add(buttonBack).pad(5);

        // Construct all level image buttons
        levelWidgets = new ArrayList<LevelOverviewButton>();
        for (int i = 0; i < levelOverviews.size(); i++) {
            LevelOverviewButton tb = new LevelOverviewButton(sandBox, levelOverviews.get(i));
            levelWidgets.add(tb);
        }

        buttonBack.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                sandBox.goToScreen(SandBox.ScreenName.MAIN);
            }
        });
        buttonNext.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showLevelOverview(++currPage);
            }
        });
        buttonPrev.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showLevelOverview(--currPage);
            }
        });

        showLevelOverview(currPage);
    }

    @Override
    public void render(float delta) {
        GL20 gl = Gdx.gl;
        gl.glClearColor(1, 1, 1, 1);
        gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();

    }

    @Override
    public void pause() {

    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    private void showLevelOverview(int page) {
        levelTable.clearChildren();

        int startIdxLevels = page * cols * rows;

        int endIdx = Math.min(cols * rows, levelOverviews.size() - startIdxLevels);
        // Insert
        for (int i = 0; i < endIdx; i++) {
            if (i > 0 && i % cols == 0) {
                levelTable.row();
            }
            Actor a = levelWidgets.get(startIdxLevels + i);
            levelTable.add(a).pad(5).prefSize(imgButtonWidth, imgButtonHeight).uniform();
            a.addAction(sequence(fadeOut(0.0f), fadeIn(0.5f)));
        }

        for (int i = endIdx; i < cols * rows; i++) {
            if (i > 0 && i % cols == 0) {
                levelTable.row();
            }
            levelTable.add(new Actor()).pad(5).prefSize(imgButtonWidth, imgButtonHeight).uniform();

        }

        if (page == 0) {
            buttonPrev.addAction(fadeOut(SandBox.BUTTON_FADE_DELAY));
            buttonPrev.setDisabled(true);
        } else {
            buttonPrev.addAction(fadeIn(SandBox.BUTTON_FADE_DELAY));
            buttonPrev.setDisabled(false);
        }
        if (page >= (float) levelOverviews.size() / (cols * rows) - 1) {
            buttonNext.addAction(fadeOut(SandBox.BUTTON_FADE_DELAY));
            buttonNext.setDisabled(true);
        } else {
            buttonNext.addAction(fadeIn(SandBox.BUTTON_FADE_DELAY));
            buttonNext.setDisabled(false);
        }
    }
}
