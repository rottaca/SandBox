package com.rottaca.sandbox.ctrl;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.rottaca.sandbox.gui.AboutScreen;
import com.rottaca.sandbox.gui.GameScreen;
import com.rottaca.sandbox.gui.MainMenuScreen;
import com.rottaca.sandbox.gui.OptionsScreen;

import java.util.HashMap;

public class SandBox extends Game {
    SpriteBatch batch;

    // All screens in the game
    public enum ScreenName {
        MAIN, GAME, OPTIONS, ABOUT
    }

    private Screen activeScreen = null;

    private Music backgroundMusic;

    public static Skin skin;

    private static TextureAtlas textureAtlas;
    private static HashMap<String, TextureRegion> textureRegionHashMap = new HashMap<String, TextureRegion>();
    public static final String TEXTURE_TANKBODY = "TankBody";
    public static final String TEXTURE_TANKGUN = "TankGun";
    public static final String TEXTURE_BULLET = "Bullet";
    public static final String TEXTURE_BULLETLINE = "BulletLine";
    public static final String TEXTURE_HORIZON = "Horizon";
    public static final String TEXTURE_MENUBACKGROUND = "MenuBackground";

    @Override
    public void create() {
        Gdx.app.setLogLevel(Application.LOG_DEBUG);
        initializePreferences();
        // TODO Show loading screen
        loadTextures();

        //skin = new Skin(Gdx.files.internal("uiskin.json"));
        skin = new Skin();
        //skin.add("default-font",font16);
        skin.addRegions(new TextureAtlas(Gdx.files.internal("uiskin.atlas")));
        skin.load(Gdx.files.internal("uiskin.json"));

        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("bensound-epic.mp3"));
        backgroundMusic.setLooping(true);
        backgroundMusic.setVolume(0.6f);

        if (ConfigLoader.prefs.getBoolean(ConfigLoader.PREF_SOUND_BG_ENABLED))
            backgroundMusic.play();

        batch = new SpriteBatch();
        goToScreen(ScreenName.MAIN);
    }

    public void toggleEnableBackgroundMusic() {
        boolean bgMusic = !ConfigLoader.prefs.getBoolean(ConfigLoader.PREF_SOUND_BG_ENABLED);
        ConfigLoader.prefs.putBoolean(ConfigLoader.PREF_SOUND_BG_ENABLED, bgMusic);
        ConfigLoader.prefs.flush();
        if (bgMusic && !backgroundMusic.isPlaying())
            backgroundMusic.play();
        else if (!bgMusic && backgroundMusic.isPlaying())
            backgroundMusic.stop();

    }

    public void toggleEnableSoundEffects() {
        boolean fxSound = !ConfigLoader.prefs.getBoolean(ConfigLoader.PREF_SOUND_FX_ENABLED);
        ConfigLoader.prefs.putBoolean(ConfigLoader.PREF_SOUND_FX_ENABLED, fxSound);
        ConfigLoader.prefs.flush();
    }

    @Override
    public void dispose() {
        backgroundMusic.dispose();
        batch.dispose();
        textureAtlas.dispose();
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

    private void initializePreferences() {
        // Set default if not done
        ConfigLoader.prefs.putBoolean(ConfigLoader.PREF_SOUND_BG_ENABLED, ConfigLoader.prefs.getBoolean(ConfigLoader.PREF_SOUND_BG_ENABLED, true));
        ConfigLoader.prefs.putBoolean(ConfigLoader.PREF_SOUND_FX_ENABLED, ConfigLoader.prefs.getBoolean(ConfigLoader.PREF_SOUND_FX_ENABLED, true));


        ConfigLoader.prefs.flush();
    }

    private static void loadTextures() {
        textureAtlas = new TextureAtlas(Gdx.files.internal("textures/pack.atlas"));

        textureRegionHashMap.put(TEXTURE_TANKBODY, textureAtlas.findRegion(TEXTURE_TANKBODY));
        textureRegionHashMap.put(TEXTURE_TANKGUN, textureAtlas.findRegion(TEXTURE_TANKGUN));
        textureRegionHashMap.put(TEXTURE_BULLET, textureAtlas.findRegion(TEXTURE_BULLET));
        textureRegionHashMap.put(TEXTURE_BULLETLINE, textureAtlas.findRegion(TEXTURE_BULLETLINE));
        textureRegionHashMap.put(TEXTURE_HORIZON, textureAtlas.findRegion(TEXTURE_HORIZON));
        textureRegionHashMap.put(TEXTURE_MENUBACKGROUND, textureAtlas.findRegion(TEXTURE_MENUBACKGROUND));
    }

    public static TextureRegion getTexture(String key) {
        return textureRegionHashMap.get(key);
    }
}
