package BASIC;

/**
 * A {@code VariableReferenceNode}
 *
 * @author Nicolas Quesada (nquesada@albany.edu)
 *
 */
public class VariableNode extends Node {

    private String name; // Name of the variable or field.

    /**
     * Creates a new VariableNode for a simple variable or field reference.
     * @param name The name of the variable or field.
     */
    public VariableNode(String name) {
        this.name = name;
    }

    /**
     * Accessor for name
     * @return the name value
     */
    public String getName() {
        return name;
    }

    /**
     * toString method for the {@code VariableNode}
     * @returnn the string value of the node
     */
    @Override
    public String toString() {return "(" + name + ")" ;}
}