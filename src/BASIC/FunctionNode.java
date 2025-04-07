package BASIC;

import java.util.ArrayList;
import java.util.List;

public class FunctionNode extends StatementNode{

    private final String name;
    private final List<Node> params;

    public String getName() {return name;}

    public List<Node> getParams() {return params;}
    public FunctionNode(String functionName, ArrayList<Node> params) {
        this.name = functionName;
        this.params = params;
    }

    @Override
    public String toString() {return "FunctionNode[" + name + "(" + params + ")]";}
}
