package com.rottaca.sandbox.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.rottaca.sandbox.ctrl.SandBox;

/**
 * Created by Andreas on 10.09.2016.
 */
public class AboutScreen extends ScreenAdapter {
    private Table table;
    private Label label;
    private TextButton buttonBack;
    private SandBox sandBox;

    private Stage stage;
    private TextureRegion menuBackgroundTexture;

    public AboutScreen(SandBox sandBox) {
        this.sandBox = sandBox;

        create();
    }

    public void create() {
        stage = new Stage(new ExtendViewport(300, 200));

        float width = stage.getWidth();
        float height = stage.getHeight();

        Gdx.input.setInputProcessor(stage);

        table = new Table();
        table.setFillParent(true);
        //table.setDebug(true);
        stage.addActor(table);

        label = new Label("Development\nAndreas Rottach\nBackground Music\nhttp://www.bensound.com\nSound Effects\nwww.soundbible.com", SandBox.skin, "defaultBlack");
        buttonBack = new TextButton("Back", SandBox.skin, "default");

        // Define layout
        table.add(label);
        table.row();

        // Define layout
        table.add(buttonBack).width(100).pad(10);

        buttonBack.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                sandBox.goToScreen(SandBox.ScreenName.MAIN);
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
