package com.rottaca.sandbox.data;

public class FieldInfo {

    private int fieldID = 0;

    public FieldInfo() {

    }

    public FieldInfo(int fieldId) {
        this.fieldID = fieldId;
    }

    public int getID() {
        return fieldID;
    }

    public void setID(int id) {
        this.fieldID = id;
    }
}
