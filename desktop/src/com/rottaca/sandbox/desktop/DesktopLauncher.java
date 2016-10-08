package com.rottaca.sandbox.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.rottaca.sandbox.ctrl.SandBox;

public class DesktopLauncher {
    public static void main(String[] arg) {

        TexturePacker.process("textures/main", "textures/main", "pack");
        TexturePacker.process("textures/bulletExplosion", "textures/bulletExplosion", "pack");

        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        new LwjglApplication(new SandBox(), config);
    }
}
