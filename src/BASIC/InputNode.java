package BASIC;

import java.util.List;

public class InputNode extends StatementNode{
    public List<Node> getInputList() {return inputList;}
    public Node getCurrentInput(){return inputList.get(0);}
    List<Node> inputList;
    public InputNode(List<Node> statement) {
        this.inputList = statement;
    }

    @Override
    public String toString(){return "InputNode[" + inputList + "]";}
}
