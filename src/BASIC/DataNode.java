package BASIC;

import java.util.ArrayList;
import java.util.List;

public class DataNode extends StatementNode{
    private List<Node> data = new ArrayList<>(); //List that holds the data
    /**
     *
     * @param statement
     */
    public DataNode(List<Node> statement) {
        this.data = statement;
    }

    /**
     * Accessor for the data statement
     * @return the entire list
     */
    public List<Node> getData() {return data;}

    /**
     * Accessor for the current data {@code Node}
     * @return the current {@code Node}
     */
    public Node getCurrentData(){return data.get(0);}

    /**
     * toString method for the {@code DataNode}
     * @return the string value of the node
     */
    @Override
    public String toString(){return "DataNode" + data + "]";}
}
