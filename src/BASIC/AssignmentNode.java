package BASIC;
/**
 * A {@code AssignmentNode}
 *
 * @author Nicolas Quesada (nquesada@albany.edu)
 *
 */
public class AssignmentNode extends StatementNode{
    public Node variable;
    public Node expression;

    /**
     * constructor for {@code AssignmentNode}
     * @param t the target
     * @param e the expression
     */
    public AssignmentNode(Node t, Node e){
        this.variable = t;
        this.expression = e;
    }

    public Node getVariable() {
        return variable;
    }
    public Node getExpression() {return expression;}

    /**
     * toString mehtod for the {@code AssignmentNode}
     * @return String holding the Node type and associated members
     */
    @Override
    public String toString() {
        return "AssignmentNode[" + variable.toString() + "," + expression.toString() + "]";
    }
}
