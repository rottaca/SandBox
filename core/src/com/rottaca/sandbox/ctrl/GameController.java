package com.rottaca.sandbox.ctrl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.rottaca.sandbox.data.Bullet;
import com.rottaca.sandbox.data.Chunk;
import com.rottaca.sandbox.data.FieldConfig;
import com.rottaca.sandbox.data.Level;
import com.rottaca.sandbox.data.Tank;
import com.rottaca.sandbox.gui.GameScreen;

import java.util.ArrayList;
import java.util.HashMap;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.delay;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.removeActor;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.rotateBy;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.run;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

public class GameController extends Stage {

    GameScreen gameScreen = null;

    public final static float GRAVITATION = -30f;
    public final static int HUMAN_TANK_ID = 0;           // Tank 0 is player
    public final static int BULLET_DAMAGE = 400;

    boolean isRunning;
    boolean levelLoaded;
    boolean gameFinished;
    boolean updateRendering;
    boolean roundFinished;

    Integer levelNr;
    Level level;
    int activeTankId;
    boolean playerLost;

    private int maxWind;
    private int wind;

    HashMap<Integer, FieldConfig> fieldConfigHashMap;

    Sound explosionSound;
    Sound tankFiringSound;
    ArrayList<Bullet> bullets;

    Image backgroundImg;

    Group bulletGroup = new Group();
    Group tankGroup = new Group();
    Group gameFieldGroup = new Group();
    Group backgroundGroup = new Group();

    TankAI tankAI;

    Rectangle tmpRect = new Rectangle();
    Rectangle tmpRect2 = new Rectangle();

