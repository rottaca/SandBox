package com.rottaca.sandbox.data;


import com.badlogic.gdx.graphics.Color;

public class FieldConfig {
    public String name;
    public float hardness;
    public float density;
    public boolean isFluid;
    public int mapId;
    public Color color;

    @Override
    public String toString() {
        return "Name: " + name + "\nMapId: " + mapId +
                "\nColor: " + color + "\nHardness: " + hardness +
                "\nDensity: " + density + "\nIsFluid: " + isFluid;
    }
}
