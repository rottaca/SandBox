package com.rottaca.sandbox.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.rottaca.sandbox.ctrl.SandBox;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.delay;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

/**
 * Created by Andreas on 10.09.2016.
 */
public class AboutScreen extends ScreenAdapter {
    private Table table;
    private Label label;
    private TextButton buttonBack;
    private SandBox sandBox;

    private Stage stage;

    private Image menuBackground;

    public AboutScreen(SandBox sandBox) {
        this.sandBox = sandBox;

        create();
    }

    public void create() {
        stage = new Stage(new ExtendViewport(300, 200));
        menuBackground = new Image(SandBox.getTexture(SandBox.TEXTURE_MENUBACKGROUND));
        float imgAspect = menuBackground.getWidth() / menuBackground.getHeight();
        float x = (stage.getWidth() - stage.getHeight() * imgAspect) / 2;
        menuBackground.setBounds(x, 0, stage.getHeight() * imgAspect, stage.getHeight() + 1);
        stage.addActor(menuBackground);
        Gdx.input.setInputProcessor(stage);

        table = new Table();
        table.setFillParent(true);
        //table.setDebug(true);
        stage.addActor(table);

        label = new Label("Development\nAndreas Rottach\nBackground Music\nwww.bensound.com\nSound Effects\nwww.soundbible.com", SandBox.skin, "defaultBlack");
        buttonBack = new TextButton("Back", SandBox.skin, "default");
        label.addAction(sequence(fadeOut(0.0f), fadeIn(SandBox.BUTTON_FADE_DELAY)));
        buttonBack.addAction(sequence(fadeOut(0.0f), delay(SandBox.BUTTON_FADE_DELAY), fadeIn(SandBox.BUTTON_FADE_DELAY)));

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
}
