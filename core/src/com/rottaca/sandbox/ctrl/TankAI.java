package com.rottaca.sandbox.ctrl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.rottaca.sandbox.data.GameGrid;
import com.rottaca.sandbox.data.Tank;

import java.util.ArrayList;

/**
 * Created by Andreas on 07.10.2016.
 */

public class TankAI {
    private Vector2 V0, X0, X1, A;

    // Higher values cause more error when calculating the optimal gun angle
    // Range for modifing target position
    private float errorMargin = 250f;

    public TankAI() {
        V0 = new Vector2();
        X0 = new Vector2();
        X1 = new Vector2();
        A = new Vector2();
    }

    public void prepareTank(GameGrid grid, ArrayList<Tank> tanks, Tank aiTank, Tank targetTank, float gravitation, float wind) {
        // Time the bullet should fly in seconds
        float flightTime = 5;

        X1.set(targetTank.getCenterPos());
        X0.set(aiTank.getCenterPos());
        A.set(wind, gravitation);

        // To avoid a perfect AI, add an error to the target X position
        X1.add(-0.5f * errorMargin + errorMargin * (float) Math.random(), 0);

        // Compute shoot angle and power according to given data
        V0.x = (X1.x - X0.x - A.x * flightTime * flightTime / 2) / flightTime;
        V0.y = (X1.y - X0.y - A.y * flightTime * flightTime / 2) / flightTime;

        aiTank.setPower(V0.x, V0.y);

        Gdx.app.log("MyTag", "Power: " + aiTank.getPower());
    }
}
