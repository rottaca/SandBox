package com.rottaca.sandbox.ctrl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.rottaca.sandbox.data.FieldConfig;
import com.rottaca.sandbox.data.GameGrid;
import com.rottaca.sandbox.data.Level;
import com.rottaca.sandbox.data.Tank;

import java.util.HashMap;


/**
 * Created by Andreas on 05.09.2016.
 */
public class ConfigLoader {
    // Map information
    public static final String MAP_TAG_CONFIG = "MapConfig";
    public static final String MAP_TAG_NAME = "Name";
    public static final String MAP_TAG_TANKS = "Tanks";

    // Tank information
    public static final String TANK_TAG_NAME = "Name";
    public static final String TANK_TAG_HEALTH = "Health";
    public static final String TANK_TAG_POSX = "PosX";
    public static final String TANK_TAG_POSY = "PosY";
    public static final String TANK_TAG_LOOK_DIR = "LookDir";

    // Field information
    public static final String FIELDS_TAG = "Fields";
    public static final String FIELD_TAG_NAME = "Name";
    public static final String FIELD_TAG_MAPID = "MapId";
    public static final String FIELD_TAG_HARDNESS = "Hardness";
    public static final String FIELD_TAG_DENSITY = "Density";
    public static final String FIELD_TAG_ISFLUID = "IsFluid";
    public static final String FIELD_TAG_COLOR = "Color";

    // Global setting for the game
    public static final Preferences prefs = Gdx.app.getPreferences("Preferences");
    public static final String PREF_SOUND_BG_ENABLED = "BackgroundMusicEnabled";
    public static final String PREF_SOUND_FX_ENABLED = "FXSoundEnabled";


    public static HashMap<Integer, FieldConfig> loadFieldConfig(String fileName) {
        FileHandle file = Gdx.files.internal(fileName);
        String mapConfigTxt = file.readString();

        JsonValue root = new JsonReader().parse(mapConfigTxt);

        HashMap<Integer, FieldConfig> fieldConfigHashMap = new HashMap<Integer, FieldConfig>();

        JsonValue fieldArray = root.get(FIELDS_TAG);
        for (JsonValue jsonField : fieldArray.iterator()) // iterator() returns a list of children
        {
            FieldConfig fieldConfig = new FieldConfig(jsonField);
            fieldConfigHashMap.put(fieldConfig.mapId, fieldConfig);
        }

        return fieldConfigHashMap;
    }

    public static Level loadLevel(String folderName, HashMap<Integer, FieldConfig> fieldConfigHashMap) {
        Level level = new Level();
        String mapConfigName = folderName + "/config.json";
        String mapFileName = folderName + "/map.png";
        Gdx.app.debug("MyTag", "Loading map config...");
        FileHandle mapConfigFile = Gdx.files.internal(mapConfigName);
        JsonValue rootMapConfig = new JsonReader().parse(mapConfigFile.readString());
        JsonValue json = rootMapConfig.get(MAP_TAG_CONFIG);
        level.name = json.getString(MAP_TAG_NAME);
        JsonValue tankArray = json.get(MAP_TAG_TANKS);

        for (JsonValue tankJson : tankArray.iterator()) // iterator() returns a list of children
        {
            Tank tank = new Tank(tankJson);
            level.tanks.add(tank);
        }

        Gdx.app.debug("MyTag", "Loading map image...");
        Pixmap mapPixmap = new Pixmap(Gdx.files.internal(mapFileName));
        level.gameGrid = new GameGrid(mapPixmap, fieldConfigHashMap);

        return level;
    }

}
