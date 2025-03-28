package BASIC;

/**
 *
 */
public class FloatNode extends Node {
    private float number;

    /**
     * Floating number constructor to assign value
     * @param number the float value of the number
     */
    public FloatNode(float number) {
        this.number = number;
    }

    /**
     * Accessor for float value
     * @return the float value of the number
     */
    public float getNumber()
    {
        return number;
    }

    /**
     * ToString method for the @{code FloatNode}
     * @return
     */
    @Override
    public String toString() {
        return String.valueOf(number);
    }

}