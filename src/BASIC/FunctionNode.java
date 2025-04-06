package BASIC;

import java.util.List;

public class FunctionNode extends StatementNode{

    private final String name;
    private final List<VariableNode> params;

    public String getName() {return name;}

    public List<VariableNode> getParams() {return params;}
    public FunctionNode(String functionName, List<VariableNode> params) {
        this.name = functionName;
        this.params = params;
    }

    @Override
    public String toString() {return "FunctionNode[" + name + "(" + params + ")]";}
}
