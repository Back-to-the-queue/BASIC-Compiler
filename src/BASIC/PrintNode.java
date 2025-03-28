package BASIC;

import java.util.ArrayList;
import java.util.List;

public class PrintNode extends StatementNode{
    private List<Node> printList = new ArrayList<>(); //Holds a list of all that needs to be printed
    public PrintNode(Node s){
        printList.add(s);
    }

    /**
     * Accessor for the entire list
     * @return the whole list of print statements
     */
    public List<Node> getPrintList() {return printList;}

    /**
     * Accessor for the current print statement
     * @return the current print statement
     */
    public Node getCurrentPrint(){return printList.get(0);}

    /**
     * toString method for the {@code PrintNode}
     * @return the string value of the node
     */
    @Override
    public String toString() {return "PRINT" + printList;}
}
