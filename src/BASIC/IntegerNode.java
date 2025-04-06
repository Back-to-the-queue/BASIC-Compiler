package BASIC;

/**
 * {@code Node} that holds an Integer
 */
public class IntegerNode extends Node {
    private final int number;//initializing the field variable

    /**
     * Constructor to assign field number value
     * @param number value to be assigned
     */
    public IntegerNode(int number) {
        this.number = number;
    }

    /**
     * Accessor for the Integer
     * @return the integer value of the number
     */
    public int getNumber()
    {
        return number;
    }

    /**
     * Returns the value of the number
     * @return the value of number in a String form
     */
    @Override
    public String toString() {return "IntegerNode[" + String.valueOf(number) + "]";}

}