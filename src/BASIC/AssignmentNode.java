package BASIC;
/**
 * A {@code AssignmentNode}
 *
 * @author Nicolas Quesada (nquesada@albany.edu)
 *
 */
public class AssignmentNode extends StatementNode{
    public Node target;
    public Node expression;

    /**
     * constructor for {@code AssignmentNode}
     * @param t the target
     * @param e the expression
     */
    public AssignmentNode(Node t, Node e){
        this.target = t;
        this.expression = e;
    }

    public Node getTarget() {
        return this.target;
    }
    public Node getExpression() {return expression;}

    /**
     * toString mehtod for the {@code AssignmentNode}
     * @return String holding the Node type and associated members
     */
    @Override
    public String toString() {
        return "AssignmentNode[" + target.toString() + "," + expression.toString() + "]";
    }
}
