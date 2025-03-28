package BASIC;
import java.util.List;
import java.util.Optional;

/**
 * A {@code MathOpNode} that creates a node for an operation
 *
 * @author Nicolas Quesada (nquesada@albany.edu)
 *
 */
public class MathOpNode extends Node {

    private Node left; //Left operand of the operation.
    private Optional<Node> right; //Right operand of the operation (optional).
    private OperationType operationType; //Type of operation (e.g., ADD, SUBTRACT, AND, OR).

    /**
     * Enum representing the different types of operations in the BASIC language.
     */
    public enum OperationType {
        ADD, SUBTRACT, MULTIPLY, DIVIDE
    }

    /**
     * Creates a new OperationNode with a specified left node, an optional right node, and the
     * type of operation to perform.
     *
     * @param left The left operand of the operation.
     * @param right The optional right operand of the operation, or empty if unary.
     * @param operationType The type of operation to perform (e.g., ADD, SUBTRACT, AND, OR).
     */
    public MathOpNode(OperationType operationType, Node left, Optional<Node> right) {
        this.left = left;
        this.right = right;
        this.operationType = operationType;
    }

    /**
     * Accessor for left side of operation
     * @return the left side of the operation
     */
    public Node getLeft() {
        return left;
    }

    /**
     * Accessor for right side of operation
     * @return the right side of the operation
     */
    public Optional<Node> getRight() {
        return right;
    }
    /**
     * Accessor for operation type
     * @return the operation type
     */
    public OperationType getOperationType() {
        return operationType;
    }

    /**
     * toString method for OperationNode
     * @return String holding the Node type and associated members
     */
    @Override
    public String toString() {
        return left.toString() + " " + operationType.toString() + " " + right.toString() + " ";
    }
}