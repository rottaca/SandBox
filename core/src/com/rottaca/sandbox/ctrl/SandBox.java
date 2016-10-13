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
import com.badlogic.gdx.utils.Array;
import com.rottaca.sandbox.gui.AboutScreen;
import com.rottaca.sandbox.gui.GameScreen;
import com.rottaca.sandbox.gui.LevelScreen;
import com.rottaca.sandbox.gui.MainMenuScreen;
import com.rottaca.sandbox.gui.OptionsScreen;

import java.util.HashMap;

public class SandBox extends Game {
    SpriteBatch batch;

    // All screens in the game
    public enum ScreenName {
        MAIN, GAME, OPTIONS, ABOUT, LEVELS
    }

    private Screen activeScreen = null;

    private Music backgroundMusic;

    public static Skin skin;

    private static TextureAtlas mainTextureAtlas;
    private static HashMap<String, TextureRegion> textureRegionHashMap = new HashMap<String, TextureRegion>();
    public static final String TEXTURE_TANKBODY = "TankBody";
    public static final String TEXTURE_TANKGUN = "TankGun";
    public static final String TEXTURE_BULLET = "Bullet";
    public static final String TEXTURE_BULLETLINE = "BulletLine";
    public static final String TEXTURE_HORIZON = "Horizon";
    public static final String TEXTURE_MENUBACKGROUND = "MenuBackground";
    public static final String TEXTURE_WHITE = "White";
    public static final String TEXTURE_STAR_FILLED = "StarFilled";
    public static final String TEXTURE_STAR_UNFILLED = "StarUnfilled";

    private static TextureAtlas bulletExplusionTextureAtlas;

    public static final float BUTTON_FADE_DELAY = 0.25f;

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
        mainTextureAtlas.dispose();
    }

    public SpriteBatch getBatch() {
        return batch;
    }

    public void goToScreen(ScreenName screenName) {
        goToScreen(screenName, -1);
    }

    public void goToScreen(ScreenName screenName, Object param1) {
        if (activeScreen != null)
            activeScreen.dispose();

        switch (screenName) {
            case GAME:
                activeScreen = new GameScreen(this, (Integer) param1);
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
            case LEVELS:
                activeScreen = new LevelScreen(this);
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
        mainTextureAtlas = new TextureAtlas(Gdx.files.internal("textures/main/pack.atlas"));

        textureRegionHashMap.put(TEXTURE_TANKBODY, mainTextureAtlas.findRegion(TEXTURE_TANKBODY));
        textureRegionHashMap.put(TEXTURE_TANKGUN, mainTextureAtlas.findRegion(TEXTURE_TANKGUN));
        textureRegionHashMap.put(TEXTURE_BULLET, mainTextureAtlas.findRegion(TEXTURE_BULLET));
        textureRegionHashMap.put(TEXTURE_BULLETLINE, mainTextureAtlas.findRegion(TEXTURE_BULLETLINE));
        textureRegionHashMap.put(TEXTURE_HORIZON, mainTextureAtlas.findRegion(TEXTURE_HORIZON));
        textureRegionHashMap.put(TEXTURE_MENUBACKGROUND, mainTextureAtlas.findRegion(TEXTURE_MENUBACKGROUND));
        textureRegionHashMap.put(TEXTURE_WHITE, mainTextureAtlas.findRegion(TEXTURE_WHITE));
        textureRegionHashMap.put(TEXTURE_STAR_FILLED, mainTextureAtlas.findRegion(TEXTURE_STAR_FILLED));
        textureRegionHashMap.put(TEXTURE_STAR_UNFILLED, mainTextureAtlas.findRegion(TEXTURE_STAR_UNFILLED));

        bulletExplusionTextureAtlas = new TextureAtlas(Gdx.files.internal("textures/bulletExplosion/pack.atlas"));
        bulletExplusionTextureAtlas.getRegions();
    }

    public static TextureRegion getTexture(String key) {
        return textureRegionHashMap.get(key);
    }

    public static Array<TextureAtlas.AtlasRegion> getTexturesBulletExplosion() {
        return bulletExplusionTextureAtlas.getRegions();
    }
}