    public GameController(GameScreen gameScreen, Viewport vp) {
        super(vp);
        this.gameScreen = gameScreen;
        levelNr = -1;
        isRunning = false;
        levelLoaded = false;
        updateRendering = false;
        bullets = new ArrayList<Bullet>();
        gameFinished = false;
        playerLost = false;
        wind = 0;
        maxWind = 10;
        roundFinished = false;

        // Load field config synced... this shouldn't take long
        fieldConfigHashMap = ConfigLoader.loadFieldConfig("fieldConfig.json");
        explosionSound = Gdx.audio.newSound(Gdx.files.internal("BombSound.mp3"));
        tankFiringSound = Gdx.audio.newSound(Gdx.files.internal("TankFiring.mp3"));
        backgroundImg = new Image(SandBox.getTexture(SandBox.TEXTURE_HORIZON));

        // Prepare stage
        clear();
        addActor(backgroundGroup);
        addActor(gameFieldGroup);
        addActor(bulletGroup);
        addActor(tankGroup);


        // Add background
        backgroundGroup.addActor(backgroundImg);

        // Setup AI
        tankAI = new TankAI();

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
        level.tanks.get(0).setActive(true);
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
                //chunks[y][x].setDebug(true);
            }
        }

        // Add tanks
        for (Tank t : level.tanks) {
            tankGroup.addActor(t);
            //t.setDebug(true,true);
        }

    }

    public void setPlayerTankParameters(float gunAngle, float power) {

        Tank t = level.tanks.get(HUMAN_TANK_ID);

        float speedX = (float) Math.cos(Math.toRadians(gunAngle));
        float speedY = (float) Math.sin(Math.toRadians(gunAngle));
        t.setPower(speedX * power, speedY * power);
        t.getTankGun().setRotation((float) Math.toDegrees(Math.atan2(t.getPower().y, t.getPower().x)));
    }

    public void act(float delta) {
        super.act(delta);

        if (!gameFinished) {
            level.gameGrid.updateGrid();
            if (!updateBullets(delta) && roundFinished)
                nextPlayer();
            checkTanks(delta);
        }
    }


    public Level getLevel() {
        return level;
    }

    private boolean updateBullets(float delta) {
        if (bullets.size() == 0)
            return false;

        final ArrayList<Bullet> removedBullets = new ArrayList<Bullet>();

        synchronized (bullets) {
            for (int i = 0; i < bullets.size(); i++) {
                Bullet b = bullets.get(i);
                int x = Math.round(b.getCenterPos().x);
                int y = Math.round(b.getCenterPos().y);

                if (x < 0 || y < 0) {
                    removedBullets.add(b);
                    continue;
                }

                // Hit ground ?
                if (x < getGameFieldWidth() && y < getGameFieldHeight() && level.gameGrid.getField(y, x) >= 0) {
                    // Explode on gamefield
                    level.gameGrid.executeExplosion(y, x, b.damage);
                    removedBullets.add(b);
                    b.setExploding();

                    // Hit tank indirect ? TODO Check
//                    for (int idx = 0; idx < level.tanks.size(); idx++) {
//                        // Tanks can't hit themselves
//                        if (b.tankId == idx || !level.tanks.get(idx).isAlive())
//                            continue;
//
//                        Tank t = level.tanks.get(idx);
//
//
//                        float dx = t.getCenterPos().x - b.getCenterPos().x;
//                        float dy = t.getCenterPos().y - b.getCenterPos().y;
//                        float dist2 = dx * dx + dy * dy;
//
//                        // TODO Check with rectangle intersect
//                        if (dist2 < 30 * 30) {
//                            t.health -= b.damage / dist2;
//                            removedBullets.add(b);
//
//                            gameScreen.queueMessage("Player " + (b.tankId + 1) + " hit Player " + (idx + 1), 1000);
//                        }
//                    }
                }
                // Tank hit direct ?
                for (int idx = 0; idx < level.tanks.size(); idx++) {
                    // Tanks can't hit themselves
                    if (b.tankId == idx || !level.tanks.get(idx).isAlive())
                        continue;

                    Tank t = level.tanks.get(idx);

                    tmpRect.setPosition(t.getX(), t.getY());
                    tmpRect.setSize(t.getWidth(), t.getHeight());
                    tmpRect2.setPosition(b.getX(), b.getY());
                    tmpRect2.setSize(b.getWidth(), b.getHeight());

                    if (tmpRect.overlaps(tmpRect2)) {
                        t.health -= b.damage;
                        removedBullets.add(b);
                        b.setExploding();

                        gameScreen.queueMessage("Player " + (b.tankId + 1) + " hit Player " + (idx + 1), 1000);
                    }
                }
            }
            // Remove from bullet list and as actor
            for (int i = 0; i < removedBullets.size(); i++) {
                Bullet b = removedBullets.get(i);

                if (b.getExploding()) {
                    if (ConfigLoader.prefs.getBoolean(ConfigLoader.PREF_SOUND_FX_ENABLED))
                        explosionSound.play();
                    b.addAction(sequence(delay(0.3f), removeActor()));
                }
                else
                    b.addAction(removeActor());
                bullets.remove(b);

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
        int enemiesDestroyed = 0;
        for (int idx = 0; idx < level.tanks.size(); idx++) {
            if (!level.tanks.get(idx).isAlive()) {
                gameScreen.queueMessage("Player " + (idx + 1) + " destroyed!", 10000);
                if (idx == 0) {
                    gameFinished = true;
                    playerLost = idx == 0;
                } else {
                    enemiesDestroyed++;
                }
            }
        }
        if (enemiesDestroyed == level.tanks.size() - 1) {
            gameFinished = true;
            playerLost = false;
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
        if (level != null && !roundFinished) {

            synchronized (bullets) {
                Bullet b = new Bullet(activeTankId,
                        level.tanks.get(activeTankId).getBulletStartPos().x,
                        level.tanks.get(activeTankId).getBulletStartPos().y,
                        BULLET_DAMAGE,
                        speedX, speedY,
                        wind, GRAVITATION);
                bullets.add(b);
                bulletGroup.addActor(b);
                //b.setDebug(true);
                tankFiringSound.play();
            }
            roundFinished = true;
        }
    }

    public int getWind() {
        return wind;
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

    public void nextPlayer() {
        Tank t = level.tanks.get(activeTankId);
        t.setActive(false);

        // Skip dead tanks
        // TODO Could cause dead lock ?
        do {
            activeTankId++;
            if (activeTankId > level.tanks.size() - 1)
                activeTankId = 0;
        } while (!level.tanks.get(activeTankId).isAlive());

        t = level.tanks.get(activeTankId);
        t.setActive(true);

        wind = (int) Math.round(-maxWind + 2 * maxWind * Math.random());

        // Human player or bot ?
        roundFinished = false;
        if (activeTankId == HUMAN_TANK_ID) {
            gameScreen.playersTurn(true);
            gameScreen.queueMessage("Your turn!", 1500);
        } else {
            gameScreen.playersTurn(false);
            gameScreen.queueMessage("Bots turn!", 1500);
            // Setup tank parameters
            tankAI.prepareTank(level.gameGrid,
                    level.tanks,
                    t, level.tanks.get(0),
                    GRAVITATION, wind);

            // Shoot
            final Tank finalT = t;
            Runnable run = new Runnable() {
                @Override
                public void run() {
                    shoot(finalT.getPower().y, finalT.getPower().x);
                }
            };

            float angle = (float) Math.toDegrees(Math.atan2(t.getPower().y, t.getPower().x));
            angle -= t.getTankGun().getRotation();

            t.getTankGun().addAction(sequence(rotateBy(angle, 1), delay(0.5f), run(run)));
        }
        gameScreen.lookAtTank(t);
    }

    @Override
    public void draw() {
        super.draw();


    }

    public Group getTankGroup() {
        return tankGroup;
    }

    public Group getBulletGroup() {
        return bulletGroup;
    }

    public Group getBackgroundGroup() {
        return backgroundGroup;
    }

    public Group getGameFieldGroup() {
        return gameFieldGroup;
    }
}
