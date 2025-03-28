package BASIC;

public class GoSubNode extends StatementNode{
    private final String str;
    public GoSubNode(String statement) {
        this.str = String.valueOf(statement);
    }

    @Override
    public String toString() {return "GOSUB(" + str + ")";}
}
