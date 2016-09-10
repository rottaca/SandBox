package com.rottaca.sandbox.ctrl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.rottaca.sandbox.data.FieldConfig;
import com.rottaca.sandbox.data.GameGrid;
import com.rottaca.sandbox.data.Level;
import com.rottaca.sandbox.data.MapConfig;

import java.util.HashMap;

/**
 * Created by Andreas on 05.09.2016.
 */
public class ConfigLoader {

    public static final String MAP_CONFIG_TAG = "MapConfig";
    public static final String MAP_NAME_TAG = "Name";

    public static final String FIELDS_TAG = "Fields";
    public static final String FIELD_TAG_NAME = "Name";
    public static final String FIELD_TAG_MAPID = "MapId";
    public static final String FIELD_TAG_HARDNESS = "Hardness";
    public static final String FIELD_TAG_DENSITY = "Density";
    public static final String FIELD_TAG_ISFLUID = "IsFluid";
    public static final String FIELD_TAG_COLOR = "Color";

    public static MapConfig loadMapConfigFile(String fileName) {
        FileHandle file = Gdx.files.internal(fileName);
        String mapConfigTxt = file.readString();

        JsonValue root = new JsonReader().parse(mapConfigTxt);
        MapConfig mapConfig = new MapConfig();

        JsonValue json = root.get(MAP_CONFIG_TAG);
        mapConfig.name = json.getString(MAP_NAME_TAG);

        return mapConfig;
    }

    public static HashMap<Integer, FieldConfig> loadFieldConfig(String fileName) {
        FileHandle file = Gdx.files.internal(fileName);
        String mapConfigTxt = file.readString();

        JsonValue root = new JsonReader().parse(mapConfigTxt);

        HashMap<Integer, FieldConfig> fieldConfigHashMap = new HashMap<Integer, FieldConfig>();

        JsonValue fieldArray = root.get(FIELDS_TAG);
        for (JsonValue field : fieldArray.iterator()) // iterator() returns a list of children
        {
            FieldConfig fieldConfig = new FieldConfig();

            fieldConfig.mapId = field.getInt(FIELD_TAG_MAPID);
            fieldConfig.density = field.getFloat(FIELD_TAG_DENSITY);
            fieldConfig.color = parseColor(field.getString(FIELD_TAG_COLOR));
            fieldConfig.hardness = field.getFloat(FIELD_TAG_HARDNESS);
            fieldConfig.isFluid = field.getBoolean(FIELD_TAG_ISFLUID);
            fieldConfig.name = field.getString(FIELD_TAG_NAME);

            fieldConfigHashMap.put(fieldConfig.mapId, fieldConfig);
        }

        return fieldConfigHashMap;
    }

    public static Level loadLevel(String folderName, HashMap<Integer, FieldConfig> fieldConfigHashMap) {
        Level level = new Level();
        String mapConfigName = folderName + "/config.json";
        String mapFileName = folderName + "/Map.png";
        Pixmap mapPixmap = new Pixmap(Gdx.files.internal(mapFileName));
        Gdx.app.debug("MyTag", "Loading map config...");
        level.mapConfig = loadMapConfigFile(mapConfigName);
        Gdx.app.debug("MyTag", "Loading map image...");
        level.gameGrid = new GameGrid(mapPixmap, fieldConfigHashMap);

        return level;
    }

    public static Color parseColor(String hex) {
        String s1 = hex.substring(0, 2);
        int v1 = Integer.parseInt(s1, 16);
        float f1 = (float) v1 / 255f;
        String s2 = hex.substring(2, 4);
        int v2 = Integer.parseInt(s2, 16);
        float f2 = (float) v2 / 255f;
        String s3 = hex.substring(4, 6);
        int v3 = Integer.parseInt(s3, 16);
        float f3 = (float) v3 / 255f;
        return new Color(f1, f2, f3, 1);
    }

}
