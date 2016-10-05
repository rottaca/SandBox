package com.rottaca.sandbox.data;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.JsonValue;
import com.rottaca.sandbox.ctrl.ConfigLoader;

public class FieldConfig {
    public String name;
    public float hardness;
    public float density;
    public boolean isFluid;
    public int mapId;
    public Color color;


    public FieldConfig(JsonValue json) {
        mapId = json.getInt(ConfigLoader.FIELD_TAG_MAPID);
        density = json.getFloat(ConfigLoader.FIELD_TAG_DENSITY);
        color = parseColor(json.getString(ConfigLoader.FIELD_TAG_COLOR));
        hardness = json.getFloat(ConfigLoader.FIELD_TAG_HARDNESS);
        isFluid = json.getBoolean(ConfigLoader.FIELD_TAG_ISFLUID);
        name = json.getString(ConfigLoader.FIELD_TAG_NAME);
    }

    @Override
    public String toString() {
        return "Name: " + name + "\nMapId: " + mapId +
                "\nColor: " + color + "\nHardness: " + hardness +
                "\nDensity: " + density + "\nIsFluid: " + isFluid;
    }

    private static Color parseColor(String hex) {
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
