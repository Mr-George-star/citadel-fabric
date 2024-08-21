package net.george.citadel.math;

import java.io.Serial;

@SuppressWarnings("unused")
public class Point2i extends Tuple2i implements java.io.Serializable {
    @Serial
    private static final long serialVersionUID = 9208072376494084954L;

    /**
     * Constructs and initializes a Point2i from the specified
     * x and y coordinates.
     * @param x the x coordinate
     * @param y the y coordinate
     */
    public Point2i(int x, int y) {
        super(x, y);
    }

    /**
     * Constructs and initializes a Point2i from the array of length 2.
     * @param array the array of length 2 containing x and y in order.
     */
    public Point2i(int[] array) {
        super(array);
    }

    /**
     * Constructs and initializes a Point2i from the specified Tuple2i.
     * @param t1 the Tuple2i containing the initialization x and y
     * data.
     */
    public Point2i(Tuple2i t1) {
        super(t1);
    }

    /**
     * Constructs and initializes a Point2i to (0,0).
     */
    public Point2i() {
        super();
    }
}
