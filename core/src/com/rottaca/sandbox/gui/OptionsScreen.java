package com.rottaca.sandbox.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.rottaca.sandbox.ctrl.ConfigLoader;
import com.rottaca.sandbox.ctrl.SandBox;

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
    private TextureRegion menuBackgroundTexture;

    public OptionsScreen(SandBox sandBox) {
        this.sandBox = sandBox;

        create();
    }

    public void create() {
        stage = new Stage(new ExtendViewport(300, 200));

        Gdx.input.setInputProcessor(stage);

        table = new Table();
        table.setFillParent(true);
        //table.setDebug(true);
        stage.addActor(table);

        // Define layout
        buttonBack = new TextButton("Back", SandBox.skin, "default");
        buttonToggleBackgroundMusic = new TextButton("Music: ON", SandBox.skin, "default");
        buttonToggleSoundEffects = new TextButton("Effects: ON", SandBox.skin, "default");

        // Update text according to options
        if (ConfigLoader.prefs.getBoolean(ConfigLoader.PREF_SOUND_BG_ENABLED))
            buttonToggleBackgroundMusic.setText("Music: ON");
        else
            buttonToggleBackgroundMusic.setText("Music: OFF");

        if (ConfigLoader.prefs.getBoolean(ConfigLoader.PREF_SOUND_FX_ENABLED))
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
                if (ConfigLoader.prefs.getBoolean(ConfigLoader.PREF_SOUND_BG_ENABLED))
                    buttonToggleBackgroundMusic.setText("Music: ON");
                else
                    buttonToggleBackgroundMusic.setText("Music: OFF");

            }
        });
        buttonToggleSoundEffects.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                sandBox.toggleEnableSoundEffects();
                if (ConfigLoader.prefs.getBoolean(ConfigLoader.PREF_SOUND_FX_ENABLED))
                    buttonToggleSoundEffects.setText("Effects: ON");
                else
                    buttonToggleSoundEffects.setText("Effects: OFF");
            }
        });

        menuBackgroundTexture = sandBox.getTexture(SandBox.TEXTURE_MENUBACKGROUND);
    }

    @Override
    public void render(float delta) {
        GL20 gl = Gdx.gl;
        gl.glClearColor(1, 1, 1, 1);
        gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        SpriteBatch batch = sandBox.getBatch();
        batch.disableBlending();

        batch.setProjectionMatrix(stage.getViewport().getCamera().combined);
        batch.begin();
        batch.draw(menuBackgroundTexture, 0, 0,
                stage.getWidth(), stage.getHeight());
        batch.end();

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
