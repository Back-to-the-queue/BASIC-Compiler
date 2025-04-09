package BASIC;

import java.util.Optional;

/**
 * A {@code StatementNode}
 * @author Nicolas Quesada (nquesada@albany.edu)
 */
public abstract class StatementNode extends Node{
    public void setNext(Optional<StatementNode> next) {
        this.next = next;
    }

    protected Optional<StatementNode> next;

    /**
     * toString method for StatementNode
     * @return String holding the Node type and associated members
     */
    @Override
    public abstract String toString();
}
