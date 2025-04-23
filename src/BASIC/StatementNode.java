package BASIC;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;


/**
 * A {@code StatementNode}
 * @author Nicolas Quesada (nquesada@albany.edu)
 */
public abstract class StatementNode extends Node{
    public void setNext(Optional<StatementNode> next) {this.next = next;}

    public Optional<StatementNode> getNext() {return next;}

    public Optional<StatementNode> next;

    public void buildList(StatementsNode statements) {
        List<Optional<StatementNode>> children = statements.getStatements();
        for (int i = 0; i < children.size() - 1; i++) {
            children.get(i).get().setNext(children.get(i+1));
        }
    }
    /**
     * toString method for StatementNode
     * @return String holding the Node type and associated members
     */
    @Override
    public abstract String toString();
}
