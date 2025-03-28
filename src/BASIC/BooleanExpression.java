package BASIC;

public class BooleanExpression extends StatementNode{
    public Node getLeftExpression() {return leftExpression;}

    private Node leftExpression;

    public Node getRightExpression() {return rightExpression;}

    private Node rightExpression;

    public Token.TokenType getCondition() {return condition;}

    private Token.TokenType condition;

    /**
     * This constructor holds boolean expressions
     * @param leftExp the left hand side of the expression
     * @param rightExp the right hand side of the expression
     * @param cond the condition to compare the left to the right hand expression
     */
    public BooleanExpression(Node leftExp, Node rightExp,Token.TokenType cond) {
        this.leftExpression = leftExp;
        this.rightExpression = rightExp;
        this.condition = cond;
    }

    @Override
    public String toString()
    {
        return (this.leftExpression.toString() + " " + condition + " " + rightExpression.toString());
    }

}