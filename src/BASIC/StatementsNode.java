package BASIC;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StatementsNode extends StatementNode{
    private List<Optional<StatementNode>> statements = new ArrayList<>(); // List of Statements
    public StatementsNode(List<Optional<StatementNode>> statement){this.statements = statement;}
    public StatementsNode(){}

    /**
     * Accessor method for the {@code StatementsNode}
     * @return the list of statement nodes
     */
    public List<Optional<StatementNode>> getStatements() {return statements;}

    /**
     * Accessor for the current {@code StatementNode}
     * @return the current {@code StatementNode}
     */
    public Optional<StatementNode> getCurrentStatement() {return statements.get(0);}

    /**
     * toString method for the {@code StatementsNode}
     * @return the string value of the node
     */
    @Override
    public String toString() {return "StatementsNode(" + statements + ")" ;}
}
