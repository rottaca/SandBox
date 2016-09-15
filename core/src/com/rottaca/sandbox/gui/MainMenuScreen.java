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
import com.rottaca.sandbox.ctrl.SandBox;

/**
 * Created by Andreas on 03.09.2016.
 */
public class MainMenuScreen extends ScreenAdapter {
    private SandBox sandBox;
    private Stage stage;

    // Gui Elements
    private TextButton buttonStart;
    private TextButton buttonOptions;
    private TextButton buttonAbout;

    private TextureRegion menuBackgroundTexture;

    // Layout
    private Table table;

    public MainMenuScreen(SandBox sandBox) {
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

        buttonStart = new TextButton("Start", SandBox.skin, "default");
        buttonOptions = new TextButton("Options", SandBox.skin, "default");
        buttonAbout = new TextButton("About", SandBox.skin, "default");

        // Define layout
        float buttonWidth = 100;
        table.add(buttonStart).width(buttonWidth).pad(5);
        table.row();
        table.add(buttonOptions).width(buttonWidth).pad(5);
        table.row();
        table.add(buttonAbout).width(buttonWidth).pad(5);

        // Listeners
        buttonStart.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                sandBox.goToScreen(SandBox.ScreenName.GAME);
            }
        });
        buttonOptions.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                sandBox.goToScreen(SandBox.ScreenName.OPTIONS);
            }
        });
        buttonAbout.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                sandBox.goToScreen(SandBox.ScreenName.ABOUT);
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
