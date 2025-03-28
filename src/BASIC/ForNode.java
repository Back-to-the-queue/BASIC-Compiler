package BASIC;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ForNode extends StatementNode{
    public VariableNode getVariable() {return variable;}

    private VariableNode variable;

    public Node getStart() {return start;}

    private Node start;

    public Node getEnd() {return end;}

    private Node end;

    public Optional<Node> getIncrement() {return increment;}

    private Optional<Node> increment;

    public List<StatementNode> getStatements() {return statements;}

    private List<StatementNode> statements = new ArrayList<StatementNode>();

    /**
     * This constructor holds a for loop
     * @param reference the counter variable
     * @param start the starting integer value
     * @param end the ending integer value
     * @param statement the statement within the for loop
     */
    public ForNode(VariableNode reference, Node start, Node end, Optional<StatementNode> statement) {
        this.variable = reference;
        this.start = start;
        this.end = end;
    }
    public ForNode(VariableNode reference, Node start, Node end, Optional<Node> increment, Optional<StatementNode> statement) {
        this.variable = reference;
        this.start = start;
        this.end = end;
    }

    @Override
    public String toString() {
        return null;
    }
}
