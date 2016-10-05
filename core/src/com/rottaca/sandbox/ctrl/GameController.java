package com.rottaca.sandbox.ctrl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.rottaca.sandbox.data.Bullet;
import com.rottaca.sandbox.data.Chunk;
import com.rottaca.sandbox.data.FieldConfig;
import com.rottaca.sandbox.data.Level;
import com.rottaca.sandbox.data.Tank;
import com.rottaca.sandbox.gui.GameScreen;

import java.util.ArrayList;
import java.util.HashMap;

public class GameController extends Stage {

    GameScreen gameScreen = null;

    final static float GRAVITATION = -0.01f;

    boolean isRunning;
    boolean levelLoaded;
    boolean gameFinished;
    boolean updateRendering;

    Integer levelNr;
    Level level;
    int activeTankId;
    boolean playerLost;

    HashMap<Integer, FieldConfig> fieldConfigHashMap;

    Sound explosionSound;
    ArrayList<Bullet> bullets;

    Image backgroundImg;

    Group bulletGroup = new Group();
    Group tankGroup = new Group();
    Group gameFieldGroup = new Group();
    Group backgroundGroup = new Group();

    public GameController(GameScreen gameScreen) {
        this.gameScreen = gameScreen;
        this.levelNr = -1;
        this.isRunning = false;
        this.levelLoaded = false;
        this.updateRendering = false;
        this.bullets = new ArrayList<Bullet>();
        this.gameFinished = false;
        this.playerLost = false;

        // Load field config synced... this shouldn't take long
        fieldConfigHashMap = ConfigLoader.loadFieldConfig("fieldConfig.json");
        explosionSound = Gdx.audio.newSound(Gdx.files.internal("BombSound.mp3"));
        backgroundImg = new Image(SandBox.getTexture(SandBox.TEXTURE_HORIZON));

        // Prepare stage
        clear();
        addActor(backgroundGroup);
        addActor(gameFieldGroup);
        addActor(bulletGroup);
        addActor(tankGroup);

        // Add background
        backgroundGroup.addActor(backgroundImg);

    }

    public void loadLevel(int nr) {
        if (nr > 0) {
            Gdx.app.debug("MyTag", "Loading level...");
            level = ConfigLoader.loadLevel("maps/map" + nr, fieldConfigHashMap);
            Gdx.app.debug("MyTag", "Done. Loaded level \"" + level.name + "\"");
        } else {
            Gdx.app.error("MyTag", "No level to load specified!");
            return;
        }

        // Clear all groups
        gameFieldGroup.clear();
        tankGroup.clear();
        bulletGroup.clear();

        // Initialize parameters
        activeTankId = 0;
        levelNr = nr;
        playerLost = false;

        // Tell gui that loading is done
        isRunning = true;
        levelLoaded = true;

        // Adapt background size to gamefield size
        backgroundImg.setBounds(0, 0, level.gameGrid.getWdith(), level.gameGrid.getHeight());

        // Add chunks to stage
        Chunk[][] chunks = level.gameGrid.getChunks();

        for (int y = 0; y < chunks.length; y++) {
            for (int x = 0; x < chunks[0].length; x++) {
                gameFieldGroup.addActor(chunks[y][x]);
            }
        }

        // Add tanks
        for (Tank t : level.tanks) {
            tankGroup.addActor(t);
        }
    }

    public void setCurrentTankParameters(float gunAngle, float power) {

        Tank t = level.tanks.get(activeTankId);

        t.gunAngle = gunAngle;
        t.power = power;
    }

    public void act(float delta) {
        super.act(delta);

        level.gameGrid.updateGrid();
        updateBullets(delta);
        checkTanks(delta);
    }


    public Level getLevel() {
        return level;
    }

