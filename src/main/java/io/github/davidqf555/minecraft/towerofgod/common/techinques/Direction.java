package io.github.davidqf555.minecraft.towerofgod.common.techinques;

public enum Direction {
    LEFT(-1, 0, 0),
    RIGHT(1, 0, 180),
    UP(0, -1, 90),
    DOWN(0, 1, -90);

    private final double dX;
    private final double dY;
    private final float angle;

    Direction(double dX, double dY, float angle) {
        this.dX = dX;
        this.dY = dY;
        this.angle = angle;
    }

    public double getX() {
        return dX;
    }

    public double getY() {
        return dY;
    }

    public float getAngle() {
        return angle;
    }
}
