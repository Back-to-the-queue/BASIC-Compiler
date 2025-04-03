package BASIC;

import java.util.Optional;

public class LabeledStatementNode extends StatementNode{
    private String label;

    private Optional<StatementNode> statement;

    public LabeledStatementNode(){}
    public LabeledStatementNode(String label, Optional<StatementNode> state){
        this.label = label;
        this.statement = state;
    }

    public String getLabel() {return label;}
    public Optional<StatementNode> getStatement() {return statement;}


    @Override
    public String toString() {
        return label + ": " + statement;
    }
}

