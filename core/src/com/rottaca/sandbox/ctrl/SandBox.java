package com.rottaca.sandbox.ctrl;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.rottaca.sandbox.data.Options;
import com.rottaca.sandbox.gui.AboutScreen;
import com.rottaca.sandbox.gui.GameScreen;
import com.rottaca.sandbox.gui.MainMenuScreen;
import com.rottaca.sandbox.gui.OptionsScreen;

public class SandBox extends Game {
    SpriteBatch batch;

    // All screens in the game
    public enum ScreenName {
        MAIN, GAME, OPTIONS, ABOUT
    }

    public Screen activeScreen = null;

    private Music backgroundMusic;

    public static BitmapFont font12, font16;
    public static Skin skin;

    @Override
    public void create() {

        Gdx.app.setLogLevel(Application.LOG_INFO);

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("From Cartoon Blocks.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 12;
        font12 = generator.generateFont(parameter); // font size 12 pixels
        parameter.size = 16;
        font16 = generator.generateFont(parameter); // font size 16 pixels
        generator.dispose(); // don't forget to dispose to avoid memory leaks!

        //skin = new Skin(Gdx.files.internal("uiskin.json"));
        skin = new Skin();
        //skin.add("default-font",font16);
        skin.addRegions(new TextureAtlas(Gdx.files.internal("uiskin.atlas")));
        skin.load(Gdx.files.internal("uiskin.json"));


        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("bensound-epic.mp3"));
        backgroundMusic.setLooping(true);
        backgroundMusic.setVolume(0.6f);

        if (Options.enableBackgroundMusic)
            backgroundMusic.play();

        batch = new SpriteBatch();
        goToScreen(ScreenName.MAIN);
    }

    public void toggleEnableBackgroundMusic() {
        Options.enableBackgroundMusic = !Options.enableBackgroundMusic;
        if (Options.enableBackgroundMusic && !backgroundMusic.isPlaying())
            backgroundMusic.play();
        else if (!Options.enableBackgroundMusic && backgroundMusic.isPlaying())
            backgroundMusic.stop();

    }

    public void toggleEnableSoundEffects() {
        Options.enableSoundEffects = !Options.enableSoundEffects;
    }

    @Override
    public void dispose() {
        backgroundMusic.dispose();
        batch.dispose();
    }

    public SpriteBatch getBatch() {
        return batch;
    }


    public void goToScreen(ScreenName screenName) {
        if (activeScreen != null)
            activeScreen.dispose();

        switch (screenName) {
            case GAME:
                activeScreen = new GameScreen(this);
                break;
            case MAIN:
                activeScreen = new MainMenuScreen(this);
                break;
            case ABOUT:
                activeScreen = new AboutScreen(this);
                break;
            case OPTIONS:
                activeScreen = new OptionsScreen(this);
                break;
            default:
                break;
        }
        setScreen(activeScreen);
    }
}
