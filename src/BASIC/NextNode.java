package BASIC;

public class NextNode extends StatementNode{

    public String getNext() {return next;}
    private String next;

    /**
     * Constructor for the {@code NextNode}
     * @param next the var that follows the next statement
     */
    public NextNode(String next){ this.next = next;}

    /**
     * toString method for the {@code NextNode}
     * @return the string value of the node
     */
    @Override
    public String toString(){return "NextNode[" + next + "]";}
}
