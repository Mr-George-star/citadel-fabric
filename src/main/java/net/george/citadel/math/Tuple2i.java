package net.george.citadel.math;

import java.io.Serial;

@SuppressWarnings("unused")
public abstract class Tuple2i implements java.io.Serializable, Cloneable {
    @Serial
    private static final long serialVersionUID = -3555701650170169638L;

    /**
     * The x coordinate.
     */
    public int x;
    /**
     * The y coordinate.
     */
    public int y;

    /**
     * Constructs and initializes a Tuple2i from the specified
     * x and y coordinates.
     * @param x the x coordinate
     * @param y the y coordinate
     */
    public Tuple2i(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Constructs and initializes a Tuple2i from the array of length 2.
     * @param array the array of length 2 containing x and y in order.
     */
    public Tuple2i(int[] array) {
        this.x = array[0];
        this.y = array[1];
    }

    /**
     * Constructs and initializes a Tuple2i from the specified Tuple2i.
     * @param other the Tuple2i containing the initialization x and y
     * data.
     */
    public Tuple2i(Tuple2i other) {
        this.x = other.x;
        this.y = other.y;
    }

    /**
     * Constructs and initializes a Tuple2i to (0,0).
     */
    public Tuple2i() {
        this.x = 0;
        this.y = 0;
    }

    /**
     * Sets the value of this tuple to the specified x and y
     * coordinates.
     * @param x the x coordinate
     * @param y the y coordinate
     */
    public final void set(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Sets the value of this tuple to the specified coordinates in the
     * array of length 2.
     * @param array the array of length 2 containing x and y in order.
     */
    public final void set(int[] array) {
        this.x = array[0];
        this.y = array[1];
    }

    /**
     * Sets the value of this tuple to the value of tuple.
     * @param tuple the tuple to be copied
     */
    public final void set(Tuple2i tuple) {
        this.x = tuple.x;
        this.y = tuple.y;
    }

    /**
     * Copies the values of this tuple into the array.
     * @param array is the array
     */
    public final void get(int[] array) {
        array[0] = this.x;
        array[1] = this.y;
    }

    /**
     * Copies the values of this tuple into the tuple target.
     * @param target is the target tuple
     */
    public final void get(Tuple2i target) {
        target.x = this.x;
        target.y = this.y;
    }

    /**
     * Sets the value of this tuple to the sum of tuples first and second.
     * @param first  the first tuple
     * @param second the second tuple
     */
    public final void add(Tuple2i first, Tuple2i second) {
        this.x = first.x + second.x;
        this.y = first.y + second.y;
    }

    /**
     * Sets the value of this tuple to the sum of itself and other.
     * @param other the other tuple
     */
    public final void add(Tuple2i other) {
        this.x += other.x;
        this.y += other.y;
    }

    /**
     * Sets the value of this tuple to the difference
     * of tuples first and second (this = first - second).
     * @param first  the first tuple
     * @param second the second tuple
     */
    public final void sub(Tuple2i first, Tuple2i second) {
        this.x = first.x - second.x;
        this.y = first.y - second.y;
    }

    /**
     * Sets the value of this tuple to the difference
     * of itself and other (this = this - other).
     * @param other the other tuple
     */
    public final void sub(Tuple2i other) {
        this.x -= other.x;
        this.y -= other.y;
    }

    /**
     * Sets the value of this tuple to the negation of tuple source.
     * @param source the source tuple
     */
    public final void negate(Tuple2i source) {
        this.x = -source.x;
        this.y = -source.y;
    }

    /**
     * Negates the value of this tuple in place.
     */
    public final void negate() {
        this.x = -this.x;
        this.y = -this.y;
    }

    /**
     * Sets the value of this tuple to the scalar multiplication
     * of a tuple source.
     * @param scalar the scalar value
     * @param source the source tuple
     */
    public final void scale(int scalar, Tuple2i source) {
        this.x = scalar * source.x;
        this.y = scalar * source.y;
    }

    /**
     * Sets the value of this tuple to the scalar multiplication
     * of the scale factor with this.
     * @param scalar the scalar value
     */
    public final void scale(int scalar) {
        this.x *= scalar;
        this.y *= scalar;
    }

    /**
     * Sets the value of this tuple to the scalar multiplication
     * of tuple first plus tuple second (this = scalar*first + second).
     * @param scalar the scalar value
     * @param first the tuple to be multiple
     * @param second the tuple to be added
     */
    public final void scaleAdd(int scalar, Tuple2i first, Tuple2i second) {
        this.x = scalar * first.x + second.x;
        this.y = scalar * first.y + second.y;
    }

    /**
     * Sets the value of this tuple to the scalar multiplication
     * of itself and then adds tuple other (this = scalar*this + other).
     * @param scalar the scalar value
     * @param other the tuple to be added
     */
    public final void scaleAdd(int scalar, Tuple2i other) {
        this.x = scalar * this.x + other.x;
        this.y = scalar * this.y + other.y;
    }

    /**
     * Returns a string that contains the values of this Tuple2i.
     * The form is (x,y).
     * @return the String representation
     */
    @Override
    public String toString() {
        return "(" + this.x + ", " + this.y + ")";
    }

    /**
     * Returns true if the Object obj is of type Tuple2i and all the
     * data members of obj are equal to the corresponding data members in
     * this Tuple2i.
     * @param obj  the object with which the comparison is made
     */
    @Override
    public boolean equals(Object obj) {
        try {
            Tuple2i other = (Tuple2i) obj;
            return(this.x == other.x && this.y == other.y);
        } catch (NullPointerException | ClassCastException exception) {
            return false;
        }
    }

    /**
     * Returns a hash code value based on the data values in this
     * object.  Two different Tuple2i objects with identical data values
     * (i.e., Tuple2i. Equals returns true) will return the same hash
     * code value.  Two objects with different data members may return the
     * same hash value, although this is not likely.
     * @return the integer hash code value
     */
    @Override
    public int hashCode() {
        long bits = 1L;
        bits = 31L * bits + (long)x;
        bits = 31L * bits + (long)y;
        return (int) (bits ^ (bits >> 32));
    }

    /**
     *  Clamps the tuple parameter to the range [low, high] and
     *  places the values into this tuple.
     *  @param min   the lowest value in the tuple after clamping
     *  @param max   the highest value in the tuple after clamping
     *  @param source the source tuple, which will not be modified
     */
    public final void clamp(int min, int max, Tuple2i source) {
        if (source.x > max) {
            this.x = max;
        } else {
            this.x = Math.max(source.x, min);
        }

        if (source.y > max) {
            this.y = max;
        } else {
            this.y = Math.max(source.y, min);
        }
    }

    /**
     *  Clamps the minimum value of the tuple parameter to the min
     *  parameter and places the values into this tuple.
     *  @param min    the lowest value in the tuple after clamping
     *  @param source the source tuple, which will not be modified
     */
    public final void clampMin(int min, Tuple2i source) {
        this.x = Math.max(source.x, min);
        this.y = Math.max(source.y, min);
    }

    /**
     *  Clamps the maximum value of the tuple parameter to the max
     *  parameter and places the values into this tuple.
     *  @param max    the highest value in the tuple after clamping
     *  @param source the source tuple, which will not be modified
     */
    public final void clampMax(int max, Tuple2i source) {
        this.x = Math.min(source.x, max);
        this.y = Math.min(source.y, max);
    }

    /**
     *  Sets each component of the tuple parameter to its absolute
     *  value and places the modified values into this tuple.
     *  @param source the source tuple, which will not be modified
     */
    public final void absolute(Tuple2i source) {
        this.x = Math.abs(source.x);
        this.y = Math.abs(source.y);
    }

    /**
     *  Clamps this tuple to the range [low, high].
     *  @param min  the lowest value in this tuple after clamping
     *  @param max  the highest value in this tuple after clamping
     */
    public final void clamp(int min, int max) {
        if (this.x > max) {
            this.x = max;
        } else {
            if (this.x < min) {
                this.x = min;
            }
        }

        if (this.y > max) {
            this.y = max;
        } else {
            if (this.y < min) {
                this.y = min;
            }
        }
    }

    /**
     *  Clamps the minimum value of this tuple to the min parameter.
     *  @param min   the lowest value in this tuple after clamping
     */
    public final void clampMin(int min) {
        if (this.x < min) {
            this.x = min;
        }

        if (this.y < min) {
            this.y = min;
        }
    }

    /**
     *  Clamps the maximum value of this tuple to the max parameter.
     *  @param max   the highest value in the tuple after clamping
     */
    public final void clampMax(int max) {
        if (this.x > max) {
            this.x = max;
        }

        if (this.y > max) {
            this.y = max;
        }
    }

    /**
     *  Sets each component of this tuple to its absolute value.
     */
    public final void absolute() {
        this.x = Math.abs(this.x);
        this.y = Math.abs(this.y);
    }

    /**
     * Creates a new object of the same class as this object.
     *
     * @return a clone of this instance.
     * @exception OutOfMemoryError if there is not enough memory.
     * @see Cloneable
     */
    public Object clone() {
        // Since there are no arrays, we can just use Object.clone()
        try {
            return super.clone();
        } catch (CloneNotSupportedException exception) {
            // this shouldn't happen, since we are Cloneable
            throw new InternalError();
        }
    }

    /**
     * Get the <i>x</i> coordinate.
     *
     * @return the x coordinate.
     *
     * @since vecmath 1.5
     */
    public final int getX() {
        return this.x;
    }

    /**
     * Set the <i>x</i> coordinate.
     *
     * @param x  value to <i>x</i> coordinate.
     *
     * @since vecmath 1.5
     */
    public final void setX(int x) {
        this.x = x;
    }

    /**
     * Get the <i>y</i> coordinate.
     *
     * @return  the <i>y</i> coordinate.
     *
     * @since vecmath 1.5
     */
    public final int getY() {
        return this.y;
    }

    /**
     * Set the <i>y</i> coordinate.
     *
     * @param y value to <i>y</i> coordinate.
     *
     * @since vecmath 1.5
     */
    public final void setY(int y) {
        this.y = y;
    }
}
