package BASIC;

import java.util.Optional;

public class IfNode extends StatementNode{
    private Optional<StatementNode> bool;
    private final String endLabel;
    public IfNode(Optional<StatementNode> condition, String label) {
        this.bool = condition;
        this.endLabel = label;
    }
    public Optional<StatementNode> getBool() {return bool;}
    public String getEndLabel() {return endLabel;}

    @Override
    public String toString() {
        return null;
    }
}
