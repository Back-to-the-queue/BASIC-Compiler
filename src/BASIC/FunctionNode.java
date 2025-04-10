package BASIC;

import java.util.ArrayList;
import java.util.List;

public class FunctionNode extends StatementNode{

    private String name;
    private List<Node> params;
    private char type;

    public char getType() {return type;}

    public String getName() {return name;}

    public List<Node> getParams() {return params;}
    public FunctionNode(String functionName, ArrayList<Node> params) {
        this.name = functionName;
        this.params = params;
    }
    public FunctionNode(String functionName, char type, ArrayList<Node> params) {
        this.name = functionName;
        this.params = params;
        this.type = type;
    }
    public FunctionNode(String functionName) {
        this.name = functionName;
    }

    @Override
    public String toString() {return "FunctionNode[" + name + "(" + params + ")]";}
}
