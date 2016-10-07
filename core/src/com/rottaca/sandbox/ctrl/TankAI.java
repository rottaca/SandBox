package com.rottaca.sandbox.ctrl;

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
    private float errorMargin = 100f;

    public TankAI() {
        V0 = new Vector2();
        X0 = new Vector2();
        X1 = new Vector2();
        A = new Vector2();

    }

    public void prepareTank(GameGrid grid, ArrayList<Tank> tanks, Tank aiTank, Tank targetTank) {
        // Time the bullet should fly in seconds
        float flightTime = 5;

        X1.set(targetTank.getX(), targetTank.getY());
        X0.set(aiTank.getX(), aiTank.getY());
        A.set(0, GameController.GRAVITATION);


        // To avoid a perfect AI, add an error to the target X position
        X1.add(-0.5f * errorMargin + errorMargin * (float) Math.random(), -0.5f * errorMargin + errorMargin * (float) Math.random());

        // Compute shoot angle and power according to given data
        V0.x = (X1.x - X0.x - A.x * flightTime * flightTime / 2) / flightTime;
        V0.y = (X1.y - X0.y - A.y * flightTime * flightTime / 2) / flightTime;

        aiTank.power = V0.len();
        aiTank.gunAngle = (float) Math.toDegrees(Math.atan2(V0.y, V0.x));
    }
}
