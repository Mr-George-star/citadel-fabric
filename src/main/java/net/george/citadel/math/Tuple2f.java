package net.george.citadel.math;

import java.io.Serial;

/**
 * A generic 2-element tuple that is represented by single-precision
 * floating point x,y coordinates.
 */
@SuppressWarnings("unused")
public abstract class Tuple2f implements java.io.Serializable, Cloneable {
    @Serial
    private static final long serialVersionUID = 9011180388985266884L;

    /**
     * The x coordinate.
     */
    public float x;

    /**
     * The y coordinate.
     */
    public float y;

    /**
     * Constructs and initializes a Tuple2f from the specified xy coordinates.
     * @param x the x coordinate
     * @param y the y coordinate
     */
    public Tuple2f(float x, float y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Constructs and initializes a Tuple2f from the specified array.
     * @param array the array of length 2 containing xy in order
     */
    public Tuple2f(float[] array) {
        this.x = array[0];
        this.y = array[1];
    }

    /**
     * Constructs and initializes a Tuple2f from the specified Tuple2f.
     * @param other the Tuple2f containing the initialization x y data
     */
    public Tuple2f(Tuple2f other) {
        this.x = other.x;
        this.y = other.y;
    }

    /**
     * Constructs and initializes a Tuple2f to (0,0).
     */
    public Tuple2f() {
        this.x = (float) 0.0;
        this.y = (float) 0.0;
    }

    /**
     * Sets the value of this tuple to the specified xy coordinates.
     * @param x the x coordinate
     * @param y the y coordinate
     */
    public final void set(float x, float y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Sets the value of this tuple from the two values specified in
     * the array.
     * @param array the array of length 2 containing xy in order
     */
    public final void set(float[] array) {
        this.x = array[0];
        this.y = array[1];
    }

    /**
     * Sets the value of this tuple to the value of the Tuple2f argument.
     * @param tuple the tuple to be copied
     */
    public final void set(Tuple2f tuple) {
        this.x = tuple.x;
        this.y = tuple.y;
    }

    /**
     *  Copies the value of the elements of this tuple into the array.
     *  @param array the array that will contain the values of the vector
     */
    public final void get(float[] array) {
        array[0] = this.x;
        array[1] = this.y;
    }

    /**
     * Sets the value of this tuple to the vector sum of tuples first and second.
     * @param first  the first tuple
     * @param second the second tuple
     */
    public final void add(Tuple2f first, Tuple2f second) {
        this.x = first.x + second.x;
        this.y = first.y + second.y;
    }

    /**
     * Sets the value of this tuple to the vector sum of itself and tuple other.
     * @param other the other tuple
     */
    public final void add(Tuple2f other) {
        this.x += other.x;
        this.y += other.y;
    }

    /**
     * Sets the value of this tuple to the vector difference of
     * tuple first and second (this = first - second).
     * @param first the first tuple
     * @param second the second tuple
     */
    public final void sub(Tuple2f first, Tuple2f second) {
        this.x = first.x - second.x;
        this.y = first.y - second.y;
    }

    /**
     * Sets the value of this tuple to the vector difference of
     * itself and tuple other (this = this - other).
     * @param other the other tuple
     */
    public final void sub(Tuple2f other) {
        this.x -= other.x;
        this.y -= other.y;
    }

    /**
     * Sets the value of this tuple to the negation of tuple source.
     * @param source the source tuple
     */
    public final void negate(Tuple2f source) {
        this.x = -source.x;
        this.y = -source.y;
    }

    /**
     * Negates the value of this vector in place.
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
    public final void scale(float scalar, Tuple2f source) {
        this.x = scalar * source.x;
        this.y = scalar * source.y;
    }

    /**
     * Sets the value of this tuple to the scalar multiplication
     * of itself.
     * @param scalar the scalar value
     */
    public final void scale(float scalar) {
        this.x *= scalar;
        this.y *= scalar;
    }

    /**
     * Sets the value of this tuple to the scalar multiplication
     * of tuple first and then adds tuple second (this = scalar*first + second).
     * @param scalar the scalar value
     * @param first the tuple to be multiple
     * @param second the tuple to be added
     */
    public final void scaleAdd(float scalar, Tuple2f first, Tuple2f second) {
        this.x = scalar * first.x + second.x;
        this.y = scalar * first.y + second.y;
    }

    /**
     * Sets the value of this tuple to the scalar multiplication
     * of itself and then adds tuple other (this = scalar*this + other).
     * @param scalar the scalar value
     * @param other  the tuple to be added
     */
    public final void scaleAdd(float scalar, Tuple2f other) {
        this.x = scalar * this.x + other.x;
        this.y = scalar * this.y + other.y;
    }

    /**
     * Returns true if all the data members of Tuple2f other are
     * equal to the corresponding data members in this Tuple2f.
     * @param other  the vector with which the comparison is made
     * @return  true or false
     */
    public boolean equals(Tuple2f other) {
        try {
            return (this.x == other.x && this.y == other.y);
        }
        catch (NullPointerException exception) {
            return false;
        }
    }

    /**
     * Returns true if the Object obj is of type Tuple2f and all the
     * data members of obj are equal to the corresponding data members in
     * this Tuple2f.
     * @param obj the object with which the comparison is made
     * @return  true or false
     */
    @Override
    public boolean equals(Object obj) {
        try {
            Tuple2f other = (Tuple2f) obj;
            return (this.x == other.x && this.y == other.y);
        }
        catch (NullPointerException | ClassCastException exception) {
            return false;
        }
    }

    /**
     * Returns true if the L-infinite distance between this tuple
     * and tuple other is less than or equal to the epsilon parameter,
     * otherwise return false.  The L-infinite
     * distance is equal to MAX[abs(x1-x2), abs(y1-y2)].
     * @param other   the tuple to be compared to this tuple
     * @param epsilon the threshold value
     * @return  true or false
     */
    public boolean epsilonEquals(Tuple2f other, float epsilon) {
        float diff;

        diff = x - other.x;
        if (Float.isNaN(diff)) {
            return false;
        }
        if ((diff < 0 ? -diff:diff) > epsilon) {
            return false;
        }

        diff = y - other.y;
        if (Float.isNaN(diff)) {
            return false;
        }
        return !((diff < 0 ? -diff : diff) > epsilon);
    }

    /**
     * Returns a string that contains the values of this Tuple2f.
     * The form is (x,y).
     * @return the String representation
     */
    @Override
    public String toString()
    {
        return("(" + this.x + ", " + this.y + ")");
    }

    /**
     *  Clamps the tuple parameter to the range [low, high] and
     *  places the values into this tuple.
     *  @param min   the lowest value in the tuple after clamping
     *  @param max   the highest value in the tuple after clamping
     *  @param other the source tuple, which will not be modified
     */
    public final void clamp(float min, float max, Tuple2f other) {
        if (other.x > max) {
            this.x = max;
        } else {
            this.x = Math.max(other.x, min);
        }

        if (other.y > max) {
            this.y = max;
        } else {
            this.y = Math.max(other.y, min);
        }
    }

    /**
     *  Clamps the minimum value of the tuple parameter to the min
     *  parameter and places the values into this tuple.
     *  @param min   the lowest value in the tuple after clamping
     *  @param other the source tuple, which will not be modified
     */
    public final void clampMin(float min, Tuple2f other) {
        this.x = Math.max(other.x, min);
        this.y = Math.max(other.y, min);
    }

    /**
     *  Clamps the maximum value of the tuple parameter to the max
     *  parameter and places the values into this tuple.
     *  @param max   the highest value in the tuple after clamping
     *  @param other the source tuple, which will not be modified
     */
    public final void clampMax(float max, Tuple2f other) {
        this.x = Math.min(other.x, max);
        this.y = Math.min(other.y, max);
    }

    /**
     *  Sets each component of the tuple parameter to its absolute
     *  value and places the modified values into this tuple.
     *  @param other   the source tuple, which will not be modified
     */
    public final void absolute(Tuple2f other) {
        this.x = Math.abs(other.x);
        this.y = Math.abs(other.y);
    }

    /**
     *  Clamps this tuple to the range [low, high].
     *  @param min  the lowest value in this tuple after clamping
     *  @param max  the highest value in this tuple after clamping
     */
    public final void clamp(float min, float max) {
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
    public final void clampMin(float min) {
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
    public final void clampMax(float max) {
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
     *  Linearly interpolates between tuples first and second and places the
     *  result into this tuple: this = (1-alpha)*first + alpha*second.
     *  @param first  the first tuple
     *  @param second the second tuple
     *  @param alpha  the alpha interpolation parameter
     */
    public final void interpolate(Tuple2f first, Tuple2f second, float alpha) {
        this.x = (1 - alpha) * first.x + alpha * second.x;
        this.y = (1 - alpha) * first.y + alpha * second.y;
    }

    /**
     *  Linearly interpolates between this tuple and tuple other and
     *  places the result into this tuple: this = (1-alpha)*this + alpha*other.
     *  @param other  the first tuple
     *  @param alpha  the alpha interpolation parameter
     */
    public final void interpolate(Tuple2f other, float alpha) {
        this.x = (1 - alpha) * this.x + alpha * other.x;
        this.y = (1 - alpha) * this.y + alpha * other.y;
    }

    /**
     * Creates a new object of the same class as this object.
     *
     * @return a clone of this instance.
     * @exception OutOfMemoryError if there is not enough memory.
     * @see Cloneable
     * @since vecmath 1.3
     */
    @Override
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
     * @return  the <i>x</i> coordinate.
     *
     * @since vecmath 1.5
     */
    public final float getX() {
        return this.x;
    }

    /**
     * Set the <i>x</i> coordinate.
     *
     * @param x  value to <i>x</i> coordinate.
     *
     * @since vecmath 1.5
     */
    public final void setX(float x) {
        this.x = x;
    }

    /**
     * Get the <i>y</i> coordinate.
     *
     * @return  the <i>y</i> coordinate.
     *
     * @since vecmath 1.5
     */
    public final float getY() {
        return this.y;
    }

    /**
     * Set the <i>y</i> coordinate.
     *
     * @param y value to <i>y</i> coordinate.
     *
     * @since vecmath 1.5
     */
    public final void setY(float y) {
        this.y = y;
    }
}
