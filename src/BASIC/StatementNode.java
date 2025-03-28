package BASIC;

import java.util.List;

/**
 * A {@code StatementNode}
 * @author Nicolas Quesada (nquesada@albany.edu)
 */
public abstract class StatementNode extends Node{
    public void setNext(StatementNode next) {
        this.next = next;
    }

    protected StatementNode next;

    /**
     * toString method for StatementNode
     * @return String holding the Node type and associated members
     */
    @Override
    public abstract String toString();
}
