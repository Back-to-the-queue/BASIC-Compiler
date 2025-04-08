package BASIC;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ForNode extends StatementNode{
    public Optional<StatementNode> getVariable() {return variable;}

    private final Optional<StatementNode> variable;

    public Node getEnd() {return end;}

    private final Node end;

    public Node getIncrement() {return increment;}

    private Node increment;

    public List<Optional<StatementNode>> getStatements() {return statements;}

    private List<Optional<StatementNode>> statements = new ArrayList<Optional<StatementNode>>();


    /**
     * This constructor holds a for loop
     * @param reference the counter variable
     * @param start the starting integer value
     * @param end the ending integer value
     * @param statement the statement within the for loop
     */
    public ForNode(Optional<StatementNode> reference, Node end, Node increment, List<Optional<StatementNode>> statement) {
        this.variable = reference;
        this.end = end;
        this.increment = increment;
        this.statements = statement;
    }

    @Override
    public String toString() {
        return "ForNode[" + variable.orElse(null) + ", " + end + ", " + increment + ", " + statements + "]";
    }
}
