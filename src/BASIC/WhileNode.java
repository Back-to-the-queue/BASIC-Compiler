package BASIC;
import java.util.List;
import java.util.Optional;

public class WhileNode extends StatementNode{
    public Optional<StatementNode> getCondition() {return condition;}

    private Optional<StatementNode> condition;

    public Optional<StatementNode> getEndLabel() {return endLabel;}

    private Optional<StatementNode> endLabel;

    public List<Optional<StatementNode>> getLoopState() {return loopState;}

    private List<Optional<StatementNode>> loopState;

    public WhileNode(Optional<StatementNode> cond, List<Optional<StatementNode>> loop,Optional<StatementNode> label) {
        this.condition = cond;
        this.endLabel = label;
        this.loopState = loop;
    }

    @Override
    public String toString() {
        return "WhileNode[condition=" + condition + ", endLabel=" + endLabel + ", loopState=" + loopState + "]";
    }
}
