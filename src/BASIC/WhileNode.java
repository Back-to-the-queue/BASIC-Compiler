package BASIC;

import java.util.Optional;

public class WhileNode extends StatementNode{
    public Optional<StatementNode> getCondition() {return condition;}

    private Optional<StatementNode> condition;

    public Optional<StatementNode> getEndLabel() {return endLabel;}

    private Optional<StatementNode> endLabel;

    public Optional<StatementNode> getLoopState() {return loopState;}

    private Optional<StatementNode> loopState;
    public WhileNode(Optional<StatementNode> cond, Optional<StatementNode> loop,Optional<StatementNode> label) {
        this.condition = cond;
        this.endLabel = label;
        this.loopState = loop;
    }

    @Override
    public String toString() {
        return null;
    }
}
