package net.george.citadel.math;

import java.io.Serial;

@SuppressWarnings("unused")
public class Point2f extends Tuple2f implements java.io.Serializable {
    // Compatible with 1.1
    @Serial
    private static final long serialVersionUID = -4801347926528714435L;

    /**
     * Constructs and initializes a Point2f from the specified xy coordinates.
     * @param x the x coordinate
     * @param y the y coordinate
     */
    public Point2f(float x, float y)
    {
        super(x,y);
    }

    /**
     * Constructs and initializes a Point2f from the specified array.
     * @param array the array of length 2 containing xy in order
     */
    public Point2f(float[] array)
    {
        super(array);
    }

    /**
     * Constructs and initializes a Point2f from the specified Point2f.
     * @param point the Point2f containing the initialization x y data
     */
    public Point2f(Point2f point) {
        super(point);
    }

    /**
     * Constructs and initializes a Point2f from the specified Tuple2f.
     * @param tuple the Tuple2f containing the initialization x y data
     */
    public Point2f(Tuple2f tuple) {
        super(tuple);
    }

    /**
     * Constructs and initializes a Point2f to (0,0).
     */
    public Point2f() {
        super();
    }

    /**
     * Computes the square of the distance between this point and point other.
     * @param other the other point
     */
    public final float distanceSquared(Point2f other) {
        float dx, dy;
        dx = this.x - other.x;
        dy = this.y - other.y;
        return dx * dx + dy * dy;
    }

    /**
     * Computes the distance between this point and point other.
     * @param other the other point
     */
    public final float distance(Point2f other) {
        float  dx, dy;
        dx = this.x - other.x;
        dy = this.y - other.y;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * Computes the L-1 (Manhattan) distance between this point and
     * point other.  The L-1 distance is equal to abs(x1-x2) + abs(y1-y2).
     * @param other the other point
     */
    public final float distanceL1(Point2f other) {
        return (Math.abs(this.x - other.x) + Math.abs(this.y - other.y));
    }

    /**
     * Computes the L-infinite distance between this point and
     * point other.  The L-infinite distance is equal to
     * MAX[abs(x1-x2), abs(y1-y2)].
     * @param other the other point
     */
    public final float distanceLinf(Point2f other) {
        return (Math.max( Math.abs(this.x - other.x), Math.abs(this.y - other.y)));
    }
}
