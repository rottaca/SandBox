package com.rottaca.sandbox.ctrl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.rottaca.sandbox.data.Bullet;
import com.rottaca.sandbox.data.Chunk;
import com.rottaca.sandbox.data.FieldConfig;
import com.rottaca.sandbox.data.Level;
import com.rottaca.sandbox.data.Options;
import com.rottaca.sandbox.gui.GameScreen;

import java.util.ArrayList;
import java.util.HashMap;

public class GameController implements Runnable {

    GameScreen gameScreen = null;

    long lastFrameTime = 0;
    final static int FPS = 30;
    final static float GRAVITATION = -0.01f;

    Thread thread;
    boolean pause;
    boolean started;
    boolean levelLoaded;
    boolean updateRendering;

    Integer levelNr;
    Level level;

    HashMap<Integer, FieldConfig> fieldConfigHashMap;

    Sound explosionSound;
    ArrayList<Bullet> bullets;

    public GameController(GameScreen gameScreen) {
        this.gameScreen = gameScreen;
        this.pause = false;
        this.levelNr = -1;
        this.started = false;
        this.levelLoaded = false;
        this.updateRendering = false;
        this.bullets = new ArrayList<Bullet>();

        // Load field config synced... this shouldn't take long
        fieldConfigHashMap = ConfigLoader.loadFieldConfig("fieldConfig.json");
        explosionSound = Gdx.audio.newSound(Gdx.files.internal("BombSound.mp3"));
    }

    @Override
    public void run() {
        lastFrameTime = 0;
        int frameDelta = 1000 / FPS;
        Gdx.app.debug("MyTag", "Starting game thread...");

        synchronized (levelNr) {
            if (levelNr > 0) {
                Gdx.app.debug("MyTag", "Loading level...");
                level = ConfigLoader.loadLevel("maps/map" + levelNr, fieldConfigHashMap);
                Gdx.app.debug("MyTag", "Done. Loaded level \"" + level.mapConfig.name + "\"");
            } else {
                Gdx.app.error("MyTag", "No level to load specified!");
                return;
            }
        }

        gameScreen.requestRendering();
        gameScreen.levelLoaded();
        started = true;
        levelLoaded = true;

        while (!Thread.currentThread().isInterrupted()) {

            if (pause) {
                pause = false;
                try {
                    Gdx.app.debug("MyTag", "Game paused...");
                    synchronized (thread) {
                        thread.wait();
                    }
                    Gdx.app.debug("MyTag", "Game resumed...");

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            // Actual game logic goes here
            updateRendering = level.gameGrid.updateGrid();
            updateRendering = updateRendering | updateBullets();


            // Render if necessary
            if (updateRendering)
                gameScreen.requestRendering();

            // Sleep as long as necessary to keep framerate
            long sleepTime = frameDelta - (System.currentTimeMillis() - lastFrameTime);
            lastFrameTime = System.currentTimeMillis();
            sleepTime = Math.max(sleepTime, 0);

            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean updateBullets() {
        if (bullets.size() == 0)
            return false;

        ArrayList<Bullet> removedBullets = new ArrayList<Bullet>();

        synchronized (bullets) {
            for (int i = 0; i < bullets.size(); i++) {
                Bullet b = bullets.get(i);
                b.update(0, GRAVITATION);

                int x = Math.round(b.getX());
                int y = Math.round(b.getY());

                if (x < 0 || y < 0)
                    removedBullets.add(b);

                    // TODO Synchronize ?
                else if (x < getGameFieldWidth() && y < getGameFieldHeight() && level.gameGrid.getField(y, x) >= 0) {
                    level.gameGrid.executeExplosion(y, x, b.getDamage());
                    removedBullets.add(b);
                    if (Options.enableSoundEffects)
                        explosionSound.play();
                }
            }
            // Remove from bullet list
            for (int i = 0; i < removedBullets.size(); i++) {
                bullets.remove(removedBullets.get(i));
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

    public void clickedOnGameField(int y, int x) {
        if (level != null) {
            //level.gameGrid.getChunkManager().invaidateCoordinate(y, x);
            //level.gameGrid.executeExplosion(y,x,10);
            synchronized (bullets) {
                bullets.add(new Bullet(y, x, 10, (float) (-0.7f + Math.random() * 1.4f), (float) (Math.random() * 0.7f)));
                Gdx.app.log("MyTag", "Bullet Count: " + bullets.size());
            }
        }
    }

    public void startLevel(int levelNr) {
        if (thread != null)
            stopLevel();

        synchronized (this.levelNr) {
            this.levelNr = levelNr;
        }

        thread = new Thread(this);
        thread.start();
    }

    public void stopLevel() {
        if (thread != null) {
            thread.interrupt();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        started = false;
        levelLoaded = false;
    }

    public void pause() {
        Gdx.app.debug("MyTag", "Pausing game...");
        pause = true;
    }

    public void resume() {
        Gdx.app.debug("MyTag", "Resuming game...");
        synchronized (thread) {
            thread.notify();
        }
    }

    public int getGameFieldWidth() {
        return level.gameGrid.getWdith();
    }

    public int getGameFieldHeight() {
        return level.gameGrid.getHeight();
    }

    public boolean isStarted() {
        return started;
    }
}
