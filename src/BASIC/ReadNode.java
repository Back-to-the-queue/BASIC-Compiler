package BASIC;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class ReadNode extends StatementNode{
    private List<VariableNode> vars = new ArrayList<>();

    /**
     * Constructor for the {@code ReadNode}
     * @param var takes in a variable
     */
    public ReadNode(List<VariableNode> var){
        this.vars = var;
    }

    /**
     * Accessor method for the {@code ReadNode}
     * @return the list of variables
     */
    public List<VariableNode> getVars() {
        return vars;
    }


    /**
     * toString method for the {@code ReadNode}
     * @return the string value of the node
     */
    @Override
    public String toString() {return "ReadNode" + vars;}
}