    private boolean updateBullets(float delta) {
        if (bullets.size() == 0)
            return false;

        ArrayList<Bullet> removedBullets = new ArrayList<Bullet>();

        synchronized (bullets) {
            for (int i = 0; i < bullets.size(); i++) {
                Bullet b = bullets.get(i);
                int x = Math.round(b.getCenterPos().x);
                int y = Math.round(b.getCenterPos().y);

                if (x < 0 || y < 0) {
                    removedBullets.add(b);
                    continue;
                }
                    // TODO Synchronize ?
                // Hit ground ?
                if (x < getGameFieldWidth() && y < getGameFieldHeight() && level.gameGrid.getField(y, x) >= 0) {
                    // Explode on gamefield
                    level.gameGrid.executeExplosion(y, x, b.damage);

                    removedBullets.add(b);
                    if (ConfigLoader.prefs.getBoolean(ConfigLoader.PREF_SOUND_FX_ENABLED))
                        explosionSound.play();

                    // Hit tank indirect ? TODO Check
                    for (int idx = 0; idx < level.tanks.size(); idx++) {
                        // Tanks can't hit themselves
                        if (b.tankId == idx || !level.tanks.get(idx).isAlive())
                            continue;

                        Tank t = level.tanks.get(idx);


                        float dx = t.getCenterPos().x - b.getCenterPos().x;
                        float dy = t.getCenterPos().y - b.getCenterPos().y;
                        float dist2 = dx * dx + dy * dy;

                        // TODO Check with rectangle intersect
                        if (dist2 < 30 * 30) {
                            t.health -= b.damage / dist2;
                            removedBullets.add(b);

                            gameScreen.queueMessage("Player " + (b.tankId + 1) + " hit Player " + (idx + 1), 1000);
                        }
                    }
                }
                // Tank hit direct ?
                for (int idx = 0; idx < level.tanks.size(); idx++) {
                    // Tanks can't hit themselves
                    if (b.tankId == idx || !level.tanks.get(idx).isAlive())
                        continue;

                    Tank t = level.tanks.get(idx);

                    float dx = t.getCenterPos().x - b.getCenterPos().x;
                    float dy = t.getCenterPos().y - b.getCenterPos().y;
                    float dist2 = dx * dx + dy * dy;

                    // TODO Check with rectangle intersect
                    if (dist2 < 25 * 25) {
                        t.health -= b.damage;
                        removedBullets.add(b);

                        if (ConfigLoader.prefs.getBoolean(ConfigLoader.PREF_SOUND_FX_ENABLED))
                            explosionSound.play();

                        gameScreen.queueMessage("Player " + (b.tankId + 1) + " hit Player " + (idx + 1), 1000);
                    }
                }
            }
            // Remove from bullet list and as actor
            for (int i = 0; i < removedBullets.size(); i++) {
                bulletGroup.removeActor(removedBullets.get(i));
                bullets.remove(removedBullets.get(i));
            }
        }
        return true;
    }

    public boolean isGameFinished() {
        return gameFinished;
    }

    public boolean isLevelLoaded() {
        return levelLoaded;
    }

    private boolean checkTanks(float delta) {
        for (int idx = 0; idx < level.tanks.size(); idx++) {
            if (!level.tanks.get(idx).isAlive()) {
                gameScreen.queueMessage("Player " + (idx + 1) + " destroyed!", 10000);
                gameFinished = true;
                playerLost = idx == 0;
            }
        }
        return true;
    }

    public Chunk[][] getChunks() {
        return level.gameGrid.getChunks();
    }

    public ArrayList<Bullet> getBullets() {
        return bullets;
    }

    public void shoot(float speedY, float speedX) {
        if (level != null) {
            synchronized (bullets) {
                Bullet b = new Bullet(activeTankId,
                        level.tanks.get(activeTankId).getCenterPos().x,
                        level.tanks.get(activeTankId).getCenterPos().y,
                        400,
                        speedX, speedY,
                        0, GRAVITATION);
                bullets.add(b);
                bulletGroup.addActor(b);
            }

            activeTankId++;
            if (activeTankId > level.tanks.size() - 1)
                activeTankId = 0;
            gameScreen.queueMessage("Player " + (activeTankId + 1) + ", your turn!", 1500);
        }
    }

    public boolean getPlayerLost() {
        return playerLost;
    }
    public int getGameFieldWidth() {
        return level.gameGrid.getWdith();
    }

    public int getGameFieldHeight() {
        return level.gameGrid.getHeight();
    }

    public boolean isRunning() {
        return isRunning;
    }

    public ArrayList<Tank> getTanks() {
        return level.tanks;
    }

    public int getActiveTankId() {
        return activeTankId;
    }
}
