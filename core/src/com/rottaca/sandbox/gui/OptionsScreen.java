package com.rottaca.sandbox.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.rottaca.sandbox.ctrl.SandBox;
import com.rottaca.sandbox.data.Options;

/**
 * Created by Andreas on 10.09.2016.
 */
public class OptionsScreen extends ScreenAdapter {
    private Table table;

    private TextButton buttonBack;
    private TextButton buttonToggleBackgroundMusic;
    private TextButton buttonToggleSoundEffects;

    private SandBox sandBox;

    private Stage stage;

    public OptionsScreen(SandBox sandBox) {
        this.sandBox = sandBox;

        create();
    }

    public void create() {
        stage = new Stage(new ExtendViewport(300, 200));

        Gdx.input.setInputProcessor(stage);

        table = new Table();
        table.setFillParent(true);
        table.setDebug(true);
        stage.addActor(table);

        // Define layout
        buttonBack = new TextButton("Back", SandBox.skin, "default");
        buttonToggleBackgroundMusic = new TextButton("Music: ON", SandBox.skin, "default");
        buttonToggleSoundEffects = new TextButton("Effects: ON", SandBox.skin, "default");

        // Update text according to options
        if (Options.enableBackgroundMusic)
            buttonToggleBackgroundMusic.setText("Music: ON");
        else
            buttonToggleBackgroundMusic.setText("Music: OFF");

        if (Options.enableSoundEffects)
            buttonToggleSoundEffects.setText("Effects: ON");
        else
            buttonToggleSoundEffects.setText("Effects: OFF");

        // Define layout
        float buttonWidth = 100;
        table.add(buttonToggleBackgroundMusic).width(buttonWidth).pad(5);
        table.row();
        table.add(buttonToggleSoundEffects).width(buttonWidth).pad(5);
        table.row();
        table.add(buttonBack).width(buttonWidth).pad(5);

        buttonBack.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                sandBox.goToScreen(SandBox.ScreenName.MAIN);
            }
        });

        buttonToggleBackgroundMusic.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                sandBox.toggleEnableBackgroundMusic();
                if (Options.enableBackgroundMusic)
                    buttonToggleBackgroundMusic.setText("Music: ON");
                else
                    buttonToggleBackgroundMusic.setText("Music: OFF");

            }
        });
        buttonToggleSoundEffects.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                sandBox.toggleEnableSoundEffects();
                if (Options.enableSoundEffects)
                    buttonToggleSoundEffects.setText("Effects: ON");
                else
                    buttonToggleSoundEffects.setText("Effects: OFF");
            }
        });
    }

    @Override
    public void render(float delta) {
        GL20 gl = Gdx.gl;
        gl.glClearColor(1, 1, 1, 1);
        gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        SpriteBatch batch = sandBox.getBatch();
        batch.disableBlending();
        batch.begin();
        stage.act(delta);
        stage.draw();
        batch.end();
    }

    @Override
    public void pause() {

    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}