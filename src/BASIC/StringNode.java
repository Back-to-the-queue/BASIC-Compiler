package BASIC;

/**
 * {@code Node} that holds a String
 * Author Nicolas Quesada (nquesada@albany.edu)
 */
public class StringNode extends Node {
    private String member;//initializing the field variable

    /**
     * Constructor to assign field number value
     * @param str value to be assigned
     */
    public StringNode(String str) {
        this.member = str;
    }

    /**
     * Accessor for the Integer
     *
     * @return the integer value of the number
     */
    public String getMember()
    {
        return member;
    }

    /**
     *
     * @return
     */
    @Override
    public String toString() {return "StringNode[" + member + "]";}
}